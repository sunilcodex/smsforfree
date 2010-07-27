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

/**
 * Class for result operation.
 * Similar to http reply, where there are a return code
 * and a reply
 *
 * @author Alfredo Morresi
 */
public class ResultOperation<T>
{
	//---------- Ctors
	public ResultOperation()
	{
		mReturnCode = RETURNCODE_OK;
	}

	public ResultOperation(Exception ex, int errorReturnCode)
	{
		mReturnCode = errorReturnCode;
		mException = ex;
	}

	public ResultOperation(T value)
	{
		this(RETURNCODE_OK, value);
	}

	public ResultOperation(int returnCode, T value)
	{
		setReturnCode(returnCode);
		setResult(value);
	}


	
	//---------- Public fields
	/** All done! */
	public final static int RETURNCODE_OK = 200;
	/** User should decode a captcha in order to send sms */
	public final static int RETURNCODE_SMS_CAPTCHA_REQUEST = 1001;
	/** Conversation with provider ends correctly, but SMS was not send or command doesn't complete for some reason*/
	public static final int RETURNCODE_PROVIDER_ERROR = 1002;
	/** Application is expired */
	public final static int RETURNCODE_APP_EXPIRED = 1003;
	/** SMS daily limit overtaked */
	public final static int RETURNCODE_SMS_DAILY_LIMIT_OVERTAKED = 1004;

	/** Errors code */
	public final static int RETURNCODE_ERROR_GENERIC = 400;
	public final static int RETURNCODE_ERROR_SAVE_PROVIDER_DATA = 401;
	public final static int RETURNCODE_ERROR_LOAD_PROVIDER_DATA = 402;
	public final static int RETURNCODE_ERROR_NOCREDENTIAL = 403;
	public static final int RETURNCODE_ERROR_COMMUNICATION = 404;
	public static final int RETURNCODE_ERROR_APPLICATION_ARCHITECTURE = 405;
	public static final int RETURNCODE_ERROR_EMPTY_REPLY = 406;
	

	
	
	//---------- Public properties

	T mResult;
	public T getResult()
	{return mResult; }
	public void setResult(T newValue)
	{ mResult = newValue; }
	
	int mReturnCode;
	public int getReturnCode()
	{ return mReturnCode; }
	public void setReturnCode(int newValue)
	{ mReturnCode = newValue; }
	
	
	protected Exception mException;
	public Exception getException()
	{ return mException; }
	public void setException(Exception newValue, int returnCode)
	{
		mException = newValue;
		mReturnCode = returnCode;
	}
	
	
	
	
	//---------- Public methods

	/**
	 * Return if the object contains error
	 */
	public boolean hasErrors()
	{ return null != mException; }
	
	
	
	
	//---------- Private methods

}
