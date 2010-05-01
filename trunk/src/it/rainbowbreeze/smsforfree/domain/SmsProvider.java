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
{
	//---------- Ctors

	
	
	
	//---------- Private fields

	
	
	
	//---------- Public properties
	/** The provider id	*/
	public abstract String getId();
	
	/** The provider name */
	public abstract String getName();
	
	/** Has this provider sub-services? */
	public abstract boolean hasConfigurations();
	

	
	
	//---------- Public fields
	public static final String ERROR_NO_USERNAME = "NO_USERNAME";
	public static final String ERROR_NO_PASSWORD = "NO_PASSWORD";
	public static final String ERROR_CREDENTIALS_NOT_VALID = "CREDENTIALS_INVALID";
	public static final String ERROR_NO_REPLY_FROM_SITE = "NO_REPLY";
	
	


	//---------- Public methods
	
	public abstract List<String> getServiceParametersDesc();

	public abstract List<String> getServiceParameters();

	public abstract void setServiceParameters(List<String> parameters);
	
	public abstract List<String> getAllConfigurationsName();

	public abstract List<String> getConfiguredConfigurationsName();

	public abstract List<String> getConfigurationParametersDesc(String configurationId);

	public abstract List<String> setConfigurationParameters(String configurationId, List<String> parameters);

	public abstract List<String> getConfigurationParameters(String configurationId);

	
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
