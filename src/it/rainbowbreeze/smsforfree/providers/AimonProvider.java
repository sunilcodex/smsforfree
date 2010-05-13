package it.rainbowbreeze.smsforfree.providers;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

import android.text.TextUtils;

import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.data.WebserviceClient;
import it.rainbowbreeze.smsforfree.domain.SmsSingleProvider;
import it.rainbowbreeze.smsforfree.util.Base64;

public class AimonProvider
	extends SmsSingleProvider
{
	//---------- Ctors
	public AimonProvider(ProviderDao dao)
	{
		this(dao, "Username", "Password", "Sender", "Id API (106:anonymous, 59:sender, 84:sender+report)");
	}
	
	public AimonProvider(ProviderDao dao, String usernameDesc, String passwordDesc, String senderDesc, String kindofsmsDesc)
	{
		super(dao, PARAM_NUMBER);
		mDictionary = new AimonDictionary();
		mParametersDesc[PARAM_INDEX_USERNAME] = usernameDesc;
		mParametersDesc[PARAM_INDEX_PASSWORD] = passwordDesc;
		mParametersDesc[PARAM_INDEX_SENDER] = senderDesc;
		mParametersDesc[PARAM_INDEX_ID_API] = kindofsmsDesc;
	}

	
	
	
	//---------- Private fields
	private final static int PARAM_NUMBER = 4;
	private final static int PARAM_INDEX_USERNAME = 0;
	private final static int PARAM_INDEX_PASSWORD = 1;
	private final static int PARAM_INDEX_SENDER = 2;
	private final static int PARAM_INDEX_ID_API = 3;

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

    
    
    //---------- Public methods
    public ResultOperation verifyCredit(String username, String password)
    {
    	//args check
    	try {
    		checkCredentialsValidity(username, password);
    	} catch (IllegalArgumentException e) {
    		return new ResultOperation(e);
		}
    	
    	HashMap<String, String> data = new HashMap<String, String>();
    	appendCredential(data, username, password);
    	
    	//verify the credit
    	return doRequest(mDictionary.getUrlGetCredit(), data);
    }


    @Override
	public ResultOperation sendMessage(String serviceId, String destination, String body) {
		// TODO Auto-generated method stub
		return sendSms(
				mParametersValue[PARAM_INDEX_USERNAME],
				mParametersValue[PARAM_INDEX_PASSWORD],
				mParametersValue[PARAM_INDEX_SENDER],
				destination,
				body,
				mParametersValue[PARAM_INDEX_ID_API]);
	}
    
    
    
	//---------- Private methods
	private ResultOperation sendSms(
    		String username,
    		String password,
    		String sender,
    		String destination,
    		String body,
    		String idApi)
    {
    	//args check
    	try {
    		checkCredentialsValidity(username, password);
    	} catch (IllegalArgumentException e) {
    		return new ResultOperation(e);
		}
    	
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
    	return doRequest(mDictionary.getUrlSendSms(), data);
    }


	private void appendCredential(HashMap<String, String> data, String username, String password)
    {
    	data.put(AimonDictionary.PARAM_USERNAME, username);
    	data.put(AimonDictionary.PARAM_PASSWORD, password);
    }
    
    
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
		
    	ResultOperation res = new ResultOperation();
		//exams the result
		if (reply.startsWith(AimonDictionary.RESULT_OK))
			//ok
			res.setResultAsString(reply);
		else {
			//some sort of error
			res.setException(new Exception(reply));
		}
		return res;    	
    }

	@Override
	protected String getParametersFileName()
	{ return GlobalDef.AimonParametersFileName; }

	@Override
	protected String getTemplatesFileName()
	{ return null; }

	@Override
	protected String getSubservicesFileName()
	{ return null; }
	
}
