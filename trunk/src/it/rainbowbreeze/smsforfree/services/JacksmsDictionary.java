package it.rainbowbreeze.smsforfree.services;

import it.rainbowbreeze.smsforfree.util.Base64;

import java.util.HashMap;

import android.text.TextUtils;

public class JacksmsDictionary
{
	//---------- Ctors
	public JacksmsDictionary() {
		
	}
	
	

	//---------- Public fields
	public static final String FORMAT_CSV = "csv";
	public static final String FORMAT_XML = "xml";
	public static final String FORMAT_JSON = "jsn";




	//---------- Private fields
	private static final String URL_BASE = "http://q.jacksms.it";
	private static final String PARAM_OUTPUTFORMAT = "outputFormat=";
	private static final String PARAM_CLIENTVERSION = "clientVersion=android";
	private static final String SEPARATOR = "\t";




	//---------- Public properties




	//---------- Public methods




	//---------- Private methods
	private String getUrlForCommand(String username, String password, String command)
	{
		String codedUser = replaceNotAllowedChars(Base64.encodeBytes(username.getBytes()));
		String codedPwd = replaceNotAllowedChars(Base64.encodeBytes(password.getBytes()));
		StringBuilder sb = new StringBuilder();
		sb.append(URL_BASE)
			.append(codedUser)
			.append("/")
			.append(codedPwd)
			.append("/")
			.append(command)
			.append("?")
			.append(PARAM_OUTPUTFORMAT)
			.append(FORMAT_JSON)
			.append("&")
			.append(PARAM_CLIENTVERSION);
		return sb.toString();
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
		value = String.valueOf(service.getId()) + SEPARATOR + 
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
	
	
	
	private String replaceNotAllowedChars(String sourceString)
	{
		return sourceString.replace("+", "-").replace("/", "_");
	}

	
	private String replaceServiceParameter(String parameter)
	{
		return TextUtils.isEmpty(parameter) ? "_" : parameter;
	}

}