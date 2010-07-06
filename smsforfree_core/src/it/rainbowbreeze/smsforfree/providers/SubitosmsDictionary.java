/**
 * 
 */
package it.rainbowbreeze.smsforfree.providers;

import java.util.HashMap;

/**
 * @author rainbowbreeze
 *
 */
public class SubitosmsDictionary
{

	//---------- Ctors

	//---------- Private fields
	private static final String BASE_URL = "http://www.subitosms.it/gateway.php";
	private static final String PARAM_USERNAME = "username";
	private static final String PARAM_PASSWORD = "password";




	//---------- Public properties
	public String getBaseUrl()
	{ return BASE_URL; }



	
	//---------- Public methods
	/**
	 * Prepares parameters for the call for checking credits
	 * @param username
	 * @param password
	 * @return
	 */
	public HashMap<String, String> getParametersForCreditCheck(String username, String password)
	{
		HashMap<String, String> params =  new HashMap<String, String>();
		getParametersForApiLogin(username, password, params);
		return params;
	}
	
	
	
	
	//---------- Private methods
	/**
	 * Append username and password to the parameters
	 * @param username
	 * @param password
	 * @return
	 */
	private void getParametersForApiLogin(
			String username,
			String password,
			HashMap<String, String> params)
	{
		params.put(PARAM_USERNAME, username);
    	params.put(PARAM_PASSWORD, password);
	}
}
