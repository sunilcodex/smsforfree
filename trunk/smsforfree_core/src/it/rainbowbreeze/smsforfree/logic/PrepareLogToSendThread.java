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

import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import android.content.Context;
import android.os.Handler;



/**
 * Send a captcha
 */
public class PrepareLogToSendThread
	extends BaseBackgroundThread
{

	//---------- Ctors
	public PrepareLogToSendThread(
			Context context,
			Handler handler)
	{
		super(context, handler);
	}




	//---------- Private fields

	
	

	//---------- Public fields
	public final static int WHAT_PREPARELOGTOSEND = 1010;
	



	//---------- Public methods
	@Override
	public void run() {
		//collect all the log
		mResultOperation = LogFacility.getLogData(new String[]{ GlobalDef.LOG_TAG, "AndroidRuntime"});
		callHandlerAndRetry(mCallerHandler.obtainMessage(WHAT_PREPARELOGTOSEND));
	}
	



	//---------- Private methods
}
