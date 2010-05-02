package it.rainbowbreeze.smsforfree.providers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.text.TextUtils;

import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.WebserviceClient;
import it.rainbowbreeze.smsforfree.domain.SmsConfigurableService;
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
	public ResultOperation loadAllServices()
	{
		ResultOperation res = new ResultOperation();

		//TODO
		mTemplateSubservices = new ArrayList<SmsService>();
		
		SmsConfigurableService service;
		service = new SmsConfigurableService("62", "Aimon-Free", 112, 2);
		mTemplateSubservices.add(service);
		service = new SmsConfigurableService("61", "Aimon", 612, 2);
		mTemplateSubservices.add(service);
		
		return res; 
	}
	
	
	public ResultOperation loadConfiguredServices()
	{
		ResultOperation res = new ResultOperation();

		//TODO
		mConfiguredSubservice = new ArrayList<SmsService>();

		
		return res; 
	}
	
	@Override
    public ResultOperation sendMessage(
    		String serviceId,
    		String destination,
    		String message)
    {
    	String jackSmsUsername = mParametersValue[PARAM_INDEX_USERNAME];
    	String jackSmsPassword = mParametersValue[PARAM_INDEX_PASSWORD];
    	//args check
    	try {
    		checkCredentialsValidity(jackSmsUsername, jackSmsPassword);
    	} catch (IllegalArgumentException e) {
    		return new ResultOperation(e);
		}
    	
    	SmsService service = getConfiguredSubservice(serviceId);
    	HashMap<String, String> headers = mDictionary.getHeaderForSendingMessage(service, destination, message);
    	return doRequest(mDictionary.getUrlForSendingMessage(jackSmsUsername, jackSmsPassword), headers);
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
		if (reply.startsWith(JacksmsDictionary.RESULT_OK))
			//ok
			res.setResultAsString(reply);
		else {
			//some sort of error
			res.setException(new Exception(reply));
		}
		return res;    	
    }


}
