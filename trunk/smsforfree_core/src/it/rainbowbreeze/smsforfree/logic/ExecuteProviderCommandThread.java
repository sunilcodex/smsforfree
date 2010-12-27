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

import it.rainbowbreeze.libs.logic.RainbowBaseBackgroundThread;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

/**
 * Execute a generic command of the provider
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ExecuteProviderCommandThread
	extends RainbowBaseBackgroundThread<String>
{
	//---------- Ctors
	public ExecuteProviderCommandThread(
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
	public final static int WHAT_EXECUTEDPROVIDERCOMMAND = 1001;




	//---------- Public methods
	@Override
	public void run() {
		//execute the command
		mResultOperation = mService.executeCommand(mCommandToExecute, getContext(), mExtraData);
		//and call the caller activity handler when the execution is terminated
		callHandlerAndRetry(WHAT_EXECUTEDPROVIDERCOMMAND);
	}

	
	
	
	//---------- Private methods
}
