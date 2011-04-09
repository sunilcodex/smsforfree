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
public class ResultOperation<ResultValueType>
extends RainbowResultOperation<ResultValueType>
{
	//---------- Constructors
	public ResultOperation()
	{ super(); }
	
	public ResultOperation(Exception ex, int errorReturnCode)
	{ super(ex, errorReturnCode); }
	
	public ResultOperation(ResultValueType value)
	{ super(value); }

    public ResultOperation(int errorReturnCode)
    { super(errorReturnCode); }




    //---------- Public fields
	/** User should decode a captcha in order to send sms */
	public final static int RETURNCODE_SMS_CAPTCHA_REQUEST = RETURNCODE_OPERATION_FIRST_USER;
	/** SMS daily limit overtaked */
	public final static int RETURNCODE_SMS_DAILY_LIMIT_OVERTAKED = RETURNCODE_OPERATION_FIRST_USER + 1;

	/** Errors code */
	public final static int RETURNCODE_ERROR_SAVE_PROVIDER_DATA = RETURNCODE_ERROR_FIRST_USER;
	public final static int RETURNCODE_ERROR_LOAD_PROVIDER_DATA = RETURNCODE_ERROR_FIRST_USER + 1;
	public final static int RETURNCODE_ERROR_INVALID_CREDENTIAL = RETURNCODE_ERROR_FIRST_USER + 2;
	/** Sms destination is empty */
	public final static int RETURNCODE_ERROR_INVALID_DESTINATION = RETURNCODE_ERROR_FIRST_USER + 3;
    /** Sms sender is empty */
    public final static int RETURNCODE_ERROR_INVALID_SENDER = RETURNCODE_ERROR_FIRST_USER + 4;
	public static final int RETURNCODE_ERROR_EMPTY_REPLY = RETURNCODE_ERROR_FIRST_USER + 5;
    /** Conversation with provider ends correctly, but SMS was not send or command doesn't complete for some reason */
    public static final int RETURNCODE_ERROR_PROVIDER_ERROR_REPLY = RETURNCODE_ERROR_FIRST_USER + 6;

	
	
	//---------- Public properties

	
	
	
	//---------- Public methods
    /**
     * Move Exception and return code to another {@link RainbowResultOperation} but with a different type
     * @param <T>
     * @param newResultOperation
     * @return
     */
    public <T> ResultOperation<T> translateError(ResultOperation<T> newResultOperation) {
        if (null == newResultOperation) return null;
        
        newResultOperation.setException(mException, mReturnCode);
        return newResultOperation;
    }
	
	
	
	
	//---------- Private methods

}
