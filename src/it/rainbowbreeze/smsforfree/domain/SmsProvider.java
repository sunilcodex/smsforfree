package it.rainbowbreeze.smsforfree.domain;

import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.ProviderDao;

import java.util.List;

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
	ProviderDao mDao;
	
	
	
	//---------- Public fields
	public static final String ERROR_NO_CREDENTIALS = "NO_CREDENTIALS";
	public static final String ERROR_NO_REPLY_FROM_SITE = "NO_REPLY";
	
	public static final String CAPTCHAREQUEST = "captcharequest";
	

	
	//---------- Public properties
	
	/** Has this provider sub-services? */
	public abstract boolean hasSubServices();
	
	public abstract List<SmsService> getAllTemplate();
	public abstract SmsService getTemplate(String templateId);

	public abstract List<SmsService> getAllSubservices();
	public abstract SmsService getSubservice(String subserviceId);

    public abstract void setSelectedSubservice(String subserviceId);

	/** Menu to show on option menu of ActSubservicesList activity */
    public abstract List<SmsServiceCommand> getSubservicesListActivityCommands();

    
    
    
	//---------- Public methods
	/**
	 * Load provider's parameters
	 * 
	 * @param context
	 * @return
	 */
	public ResultOperation loadParameters(Context context){
		return mDao.loadProviderParameters(context, getParametersFileName(), this);
	}
	
	/**
	 * Save provider's parameters
	 * 
	 * @param context
	 * @return
	 */
	public ResultOperation saveParameters(Context context){
		return mDao.saveProviderParameters(context, getParametersFileName(), this);
	}

	/**
	 * Load provider's templates
	 * 
	 * @param context
	 * @return
	 */
	public abstract ResultOperation saveTemplates(Context context);

	/**
	 * Save provider's templates
	 * 
	 * @param context
	 * @return
	 */
	public abstract ResultOperation loadTemplates(Context context);
	
	/**
	 * Save provider's configured subservices
	 * 
	 * @param context
	 * @return
	 */
	public abstract ResultOperation saveSubservices(Context context);

	/**
	 * Load provider's configured subservices
	 * 
	 * @param context
	 * @return
	 */
	public abstract ResultOperation loadSubservices(Context context);
	
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
	 * Sends the message
	 * 
	 * @param destination
	 * @param body
	 */
	public abstract ResultOperation sendMessage(String serviceId, String destination, String body);

    /**
     * Get the captcha image content from a provider reply
     * 
     * @param providerReply message from the provider used to retrieve the captcha content
     * @return
     */
    public abstract ResultOperation getCaptchaContentFromProviderReply(String providerReply);
    
    /**
     * Send a captcha code to the provider
     * 
     * @param providerReply original message from the provider used to retrieve
     *   the captcha content
     * @param captchaCode the captcha code read
     * @return
     */
    public abstract ResultOperation sendCaptcha(String providerReply, String captchaCode);
    
    
	//---------- Private methods
	
	/** file name where save provider parameters */
	protected abstract String getParametersFileName();
	
	/** file name where save subservices templates */
	protected abstract String getTemplatesFileName();

	/** file name where save provider subservices */
	protected abstract String getSubservicesFileName();
	
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
	protected ResultOperation getExceptionForInvalidCredentials()
	{
		return new ResultOperation(new Exception(ERROR_NO_CREDENTIALS));
	}  

}
