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
	protected void callHandlerAndRetry(int messageCode)
	{
		for (int retryCounter = 0; retryCounter < TOTAL_RETRIES; retryCounter++) {
			if (null != mCallerHandler) {
				Message message = mCallerHandler.obtainMessage(messageCode);
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
