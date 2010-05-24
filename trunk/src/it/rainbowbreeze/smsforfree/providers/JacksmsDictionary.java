package it.rainbowbreeze.smsforfree.providers;

import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.util.Base64;

import java.util.HashMap;

import android.text.TextUtils;

/**
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
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
	public static final String RESULT_OK = "1";
	public static final String RESULT_ERROR = "0";


	//---------- Public methods
	public String getUrlForSendingMessage(String username, String password)
	{
		return getUrlForCommand(username, password, ACTION_SEND_MESSAGE);
	}

	
	public HashMap<String, String> getHeaderForSendingMessage(
			SmsService service,
			String destination,
			String message)
	{
		String key;
		String value;
		HashMap<String, String> headers = new HashMap<String, String>();
		
		//first header
		key = "J_R";
		value = String.valueOf(service.getTemplateId()) + SEPARATOR + 
				destination + SEPARATOR +
				replaceServiceParameter(service.getParameterValue(0)) + SEPARATOR +
				replaceServiceParameter(service.getParameterValue(1)) + SEPARATOR +
				replaceServiceParameter(service.getParameterValue(2)) + SEPARATOR +
				replaceServiceParameter(service.getParameterValue(3));
		headers.put(key, value);
		
		
		//second header
		key = "J_M";
		value = message;
		headers.put(key, value);
		
		return headers;
	}
	
	
	/**
	 * Extracts the message text from provider's reply
	 * @param reply
	 * @return
	 */
	public String getTextPartFromReply(String reply)
	{
		if (TextUtils.isEmpty(reply)) return "";
		
		String[] lines = reply.split(SEPARATOR);
		if (lines.length > 2)
			//message is in the second item
			return lines[1];
		else
			return "";
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