package it.rainbowbreeze.smsforfree.domain;

import java.util.List;

import it.rainbowbreeze.smsforfree.common.ResultOperation;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;


/**
 * Base class for service configuration.
 * 
 * Id, Name, Parameters number, Max message lenght are mandatory
 *  
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public abstract class SmsService
	implements Comparable<SmsService>
{
	//---------- Ctors
	protected SmsService()
	{}
	
	protected SmsService(int numberOfParameters) {
		if (numberOfParameters < 1) {
			mParameters = null;
		} else {
			mParameters = new SmsServiceParameter[numberOfParameters];
			for(int i = 0; i < numberOfParameters; i++)
				mParameters[i] = new SmsServiceParameter();
		}
	}

	
	
	
	//---------- Private fields
	
	
	
	
	//---------- Public properties
	
	/** Id for a new service */
	public static final String NEWSERVICEID = "-1";

	/** The id of the service*/
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
	
	/** Service long description */
	protected String mDescription;
	public String getDescription()
	{ return mDescription; }
	public void setDescription(String value)
	{ mDescription = value; }

	/** Description of service parameters */
	protected SmsServiceParameter[] mParameters;
	
	public SmsServiceParameter getParameter(int index)
	{ return index >= 0 && index < mParameters.length ? mParameters[index] : null; }
	
	public String getParameterDesc(int index)
	{ return index >= 0 && index < mParameters.length ? mParameters[index].getDesc() : "";  }
	public void setParameterDesc(int index, String value)
	{ if (index >= 0 && index < mParameters.length) mParameters[index].setDesc(value); }
	
	/** Value of service parameters */ 
	public String getParameterValue(int index)
	{ return index >= 0 && index < mParameters.length ? mParameters[index].getValue() : ""; }
	public void setParameterValue(int index, String value)
	{ if (index >= 0 && index < mParameters.length) mParameters[index].setValue(value); }
	
	/** Visual attributes of service parameters */ 
	public int getParameterFormat(int index)
	{ return index >= 0 && index < mParameters.length ? mParameters[index].getFormat(): SmsServiceParameter.FORMAT_NONE ; }
	public void setParameterFormat(int index, int value)
	{ if (index >= 0 && index < mParameters.length) mParameters[index].setFormat(value); }
	
	/**
	 * Returns the list of commands to add to option menu of ActSmsServiceSetting
	 * activity
	 */
    public List<SmsServiceCommand> getSettingsActivityCommands()
    { return null; }




	//---------- Public methods

	/**
	 * Returns if service has parameter configured
	 */
	public boolean hasParametersConfigured()
	{
		if (getParametersNumber() == 0)
			return true;

		if (null == mParameters)
			return false;
		
		//check if mandatory parameters have a value
		boolean result = true;
		for(SmsServiceParameter param : mParameters)
			if (!param.isOptional())
				result &= !TextUtils.isEmpty(param.getValue());
		
		return result;
	}

	
	/**
	 * Service has additional commands to show on option menu of
	 * ActSettingSmsService activity
	 */
    public boolean hasSettingsActivityCommands()
    {
    	List<SmsServiceCommand> commands = getSettingsActivityCommands();
    	if (null == commands) return false;
    	return commands.size() > 0;
    }
    
    /**
     * Executes a command identified by its id
     * 
     * @param commandId
     * @param extraData
     * @return String with command result
     */
    public ResultOperation<String> executeCommand(int commandId, Context context, Bundle extraData)
    { return null; }

    
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
}
