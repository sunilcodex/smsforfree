package it.rainbowbreeze.smsforfree.providers;

import android.content.Context;
import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.domain.SmsServiceParameter;
import it.rainbowbreeze.smsforfree.domain.SmsSingleProvider;

public class VoipstuntProvider
	extends SmsSingleProvider
{
	//---------- Ctors
	public VoipstuntProvider(ProviderDao dao, Context context)
	{
		super(dao, PARAM_NUMBER);
		
		setParameterDesc(PARAM_INDEX_USERNAME, context.getString(R.string.voipstunt_username));
		setParameterDesc(PARAM_INDEX_PASSWORD, context.getString(R.string.voipstunt_password));
		setParameterFormat(PARAM_INDEX_PASSWORD, SmsServiceParameter.FORMAT_PASSWORD);
		setParameterDesc(PARAM_INDEX_SENDER, context.getString(R.string.voipstunt_sender));
		setDescription(context.getString(R.string.voipstunt_description));

		//save some messages
		mMessages = new String[1];
		mMessages[MSG_INDEX_MESSAGE_SENT] = context.getString(R.string.voipstunt_msg_messageSent);
	}




	//---------- Private fields
	private final static int PARAM_NUMBER = 3;
	private final static int PARAM_INDEX_USERNAME = 0;
	private final static int PARAM_INDEX_PASSWORD = 1;
	private final static int PARAM_INDEX_SENDER = 2;
	
	private final static int MSG_INDEX_MESSAGE_SENT = 0;
	
	
	private String[] mMessages;
	
	private final static String VOIPSTUNT_BASE_URL = "https://www.voipstunt.com/myaccount/sendsms.php";

	
	
	
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
		
		//check for destination number and, eventually, add internation prefix
		
		StringBuilder sb = new StringBuilder();
		sb.append(VOIPSTUNT_BASE_URL)
			.append("?")
			.append("username=")
			.append(getParameterValue(PARAM_INDEX_USERNAME))
			.append("&")
			.append("password=")
			.append(getParameterValue(PARAM_INDEX_PASSWORD))
			.append("&")
			.append("from=")
			.append(getParameterValue(PARAM_INDEX_SENDER))
			.append("&")
			.append("to=")
			.append(destination)
			.append("&")
			.append("text=")
			.append(body);
			

		ResultOperation<String> res = doRequest(sb.toString(), null, null);

    	//checks for applications errors
    	if (res.HasErrors()) return res;
    	
    	//examine it the return contains confirmation if the message was sent
		if (res.getResult().startsWith(AimonDictionary.RESULT_SENDSMS_OK)) {
			res.setResult(String.format(
					mMessages[MSG_INDEX_MESSAGE_SENT], res.getResult()));
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
