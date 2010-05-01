package it.rainbowbreeze.smsforfree.domain;

public class SmsSubService
{
	//---------- Ctors
	public SmsSubService(int numberOfParameters) {
		mNumberOfParameters = numberOfParameters;
		mParametersDesc = new String[numberOfParameters];
		mParametersValue = new String[numberOfParameters];
	}

	
	
	
	//---------- Private fields
	private int mNumberOfParameters;
	
	
	
	//---------- Public properties
	
	/** The id of the sub service */
	protected String mId;
	public String getId()
	{ return mId; }
	public void setId(String value)
	{ mId = value; }
	
	/** The display name of the sub service */
	protected String mName;
	public String getName()
	{ return mName; }
	public void setName(String value)
	{ mName = value; }

	String[] mParametersDesc;
	public String[] getParametersDesc(int index)
	{ return mParametersDesc; }
	
	String [] mParametersValue;
	public String[] getParametersValue(int index)
	{ return mParametersValue; }

	
	
	
	//---------- Public methods

	
	
	
	//---------- Private methods

}
