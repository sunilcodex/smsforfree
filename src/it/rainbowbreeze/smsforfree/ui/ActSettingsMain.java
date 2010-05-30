/**
 * 
 */
package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalBag;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;

/**
 * @author rainbowbreeze
 *
 */
public class ActSettingsMain
	extends PreferenceActivity
{
	//---------- Private fields
	private CheckBoxPreference mChkResetData;
//	private CheckBoxPreference mChkInserIntoPim;
	private EditTextPreference mTxtSignature;
	private EditTextPreference mTxtPrefix;

	
	
	
	//---------- Public properties

	
	
	
	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.layout.actsettingsmain);
		
		mChkResetData = (CheckBoxPreference) findPreference("actsettingsmain_chkResetDataAfterSend");
		mTxtSignature = (EditTextPreference) findPreference("actsettingsmain_txtSignature");
		mTxtPrefix = (EditTextPreference) findPreference("actsettingsmain_txtDefaultInternationalPrefix");
//		mChkInserIntoPim = (CheckBoxPreference) findPreference("actsettingsmain_chkInsertSmsIntoPim");
		
		//Get the custom preference
		Preference customPref = (Preference) findPreference("actsettingsmain_providersPref");
		//register listener for it
		customPref.setOnPreferenceClickListener(providersPrefsClickListener);
		
		//set value of other preferences
		mChkResetData.setChecked(AppPreferencesDao.instance().getAutoClearMessage());
		mTxtSignature.setText(AppPreferencesDao.instance().getSignature());
		mTxtPrefix.setText(AppPreferencesDao.instance().getDefaultInternationalPrefix());
		
		//register listeners
		mChkResetData.setOnPreferenceChangeListener(mChkResetDataChangeListener);
		mTxtSignature.setOnPreferenceChangeListener(mTxtSignatureChangeListener);
		mTxtPrefix.setOnPreferenceChangeListener(mTxtPrefixChangeListener);
	}


	/**
	 * Called when providers preferences button is pressed
	 */
	private OnPreferenceClickListener providersPrefsClickListener = new OnPreferenceClickListener() {
		public boolean onPreferenceClick(Preference preference) {
			//checks if only on provider is configured
			
			if (1 == GlobalBag.providerList.size()) {
				//open directly the setting for the only provider present
				ActivityHelper.openSettingsSmsService(ActSettingsMain.this, GlobalBag.providerList.get(0).getId());
			} else {
				//open providers list
				ActivityHelper.openProvidersList(ActSettingsMain.this);
			}
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

	
	
	
	//---------- Public methods

	
	
	
	//---------- Private methods
}
