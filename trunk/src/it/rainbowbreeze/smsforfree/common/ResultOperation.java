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
public class ResultOperation
{
	//---------- Ctors
	private ResultOperation()
	{
		//executes start operations (clean internal values)
		mException = null;
		mResult = null;
	}
	
	public ResultOperation(Exception ex)
	{
		this();
		mReturnCode = RETURNCODE_ERROR;
		mException = ex;
	}

	public ResultOperation(boolean value)
	{ this(RETURNCODE_OK, value); }

	public ResultOperation(int returnCode, boolean value)
	{
		this();
		setReturnCode(returnCode);
		setResultAsBoolean(value);
	}

	public ResultOperation(String value)
	{ this(RETURNCODE_OK, value); }

	public ResultOperation(int returnCode, String value)
	{
		this();
		setReturnCode(returnCode);
		setResultAsString(value);
	}

	public ResultOperation(byte[] value)
	{ this(RETURNCODE_OK, value); }

	public ResultOperation(int returnCode, byte[] value)
	{
		this();
		setReturnCode(returnCode);
		setResultAsByteArray(value);
	}

	
	
	
	//---------- Public fields
	public final static int RETURNCODE_OK = 200;
	public final static int RETURNCODE_ERROR = 400;
	public final static int RETURNCODE_CAPTCHA_REQUEST = 1001;


	
	
	//---------- Public properties

	Object mResult;
	public Integer getResultAsInt()
	{ return (Integer) mResult; }
	public void setResultAsInt(Integer newValue)
	{ mResult = newValue; }
	public String getResultAsString()
	{ return mResult != null ? mResult.toString() : ""; }
	public void setResultAsString(String newValue)
	{ mResult = newValue; }
	public Boolean getResultAsBoolean()
	{ return (Boolean) mResult; }
	public void setResultAsBoolean(Boolean newValue)
	{ mResult = newValue; }
	public byte[] getResultAsByteArray()
	{ return (byte[]) mResult; }
	public void setResultAsByteArray(byte[] newValue)
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
