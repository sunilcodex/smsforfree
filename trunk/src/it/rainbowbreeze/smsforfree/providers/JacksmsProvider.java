package it.rainbowbreeze.smsforfree.providers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.data.WebserviceClient;
import it.rainbowbreeze.smsforfree.domain.SmsConfigurableService;
import it.rainbowbreeze.smsforfree.domain.SmsMultiProvider;
import it.rainbowbreeze.smsforfree.domain.SmsServiceCommand;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.domain.SmsServiceParameter;

public class JacksmsProvider
	extends SmsMultiProvider
{
	//---------- Ctors
	public JacksmsProvider(ProviderDao dao, Context context)
	{
		super(dao, PARAM_NUMBER);
		mDictionary = new JacksmsDictionary();
		
		setParameterDesc(PARAM_INDEX_USERNAME, context.getString(R.string.jacksms_username_desc));
		setParameterDesc(PARAM_INDEX_PASSWORD, context.getString(R.string.jacksms_password_desc));
		setParameterFormat(PARAM_INDEX_PASSWORD, SmsServiceParameter.FORMAT_PASSWORD);
		
		//initializes the command list
		mSubservicesListActivityCommands = new ArrayList<SmsServiceCommand>();
		SmsServiceCommand command;
		command = new SmsServiceCommand(
				COMMAND_LOADTEMPLATESERVICES, context.getString(R.string.jacksms_commandLoadTemplateServices), 1000, R.drawable.ic_menu_refresh);
		mSubservicesListActivityCommands.add(command);
		command = new SmsServiceCommand(
				COMMAND_LOADUSERSERVICES, context.getString(R.string.jacksms_commandLoadUserSubservices), 1001);
		mSubservicesListActivityCommands.add(command);
		
		//save some messages
		mMessages = new String[4];
		mMessages[MSG_INDEX_INVALID_CREDENTIALS] = context.getString(R.string.jacksms_msg_invalidCredentials);
		mMessages[MSG_INDEX_SERVER_ERROR] = context.getString(R.string.jacksms_msg_serverError);
		mMessages[MSG_INDEX_MESSAGE_SENT] = context.getString(R.string.jacksms_msg_messageSent);
		mMessages[MSG_INDEX_NO_CAPTCHA_SESSION_ID] = context.getString(R.string.jacksms_msg_noCaptchaSessionId);
	}
	
	
	

	//---------- Private fields
	private final static int PARAM_NUMBER = 2;
	private final static int PARAM_INDEX_USERNAME = 0;
	private final static int PARAM_INDEX_PASSWORD = 1;

	private final static int COMMAND_LOADTEMPLATESERVICES = 1000;
	private final static int COMMAND_LOADUSERSERVICES = 1001;
	
	private final static int MSG_INDEX_INVALID_CREDENTIALS = 0;
	private final static int MSG_INDEX_SERVER_ERROR = 1;
	private final static int MSG_INDEX_MESSAGE_SENT = 2;
	private final static int MSG_INDEX_NO_CAPTCHA_SESSION_ID = 3;
	
	private JacksmsDictionary mDictionary;
	
	private String[] mMessages;

	
	

	//---------- Public properties
	@Override
	public String getId()
	{ return "JackSMS"; }

	@Override
	public String getName()
	{ return "JackSMS"; }
	
	@Override
	public int getParametersNumber()
	{ return PARAM_NUMBER; }


	@Override
	public boolean hasSettingsActivityCommands()
	{ return false; }

	@Override
	public List<SmsServiceCommand> getSettingsActivityCommands()
	{ return null; }

	@Override
	public boolean hasSubservicesListActivityCommands()
	{ return true; }

	private List<SmsServiceCommand> mSubservicesListActivityCommands;
	@Override
	public List<SmsServiceCommand> getSubservicesListActivityCommands()
	{ return mSubservicesListActivityCommands; }


	

	//---------- Public methods
	@Override
	public ResultOperation loadTemplates(Context context)
	{
		//TODO
		mTemplates = new ArrayList<SmsService>();
		
		SmsConfigurableService service;
		service = new SmsConfigurableService("62", "Aimon-Free", 112, 3);
		service.setParameterDesc(0, "Username di login (SOLO il nome, senza @aimon.it)");
		service.setParameterDesc(1, "Password di accesso");
		service.setParameterFormat(1, SmsServiceParameter.FORMAT_PASSWORD);
		service.setParameterDesc(2, "Mittente (senza pref internazionale)");
		mTemplates.add(service);
		service = new SmsConfigurableService("61", "Aimon", 612, 3);
		service.setParameterDesc(0, "Username di login (SOLO il nome, senza @aimon.it)");
		service.setParameterDesc(1, "Password di accesso");
		service.setParameterFormat(1, SmsServiceParameter.FORMAT_PASSWORD);
		service.setParameterDesc(2, "Mittente (senza pref internazionale)");
		mTemplates.add(service);
		service = new SmsConfigurableService("29", "VoipStunt", 160, 3);
		service.setParameterDesc(0, "Username di accesso su voipstunt.com");
		service.setParameterDesc(1, "Password di accesso su voipstunt.com");
		service.setParameterFormat(1, SmsServiceParameter.FORMAT_PASSWORD);
		service.setParameterDesc(2, "Numero verificato che verra' visualizzato come mittente");
		mTemplates.add(service);
		service = new SmsConfigurableService("1", "Vodafone-SMS", 360, 3);
		service.setParameterDesc(0, "Username di accesso a www.190.it");
		service.setParameterDesc(1, "Password di accesso a www.190.it");
		service.setParameterFormat(1, SmsServiceParameter.FORMAT_PASSWORD);
		service.setParameterDesc(2, "Inserisci il numero di telefono della sim con cui vuoi inviare tramite questo account.");
		mTemplates.add(service);
		service = new SmsConfigurableService("33", "Enel", 110, 2);
		service.setParameterDesc(0, "Username");
		service.setParameterDesc(1, "Password");
		service.setParameterFormat(1, SmsServiceParameter.FORMAT_PASSWORD);
		mTemplates.add(service);
		
		Collections.sort(mTemplates);

		return new ResultOperation("");
 
	}

	
	@Override
    public ResultOperation sendMessage(
    		String serviceId,
    		String destination,
    		String message)
    {
		return sendSms(
				getParameterValue(PARAM_INDEX_USERNAME),
				getParameterValue(PARAM_INDEX_PASSWORD),
				serviceId,
				destination,
				message);
    }

	@Override
	public ResultOperation getCaptchaContentFromProviderReply(String providerReply)
	{
		//captcha content is the text part of the reply
		String content = mDictionary.getCaptchaImageContentFromReply(providerReply);
		
		return new ResultOperation(content);
	}

	@Override
	public ResultOperation sendCaptcha(String providerReply, String captchaCode)
	{
		//find captcha sessionId
		String sessionId = mDictionary.getCaptchaSessionIdFromReply(providerReply);
		if (TextUtils.isEmpty(sessionId)) {
			return new ResultOperation(mMessages[MSG_INDEX_NO_CAPTCHA_SESSION_ID]);
		}
    	
		String username = getParameterValue(PARAM_INDEX_USERNAME);
		String password = getParameterValue(PARAM_INDEX_PASSWORD);

    	//sends the captcha code
    	HashMap<String, String> headers = mDictionary.getHeaderForSendingCaptcha(sessionId, captchaCode);
    	ResultOperation res = doRequest(mDictionary.getUrlForSendingCaptcha(username, password), headers);

    	//checks for applications errors
    	if (res.HasErrors()) return res;
    	//checks for jacksms errors
    	if (parseReplyForErrors(res)) return res;
    	
    	//at this point, no error happened, so the reply contains captcha submission result
    	String reply = res.getResultAsString();
		res.setResultAsString(mDictionary.getTextPartFromReply(reply));
		
		return res;    	
	}
	
	

	@Override
	public ResultOperation executeCommand(int commandId, Bundle extraData) {
		ResultOperation res;

		//execute commands
		switch (commandId) {
		case COMMAND_LOADTEMPLATESERVICES:
			res = downloadTemplates();
			break;

		case COMMAND_LOADUSERSERVICES:
			res = downloadUserConfiguredServices();
			break;

		default:
			res = new ResultOperation(new IllegalArgumentException("Command not found!"));
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
	 * Send an sms via the service API
	 * 
	 * @param username
	 * @param password
	 * @param serviceId
	 * @param destination
	 * @param message
	 * @return
	 */
	private ResultOperation sendSms(
    		String username,
    		String password,
    		String serviceId,
    		String destination,
    		String message)
	{
    	//credentials check
    	if (!checkCredentialsValidity(username, password))
    		return getExceptionForInvalidCredentials();
    	
    	//sends the sms
    	SmsService service = getSubservice(serviceId);
    	HashMap<String, String> headers = mDictionary.getHeaderForSendingMessage(service, destination, message);
    	ResultOperation res = doRequest(mDictionary.getUrlForSendingMessage(username, password), headers);

    	//checks for applications errors
    	if (res.HasErrors()) return res;
    	//checks for jacksms errors
    	if (parseReplyForErrors(res)) return res;
    	
    	//at this point, no error happened, so checks if the sms was sent or
    	//a captcha code is needed
    	String reply = res.getResultAsString();
    	//message sent
		if (reply.startsWith(JacksmsDictionary.PREFIX_RESULT_OK)) {
			//breaks the reply and find the message
			res.setResultAsString(String.format(
					mMessages[MSG_INDEX_MESSAGE_SENT], mDictionary.getTextPartFromReply(reply)));
		//captcha request
		} else {
			//returns captcha, message contains all captcha information
			res.setReturnCode(ResultOperation.RETURNCODE_CAPTCHA_REQUEST);
		}
		return res;    	
	}

    
    /**
     * Downloads all service templates available from JackSMS site
     * @return
     */
    private ResultOperation downloadTemplates()
    {
    	String username = getParameterValue(PARAM_INDEX_USERNAME);
		String password = getParameterValue(PARAM_INDEX_PASSWORD);

    	//credentials check
    	if (!checkCredentialsValidity(username, password))
    		return getExceptionForInvalidCredentials();

    	ResultOperation res = doRequest(mDictionary.getUrlForDownloadTemplates(username, password), null);

    	String message = res.getResultAsString();
    	
    	return res;
    }
    
    
    /**
     * Downloads all services configured for the user from JackSMS site
     * @return
     */
    private ResultOperation downloadUserConfiguredServices()
    {
    	String username = getParameterValue(PARAM_INDEX_USERNAME);
		String password = getParameterValue(PARAM_INDEX_PASSWORD);

    	//credentials check
    	if (!checkCredentialsValidity(username, password))
    		return getExceptionForInvalidCredentials();
    	
    	return null;
    }

    
    private ResultOperation doRequest(String url, HashMap<String, String> headers)
    {
    	String reply = "";
    	WebserviceClient client = new WebserviceClient();
    	
    	try {
    		reply = client.requestPost(url, headers);
		} catch (ClientProtocolException e) {
			return new ResultOperation(e);
		} catch (IOException e) {
			return new ResultOperation(e);
		}
    	
    	//empty reply
    	if (TextUtils.isEmpty(reply)) {
			return new ResultOperation(new Exception(ERROR_NO_REPLY_FROM_SITE));
		}

    	//return the reply
    	return new ResultOperation(reply);
    }

	/**
	 * Parse the webservice reply searching for know errors code.
	 * If one of them is found, the ResultOperation object is modified
	 * whit the error message to display
	 * 
	 * @param resultToAnalyze
	 * @return true if a JackSMS error is found, otherwise false
	 */
	public boolean parseReplyForErrors(ResultOperation resultToAnalyze)
	{
		String res = "";
		String reply = resultToAnalyze.getResultAsString();

		//no reply from server is already handled in doRequest method
				
		//JackSMS internal error
		for (String errSignature : JacksmsDictionary.PREFIX_RESULT_ERROR_ARRAY) {
			if (reply.startsWith(errSignature)) {
				res = String.format(mMessages[MSG_INDEX_SERVER_ERROR], mDictionary.getTextPartFromReply(reply));
				//error found, exit from cycle
				break;
			}
		}
		
    	//errors are internal to jacksms, not related to communication issues.
    	//so no application errors (like network issues) should be returned, but
		//the jacksms error must stops the execution of the calling method
    	if (!TextUtils.isEmpty(res)) {
    		resultToAnalyze.setResultAsString(res);
    		return true;
    	} else {
    		return false;
    	}
	}
}
