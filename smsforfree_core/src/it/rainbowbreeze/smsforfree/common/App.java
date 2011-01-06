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

import java.util.ArrayList;
import java.util.List;

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

public class App
	extends Application
{
    //---------- Private fields
    private static final String LOG_HASH = "App";
    
    
    
	//---------- Constructor
	
	
	
	//---------- Public properties
	
    
	/** keys for application preferences */
	public final static String APP_PREFERENCES_KEY = "SmsForFreePrefs";

	/** Application version displayed to the user (about activity etc) */
	public final static String APP_DISPLAY_VERSION = "2.1";

	/** Application name used during the ping of update site */
	public final static String APP_INTERNAL_NAME = "SmsForFree";
    
	/** Application version for internal use (update, crash report etc) */
	public final static String APP_INTERNAL_VERSION = "02.01.02";

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
	
	
	/** List of providers */
    public static final List<SmsProvider> providerList = new ArrayList<SmsProvider>();
//	protected final List<SmsProvider> mProviderList = new ArrayList<SmsProvider>();
//	public List<SmsProvider> getProviderList()
//	{ return mProviderList; }
	
	/** Max allowed SMS for each day */
	public static int allowedSmsForDay;
//    protected int mAllowedSmsForDay;
//	public int getAllowedSmsForDay()
//	{ return mAllowedSmsForDay; }
//	public void setAllowedSmsForDay(int newValue)
//	{ mAllowedSmsForDay = newValue; }

	/** Application is demo, not full version */
    public static boolean liteVersionApp;
//	protected boolean mLiteVersionApp;
//	public boolean isLiteVersionApp()
//	{ return mLiteVersionApp; }
//	public void setLiteVersionApp(boolean newValue)
//	{ mLiteVersionApp = newValue; }

	/** Application name */
    public static String appDisplayName;
//	protected String mAppDisplayName;
//	public String getAppDisplayName()
//	{ return mAppDisplayName; }
//	public void setAppDisplayName(String newValue)
//	{ mAppDisplayName = newValue; }

	/** Force the refresh of subservice list */
    public static boolean forceSubserviceRefresh;
//	protected boolean mForceSubserviceRefresh;
//	public boolean getForceSubserviceRefresh()
//	{ return mForceSubserviceRefresh; }
//	public void setForceSubserviceRefresh(boolean newValue)
//	{ mForceSubserviceRefresh = newValue; }
	
	/** Show o don't show the ads */
    public static boolean adEnables;
//    protected boolean mAdEnables;
//	public boolean isAdEnables()
//	{ return mAdEnables; }
//	public void setAdEnables(boolean newValue)
//	{ mAdEnables = newValue; }

    /** Show o don't show the ads */
    public static boolean showOnlyMobileNumbers;
//    protected boolean mShowOnlyMobileNumbers;
//    public boolean getShowOnlyMobileNumbers()
//    { return mShowOnlyMobileNumbers; }
//    public void setShowOnlyMobileNumbers(boolean newValue)
//    { mShowOnlyMobileNumbers = newValue; }

	

	
	//---------- Events
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		setupEnvironment(getApplicationContext());
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
		LogicManager logicManager = new LogicManager(logFacility, appPreferencesDao, APP_INTERNAL_VERSION, providerDao, activityHelper);
		RainbowServiceLocator.put(logicManager);
		SmsDao smsDao = new SmsDao(logFacility);
        RainbowServiceLocator.put(smsDao);
	}
}
