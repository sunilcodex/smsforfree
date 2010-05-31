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
//	private static final String FORMAT_CSV = "csv";
	private static final String FORMAT_XML = "xml";
//	private static final String FORMAT_JSON = "jsn";

	private static final String URL_BASE = "http://q.jacksms.it/";
	private static final String ACTION_GET_ALL_TEMPLATES = "getProviders";
	private static final String ACTION_SEND_MESSAGE = "sendMessage";
	private static final String ACTION_SEND_CAPTCHA = "continueSend";
	
	private static final String PARAM_OUTPUTFORMAT = "outputFormat=" + FORMAT_XML;
	private static final String PARAM_CLIENTVERSION = "clientVersion=android";
	private static final String SEPARATOR = "\t";

	private static final String USER_TEST = "guest";
	

	//---------- Public properties
	//message sent
	public static final String PREFIX_RESULT_OK = "1";
	//JackSMS has different error signatures
	public static final String[] PREFIX_RESULT_ERROR_ARRAY = { "error", "0" };


	//---------- Public methods
	public String getUrlForSendingMessage(String username, String password)
	{
		return getUrlForCommand(username, password, ACTION_SEND_MESSAGE);
	}
	
	public String getUrlForSendingCaptcha(String username, String password) {
		return getUrlForCommand(username, password, ACTION_SEND_CAPTCHA);
	}
	
	public String getUrlForDownloadTemplates(String username, String password)
	{
		return getUrlForCommand(username, password, ACTION_GET_ALL_TEMPLATES);
	}

	
	/**
	 * Builds headers used in send sms api
	 * 
	 * @param service
	 * @param destination
	 * @param message
	 * @return
	 */
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
	 * Builds headers used in the captcha api
	 * @param sessionId
	 * @param captchaCode
	 * @return
	 */
	public HashMap<String, String> getHeaderForSendingCaptcha(String sessionId, String captchaCode)
	{
		String key;
		String value;
		HashMap<String, String> headers = new HashMap<String, String>();
		
		//first header
		key = "J_R";
		value = String.valueOf(sessionId) + SEPARATOR + 
				captchaCode + SEPARATOR;
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
		
		//if strings contains error codes from JackSMS
		for (String errorPrefix : PREFIX_RESULT_ERROR_ARRAY) {
			if (reply.startsWith(errorPrefix)) {
				return reply.substring(errorPrefix.length()).trim();
			}
		}
		
		//other reply from JackSMS
		String[] lines = reply.split(SEPARATOR);
		if (lines.length > 2)
			//message is in the second item
			return lines[1];
		else
			return "";
	}
	
	/**
	 * Extracts captcha image content from provider's reply
	 * @param reply
	 * @return
	 */
	public String getCaptchaImageContentFromReply(String reply)
	{
		//captcha content is the text part of the reply
		String content = getTextPartFromReply(reply);
		return content;
	}
	
	/**
	 * Extract sessionId from captcha reply
	 * @param reply
	 * @return
	 */
	public String getCaptchaSessionIdFromReply(String reply)
	{
		//find captcha sessionId, the first part of the message
		int separatorPos = reply.indexOf(SEPARATOR);
		if (separatorPos < 0)
			return "";
		
		String sessionId = reply.substring(0, separatorPos).trim();
		return sessionId;
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