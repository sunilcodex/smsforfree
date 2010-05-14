package it.rainbowbreeze.smsforfree.domain;

import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.ProviderDao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
	public static final String ERROR_NO_USERNAME = "NO_USERNAME";
	public static final String ERROR_NO_PASSWORD = "NO_PASSWORD";
	public static final String ERROR_CREDENTIALS_NOT_VALID = "CREDENTIALS_INVALID";
	public static final String ERROR_NO_REPLY_FROM_SITE = "NO_REPLY";
	
	/** Id for a new service */
	public static final String NEWSUBSERVICEID = "-1";
	


	//---------- Public properties
	
	/** Has this provider sub-services? */
	public abstract boolean hasSubServices();
	
	public abstract List<SmsService> getAllTemplate();
	public abstract SmsService getTemplate(String templateId);

	public abstract List<SmsService> getAllSubservices();
	public abstract SmsService getSubservice(String subserviceId);

    public abstract void setSelectedSubservice(String subserviceId);
	
	
	//---------- Public methods
	/**
	 * Send the message
	 * 
	 * @param destination
	 * @param body
	 */
	public abstract ResultOperation sendMessage(String serviceId, String destination, String body);
	
	
	public ResultOperation loadParameters(Context context){
		ResultOperation res;
		FileInputStream fis = null;
		
		res = new ResultOperation();
		
		//checks if file exists
		File file = context.getFileStreamPath(getParametersFileName());
		if (!file.exists()) {
			//no parameters for the provide
			res.setResultAsBoolean(false);
			return res;
		}
		
		try {
			fis = context.openFileInput(getParametersFileName());
			res = mDao.loadProviderParameters(fis, this);
		} catch (FileNotFoundException e) {
			res.setException(e);
		} finally {
			if (null != fis) {
				try {
					fis.close();
					fis = null;
				} catch (IOException e) {
					res.setException(e);
				}
			}
		}
		if (res.HasErrors()) return res;
		
		//checks for errors
		if (res.HasErrors()) return res;
		
		//all went good, no errors to return
		res.setResultAsBoolean(true);
		return res;
	}
	
	public ResultOperation saveParameters(Context context){
		ResultOperation res = mDao.saveProviderParameters(context, getParametersFileName(), this);
		return res;
	}

	public abstract ResultOperation saveTemplates(Context context);

	public abstract ResultOperation loadTemplates(Context context);
	
	public abstract ResultOperation saveSubservices(Context context);

	public abstract ResultOperation loadSubservices(Context context);
	
	public abstract SmsService newSubserviceFromTemplate(String templateId);


	
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
	protected void checkCredentialsValidity(String username, String password)
		throws IllegalArgumentException
	{
    	if (TextUtils.isEmpty(username))
    		 throw new IllegalArgumentException(ERROR_NO_USERNAME);
    	if (TextUtils.isEmpty(password))
    		throw new IllegalArgumentException(ERROR_NO_PASSWORD);
	}

}
