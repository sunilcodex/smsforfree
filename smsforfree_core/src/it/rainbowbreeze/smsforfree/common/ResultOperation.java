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

package it.rainbowbreeze.smsforfree.common;

import it.rainbowbreeze.libs.common.RainbowResultOperation;

/**
 * Class for result operation.
 *
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class ResultOperation<T>
extends RainbowResultOperation<T>
{
	//---------- Constructors
	public ResultOperation()
	{ super(); }
	
	public ResultOperation(Exception ex, int errorReturnCode)
	{ super(ex, errorReturnCode); }
	
	public ResultOperation(T value)
	{ super(value); }

	public ResultOperation(int errorReturnCode, T errorMessage)
	{ super(errorReturnCode, errorMessage); }


	
	//---------- Public fields
	/** User should decode a captcha in order to send sms */
	public final static int RETURNCODE_SMS_CAPTCHA_REQUEST = 1001;
	/** Conversation with provider ends correctly, but SMS was not send or command doesn't complete for some reason*/
	public static final int RETURNCODE_PROVIDER_ERROR = 1002;
	/** SMS daily limit overtaked */
	public final static int RETURNCODE_SMS_DAILY_LIMIT_OVERTAKED = 1003;

	/** Errors code */
	public final static int RETURNCODE_ERROR_SAVE_PROVIDER_DATA = 501;
	public final static int RETURNCODE_ERROR_LOAD_PROVIDER_DATA = 502;
	public final static int RETURNCODE_ERROR_NOCREDENTIAL = 503;
	public static final int RETURNCODE_ERROR_EMPTY_REPLY = 504;
	

	
	
	//---------- Public properties

	
	
	
	//---------- Public methods
	
	
	
	
	//---------- Private methods

}
