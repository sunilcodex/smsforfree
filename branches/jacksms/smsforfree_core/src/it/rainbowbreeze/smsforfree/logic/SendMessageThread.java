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

import java.util.List;

import com.jacksms.android.gui.ComposeMessageActivity;

import it.rainbowbreeze.libs.logic.RainbowBaseBackgroundThread;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import android.content.Context;
import android.os.Handler;

/**
 * Send a message using provider's method
 */
public class SendMessageThread
extends RainbowBaseBackgroundThread<String>
{

	//---------- Ctors
	public SendMessageThread(
			Context context,
			Handler handler,
			SmsProvider provider,
			String serviceId,
			String destination,
			String message)
	{
		super(context, handler);
		mProvider = provider;
		mServiceId = serviceId;
		mDestination = destination;
		mMessage = message;
		mMultipleSenders = false;
	}


	/**
	 * costruttore che accetta una lista di numeri
	 * @param context
	 * @param mActivityHandler
	 * @param provider
	 * @param serviceId
	 * @param numbers
	 * @param message
	 */
	public SendMessageThread(ComposeMessageActivity context,
			Handler mActivityHandler,
			SmsProvider provider,
			String serviceId,
			List<String> numbers,
			String message) {

		super(context, mActivityHandler);
		mProvider = provider;
		mServiceId = serviceId;
		mListNumbers = numbers;
		mMessage = message;
		mMultipleSenders = true;
	}




	//---------- Private fields
	private SmsProvider mProvider;
	private String mServiceId;
	private String mDestination;
	private String mMessage;
	private List<String> mListNumbers;
	private boolean mMultipleSenders;




	//---------- Public fields
	public final static int WHAT_SENDMESSAGE = 1002;




	//---------- Public methods
	@Override
	public void run() {
		//check for single or multiple senders
		if(mMultipleSenders){
			//execute the command for each number
			for(int i=0;i<mListNumbers.size();i++){
				mResultOperation = mProvider.sendMessage(mServiceId, mListNumbers.get(i), mMessage);
			}	
		}
		else{
			//execute the command
			mResultOperation = mProvider.sendMessage(mServiceId, mDestination, mMessage);
		}
		//and call the caller activity handler when the execution is terminated
		callHandlerAndRetry(WHAT_SENDMESSAGE);
	}




	//---------- Private methods
}
