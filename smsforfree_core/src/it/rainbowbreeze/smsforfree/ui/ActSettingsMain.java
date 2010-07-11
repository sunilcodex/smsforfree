/**
 * Copyright (C) 2010 Alfredo Morresi
 * 
 * This file is part of SmsForFree project.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/>.
 */

package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.common.SmsForFreeApplication;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.logic.PrepareLogToSendThread;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;

/**
 * Application main settings
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ActSettingsMain
	extends PreferenceActivity
{
	//---------- Private fields
	private CheckBoxPreference mChkResetData;
	private EditTextPreference mTxtSignature;
	private EditTextPreference mTxtPrefix;
	private ProgressDialog mProgressDialog;
	
	private PrepareLogToSendThread mPrepareLogThread;
	
	
	
	
	//---------- Public properties

	
	
	
	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setTitle(String.format(
        		getString(R.string.actsettingsmain_title), SmsForFreeApplication.instance().getAppName()));
		
		addPreferencesFromResource(R.layout.actsettingsmain);
		
		mChkResetData = (CheckBoxPreference) findPreference("actsettingsmain_chkResetDataAfterSend");
		mTxtSignature = (EditTextPreference) findPreference("actsettingsmain_txtSignature");
		mTxtPrefix = (EditTextPreference) findPreference("actsettingsmain_txtDefaultInternationalPrefix");
		
		//Get the custom preference
		Preference providerPref = findPreference("actsettingsmain_providersPref");
		//register listener for it
		providerPref.setOnPreferenceClickListener(providersPrefsClickListener);
		//send application log
		Preference sendLog = findPreference("actsettingsmain_sendLog");
		sendLog.setOnPreferenceClickListener(sendLogClickListener);
		
		//set value of other preferences
		mChkResetData.setChecked(AppPreferencesDao.instance().getAutoClearMessage());
		mTxtSignature.setText(AppPreferencesDao.instance().getSignature());
		mTxtPrefix.setText(AppPreferencesDao.instance().getDefaultInternationalPrefix());
		
		//register listeners
		mChkResetData.setOnPreferenceChangeListener(mChkResetDataChangeListener);
		mTxtSignature.setOnPreferenceChangeListener(mTxtSignatureChangeListener);
		mTxtPrefix.setOnPreferenceChangeListener(mTxtPrefixChangeListener);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mPrepareLogThread = (PrepareLogToSendThread) getLastNonConfigurationInstance();
		if (null != mPrepareLogThread) {
			//create and show a new progress dialog
			mProgressDialog = ActivityHelper.createAndShowProgressDialog(this, R.string.common_msg_executingCommand);
			//register new handler
			mPrepareLogThread.registerCallerHandler(mPrepareLogHandler);
		}
	}

	@Override
	protected void onStop() {
		if (null != mPrepareLogThread) {
			//unregister handler from background thread
			mPrepareLogThread.registerCallerHandler(null);
		}
		super.onStop();
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		//save eventually open background thread
		return mPrepareLogThread;
	}


	/**
	 * Called when providers preferences button is pressed
	 */
	private OnPreferenceClickListener providersPrefsClickListener = new OnPreferenceClickListener() {
		public boolean onPreferenceClick(Preference preference) {
			//checks if only on provider is configured
			
			if (1 == SmsForFreeApplication.instance().getProviderList().size()) {
				//open directly the setting for the only provider present
				ActivityHelper.openSettingsSmsService(ActSettingsMain.this, SmsForFreeApplication.instance().getProviderList().get(0).getId());
			} else {
				//open providers list
				ActivityHelper.openProvidersList(ActSettingsMain.this);
			}
			return true;
		}
	};
	
	
	/**
	 * Called when send log button is pressed
	 */
	private OnPreferenceClickListener sendLogClickListener = new OnPreferenceClickListener() {
		public boolean onPreferenceClick(Preference preference) {
			Activity activity = ActSettingsMain.this;
			//create new progress dialog
			mProgressDialog = ActivityHelper.createAndShowProgressDialog(activity, R.string.common_msg_executingCommand);

			//preparing the background thread for executing service command
			mPrepareLogThread = new PrepareLogToSendThread(
					activity.getApplicationContext(),
					mPrepareLogHandler);
			//and execute the command
			mPrepareLogThread.start();
			return true;
		}
	};
	
	
	private OnPreferenceChangeListener mTxtSignatureChangeListener = new OnPreferenceChangeListener() {
		
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			AppPreferencesDao.instance().setSignature(newValue.toString());
			return AppPreferencesDao.instance().save();
		}
	};
	
	
	private OnPreferenceChangeListener mTxtPrefixChangeListener = new OnPreferenceChangeListener() {
		
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			AppPreferencesDao.instance().setDefaultInternationalPrefix(newValue.toString());
			return AppPreferencesDao.instance().save();
		}
	};
	
	
	private OnPreferenceChangeListener mChkResetDataChangeListener = new OnPreferenceChangeListener() {
		
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			AppPreferencesDao.instance().setAutoClearMessage(((Boolean)newValue).booleanValue());
			return AppPreferencesDao.instance().save();
		}
	};

	/**
	 * Hander to call when the execute command menu option ended
	 */
	private Handler mPrepareLogHandler = new Handler() {
		public void handleMessage(Message msg)
		{
			//check if the message is for this handler
			if (msg.what != PrepareLogToSendThread.WHAT_PREPARELOGTOSEND)
				return;
			
			//dismisses progress dialog
			if (null != mProgressDialog && mProgressDialog.isShowing())
				mProgressDialog.dismiss();
			ResultOperation<String> res = mPrepareLogThread.getResult();
			if (res.hasErrors()) {
				//some errors
				ActivityHelper.reportError(ActSettingsMain.this, res);
			} else {
				//send email with log
				ActivityHelper.sendEmail(
						ActSettingsMain.this,
						GlobalDef.EMAIL_FOR_LOG,
						String.format(getString(R.string.common_sendlogSubject), SmsForFreeApplication.instance().getAppName()),
						String.format(getString(R.string.common_sendlogBody), res.getResult()));
			}
			//free the thread
			mPrepareLogThread = null;
		};
	};


	
	
	//---------- Public methods

	
	
	
	//---------- Private methods
}
