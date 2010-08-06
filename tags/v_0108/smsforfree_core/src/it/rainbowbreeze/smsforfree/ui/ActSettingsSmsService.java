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
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.common.App;
import it.rainbowbreeze.smsforfree.domain.SmsConfigurableService;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsServiceCommand;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.logic.ExecuteServiceCommandThread;
import it.rainbowbreeze.smsforfree.util.GlobalUtils;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	private final static int MAXFIELDS = 10;

	private SmsService mEditedService;
	private SmsService mTemplateService;
	private SmsProvider mProvider;
	private boolean mIsEditingAProvider;
	private Button mBtnConfigureSubservices;
	private TextView mLblServiceName;
	private TextView mTxtServiceName;
	private TextView mLblServiceDesc;
	
	//fuck java only passing parameters by value :(
	private TextView mLblDesc;
	private EditText mTxtValue;

	//save originals values of the service
	private String[] mValuesBackup;
	private boolean mAssignedValues;
	
	private ExecuteServiceCommandThread mExecutedServiceCommandThread;
	
	private ProgressDialog mProgressDialog;




	//---------- Public properties

	
	
	
	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.actsettingssmsservice);
        getDataFromIntent(getIntent());
        
        //Debug: check if the service has more parameters than ones that this activity can handle
        if (null != mEditedService && mEditedService.getParametersNumber() > MAXFIELDS) return;
        if (null == mProvider) return;
        
        mBtnConfigureSubservices = (Button) findViewById(R.id.actsettingssmsservice_btnConfigsubservices);
        mBtnConfigureSubservices.setOnClickListener(mBtnConfigureSubservicesClickListener);
        mLblServiceName = (TextView) findViewById(R.id.actsettingssmsservice_lblServiceName);
        mTxtServiceName = (EditText) findViewById(R.id.actsettingssmsservice_txtServiceName);
        mLblServiceDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblServiceDesc);
        
		showAndHideViews();

		if (null == savedInstanceState) {
	        //no service parameters saved inside the collection
	        mAssignedValues = false;
        }
	}
		
	@Override
	protected void onStart() {
		super.onStart();
		mExecutedServiceCommandThread = (ExecuteServiceCommandThread) getLastNonConfigurationInstance();
		if (null != mExecutedServiceCommandThread) {
			//create and show a new progress dialog
			mProgressDialog = ActivityHelper.createAndShowProgressDialog(this, R.string.common_msg_executingCommand);
			//register new handler
			mExecutedServiceCommandThread.registerCallerHandler(mExecutedCommandHandler);
		}
	}

	@Override
	protected void onStop() {
		if (null != mExecutedServiceCommandThread) {
			//unregister handler from background thread
			mExecutedServiceCommandThread.registerCallerHandler(null);
		}
		super.onStop();
	}
		
	@Override
	public Object onRetainNonConfigurationInstance() {
		//save eventually open background thread
		return mExecutedServiceCommandThread;
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean canContinue = super.onCreateOptionsMenu(menu);
		if (!canContinue) return canContinue;

		if (null != mEditedService && mEditedService.hasSettingsActivityCommands()) {
			for (SmsServiceCommand command : mEditedService.getSettingsActivityCommands()) {
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
		for (int i = 0; i < mEditedService.getParametersNumber(); i++) {
    		findLabelAndEditTextViewsForParameter(i);
    		bundle.putString(String.valueOf(i), mTxtValue.getText().toString());
		}
		
		//create new progress dialog
		mProgressDialog = ActivityHelper.createAndShowProgressDialog(this, R.string.common_msg_executingCommand);

		//preparing the background thread for executing service command
		mExecutedServiceCommandThread = new ExecuteServiceCommandThread(
				this.getApplicationContext(),
				mExecutedCommandHandler,
				mEditedService,
				item.getItemId(),
				bundle);
		//and execute the command
		mExecutedServiceCommandThread.start();
		return true;
	}


	private OnClickListener mBtnConfigureSubservicesClickListener = new OnClickListener() {
		public void onClick(View v) {
			//backup data of services
			if (!mAssignedValues) {
				mAssignedValues = true;
				if (mEditedService.getParametersNumber() > 0) {
					mValuesBackup = new String[mEditedService.getParametersNumber()];
					for (int i = 0; i < mEditedService.getParametersNumber(); i++) {
						mValuesBackup[i] = mEditedService.getParameterValue(i);
					}
				}
			}

			//assigns user-edited values to service parameter's values
			for (int i = 0; i < mEditedService.getParametersNumber(); i++){
        		findLabelAndEditTextViewsForParameter(i);
        		if (null != mTxtValue) mEditedService.setParameterValue(i, mTxtValue.getText().toString());
			}
			
			//store that at least one of the providers' subservices list was accessed
			App.instance().setForceSubserviceRefresh(true);
			
			//open the subservice configuration activity
			ActivityHelper.openSubservicesList(ActSettingsSmsService.this, mProvider.getId());
		}
	};


	/**
	 * Hander to call when the execute command menu option ended
	 */
	private Handler mExecutedCommandHandler = new Handler() {
		public void handleMessage(Message msg)
		{
			//check if the message is for this handler
			if (msg.what != ExecuteServiceCommandThread.WHAT_EXECUTESERVICECOMMAND)
				return;
			
			//dismisses progress dialog
			if (null != mProgressDialog && mProgressDialog.isShowing())
				mProgressDialog.dismiss();
			ResultOperation<String> res = mExecutedServiceCommandThread.getResult();
			//and show the result
			ActivityHelper.showCommandExecutionResult(ActSettingsSmsService.this, res);
			//free the thread
			mExecutedServiceCommandThread = null;
		};
	};
	



	//---------- Public methods
	

	
	
	//---------- Private methods
	@Override
	protected void loadDataIntoViews() {
		//update title
        this.setTitle(String.format(
        		getString(R.string.actsettingssmsservice_title),
        		App.instance().getAppName(),
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
			for (int i = 0; i < mEditedService.getParametersNumber(); i++){
	        	findLabelAndEditTextViewsForParameter(i);
	        	//set the content of the view
    			if (null != mTxtValue) mTxtValue.setText(mEditedService.getParameterValue(i));
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
			mEditedService = mProvider.integrateSubserviceWithTemplateData(null, mTemplateService.getId());
		}
		//set object name, if object edited is a subservice
		if (!mIsEditingAProvider)
			((SmsConfigurableService)mEditedService).setName(mTxtServiceName.getText().toString().trim());

		for (int i = 0; i < mEditedService.getParametersNumber(); i++){
        	//save the data inside the object
    		findLabelAndEditTextViewsForParameter(i);
    		if (null != mTxtValue) mEditedService.setParameterValue(i, mTxtValue.getText().toString().trim());
		}
		
		//persist the parameters
		ResultOperation<Void> res;
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
		
		if (res.hasErrors()) {
			ActivityHelper.reportError(this, res.getException(), res.getReturnCode());
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
			mProvider = GlobalUtils.findProviderInList(App.instance().getProviderList(), providerId);
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
			if (mProvider.hasSubServicesToConfigure()) {
				mBtnConfigureSubservices.setVisibility(View.VISIBLE);
			} else {
				mBtnConfigureSubservices.setVisibility(View.GONE);
			}
		} else {
			mLblServiceName.setVisibility(View.VISIBLE);
			mTxtServiceName.setVisibility(View.VISIBLE);
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
		
		//Service description status
		String description;
		if (mIsEditingAProvider) {
			description = mProvider.getDescription();
		} else {
			description = mTemplateService.getDescription();
		}
		if (TextUtils.isEmpty(description)) {
			mLblServiceDesc.setVisibility(View.GONE);
		} else {
			mLblServiceDesc.setVisibility(View.VISIBLE);
			mLblServiceDesc.setText(description);
			
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
	

	@Override
	protected void cancelEdit() {
		if (mAssignedValues) {
			//restore saved values of service parameters
			for (int i = 0; i < mValuesBackup.length; i++)
				mEditedService.setParameterValue(i, mValuesBackup[i]);
		}

		super.cancelEdit();
	}


	@Override
	protected void loadVolatileData(Bundle savedInstanceState) {
		super.loadVolatileData(savedInstanceState);
		mAssignedValues = savedInstanceState.getBoolean("ASSIGNED");
		if (mAssignedValues) {
			int paramsNumber = savedInstanceState.getInt("PARAMSNUMBER");
			mValuesBackup = new String[paramsNumber];
			for (int i=0; i < paramsNumber; i++)
				mValuesBackup[i] = savedInstanceState.getString(String.valueOf(i));
		}
	}
	
	@Override
	protected void saveVolatileData(Bundle outState) {
		super.saveVolatileData(outState);
		outState.putBoolean("ASSIGNED", mAssignedValues);
		if (mAssignedValues) {
			outState.putInt("PARAMSNUMBER", mEditedService.getParametersNumber());
			for (int i=0; i < mEditedService.getParametersNumber(); i++)
				outState.putString(String.valueOf(i), mValuesBackup[i]);
		}
	}
	
}
