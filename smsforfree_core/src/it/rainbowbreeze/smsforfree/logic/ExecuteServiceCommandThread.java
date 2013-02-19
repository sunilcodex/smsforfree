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
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import static it.rainbowbreeze.libs.common.RainbowContractHelper.*;

/**
 * Execute a generic command of the service
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ExecuteServiceCommandThread
	extends RainbowBaseBackgroundThread<ResultOperation<String>, String>
{
	//---------- Private fields
    private static final String LOG_HASH = "ExecuteServiceCommandThread";
	private final SmsService mService;
	private final int mCommandToExecute;
	private final LogFacility mLogFacility;
	Bundle mExtraData;

	
	

	//---------- Constructors
	public ExecuteServiceCommandThread(
			LogFacility logFacility,
			Context context,
			Handler handler,
            int handlerMessageWhat,
			SmsService service,
			int commandToExecute,
			Bundle extraData)
	{
		super(context, handler, handlerMessageWhat);
		mService = service;
		mCommandToExecute = commandToExecute;
		mExtraData = extraData;
		mLogFacility = checkNotNull(logFacility, "LogFacility");
	}

	
	

	//---------- Public fields



	//---------- Public methods
	@Override
	public ResultOperation<String> executeTask() {
		mLogFacility.v(LOG_HASH, "Execute command " + mCommandToExecute + " for service " + mService.getId());
		return mService.executeCommand(mCommandToExecute, getContext(), mExtraData);
	}




	//---------- Private methods
}
