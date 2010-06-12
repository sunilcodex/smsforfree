package it.rainbowbreeze.smsforfree.logic;

import it.rainbowbreeze.smsforfree.common.ISendCaptchaActivity;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import android.content.Context;


/**
 * Send a captcha
 */
public class SendCaptchaAsyncTask
	extends ProgressDialogAsyncTask<ISendCaptchaActivity>
{

	//---------- Ctors
	public SendCaptchaAsyncTask(
			Context context,
			ISendCaptchaActivity callerActivity,
			String progressTitle,
			SmsProvider provider)
	{
		super(context, callerActivity, progressTitle);
		mProvider = provider;
	}




	//---------- Private fields
	private SmsProvider mProvider;




	//---------- Private methods
	protected ResultOperation doInBackground(String... params)
	{
		//params.length must be equals to 2
			
		String providerReply = params[0];
		String captchaCode = params[1];
		
		return mProvider.sendCaptcha(providerReply, captchaCode);
	}
	
	@Override
	protected void onPostExecute(ResultOperation result) {
		//close progress dialog
		super.onPostExecute(result);
		//and pass the control to caller activity with the result
		mCallerActivity.sendCaptchaComplete(result);
	}
}
