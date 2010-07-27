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
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.domain.SmsServiceCommand;
import it.rainbowbreeze.smsforfree.domain.SmsServiceParameter;
import it.rainbowbreeze.smsforfree.domain.SmsSingleProvider;

public class VoipstuntProvider
	extends SmsSingleProvider
{
	//---------- Ctors
	public VoipstuntProvider(ProviderDao dao)
	{
		super(dao, PARAM_NUMBER);
	}




	//---------- Private fields
	private final static int PARAM_NUMBER = 3;
	private final static int PARAM_INDEX_USERNAME = 0;
	private final static int PARAM_INDEX_PASSWORD = 1;
	private final static int PARAM_INDEX_SENDER = 2;
	
	private final static int MSG_INDEX_MESSAGE_SENT = 0;
	private final static int MSG_INDEX_MESSAGE_NO_SENT = 1;
	
	private final static int COMMAND_REGISTER = 1000;
	
	private String[] mMessages;
	private VoipstuntDictionary mDictionary;
	

	
	
	
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

	private List<SmsServiceCommand> mProviderSettingsActivityCommands;
	@Override
	public List<SmsServiceCommand> getSettingsActivityCommands()
	{ return mProviderSettingsActivityCommands; }



	
	//---------- Public methods
	
	@Override
	public ResultOperation<Void> initProvider(Context context)
	{
		mDictionary = new VoipstuntDictionary();
		
		setParameterDesc(PARAM_INDEX_USERNAME, context.getString(R.string.voipstunt_username_desc));
		setParameterDesc(PARAM_INDEX_PASSWORD, context.getString(R.string.voipstunt_password_desc));
		setParameterFormat(PARAM_INDEX_PASSWORD, SmsServiceParameter.FORMAT_PASSWORD);
		setParameterDesc(PARAM_INDEX_SENDER, context.getString(R.string.voipstunt_sender_desc));
		setDescription(context.getString(R.string.voipstunt_description));

		SmsServiceCommand command;
		//initializes the command list
		mProviderSettingsActivityCommands = new ArrayList<SmsServiceCommand>();
		command = new SmsServiceCommand(
				COMMAND_REGISTER, context.getString(R.string.voipstunt_commandRegister), 1, R.drawable.ic_menu_invite); 
		mProviderSettingsActivityCommands.add(command);

		//save some messages
		mMessages = new String[2];
		mMessages[MSG_INDEX_MESSAGE_SENT] = context.getString(R.string.voipstunt_msg_messageSent);
		mMessages[MSG_INDEX_MESSAGE_NO_SENT] = context.getString(R.string.voipstunt_msg_messageNotSent);

		return super.initProvider(context);
	}
	
	@Override
	public ResultOperation<String> sendMessage(String serviceId, String destination, String body)
	{
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
		
		String urlToSend = mDictionary.getUrlForMessage(
				getParameterValue(PARAM_INDEX_USERNAME),
				getParameterValue(PARAM_INDEX_PASSWORD),
				getParameterValue(PARAM_INDEX_SENDER),
				okDestination,
				body);
			
		ResultOperation<String> res = doSingleHttpRequest(urlToSend, null, null);

    	//checks for applications errors
    	if (res.hasErrors()) return res;
    	
    	//examine it the return contains confirmation if the message was sent
    	if (mDictionary.messageWasSent(res.getResult())) {
    		res.setResult(mMessages[MSG_INDEX_MESSAGE_SENT]);
		} else {
			LogFacility.e("VoipstuntProvider error reply");
			LogFacility.e(res.getResult());
    		res.setResult(mMessages[MSG_INDEX_MESSAGE_NO_SENT]);
    		res.setReturnCode(ResultOperation.RETURNCODE_PROVIDER_ERROR);
    	}
		
		return res;    	
	}


	@Override
    public ResultOperation<String> executeCommand(int commandId, Context context, Bundle extraData)
	{
		ResultOperation<String> res;

		//execute commands
		switch (commandId) {
		case COMMAND_REGISTER:
			res = registerToProvider(context, context.getString(R.string.voipstunt_registerLink));
			break;

		default:
			res = super.executeCommand(commandId, context, extraData);
		}

		return res;
	}




	//---------- Private methods

	@Override
	public ResultOperation<Object> getCaptchaContentFromProviderReply(String providerReply)
	{ return null; }

	@Override
	protected String getParametersFileName()
	{ return GlobalDef.voipstuntParametersFileName; }

	@Override
	public ResultOperation<String> sendCaptcha(String providerReply, String captchaCode)
	{ return null; }

}
