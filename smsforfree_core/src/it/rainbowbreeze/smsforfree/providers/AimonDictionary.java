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

import it.rainbowbreeze.libs.helper.RainbowStringHelper;

import java.util.HashMap;

import android.text.TextUtils;


public class AimonDictionary
{
	//---------- Private fields
	private static final String RESULT_FREE_ERROR_INVALID_CREDENTIALS = "Nome utente e/o password non riconosciute";
	private static final String RESULT_FREE_ERROR_NOT_ENOUGH_CREDIT = "Credito non sufficiente per spedire altri messaggi";
	private static final String RESULT_FREE_ERROR_INVALID_SENDER = "Mittente non valido";
	private static final String RESULT_FREE_ERROR_INVALID_DESTINATION = "Il destinatario deve essere un numero di telefono" ;
	private static final String RESULT_FREE_ERROR_EMPTY_DESTINATION = "Destinatario richiesto";
	private static final String RESULT_FREE_ERROR_EMPTY_BODY = "Testo richiesto";
	private static final String RESULT_FREE_ERROR_UNSUPPORTED_ENCODING = "Carattere GSM non supportato";
	private static final String RESULT_FREE_ERROR_GENERIC_SERVER_ERROR = "Messaggio non inviato per errore di spedizione";
	private static final String RESULT_FREE_ERROR_DAILY_LIMIT_REACHED = "limite massimo di sms inviabili gratis in 24 ore";
	private static final String RESULT_FREE_ERROR_MONTHLY_LIMIT_REACHED = "limite massimo di sms inviabili gratis in 30 giorni";
	private static final String RESULT_FREE_WELCOME_MESSAGE_FOR_THE_USER = "Ciao <b>%s</b>!";
	private static final String RESULT_FREE_SENT_OK = "Messaggio inviato con successo";

	private static final String SEARCH_CREDITI_SMS_START = "Credito residuo giornaliero: ";
	private static final String SEARCH_CREDITI_SMS_END = "crediti/sms";

	private static final String RESULT_SENDSMS_OK = "+01 SMS Queued";
	private static final String RESULT_ERRORMSG_ACCESS_DENIED = "-3-";
	private static final String RESULT_ERRORMSG_MISSING_PARAMETERS = "-5-";
	private static final String RESULT_ERRORMSG_INTERNAL_SERVER_ERROR = "-32-";
	private static final String RESULT_ERRORMSG_INVALID_DESTINATION = "-100-";
	private static final String RESULT_ERRORMSG_INVALID_DESTINATION2 = "-13-";
	private static final String RESULT_ERRORMSG_DESTINATION_NOT_ALLOWED = "-101-";
	private static final String RESULT_ERRORMSG_BODY_HAS_INVALID_CHARS_OR_TOO_LONG = "-102-";
	private static final String RESULT_ERRORMSG_NOT_ENOUGH_CREDIT = "-103-";
	private static final String RESULT_ERRORMSG_INVALID_SENDER = "-105-";
	
	private static final String FIELD_FREE_INPUT_USERNAME = "inputUsername";
	private static final String FIELD_FREE_INPUT_PASSWORD = "inputPassword";
	private static final String FIELD_FREE_SUBMIT_BUTTON = "submit";
	private static final String FIELD_FREE_DESTINATION = "destinatario";
	private static final String FIELD_FREE_MESSAGE_LENGTH = "caratteri";
	private static final String FIELD_FREE_MESSAGE = "testo";
	private static final String FIELD_FREE_SENDER = "mittente";
	private static final String FIELD_FREE_INTERNATIONAL_PREFIX = "prefisso_internazionale";
	private static final String FIELD_FREE_SENDER_TYPE = "tipomittente";
	private static final String FIELD_FREE_SMS_TYPE = "tiposms";
	private static final String FIELD_FREE_SUBMIT_BUTTON2 = "btnSubmit";
	
	private static final String PARAM_USERNAME = "authlogin";
	private static final String PARAM_PASSWORD = "authpasswd";
	private static final String PARAM_SENDER = "sender";
	private static final String PARAM_DESTINATION = "destination";
	private static final String PARAM_BODY = "body";
	private static final String PARAM_ID_API = "id_api";

	

    //---------- Constructors

    
    
    
	//---------- Public fields
	public final static String URL_BASE = "https://secure.apisms.it/";
	public final static String URL_GET_CREDIT = URL_BASE + "http/get_credit";
	public final static String URL_SEND_SMS = URL_BASE + "http/send_sms";
	public final static String URL_SEND_SMS_FREE_1 = "http://aimon.it/?cmd=smsgratis";
	public static final String URL_SEND_SMS_FREE_2 = "http://aimon.it/index.php?cmd=smsgratis&sez=smsgratis";
	public static final String URL_SEND_SMS_FREE_3 = "http://aimon.it/index.php?cmd=smsgratis&sez=smsgratis&azione=logout";

	public final static int MAX_SENDER_LENGTH_ALPHANUMERIC = 11;
	public final static int MAX_SENDER_LENGTH_NUMERIC = 21;
	public final static int MAX_BODY_API_LENGTH = 612;
	public final static int MAX_BODY_FREE_LENGTH = 124;
	
	public static final String ID_API_FREE_ANONYMOUS_SENDER = "0";
	public static final String ID_API_FREE_NORMAL = "1";
	public static final String ID_API_ANONYMOUS_SENDER = "106";
	public static final String ID_API_SELECTED_SENDER_NO_REPORT = "59";
	public static final String ID_API_SELECTED_SENDER_REPORT = "84";

	
	
	
	//---------- Public properties



	
	//---------- Public methods
	/**
	 * Checks if the login for free sms is valid
	 */
	public boolean isFreeSmsLoginOk(String message, String username) {
		return message.contains(
				String.format(RESULT_FREE_WELCOME_MESSAGE_FOR_THE_USER, username));
	}

	/**
	 * Checks if the login for free sms returns an invalid credential page
	 */
	public boolean isFreeSmsLoginInvalidCredentials(String message) {
		return message.contains(RESULT_FREE_ERROR_INVALID_CREDENTIALS);
	}
	
	/**
	 * Checks if the send for free sms correctly sent the sms
	 */
	public boolean isFreeSmsCorrectlySent(String message)
	{ return message.contains(RESULT_FREE_SENT_OK); }
	
	public boolean isFreeSmsNotEnoughCredit(String message)
	{ return message.contains(RESULT_FREE_ERROR_NOT_ENOUGH_CREDIT); }
	
	public boolean isFreeSmsInvalidSender(String message)
	{ return message.contains(RESULT_FREE_ERROR_INVALID_SENDER); }
	
	public boolean isFreeSmsInvalidDestination(String message)
	{ 
		return message.contains(RESULT_FREE_ERROR_INVALID_DESTINATION) || 
			message.contains(RESULT_FREE_ERROR_EMPTY_DESTINATION);
	}
	
	public boolean isFreeSmsEmptyBody(String message)
	{ return message.contains(RESULT_FREE_ERROR_EMPTY_BODY); }
	
	public boolean isFreeSmsUnsupportedMessageEncoding(String message)
	{ return message.contains(RESULT_FREE_ERROR_UNSUPPORTED_ENCODING); }
	
	public boolean isFreeSmsGenericServerError(String message)
	{ return message.contains(RESULT_FREE_ERROR_GENERIC_SERVER_ERROR); }
	
	public boolean isFreeSmsDailyLimitReached(String message)
	{ return message.contains(RESULT_FREE_ERROR_DAILY_LIMIT_REACHED); }
	
	public boolean isFreeSmsMonthlyLimitReached(String message)
	{ return message.contains(RESULT_FREE_ERROR_MONTHLY_LIMIT_REACHED); }
	
	
	public boolean isLoginInvalidCredentials(String webserviceReply)
	{ return webserviceReply.startsWith(RESULT_ERRORMSG_ACCESS_DENIED); }

	public boolean isInternalServerError(String webserviceReply)
	{ return webserviceReply.startsWith(RESULT_ERRORMSG_INTERNAL_SERVER_ERROR); }
	
	public boolean isMissingParameters(String webserviceReply)
	{ return webserviceReply.startsWith(RESULT_ERRORMSG_MISSING_PARAMETERS); }

	public boolean isInvalidDestination(String webserviceReply)
	{
		return webserviceReply.startsWith(AimonDictionary.RESULT_ERRORMSG_INVALID_DESTINATION) ||
			webserviceReply.startsWith(RESULT_ERRORMSG_INVALID_DESTINATION2) ||
			webserviceReply.startsWith(RESULT_ERRORMSG_DESTINATION_NOT_ALLOWED);
	}

	public boolean isNotEnoughCredit(String webserviceReply)
	{ return webserviceReply.startsWith(RESULT_ERRORMSG_NOT_ENOUGH_CREDIT); }
	
	public boolean isInvalidSender(String webserviceReply)
	{ return webserviceReply.startsWith(RESULT_ERRORMSG_INVALID_SENDER); }

	public boolean isUnsupportedMessageEncodingOrTooLong(String webserviceReply)
	{ return webserviceReply.startsWith(RESULT_ERRORMSG_BODY_HAS_INVALID_CHARS_OR_TOO_LONG); }

	/**
	 * Checks if Aimon webservice return message indicates that
	 * the request operation was correctly executed
	 * 
	 * @param webserviceReply
	 * @return
	 */
	public boolean isOperationCorrectlyExecuted(String webserviceReply)
	{
		if (null == webserviceReply ) return false;
		if (webserviceReply.startsWith(RESULT_SENDSMS_OK)) return true;
		
		//generally, a wrong operation result starts with the - char
		return !webserviceReply.startsWith("-");
	}
	
	/**
	 * Extract from the output of the html pages for sending free sms the remaining credits
	 * What i need is in the string
	 *   Credito residuo giornaliero: 3 crediti/sms
	 *   
	 * @param message
	 * @return
	 */
	public String findRemainingCreditsForFreeSms(String message)
	{
		String result = RainbowStringHelper.getStringBetween(message, SEARCH_CREDITI_SMS_START, SEARCH_CREDITI_SMS_END).trim();
		return TextUtils.isEmpty(result) ? "--" : result;
		
	}

	
	/**
     * Prepares parameters for the call for checking user credit
     * @param data
     * @param username
     * @param password
     */
	public HashMap<String, String> getParametersForApiCreditCheck(String username, String password)
    {
		HashMap<String, String> params = new HashMap<String, String>();
		getParametersForApiLogin(username, password, params);
		return params;
    }

	/**
	 * Prepares parameters for the call for sending sms via api
	 * 
	 * @param username
	 * @param password
	 * @param idApi
	 * @param sender
	 * @param destination
	 * @param body
	 * @return
	 */
	public HashMap<String, String> getParametersForApiSend(
			String username,
			String password,
			String idApi,
			String sender,
			String destination,
			String body)
	{
		HashMap<String, String> params = new HashMap<String, String>();
		getParametersForApiLogin(username, password, params);
		params.put(PARAM_SENDER, sender);
		params.put(PARAM_DESTINATION, destination);
		params.put(PARAM_BODY, body);
		params.put(PARAM_ID_API, idApi);
		return params;
	}
	
	
	/**
	 * Prepares parameters to use when login to free sms site
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public HashMap<String, String> getParametersForFreeSmsLogin(
			String username,
			String password)
	{
		HashMap<String, String> params = new HashMap<String, String>();

		params.put(FIELD_FREE_INPUT_USERNAME, username);
        params.put(FIELD_FREE_INPUT_PASSWORD, password);
        params.put(FIELD_FREE_SUBMIT_BUTTON, "procedi");
		return params;
	}
	

	/**
	 * Prepares parameter to use when sending a free sms
	 * 
	 * @param idApi
	 * @param sender
	 * @param destination
	 * @param body
	 * @param params
	 */
	public  HashMap<String, String> getParametersForFreeSmsSend(
			String idApi,
			String sender,
			String destination,
			String body)
	{
		HashMap<String, String> params = new HashMap<String, String>();

		params.put(FIELD_FREE_SMS_TYPE, idApi);  //1 credit sms, fixed sender
		params.put(FIELD_FREE_SENDER_TYPE , "1"); //1 numeric 2 alphanumeric
		params.put(FIELD_FREE_INTERNATIONAL_PREFIX, "39 (Italy)");
		params.put(FIELD_FREE_SENDER, sender);
		params.put(FIELD_FREE_MESSAGE, body);
		params.put(FIELD_FREE_MESSAGE_LENGTH, String.valueOf(body.length()));
		params.put(FIELD_FREE_DESTINATION, destination);
		params.put(FIELD_FREE_SUBMIT_BUTTON2, "Invia SMS");
		
		return params;
	}

	public String adjustMessageBody(String message) {
		String okMessage;
		
		//checks body length
    	if (message.length() > MAX_BODY_API_LENGTH) {
    		okMessage = message.substring(0, AimonDictionary.MAX_BODY_API_LENGTH);
    	} else {
    		okMessage = message;
    	}

    	okMessage = okMessage.replace("\n", " ").replace("\t", " ").replace("\r", " ");
    	
    	return okMessage;
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
