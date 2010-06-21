package it.rainbowbreeze.smsforfree.providers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalDef;
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
	public AimonProvider(ProviderDao dao, Context context)
	{
		super(dao, PARAM_NUMBER);

		setParameterDesc(PARAM_INDEX_USERNAME, context.getString(R.string.aimon_username));
		setParameterDesc(PARAM_INDEX_PASSWORD, context.getString(R.string.aimon_password));
		setParameterFormat(PARAM_INDEX_PASSWORD, SmsServiceParameter.FORMAT_PASSWORD);
		setParameterDesc(PARAM_INDEX_SENDER, context.getString(R.string.aimon_sender));
		setDescription(context.getString(R.string.aimon_description));
		
		SmsServiceCommand command;
		//initializes the command list
		mProviderSettingsActivityCommands = new ArrayList<SmsServiceCommand>();
		command = new SmsServiceCommand(
				COMMAND_REGISTER, context.getString(R.string.aimon_commandRegister), 1, R.drawable.ic_menu_invite); 
		mProviderSettingsActivityCommands.add(command);
		command = new SmsServiceCommand(
				COMMAND_CHECKCREDENTIALS, context.getString(R.string.aimon_commandCheckCredentials), 2, R.drawable.ic_menu_login);
		mProviderSettingsActivityCommands.add(command);
		command = new SmsServiceCommand(
				COMMAND_CHECKCREDITS, context.getString(R.string.aimon_commandCheckCredits), 3);
		mProviderSettingsActivityCommands.add(command);
		
		//save some messages
		mMessages = new String[10];
		mMessages[MSG_INDEX_INVALID_CREDENTIALS] = context.getString(R.string.aimon_msg_invalidCredentials);
		mMessages[MSG_INDEX_VALID_CREDENTIALS] = context.getString(R.string.aimon_msg_validCredentials);
		mMessages[MSG_INDEX_SERVER_ERROR] = context.getString(R.string.aimon_msg_serverError);
		mMessages[MSG_INDEX_REMAINING_CREDITS] = context.getString(R.string.aimon_msg_remainingCredits);
		mMessages[MSG_INDEX_MESSAGE_SENT] = context.getString(R.string.aimon_msg_messageQueued);
		mMessages[MSG_INDEX_MISSING_PARAMETERS] = context.getString(R.string.aimon_msg_missingParameters);
		mMessages[MSG_INDEX_SERVICENANE_ANONYMOUS] = context.getString(R.string.aimon_serviceNameAnonymous);
		mMessages[MSG_INDEX_SERVICENANE_FREE] = context.getString(R.string.aimon_serviceNameFree);
		mMessages[MSG_INDEX_SERVICENANE_NORMAL] = context.getString(R.string.aimon_serviceNameNormal);
		mMessages[MSG_INDEX_SERVICENANE_REPORT] = context.getString(R.string.aimon_serviceNameReport);
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
	private final static int MSG_INDEX_SERVICENANE_FREE = 6;
	private final static int MSG_INDEX_SERVICENANE_ANONYMOUS = 7;
	private final static int MSG_INDEX_SERVICENANE_NORMAL = 8;
	private final static int MSG_INDEX_SERVICENANE_REPORT = 9;
	
	private final static int COMMAND_CHECKCREDENTIALS = 1000;
	private final static int COMMAND_CHECKCREDITS = 1001;
	private final static int COMMAND_REGISTER = 1002;
	
	private String[] mMessages;
	
	
	

	//---------- Public fields
	public final static String ID_API_ANONYMOUS_SENDER = "106";
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
	public boolean hasSubServicesToConfigure()
	{ return false; }
    
	private List<SmsServiceCommand> mProviderSettingsActivityCommands;
	@Override
	public List<SmsServiceCommand> getSettingsActivityCommands()
	{ return mProviderSettingsActivityCommands; }
	

	
	
    //---------- Public methods
	
	@Override
	public ResultOperation<Void> initProvider(Context context) {
		ResultOperation<Void> res;
		
		res = loadParameters(context);
		if (res.HasErrors()) return res;
		res = loadSubservices(context);
		if (res.HasErrors()) return res;
		
		return res;
	}
	
    @Override
	public ResultOperation<String> sendMessage(String serviceId, String destination, String body) {
		return sendSms(
				getParameterValue(PARAM_INDEX_USERNAME),
				getParameterValue(PARAM_INDEX_PASSWORD),
				getParameterValue(PARAM_INDEX_SENDER),
				destination,
				body,
				serviceId);
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

		case COMMAND_REGISTER:
			res = registerToProvider(context, context.getString(R.string.aimon_registerLink));
			break;

		default:
			res = new ResultOperation<String>(
					new Exception("No command with id " + commandId + " for Aimon provider"),
					ResultOperation.RETURNCODE_ERROR_APPLICATION_ARCHITECTURE);
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

	@Override
	protected ResultOperation<Void> loadTemplates(Context context)
	{ return null; }

	@Override
	protected ResultOperation<Void> saveTemplates(Context context)
	{ return null; }

	@Override
	protected ResultOperation<Void> loadSubservices(Context context)
	{
		//the subservice have as id the id to use with Aimon api for sending message
		SmsConfigurableService service;
		
		//create the list of subservices
		mSubservices = new ArrayList<SmsService>();
		
		service = new SmsConfigurableService(0);
		service.setId(ID_API_ANONYMOUS_SENDER);
		service.setName(mMessages[MSG_INDEX_SERVICENANE_ANONYMOUS]);
		mSubservices.add(service);

		service = new SmsConfigurableService(0);
		service.setId(ID_API_FREE_SENDER_NO_REPORT);
		service.setName(mMessages[MSG_INDEX_SERVICENANE_NORMAL]);
		mSubservices.add(service);

		service = new SmsConfigurableService(0);
		service.setId(ID_API_FREE_SENDER_REPORT);
		service.setName(mMessages[MSG_INDEX_SERVICENANE_REPORT]);
		mSubservices.add(service);
		
		return new ResultOperation<Void>();
	}


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
    	okDestination = transalteInInternationalFormat(destination);
		//removes begin + char
		okDestination = okDestination.substring(1);

		//checks body length
    	if (body.length() > AimonDictionary.MAX_BODY_LENGTH) {
    		okBody = body.substring(0, AimonDictionary.MAX_BODY_LENGTH);
    	} else {
    		okBody = body;
    	}
    	//TODO : remove unsupported characters
    	//encode the body
		okBody = Base64.encodeBytes(okBody.getBytes());
    	
    	
    	HashMap<String, String> params = new HashMap<String, String>();
    	appendCredential(params, username, password);
    	params.put(AimonDictionary.PARAM_SENDER, okSender);
    	params.put(AimonDictionary.PARAM_DESTINATION, okDestination);
    	params.put(AimonDictionary.PARAM_BODY, okBody);
    	params.put(AimonDictionary.PARAM_ID_API, idApi);
    	
    	//sends the sms
    	ResultOperation<String> res = doRequest(AimonDictionary.URL_SEND_SMS, null, params);
    	//checks for applications errors
    	if (res.HasErrors()) return res;
    	//checks for aimon errors
    	if (parseReplyForErrors(res)) return res;
    	
    	//examine it the return contains confirmation if the message was sent
		if (res.getResult().startsWith(AimonDictionary.RESULT_SENDSMS_OK)) {
			res.setResult(String.format(
					mMessages[MSG_INDEX_MESSAGE_SENT], res.getResult()));
			
			//TODO
			//at this point, i can also read the remaining credits and append it to
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
    	
		HashMap<String, String> params = new HashMap<String, String>();
    	appendCredential(params, username, password);
    	
    	//call the api that gets the credit
    	ResultOperation<String> res = doRequest(AimonDictionary.URL_GET_CREDIT, null, params);
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
		
    	//errors are internal to Aimon, not related to communication issues.
    	//so no application errors (like network issues) should be returned, but
		//the Aimon error must stops the execution of the calling method
    	if (!TextUtils.isEmpty(res)) {
    		resultToAnalyze.setResult(res);
    		return true;
    	} else {
    		return false;
    	}
	}
}
