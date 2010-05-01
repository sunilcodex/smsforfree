package it.rainbowbreeze.smsforfree.providers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.text.TextUtils;

import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.WebserviceClient;
import it.rainbowbreeze.smsforfree.domain.SmsMultiProvider;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsService;

public class JacksmsProvider
	extends SmsMultiProvider
{
	//---------- Ctors
	public JacksmsProvider()
	{
		this("Username", "Password");
	}
	
	public JacksmsProvider(String usernameDesc, String passwordDesc)
	{
		super(PARAM_NUMBER);
		mInstance = this;
		mDictionary = new JacksmsDictionary();
		mParametersDesc[PARAM_INDEX_USERNAME] = usernameDesc;
		mParametersDesc[PARAM_INDEX_PASSWORD] = passwordDesc;
	}
	
	
	

	//---------- Private fields
	private final static int PARAM_NUMBER = 2;
	private final static int PARAM_INDEX_USERNAME = 0;
	private final static int PARAM_INDEX_PASSWORD = 1;
	
	private JacksmsDictionary mDictionary;
	


	//---------- Public properties
    private static JacksmsProvider mInstance;
    public static JacksmsProvider instance()
    {
    	if (null == mInstance)
    		mInstance = new JacksmsProvider();
    	return mInstance;
    }

	@Override
	public String getId()
	{ return "JackSMS"; }

	@Override
	public String getName()
	{ return "JackSMS"; }
	
	@Override
	public int getParametersNumber()
	{ return PARAM_NUMBER; }



	


	//---------- Public methods
//	public void loadSubServices()
//	{
//		//TODO
//		mAllSubservice = new ArrayList<SmsSubService>();
//		
//		SmsSubService service;
//		service = new SmsSubService(61, "Aimon", "1.4", 112, 112);
//		mAllSubservice.put(service.getId(), service);
//		service = new JacksmsTemplateService(62, "Aimon-Free", "1.2", 160, 612);
//		mTemplateServices.put(service.getId(), service);
//	}
//	
//	
//	public void loadUserService()
//	{
//		JacksmsUserService service;
//		service = new JacksmsUserService(1, 62, "XXXX", "XXXX", "XXXX", "");
//		mUserServices.put(service.getServiceId(), service);
//	}
//	
//	
//	public void loadCredentials()
//	{
//		//TODO
//		mJacksmsUsername = "guest";
//		mJacksmsPassword = "guest";
//	}
//
//
//    
//	public void saveCredentials(String newUsername, String newValue)
//	{
//		//TODO
//		mJacksmsUsername = newUsername;
//		mJacksmsPassword = newValue;
//	}
//
//
//    
//    public ResultOperation sendSms(
//    		int serviceId,
//    		String destination,
//    		String message)
//    {
//    	//args check
////    	try {
////    		checkIfCredentialsAreEmpty(username, password);
////    	} catch (IllegalArgumentException e) {
////    		return new ResultOperation(e);
////		}
//    	
//    	JacksmsUserService service = mUserServices.get(serviceId);
//    	HashMap<String, String> headers = mDictionary.getHeaderForSendingMessage(service, destination, message);
//    	doRequest(mDictionary.getUrlForSendingMessage(mJacksmsUsername, mJacksmsPassword), headers);
//    	return null;
//    }
//
//
//
//
//	//---------- Private methods
//    
//    
//    private ResultOperation doRequest(String url, HashMap<String, String> headers)
//    {
//    	String reply = "";
//    	WebserviceClient client = new WebserviceClient();
//    	
//    	try {
//    		reply = client.requestPost(url, headers, null);
//		} catch (ClientProtocolException e) {
//			// TODO
//			e.printStackTrace();
//			return new ResultOperation(e);
//		} catch (IOException e) {
//			// TODO
//			e.printStackTrace();
//			return new ResultOperation(e);
//		}
//    	
//    	//empty reply
//    	if (TextUtils.isEmpty(reply)) {
//			return new ResultOperation(new Exception(ERROR_NO_REPLY_FROM_SITE));
//		}
//		
//    	ResultOperation res = new ResultOperation();
//		//exams the result
//		if (reply.startsWith(AimonDictionary.RESULT_OK))
//			//ok
//			res.setResultAsString(reply);
//		else {
//			//some sort of error
//			res.setException(new Exception(reply));
//		}
//		return res;    	
//    }





}
