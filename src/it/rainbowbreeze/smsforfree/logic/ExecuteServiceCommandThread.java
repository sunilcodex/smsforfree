package it.rainbowbreeze.smsforfree.logic;

import it.rainbowbreeze.smsforfree.domain.SmsService;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

/**
 * Execute a generic command of the service
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ExecuteServiceCommandThread
	extends BaseBackgroundThread
{
	//---------- Ctors
	public ExecuteServiceCommandThread(
			Context context,
			Handler handler,
			SmsService service,
			int commandToExecute,
			Bundle extraData)
	{
		super(context, handler);
		mService = service;
		mCommandToExecute = commandToExecute;
		mExtraData = extraData;
	}

	
	

	//---------- Private fields
	private SmsService mService;
	private int mCommandToExecute;
	Bundle mExtraData;

	
	

	//---------- Public fields
	public final static int WHAT_EXECUTESERVICECOMMAND = 1000;




	//---------- Public methods
	@Override
	public void run() {
		//execute the command
		mResultOperation = mService.executeCommand(mCommandToExecute, getContext(), mExtraData);
		//and call the caller activity handler when the execution is terminated
		callHandlerAndRetry(mCallerHandler.obtainMessage(WHAT_EXECUTESERVICECOMMAND));
	}




	//---------- Private methods
}
