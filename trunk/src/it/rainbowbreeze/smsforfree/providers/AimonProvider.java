package it.rainbowbreeze.smsforfree.providers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.os.Bundle;
import android.text.TextUtils;

import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.data.WebserviceClient;
import it.rainbowbreeze.smsforfree.domain.SmsProviderMenuCommand;
import it.rainbowbreeze.smsforfree.domain.SmsServiceParameter;
import it.rainbowbreeze.smsforfree.domain.SmsSingleProvider;
import it.rainbowbreeze.smsforfree.util.Base64;

public class AimonProvider
	extends SmsSingleProvider
{
	//---------- Ctors
	public AimonProvider(ProviderDao dao)
	{
		this(dao,
				"Username", "Password", "Sender", "Id API (106:anonymous, 59:sender, 84:sender+report)",
				"Check credentials", "Check credits");
	}
	
	public AimonProvider(ProviderDao dao,
			String usernameDesc,
			String passwordDesc,
			String senderDesc,
			String kindofsmsDesc,
			String optionMenuCheckCredentialDesc,
			String optionMenuCheckCreditsDesc)
	{
		super(dao, PARAM_NUMBER);
		mDictionary = new AimonDictionary();
		setParameterDesc(PARAM_INDEX_USERNAME, usernameDesc);
		setParameterDesc(PARAM_INDEX_PASSWORD, passwordDesc);
		setParameterFormat(PARAM_INDEX_PASSWORD, SmsServiceParameter.FORMAT_PASSWORD);
		setParameterDesc(PARAM_INDEX_SENDER, senderDesc);
		setParameterDesc(PARAM_INDEX_ID_API, kindofsmsDesc);
		
		//initializes the command list
		mProviderSettingsMenuCommand = new ArrayList<SmsProviderMenuCommand>();
		SmsProviderMenuCommand command;
		command = new SmsProviderMenuCommand(
				COMMAND_CHECKCREDENTIALS, optionMenuCheckCredentialDesc, 1000);
		mProviderSettingsMenuCommand.add(command);
		command = new SmsProviderMenuCommand(
				COMMAND_CHECKCREDITS, optionMenuCheckCreditsDesc, 1001); 
		mProviderSettingsMenuCommand.add(command);
	}

	
	
	
	//---------- Private fields
	private final static int PARAM_NUMBER = 4;
	private final static int PARAM_INDEX_USERNAME = 0;
	private final static int PARAM_INDEX_PASSWORD = 1;
	private final static int PARAM_INDEX_SENDER = 2;
	private final static int PARAM_INDEX_ID_API = 3;

	private final static int COMMAND_CHECKCREDENTIALS = 1000;
	private final static int COMMAND_CHECKCREDITS = 1001;
	
	private AimonDictionary mDictionary;
	
	
	

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
	public int getMaxMessageLenght() {
		return 160;
	}
    
	@Override
	public boolean hasProviderSettingsActivityCommands() {
		return false;
	}

	private List<SmsProviderMenuCommand> mProviderSettingsMenuCommand;
	@Override
	public List<SmsProviderMenuCommand> getProviderSettingsActivityCommands() {
		return mProviderSettingsMenuCommand;
	}
	

	
	
    //---------- Public methods
    @Override
	public ResultOperation sendMessage(String serviceId, String destination, String body) {
		return sendSms(
				getParameterValue(PARAM_INDEX_USERNAME),
				getParameterValue(PARAM_INDEX_PASSWORD),
				getParameterValue(PARAM_INDEX_SENDER),
				destination,
				body,
				getParameterValue(PARAM_INDEX_ID_API));
	}

	@Override
    public ResultOperation executeCommand(int commandId, Bundle extraData)
	{
		ResultOperation res;
		
		switch (commandId) {
		case COMMAND_CHECKCREDENTIALS:
			res = verifyCredentials();
			break;

		case COMMAND_CHECKCREDITS:
			res = new ResultOperation("Not implemented");
			break;

		default:
			res = new ResultOperation("Nothing to execute");
		}

		return res;
	}

	
	//---------- Private methods
    /**
     * Send an sms
     */
	private ResultOperation sendSms(
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
    	} else {
        	if (TextUtils.isDigitsOnly(sender)) {
        		//sender is a phone number, add international prefix
        		okSender = "+39" + sender;
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
    		okDestination = "39" + destination;
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
    	ResultOperation res = doRequest(mDictionary.getUrlSendSms(), data);
    	
    	//check results
    	if (res.HasErrors()) return res;
    	
		//exams the result
		if (res.getResultAsString().startsWith(AimonDictionary.RESULT_SENDSMS_OK))
			//ok
			return res;
		else {
			//some sort of error
			res.setException(new Exception(res.getResultAsString()));
		}
		return res;    	
    	
    }


	/**
	 * Verifies how much credits the user has
	 * @param username
	 * @param password
	 * @return
	 */
    private ResultOperation verifyCredit(String username, String password)
    {
    	//args check
    	if (!checkCredentialsValidity(username, password))
    		return getExceptionForInvalidCredentials();
    	
    	HashMap<String, String> data = new HashMap<String, String>();
    	appendCredential(data, username, password);
    	
    	//verify the credit
    	return doRequest(mDictionary.getUrlGetCredit(), data);
    }


    /**
     * Verifies if username and password are correct
     * @return
     */
	private ResultOperation verifyCredentials() {
		ResultOperation res;
		
		//if i can obtain number of credits from aimon, username e password are correct
		res = verifyCredit(getParameterValue(PARAM_INDEX_USERNAME),
				getParameterValue(PARAM_INDEX_PASSWORD));
		
		//routes to caller method some error
		if (res.HasErrors())
			return res;
		
		String reply = res.getResultAsString();
		return new ResultOperation(!reply.startsWith(AimonDictionary.RESULT_ERROR_ACCESS_DENIED)); 
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
    private ResultOperation doRequest(String url, HashMap<String, String> data)
    {
    	String reply = "";
    	WebserviceClient client = new WebserviceClient();
    	
    	try {
    		reply = client.requestPost(url, data);
		} catch (ClientProtocolException e) {
			// TODO
			e.printStackTrace();
			return new ResultOperation(e);
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
			return new ResultOperation(e);
		}
    	
    	//empty reply
    	if (TextUtils.isEmpty(reply)) {
			return new ResultOperation(new Exception(ERROR_NO_REPLY_FROM_SITE));
		}

    	//return the reply
    	return new ResultOperation(reply);
    }

	@Override
	protected String getParametersFileName()
	{ return GlobalDef.aimonParametersFileName; }

	@Override
	protected String getTemplatesFileName()
	{ return null; }

	@Override
	protected String getSubservicesFileName()
	{ return null; }
}
