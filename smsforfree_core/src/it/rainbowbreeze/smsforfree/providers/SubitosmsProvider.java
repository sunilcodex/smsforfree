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

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.App;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.domain.SmsServiceCommand;
import it.rainbowbreeze.smsforfree.domain.SmsServiceParameter;
import it.rainbowbreeze.smsforfree.domain.SmsSingleProvider;
import it.rainbowbreeze.smsforfree.ui.ActivityHelper;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class SubitosmsProvider
	extends SmsSingleProvider
{

	//---------- Ctors
	public SubitosmsProvider(
			LogFacility logFacility,
			AppPreferencesDao appPreferencesDao,
			ProviderDao providerDao,
			ActivityHelper activityHelper)
	{
		super(logFacility, PARAM_NUMBER, appPreferencesDao, providerDao, activityHelper);
	}

	
	

	//---------- Private fields
	private final static int PARAM_NUMBER = 3;

	private final static int PARAM_INDEX_USERNAME = 0;
	private final static int PARAM_INDEX_PASSWORD = 1;
	private final static int PARAM_INDEX_SENDER = 2;

	public static final int COMMAND_CHECKCREDENTIALS = 1000;
	public static final int COMMAND_CHECKCREDITS = 1001;

	private static final int MSG_INDEX_REMAINING_CREDITS = 0;
	private static final int MSG_INDEX_VALID_CREDENTIALS = 1;
	private static final int MSG_INDEX_INVALID_CREDENTIALS = 2;
	private static final int MSG_INDEX_EMPTY_MESSAGE = 3;
	private static final int MSG_INDEX_INVALID_DESTINATION = 4;
	private static final int MSG_INDEX_INVALID_SENDER = 5;
	private static final int MSG_INDEX_MESSAGE_SENT = 6;
	private static final int MSG_INDEX_NOT_ENOUGH_CREDIT = 7;
	private static final int MSG_INDEX_SERVER_ERROR = 8;


	private String[] mMessages;
	private SubitosmsDictionary mDictionary;
	

	//---------- Public properties
	@Override
	public String getId()
	{ return "SubitoSMS"; }

	@Override
	public String getName()
	{ return "SubitoSMS"; }

	@Override
	public int getParametersNumber()
	{ return PARAM_NUMBER; }
	
	@Override
	public int getMaxMessageLenght()
	{ return 160; }
    
	
	
	
	//---------- Public methods
	@Override
	public ResultOperation<Void> initProvider(Context context)
	{
		mDictionary = new SubitosmsDictionary();

		setParameterDesc(PARAM_INDEX_USERNAME, context.getString(R.string.subitosms_username_desc));
		setParameterDesc(PARAM_INDEX_PASSWORD, context.getString(R.string.subitosms_password_desc));
		setParameterFormat(PARAM_INDEX_PASSWORD, SmsServiceParameter.FORMAT_PASSWORD);
		setParameterDesc(PARAM_INDEX_SENDER, context.getString(R.string.subitosms_sender_desc));
		setDescription(context.getString(R.string.subitosms_description));
		
		mMessages = new String[9];
		mMessages[MSG_INDEX_REMAINING_CREDITS] = context.getString(R.string.subitosms_msg_remainingCredits);
		mMessages[MSG_INDEX_VALID_CREDENTIALS] = context.getString(R.string.subitosms_msg_validCredentials);
		mMessages[MSG_INDEX_INVALID_CREDENTIALS] = context.getString(R.string.subitosms_msg_invalidCredentials);
		mMessages[MSG_INDEX_EMPTY_MESSAGE] = context.getString(R.string.subitosms_msg_emptyMessage);
		mMessages[MSG_INDEX_INVALID_DESTINATION] = context.getString(R.string.subitosms_msg_invalidDestination);
		mMessages[MSG_INDEX_INVALID_SENDER] = context.getString(R.string.subitosms_msg_invalidSender);
		mMessages[MSG_INDEX_MESSAGE_SENT] = context.getString(R.string.subitosms_msg_messageQueued);
		mMessages[MSG_INDEX_NOT_ENOUGH_CREDIT] = context.getString(R.string.subitosms_msg_notEnoughCredit);
		mMessages[MSG_INDEX_SERVER_ERROR] = context.getString(R.string.subitosms_msg_serverError);

		return super.initProvider(context);
	}

	
	@Override
	public ResultOperation<String> sendMessage(
			String serviceId,
			String destination,
			String body)
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

		//send the sms
    	String url = mDictionary.getBaseUrl();
    	HashMap<String, String> params = mDictionary.getParametersForApiSend(username, password, sender, destination, body);
		ResultOperation<String> res = doSingleHttpRequest(url, null, params);

    	//checks for errors
		if (parseReplyForErrors(res)){
			//log action data for a better error management
			logRequest(url, null, params);
			return res;
		}

    	//at this point, the operation was surely executed correctly
		res.setResult(String.format(
				mMessages[MSG_INDEX_MESSAGE_SENT], res.getResult()));
		
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
	
	
	
	
	//---------- Private methods
	@Override
	protected String getParametersFileName()
	{ return App.subitosmsParametersFileName; }
	
	@Override
	protected String getProviderRegistrationUrl(Context context) {
		return context.getString(R.string.aimon_registerLink);
	}
	
	@Override
	protected List<SmsServiceCommand> loadSettingsActivityCommands(Context context)
	{
		List<SmsServiceCommand> commands = super.loadSettingsActivityCommands(context);

		SmsServiceCommand newCommand;
		newCommand = new SmsServiceCommand(
				COMMAND_CHECKCREDENTIALS, context.getString(R.string.aimon_commandCheckCredentials), 1000, R.drawable.ic_menu_login);
		commands.add(newCommand);
		newCommand = new SmsServiceCommand(
				COMMAND_CHECKCREDITS, context.getString(R.string.aimon_commandCheckCredits), 1001);
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
		String url = mDictionary.getBaseUrl();
		HashMap<String, String> params = mDictionary.getParametersForCreditCheck(username, password);
    	ResultOperation<String> res = doSingleHttpRequest(url, null, params);
    	//checks for errors
		if (parseReplyForErrors(res)){
			mLogFacility.e("Error in command verifyCredit");
			//log action data for a better error management
			logRequest(url, null, params);
			return res;
		}
    	
    	//at this point reply can only contains the remaining credits
    	String credit = mDictionary.findRemainingCredit(res.getResult());
    	//append the credit amount to the message
    	res.setResult(String.format(
    			mMessages[MSG_INDEX_REMAINING_CREDITS], credit));
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
    

	private boolean parseReplyForErrors(ResultOperation<String> resultToAnalyze) {
    	//checks for application errors
    	if (resultToAnalyze.hasErrors()) return true;
		
		String reply = resultToAnalyze.getResult();
		String res;

		//no reply from server is already handled in doRequest method

		//check for know errors
		if (mDictionary.isValidReplyForCreditRequest(reply) || 
				mDictionary.isValidReplyForSmsQueued(reply)) {
			res = "";
		} else if (mDictionary.isLoginInvalidCredentials(reply)) {
			res = mMessages[MSG_INDEX_INVALID_CREDENTIALS];
		} else if (mDictionary.isNotEnoughCredit(reply)) {
			res = mMessages[MSG_INDEX_NOT_ENOUGH_CREDIT];
		} else {
			//at this point, unknown errors
			res = String.format(mMessages[MSG_INDEX_SERVER_ERROR], reply);
		}
	
    	//errors are internal to provider, not related to communication issues.
    	//so no application errors (like network issues) should be returned, but
		//the provider error must stops the execution of the calling method
    	if (!TextUtils.isEmpty(res)) {
			mLogFacility.e("SubitosmsProvider error reply");
			mLogFacility.e(res);
			mLogFacility.e(reply);
    		resultToAnalyze.setResult(res);
    		resultToAnalyze.setReturnCode(ResultOperation.RETURNCODE_PROVIDER_ERROR);
    		return true;
    	} else {
    		return false;
    	}
	}


	

}
