package it.rainbowbreeze.smsforfree.logic;

import it.rainbowbreeze.smsforfree.common.IExecuteServiceCommandActivity;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import android.content.Context;
import android.os.Bundle;

/**
 * Execute a generic command of the service
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ExecuteServiceCommandAsyncTask
	extends ProgressDialogAsyncTask<IExecuteServiceCommandActivity>
{
	//---------- Ctors
	public ExecuteServiceCommandAsyncTask(
			Context context,
			IExecuteServiceCommandActivity callerActivity,
			String progressTitle,
			SmsService service,
			int commandToExecute,
			Bundle extraData)
	{
		super(context, callerActivity, progressTitle);
		mService = service;
		mCommandToExecute = commandToExecute;
		mExtraData = extraData;
	}
	
	//---------- Private fields
	private SmsService mService;
	private int mCommandToExecute;
	Bundle mExtraData;
	



	//---------- Private methods
	protected ResultOperation doInBackground(String... params)
	{
		return mService.executeCommand(mCommandToExecute, getContext(), mExtraData);
	}
	
	@Override
	protected void onPostExecute(ResultOperation result) {
		//close progress dialog
		super.onPostExecute(result);
		//and pass the control to caller activity with the result
		mCallerActivity.executeCommandComplete(result);
	}
	
}
