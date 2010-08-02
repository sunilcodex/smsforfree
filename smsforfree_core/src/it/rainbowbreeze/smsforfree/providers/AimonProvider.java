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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.domain.SmsConfigurableService;
import it.rainbowbreeze.smsforfree.domain.SmsMultiProvider;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.domain.SmsServiceCommand;
import it.rainbowbreeze.smsforfree.domain.SmsServiceParameter;
import it.rainbowbreeze.smsforfree.util.Base64;

/**
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class AimonProvider
	extends SmsMultiProvider
{
	//---------- Ctors
	public AimonProvider(ProviderDao dao)
	{
		super(dao, PARAM_NUMBER);
	}
	
	
	//---------- Private fields
	private final static int PARAM_NUMBER = 3;
	private final static int PARAM_INDEX_USERNAME = 0;
	private final static int PARAM_INDEX_PASSWORD = 1;
	private final static int PARAM_INDEX_SENDER = 2;

	private final static int MSG_INDEX_INVALID_CREDENTIALS = 0;
	private final static int MSG_INDEX_VALID_CREDENTIALS = 1;
	private final static int MSG_INDEX_SERVER_ERROR = 2;
	private final static int MSG_INDEX_REMAINING_CREDITS = 3;
	private final static int MSG_INDEX_MESSAGE_SENT = 4;
	private final static int MSG_INDEX_MISSING_PARAMETERS = 5;
	private final static int MSG_INDEX_SERVICENANE_FREE_NORMAL = 6;
	private final static int MSG_INDEX_SERVICENANE_ANONYMOUS = 7;
	private final static int MSG_INDEX_SERVICENANE_NORMAL = 8;
	private final static int MSG_INDEX_SERVICENANE_REPORT = 9;
	private static final int MSG_INDEX_REMAINING_FREE_CREDITS = 10;
	private static final int MSG_INDEX_SERVICENANE_FREE_ANONYMOUS = 11;
	private static final int MSG_INDEX_INVALID_SENDER = 12;
	private static final int MSG_INDEX_INVALID_DESTINATION = 13;
	private static final int MSG_INDEX_EMPTY_MESSAGE = 14;
	private static final int MSG_INDEX_INVALID_MESSAGE_ENCODING = 15;
	private static final int MSG_INDEX_FREE_SMS_DAILY_LIMIT_REACHED = 16;
	private static final int MSG_INDEX_FREE_SMS_MONTHLY_LIMIT_REACHED = 17;
	private static final int MSG_INDEX_NOT_ENOUGH_FREE_SMS_CREDIT = 18;
	private static final int MSG_INDEX_NOT_ENOUGH_CREDIT = 19;
	private static final int MSG_INDEX_INVALID_MESSAGE_ENCODING_OR_TOO_LONG = 20;
	private static final int MSG_INDEX_UNMANAGED_SERVER_ERROR = 21;
	
	public final static int COMMAND_CHECKCREDENTIALS = 100;
	public final static int COMMAND_CHECKCREDITS = 101;
	
	private String[] mMessages;
	private AimonDictionary mDictionary;
	
	

	//---------- Public fields
	
	
	
	
	//---------- Public properties
	@Override
	public String getId()
	{ return "Aimon"; }

	@Override
	public String getName()
	{ return "Aimon"; }

	@Override
	public int getParametersNumber()
	{ return PARAM_NUMBER; }

	@Override
	public boolean hasSubServicesToConfigure()
	{ return false; }
    

	
	
    //---------- Public methods
	
	@Override
	public ResultOperation<Void> initProvider(Context context)
	{
		mDictionary = new AimonDictionary();
		setParameterDesc(PARAM_INDEX_USERNAME, context.getString(R.string.aimon_username_desc));
		setParameterDesc(PARAM_INDEX_PASSWORD, context.getString(R.string.aimon_password_desc));
		setParameterFormat(PARAM_INDEX_PASSWORD, SmsServiceParameter.FORMAT_PASSWORD);
		setParameterDesc(PARAM_INDEX_SENDER, context.getString(R.string.aimon_sender_desc));
		setDescription(context.getString(R.string.aimon_description));
		
		//save some messages
		mMessages = new String[22];
		mMessages[MSG_INDEX_INVALID_CREDENTIALS] = context.getString(R.string.aimon_msg_invalidCredentials);
		mMessages[MSG_INDEX_VALID_CREDENTIALS] = context.getString(R.string.aimon_msg_validCredentials);
		mMessages[MSG_INDEX_SERVER_ERROR] = context.getString(R.string.aimon_msg_serverError);
		mMessages[MSG_INDEX_REMAINING_CREDITS] = context.getString(R.string.aimon_msg_remainingCredits);
		mMessages[MSG_INDEX_MESSAGE_SENT] = context.getString(R.string.aimon_msg_messageQueued);
		mMessages[MSG_INDEX_MISSING_PARAMETERS] = context.getString(R.string.aimon_msg_missingParameters);
		mMessages[MSG_INDEX_SERVICENANE_FREE_ANONYMOUS] = context.getString(R.string.aimon_serviceNameFreeAnonymous);
		mMessages[MSG_INDEX_SERVICENANE_FREE_NORMAL] = context.getString(R.string.aimon_serviceNameFreeNormal);
		mMessages[MSG_INDEX_SERVICENANE_ANONYMOUS] = context.getString(R.string.aimon_serviceNameAnonymous);
		mMessages[MSG_INDEX_SERVICENANE_NORMAL] = context.getString(R.string.aimon_serviceNameNormal);
		mMessages[MSG_INDEX_SERVICENANE_REPORT] = context.getString(R.string.aimon_serviceNameReport);
		mMessages[MSG_INDEX_REMAINING_FREE_CREDITS] = context.getString(R.string.aimon_msg_remainingFreeCredits);
		mMessages[MSG_INDEX_INVALID_SENDER] = context.getString(R.string.aimon_msg_invalidSender);
		mMessages[MSG_INDEX_INVALID_DESTINATION] = context.getString(R.string.aimon_msg_invalidDestination);
		mMessages[MSG_INDEX_EMPTY_MESSAGE] = context.getString(R.string.aimon_msg_emptyMessage);
		mMessages[MSG_INDEX_INVALID_MESSAGE_ENCODING] = context.getString(R.string.aimon_msg_invalidMessageEncoding);
		mMessages[MSG_INDEX_FREE_SMS_DAILY_LIMIT_REACHED] = context.getString(R.string.aimon_msg_freeSmsDailyLimitReached);
		mMessages[MSG_INDEX_FREE_SMS_MONTHLY_LIMIT_REACHED] = context.getString(R.string.aimon_msg_freeSmsMonthlyLimitReached);
		mMessages[MSG_INDEX_NOT_ENOUGH_FREE_SMS_CREDIT] = context.getString(R.string.aimon_msg_notEnoughFreeSmsCredit);
		mMessages[MSG_INDEX_NOT_ENOUGH_CREDIT] = context.getString(R.string.aimon_msg_notEnoughCredit);
		mMessages[MSG_INDEX_INVALID_MESSAGE_ENCODING_OR_TOO_LONG] = context.getString(R.string.aimon_msg_invalidMessageEncodingOrTooLong);
		mMessages[MSG_INDEX_UNMANAGED_SERVER_ERROR] = context.getString(R.string.aimon_msg_unmanagedServerError);
		
		return super.initProvider(context);
	}
	
    @Override
	public ResultOperation<String> sendMessage(String serviceId, String destination, String body)
	{
    	String username = getParameterValue(PARAM_INDEX_USERNAME);
		String password = getParameterValue(PARAM_INDEX_PASSWORD);
		String sender = getParameterValue(PARAM_INDEX_SENDER);

    	//arguments check
    	if (!checkCredentialsValidity(username, password))
    		return getExceptionForInvalidCredentials();
    	if (TextUtils.isEmpty(sender)) 
    		return new ResultOperation<String>(
					ResultOperation.RETURNCODE_PROVIDER_ERROR, mMessages[MSG_INDEX_INVALID_SENDER]);
    	if (TextUtils.isEmpty(destination)) 
    		return new ResultOperation<String>(
					ResultOperation.RETURNCODE_PROVIDER_ERROR, mMessages[MSG_INDEX_INVALID_DESTINATION]);
    	if (TextUtils.isEmpty(body)) 
    		return new ResultOperation<String>(
					ResultOperation.RETURNCODE_PROVIDER_ERROR, mMessages[MSG_INDEX_EMPTY_MESSAGE]);
    	
    	
    	//parameters correction
    	String okSender = "";
    	String okDestination;
    	String okBody;

    	//send free sms via http form
    	if (isFreeSmsCall(serviceId)) {
    		okSender = removeInternationalPrefix(sender);
    		//sender still starts with an international prefix
    		if (okSender.substring(0, 1).equals("+")) {
				//no way, error is generated
				ResultOperation<String> res = new ResultOperation<String>(
						ResultOperation.RETURNCODE_PROVIDER_ERROR, mMessages[MSG_INDEX_INVALID_SENDER]);
				return res;
			}
        		
    	//normal api send
    	} else {
    	
	    	//sender is a phone number and starts with international prefix
	    	if (sender.substring(0, 1).equals("+")) {
	    		//checks the length
	    		if (sender.length() > AimonDictionary.MAX_SENDER_LENGTH_NUMERIC) {
	    			okSender = sender.substring(0, AimonDictionary.MAX_SENDER_LENGTH_NUMERIC);
	    		} else {
	    			okSender = sender;
	    		}
			//sender is a phone number, add international prefix
	    	} else {
	        	if (TextUtils.isDigitsOnly(sender)) {
	        		//find prefix to use
	        		String prefix = AppPreferencesDao.instance().getDefaultInternationalPrefix();
	        		//append it to number
	        		okSender = prefix + sender;
	        		//and check length
	        		if (okSender.length() > AimonDictionary.MAX_SENDER_LENGTH_NUMERIC) {
	        			okSender = okSender.substring(0, AimonDictionary.MAX_SENDER_LENGTH_NUMERIC);
	        		}
	        	} else {
	        		//sender is a name, checks length and encodes it
	            	if (sender.length() > AimonDictionary.MAX_SENDER_LENGTH_ALPHANUMERIC){
	            		okSender = sender.substring(0, AimonDictionary.MAX_SENDER_LENGTH_ALPHANUMERIC);
	            	} else {
	            		okSender = sender;
	            	}
	        	}
	    	}
    	}

    	if (isFreeSmsCall(serviceId)) {
    		okDestination = removeInternationalPrefix(destination);
    		//sender still starts with an international prefix
    		if (okDestination.substring(0, 1).equals("+")) {
				//no way, error is generated
				ResultOperation<String> res = new ResultOperation<String>(
						ResultOperation.RETURNCODE_PROVIDER_ERROR, mMessages[MSG_INDEX_INVALID_DESTINATION]);
				return res;
			}
    	} else {
    		//add international prefix to the destination
	    	okDestination = transalteInInternationalFormat(destination);
			//removes begin + char
			okDestination = okDestination.substring(1);
    	}

    	okBody = mDictionary.adjustMessageBody(body);
    	
		//find how to send the sms (via httpform or via api)
		ResultOperation<String> res;
		if (isFreeSmsCall(serviceId)) {
	    	//send free sms
			res = sendSmsViaHttpConversation(username, password, serviceId, okSender, okDestination, okBody);
		} else {
	    	//encode sender and body
	    	okSender = Base64.encodeBytes(okSender.getBytes());
			okBody = Base64.encodeBytes(okBody.getBytes());
			//other type of payed sms
			res = sendSmsWithApi(username, password, serviceId, okSender, okDestination, okBody);
		}
    	
		return res;    	
		
	}

    @Override
    public ResultOperation<String> executeCommand(int commandId, Context context, Bundle extraData)
	{
		ResultOperation<String> res;
		String currentUsername = null;
		String currentPassword = null;

		//controls if some parameters must be retrived from extraData
		switch (commandId) {
		case COMMAND_CHECKCREDENTIALS:
		case COMMAND_CHECKCREDITS:
			currentUsername = extraData.getString(String.valueOf(PARAM_INDEX_USERNAME));
			currentPassword = extraData.getString(String.valueOf(PARAM_INDEX_PASSWORD));
		}
		
		//execute commands
		switch (commandId) {
		case COMMAND_CHECKCREDENTIALS:
			res = verifyCredentials(currentUsername, currentPassword);
			break;

		case COMMAND_CHECKCREDITS:
			res = verifyCredit(currentUsername, currentPassword);
			break;

		default:
			res = super.executeCommand(commandId, context, extraData);
		}

		return res;
	}

	
	@Override
	public ResultOperation<Object> getCaptchaContentFromProviderReply(String providerReply)
	{ return null; }

	@Override
	public ResultOperation<String> sendCaptcha(String providerReply, String captchaCode)
	{ return null; }

	@Override
	public ResultOperation<Void> saveSubservices(Context context)
	{ return new ResultOperation<Void>(); }



	

	//---------- Private methods

	@Override
	protected String getParametersFileName()
	{ return GlobalDef.aimonParametersFileName; }

	@Override
	protected String getTemplatesFileName()
	{ return null; }

	@Override
	protected String getSubservicesFileName()
	{ return null; }

	@Override
	protected ResultOperation<Void> loadTemplates(Context context)
	{ return new ResultOperation<Void>(); }

	@Override
	protected ResultOperation<Void> saveTemplates(Context context)
	{ return new ResultOperation<Void>(); }

	@Override
	protected ResultOperation<Void> loadSubservices(Context context)
	{
		//the subservice have as id the id to use with Aimon api for sending message
		SmsConfigurableService service;
		
		//create the list of subservices
		mSubservices = new ArrayList<SmsService>();
		
		service = new SmsConfigurableService(0);
		service.setId(AimonDictionary.ID_API_FREE_ANONYMOUS_SENDER);
		service.setName(mMessages[MSG_INDEX_SERVICENANE_FREE_ANONYMOUS]);
		service.setMaxMessageLenght(AimonDictionary.MAX_BODY_FREE_LENGTH);
		mSubservices.add(service);

		service = new SmsConfigurableService(0);
		service.setId(AimonDictionary.ID_API_FREE_NORMAL);
		service.setName(mMessages[MSG_INDEX_SERVICENANE_FREE_NORMAL]);
		service.setMaxMessageLenght(AimonDictionary.MAX_BODY_FREE_LENGTH);
		mSubservices.add(service);

		service = new SmsConfigurableService(0);
		service.setId(AimonDictionary.ID_API_ANONYMOUS_SENDER);
		service.setName(mMessages[MSG_INDEX_SERVICENANE_ANONYMOUS]);
		service.setMaxMessageLenght(AimonDictionary.MAX_BODY_API_LENGTH);
		mSubservices.add(service);

		service = new SmsConfigurableService(0);
		service.setId(AimonDictionary.ID_API_SELECTED_SENDER_NO_REPORT);
		service.setName(mMessages[MSG_INDEX_SERVICENANE_NORMAL]);
		service.setMaxMessageLenght(AimonDictionary.MAX_BODY_API_LENGTH);
		mSubservices.add(service);

		service = new SmsConfigurableService(0);
		service.setId(AimonDictionary.ID_API_SELECTED_SENDER_REPORT);
		service.setName(mMessages[MSG_INDEX_SERVICENANE_REPORT]);
		service.setMaxMessageLenght(AimonDictionary.MAX_BODY_API_LENGTH);
		mSubservices.add(service);
		
		return new ResultOperation<Void>();
	}
	
	
	@Override
	protected String getProviderRegistrationUrl(Context context) {
		return context.getString(R.string.aimon_registerLink);
	}
	
	@Override
	protected List<SmsServiceCommand> loadSettingsActivityCommands(Context context)
	{
		List<SmsServiceCommand> commands = super.loadSettingsActivityCommands(context);
		
		SmsServiceCommand newCommand;
		//initializes the command list
		newCommand = new SmsServiceCommand(
				COMMAND_CHECKCREDENTIALS, context.getString(R.string.aimon_commandCheckCredentials), 100, R.drawable.ic_menu_login);
		commands.add(newCommand);
		newCommand = new SmsServiceCommand(
				COMMAND_CHECKCREDITS, context.getString(R.string.aimon_commandCheckCredits), 101);
		commands.add(newCommand);
		
		return commands;
	}

	
	/**
	 * Verifies remaining credits for the user
	 * @param username
	 * @param password
	 * @return
	 */
    private ResultOperation<String> verifyCredit(String username, String password)
    {
    	//args check
    	if (!checkCredentialsValidity(username, password))
    		return getExceptionForInvalidCredentials();

    	//call the api that gets the credit
    	String url = AimonDictionary.URL_GET_CREDIT;
		HashMap<String, String> params = mDictionary.getParametersForApiCreditCheck(username, password);
    	ResultOperation<String> res = doSingleHttpRequest(url, null, params);

    	//checks for errors
    	if (parseReplyForApiErrors(res)) {
			//log action data for a better error management
			logRequest(url, null, params);
			return res;
		}
    	
    	//at this point reply can only contains the remaining credits
    	//append the message to credit amount
    	res.setResult(String.format(
    			mMessages[MSG_INDEX_REMAINING_CREDITS], res.getResult()));
		return res;
    }


    /**
     * Verifies if username and password are correct
     * @return an error if the user is not authenticated, otherwise the message to show
     */
	private ResultOperation<String> verifyCredentials(String username, String password)
	{
		ResultOperation<String> res;
		
		//calls the verify credits and, if the user has credits,
		//this means that the user can authenticate and the credentials
		//are correct.
		res = verifyCredit(username, password);
		//checks for application errors
		if (res.hasErrors() || ResultOperation.RETURNCODE_PROVIDER_ERROR == res.getReturnCode()) return res;
		
		//at this point reply can only contains the remaining credits, so credential are correct
		res.setResult(mMessages[MSG_INDEX_VALID_CREDENTIALS]);
		
		return res;
	}


	/**
	 * Send an SMS using Aimon API
	 * 
	 * @param username
	 * @param password
	 * @param idApi
	 * @param okSender
	 * @param okDestination
	 * @param okBody
	 * @param params
	 * @return
	 */
	private ResultOperation<String> sendSmsWithApi(
			String username,
			String password,
			String idApi,
			String sender,
			String destination,
			String body)
	{
		//send the sms
    	String url = AimonDictionary.URL_SEND_SMS;
    	HashMap<String, String> params = mDictionary.getParametersForApiSend(username, password, idApi, sender, destination, body);
		ResultOperation<String> res = doSingleHttpRequest(url, null, params);

    	//checks for errors
    	if (parseReplyForApiErrors(res)) {
			//log action data for a better error management
			logRequest(url, null, params);
			return res;
		}
    	
    	//at this point, the operation was surely executed correctly
		res.setResult(String.format(
				mMessages[MSG_INDEX_MESSAGE_SENT], res.getResult()));
		
    	return res;
	}


    
	/**
	 * Parse the webservice reply searching for know errors code.
	 * If one of them is found, the ResultOperation object is modified
	 * whit the error message to display
	 * 
	 * @param resultToAnalyze
	 * @return true if an aimon error is found, otherwise false
	 */
	private boolean parseReplyForApiErrors(ResultOperation<String> resultToAnalyze)
	{
		if (resultToAnalyze.hasErrors()) return true;

		String res;
		String reply = resultToAnalyze.getResult();

		//no reply from server is already handled in doRequest method

		//message correctly sent
		if (mDictionary.isOperationCorrectlyExecuted(reply)) {
			res = "";
		//check for know errors
		} else if (mDictionary.isLoginInvalidCredentials(reply)) {
			res = mMessages[MSG_INDEX_INVALID_CREDENTIALS];
		} else if (mDictionary.isInternalServerError(reply)) {
			res = mMessages[MSG_INDEX_SERVER_ERROR];
		} else if (mDictionary.isMissingParameters(reply)) {
			res = mMessages[MSG_INDEX_MISSING_PARAMETERS];
		} else if (mDictionary.isInvalidSender(reply)) {
			res = mMessages[MSG_INDEX_INVALID_SENDER];
		} else if (mDictionary.isInvalidDestination(reply)) {
			res = mMessages[MSG_INDEX_INVALID_DESTINATION];
		} else if (mDictionary.isNotEnoughCredit(reply)) {
			res = mMessages[MSG_INDEX_NOT_ENOUGH_CREDIT];
		} else if (mDictionary.isUnsupportedMessageEncodingOrTooLong(reply)) {
			res = mMessages[MSG_INDEX_INVALID_MESSAGE_ENCODING_OR_TOO_LONG];
		} else {
			//other generic errors
			res = String.format(mMessages[MSG_INDEX_UNMANAGED_SERVER_ERROR], reply);
		}
	
    	//errors are internal to Aimon, not related to communication issues.
    	//so no application errors (like network issues) should be returned, but
		//the Aimon error must stops the execution of the calling method
    	if (!TextUtils.isEmpty(res)) {
			LogFacility.e("AimonProvider api error reply");
			LogFacility.e(res);
			LogFacility.e(reply);
    		resultToAnalyze.setResult(res);
    		resultToAnalyze.setReturnCode(ResultOperation.RETURNCODE_PROVIDER_ERROR);
    		return true;
    	} else {
    		return false;
    	}
	}


	
	/**
	 * Send an SMS using Aimon form in the site
	 * 
	 * @param username
	 * @param password
	 * @param idApi 0: fixes sender, 1 : free sender
	 * @param sender
	 * @param destination
	 * @param body
	 * @return
	 */
	private ResultOperation<String> sendSmsViaHttpConversation(
			String username,
			String password,
			String idApi,
			String sender,
			String destination,
			String body)
	{
        ResultOperation<String> res;
		HashMap<String, String> params;
		String url;
		
		startConversation();

		//login to the service
        url = AimonDictionary.URL_SEND_SMS_FREE_1;
        params = mDictionary.getParametersForFreeSmsLogin(username, password);
        res = doConversationHttpRequest(url, null, params);
        
    	//checks for errors
        if (parseRetryForHttpErrors(res, username)) {
			//log action data for a better error management
			logRequest(url, null, params);
			return res;
		}
        
        //check if login is really correct
        if (!mDictionary.isFreeSmsLoginOk(res.getResult(), username)) {
        	//set invalid credentials
        	res.setReturnCode(ResultOperation.RETURNCODE_PROVIDER_ERROR);
        	res.setResult(mMessages[MSG_INDEX_INVALID_CREDENTIALS]);
        	return res;
        }

        //no errors, continue with the second page
		url = AimonDictionary.URL_SEND_SMS_FREE_2;
		params = mDictionary.getParametersForFreeSmsSend(idApi, sender, destination, body);		
        res = doConversationHttpRequest(url, null, params);
        
        //examines results
        parseRetryForHttpErrors(res, username);
        
		//result returned is the result of URL_SEND_SMS_FREE_2 request
    	//examine it the return contains confirmation if the message was sent
    	if (ResultOperation.RETURNCODE_OK == res.getReturnCode()) {
    		
    		//at this point, if i want to retrieve the remaining free credits, i
    		//must call the same page used for sending message and analyze it
    		url = AimonDictionary.URL_SEND_SMS_FREE_1;
            ResultOperation<String> res2 = doConversationHttpRequest(url, null, null);
            String remainingCredits = "--";
            if (!res2.hasErrors()) {
            	//parse reply, no matter of other errors
            	remainingCredits = mDictionary.findRemainingCreditsForFreeSms(res2.getResult());
            }
			res.setResult(String.format(
					mMessages[MSG_INDEX_MESSAGE_SENT], 
					String.format(mMessages[MSG_INDEX_REMAINING_FREE_CREDITS], remainingCredits)));
    		
		} else {
			//log action data for a better error management
			logRequest(url, null, params);
		}
		
        //logout from the site and close the conversation
		url = AimonDictionary.URL_SEND_SMS_FREE_3;
		doConversationHttpRequest(url, null, null);
		endConversation();
		
		return res;
	}


	/**
	 * Analyzes returns of http conversation request for finding errors
	 * @param resultToAnalyze
	 * @return
	 */
	private boolean parseRetryForHttpErrors(ResultOperation<String> resultToAnalyze, String username)
	{
		if (resultToAnalyze.hasErrors()) return true;
		
		String res;
		String reply = resultToAnalyze.getResult();

		//no reply from server is already handled in doRequest method
				
		//message sent
		if (mDictionary.isFreeSmsCorrectlySent(reply) || mDictionary.isFreeSmsLoginOk(reply, username)) {
			res = "";
		//other possible errors
		} else if (mDictionary.isFreeSmsLoginInvalidCredentials(reply)) {
			res = mMessages[MSG_INDEX_INVALID_CREDENTIALS];
		} else if (mDictionary.isFreeSmsInvalidDestination(reply)) {
			res = mMessages[MSG_INDEX_INVALID_DESTINATION];
		} else if (mDictionary.isFreeSmsInvalidSender(reply)) {
			res = mMessages[MSG_INDEX_INVALID_SENDER];
		} else if (mDictionary.isFreeSmsDailyLimitReached(reply)) {
			res = mMessages[MSG_INDEX_FREE_SMS_DAILY_LIMIT_REACHED];
		} else if (mDictionary.isFreeSmsEmptyBody(reply)) {
			res = mMessages[MSG_INDEX_EMPTY_MESSAGE];
		} else if (mDictionary.isFreeSmsGenericServerError(reply)) {
			res = mMessages[MSG_INDEX_SERVER_ERROR];
		} else if (mDictionary.isFreeSmsMonthlyLimitReached(reply)) {
			res = mMessages[MSG_INDEX_FREE_SMS_MONTHLY_LIMIT_REACHED];
		} else if (mDictionary.isFreeSmsNotEnoughCredit(reply)) {
			res = mMessages[MSG_INDEX_NOT_ENOUGH_FREE_SMS_CREDIT];
		} else if (mDictionary.isFreeSmsUnsupportedMessageEncoding(reply)) {
			res = mMessages[MSG_INDEX_INVALID_MESSAGE_ENCODING];
		} else {
			//other generic errors
			res = String.format(mMessages[MSG_INDEX_UNMANAGED_SERVER_ERROR], reply);
		}
		
    	//errors are internal to Aimon, not related to communication issues.
    	//so no application errors (like network issues) should be returned, but
		//the Aimon error must stops the execution of the calling method
    	if (!TextUtils.isEmpty(res)) {
			LogFacility.e("AimonProvider http error reply");
			LogFacility.e(res);
			LogFacility.e(reply);
    		resultToAnalyze.setResult(res);
    		resultToAnalyze.setReturnCode(ResultOperation.RETURNCODE_PROVIDER_ERROR);
    		return true;
    	} else {
    		return false;
    	}
	}
	
	
	private boolean isFreeSmsCall(String serviceId) {
		return AimonDictionary.ID_API_FREE_ANONYMOUS_SENDER == serviceId || AimonDictionary.ID_API_FREE_NORMAL == serviceId;
	}

}
