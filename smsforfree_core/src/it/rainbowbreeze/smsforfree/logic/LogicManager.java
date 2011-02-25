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

package it.rainbowbreeze.smsforfree.logic;

import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import it.rainbowbreeze.libs.common.RainbowResultOperation;
import it.rainbowbreeze.libs.helper.RainbowStringHelper;
import it.rainbowbreeze.libs.logic.RainbowLogicManager;
import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.common.AppEnv;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.TextMessage;
import it.rainbowbreeze.smsforfree.providers.AimonProvider;
import it.rainbowbreeze.smsforfree.providers.JacksmsProvider;
import it.rainbowbreeze.smsforfree.providers.SubitosmsProvider;
import it.rainbowbreeze.smsforfree.providers.VoipstuntProvider;
import it.rainbowbreeze.smsforfree.ui.ActivityHelper;
import static it.rainbowbreeze.libs.common.RainbowContractHelper.*;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class LogicManager extends RainbowLogicManager {
	//---------- Private fields
    private static final String LOG_HASH = "LogicManager";

    protected final AppPreferencesDao mAppPreferencesDao;
	protected final LogFacility mLogFacility;
	protected final ProviderDao mProviderDao;
	protected final ActivityHelper mActivityHelper;
	
	
	
	//---------- Constructors
	/**
	 * @param logFacility
	 * @param appPreferencesDao
	 * @param globalBag
	 * @param currentAppVersion
	 * @param itemsDao
	 */
	public LogicManager(
			LogFacility logFacility,
			AppPreferencesDao appPreferencesDao,
			String currentAppVersion,
			ProviderDao providerDao,
			ActivityHelper activityHelper)
	{
		super(logFacility, appPreferencesDao, currentAppVersion);
		mAppPreferencesDao = appPreferencesDao;
		mLogFacility = logFacility;
		mProviderDao = checkNotNull(providerDao, "ProviderDao");
		mActivityHelper = checkNotNull(activityHelper, "ActivityHelper");
	}

	
	
	
	//---------- Public properties

	
	
	
	//---------- Public methods
	
	/**
	 * Initializes data, execute begin operation
	 */
	@Override
	public RainbowResultOperation<Void> executeBeginTasks(Context context)
	{
		RainbowResultOperation<Void> res;
		
		res = super.executeBeginTasks(context);
		if (res.hasErrors())
			return res;

		//update the daily number of sms
		updateSmsCounter(0);
		
		res = checksTemplatesValues(context);
		if (res.hasErrors()) return res;
			
		return res;
	}
	
	/**
	 * Executes final tasks (free resources, etc)
	 */
	@Override
	public RainbowResultOperation<Void> executeEndTasks(Context context) {
		mLogFacility.v(LOG_HASH, "Executing end tasks");
		return super.executeEndTasks(context);
	}


	/**
	 * Update the number of message sent in current day
	 * 
	 * @param factorToAdd number of sms to add to the total
	 */
	public void updateSmsCounter(int factorToAdd)
	{
		//check current date hash
		String currentDateHash = getCurrentDayHash();
		String lastUpdate = mAppPreferencesDao.getSmsCounterDate();
		
		int smsSentToday = 0;
		if (TextUtils.isEmpty(lastUpdate) || !lastUpdate.equals(currentDateHash)) {
			//new day :D
			mAppPreferencesDao.setSmsCounterDate(currentDateHash);
			smsSentToday = factorToAdd;
		} else {
			//update sms sent in the day
			smsSentToday = mAppPreferencesDao.getSmsCounterNumberForCurrentDay() + factorToAdd;
		}
		mLogFacility.v(LOG_HASH, "Set the number of SMS sent today to " + smsSentToday);
        mAppPreferencesDao.setSmsCounterNumberForCurrentDay(smsSentToday);
		mAppPreferencesDao.save();
	}


	/**
	 * Checks if sms message sent in the current day is still under the allowed limit
	 */
	public boolean checkIfCanSendSms(Context context)
	{
		//unlimited sms for normal app
		if (!AppEnv.i(context).isLiteVersionApp()) return true;
		
		//0: no send limit
		if (0 == AppEnv.i(context).getAllowedSmsForDay()) return true;
		
		return getSmsSentToday() <= AppEnv.i(context).getAllowedSmsForDay();
	}
	
	/**
	 * Get the number of sms sent today
	 */
	public int getSmsSentToday()
	{
		//check current date hash
		String currentDateHash = getCurrentDayHash();
		String lastUpdate = mAppPreferencesDao.getSmsCounterDate();

		int sentSms = 0;
		if (currentDateHash.equals(lastUpdate)) {
			sentSms = mAppPreferencesDao.getSmsCounterNumberForCurrentDay();
		}
		
		return sentSms;
	}

	/**
	 * Checks if message templates values are not empty, elsewhere put default templates
	 * 
	 * public for testing purposes
	 * @param context
	 * @return
	 */
	public ResultOperation<Void> checksTemplatesValues(Context context) {
		//checks if templates are ok
		String [] templates = mAppPreferencesDao.getMessageTemplates();
		if (null == templates || templates.length < 1 || TextUtils.isEmpty(templates[0])) {
			//load standard templates
			templates = context.getString(R.string.common_defaultMessageTemplates).split("§§§§");
			mAppPreferencesDao.setMessageTemplates(templates);
			boolean result = mAppPreferencesDao.save();
			if (!result) return new ResultOperation<Void>(new Exception("Error saving application preferences"), ResultOperation.RETURNCODE_ERROR_APPLICATION_ARCHITECTURE);
		}
		return new ResultOperation<Void>();
	}
	
	/**
	 * Get the message data from the intent
	 * 
	 * @param intent intent with, maybe, the message
	 * 
	 * @return null if no message was found inside the intent,
	 *         else an object with message data
	 */
	public TextMessage getMessageFromIntent(Intent intent) {
        if (null == intent) return null;
        
        TextMessage message = TextMessage.Factory.create();
        
        if (Intent.ACTION_SENDTO.equals(intent.getAction())) {
            //in the data i'll find the number of the destination
            String destionationNumber = intent.getDataString();
            destionationNumber = URLDecoder.decode(destionationNumber);
            //clear the string
            destionationNumber = destionationNumber.replace("-", "")
                .replace("smsto:", "")
                .replace("sms:", "");
            //and set fields
            mLogFacility.i(LOG_HASH, "Application called for sending number to " + RainbowStringHelper.scrambleNumber(destionationNumber));
            message.setDestination(destionationNumber);
            
        } else if (Intent.ACTION_SEND.equals(intent.getAction()) && "text/plain".equals(intent.getType())) {
            //in the data i'll find the content of the message
            String messageBody = intent.getStringExtra(Intent.EXTRA_TEXT);
            mLogFacility.i(LOG_HASH, "Application called for sending message " + (messageBody.length() < 200 ? message : messageBody.substring(0, 200)));
            //clear the string
            message.setMessage(messageBody);
        }
        
        return message;
	}

	/**
	 * Add providers to the list of available providers, according with configurations
	 * @param context
	 */
	public ResultOperation<Void> addProvidersToList(Context context, List<SmsProvider> providers) {
		ResultOperation<Void> res = null;
		
		//initialize provider list
		String restrictToProviders = context.getString(R.string.config_RestrictToProviders);
		providers.clear();
		
		//cycles thru all providers and initializes only the required providers
		String[] allSupportedProviders = "AIMON,JACKSMS,SUBITOSMS,VOIPSTUNT".split(",");
		for (String providerName : allSupportedProviders) {
			SmsProvider provider = null;
			if (TextUtils.isEmpty(restrictToProviders) || restrictToProviders.toUpperCase().contains(providerName)) {
				
				if ("AIMON".equals(providerName)) provider = new AimonProvider(mLogFacility, mAppPreferencesDao, mProviderDao, mActivityHelper);
				else if ("JACKSMS".equals(providerName)) provider = new JacksmsProvider(mLogFacility, mAppPreferencesDao, mProviderDao, mActivityHelper);
				else if ("SUBITOSMS".equals(providerName)) provider = new SubitosmsProvider(mLogFacility, mAppPreferencesDao, mProviderDao, mActivityHelper);
				else if ("VOIPSTUNT".equals(providerName)) provider = new VoipstuntProvider(mLogFacility, mAppPreferencesDao, mProviderDao, mActivityHelper);
				
				if (null != provider) {
					mLogFacility.i(LOG_HASH, "Inizializing provider " + providerName);
					res = provider.initProvider(context);
					providers.add(provider);
				}
				if (res.hasErrors()) break;
			}
		}
		
		//sort the collection of provider
		Collections.sort(providers);
		return res;
	}
	

    

    //---------- Private methods
    private String getCurrentDayHash()
    {
        final Calendar c = Calendar.getInstance();
        StringBuilder dateHash = new StringBuilder();
        dateHash.append(c.get(Calendar.YEAR))
            .append("-")
            .append(c.get(Calendar.MONTH))
            .append("-")
            .append(c.get(Calendar.DAY_OF_MONTH));
        return dateHash.toString();
    }

    
	@Override
	protected RainbowResultOperation<Void> executeUpgradeTasks(
			Context context,
			String startingAppVersion) {
		return new RainbowResultOperation<Void>();
	}
}
