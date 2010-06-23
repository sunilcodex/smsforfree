package it.rainbowbreeze.smsforfree.providers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import it.rainbowbreeze.smsforfree_core.R;
import it.rainbowbreeze.smsforfree.common.GlobalDef;
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
	public JacksmsProvider(ProviderDao dao, Context context)
	{
		super(dao, PARAM_NUMBER);
		mDictionary = new JacksmsDictionary();
		
		setParameterDesc(PARAM_INDEX_USERNAME, context.getString(R.string.jacksms_username_desc));
		setParameterDesc(PARAM_INDEX_PASSWORD, context.getString(R.string.jacksms_password_desc));
		setParameterFormat(PARAM_INDEX_PASSWORD, SmsServiceParameter.FORMAT_PASSWORD);
		
		SmsServiceCommand command;
		//subservices commands list
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
		
		
		//save some messages
		mMessages = new String[8];
		mMessages[MSG_INDEX_INVALID_CREDENTIALS] = context.getString(R.string.jacksms_msg_invalidCredentials);
		mMessages[MSG_INDEX_SERVER_ERROR] = context.getString(R.string.jacksms_msg_serverError);
		mMessages[MSG_INDEX_MESSAGE_SENT] = context.getString(R.string.jacksms_msg_messageSent);
		mMessages[MSG_INDEX_NO_CAPTCHA_SESSION_ID] = context.getString(R.string.jacksms_msg_noCaptchaSessionId);
		mMessages[MSG_INDEX_NO_TEMPLATES_PARSED] = context.getString(R.string.jacksms_msg_noTemplates);
		mMessages[MSG_INDEX_NO_CAPTCHA_PARSED] = context.getString(R.string.jacksms_msg_noCaptcha);
		mMessages[MSG_INDEX_TEMPLATES_UPDATED] = context.getString(R.string.jacksms_msg_TemplatesListUpdated);
		mMessages[MSG_INDEX_CAPTCHA_OK] = context.getString(R.string.jacksms_msg_captchaOk);
	}
	
	
	

	//---------- Private fields
	private final static int PARAM_NUMBER = 2;
	private final static int PARAM_INDEX_USERNAME = 0;
	private final static int PARAM_INDEX_PASSWORD = 1;

	private final static int COMMAND_LOADTEMPLATESERVICES = 1000;
	private final static int COMMAND_LOADUSERSERVICES = 1001;
	private final static int COMMAND_REGISTER = 1002;
	
	private final static int MSG_INDEX_INVALID_CREDENTIALS = 0;
	private final static int MSG_INDEX_SERVER_ERROR = 1;
	private final static int MSG_INDEX_MESSAGE_SENT = 2;
	private final static int MSG_INDEX_NO_CAPTCHA_SESSION_ID = 3;
	private final static int MSG_INDEX_NO_TEMPLATES_PARSED = 4;
	private final static int MSG_INDEX_NO_CAPTCHA_PARSED = 5;
	private final static int MSG_INDEX_TEMPLATES_UPDATED = 6;
	private final static int MSG_INDEX_CAPTCHA_OK = 7;
	
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
    public ResultOperation<String> sendMessage(
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
    	ResultOperation<String> res = doRequest(mDictionary.getUrlForSendingCaptcha(username, password), headers, null);

    	//checks for applications errors
    	if (res.HasErrors()) return res;
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
	 * Send an sms via the service API
	 * 
	 * @param username
	 * @param password
	 * @param serviceId
	 * @param destination
	 * @param message
	 * @return
	 */
	private ResultOperation<String> sendSms(
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
    	ResultOperation<String> res = doRequest(mDictionary.getUrlForSendingMessage(username, password), headers, null);

    	//checks for applications errors
    	if (res.HasErrors()) return res;
    	//checks for jacksms errors
    	if (parseReplyForErrors(res)) return res;
    	
    	//at this point, no error happened, so checks if the sms was sent or
    	//a captcha code is needed
    	String reply = res.getResult();
    	//message sent
		if (reply.startsWith(JacksmsDictionary.PREFIX_RESULT_OK)) {
			//breaks the reply and find the message
			res.setResult(String.format(
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
    private ResultOperation<String> downloadTemplates(Context context)
    {
    	String username = getParameterValue(PARAM_INDEX_USERNAME);
		String password = getParameterValue(PARAM_INDEX_PASSWORD);

    	//credentials check
    	if (!checkCredentialsValidity(username, password))
    		return getExceptionForInvalidCredentials();

    	ResultOperation<String> res = doRequest(mDictionary.getUrlForDownloadTemplates(username, password), null, null);

    	//checks for applications errors
    	if (res.HasErrors()) return res;
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
    	if (saveResult.HasErrors()) {
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
		for (String errSignature : JacksmsDictionary.PREFIX_RESULT_ERROR_ARRAY) {
			if (reply.startsWith(errSignature)) {
				res = String.format(mMessages[MSG_INDEX_SERVER_ERROR], mDictionary.getTextPartFromReply(reply));
				//error found, exit from cycle
				break;
			}
		}
		
    	//errors are internal to JackSMS, not related to communication issues.
    	//so no application errors (like network issues) should be returned, but
		//the JackSMS error must stops the execution of the calling method
    	if (!TextUtils.isEmpty(res)) {
    		resultToAnalyze.setResult(res);
    		return true;
    	} else {
    		return false;
    	}
	}
}