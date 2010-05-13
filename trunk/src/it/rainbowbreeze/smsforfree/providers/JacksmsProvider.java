package it.rainbowbreeze.smsforfree.providers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.text.TextUtils;

import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.data.WebserviceClient;
import it.rainbowbreeze.smsforfree.domain.SmsConfigurableService;
import it.rainbowbreeze.smsforfree.domain.SmsMultiProvider;
import it.rainbowbreeze.smsforfree.domain.SmsService;

public class JacksmsProvider
	extends SmsMultiProvider
{
	//---------- Ctors
	public JacksmsProvider(ProviderDao dao)
	{
		this(dao, "Username", "Password");
	}
	
	public JacksmsProvider(ProviderDao dao, String usernameDesc, String passwordDesc)
	{
		super(dao, PARAM_NUMBER);
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
	@Override
	public ResultOperation loadTemplates(Context context)
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
	
	
	@Override
	public ResultOperation loadSubservices(Context context)
	{
		ResultOperation res = new ResultOperation();

		//TODO
		mConfiguredSubservice = new ArrayList<SmsService>();
		SmsConfigurableService service;
		service = new SmsConfigurableService("1", "62", "Aimon-Free", 112, 4);
		service.setParameterValue(0, "rainbowbreeze");
		service.setParameterValue(1, "XXXXXX");
		service.setParameterValue(2, "3921234567");
		mConfiguredSubservice.add(service);
		service = new SmsConfigurableService("2", "61", "Aimon", 612, 4);
		service.setParameterValue(0, "rainbowbreeze");
		service.setParameterValue(1, "XXXXXX");
		service.setParameterValue(2, "3921234567");
		mConfiguredSubservice.add(service);
		
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

	@Override
	protected String getParametersFileName()
	{ return GlobalDef.JacksmsParametersFileName; }

	@Override
	protected String getTemplatesFileName()
	{ return GlobalDef.JacksmsmTemplatesFileName; }

	@Override
	protected String getSubservicesFileName()
	{ return GlobalDef.JacksmsSubservicesFileName; }

}
