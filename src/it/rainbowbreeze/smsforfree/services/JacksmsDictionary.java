package it.rainbowbreeze.smsforfree.services;

import it.rainbowbreeze.smsforfree.util.Base64;

import java.util.HashMap;

import android.text.TextUtils;

/**
 * 
 * @author Alfredo "rainbowbreeze" Morresi
 *
 */
public class JacksmsDictionary
{
	//---------- Ctors
	public JacksmsDictionary() {
	}
	
	

	//---------- Public fields



	
	//---------- Private fields
	private static final String FORMAT_CSV = "csv";
	private static final String FORMAT_XML = "xml";
	private static final String FORMAT_JSON = "jsn";

	private static final String URL_BASE = "http://q.jacksms.it/";
	private static final String ACTION_GET_SERVICE_FULL = "getServiceFull";
	private static final String ACTION_SEND_MESSAGE = "sendMessage";
	
	private static final String PARAM_OUTPUTFORMAT = "outputFormat=" + FORMAT_XML;
	private static final String PARAM_CLIENTVERSION = "clientVersion=android";
	private static final String SEPARATOR = "\t";

	private static final String USER_TEST = "guest";


	//---------- Public properties


	//---------- Public methods
	public String getUrlForSendingMessage(String username, String password)
	{
		return getUrlForCommand(username, password, ACTION_SEND_MESSAGE);
	}

	
	public HashMap<String, String> getHeaderForSendingMessage(
			JacksmsUserService service,
			String destination,
			String message)
	{
		String key;
		String value;
		HashMap<String, String> headers = new HashMap<String, String>();
		
		//first header
		key = "J_R";
		value = String.valueOf(service.getServiceId()) + SEPARATOR + 
				destination + SEPARATOR +
				service.getUsername() + SEPARATOR +
				service.getPassword() + SEPARATOR +
				replaceServiceParameter(service.getFreeField1()) + SEPARATOR +
				replaceServiceParameter(service.getFreeField2());
		headers.put(key, value);
		
		
		//second header
		key = "J_M";
		value = message;
		headers.put(key, value);
		
		return headers;
	}




	//---------- Private methods
	private String getUrlForCommand(String username, String password, String command)
	{
		String codedUser;
		String codedPwd;
		
		if (USER_TEST.equals(username)){
			codedUser = USER_TEST;
			codedPwd = USER_TEST;
		}else{
			codedUser = replaceNotAllowedChars(Base64.encodeBytes(username.getBytes()));
			codedPwd = replaceNotAllowedChars(Base64.encodeBytes(password.getBytes()));
		}
		StringBuilder sb = new StringBuilder();
		sb.append(URL_BASE)
			.append(codedUser)
			.append("/")
			.append(codedPwd)
			.append("/")
			.append(command)
			.append("?")
			.append(PARAM_OUTPUTFORMAT)
			.append("&")
			.append(PARAM_CLIENTVERSION);
		return sb.toString();
	}
	

	private String replaceNotAllowedChars(String sourceString)
	{
		return sourceString.replace("+", "-").replace("/", "_");
	}

	
	private String replaceServiceParameter(String parameter)
	{
		return TextUtils.isEmpty(parameter) ? "" : parameter;
	}

}