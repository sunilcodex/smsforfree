/**
 * 
 */
package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalBag;
import it.rainbowbreeze.smsforfree.common.GlobalUtils;
import it.rainbowbreeze.smsforfree.data.BasePreferencesDao;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ActSettingsSmsService
	extends ActBaseDataEntry
{
	//---------- Private fields
	private SmsService mServiceToEdit;
	private SmsService mTemplateService;
	private SmsProvider mProvider;
	private boolean mIsEditingAProvider;
	private Button mBtnConfigureSubservices;
	
	
	//---------- Public properties

	
	
	
	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.actsettingssmsservice);
        
        mBtnConfigureSubservices = (Button) findViewById(R.id.actsettingssmsservice_btnConfigsubservices);
        mBtnConfigureSubservices.setOnClickListener(mBtnConfigureSubservicesClickListener);
        
        //TODO: check 
        //get data from intent
        getDataFromIntent(getIntent());
        
	}
	
	private OnClickListener mBtnConfigureSubservicesClickListener = new OnClickListener() {
		public void onClick(View v) {
			//open the subservice configuration activity
			ActivityHelper.openProviderSubServicesList(ActSettingsSmsService.this, mServiceToEdit.getId());
		}
	};




	//---------- Public methods

	
	
	
	//---------- Private methods
	@Override
	protected void backupStorageData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected BasePreferencesDao getPreferencesDao() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void loadDataIntoControls() {
		//update title
        this.setTitle(String.format(
        		getString(R.string.actsettingssmsservice_titleEdit),
        		mTemplateService.getName()));

        //update texts visibility and values
		for (int i = 0; i < 10; i++){
        	TextView lblDesc = null;
        	EditText txtValue = null;
        	
        	switch (i) {
			case 0:
				lblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter00);
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter00);
				break;
			case 1:
				lblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter01);
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter01);
				break;
			case 2:
				lblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter02);
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter02);
				break;
			case 3:
				lblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter03);
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter03);
				break;
			case 4:
				lblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter04);
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter04);
				break;
			case 5:
				lblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter05);
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter05);
				break;
			case 6:
				lblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter06);
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter06);
				break;
			case 7:
				lblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter07);
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter07);
				break;
			case 8:
				lblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter08);
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter08);
				break;
			case 9:
				lblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter09);
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter09);
				break;
			}
        	
        	if (i < mTemplateService.getParametersNumber()) {
        		lblDesc.setText(mTemplateService.getParameterDesc(i));
        		txtValue.setText(mTemplateService.getParameterValue(i));
        	} else {
        		lblDesc.setVisibility(View.GONE);
        		txtValue.setVisibility(View.GONE);
        	}
        }
		
		//display or not the button
		//if (mTemplateService instanceof SmsProvider) {
		if (mIsEditingAProvider) {
			if (mProvider.hasSubServices()) {
				mBtnConfigureSubservices.setVisibility(View.VISIBLE);
			} else {
				mBtnConfigureSubservices.setVisibility(View.GONE);
			}
		}
	}

	@Override
	protected void restoreStorageData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void saveDataInsideControls() {
		// TODO Auto-generated method stub
		
	}

	private void getDataFromIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		//checks if intent 
		if(extras != null) {
			String providerId = extras.getString(ActivityHelper.INTENTKEY_SMSPROVIDERID);
			mProvider = GlobalUtils.findProviderInList(GlobalBag.providerList, providerId);
			String subserviceId = extras.getString(ActivityHelper.INTENTKEY_SMSSERVICEID);
			if (TextUtils.isEmpty(subserviceId)) {
				//edit a provider preferences
				mIsEditingAProvider = true;
				//template and service to edit are always the same provider
				mTemplateService = mProvider;
				mServiceToEdit = mProvider;
			} else {
				//edit a subservice preferences
				String templateId = extras.getString(ActivityHelper.INTENTKEY_SMSTEMPLATEID);
				mIsEditingAProvider = false;
				mTemplateService = GlobalUtils.findTemplateInList(mProvider, templateId);
				mServiceToEdit = GlobalUtils.findSubserviceInList(mProvider, subserviceId);
			}

		} else {
			mServiceToEdit = null;
		}
	}

}
