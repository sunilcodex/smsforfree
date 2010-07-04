package it.rainbowbreeze.smsforfree.domain;

import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.data.WebserviceClient;
import it.rainbowbreeze.smsforfree.ui.ActivityHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
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
	
	
	
	//---------- Public fields
	public static final String ERROR_NO_CREDENTIALS = "NO_CREDENTIALS";
	public static final String ERROR_NO_REPLY_FROM_SITE = "NO_REPLY";
	
	public static final String CAPTCHAREQUEST = "captcharequest";
	

	
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

    
    
    
	//---------- Public methods
    
    /**
     * Initialize provider
     */
    public abstract ResultOperation<Void> initProvider(Context context);
    
	/**
	 * Load provider's parameters
	 * 
	 * @param context
	 * @return
	 */
	public ResultOperation<Void> loadParameters(Context context){
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
	 * Create a new service object starting from a template
	 * 
	 * @param templateId
	 * @return
	 */
	public abstract SmsService newSubserviceFromTemplate(String templateId);

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
    	if (!TextUtils.isEmpty(number) &&
    			TextUtils.isDigitsOnly(number) &&
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
		if (TextUtils.isEmpty(number)) return number;
		
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
}