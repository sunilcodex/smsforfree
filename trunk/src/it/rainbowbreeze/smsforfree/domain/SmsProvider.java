package it.rainbowbreeze.smsforfree.domain;

import it.rainbowbreeze.smsforfree.common.ResultOperation;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;


/**
 * Base provider for sms service
 * 
 *  Each service could have more "sub-services" (for example, the service act as a
 *  unified "proxy" for other services)
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public abstract class SmsProvider
	extends SmsService
{
	//---------- Ctors
	protected SmsProvider()
	{ super(); }
	
	protected SmsProvider(int numberOfParameters)
	{ super(numberOfParameters); }

	
	
	
	//---------- Private fields

	
	
	
	//---------- Public fields
	public static final String ERROR_NO_USERNAME = "NO_USERNAME";
	public static final String ERROR_NO_PASSWORD = "NO_PASSWORD";
	public static final String ERROR_CREDENTIALS_NOT_VALID = "CREDENTIALS_INVALID";
	public static final String ERROR_NO_REPLY_FROM_SITE = "NO_REPLY";
	
	


	//---------- Public properties
	
	/** Has this provider sub-services? */
	public abstract boolean hasSubServices();
	
	public abstract List<SmsService> getTemplateSubservices();

	public abstract List<SmsService> getConfiguredSubservices();

	public abstract SmsService getConfiguredSubservice(String subserviceId);

    public abstract void setSelectedSubservice(String subserviceId);
	
	
	//---------- Public methods
	/**
	 * Send the message
	 * 
	 * @param destination
	 * @param body
	 */
	public abstract ResultOperation sendMessage(String serviceId, String destination, String body);
	
	
	
	public ResultOperation savePreferences(Context context){
		//TODO
		return null;
	}

	public ResultOperation loadPreferences(Context context){
		//TODO
		return null;
	}
	
	
	public ResultOperation saveSubservicesList(Context context){
		//TODO
		return null;
	}

	public ResultOperation loadSubservicesList(Context context){
		//TODO
		return null;
	}
	
	
	
	//---------- Private methods
	
	/** file name where save provider parameters */
	protected abstract String getParametersFileName();
	
	/** file name where save provider subservices */
	protected abstract String getSubservicesFileName();
	
	/**
	 * Checks if username and password are not empty
	 * Throws an error if one of the two are not correct
	 * 
	 * @param username
	 * @param password
	 */
	protected void checkCredentialsValidity(String username, String password)
		throws IllegalArgumentException
	{
    	if (TextUtils.isEmpty(username))
    		 throw new IllegalArgumentException(ERROR_NO_USERNAME);
    	if (TextUtils.isEmpty(password))
    		throw new IllegalArgumentException(ERROR_NO_PASSWORD);
	}

}
