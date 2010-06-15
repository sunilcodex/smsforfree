package it.rainbowbreeze.smsforfree.providers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.data.WebserviceClient;
import it.rainbowbreeze.smsforfree.domain.SmsServiceCommand;
import it.rainbowbreeze.smsforfree.domain.SmsServiceParameter;
import it.rainbowbreeze.smsforfree.domain.SmsSingleProvider;
import it.rainbowbreeze.smsforfree.util.Base64;

public class AimonProvider
	extends SmsSingleProvider
{
	//---------- Ctors
	public AimonProvider(ProviderDao dao, Context context)
	{
		super(dao, PARAM_NUMBER);

		setParameterDesc(PARAM_INDEX_USERNAME, context.getString(R.string.aimon_username));
		setParameterDesc(PARAM_INDEX_PASSWORD, context.getString(R.string.aimon_password));
		setParameterFormat(PARAM_INDEX_PASSWORD, SmsServiceParameter.FORMAT_PASSWORD);
		setParameterDesc(PARAM_INDEX_SENDER, context.getString(R.string.aimon_sender));
		setParameterDesc(PARAM_INDEX_ID_API, context.getString(R.string.aimon_idapi));
		
		//initializes the command list
		mProviderSettingsActivityCommands = new ArrayList<SmsServiceCommand>();
		SmsServiceCommand command;
		command = new SmsServiceCommand(
				COMMAND_CHECKCREDENTIALS, context.getString(R.string.aimon_commandCheckCredentials), 1000, R.drawable.ic_menu_login);
		mProviderSettingsActivityCommands.add(command);
		command = new SmsServiceCommand(
				COMMAND_CHECKCREDITS, context.getString(R.string.aimon_commandCheckCredits), 1001); 
		mProviderSettingsActivityCommands.add(command);
		
		//save some messages
		mMessages = new String[6];
		mMessages[MSG_INDEX_INVALID_CREDENTIALS] = context.getString(R.string.aimon_msg_invalidCredentials);
		mMessages[MSG_INDEX_VALID_CREDENTIALS] = context.getString(R.string.aimon_msg_validCredentials);
		mMessages[MSG_INDEX_SERVER_ERROR] = context.getString(R.string.aimon_msg_serverError);
		mMessages[MSG_INDEX_REMAINING_CREDITS] = context.getString(R.string.aimon_msg_remainingCredits);
		mMessages[MSG_INDEX_MESSAGE_SENT] = context.getString(R.string.aimon_msg_messageQueued);
		mMessages[MSG_INDEX_MISSING_PARAMETERS] = context.getString(R.string.aimon_msg_missingParameters);
	}
	
	
	//---------- Private fields
	private final static int PARAM_NUMBER = 4;
	private final static int PARAM_INDEX_USERNAME = 0;
	private final static int PARAM_INDEX_PASSWORD = 1;
	private final static int PARAM_INDEX_SENDER = 2;
	private final static int PARAM_INDEX_ID_API = 3;

	private final static int MSG_INDEX_INVALID_CREDENTIALS = 0;
	private final static int MSG_INDEX_VALID_CREDENTIALS = 1;
	private final static int MSG_INDEX_SERVER_ERROR = 2;
	private final static int MSG_INDEX_REMAINING_CREDITS = 3;
	private final static int MSG_INDEX_MESSAGE_SENT = 4;
	private final static int MSG_INDEX_MISSING_PARAMETERS = 5;
	
	private final static int COMMAND_CHECKCREDENTIALS = 1000;
	private final static int COMMAND_CHECKCREDITS = 1001;
	
	private String[] mMessages;
	
	
	

	//---------- Public fields
	public final static String ID_API_FIXED_SENDER = "106";
	public final static String ID_API_FREE_SENDER_NO_REPORT = "59";
	public final static String ID_API_FREE_SENDER_REPORT = "84";
	
	
	
	
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
	public int getMaxMessageLenght()
	{ return 160; }
    
	@Override
	public boolean hasSettingsActivityCommands()
	{ return true; }

	private List<SmsServiceCommand> mProviderSettingsActivityCommands;
	@Override
	public List<SmsServiceCommand> getSettingsActivityCommands()
	{ return mProviderSettingsActivityCommands; }
	

	
	
    //---------- Public methods
    @Override
	public ResultOperation<String> sendMessage(String serviceId, String destination, String body) {
		return sendSms(
				getParameterValue(PARAM_INDEX_USERNAME),
				getParameterValue(PARAM_INDEX_PASSWORD),
				getParameterValue(PARAM_INDEX_SENDER),
				destination,
				body,
				getParameterValue(PARAM_INDEX_ID_API));
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
			res = new ResultOperation<String>(new IllegalArgumentException("Command not found!"));
		}

		return res;
	}

	
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
	public ResultOperation<Object> getCaptchaContentFromProviderReply(String providerReply)
	{ return null; }

	@Override
	public ResultOperation<String> sendCaptcha(String providerReply, String captchaCode)
	{ return null; }

	/**
	 * Sends an sms
	 */
	private ResultOperation<String> sendSms(
    		String username,
    		String password,
    		String sender,
    		String destination,
    		String body,
    		String idApi)
    {
    	//args check
    	if (!checkCredentialsValidity(username, password))
    		return getExceptionForInvalidCredentials();
    	
    	String okSender;
    	String okDestination;
    	String okBody;
    	
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
        		if (TextUtils.isEmpty(prefix)) prefix = GlobalDef.italyInternationalPrefix;
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
    	okSender = Base64.encodeBytes(okSender.getBytes());

    	//check the destination
    	if (destination.substring(0, 1).equals("+")) {
    		okDestination = destination.substring(1);
    	} else {
    		String prefix = AppPreferencesDao.instance().getDefaultInternationalPrefix();
    		if (TextUtils.isEmpty(prefix)) prefix = GlobalDef.italyInternationalPrefix;
    		//removes begin + char
    		okDestination = prefix.substring(1) + destination;
    	}

		//checks body length
    	if (body.length() > AimonDictionary.MAX_BODY_LENGTH) {
    		okBody = body.substring(0, AimonDictionary.MAX_BODY_LENGTH);
    	} else {
    		okBody = body;
    	}
    	//TODO : remove unsupported characters
    	//encode the body
		okBody = Base64.encodeBytes(okBody.getBytes());
    	
    	
    	HashMap<String, String> data = new HashMap<String, String>();
    	appendCredential(data, username, password);
    	data.put(AimonDictionary.PARAM_SENDER, okSender);
    	data.put(AimonDictionary.PARAM_DESTINATION, okDestination);
    	data.put(AimonDictionary.PARAM_BODY, okBody);
    	data.put(AimonDictionary.PARAM_ID_API, idApi);
    	
    	//sends the sms
    	ResultOperation<String> res = doRequest(AimonDictionary.URL_SEND_SMS, data);
    	//checks for applications errors
    	if (res.HasErrors()) return res;
    	//checks for aimon errors
    	if (parseReplyForErrors(res)) return res;
    	
    	//examine it the return contains confirmation if the message was sent
		if (res.getResult().startsWith(AimonDictionary.RESULT_SENDSMS_OK)) {
			res.setResult(String.format(
					mMessages[MSG_INDEX_MESSAGE_SENT], res.getResult()));
			
			//TODO
			//at this point, i cal also read the remaining credits and append it to
			//the message
		}
		
		return res;    	
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
    	
		HashMap<String, String> data = new HashMap<String, String>();
    	appendCredential(data, username, password);
    	
    	//call the api that gets the credit
    	ResultOperation<String> res = doRequest(AimonDictionary.URL_GET_CREDIT, data);
    	//checks for application errors
    	if (res.HasErrors()) return res;
    	//checks for aimon errors
    	if (parseReplyForErrors(res)) return res;
    	
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
		if (res.HasErrors()) return res;
		
		//at this point reply can only contains the remaining credits or
		//aimon internal errors
		
		//ok, i know it isn't the best way, but it works as a workaround
		//for the presence of %s parameter in the source message string ;)
		int pos = mMessages[MSG_INDEX_REMAINING_CREDITS].indexOf("%");
		if (pos < 0) pos = mMessages[MSG_INDEX_REMAINING_CREDITS].length();
		String returnMessage = res.getResult(); 
		if (returnMessage.substring(0, pos).equals(mMessages[MSG_INDEX_REMAINING_CREDITS].substring(0, pos))) {
			//credits, so credentials are correct
			res = new ResultOperation<String>(mMessages[MSG_INDEX_VALID_CREDENTIALS]);
		}
		
		return res;
	}
	
	
    /**
     * Append username and password on headers map
     * @param data
     * @param username
     * @param password
     */
	private void appendCredential(HashMap<String, String> data, String username, String password)
    {
    	data.put(AimonDictionary.PARAM_USERNAME, username);
    	data.put(AimonDictionary.PARAM_PASSWORD, password);
    }
    

	/**
	 * Execute the http request
	 * @param url
	 * @param data
	 * @return
	 */
    private ResultOperation<String> doRequest(String url, HashMap<String, String> parameters)
    {
    	String reply = "";
    	WebserviceClient client = new WebserviceClient();
    	
    	try {
    		reply = client.requestPost(url, null, parameters);
		} catch (ClientProtocolException e) {
			return new ResultOperation<String>(e);
		} catch (IOException e) {
			return new ResultOperation<String>(e);
		}
    	
    	//empty reply
    	if (TextUtils.isEmpty(reply)) {
			return new ResultOperation<String>(new Exception(ERROR_NO_REPLY_FROM_SITE));
		}

    	//return the reply
    	return new ResultOperation<String>(reply);
    }

    
	/**
	 * Parse the webservice reply searching for know errors code.
	 * If one of them is found, the ResultOperation object is modified
	 * whit the error message to display
	 * 
	 * @param resultToAnalyze
	 * @return true if an aimon error is found, otherwise false
	 */
	public boolean parseReplyForErrors(ResultOperation<String> resultToAnalyze)
	{
		String res = "";
		String reply = resultToAnalyze.getResult();

		//no reply from server is already handled in doRequest method
				
		//access denied
		if (reply.startsWith(AimonDictionary.RESULT_ERROR_ACCESS_DENIED)) {
			res = mMessages[MSG_INDEX_INVALID_CREDENTIALS];

		//server error
		} else if (reply.startsWith(AimonDictionary.RESULT_ERROR_INTERNAL_SERVER_ERROR)) {
			res = mMessages[MSG_INDEX_SERVER_ERROR];
		
		//missing parameters
		} else if (reply.startsWith(AimonDictionary.RESULT_ERROR_MISSING_PARAMETERS)) {
			res = String.format(mMessages[MSG_INDEX_MISSING_PARAMETERS], reply);
		}
	
		//TODO
		//add other server errors
		
    	//errors are internal to aimon, not related to communication issues.
    	//so no application errors (like network issues) should be returned, but
		//the aimon error must stops the execution of the calling method
    	if (!TextUtils.isEmpty(res)) {
    		resultToAnalyze.setResult(res);
    		return true;
    	} else {
    		return false;
    	}
	}
}
