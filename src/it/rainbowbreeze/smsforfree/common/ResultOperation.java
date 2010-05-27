/**
 * 
 */
package it.rainbowbreeze.smsforfree.common;

/**
 * Class for result operation
 *
 * @author Alfredo Morresi
 */
public class ResultOperation
{
	//---------- Ctors
	public ResultOperation()
	{
		startOperation();
	}
	
	public ResultOperation(Exception ex)
	{
		this();
		mException = ex;
	}

	public ResultOperation(boolean value)
	{
		this();
		setResultAsBoolean(value);
	}

	public ResultOperation(String value)
	{
		this();
		setResultAsString(value);
	}

	
	
	
	//---------- Private fields


	
	
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
	{ return null != mException; }
	
	
	/**
	 * Execute start operations (clean internal values)
	 */
	public void startOperation()
	{
		mException = null;
		mResult = null;
	}
	
	
	
	//---------- Private methods

}
