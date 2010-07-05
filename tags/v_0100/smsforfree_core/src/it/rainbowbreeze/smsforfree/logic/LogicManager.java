/**
 * 
 */
package it.rainbowbreeze.smsforfree.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import android.content.Context;
import android.text.TextUtils;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.common.SmsForFreeApplication;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.providers.AimonProvider;
import it.rainbowbreeze.smsforfree.providers.JacksmsProvider;
import it.rainbowbreeze.smsforfree.providers.VoipstuntProvider;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class LogicManager
{
	//---------- Private fields

	
	
	
	//---------- Public properties

	
	
	
	//---------- Public methods
	
	/**
	 * Initializes data, execute begin operation
	 */
	public static ResultOperation<Void> executeBeginTask(Context context)
	{
		ResultOperation<Void> res = new ResultOperation<Void>();
		
		//set application name
		SmsForFreeApplication.instance().setAppName(context.getString(R.string.common_appNameForDisplay));
		SmsForFreeApplication.instance().setForceSubserviceRefresh(false);
		
		//load configurations
		AppPreferencesDao.instance().load(context);
		
		//load some application license setting
		SmsForFreeApplication.instance().setLiteVersionApp(
				GlobalDef.lite_description.equalsIgnoreCase(context.getString(R.string.config_AppType)));
		SmsForFreeApplication.instance().setAllowedSmsForDay(
				Integer.valueOf(context.getString(R.string.config_MaxAllowedSmsForDay)));

//		//check if the application expired
//		if (SmsForFreeApplication.instance().isLiteVersionApp()) {
//			SmsForFreeApplication.instance().setAppExpired(checkIfAppExpired());
//			if (SmsForFreeApplication.instance().isAppExpired()) {
//				res.setReturnCode(ResultOperation.RETURNCODE_APP_EXPIRED);
//				return res;
//			}
//		} else {
			SmsForFreeApplication.instance().setAppExpired(false);
//		}
		
		//update the daily number of sms
		updateSmsCounter(0);
		
		//checks for application upgrade
		res = performAppVersionUpgrade(context);
		if (res.HasErrors()) return res;
		res = addProvidersToList(context);
		if (res.HasErrors()) return res;

		return res;
	}


	/**
	 * Executes final operation, just before the app close
	 * @param context
	 * @return
	 */
	public static ResultOperation<Void> executeEndTast(Context context)
	{
		ResultOperation<Void> res = new ResultOperation<Void>();

		return res;
	}


	/**
	 * Update the number of message sent in current day
	 * 
	 * @param factorToAdd number of sms to add to the total
	 */
	public static void updateSmsCounter(int factorToAdd)
	{
		//check current date hash
		String currentDateHash = getCurrentDayHash();
		String lastUpdate = AppPreferencesDao.instance().getSmsCounterDate();
		
		if (TextUtils.isEmpty(lastUpdate) || !lastUpdate.equals(currentDateHash)) {
			//new day :D
			AppPreferencesDao.instance().setSmsCounterDate(currentDateHash);
			AppPreferencesDao.instance().setSmsCounterNumberForCurrentDay(factorToAdd);
		} else {
			//update sms sent in the day
			AppPreferencesDao.instance().setSmsCounterNumberForCurrentDay(AppPreferencesDao.instance().getSmsCounterNumberForCurrentDay() + factorToAdd);
		}
		AppPreferencesDao.instance().save();
	}


	/**
	 * Checks if sms message sent in the current day is still under the allowed limit
	 */
	public static boolean checkIfCanSendSms()
	{
		//unlimited sms for normal app
		if (!SmsForFreeApplication.instance().isLiteVersionApp()) return true;
		
		return getSmsSentToday() <= SmsForFreeApplication.instance().getAllowedSmsForDay();
	}
	
	/**
	 * Get the number of sms sent today
	 */
	public static int getSmsSentToday()
	{
		//check current date hash
		String currentDateHash = getCurrentDayHash();
		String lastUpdate = AppPreferencesDao.instance().getSmsCounterDate();

		int sentSms = 0;
		if (currentDateHash.equals(lastUpdate)) {
			sentSms = AppPreferencesDao.instance().getSmsCounterNumberForCurrentDay();
		}
		
		return sentSms;
	}


	
	
	
	//---------- Private methods

	/**
	 * Checks the expiration date of the application
	 * 
	 * @return true if the app is valid, false and RETURNCODE_SMS_DAILY_LIMIT_OVERTAKED
	 *         return code if app is invalid
	 */
	private static boolean checkIfAppExpired()
	{
		//get installation time
	    long installTime = AppPreferencesDao.instance().getInstallationTime();
		
		// get the current time
	    final Calendar c = Calendar.getInstance();
	    long currentTime = c.getTimeInMillis();

	    //find the maximum valid time interval
	    //60 days
	    long maxGap = 60 * 86400000;

	    //if the difference between the two dates is greater than the max allowed gap
	    return (currentTime - installTime > maxGap);
	}
	
	
	/**
	 * Checks if some upgrade is needed between current version of the
	 * application and the previous one
	 * 
	 *  @return true if all ok, otherwise false
	 */
	private static ResultOperation<Void> performAppVersionUpgrade(Context context)
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
		
		return new ResultOperation<Void>();
	}
	
	
	private static String getCurrentDayHash()
	{
        final Calendar c = Calendar.getInstance();
        StringBuilder dateHash = new StringBuilder();
        dateHash.append(c.get(Calendar.YEAR));
        dateHash.append(c.get(Calendar.MONTH));
        dateHash.append(c.get(Calendar.DAY_OF_MONTH));
        return dateHash.toString();
	}

	
	/**
	 * Add providers to the list of available providers, according with configurations
	 * @param context
	 */
	private static ResultOperation<Void> addProvidersToList(Context context)
	{
		ResultOperation<Void> res = null;
		
		//initialize provider list
		ProviderDao dao = new ProviderDao();
		String allowedProviders = context.getString(R.string.config_AllowedProviders);
		SmsForFreeApplication.instance().setProviderList(new ArrayList<SmsProvider>());
		
		if (allowedProviders.toUpperCase().contains("JACKSMS")) {
			//add JackSMS
			JacksmsProvider prov = new JacksmsProvider(dao, context);
			res = prov.initProvider(context);
			SmsForFreeApplication.instance().getProviderList().add(prov);
		}
	
		if (allowedProviders.toUpperCase().contains("AIMON")) {
			//add Aimon
			AimonProvider prov = new AimonProvider(dao, context);
			res = prov.initProvider(context);
			SmsForFreeApplication.instance().getProviderList().add(prov);
		}
		
		if (allowedProviders.toUpperCase().contains("VOIPSTUNT")) {
			//add Voipstunt
			VoipstuntProvider prov = new VoipstuntProvider(dao, context);
			res = prov.initProvider(context);
			SmsForFreeApplication.instance().getProviderList().add(prov);
		}
		
		//sort the collection of provider
		Collections.sort(SmsForFreeApplication.instance().getProviderList());
		return res;
	}
	
}
