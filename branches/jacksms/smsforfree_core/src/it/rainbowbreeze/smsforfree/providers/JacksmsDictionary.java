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

import static it.rainbowbreeze.libs.common.RainbowContractHelper.checkNotNull;
import it.rainbowbreeze.libs.helper.RainbowArrayHelper;
import it.rainbowbreeze.smsforfree.common.AppEnv;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.domain.SmsConfigurableService;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.helper.Base64Helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.text.TextUtils;

/**
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class JacksmsDictionary
{
	//---------- Private fields
    protected static final String LOG_HASH = "JacksmsDictionary";
    protected final LogFacility mLogFacility;
    
	private static final String FORMAT_CSV = "csv";
	//private static final String FORMAT_XML = "xml";
	//private static final String FORMAT_JSON = "jsn";

	public static final String URL_STREAM_BASE = "http://stream.jacksms.it/";
	public static final String URL_STREAM_TEST = "http://stream.jacksms.it:9911/";//per i test sul server
	public static final String URL_Q_BASE = "http://q.jacksms.it";
	private static final String ACTION_GET_ALL_TEMPLATES = "getProviders";
	private static final String ACTION_SEND_MESSAGE = "send?http&";
	private static final String ACTION_SEND_CAPTCHA = "continue?http&";
	private static final String ACTION_GET_USER_SERVICES = "getServicesFull";
	private static final String ACTION_GET_USER_LOGINSTRING = "getLoginString";
	private static final String ACTION_GET_EDITSERVICE = "editService";
	private static final String ACTION_GET_ADDSERVICE = "addService";
	private static final String ACTION_GET_DELSERVICE = "delService";

	//TODO: get version from global variable or settings
	private static final String PARAM_CLIENTVERSION_VALUE = "android="+AppEnv.APP_DISPLAY_VERSION;
	public static final String TAB_SEPARATOR = "\t";

	private static final String USER_TEST = "guest";
	
	/** Max number of parameters a JackSMS service can have */
	private static final int MAX_SERVICE_PARAMETERS = 4;
	
	//message sent
	private static final String PREFIX_RESULT_OK = "1" + TAB_SEPARATOR;
	//JackSMS has different error signatures
	private static final String[] PREFIX_RESULT_ERROR_ARRAY = {
		"error" + TAB_SEPARATOR,
		"0" + TAB_SEPARATOR
		};
	



	//---------- Constructors
	public JacksmsDictionary(LogFacility logFacility) {
	    mLogFacility = checkNotNull(logFacility, "LogFacility");
	}
	
	

	//---------- Public properties

	
	

	//---------- Public methods
	public String getUrlForSendingMessage(String loginString)
	{ return getUrlForCommand(loginString , ACTION_SEND_MESSAGE); }
	
	public String getUrlForSendingCaptcha(String loginString)
	{ return getUrlForCommand(loginString, ACTION_SEND_CAPTCHA); }
	
	public String getUrlForDownloadTemplates(String username, String password)
	{ return getUrlForCommand(username, password, ACTION_GET_ALL_TEMPLATES, FORMAT_CSV); }

	public String getUrlForDownloadUserServices(String username, String password)
	{ return getUrlForCommand(username, password, ACTION_GET_USER_SERVICES, FORMAT_CSV); }
	
	public String getUrlForLoginString(String username, String password)
	{ return getUrlForCommand(username, password, ACTION_GET_USER_LOGINSTRING, FORMAT_CSV); }

	public String getUrlForSaveService(String username, String password) {
		return getUrlForCommand(username, password, ACTION_GET_EDITSERVICE, FORMAT_CSV);
	}
	
	public String getUrlForAddService(String username, String password) {
		return getUrlForCommand(username, password, ACTION_GET_ADDSERVICE, FORMAT_CSV);
	}

	public String getUrlForDeleteService(String username, String password) {
		return getUrlForCommand(username, password, ACTION_GET_DELSERVICE, FORMAT_CSV);
	}
	
	public List<NameValuePair> getParamsForAccountOperation(SmsService editedService,
			String firstParam, String secondParam){
		List<NameValuePair> nVp = new ArrayList<NameValuePair>(4);
		nVp.add(new BasicNameValuePair(firstParam, secondParam));
		nVp.add(new BasicNameValuePair("account_name", editedService.getName()));
		nVp.add(new BasicNameValuePair("data_1",editedService.getParameterValue(0)));
		nVp.add(new BasicNameValuePair("data_2",editedService.getParameterValue(1)));
		nVp.add(new BasicNameValuePair("data_3",editedService.getParameterValue(2)));
		nVp.add(new BasicNameValuePair("data_4",editedService.getParameterValue(3)));
		return nVp;
	}
	
	/**
	 * Builds headers used in send sms api
	 * prototipo di richiesta:
	 * J-X: service_id \t recipient \t data1 \t data2 \t data3 \t data4 \t message
	 * 
	 * @param service
	 * @param destination
	 * @param message
	 * @return
	 */
	public HashMap<String, String> getParamsForSendingMessage(
			SmsService service,
			String destination,
			String message)
	{
		String key;
		String value;
		HashMap<String, String> headers = new HashMap<String, String>();
		
		//la key j-x e' necessaria perche' vodafone filtra i messaggi con key x
		key = "J-X";
		value = String.valueOf(service.getTemplateId()) + TAB_SEPARATOR + 
		destination + TAB_SEPARATOR +
		replaceServiceParameter(service.getParameterValue(0)) + TAB_SEPARATOR +
		replaceServiceParameter(service.getParameterValue(1)) + TAB_SEPARATOR +
		replaceServiceParameter(service.getParameterValue(2)) + TAB_SEPARATOR +
		replaceServiceParameter(service.getParameterValue(3)) + TAB_SEPARATOR +
		adjustMessageBody(message);
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
		
		//la key j-x e' necessaria perche' vodafone filtra i messaggi con key x
		key = "J-X";
		value = String.valueOf(sessionId) + TAB_SEPARATOR + 
				captchaCode + TAB_SEPARATOR;
		headers.put(key, value);
		return headers;
	}
	
	/**
	 * Replace illegal chars in the message
	 * @param body
	 * @return
	 */
	public String adjustMessageBody(String body) {
		body = body.replace("\n", " ");
		body = body.replace("\t", " ");
		body = body.replace("\r", " ");
		return body;
	}
	
	
	/**
	 * Extracts the message text from provider's reply
	 * @param reply
	 * @return
	 */
	public String getTextPartFromReply(String reply)
	{
		//TODO marco
		//removed, if null must fail!
		
		//if (TextUtils.isEmpty(reply)) return "";
		
		//TODO marco 
		// in ogni caso devo ritornare
		// la seconda posizione
		String[] split = reply.split(TAB_SEPARATOR);
		return split[1];
		
//		//if strings contains error codes from JackSMS
//		for (String errorPrefix : PREFIX_RESULT_ERROR_ARRAY) {
//			if (reply.startsWith(errorPrefix)) {
//				{
//					
//					
//					// TODO marco original
//					//return reply.substring(errorPrefix.length()).trim();
//				
//				}
//			}
//		}
//		
//		
//		//TODO marco (in questo caso o è un messaggio destinato all'utente, oppure è il captcha)
//		
//		
//		//other reply from JackSMS 
//		String[] lines = reply.split(TAB_SEPARATOR);
//		
//		//TODO marco se non è cosi allora il server ha mandato una risposta sbagliata!
//		// dovrebbe essere stato gestito già prima!
//		if (lines.length > 2)
//			//message is in the second item
//			return lines[1];
//		else
//			return "";
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
			decodedCaptcha = Base64Helper.decode(content);
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
		int separatorPos = reply.indexOf(TAB_SEPARATOR);
		if (separatorPos < 0)
			return "";
		
		String sessionId = reply.substring(0, separatorPos).trim();
		return sessionId;
	}

	
	public List<SmsService> extractTemplates(LogFacility logFacility, String providerReply)
	{
		List<SmsService> templates = new ArrayList<SmsService>();
		
		//examine the reply, line by line
		String[] lines = providerReply.split("\n");
		
		for(String templateLine : lines) {
			//TODO marco, per il momento lascio cosi com'è ma è da testare
			// questo split non dovrebbe funzionare!!
			String[] pieces = templateLine.split(TAB_SEPARATOR);
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
				parametersDesc = (String[]) RainbowArrayHelper.resizeArray(parametersDesc, numberOfParameters);
				SmsService newTemplate = new SmsConfigurableService(
						logFacility, serviceId, serviceName, maxChar, parametersDesc);
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
	    mLogFacility.v(LOG_HASH, "Extract user services");
		List<SmsConfigurableService> services = new ArrayList<SmsConfigurableService>();
		
		//examine the reply, line by line
		String[] lines = providerReply.split(String.valueOf((char) 10));
		
		for(String serviceLine : lines) {
			String[] pieces = serviceLine.split(TAB_SEPARATOR);
			try {
				String serviceId = pieces[0];
				String templateId = pieces[1];
				String serviceName = pieces[2];
				String[] parametersValue = new String[MAX_SERVICE_PARAMETERS];

				int numberOfParameters = MAX_SERVICE_PARAMETERS;
				for(int i = 0; i < MAX_SERVICE_PARAMETERS; i++) {
					if (pieces.length > 3+i)
						parametersValue[i] = new String(Base64Helper.decode(pieces[3+i]));
					else
						parametersValue[i] = "";
					//find the total number of parameter
					if (TextUtils.isEmpty(parametersValue[i])) numberOfParameters--;
				}
				mLogFacility.v(LOG_HASH, "Found new service:" +
				        "\n service id: " + serviceId +
				        "\n yemplate id: " + templateId +
				        "\n service name: " + serviceName +
				        "\n parameters: " + numberOfParameters);
				//create new service
				parametersValue = (String[]) RainbowArrayHelper.resizeArray(parametersValue, numberOfParameters);
				SmsConfigurableService newService = new SmsConfigurableService(
						mLogFacility, serviceId, templateId, serviceName, parametersValue);
				services.add(newService);
			} catch (Exception e) {
				//do nothing, simply skips to next service
			}
		}
		
		mLogFacility.v(LOG_HASH, "Total services found: " + services.size());
		Collections.sort(services);
		return services;
	}


	public boolean isSmsCorrectlySent(String webserviceReply) {
		if (TextUtils.isEmpty(webserviceReply)) return false;
		return webserviceReply.startsWith(JacksmsDictionary.PREFIX_RESULT_OK);
	}

	public boolean isCaptchaRequest(String webserviceReply) {
		if (TextUtils.isEmpty(webserviceReply)) return false;
		
		//find first part of the message
		int pos = webserviceReply.indexOf(TAB_SEPARATOR);
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
	 * Checks if captcha code was correctly received by JackSMS
	 * server and sent to sms provider
	 * @param webserviceReply
	 * @return
	 */
    public boolean isCaptchaCorrectlySent(String webserviceReply) {
        return isSmsCorrectlySent(webserviceReply);
    }

	/**
	 * Checks if the reply for webservice contains errors or not
	 * @param webserviceReply
	 * @return
	 */
	public boolean isErrorReply(String webserviceReply)
	{
		//TODO marco
		// cambiato a false, una risposta vuota non ha errori espliciti,
		// qui si stanno cercando errori comunicati dal server!!!
		
		if (TextUtils.isEmpty(webserviceReply)) return false;
		
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
		
		//TODO marco, siamo sicuri che in questo caso non ci sia il tab???
		
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
	private String getUrlForCommand(String loginString, String command)
	{
		String loginS = loginString;
		
		StringBuilder sb = new StringBuilder();
		//per le richieste di test
		//sb.append(URL_STREAM_TEST)
		//per le richieste normali
		sb.append(URL_STREAM_BASE)
			.append(loginS)
			.append("/")
			.append(command)
			.append(PARAM_CLIENTVERSION_VALUE);
		return sb.toString();
	}
	
	//Per ottenere loginString devo usare per forza username e password
	private String getUrlForCommand(String username, String password,
			String command, String out_format) {
		
		String codedUser;
		String codedPwd;
		
		if (USER_TEST.equals(username)){
			codedUser = USER_TEST;
			codedPwd = USER_TEST;
		}else{
			codedUser = replaceNotAllowedChars(Base64Helper.encodeBytes(username.getBytes()));
			codedPwd = replaceNotAllowedChars(Base64Helper.encodeBytes(password.getBytes()));
		}
		StringBuilder sb = new StringBuilder();
		sb.append(URL_Q_BASE)
			.append("/")	
			.append(codedUser)
			.append("/")
			.append(codedPwd)
			.append("/")
			.append(command)
			.append("?")
			.append(out_format)
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