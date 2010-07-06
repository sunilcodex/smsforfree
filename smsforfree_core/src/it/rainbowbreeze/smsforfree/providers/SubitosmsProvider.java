/**
 * 
 */
package it.rainbowbreeze.smsforfree.providers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.domain.SmsServiceCommand;
import it.rainbowbreeze.smsforfree.domain.SmsServiceParameter;
import it.rainbowbreeze.smsforfree.domain.SmsSingleProvider;

/**
 * @author rainbowbreeze
 *
 */
public class SubitosmsProvider
	extends SmsSingleProvider
{

	//---------- Ctors
	public SubitosmsProvider(ProviderDao dao)
	{
		super(dao, PARAM_NUMBER);
	}

	
	

	//---------- Private fields
	private final static int PARAM_NUMBER = 2;

	private final static int PARAM_INDEX_USERNAME = 0;
	private final static int PARAM_INDEX_PASSWORD = 1;

	public static final int COMMAND_CHECKCREDENTIALS = 1000;
	public static final int COMMAND_CHECKCREDITS = 1001;
	private static final int COMMAND_REGISTER = 1002;

	private static final int MSG_INDEX_REMAINING_CREDITS = 0;

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
    
	private List<SmsServiceCommand> mProviderSettingsActivityCommands;
	@Override
	public List<SmsServiceCommand> getSettingsActivityCommands()
	{ return mProviderSettingsActivityCommands; }
	
	
	
	
	//---------- Public methods
	@Override
	public ResultOperation<Void> initProvider(Context context)
	{
		mDictionary = new SubitosmsDictionary();

		setParameterDesc(PARAM_INDEX_USERNAME, context.getString(R.string.subitosms_username_desc));
		setParameterDesc(PARAM_INDEX_PASSWORD, context.getString(R.string.subitosms_password_desc));
		setParameterFormat(PARAM_INDEX_PASSWORD, SmsServiceParameter.FORMAT_PASSWORD);
		setDescription(context.getString(R.string.subitosms_description));
		
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
		
		mMessages = new String[1];
//		mMessages[MSG_INDEX_INVALID_CREDENTIALS] = context.getString(R.string.aimon_msg_invalidCredentials);
//		mMessages[MSG_INDEX_VALID_CREDENTIALS] = context.getString(R.string.aimon_msg_validCredentials);
//		mMessages[MSG_INDEX_SERVER_ERROR] = context.getString(R.string.aimon_msg_serverError);
		mMessages[MSG_INDEX_REMAINING_CREDITS] = context.getString(R.string.subitosms_msg_remainingCredits);
//		mMessages[MSG_INDEX_MESSAGE_SENT] = context.getString(R.string.aimon_msg_messageQueued);
//		mMessages[MSG_INDEX_MISSING_PARAMETERS] = context.getString(R.string.aimon_msg_missingParameters);
//		mMessages[MSG_INDEX_SERVICENANE_FREE_ANONYMOUS] = context.getString(R.string.aimon_serviceNameFreeAnonymous);
//		mMessages[MSG_INDEX_SERVICENANE_FREE_NORMAL] = context.getString(R.string.aimon_serviceNameFreeNormal);
//		mMessages[MSG_INDEX_SERVICENANE_ANONYMOUS] = context.getString(R.string.aimon_serviceNameAnonymous);
//		mMessages[MSG_INDEX_SERVICENANE_NORMAL] = context.getString(R.string.aimon_serviceNameNormal);
//		mMessages[MSG_INDEX_SERVICENANE_REPORT] = context.getString(R.string.aimon_serviceNameReport);
//		mMessages[MSG_INDEX_REMAINING_FREE_CREDITS] = context.getString(R.string.aimon_msg_remainingFreeCredits);
//		mMessages[MSG_INDEX_INVALID_SENDER] = context.getString(R.string.aimon_msg_invalidSender);
//		mMessages[MSG_INDEX_INVALID_DESTINATION] = context.getString(R.string.aimon_msg_invalidDestination);
//		mMessages[MSG_INDEX_EMPTY_MESSAGE] = context.getString(R.string.aimon_msg_emptyMessage);
//		mMessages[MSG_INDEX_INVALID_MESSAGE_ENCODING] = context.getString(R.string.aimon_msg_invalidMessageEncoding);
//		mMessages[MSG_INDEX_FREE_SMS_DAILY_LIMIT_REACHED] = context.getString(R.string.aimon_msg_freeSmsDailyLimitReached);
//		mMessages[MSG_INDEX_FREE_SMS_MONTHLY_LIMIT_REACHED] = context.getString(R.string.aimon_msg_freeSmsMonthlyLimitReached);
//		mMessages[MSG_INDEX_NOT_ENOUGH_FREE_SMS_CREDIT] = context.getString(R.string.aimon_msg_notEnoughFreeSmsCredit);
//		mMessages[MSG_INDEX_NOT_ENOUGH_CREDIT] = context.getString(R.string.aimon_msg_notEnoughCredit);
//		mMessages[MSG_INDEX_INVALID_MESSAGE_ENCODING_OR_TOO_LONG] = context.getString(R.string.aimon_msg_invalidMessageEncodingOrTooLong);
//		mMessages[MSG_INDEX_UNMANAGED_SERVER_ERROR] = context.getString(R.string.aimon_msg_unmanagedServerError);

		return super.initProvider(context);
	}

	
	@Override
	public ResultOperation<String> sendMessage(
			String serviceId,
			String destination,
			String body)
	{
		return null;
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
//		case COMMAND_CHECKCREDENTIALS:
//			res = verifyCredentials(currentUsername, currentPassword);
//			break;

		case COMMAND_CHECKCREDITS:
			res = verifyCredit(currentUsername, currentPassword);
			break;

		case COMMAND_REGISTER:
			res = registerToProvider(context, context.getString(R.string.aimon_registerLink));
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
	{ return GlobalDef.subitosmsParametersFileName; }
	
	
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
    	
		HashMap<String, String> params = mDictionary.getParametersForCreditCheck(username, password);
    	
    	//call the api that gets the credit
    	ResultOperation<String> res = doSingleHttpRequest(mDictionary.getBaseUrl(), null, params);
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

	private boolean parseReplyForErrors(ResultOperation<String> res) {
		// TODO Auto-generated method stub
		return true;
	}


	

}
