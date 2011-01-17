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
import it.rainbowbreeze.smsforfree.common.AppEnv;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.TextUtils;

/**
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ActMessageTemplates
	extends PreferenceActivity
{
	//---------- Private fields
    protected final static String LOG_HASH = "ActMessageTemplates";
    
	private EditTextPreference[] mTxtTemplates;
	private final static int FIELDS = 10;
	private String[] mMessageTemplates;

	private LogFacility mLogFacility;
	private AppPreferencesDao mAppPreferencesDao;



    //---------- Constructors




	//---------- Public properties




	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mLogFacility = AppEnv.i(getBaseContext()).getLogFacility();
        mLogFacility.logStartOfActivity(LOG_HASH, this.getClass(), savedInstanceState);
        mAppPreferencesDao = AppEnv.i(getBaseContext()).getAppPreferencesDao();

        setTitle(String.format(
        		getString(R.string.actmessagetemplates_title),
        		AppEnv.i(getBaseContext()).getAppDisplayName()));
		addPreferencesFromResource(R.layout.actmessagetemplates);
		
		//load message templates into internal value
		String[] savedTemplates = mAppPreferencesDao.getMessageTemplates();
		mMessageTemplates = new String[FIELDS];
		for (int i = 0; i < savedTemplates.length; i++)
			mMessageTemplates[i] = savedTemplates[i];
		
		//get all text preference fields, set their fields and and set their listeners
		mTxtTemplates = new EditTextPreference[FIELDS];
		for (int i = 0; i < FIELDS; i++) {
			String fieldIndex = String.valueOf(i + 1);
			String fieldName = "actsettingsmain_txtTemplate" + fieldIndex;
			mTxtTemplates[i] = (EditTextPreference) findPreference(fieldName);
			mTxtTemplates[i].setText(mMessageTemplates[i]);
			updateFieldStatus(mTxtTemplates[i], i);
			mTxtTemplates[i].setOnPreferenceChangeListener(new OnPreferenceChangeListenerIndexed(i));
		}
	}
	

	private class OnPreferenceChangeListenerIndexed implements OnPreferenceChangeListener {
		private final int mElementIndex;
		
		public OnPreferenceChangeListenerIndexed(int index) {
			mElementIndex = index;
		}

		public boolean onPreferenceChange(Preference preference, Object newValue) {
			String newTemplate = newValue.toString();
			boolean result = changeAndSaveTemplate(mElementIndex, newTemplate);
			if (result)
				updateFieldStatus((EditTextPreference) preference, mElementIndex);
			return result;
		}
	}




	//---------- Public methods




	//---------- Private methods
	/**
	 * Set title and message of the text preference element
	 */
	private void updateFieldStatus(
			EditTextPreference editTextPreference,
			int editTextPreferenceIndex)
	{
		if (TextUtils.isEmpty(mMessageTemplates[editTextPreferenceIndex])) {
			editTextPreference.setTitle(R.string.actmessagetemplates_txtTemplate);
			editTextPreference.setSummary("");
		} else {
			editTextPreference.setTitle(
					String.format(getString(R.string.actmessagetemplates_txtTemplateFull), String.valueOf(editTextPreferenceIndex + 1)));
			editTextPreference.setSummary(mMessageTemplates[editTextPreferenceIndex]);
		}
	}

	/**
	 * Change message template and save it in the app preferences
	 * 
	 * @param editTextPreferenceIndex
	 * @param newTemplate
	 * @return
	 */
	private boolean changeAndSaveTemplate(int editTextPreferenceIndex, String newTemplate)
	{
		//change message template
		mMessageTemplates[editTextPreferenceIndex] = newTemplate;

		//find max length of new message templates array
		int maxSize = mMessageTemplates.length;
		while(maxSize > 0 && TextUtils.isEmpty(mMessageTemplates[maxSize - 1]))
			maxSize--;
		
		//create new array for app preferences
		String[] newTemplates = new String[maxSize];
		for (int i = 0; i < maxSize; i++) {
			if (TextUtils.isEmpty(mMessageTemplates[i]))
				newTemplates[i] = "";
			else
				newTemplates[i] = mMessageTemplates[i];
		}
		
		//save all
		mAppPreferencesDao.setMessageTemplates(newTemplates);
		return mAppPreferencesDao.save();
	}

}
