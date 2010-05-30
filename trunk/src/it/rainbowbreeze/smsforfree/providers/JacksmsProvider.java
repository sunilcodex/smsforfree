package it.rainbowbreeze.smsforfree.providers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.data.WebserviceClient;
import it.rainbowbreeze.smsforfree.domain.SmsConfigurableService;
import it.rainbowbreeze.smsforfree.domain.SmsMultiProvider;
import it.rainbowbreeze.smsforfree.domain.SmsProviderMenuCommand;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.domain.SmsServiceParameter;

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
		//TODO
		mTemplates = new ArrayList<SmsService>();
		
		SmsConfigurableService service;
		service = new SmsConfigurableService("62", "Aimon-Free", 112, 3);
		service.setParameterDesc(0, "Username di login (SOLO il nome, senza @aimon.it)");
		service.setParameterDesc(1, "Password di accesso");
		service.setParameterFormat(1, SmsServiceParameter.FORMAT_PASSWORD);
		service.setParameterDesc(2, "Mittente (senza pref internazionale)");
		mTemplates.add(service);
		service = new SmsConfigurableService("61", "Aimon", 612, 3);
		service.setParameterDesc(0, "Username di login (SOLO il nome, senza @aimon.it)");
		service.setParameterDesc(1, "Password di accesso");
		service.setParameterFormat(1, SmsServiceParameter.FORMAT_PASSWORD);
		service.setParameterDesc(2, "Mittente (senza pref internazionale)");
		mTemplates.add(service);
		service = new SmsConfigurableService("29", "VoipStunt", 160, 3);
		service.setParameterDesc(0, "Username di accesso su voipstunt.com");
		service.setParameterDesc(1, "Password di accesso su voipstunt.com");
		service.setParameterFormat(1, SmsServiceParameter.FORMAT_PASSWORD);
		service.setParameterDesc(2, "Numero verificato che verra' visualizzato come mittente");
		mTemplates.add(service);
		service = new SmsConfigurableService("1", "Vodafone-SMS", 360, 3);
		service.setParameterDesc(0, "Username di accesso a www.190.it");
		service.setParameterDesc(1, "Password di accesso a www.190.it");
		service.setParameterFormat(1, SmsServiceParameter.FORMAT_PASSWORD);
		service.setParameterDesc(2, "Inserisci il numero di telefono della sim con cui vuoi inviare tramite questo account.");
		mTemplates.add(service);
		service = new SmsConfigurableService("33", "Enel", 110, 2);
		service.setParameterDesc(0, "Username");
		service.setParameterDesc(1, "Password");
		service.setParameterFormat(1, SmsServiceParameter.FORMAT_PASSWORD);
		mTemplates.add(service);
		
		Collections.sort(mTemplates);

		return new ResultOperation("");
 
	}

	
	@Override
    public ResultOperation sendMessage(
    		String serviceId,
    		String destination,
    		String message)
    {
    	String jackSmsUsername = getParameterValue(PARAM_INDEX_USERNAME);
    	String jackSmsPassword = getParameterValue(PARAM_INDEX_PASSWORD);
    	//args check
    	try {
    		checkCredentialsValidity(jackSmsUsername, jackSmsPassword);
    	} catch (IllegalArgumentException e) {
    		return new ResultOperation(e);
		}
    	
    	SmsService service = getSubservice(serviceId);
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
		
    	ResultOperation res;
		//exams the result
		if (reply.startsWith(JacksmsDictionary.RESULT_OK)) {
			//ok
			//break the reply
			res = new ResultOperation(mDictionary.getTextPartFromReply(reply));
		} else if (reply.startsWith(JacksmsDictionary.RESULT_ERROR)) {
			//some sort of error
			res = new ResultOperation(new Exception(mDictionary.getTextPartFromReply(reply)));
		} else {
			//captcha
			res = new ResultOperation(ResultOperation.RETURNCODE_CAPTCHA_REQUEST, reply);
		}
		return res;    	
    }

	@Override
	protected String getParametersFileName()
	{ return GlobalDef.jacksmsParametersFileName; }

	@Override
	protected String getTemplatesFileName()
	{ return GlobalDef.jacksmsmTemplatesFileName; }

	@Override
	protected String getSubservicesFileName()
	{ return GlobalDef.jacksmsSubservicesFileName; }

	@Override
	public ResultOperation executeCommand(int commandId, Bundle extraData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SmsProviderMenuCommand> getProviderSettingsActivityCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SmsProviderMenuCommand> getSubservicesListActivityCommandS() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasProviderSettingsActivityCommands() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasSubservicesListActivityCommands() {
		// TODO Auto-generated method stub
		return false;
	}

}
