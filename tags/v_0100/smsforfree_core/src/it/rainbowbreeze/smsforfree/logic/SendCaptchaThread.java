package it.rainbowbreeze.smsforfree.logic;

import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import android.content.Context;
import android.os.Handler;



/**
 * Send a captcha
 */
public class SendCaptchaThread
	extends BaseBackgroundThread
{

	//---------- Ctors
	public SendCaptchaThread(
			Context context,
			Handler handler,
			SmsProvider provider,
			String providerCaptchaData,
			String captchaCode)
	{
		super(context, handler);
		mProvider = provider;
		mProviderCaptchaData = providerCaptchaData;
		mCaptchaCode = captchaCode;
	}




	//---------- Private fields
	private SmsProvider mProvider;
	private String mProviderCaptchaData;
	private String mCaptchaCode;

	
	

	//---------- Public fields
	public final static int WHAT_SENDCAPTCHA = 1003;
	



	//---------- Public methods
	@Override
	public void run() {
		//execute the command
		mResultOperation = mProvider.sendCaptcha(mProviderCaptchaData, mCaptchaCode);
		//and call the caller activity handler when the execution is terminated
		callHandlerAndRetry(mCallerHandler.obtainMessage(WHAT_SENDCAPTCHA));
	}
	



	//---------- Private methods
}
