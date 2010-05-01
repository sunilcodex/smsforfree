package it.rainbowbreeze.smsforfree.domain;

import java.util.List;

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
	
	public abstract List<SmsService> getAllSubservice();

	public abstract List<SmsConfigurableService> getAllConfiguredSubservice();

	public abstract SmsService getSubservice(String subserviceId);

    public abstract void setSelectedSubservice(String subserviceId);
	
	
	//---------- Public methods
	
	
	
	//---------- Private methods
	
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
