/**
 * Copyright (C) 2010 Alfredo Morresi
 * 
 * This file is part of SmsForFree project.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/>.
 */

package it.rainbowbreeze.smsforfree.domain;

import java.util.List;

import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import static it.rainbowbreeze.libs.common.RainbowContractHelper.*;


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
	//---------- Private fields
	protected final LogFacility mLogFacility;
	
	
	
	
	//---------- Constructors
	protected SmsService(LogFacility logFacility){
		mLogFacility = checkNotNull(logFacility, "LogFacility");
	}
	
	protected SmsService(LogFacility logFacility, int numberOfParameters) {
		this(logFacility);
		if (numberOfParameters < 1) {
			mParameters = null;
		} else {
			mParameters = new SmsServiceParameter[numberOfParameters];
			for(int i = 0; i < numberOfParameters; i++)
				mParameters[i] = new SmsServiceParameter();
		}
	}

	
	
	
	//---------- Public properties
	
	/** Id for a new service starts from 2 because 0 and 1
		are reserved values*/
	public static final String NEWSERVICEID = "1";

	/** The id of the service*/
	public abstract String getId();
	
	/** The display name of the service */
	public abstract String getName();
	
	/** Number of parameters of service */
	public abstract int getParametersNumber();

	/** Max length of sms message */
	public abstract int getMaxMessageLenght();
	

	public abstract int getSingleLength();

	public abstract int getDivisorLength();
	
	/** Referring template */
	protected String mTemplateId;
	public String getTemplateId()
	{ return mTemplateId; }
	public void setTemplateId(String value)
	{ mTemplateId = value; }
	
	//jack
	/** Supported Operators*/
	protected String mSupportedOperators;
	public String getSupportedOperators(){return mSupportedOperators;}
	public void setSupportedOperators(String values){mSupportedOperators=values;}
	
	protected int mServiceType;
	public int getServiceType(){return mServiceType;}
	public void setServiceType(int value){mServiceType = value;}
	
	protected String mVersion;
	public String getVersion(){return mVersion;}
	public void setVersion(String value){mVersion = value;}
	
	/** Service long description */
	protected String mDescription;
	public String getDescription()
	{ return mDescription; }
	public void setDescription(String value)
	{ mDescription = value; }

	/** Description of service parameters */
	protected SmsServiceParameter[] mParameters;
	
	public SmsServiceParameter getParameter(int index)
	{ return null != mParameters && index >= 0 && index < mParameters.length ? mParameters[index] : null; }
	
	public String getParameterDesc(int index)
	{ return null != mParameters && index >= 0 && index < mParameters.length ? mParameters[index].getDesc() : "";  }
	public void setParameterDesc(int index, String value)
	{ if (null != mParameters && index >= 0 && index < mParameters.length) mParameters[index].setDesc(value); }
	
	/** Value of service parameters */ 
	public String getParameterValue(int index)
	{ return null != mParameters && index >= 0 && index < mParameters.length ? mParameters[index].getValue() : ""; }
	public void setParameterValue(int index, String value)
	{ if (null != mParameters && index >= 0 && index < mParameters.length) mParameters[index].setValue(value); }
	
	/** Visual attributes of service parameters */ 
	public int getParameterFormat(int index)
	{ return null != mParameters && index >= 0 && index < mParameters.length ? mParameters[index].getFormat(): SmsServiceParameter.FORMAT_NONE ; }
	public void setParameterFormat(int index, int value)
	{ if (null != mParameters && index >= 0 && index < mParameters.length) mParameters[index].setFormat(value); }
	
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
    {
		return new ResultOperation<String>(
				new Exception("No command with id " + commandId + " for " + getName()),
				ResultOperation.RETURNCODE_ERROR_APPLICATION_ARCHITECTURE);
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
}
