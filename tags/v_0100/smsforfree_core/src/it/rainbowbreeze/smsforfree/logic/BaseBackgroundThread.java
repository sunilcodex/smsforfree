package it.rainbowbreeze.smsforfree.logic;

import java.lang.ref.WeakReference;

import it.rainbowbreeze.smsforfree.common.ResultOperation;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * Execute a generic background task
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public abstract class BaseBackgroundThread
	extends Thread
{
	//---------- Ctors
	public BaseBackgroundThread(Context context, Handler handler)
	{
		registrerNewContext(context);
		registerCallerHandler(handler);
	}

	
	

	//---------- Private fields
	/** How many times should try to call caller activity handler */
	protected static final int TOTAL_RETRIES = 3;
	/** Delay between one call to caller activity handler and the other */
	protected static final int INTERVAL_BETWEEN_RETRIES = 2000;
	
	protected WeakReference<Context> mWeakContext; 
	protected ResultOperation<String> mResultOperation;
	protected Handler mCallerHandler;

	
	

	//---------- Public fields
	


	//---------- Public methods
	/**
	 * Register new context
	 */
	public void registrerNewContext(Context context) {
		mWeakContext = new WeakReference<Context>(context);
	}
	
	public void registerCallerHandler(Handler newHandler)
	{ mCallerHandler = newHandler; }

	public ResultOperation<String> getResult()
	{ return mResultOperation; }


	public abstract void run();




	//---------- Private methods
	/**
	 * Get the caller context from the WeakReference object
	 */
	protected Context getContext()
	{ return mWeakContext.get(); }
	
	
	/**
	 * Call the caller activity handler retrying some times if the handler is
	 * still null
	 * @param message
	 */
	protected void callHandlerAndRetry(Message message)
	{
		for (int retryCounter = 0; retryCounter < TOTAL_RETRIES; retryCounter++) {
			if (null != mCallerHandler) {
				mCallerHandler.sendMessage(message);
				break;
			}
			//what some times, maybe next time activity is ready
			try {
				Thread.sleep(INTERVAL_BETWEEN_RETRIES);
			} catch (InterruptedException ignoreExcepition) {
			}
		}
	}
}
