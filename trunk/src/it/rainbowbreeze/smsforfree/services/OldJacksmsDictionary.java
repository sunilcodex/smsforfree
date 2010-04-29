package it.rainbowbreeze.smsforfree.services;

public class OldJacksmsDictionary {
	//---------- Ctors




	//---------- Private fields
	private static String URL_BASE = "http://sync.jacksms.it";

	
	

	//---------- Public fields
	public static final String PARAMS_SEPARATOR = "\n";
	public static final String PARAM_JCODE = "JCODE=";
	public static final String PARAM_SENT = "SENT=";
	public static final String PARAM_CREDENTIALS_CORRECT = "UPDATE=";
	public static final String CREDENTIALS_CORRECT = "2";


	

	//---------- Public properties
	public String getSessionCodeUrl()
	{ return URL_BASE; }

	public String getCheckCredentialsUrl()
	{ return URL_BASE; }

    
    

	//---------- Public methods
	
	

	
	//---------- Private methods

}
