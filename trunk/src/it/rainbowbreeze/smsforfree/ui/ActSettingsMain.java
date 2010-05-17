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
import android.preference.Preference.OnPreferenceClickListener;

/**
 * @author rainbowbreeze
 *
 */
public class ActSettingsMain
	extends ActBasePreferenceEntry
{
	//---------- Private fields
	private CheckBoxPreference mChkResetData;
//	private CheckBoxPreference mChkInserIntoPim;
	private EditTextPreference mTxtSignature;

	
	
	
	//---------- Public properties

	
	
	
	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.layout.actsettingsmain);
		
		mChkResetData = (CheckBoxPreference) findPreference("actsettingsmain_chkResetDataAfterSend");
		mTxtSignature = (EditTextPreference) findPreference("actsettingsmain_txtSignature");
//		mChkInserIntoPim = (CheckBoxPreference) findPreference("actsettingsmain_chkInsertSmsIntoPim");
		
		//Get the custom preference
		Preference customPref = (Preference) findPreference("actsettingsmain_providersPref");
		customPref.setOnPreferenceClickListener(providersPrefsClickListener);
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
	
	
	//---------- Public methods

	
	
	
	//---------- Private methods
	@Override
	protected void loadData() {
		mChkResetData.setChecked(AppPreferencesDao.instance().getAutoClearMessage());
//		mChkInserIntoPim.setChecked(AppPreferencesDao.instance().getInsertMessageIntoPim());
		mTxtSignature.setText(AppPreferencesDao.instance().getSignature());
	}

	@Override
	protected void saveData() {
		AppPreferencesDao.instance().setAutoClearMessage(mChkResetData.isChecked());
//		AppPreferencesDao.instance().setInsertMessageIntoPim(mChkInserIntoPim.isChecked());
		AppPreferencesDao.instance().setSignature(mTxtSignature.getText());
		AppPreferencesDao.instance().save();
	}

	@Override
	protected void getDataFromTemporaryStore(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void putDataIntoTemporaryStore(Bundle outState) {
		// TODO Auto-generated method stub
		
	}

}
