package it.rainbowbreeze.smsforfree.domain;

import android.text.TextUtils;


public abstract class SmsService
	implements Comparable<SmsService>
{
	//---------- Ctors
	protected SmsService()
	{}
	
	protected SmsService(int numberOfParameters) {
		if (numberOfParameters < 1) {
			mParametersDesc = null;
			mParametersValue = null;
		} else {
			mParametersDesc = new String[numberOfParameters];
			mParametersValue = new String[numberOfParameters];
		}
	}

	
	
	
	//---------- Private fields
	
	
	
	
	//---------- Public properties
	
	/** Id for a new service */
	public static final String NEWSERVICEID = "-1";

	/** The id of the service */
	public abstract String getId();
	
	/** The display name of the service */
	public abstract String getName();
	
	/** Number of parameters of service */
	public abstract int getParametersNumber();

	/** Max length of sms message */
	public abstract int getMaxMessageLenght();
	
	/** Referring template */
	protected String mTemplateId;
	public String getTemplateId()
	{ return mTemplateId; }
	public void setTemplateId(String value)
	{ mTemplateId = value; }
	
	
	/** Description of service parameters */ 
	protected String[] mParametersDesc;
	public String[] getParametersDesc()
	{ return mParametersDesc; }
	public String getParameterDesc(int index)
	{ return getArrayItem(mParametersDesc, index); }
	public void setParameterDesc(int index, String value)
	{ setArrayItem(mParametersDesc, index, value); }
	
	/** Value of service parameters */ 
	protected String [] mParametersValue;
	public String[] getParametersValue()
	{ return mParametersValue; }
	public String getParameterValue(int index)
	{ return getArrayItem(mParametersValue, index); }
	public void setParameterValue(int index, String value)
	{ setArrayItem(mParametersValue, index, value); }
	
	/** Visual attributes of service parameters */ 
	protected String [] mParametersAttributes;
	public String[] getParametersAttributes()
	{ return mParametersValue; }
	public String getParameterAttributes(int index)
	{ return getArrayItem(mParametersAttributes, index); }
	public void setParameterAttributes(int index, String value)
	{ setArrayItem(mParametersAttributes, index, value); }
	
	

		
	//---------- Public methods

	/**
	 * Returns if service has parameter configured
	 */
	public boolean hasParametersConfigured()
	{
		if (getParametersNumber() == 0)
			return true;

		if (null == mParametersValue)
			return false;
		
		for(String value : mParametersValue)
			if (!TextUtils.isEmpty(value)) return true;
		
		return false;
	}
	
	@Override
	public boolean equals(Object aThat) {
	    //check for self-comparison
	    if ( this == aThat ) return true;

	    //use instanceof instead of getClass here for two reasons
	    //1. if need be, it can match any supertype, and not just one class;
	    //2. it renders an explict check for "that == null" redundant, since
	    //it does the check for null already - "null instanceof [type]" always
	    //returns false. (See Effective Java by Joshua Bloch.)
	    if ( !(aThat instanceof SmsService) ) return false;
	    //Alternative to the above line :
	    //if ( aThat == null || aThat.getClass() != this.getClass() ) return false;

	    //cast to native object is now safe
	    SmsService that = (SmsService)aThat;

	    return getId().equals(that.getId());
	}
	
	@Override
	public String toString() {
		//used by Spinner adapter
		return getName();
	}

	public int compareTo(SmsService another) {
		if (null != another)
			return this.getName().compareTo(another.getName());
		else
			return 1;
	}

	
	
	
	//---------- Private methods
	protected String getArrayItem(String[] array, int index){
		return index < 0 && index >= array.length ? "" : array[index]; 
	}

	protected void setArrayItem(String[] array, int index, String value) {
		if (index >= 0 && index < array.length) array[index] = value; 
	}
}
