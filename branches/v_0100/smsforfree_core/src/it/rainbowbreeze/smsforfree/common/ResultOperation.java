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
	public final static int RETURNCODE_OK = 200;
	public final static int RETURNCODE_CAPTCHA_REQUEST = 1001;
	public final static int RETURNCODE_APP_EXPIRED = 1002;
	public final static int RETURNCODE_SMS_DAILY_LIMIT_OVERTAKED = 1003;

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
	{ return null != mException || mReturnCode == RETURNCODE_ERROR_GENERIC; }
	
	
	
	
	//---------- Private methods

}
