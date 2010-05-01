package it.rainbowbreeze.smsforfree.providers;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

import android.text.TextUtils;

import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.WebserviceClient;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;

public class JacksmsService
	extends SmsProvider
{
	//---------- Ctors
	public JacksmsService() {
		mDictionary = new JacksmsDictionary();
		mTemplateServices = new HashMap<Integer, JacksmsTemplateService>();
		mUserServices = new HashMap<Integer, JacksmsUserService>();
		//TODO
	}



	//---------- Private fields
	private JacksmsDictionary mDictionary;
	HashMap<Integer, JacksmsTemplateService> mTemplateServices;
	HashMap<Integer, JacksmsUserService> mUserServices;


	private String mJacksmsUsername;
	private String mJacksmsPassword;


	//---------- Public properties
    private static JacksmsService mInstance;
    public static JacksmsService instance()
    {
    	if (null == mInstance)
    		mInstance = new JacksmsService();
    	return mInstance;
    }
    

	@Override
	public String getServiceName() {
		return "JackSMS";
	}
	
	@Override
	public boolean hasConfigurations() {
		return true;
	}

	


	//---------- Public methods
	public void loadTemplateServices()
	{
		//TODO
		JacksmsTemplateService service;
		service = new JacksmsTemplateService(61, "Aimon", "1.4", 112, 112);
		mTemplateServices.put(service.getId(), service);
		service = new JacksmsTemplateService(62, "Aimon-Free", "1.2", 160, 612);
		mTemplateServices.put(service.getId(), service);
	}
	
	
	public void loadUserService()
	{
		JacksmsUserService service;
		service = new JacksmsUserService(1, 62, "XXXX", "XXXX", "XXXX", "");
		mUserServices.put(service.getServiceId(), service);
	}
	
	
	public void loadCredentials()
	{
		//TODO
		mJacksmsUsername = "guest";
		mJacksmsPassword = "guest";
	}


    
	public void saveCredentials(String newUsername, String newValue)
	{
		//TODO
		mJacksmsUsername = newUsername;
		mJacksmsPassword = newValue;
	}


    
    public ResultOperation sendSms(
    		int serviceId,
    		String destination,
    		String message)
    {
    	//args check
//    	try {
//    		checkIfCredentialsAreEmpty(username, password);
//    	} catch (IllegalArgumentException e) {
//    		return new ResultOperation(e);
//		}
    	
    	JacksmsUserService service = mUserServices.get(serviceId);
    	HashMap<String, String> headers = mDictionary.getHeaderForSendingMessage(service, destination, message);
    	doRequest(mDictionary.getUrlForSendingMessage(mJacksmsUsername, mJacksmsPassword), headers);
    	return null;
    }




	//---------- Private methods
    
    
    private ResultOperation doRequest(String url, HashMap<String, String> headers)
    {
    	String reply = "";
    	WebserviceClient client = new WebserviceClient();
    	
    	try {
    		reply = client.requestPost(url, headers, null);
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




}
