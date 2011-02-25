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

package it.rainbowbreeze.smsforfree.common;

import java.util.List;

import it.rainbowbreeze.libs.common.RainbowAppGlobalBag;
import it.rainbowbreeze.libs.common.RainbowResultOperation;
import it.rainbowbreeze.libs.common.RainbowServiceLocator;
import it.rainbowbreeze.libs.logic.RainbowCrashReporter;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.data.SmsDao;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.logic.LogicManager;
import it.rainbowbreeze.smsforfree.ui.ActivityHelper;
import android.app.Application;
import android.content.Context;
import static it.rainbowbreeze.libs.common.RainbowContractHelper.*;

public class App
	extends Application
	implements RainbowAppGlobalBag
{
    //---------- Private fields
    private static final String LOG_HASH = "App";
    
    
    
	//---------- Constructor
	public App()
	{
		super();
		//this is the first instruction, so no fear that mInstance is null is following calls
		mInstance = this;
	}
	
	
	
	//---------- Public properties

	//singleton, modified for testing purposes
    private static App mInstance;
    public static App i()
    { return mInstance; }
	
    
	/** keys for application preferences */
	public final static String APP_PREFERENCES_KEY = "SmsForFreePrefs";

	/** Application version displayed to the user (about activity etc) */
	public final static String APP_DISPLAY_VERSION = "2.0";

	/** Application name used during the ping of update site */
	public final static String APP_INTERNAL_NAME = "SmsForFree";
    
	/** Application version for internal use (update, crash report etc) */
	public final static String APP_INTERNAL_VERSION = "02.00.00";

	/** address where send log */
	public final static String EMAIL_FOR_LOG = "devel@rainbowbreeze.it";
	
	/** Tag to use in the log */
	public final static String LOG_TAG = "SmsForFree";

	/** keys for application preferences */
	public final static String appPreferencesKeys = "SmsForFreePrefs"; 
	
	/** file name for providers preferences */
	public final static String jacksmsParametersFileName = "jacksms_parameters.xml"; 
	/** file name for providers templates list */
	public final static String jacksmsmTemplatesFileName = "jacksms_templates.xml";
	/** file name for providers subservices list */
	public final static String jacksmsSubservicesFileName = "jacksms_subservices.xml"; 

	/** file name for providers preferences */
	public final static String aimonParametersFileName = "aimon_parameters.xml"; 
	
	/** file name for providers preferences */
	public final static String voipstuntParametersFileName = "voipstunt_parameters.xml";

	public final static String subitosmsParametersFileName = "subitosms_parameters.xml";

	/** international prefix for Italy */
	public final static String italyInternationalPrefix = "+39";
	
	/** url where send statistics about device */
	public final static String STATISTICS_WEBSERVER_URL = "http://www.rainbowbreeze.it/devel/getlatestversion.php";
	
	/** string for lite version */
	public final static String lite_description = "Lite";

	/** platform - dependent newline char */
	public final static String LINE_SEPARATOR = System.getProperty("line.separator");	
	
	
	/** First run after an update of the application */
	protected boolean mFirstRunAfterUpdate;
	public RainbowAppGlobalBag setFirstRunAfterUpdate(boolean newValue)
	{ mFirstRunAfterUpdate = newValue; return this; }
	public boolean isFirstRunAfterUpdate()
	{ return mFirstRunAfterUpdate; }

	/** List of providers */
	protected List<SmsProvider> mProviderList;
	public List<SmsProvider> getProviderList()
	{ return mProviderList; }
	public void setProviderList(List<SmsProvider> newValue)
	{ mProviderList = newValue; }
	
	/** Max allowed SMS for each day */
	protected int mAllowedSmsForDay;
	public int getAllowedSmsForDay()
	{ return mAllowedSmsForDay; }
	public void setAllowedSmsForDay(int newValue)
	{ mAllowedSmsForDay = newValue; }

	/** Application is demo, not full version */
	protected boolean mLiteVersionApp;
	public boolean isLiteVersionApp()
	{ return mLiteVersionApp; }
	public void setLiteVersionApp(boolean newValue)
	{ mLiteVersionApp = newValue; }

	/** Application name */
	protected String mAppDisplayName;
	public String getAppDisplayName()
	{ return mAppDisplayName; }
	public void setAppDisplayName(String newValue)
	{ mAppDisplayName = newValue; }

	/** Force the refresh of subservice list */
	protected boolean mForceSubserviceRefresh;
	public boolean getForceSubserviceRefresh()
	{ return mForceSubserviceRefresh; }
	public void setForceSubserviceRefresh(boolean newValue)
	{ mForceSubserviceRefresh = newValue; }
	
	/** Show o don't show the ads */
	protected boolean mAdEnables;
	public boolean isAdEnables()
	{ return mAdEnables; }
	public void setAdEnables(boolean newValue)
	{ mAdEnables = newValue; }

    /** Show o don't show the ads */
    protected boolean mShowOnlyMobileNumbers;
    public boolean getShowOnlyMobileNumbers()
    { return mShowOnlyMobileNumbers; }
    public void setShowOnlyMobileNumbers(boolean newValue)
    { mShowOnlyMobileNumbers = newValue; }

	

	
	//---------- Events
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		setupEnvironment(getApplicationContext());
	}
	
	
	
	@Override
	public void onTerminate()
	{
		LogicManager logicManager = checkNotNull(RainbowServiceLocator.get(LogicManager.class), "LogicManager");
		//execute end tasks
		RainbowResultOperation<Void> res = logicManager.executeEndTasks(this);
		if (res.hasErrors()) {
			RainbowServiceLocator.get(ActivityHelper.class).reportError(this, res.getException(), res.getReturnCode());
		}

		
		//log the end of the application
		LogFacility logFacility = checkNotNull(RainbowServiceLocator.get(LogFacility.class), "LogFacility");
		logFacility.i(LOG_HASH, "App ending: " + App.APP_INTERNAL_NAME);
		super.onTerminate();
	}
	
	

	
	//---------- Public methods

	
	
	
	//---------- Private methods
	/**
	 * Setup the application environment.
	 * Public only for test purpose
	 */
	public void setupEnvironment(Context context) {
		//set the log tag
		LogFacility logFacility = new LogFacility(LOG_TAG);
		//log the begin of the application
		logFacility.i(LOG_HASH, "App started: " + App.APP_INTERNAL_NAME);

		//initialize (and automatically register) crash reporter
		RainbowCrashReporter crashReport = new RainbowCrashReporter(context);
		RainbowServiceLocator.put(crashReport);
		
		RainbowServiceLocator.put(logFacility);
		
		//create services and helper respecting IoC dependencies
		ActivityHelper activityHelper = new ActivityHelper(logFacility, context);
		RainbowServiceLocator.put(activityHelper);
		AppPreferencesDao appPreferencesDao = new AppPreferencesDao(context, APP_PREFERENCES_KEY);
		RainbowServiceLocator.put(appPreferencesDao);
		ProviderDao providerDao = new ProviderDao();
		RainbowServiceLocator.put(providerDao);
		LogicManager logicManager = new LogicManager(logFacility, appPreferencesDao, this, APP_INTERNAL_VERSION, providerDao, activityHelper);
		RainbowServiceLocator.put(logicManager);
		SmsDao smsDao = new SmsDao(logFacility);
        RainbowServiceLocator.put(smsDao);
	}
}
