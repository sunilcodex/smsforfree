/**
 * 
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
	/** Conversation with provider ends correctly, but SMS was not send for some reason */
	public static final int RETURNCODE_INTERNAL_PROVIDER_ERROR = 1002;
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
	public boolean HasErrors()
	{ return null != mException; }
	
	
	
	
	//---------- Private methods

}
