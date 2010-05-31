/**
 * 
 */
package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalBag;
import it.rainbowbreeze.smsforfree.common.GlobalUtils;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.domain.SmsConfigurableService;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsProviderMenuCommand;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
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
	private SmsService mEditedService;
	private SmsService mTemplateService;
	private SmsProvider mProvider;
	private boolean mIsEditingAProvider;
	private Button mBtnConfigureSubservices;
	private TextView mLblServiceName;
	private TextView mTxtServiceName;
	private TextView mLblServiceInfo;
	private TextView mTxtServiceInfo;
	
	//fuck java only passing parameters by value :(
	TextView mLblDesc;
	EditText mTxtValue;

	private final static int MAXFIELDS = 10;
	
	//---------- Public properties

	
	
	
	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.actsettingssmsservice);
        getDataFromIntent(getIntent());
        
        if (null == mProvider) return;
        
        mBtnConfigureSubservices = (Button) findViewById(R.id.actsettingssmsservice_btnConfigsubservices);
        mBtnConfigureSubservices.setOnClickListener(mBtnConfigureSubservicesClickListener);
        mLblServiceName = (TextView) findViewById(R.id.actsettingssmsservice_lblServiceName);
        mTxtServiceName = (EditText) findViewById(R.id.actsettingssmsservice_txtServiceName);
        mLblServiceInfo = (TextView) findViewById(R.id.actsettingssmsservice_lblServiceInfo);
        mTxtServiceInfo = (EditText) findViewById(R.id.actsettingssmsservice_txtServiceInfo);
        
		showAndHideViews();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean canContinue = super.onCreateOptionsMenu(menu);
		
		if (!canContinue) return canContinue;
		if (mIsEditingAProvider && mProvider.hasProviderSettingsActivityCommands()) {
			for (SmsProviderMenuCommand command : mProvider.getProviderSettingsActivityCommands()) {
				MenuItem item = menu.add(0,
						command.getCommandId(), command.getCommandOrder(), command.getCommandDescription());
				if (command.hasIcon()) item.setIcon(command.getCommandIcon());
			}
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean result;
		
		result = super.onOptionsItemSelected(item);

		//exit if the command was processed
		if (result) return result;
		
		
		//save EditText values
		Bundle bundle = new Bundle();
		for (int i = 0; i < MAXFIELDS; i++) {
        	if (i < mEditedService.getParametersNumber()) {
        		findLabelAndEditTextViewsForParameter(i);
        		bundle.putString(String.valueOf(i), mTxtValue.getText().toString());
    		}else{
        		bundle.putString(String.valueOf(i), "");
    		}
		}

		//calls the method passing it all text values
		ResultOperation res = mProvider.executeCommand(item.getItemId(), bundle);
		
		//show command results
		if (res.HasErrors()) {
			//show an error message
			ActivityHelper.reportError(ActSettingsSmsService.this, res);
		} else {
			//shows the output of the command
			ActivityHelper.reportError(ActSettingsSmsService.this, res.getResultAsString());
		}
		
		return true;
	}


	private OnClickListener mBtnConfigureSubservicesClickListener = new OnClickListener() {
		public void onClick(View v) {
			//open the subservice configuration activity
			ActivityHelper.openSubservicesList(ActSettingsSmsService.this, mProvider.getId());
		}
	};

	


	//---------- Public methods

	
	
	
	//---------- Private methods
	@Override
	protected void loadDataIntoViews() {
		//update title
        this.setTitle(String.format(
        		getString(R.string.actsettingssmsservice_titleEdit),
        		mTemplateService.getName()));

        //set the name, if the object edited is a subservice
		if (!mIsEditingAProvider) {
			if (null == mEditedService)
				//use the name of the provider for a new service
				mTxtServiceName.setText(mTemplateService.getName());
			else
				//use the service name
				mTxtServiceName.setText(mEditedService.getName());
		}
		
		//update data inside views, if the object edited isn't a new subservice
		if (null != mEditedService) {
	        //update values of parameters views
			for (int i = 0; i < MAXFIELDS; i++){
	        	findLabelAndEditTextViewsForParameter(i);
	        	//set the content of the view
	        	if (i < mEditedService.getParametersNumber()) {
        			if (null != mTxtValue)
        				mTxtValue.setText(mEditedService.getParameterValue(i));
	        	}
	        }
		}
	}

	@Override
	protected boolean saveDataFromViews()
	{
		boolean isNewService;
		
		isNewService = null == mEditedService;
		
		if (isNewService) {
			//create new service
			mEditedService = mProvider.newSubserviceFromTemplate(mTemplateService.getId());
		}
		//set object name, if object edited is a subservice
		if (!mIsEditingAProvider)
			((SmsConfigurableService)mEditedService).setName(mTxtServiceName.getText().toString());

		for (int i = 0; i < MAXFIELDS; i++){
        	//save the data inside the object
        	if (i < mEditedService.getParametersNumber()) {
        		findLabelAndEditTextViewsForParameter(i);
        		if (null != mTxtValue) mEditedService.setParameterValue(i, mTxtValue.getText().toString());
        	}
		}
		
		//persist the parameters
		ResultOperation res;
		if (mIsEditingAProvider) {
			//update provider data
			res = mProvider.saveParameters(this);
		} else if (isNewService) {
			mProvider.getAllSubservices().add(mEditedService);
			//save new subservice
			res = mProvider.saveSubservices(this);
		} else {
			//update subservice
			res = mProvider.saveSubservices(this);
		}
		
		if (null == res || res.HasErrors()) {
			ActivityHelper.reportError(this, res);
			return false;
		}
		
    	return true;
	}

	/**
	 * Get data from intent and configured internal fields
	 * @param intent
	 */
	private void getDataFromIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		//checks if current editing is for a provider or a subservice
		if(extras != null) {
			String providerId = extras.getString(ActivityHelper.INTENTKEY_SMSPROVIDERID);
			mProvider = GlobalUtils.findProviderInList(GlobalBag.providerList, providerId);
			String subserviceId = extras.getString(ActivityHelper.INTENTKEY_SMSSERVICEID);
			if (TextUtils.isEmpty(subserviceId)) {
				//edit a provider preferences
				mIsEditingAProvider = true;
				//template and service to edit are always the same provider
				mTemplateService = mProvider;
				mEditedService = mProvider;
			} else {
				//editing a subservice
				mIsEditingAProvider = false;
				String templateId = extras.getString(ActivityHelper.INTENTKEY_SMSTEMPLATEID);
				mTemplateService = mProvider.getTemplate(templateId);
				
				if (SmsService.NEWSERVICEID.equals(subserviceId)) {
					//edit a new subservice
					mEditedService = null;
				} else {
					//edit an existing subservice preferences
					mEditedService = mProvider.getSubservice(subserviceId);
				}
			}

		} else {
			mEditedService = null;
		}
	}


	/**
	 * Show and hide view for editing data
	 */
	private void showAndHideViews() {
		//display or not the button for editing provider's subservices
		if (mIsEditingAProvider) {
			mLblServiceName.setVisibility(View.GONE);
			mTxtServiceName.setVisibility(View.GONE);
			mLblServiceInfo.setVisibility(View.GONE);
			mTxtServiceInfo.setVisibility(View.GONE);
			if (mProvider.hasSubServices()) {
				mBtnConfigureSubservices.setVisibility(View.VISIBLE);
			} else {
				mBtnConfigureSubservices.setVisibility(View.GONE);
			}
		} else {
			mLblServiceName.setVisibility(View.VISIBLE);
			mTxtServiceName.setVisibility(View.VISIBLE);
			mLblServiceInfo.setVisibility(View.GONE);
			mTxtServiceInfo.setVisibility(View.GONE);
			mBtnConfigureSubservices.setVisibility(View.GONE);
		}
		

        //update views visibility
		for (int i = 0; i < MAXFIELDS; i++){
			findLabelAndEditTextViewsForParameter(i);
        	
        	//set the content of the view
        	if (i >= mTemplateService.getParametersNumber()) {
        		if (null != mLblDesc) mLblDesc.setVisibility(View.GONE);
        		if (null != mTxtValue) mTxtValue.setVisibility(View.GONE);
        	} else {
        		if (null != mLblDesc) {
        			mLblDesc.setText(mTemplateService.getParameterDesc(i));
        			if (mTemplateService.getParameter(i).isPassword()) {
        				mTxtValue.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        				mTxtValue.setTransformationMethod(new PasswordTransformationMethod());
        			}
        		}
        	}
        }
		
	}
	
	
	private void findLabelAndEditTextViewsForParameter(int parameterNumber)
	{
    	//get description and value views
    	switch (parameterNumber) {
		case 0:
			mLblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter00);
			mTxtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter00);
			break;
		case 1:
			mLblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter01);
			mTxtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter01);
			break;
		case 2:
			mLblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter02);
			mTxtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter02);
			break;
		case 3:
			mLblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter03);
			mTxtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter03);
			break;
		case 4:
			mLblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter04);
			mTxtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter04);
			break;
		case 5:
			mLblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter05);
			mTxtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter05);
			break;
		case 6:
			mLblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter06);
			mTxtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter06);
			break;
		case 7:
			mLblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter07);
			mTxtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter07);
			break;
		case 8:
			mLblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter08);
			mTxtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter08);
			break;
		case 9:
			mLblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter09);
			mTxtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter09);
			break;
		}
	}
}
