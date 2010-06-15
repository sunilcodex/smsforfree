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
	public ResultOperation(Exception ex)
	{
		mReturnCode = RETURNCODE_ERROR;
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
	public final static int RETURNCODE_ERROR = 400;
	public final static int RETURNCODE_CAPTCHA_REQUEST = 1001;
	public final static int RETURNCODE_APP_EXPIRED = 1002;
	public final static int RETURNCODE_SMS_DAILY_LIMIT_OVERTAKED = 1003;


	
	
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
	public void setException(Exception newValue)
	{ mException = newValue; }
	
	
	
	
	//---------- Public methods

	/**
	 * Return if the object contains error
	 */
	public boolean HasErrors()
	{ return null != mException || mReturnCode == RETURNCODE_ERROR; }
	
	
	
	
	//---------- Private methods

}
