/**
 * 
 */
package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.providers.JacksmsProvider;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;

/**
 * @author rainbowbreeze
 *
 */
public class ActSettingsMain extends PreferenceActivity {
	//---------- Ctors

	//---------- Private fields
	private CheckBoxPreference mChkResetData;
	private CheckBoxPreference mChkInserIntoPim;

	//---------- Public properties

	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.layout.actsettingsmain);
		
		mChkResetData = (CheckBoxPreference) findPreference("actsettingsmain_chkResetDataAfterSend");
		mChkInserIntoPim = (CheckBoxPreference) findPreference("actsettingsmain_chkInsertSmsIntoPim");
		
		//Get the custom preference
		Preference customPref = (Preference) findPreference("actsettingsmain_providersPref");
		
		customPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				ActivityHelper.openProvidersList(ActSettingsMain.this);
//				ActivityHelper.openSettingsSmsService(ActSettingsMain.this, JacksmsProvider.instance().getName());
				return true;
			}
		});
	}
	
//	@Override
//	protected void onStart() {
//		super.onStart();
//		
//		mChkResetData.setChecked(AppPre)
//	}

	//---------- Public methods

	//---------- Private methods

}
