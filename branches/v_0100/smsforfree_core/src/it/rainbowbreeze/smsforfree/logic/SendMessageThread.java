package it.rainbowbreeze.smsforfree.logic;

import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import android.content.Context;
import android.os.Handler;

/**
 * Send a message using provider's method
 */
public class SendMessageThread
	extends BaseBackgroundThread
{

	//---------- Ctors
	public SendMessageThread(
			Context context,
			Handler handler,
			SmsProvider provider,
			String servideId,
			String destination,
			String message)
	{
		super(context, handler);
		mProvider = provider;
		mServiceId = servideId;
		mDestination = destination;
		mMessage = message;
	}




	//---------- Private fields
	private SmsProvider mProvider;
	private String mServiceId;
	private String mDestination;
	private String mMessage;

	
	

	//---------- Public fields
	public final static int WHAT_SENDMESSAGE = 1002;
	



	//---------- Public methods
	@Override
	public void run() {
		//execute the command
		mResultOperation = mProvider.sendMessage(mServiceId, mDestination, mMessage);
		
		//and call the caller activity handler when the execution is terminated
		callHandlerAndRetry(mCallerHandler.obtainMessage(WHAT_SENDMESSAGE));
	}
	



	//---------- Private methods
}
