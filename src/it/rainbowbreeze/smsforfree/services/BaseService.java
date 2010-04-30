package it.rainbowbreeze.smsforfree.services;

import java.util.List;

import android.text.TextUtils;


/**
 * Base class for sms service management
 * 
 *  Each service could have more "configurations" (for example, multi-provider service)
 * 
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public abstract class BaseService
{
	//---------- Private fields

	
	
	
	//---------- Public properties
	
	/**
	 * The service name
	 */
	public abstract String getServiceName();

	/**
	 * Has this service configurations?
	 */
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
