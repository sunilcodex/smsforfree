/**
 * Copyright (C) 2010 Alfredo Morresi
 * 
 * This file is part of SmsForFree project.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/>.
 */

package it.rainbowbreeze.smsforfree.providers;

import it.rainbowbreeze.smsforfree.domain.SmsConfigurableService;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.util.Base64;
import it.rainbowbreeze.smsforfree.util.GlobalUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
	private static final String ACTION_GET_ALL_TEMPLATES = "getProviders";
	private static final String ACTION_SEND_MESSAGE = "sendMessage";
	private static final String ACTION_SEND_CAPTCHA = "continueSend";
	private static final String ACTION_GET_USER_SERVICES = "getServicesFull";
	
	private static final String PARAM_OUTPUTFORMAT = "outputFormat=";
	private static final String PARAM_CLIENTVERSION = "clientVersion=";
	private static final String PARAM_CLIENTVERSION_VALUE = "android";
	private static final String CSV_SEPARATOR = "\t";

	private static final String USER_TEST = "guest";
	
	/** Max number of parameters a JackSMS service can have */
	private static final int MAX_SERVICE_PARAMETERS = 4;
	
	//message sent
	private static final String PREFIX_RESULT_OK = "1" + CSV_SEPARATOR;
	//JackSMS has different error signatures
	private static final String[] PREFIX_RESULT_ERROR_ARRAY = {
		"error" + CSV_SEPARATOR,
		"0" + CSV_SEPARATOR
		};
	
	


	//---------- Public properties

	
	

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

	public String getUrlForDownloadUserServices(String username, String password) {
		return getUrlForCommand(username, password, ACTION_GET_USER_SERVICES);
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
		key = "J-R";
		value = String.valueOf(service.getTemplateId()) + CSV_SEPARATOR + 
				destination + CSV_SEPARATOR +
				replaceServiceParameter(service.getParameterValue(0)) + CSV_SEPARATOR +
				replaceServiceParameter(service.getParameterValue(1)) + CSV_SEPARATOR +
				replaceServiceParameter(service.getParameterValue(2)) + CSV_SEPARATOR +
				replaceServiceParameter(service.getParameterValue(3));
		headers.put(key, value);
		
		
		//second header
		key = "J-M";
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
		key = "J-R";
		value = String.valueOf(sessionId) + CSV_SEPARATOR + 
				captchaCode + CSV_SEPARATOR;
		headers.put(key, value);
		
		return headers;
	}
	
	/**
	 * Replace illegal chars in the message
	 * @param body
	 * @return
	 */
	public String adjustMessageBody(String body) {
		body.replace("\n", " ");
		body.replace("\t", " ");
		body.replace("\r", " ");
		return body;
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
		String[] lines = reply.split(CSV_SEPARATOR);
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
	public byte[] getCaptchaImageContentFromReply(String reply)
	{
		//captcha content is the text part of the reply
		String content = getTextPartFromReply(reply);
		
		//is encoded in base64, so decode id
		byte[] decodedCaptcha;
		try {
			//decodedCaptcha = new String(Base64.decode(content), "UTF-8");
			decodedCaptcha = Base64.decode(content);
		} catch (IOException e) {
			decodedCaptcha = null;
		}
		return decodedCaptcha;
	}
	
	/**
	 * Extract sessionId from captcha reply
	 * @param reply
	 * @return
	 */
	public String getCaptchaSessionIdFromReply(String reply)
	{
		//find captcha sessionId, the first part of the message
		int separatorPos = reply.indexOf(CSV_SEPARATOR);
		if (separatorPos < 0)
			return "";
		
		String sessionId = reply.substring(0, separatorPos).trim();
		return sessionId;
	}

	
	public List<SmsService> extractTemplates(String providerReply)
	{
		List<SmsService> templates = new ArrayList<SmsService>();
		
		//examine the reply, line by line
		String[] lines = providerReply.split(String.valueOf((char) 10));
		
		for(String templateLine : lines) {
			String[] pieces = templateLine.split(CSV_SEPARATOR);
			try {
				String serviceId = pieces[0];
				String serviceName = pieces[1];
				int maxChar = Integer.parseInt(pieces[2]);
				String[] parametersDesc = new String[MAX_SERVICE_PARAMETERS];

				int numberOfParameters = MAX_SERVICE_PARAMETERS;
				for(int i = 0; i < MAX_SERVICE_PARAMETERS; i++) {
					parametersDesc[i] = pieces[3+i];
					//find the total number of parameter
					if (TextUtils.isEmpty(parametersDesc[i])) numberOfParameters--;
				}
				//create new service
				parametersDesc = (String[]) GlobalUtils.resizeArray(parametersDesc, numberOfParameters);
				SmsService newTemplate = new SmsConfigurableService(serviceId, serviceName, maxChar, parametersDesc);
				//sometimes the service description could be unavailable
				if (pieces.length > 7)
					newTemplate.setDescription(pieces[7]);
				templates.add(newTemplate);
			} catch (Exception e) {
				//do nothing, simply skips to next service
			}
		}
		
		Collections.sort(templates);
		return templates;
	}
	
	
	public List<SmsConfigurableService> extractUserServices(String providerReply)
	{
		List<SmsConfigurableService> services = new ArrayList<SmsConfigurableService>();
		
		//examine the reply, line by line
		String[] lines = providerReply.split(String.valueOf((char) 10));
		
		for(String serviceLine : lines) {
			String[] pieces = serviceLine.split(CSV_SEPARATOR);
			try {
				String serviceId = pieces[0];
				String templateId = pieces[1];
				String serviceName = pieces[2];
				String[] parametersValue = new String[MAX_SERVICE_PARAMETERS];

				int numberOfParameters = MAX_SERVICE_PARAMETERS;
				for(int i = 0; i < MAX_SERVICE_PARAMETERS; i++) {
					if (pieces.length > 3+i)
						parametersValue[i] = new String(Base64.decode(pieces[3+i]));
					else
						parametersValue[i] = "";
					//find the total number of parameter
					if (TextUtils.isEmpty(parametersValue[i])) numberOfParameters--;
				}
				//create new service
				parametersValue = (String[]) GlobalUtils.resizeArray(parametersValue, numberOfParameters);
				SmsConfigurableService newService = new SmsConfigurableService(serviceId, templateId, serviceName, parametersValue);
				services.add(newService);
			} catch (Exception e) {
				//do nothing, simply skips to next service
			}
		}
		
		Collections.sort(services);
		return services;
	}


	public boolean isSmsCorrectlySend(String webserviceReply)
	{
		if (TextUtils.isEmpty(webserviceReply)) return false;
		return webserviceReply.startsWith(JacksmsDictionary.PREFIX_RESULT_OK);
	}

	public boolean isCaptchaRequest(String webserviceReply) {
		if (TextUtils.isEmpty(webserviceReply)) return false;
		
		//find first part of the message
		int pos = webserviceReply.indexOf(CSV_SEPARATOR);
		if (pos < 0) return false;
		
		//find the number at the start of the message
		String token = webserviceReply.substring(0, pos);

		int number = 1;
		try {
			number = Integer.parseInt(token);
		} catch (Exception e) {
			return false;
		}
		return 1 != number; 
	}

	/**
	 * Checks if the reply for webservice contains errors or not
	 * @param webserviceReply
	 * @return
	 */
	public boolean isErrorReply(String webserviceReply)
	{
		if (TextUtils.isEmpty(webserviceReply)) return true;
		
		//explicit error return string
		for (String errSignature : PREFIX_RESULT_ERROR_ARRAY) {
			if (webserviceReply.startsWith(errSignature)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isInvalidCredetials(String webserviceReply)
	{
		if (TextUtils.isEmpty(webserviceReply)) return false;
		
		return ("error	Dati di accesso JackSMS non validi").equals(webserviceReply);
	}

	/**
	 * Checks if the reply for webservice contains some strange strings
	 * that are not recognized as errors, but don't allow the app to work
	 * @param webserviceReply
	 * @return
	 */
	public boolean isUnmanagedErrorReply(String webserviceReply)
	{
		if (TextUtils.isEmpty(webserviceReply)) return true;

		return (webserviceReply.startsWith("<!DOCTYPE HTML PUBLIC"));
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
			.append(FORMAT_CSV)
			.append(",")
			.append(PARAM_CLIENTVERSION_VALUE);
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