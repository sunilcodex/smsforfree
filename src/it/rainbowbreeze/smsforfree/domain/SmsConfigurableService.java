package it.rainbowbreeze.smsforfree.domain;

import it.rainbowbreeze.smsforfree.common.ResultOperation;

public class SmsConfigurableService
	extends SmsService
{
	//---------- Ctors
	public SmsConfigurableService(int numberOfParameters) {
		super(numberOfParameters);
		mNumberOfParameters = numberOfParameters;
	}
	
	public SmsConfigurableService(String id, String name, int maxLen, int numberOfParameters) {
		this(numberOfParameters);
		setId(id);
		setName(name);
		setMaxMessageLenght(maxLen);
	}
	
	public SmsConfigurableService(String id, String templateId, String name, int maxLen, int numberOfParameters) {
		this(numberOfParameters);
		setId(id);
		setTemplateId(templateId);
		setName(name);
		setMaxMessageLenght(maxLen);
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
	@Override
	public boolean canVerifyCredentials()
	{ return false; }
	
	@Override
	public ResultOperation verifyCredentials()
	{ return null; }

	
	
	
	//---------- Private methods

}
