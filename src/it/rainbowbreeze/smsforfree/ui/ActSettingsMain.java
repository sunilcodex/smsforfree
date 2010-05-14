/**
 * 
 */
package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
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
	private CheckBoxPreference mChkInserIntoPim;

	
	
	
	//---------- Public properties

	
	
	
	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.layout.actsettingsmain);
		
		mChkResetData = (CheckBoxPreference) findPreference("actsettingsmain_chkResetDataAfterSend");
		mChkInserIntoPim = (CheckBoxPreference) findPreference("actsettingsmain_chkInsertSmsIntoPim");
		
		//Get the custom preference
		Preference customPref = (Preference) findPreference("actsettingsmain_providersPref");
		
		customPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				ActivityHelper.openProvidersList(ActSettingsMain.this);
//				ActivityHelper.openSettingsSmsService(ActSettingsMain.this, JacksmsProvider.instance().getName());
				return true;
			}
		});
	}

	
	
	
	//---------- Public methods

	
	
	
	//---------- Private methods
	@Override
	protected void loadData() {
		mChkResetData.setChecked(AppPreferencesDao.instance().getAutoClearMessage());
		mChkInserIntoPim.setChecked(AppPreferencesDao.instance().getInsertMessageIntoPim());
	}

	@Override
	protected void saveData() {
		AppPreferencesDao.instance().setAutoClearMessage(mChkResetData.isChecked());
		AppPreferencesDao.instance().setInsertMessageIntoPim(mChkInserIntoPim.isChecked());
		AppPreferencesDao.instance().save();
	}

	@Override
	protected void backupData() {
		AppPreferencesDao.instance().backup(this);
	}

	@Override
	protected void restoreData() {
		AppPreferencesDao.instance().restore(this);
	}

}
