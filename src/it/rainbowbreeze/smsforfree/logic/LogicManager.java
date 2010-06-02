/**
 * 
 */
package it.rainbowbreeze.smsforfree.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;

import it.rainbowbreeze.smsforfree.common.GlobalBag;
import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.providers.AimonProvider;
import it.rainbowbreeze.smsforfree.providers.JacksmsProvider;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class LogicManager {
	//---------- Ctors

	//---------- Private fields

	//---------- Public properties

	//---------- Public methods
	public static ResultOperation executeBeginTask(ContextWrapper context)
	{
		ResultOperation res = new ResultOperation(true);
		
		//load configurations
		AppPreferencesDao.instance().load(context);
		
		//check for application upgrade
		res = checkForUpgrade(context);
		
		//initialize provider list
		ProviderDao dao = new ProviderDao();
		GlobalBag.providerList = new ArrayList<SmsProvider>();
		
		//add aimon
		JacksmsProvider jackProv = new JacksmsProvider(dao, context);
		//TODO check errors
		jackProv.loadParameters(context);
		jackProv.loadTemplates(context);
		jackProv.loadSubservices(context);
		GlobalBag.providerList.add(jackProv);
	
		//add jacksms
		AimonProvider aimonProv = new AimonProvider(dao, context);
		aimonProv.loadParameters(context);
		GlobalBag.providerList.add(aimonProv);
		
		Collections.sort(GlobalBag.providerList);

		return res;
	}
	
	
	public static ResultOperation executeEndTast(ContextWrapper context)
	{
		ResultOperation res = new ResultOperation(true);

		return res;
	}


	/**
	 * Checks the expiration date of the application
	 * 
	 * @return true if the app is valid, false and RETURNCODE_SMS_DAILY_LIMIT_OVERTAKED
	 *         return code if app is invalid
	 */
	public static boolean checkIfAppIsValid()
	{
		//get installation time
	    long installTime = AppPreferencesDao.instance().getInstallationTime();
		
		// get the current time
	    final Calendar c = Calendar.getInstance();
	    long currentTime = c.getTimeInMillis();

	    //find the maximum valid time interval
	    //60 days
	    long maxGap = 60 * 86400000;
	    maxGap = 86400000;

	    //if the difference between the two dates is greater than the max allowed gap
	    return (currentTime - installTime <= maxGap);
	}


	
	
	
	//---------- Private methods

	/**
	 * Checks if some upgrade is needed between current version of the
	 * application and the previous one
	 * 
	 *  @return true if all ok, otherwise false
	 */
	private static ResultOperation checkForUpgrade(ContextWrapper context)
	{
		String currentAppVersion = AppPreferencesDao.instance().getAppVersion();
		
		if (!GlobalDef.appVersion.equals(currentAppVersion)) {
			//perform upgrade
			
			//update expiration date
    	    final Calendar c = Calendar.getInstance();
			AppPreferencesDao.instance().setInstallationTime(c.getTimeInMillis());
			
			//update application version in the configuration
			AppPreferencesDao.instance().setAppVersion(GlobalDef.appVersion);
			
			//and save updates
			AppPreferencesDao.instance().save();
		}
		
		return new ResultOperation(true);
	}
	

}
