package it.rainbowbreeze.smsforfree.logic;

import it.rainbowbreeze.smsforfree.common.ISendSmsActivity;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import android.content.Context;

/**
 * Send a sms
 */
public class SendSmsAsyncTask
	extends ProgressDialogAsyncTask<ISendSmsActivity>
{

	//---------- Ctors
	public SendSmsAsyncTask(
			Context context,
			ISendSmsActivity callerActivity,
			String progressTitle,
			SmsProvider provider,
			String servideId)
	{
		super(context, callerActivity, progressTitle);
		mProvider = provider;
		mServiceId = servideId;
	}
	
	//---------- Private fields
	private SmsProvider mProvider;
	private String mServiceId;
	



	//---------- Private methods
	protected ResultOperation doInBackground(String... params)
	{
		//params.length must be equals to 2
			
		String destination = params[0];
		String messageBody = params[1];
		
		return mProvider.sendMessage(mServiceId, destination, messageBody);
	}
	
	@Override
	protected void onPostExecute(ResultOperation result) {
		//close progress dialog
		super.onPostExecute(result);
		//and pass the control to caller activity with the result
		mCallerActivity.sendMessageComplete(result);
	}
}
