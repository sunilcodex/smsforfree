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
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.domain.SmsMultiProvider;
import it.rainbowbreeze.smsforfree.domain.SmsServiceCommand;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.domain.SmsServiceParameter;

/**
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class JacksmsProvider
	extends SmsMultiProvider
{
	//---------- Ctors
	public JacksmsProvider(ProviderDao dao)
	{
		super(dao, PARAM_NUMBER);
	}
	
	
	

	//---------- Private fields
	private final static int PARAM_NUMBER = 2;
	private final static int PARAM_INDEX_USERNAME = 0;
	private final static int PARAM_INDEX_PASSWORD = 1;

	private final static int MSG_INDEX_INVALID_CREDENTIALS = 0;
	private final static int MSG_INDEX_SERVER_ERROR = 1;
	private final static int MSG_INDEX_MESSAGE_SENT = 2;
	private final static int MSG_INDEX_NO_CAPTCHA_SESSION_ID = 3;
	private final static int MSG_INDEX_NO_TEMPLATES_PARSED = 4;
	private final static int MSG_INDEX_NO_CAPTCHA_PARSED = 5;
	private final static int MSG_INDEX_TEMPLATES_UPDATED = 6;
	private final static int MSG_INDEX_CAPTCHA_OK = 7;
	private final static int MSG_INDEX_NO_TEMPLATES_TO_USE = 8;
	
	private JacksmsDictionary mDictionary;
	
	private String[] mMessages;

	
	

	//---------- Public properties
	public final static int COMMAND_LOADTEMPLATESERVICES = 1000;
	public final static int COMMAND_LOADUSERSERVICES = 1001;
	public final static int COMMAND_REGISTER = 1002;
	
	@Override
	public String getId()
	{ return "JackSMS"; }

	@Override
	public String getName()
	{ return "JackSMS"; }
	
	@Override
	public int getParametersNumber()
	{ return PARAM_NUMBER; }


	private List<SmsServiceCommand> mProviderSettingsActivityCommands;
	@Override
	public List<SmsServiceCommand> getSettingsActivityCommands()
	{ return mProviderSettingsActivityCommands; }

	private List<SmsServiceCommand> mSubservicesListActivityCommands;
	@Override
	public List<SmsServiceCommand> getSubservicesListActivityCommands()
	{ return mSubservicesListActivityCommands; }


	

	//---------- Public methods
	
	@Override
	public ResultOperation<Void> initProvider(Context context)
	{
		mDictionary = new JacksmsDictionary();

		//provider parameters
		setParameterDesc(PARAM_INDEX_USERNAME, context.getString(R.string.jacksms_username_desc));
		setParameterDesc(PARAM_INDEX_PASSWORD, context.getString(R.string.jacksms_password_desc));
		setParameterFormat(PARAM_INDEX_PASSWORD, SmsServiceParameter.FORMAT_PASSWORD);
		setDescription(context.getString(R.string.jacksms_description));
	
		//subservices commands list
		SmsServiceCommand command;
		mSubservicesListActivityCommands = new ArrayList<SmsServiceCommand>();
		command = new SmsServiceCommand(
				COMMAND_LOADTEMPLATESERVICES, context.getString(R.string.jacksms_commandLoadTemplateServices), 1, R.drawable.ic_menu_refresh);
		mSubservicesListActivityCommands.add(command);
//		command = new SmsServiceCommand(
//				COMMAND_LOADUSERSERVICES, context.getString(R.string.jacksms_commandLoadUserSubservices), 2, R.drawable.ic_menu_cloud);
//		mSubservicesListActivityCommands.add(command);
		//provider commands list
		mProviderSettingsActivityCommands = new ArrayList<SmsServiceCommand>();
		command = new SmsServiceCommand(
				COMMAND_REGISTER, context.getString(R.string.jacksms_commandRegister), 1, R.drawable.ic_menu_invite); 
		mProviderSettingsActivityCommands.add(command);
		
		//save messages
		mMessages = new String[9];
		mMessages[MSG_INDEX_INVALID_CREDENTIALS] = context.getString(R.string.jacksms_msg_invalidCredentials);
		mMessages[MSG_INDEX_SERVER_ERROR] = context.getString(R.string.jacksms_msg_serverError);
		mMessages[MSG_INDEX_MESSAGE_SENT] = context.getString(R.string.jacksms_msg_messageSent);
		mMessages[MSG_INDEX_NO_CAPTCHA_SESSION_ID] = context.getString(R.string.jacksms_msg_noCaptchaSessionId);
		mMessages[MSG_INDEX_NO_TEMPLATES_PARSED] = context.getString(R.string.jacksms_msg_noTemplatesParsed);
		mMessages[MSG_INDEX_NO_CAPTCHA_PARSED] = context.getString(R.string.jacksms_msg_noCaptcha);
		mMessages[MSG_INDEX_TEMPLATES_UPDATED] = context.getString(R.string.jacksms_msg_TemplatesListUpdated);
		mMessages[MSG_INDEX_CAPTCHA_OK] = context.getString(R.string.jacksms_msg_captchaOk);
		mMessages[MSG_INDEX_NO_TEMPLATES_TO_USE] = context.getString(R.string.jacksms_msg_NoTemplatesToUse);
		
		return super.initProvider(context);
	}
	
	
	@Override
    public ResultOperation<String> sendMessage(
    		String serviceId,
    		String destination,
    		String message)
    {
		String username = getParameterValue(PARAM_INDEX_USERNAME);
		String password = getParameterValue(PARAM_INDEX_PASSWORD);
	
		//credentials check
		if (!checkCredentialsValidity(username, password))
			return getExceptionForInvalidCredentials();
		
		//sends the sms
		SmsService service = getSubservice(serviceId);
		HashMap<String, String> headers = mDictionary.getHeaderForSendingMessage(service, destination, message);
		ResultOperation<String> res = doSingleHttpRequest(mDictionary.getUrlForSendingMessage(username, password), headers, null);
	
		//checks for applications errors
		if (res.hasErrors()) return res;
		//checks for jacksms errors
		if (parseReplyForErrors(res)) return res;
		
		//at this point, no error happened, so checks if the sms was sent or
		//a captcha code is needed
		String reply = res.getResult();
		//message sent
		if (mDictionary.isSmsCorrectlySend(reply)) {
			//breaks the reply and find the message
			res.setResult(String.format(
					mMessages[MSG_INDEX_MESSAGE_SENT], mDictionary.getTextPartFromReply(reply)));
		//captcha request
		} else if (mDictionary.isCaptchaRequest(reply)) {
			//returns captcha, message contains all captcha information
			res.setReturnCode(ResultOperation.RETURNCODE_SMS_CAPTCHA_REQUEST);
		} else {
			//other generic error not handled by the parseReplyForErrors() method
			res.setReturnCode(ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR);
			res.setResult(mMessages[MSG_INDEX_SERVER_ERROR]);
			LogFacility.e("Error sending message in Jacksms Provider");
			LogFacility.e(reply);
		}
		
		return res;    	
	}

	@Override
	public ResultOperation<Object> getCaptchaContentFromProviderReply(String providerReply)
	{
		//captcha content is the text part of the reply
		byte[] content = mDictionary.getCaptchaImageContentFromReply(providerReply);

		if (null == content) {
    		//errors in parsing captcha
			ResultOperation<Object> res = new ResultOperation<Object>(mMessages[MSG_INDEX_NO_CAPTCHA_PARSED]);
			res.setReturnCode(ResultOperation.RETURNCODE_ERROR_GENERIC);
    		return res;
    	}

		return new ResultOperation<Object>(content);
	}

	@Override
	public ResultOperation<String> sendCaptcha(String providerReply, String captchaCode)
	{
		//find captcha sessionId
		String sessionId = mDictionary.getCaptchaSessionIdFromReply(providerReply);
		if (TextUtils.isEmpty(sessionId)) {
			return new ResultOperation<String>(mMessages[MSG_INDEX_NO_CAPTCHA_SESSION_ID]);
		}
    	
		String username = getParameterValue(PARAM_INDEX_USERNAME);
		String password = getParameterValue(PARAM_INDEX_PASSWORD);

    	//sends the captcha code
    	HashMap<String, String> headers = mDictionary.getHeaderForSendingCaptcha(sessionId, captchaCode);
    	ResultOperation<String> res = doSingleHttpRequest(mDictionary.getUrlForSendingCaptcha(username, password), headers, null);

    	//checks for applications errors
    	if (res.hasErrors()) return res;
    	//checks for jacksms errors
    	if (parseReplyForErrors(res)) return res;
    	
    	//at this point, no error happened, so the reply contains captcha submission result
    	String reply = res.getResult();
    	String returnMessage = mDictionary.getTextPartFromReply(reply);
    	if (TextUtils.isEmpty(returnMessage)) returnMessage = mMessages[MSG_INDEX_CAPTCHA_OK];
		res.setResult(returnMessage);
		
		return res;    	
	}
	
	

	@Override
	public ResultOperation<String> executeCommand(int commandId, Context context, Bundle extraData) {
		ResultOperation<String> res;

		//execute commands
		switch (commandId) {
		case COMMAND_LOADTEMPLATESERVICES:
			res = downloadTemplates(context);
			break;

		case COMMAND_LOADUSERSERVICES:
			res = downloadUserConfiguredServices(context);
			break;

		case COMMAND_REGISTER:
			res = registerToProvider(context, context.getString(R.string.jacksms_registerLink));
			break;

		default:
			res = super.executeCommand(commandId, context, extraData);
		}

		return res;
	}



	//---------- Private methods
	@Override
	protected String getParametersFileName()
	{ return GlobalDef.jacksmsParametersFileName; }

	@Override
	protected String getTemplatesFileName()
	{ return GlobalDef.jacksmsmTemplatesFileName; }

	@Override
	protected String getSubservicesFileName()
	{ return GlobalDef.jacksmsSubservicesFileName; }

    
    /**
     * Downloads all service templates available from JackSMS site
     * @return
     */
    private ResultOperation<String> downloadTemplates(Context context)
    {
    	String username = getParameterValue(PARAM_INDEX_USERNAME);
		String password = getParameterValue(PARAM_INDEX_PASSWORD);

    	//credentials check
    	if (!checkCredentialsValidity(username, password))
    		return getExceptionForInvalidCredentials();

    	ResultOperation<String> res = doSingleHttpRequest(mDictionary.getUrlForDownloadTemplates(username, password), null, null);

    	//checks for applications errors
    	if (res.hasErrors()) return res;
    	//checks for jacksms errors
    	if (parseReplyForErrors(res)) return res;

    	//at this point, the provider reply should contains the list of templates
    	String templatesReply = res.getResult();
    	
    	//transform the reply in the list of templates
    	List<SmsService> newTemplates = mDictionary.extractTemplates(templatesReply);
    	
    	if (newTemplates.size() <= 0) {
    		//retain old templates
    		res.setResult(mMessages[MSG_INDEX_NO_TEMPLATES_PARSED]);
    		return res;
    	}
    	
    	//try to set password fields
    	for (SmsService template : newTemplates) {
    		//generally, jacksms use second parameter as password
    		String description = template.getParameterDesc(1);
    		if (!TextUtils.isEmpty(description) && description.toUpperCase().contains("PASSWORD"))
    			template.setParameterFormat(1, SmsServiceParameter.FORMAT_PASSWORD);
    	}
    	
    	//override current templates with new one
    	mTemplates = newTemplates;
    	//save the template list
    	ResultOperation<Void> saveResult = saveTemplates(context);
    	//and checks for errors in saving
    	if (saveResult.hasErrors()) {
    		res.setException(saveResult.getException(), saveResult.getReturnCode());
    		return res;
    	}
    	
    	//all done, set the result message
    	res.setResult(mMessages[MSG_INDEX_TEMPLATES_UPDATED]);
    	return res;
    }
    
    
    /**
     * Downloads all services configured for the user from JackSMS site
     * @return
     */
    private ResultOperation<String> downloadUserConfiguredServices(Context context)
    {
    	String username = getParameterValue(PARAM_INDEX_USERNAME);
		String password = getParameterValue(PARAM_INDEX_PASSWORD);

    	//credentials check
    	if (!checkCredentialsValidity(username, password))
    		return getExceptionForInvalidCredentials();
    	
    	//checks for templates
    	if (!hasTemplatesConfigured())
    		return new ResultOperation<String>(mMessages[MSG_INDEX_NO_TEMPLATES_TO_USE]);
    	
    	ResultOperation<String> res = doSingleHttpRequest(mDictionary.getUrlForDownloadUserServices(username, password), null, null);

    	//checks for applications errors
    	if (res.hasErrors()) return res;
    	//checks for jacksms errors
    	if (parseReplyForErrors(res)) return res;

    	//at this point, the provider reply should contains the list of templates
    	String providerReply = res.getResult();
    	
    	//transform the reply in the list of templates
    	List<SmsService> newTemplates = mDictionary.extractUserServices(providerReply);
    	
    	if (newTemplates.size() <= 0) {
    		//retain old templates
    		res.setResult(mMessages[MSG_INDEX_NO_TEMPLATES_PARSED]);
    		return res;
    	}
    	
    	return null;
    }

    
	/**
	 * Parse the webservice reply searching for know errors code.
	 * If one of them is found, the ResultOperation object is modified
	 * whit the error message to display
	 * 
	 * @param resultToAnalyze
	 * @return true if a JackSMS error is found, otherwise false
	 */
	public boolean parseReplyForErrors(ResultOperation<String> resultToAnalyze)
	{
		String res = "";
		String reply = resultToAnalyze.getResult();

		//no reply from server is already handled in doRequest method
				
		//JackSMS internal error
		if (mDictionary.isErrorReply(reply)) {
			res = String.format(mMessages[MSG_INDEX_SERVER_ERROR], mDictionary.getTextPartFromReply(reply));
		}
		
		//JackSMS internal error
		if (mDictionary.isUnmanagedErrorReply(reply)) {
			res = reply;
		}
		
    	//errors are internal to JackSMS, not related to communication issues.
    	//so no application errors (like network issues) should be returned, but
		//the JackSMS error must stops the execution of the calling method
    	if (!TextUtils.isEmpty(res)) {
			LogFacility.e("JacksmsProvider error reply");
			LogFacility.e(reply);
    		resultToAnalyze.setResult(res);
    		resultToAnalyze.setReturnCode(ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR);
    		return true;
    	} else {
    		return false;
    	}
	}
}
