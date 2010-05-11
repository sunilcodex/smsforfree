/**
 * 
 */
package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalBag;
import it.rainbowbreeze.smsforfree.common.GlobalUtils;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author rainbowbreeze
 *
 */
public class ActProviderSubServicesList
	extends ListActivity
{
	//---------- Private fields
	private SmsProvider mProviderToEdit;

	
	
	
	//---------- Public properties

	
	
	
	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//update title
        this.setTitle(String.format(
        		getString(R.string.actsettingssmsservice_titleEdit),
        		mProviderToEdit.getName()));
	}

	//---------- Public methods

	
	
	
	//---------- Private methods

	private void getDataFromIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		//checks if intent 
		if(extras != null) {
			String id = extras.getString(ActivityHelper.INTENTKEY_SMSSERVICE);
			mProviderToEdit = (SmsProvider) GlobalUtils.findProviderInList(GlobalBag.providerList, id);
		} else {
			mProviderToEdit = null;
		}
	}
}
