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

import android.content.Context;
import android.text.TextUtils;
import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.AppEnv;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.domain.SmsServiceParameter;
import it.rainbowbreeze.smsforfree.domain.SmsSingleProvider;
import it.rainbowbreeze.smsforfree.ui.ActivityHelper;

public class VoipstuntProvider
	extends SmsSingleProvider
{
	//---------- Private fields
	private final static String LOG_HASH = "VoipstuntProvider";

	private final static int PARAM_NUMBER = 3;
	private final static int PARAM_INDEX_USERNAME = 0;
	private final static int PARAM_INDEX_PASSWORD = 1;
	private final static int PARAM_INDEX_SENDER = 2;
	
	private final static int MSG_INDEX_MESSAGE_SENT = 0;
	private final static int MSG_INDEX_MESSAGE_NO_SENT = 1;
    private final static int MSG_INDEX_ERROR_PARSING_PROVIDER_REPLY = 2;
    private final static int MSG_INDEX_MESSAGE_TOO_LONG = 3;
	
	private String[] mMessages;
	private VoipstuntDictionary mDictionary;
	


	
	//---------- Constructors
	public VoipstuntProvider(
			LogFacility logFacility,
			AppPreferencesDao appPreferencesDao,
			ProviderDao providerDao,
			ActivityHelper activityHelper)
	{
		super(logFacility, PARAM_NUMBER, appPreferencesDao, providerDao, activityHelper);
	}




	//---------- Public properties
	@Override
	public String getId()
	{ return "Voipstunt"; }

	@Override
	public String getName()
	{ return "Voipstunt"; }

	@Override
	public int getMaxMessageLenght()
	{ return 160; }

	@Override
	public int getParametersNumber()
	{ return PARAM_NUMBER; }



	
	//---------- Public methods
	
	@Override
	public ResultOperation<Void> initProvider(Context context)
	{
		mDictionary = new VoipstuntDictionary(mLogFacility);
		
		setParameterDesc(PARAM_INDEX_USERNAME, context.getString(R.string.voipstunt_username_desc));
		setParameterDesc(PARAM_INDEX_PASSWORD, context.getString(R.string.voipstunt_password_desc));
		setParameterFormat(PARAM_INDEX_PASSWORD, SmsServiceParameter.FORMAT_PASSWORD);
		setParameterDesc(PARAM_INDEX_SENDER, context.getString(R.string.voipstunt_sender_desc));
		setDescription(context.getString(R.string.voipstunt_description));

		//save some messages
		mMessages = new String[4];
		mMessages[MSG_INDEX_MESSAGE_SENT] = context.getString(R.string.voipstunt_msg_messageSent);
		mMessages[MSG_INDEX_MESSAGE_NO_SENT] = context.getString(R.string.voipstunt_msg_messageNotSent);
        mMessages[MSG_INDEX_ERROR_PARSING_PROVIDER_REPLY] = context.getString(R.string.voipstunt_msg_errorParsingProviderReply);
        mMessages[MSG_INDEX_MESSAGE_TOO_LONG] = context.getString(R.string.voipstunt_msg_messageTooLong);

		return super.initProvider(context);
	}
	
	@Override
	public ResultOperation<String> sendMessage(String serviceId, String destination, String messageBody)
	{
        super.sendMessage(serviceId, destination, messageBody);
		/*
		 * https://www.voipstunt.com/myaccount/sendsms.php?username=xxxxxxxxxx&password=xxxxxxxxxx&from=xxxxxxxxxx&to=xxxxxxxxxx&text=xxxxxxxxxx
		 * 
		 * Explanation of the variables:
		 * - username: your VoipStunt username
		 * - password: your VoipStunt password
		 * - from: your username or your verified phone number. Always use international format for the number starting with +, for instance +491701234567
		 * - to: the number you wish to send the sms to. Always use international format starting with +, for instance +491701234567
		 * - text: the message you want to send 
		 */
		//build url
		
		//check for destination number and, eventually, add international prefix
    	String okDestination = transalteInInternationalFormat(destination);
		
		String url = mDictionary.getUrlForMessage(
				getParameterValue(PARAM_INDEX_USERNAME),
				getParameterValue(PARAM_INDEX_PASSWORD),
				getParameterValue(PARAM_INDEX_SENDER),
				okDestination,
				messageBody);
			
		ResultOperation<String> res = doSingleHttpRequest(url, null, null);

    	//checks for errors
		if (parseReplyForErrors(res)){
			//log action data for a better error management
			logRequest(url, null, null);
			return res;
		}

    	//at this point, the operation was surely executed correctly
		res.setResult(mMessages[MSG_INDEX_MESSAGE_SENT]);
		
		return res;    	
	}

	@Override
	public ResultOperation<String> sendCaptcha(String providerReply, String captchaCode)
	{ return null; }
	



	//---------- Private methods

	@Override
	public ResultOperation<Object> getCaptchaContentFromProviderReply(String providerReply)
	{ return null; }

	@Override
	protected String getParametersFileName()
	{ return AppEnv.voipstuntParametersFileName; }
	
	@Override
	protected String getProviderRegistrationUrl(Context context) {
		return context.getString(R.string.voipstunt_registerLink);
	}

	
	private boolean parseReplyForErrors(ResultOperation<String> resultToAnalyze) {
    	//checks for application errors
    	if (resultToAnalyze.hasErrors()) return true;
		
        //no reply from server is already handled in doRequest method
		String reply = resultToAnalyze.getResult();
        VoipstuntDictionary.ProviderReply providerReply = mDictionary.deserializeProviderReply(reply);
		
        //empty or not valid reply message
        if (null == providerReply) {
            setSmsProviderException(resultToAnalyze, mMessages[MSG_INDEX_ERROR_PARSING_PROVIDER_REPLY]);
            return true;
        }

        String errorMessage;
		if (mDictionary.isMessageTooLong(providerReply))
		    errorMessage = mMessages[MSG_INDEX_MESSAGE_TOO_LONG];
		if (mDictionary.messageWasSent(providerReply)) {
			errorMessage = "";
		} else {
			//at this point, unknown errors
			errorMessage = mMessages[MSG_INDEX_MESSAGE_NO_SENT];
		}
	
    	//errors are internal to provider, not related to communication issues.
    	//so no application errors (like network issues) should be returned, but
		//the provider error must stops the execution of the calling method
    	if (!TextUtils.isEmpty(errorMessage)) {
			mLogFacility.e(LOG_HASH, "VoipstuntProvider error reply");
			mLogFacility.e(LOG_HASH, " Message: " + errorMessage);
			mLogFacility.e(LOG_HASH, " Server reply: " + reply);
			setSmsProviderException(resultToAnalyze, errorMessage);
    		return true;
    	} else {
    		return false;
    	}
	}
	

}
