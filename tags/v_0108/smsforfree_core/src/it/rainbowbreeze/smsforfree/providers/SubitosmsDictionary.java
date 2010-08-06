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

import it.rainbowbreeze.smsforfree.util.ParserUtils;

import java.util.HashMap;

import android.text.TextUtils;

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
	private static final String PARAM_MESSAGE = "testo";
	private static final String PARAM_DESTINATION = "dest";
	private static final String PARAM_SENDER = "mitt";

	private static final String SEARCH_CREDITI_SMS_START = "credito:";
	private static final String SEARCH_CREDITI_SMS_END = "";

	private static final String RESULT_ERRORMSG_ACCESS_DENIED = "non autorizzato";
	private static final String RESULT_ERRORMSG_NOT_ENOUGH_CREDIT = "credito insufficiente";
	private static final String RESULT_MSG_SMSQUEUED = "id:";




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
	
	/**
	 * Prepares parameters for the call for sending sms
	 * @param username
	 * @param password
	 * @param sender
	 * @param destination
	 * @param body
	 * @return
	 */
	public HashMap<String, String> getParametersForApiSend(
			String username,
			String password,
			String sender,
			String destination,
			String message)
	{
		HashMap<String, String> params =  new HashMap<String, String>();
		getParametersForApiLogin(username, password, params);
		params.put(PARAM_SENDER, sender);
		params.put(PARAM_DESTINATION, destination);
		params.put(PARAM_MESSAGE, adjustMessageBody(message));
		return params;
	}

	
	/**
	 * Extract from the output of the html pages for sending free sms the remaining credits
	 * What i need is in the string
	 *   Credito residuo giornaliero: 3 crediti/sms
	 *   
	 * @param message
	 * @return
	 */
	public String findRemainingCredit(String message)
	{
		return ParserUtils.getStringBetween(message, SEARCH_CREDITI_SMS_START, SEARCH_CREDITI_SMS_END);
	}
	
	public boolean isLoginInvalidCredentials(String webserviceReply)
	{ return webserviceReply.startsWith(RESULT_ERRORMSG_ACCESS_DENIED); }

	public boolean isNotEnoughCredit(String webserviceReply)
	{ return webserviceReply.startsWith(RESULT_ERRORMSG_NOT_ENOUGH_CREDIT); }	
	
	public boolean isValidReplyForSmsQueued(String webserviceReply)
	{ return webserviceReply.startsWith(RESULT_MSG_SMSQUEUED); }	
	
	public boolean isValidReplyForCreditRequest(String webserviceReply)
	{
		if (TextUtils.isEmpty(webserviceReply)) return false;
		
		//if the reply starts with "credito:", is a correct reply
		if (webserviceReply.startsWith(SEARCH_CREDITI_SMS_START)) return true;
		
		//if the result could be parsed to an int, the reply is valid
		try {
			Integer.parseInt(webserviceReply);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
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
}
