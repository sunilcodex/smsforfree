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

import it.rainbowbreeze.libs.ui.RainbowSettingsMainActivity;
import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.AppEnv;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.data.SmsDao;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;

/**
 * Application main settings
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ActSettingsMain
	extends RainbowSettingsMainActivity
{
	//---------- Private fields
    protected CheckBoxPreference mChkResetData;
	protected CheckBoxPreference mChkInsertSmsIntoPim;
	protected CheckBoxPreference mChkShowOnlyMobileNumbers;
//	protected CheckBoxPreference mChkAskConfirmationForSending;
	protected EditTextPreference mTxtSignature;
	protected EditTextPreference mTxtPrefix;
	
	protected AppPreferencesDao mAppPreferencesDao;
	protected ActivityHelper mActivityHelper;
    protected SmsDao mSmsDao;

	
	
	
	//---------- Public properties

	
	
	
	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAppPreferencesDao = AppEnv.i(getBaseContext()).getAppPreferencesDao();
        mActivityHelper = AppEnv.i(getBaseContext()).getActivityHelper();
        mSmsDao = AppEnv.i(getBaseContext()).getSmsDao();

        
		mChkResetData = (CheckBoxPreference) findPreference("actsettingsmain_chkResetDataAfterSend");
		mChkInsertSmsIntoPim = (CheckBoxPreference) findPreference("actsettingsmain_chkInsertSmsIntoPim");
        mChkShowOnlyMobileNumbers = (CheckBoxPreference) findPreference("actsettingsmain_chkShowOnlyMobileNumbers");
//        mChkAskConfirmationForSending = (CheckBoxPreference) findPreference("actsettingsmain_chkAskConfirmationForSending");
		mTxtSignature = (EditTextPreference) findPreference("actsettingsmain_txtSignature");
		mTxtPrefix = (EditTextPreference) findPreference("actsettingsmain_txtDefaultInternationalPrefix");
		
		//Get the custom providers' preference and register listener for it
		Preference providerPref = findPreference("actsettingsmain_providersPref");
		providerPref.setOnPreferenceClickListener(providersPrefClickListener);
		//Get the custom providers' preference and register listener for it
		Preference templatesPref = findPreference("actsettingsmain_templatesPref");
		templatesPref.setOnPreferenceClickListener(templatesPrefClickListener);
		
		//set value of other preferences
		mChkResetData.setChecked(mAppPreferencesDao.getAutoClearMessage());
		mChkInsertSmsIntoPim.setChecked(mAppPreferencesDao.getInsertMessageIntoPim());
        mChkShowOnlyMobileNumbers.setChecked(mAppPreferencesDao.getShowOnlyMobileNumbers());
//        mChkAskConfirmationForSending.setChecked(mAppPreferencesDao.getAskConfirmationForSending());
		mTxtSignature.setText(mAppPreferencesDao.getSignature());
		mTxtPrefix.setText(mAppPreferencesDao.getDefaultInternationalPrefix());
		
		//register listeners
		mChkResetData.setOnPreferenceChangeListener(mChkResetDataChangeListener);
		mChkInsertSmsIntoPim.setOnPreferenceChangeListener(mChkInsertSmsIntoPimChangeListener);
        mChkShowOnlyMobileNumbers.setOnPreferenceChangeListener(mChkShowOnlyMobileNumbersChangeListener);
//        mChkAskConfirmationForSending.setOnPreferenceChangeListener(mChkAskConfirmationForSendingListener);
		mTxtSignature.setOnPreferenceChangeListener(mTxtSignatureChangeListener);
		mTxtPrefix.setOnPreferenceChangeListener(mTxtPrefixChangeListener);
		
		//can send the log only when the activity is called for the first time
		if(null != savedInstanceState) mMustSendLog = false;
	}
	
	/**
	 * Called when providers preferences button is pressed
	 */
	private OnPreferenceClickListener providersPrefClickListener = new OnPreferenceClickListener() {
		public boolean onPreferenceClick(Preference preference) {
			//checks if only on provider is configured
			
			if (1 == AppEnv.i(getBaseContext()).getProviderList().size()) {
				//open directly the setting for the only provider present
				mActivityHelper.openSettingsSmsService(ActSettingsMain.this, AppEnv.i(getBaseContext()).getProviderList().get(0).getId());
			} else {
				//open providers list
				mActivityHelper.openProvidersList(ActSettingsMain.this);
			}
			return true;
		}
	};
	
	
	/**
	 * Called when template button is pressed
	 */
	private OnPreferenceClickListener templatesPrefClickListener = new OnPreferenceClickListener() {
		public boolean onPreferenceClick(Preference preference) {
			mActivityHelper.openMessageTemplates(ActSettingsMain.this);
			return true;
		}
	};
	
	
	private OnPreferenceChangeListener mTxtSignatureChangeListener = new OnPreferenceChangeListener() {
		
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			mAppPreferencesDao.setSignature(newValue.toString());
			return mAppPreferencesDao.save();
		}
	};
	
	
	private OnPreferenceChangeListener mTxtPrefixChangeListener = new OnPreferenceChangeListener() {
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			mAppPreferencesDao.setDefaultInternationalPrefix(newValue.toString());
			return mAppPreferencesDao.save();
		}
	};
	
	
	private OnPreferenceChangeListener mChkResetDataChangeListener = new OnPreferenceChangeListener() {
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			mAppPreferencesDao.setAutoClearMessage(((Boolean)newValue).booleanValue());
			return mAppPreferencesDao.save();
		}
	};
	
	private OnPreferenceChangeListener mChkInsertSmsIntoPimChangeListener = new OnPreferenceChangeListener() {
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if (mSmsDao.isSentSmsProviderAvailable(preference.getContext())) {
                mAppPreferencesDao.setInsertMessageIntoPim(((Boolean)newValue).booleanValue());
                return mAppPreferencesDao.save();
			} else {
                mActivityHelper.showInfo(preference.getContext(), R.string.actsettingsmain_msgNoSmsProviderAvailable);
                return false;
			}
		}
	};
	
    private OnPreferenceChangeListener mChkShowOnlyMobileNumbersChangeListener = new OnPreferenceChangeListener() {
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            mAppPreferencesDao.setShowOnlyMobileNumbers(((Boolean)newValue).booleanValue());
            return mAppPreferencesDao.save();
        }
    };
	
    private OnPreferenceChangeListener mChkAskConfirmationForSendingListener = new OnPreferenceChangeListener() {
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            mAppPreferencesDao.setAskConfirmationForSending(((Boolean)newValue).booleanValue());
            return mAppPreferencesDao.save();
        }
    };



	
	
	//---------- Public methods

	
	
	
	//---------- Private methods
}
