package it.rainbowbreeze.smsforfree.domain;

public class SmsConfigurableService
	extends SmsService
{
	//---------- Ctors
	public SmsConfigurableService(int numberOfParameters) {
		super(numberOfParameters);
		mNumberOfParameters = numberOfParameters;
	}

	
	
	
	//---------- Private fields
	
	
	
	//---------- Public properties
	
	/** The id of the service */
	protected String mId;
	@Override
	public String getId()
	{ return mId; }
	public void setId(String value)
	{ mId = value; }
	
	/** The display name of the service */
	protected String mName;
	@Override
	public String getName()
	{ return mName; }
	public void setName(String value)
	{ mName = value; }
	
	/** Number of parameters of service */
	protected int mNumberOfParameters;
	@Override
	public int getParametersNumber()
	{ return mNumberOfParameters; }

	protected int mMaxMessageLenght;
	@Override
	public int getMaxMessageLenght()
	{ return mMaxMessageLenght; }
	public void setMaxMessageLenght(int value)
	{ mMaxMessageLenght = value; }
	
	

	
	
	
	//---------- Public methods

	
	
	
	//---------- Private methods

}
