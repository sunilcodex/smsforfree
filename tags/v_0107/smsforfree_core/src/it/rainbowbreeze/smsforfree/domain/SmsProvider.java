/**
 * Copyright (C) 2010 Alfredo Morresi
 * 
 * This file is part of SmsForFree project.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/>.
 */

package it.rainbowbreeze.smsforfree.domain;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.data.WebserviceClient;
import it.rainbowbreeze.smsforfree.ui.ActivityHelper;
import it.rainbowbreeze.smsforfree.util.Base64;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;


/**
 * Base provider for sms service
 * 
 *  Each service could have more "sub-services" (for example, the service act as a
 *  unified "proxy" for other services)
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public abstract class SmsProvider
	extends SmsService
{
	//---------- Ctors
	protected SmsProvider(ProviderDao dao, int numberOfParameters)
	{
		super(numberOfParameters);
		mDao = dao;
	}

	
	
	
	//---------- Private fields
	protected ProviderDao mDao;
	protected WebserviceClient mWebserviceClient;

	//command used to register to provider
	protected final static int COMMAND_REGISTER = 10000;
	
	
	
	//---------- Public fields
	



	//---------- Public properties
	
	/** Has this provider sub-services? */
	public abstract boolean hasSubServices();
	
	/** Has this provider sub-services to configure? */
	public abstract boolean hasSubServicesToConfigure();
	
	public abstract boolean hasTemplatesConfigured();
	public abstract List<SmsService> getAllTemplates();
	public abstract SmsService getTemplate(String templateId);

	public abstract List<SmsService> getAllSubservices();
	public abstract SmsService getSubservice(String subserviceId);

    public abstract void setSelectedSubservice(String subserviceId);

	/** Menu to show on option menu of ActSubservicesList activity */
    public abstract List<SmsServiceCommand> getSubservicesListActivityCommands();

    /** List of provider's commands to show in the provider's settings activity  */
    protected List<SmsServiceCommand> mProviderSettingsActivityCommands;
	@Override
	public List<SmsServiceCommand> getSettingsActivityCommands()
	{ return mProviderSettingsActivityCommands; }
    
    
    
	//---------- Public methods
    
    /**
     * Base initialization for the provider, overrides in providers to
     * add additional initialization logic
     * 
     * @param context
     */
    public ResultOperation<Void> initProvider(Context context) {
		ResultOperation<Void> res;

		res = loadParameters(context);
		if (res.hasErrors()) return res;

		mProviderSettingsActivityCommands = loadSettingsActivityCommands(context);
    	
    	return res;
    }
    
	/**
	 * Load provider's parameters
	 * 
	 * @param context
	 * @return
	 */
	public ResultOperation<Void> loadParameters(Context context) {
		return mDao.loadProviderParameters(context, getParametersFileName(), this);
	}
	
	/**
	 * Save provider's parameters
	 * 
	 * @param context
	 * @return
	 */
	public ResultOperation<Void> saveParameters(Context context){
		return mDao.saveProviderParameters(context, getParametersFileName(), this);
	}

	/**
	 * Save provider's configured subservices
	 * 
	 * @param context
	 * @return
	 */
	public abstract ResultOperation<Void> saveSubservices(Context context);
	
	/**
	 * Create a new service object starting from a template or integrate an existing service data
	 * with the info from the template
	 * 
	 * @param originalService the service to integrate, null if a new service must be created
	 * @param templateId
	 * @return
	 */
	public abstract SmsService integrateSubserviceWithTemplateData(SmsConfigurableService originalService, String templateId);

	/**
	 * Checks if services has all mandatory parameters configured
	 * @param serviceId
	 * @return
	 */
	public abstract boolean hasServiceParametersConfigured(String serviceId);

	
	/**
	 * Provider has additional command to show on option menu of
	 * ActSubservicesList activity
	 */
    public boolean hasSubservicesListActivityCommands()
    {
    	List<SmsServiceCommand> commands = getSubservicesListActivityCommands();
    	if (null == commands) return false;
    	return commands.size() > 0;
    }
    
    /**
     * Find the subservice index in the provider's subservice list
     * 
     * @param subserviceId
     * @return
     */
    public int findSubservicePositionInList(String subserviceId)
    {
		if (TextUtils.isEmpty(subserviceId)) return -1;
		
		List<SmsService> subservices = getAllSubservices();
		if (null == subservices) return -1;
		for(int i = 0; i < subservices.size(); i++) {
			if (subserviceId.equals(subservices.get(i).getId())) return i;
		}
		return -1;
    }
    
	
	/**
	 * Sends the message
	 * 
	 * @param destination
	 * @param body
	 */
	public abstract ResultOperation<String> sendMessage(String serviceId, String destination, String body);

    /**
     * Get the captcha image content from a provider reply
     * 
     * @param providerReply message from the provider used to retrieve the captcha content
     * @return
     */
    public abstract ResultOperation<Object> getCaptchaContentFromProviderReply(String providerReply);
    
    /**
     * Send a captcha code to the provider
     * 
     * @param providerReply original message from the provider used to retrieve
     *   the captcha content
     * @param captchaCode the captcha code read
     * @return
     */
    public abstract ResultOperation<String> sendCaptcha(String providerReply, String captchaCode);


    /**
     * Execute the command action
     * 
     * @param commandId
     * @param context
     * @param extraData
     */
    @Override
    public ResultOperation<String> executeCommand(
    		int commandId,
    		Context context,
    		Bundle extraData)
	{
		ResultOperation<String> res;
    	
		//execute commands
		switch (commandId) {

		case COMMAND_REGISTER:
			res = registerToProvider(context, getProviderRegistrationUrl(context));
			break;

		default:
			res = super.executeCommand(commandId, context, extraData);
		}

		return res;
    }
    
    
	//---------- Private methods
	
	/** file name where save provider parameters */
	protected abstract String getParametersFileName();
	
	/** file name where save subservices templates */
	protected abstract String getTemplatesFileName();

	/** file name where save provider subservices */
	protected abstract String getSubservicesFileName();
	
	/**
	 * Load provider's templates
	 * 
	 * @param context
	 * @return
	 */
	protected abstract ResultOperation<Void> saveTemplates(Context context);

	/**
	 * Save provider's templates
	 * 
	 * @param context
	 * @return
	 */
	protected abstract ResultOperation<Void> loadTemplates(Context context);

	/**
	 * Load provider's configured subservices
	 * 
	 * @param context
	 * @return
	 */
	protected abstract ResultOperation<Void> loadSubservices(Context context);
	
    /**
     * Return the url to use for provider's new account registration
     * @param context
     * @return
     */
	protected abstract String getProviderRegistrationUrl(Context context);

	/**
	 * Checks if username and password are not empty
	 * Throws an error if one of the two are not correct
	 * 
	 * @param username
	 * @param password
	 */
	protected boolean checkCredentialsValidity(String username, String password)
	{
		return !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password);
	}
	
	/**
	 * Create an exception for empty username or password
	 * @return
	 */
	protected ResultOperation<String> getExceptionForInvalidCredentials()
	{
		return new ResultOperation<String>(new Exception(), ResultOperation.RETURNCODE_ERROR_NOCREDENTIAL);
	}
	

	/**
	 * Create the list of commands to show in the provider's settings activity
	 * @param context
	 * @return
	 */
	protected List<SmsServiceCommand> loadSettingsActivityCommands(Context context)
	{
		//initializes the command list
		List<SmsServiceCommand> commands = new ArrayList<SmsServiceCommand>();
		
		SmsServiceCommand command;
		//register to provider command
		command = new SmsServiceCommand(
				COMMAND_REGISTER, 
				String.format(context.getString(R.string.actsettingssmsservice_mnuRegisterToProvider), getName()),
				1,
				R.drawable.ic_menu_invite); 
		commands.add(command);
		
		return commands;
	}

	
    

	/**
	 * Execute the http request as single request, without past state
	 * 
	 * @param url
	 * @param headers
	 * @param parameters
	 * @return
	 */
    protected ResultOperation<String> doSingleHttpRequest(
    		String url,
    		HashMap<String, String> headers,
    		HashMap<String, String> parameters
		)
    {
    	WebserviceClient client = new WebserviceClient();
    	return doHttpRequest(url, headers, parameters, client);
    }
    
    
    /**
     * Start a new http conversation 
     * 
     * @return host for the conversation
     */
    protected WebserviceClient startConversation()
    {
    	//close a previous conversation, if it exists;
    	if (null != mWebserviceClient) {
    		endConversation();
    	}
    	
    	mWebserviceClient = new WebserviceClient();
    	mWebserviceClient.startConversation();
    	return mWebserviceClient;
    }

	/**
	 * Add a new http request in the current conversation
	 * 
	 * @param url
	 * @param headers
	 * @param parameters
	 * @return
	 */
    protected ResultOperation<String> doConversationHttpRequest(
    		String url,
    		HashMap<String, String> headers,
    		HashMap<String, String> parameters
		)
    {
    	if (null == mWebserviceClient) startConversation();
    	return doHttpRequest(url, headers, parameters, mWebserviceClient);
    }

    
    /**
     * End an http conversation
     */
	protected void endConversation()
	{
		mWebserviceClient.endConversation();
		mWebserviceClient = null;
	}

	
	/**
     * Execute single or conversation http request, depends on the kind of
     * WebserviceClient used as parameter
     * 
     * @param url
     * @param headers
     * @param parameters
     * @param client
     * @return
     */
	private ResultOperation<String> doHttpRequest(
			String url,
			HashMap<String, String> headers,
			HashMap<String, String> parameters,
			WebserviceClient client)
	{
    	String reply = "";
		
		try {
    		reply = client.requestPost(url, headers, parameters);
		} catch (IllegalArgumentException e) {
			return new ResultOperation<String>(e, ResultOperation.RETURNCODE_ERROR_APPLICATION_ARCHITECTURE);
		} catch (ClientProtocolException e) {
			return new ResultOperation<String>(e, ResultOperation.RETURNCODE_ERROR_COMMUNICATION);
		} catch (IOException e) {
			return new ResultOperation<String>(e, ResultOperation.RETURNCODE_ERROR_COMMUNICATION);
		}
    	
    	//empty reply
    	if (TextUtils.isEmpty(reply)) {
			return new ResultOperation<String>(new Exception(), ResultOperation.RETURNCODE_ERROR_EMPTY_REPLY);
		}

    	//return the reply
    	return new ResultOperation<String>(reply);
	}    
    
    
    /**
     * Append to destination number the default international prefix
     * 
     * @param number
     * @return
     */
    protected String transalteInInternationalFormat(String number)
    {
    	String defaultPrefix = AppPreferencesDao.instance().getDefaultInternationalPrefix();
    	return transalteInInternationalFormat(number, defaultPrefix);
	}
    
    /**
     * Append to destination number the specified international prefix
     * 
     * @param number
     * @param internationalPrefix
     * @return
     */
    protected String transalteInInternationalFormat(String number, String internationalPrefix)
    {
    	String finalNumber;
    	if (TextUtils.isDigitsOnly(number) &&
			!number.substring(0, 1).equals("+")) {
    		//append prefix to number
    		finalNumber = internationalPrefix + number;
    	} else {
    		finalNumber =  number;
    	}
    	
    	return finalNumber;
    }


    /**
     * Remove the international prefix from a phone number, if it's present
     * @param number
     * @return
     */
	protected String removeInternationalPrefix(String number)
	{
    	String defaultPrefix = AppPreferencesDao.instance().getDefaultInternationalPrefix();
    	return removeInternationalPrefix(number, defaultPrefix);
	}

	/**
     * Remove the international prefix from a phone number, if it's present
     * @param number
     * @return
     */
	protected String removeInternationalPrefix(String number, String internationalPrefix)
	{
		String finalNumber = number;

		//sender number starts with international prefix
		if (number.substring(0, 1).equals("+")) {
			//check if number match with international default prefix
    		if (number.startsWith(internationalPrefix)) {
    			//crop international prefix
    			finalNumber = number.substring(internationalPrefix.length());
			}
		}
    	
    	return finalNumber;
	}

	
	/**
	 * Launch the browser activity with a personalized link for subscribe to provider
	 * @param context
	 * @return
	 */
	protected ResultOperation<String> registerToProvider(Context context, String urlToOpen)
	{
		ActivityHelper.openBrowser(context, urlToOpen, true);
		return new ResultOperation<String>();
	}
	

	/**
	 * Write headers and url in the log, so a better debugger could be made
	 * when a report is sent to the developer
	 * 
	 * @param url
	 * @param headers
	 */
	protected void logRequest(String url, HashMap<String, String> headers, HashMap<String, String> parameters)
	{
		LogFacility.e(getName() + " provider request content");
		if (!TextUtils.isEmpty(url)) {
			LogFacility.e("Url");
			LogFacility.e(Base64.encodeBytes(url.getBytes()));
		}
		if (null != headers) {
			LogFacility.e("Headers");
			for (Entry<String, String> header : headers.entrySet()) {
				LogFacility.e(Base64.encodeBytes(header.getKey().getBytes()));
				LogFacility.e(Base64.encodeBytes(header.getValue().getBytes()));
			}
		}
		if (null != parameters) {
			LogFacility.e("Parameters");
			for (Entry<String, String> param : parameters.entrySet()) {
				LogFacility.e(Base64.encodeBytes(param.getKey().getBytes()));
				LogFacility.e(Base64.encodeBytes(param.getValue().getBytes()));
			}
		}
	}

	
}