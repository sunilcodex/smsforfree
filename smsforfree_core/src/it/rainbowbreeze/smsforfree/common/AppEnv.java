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
import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.data.SmsDao;
import it.rainbowbreeze.smsforfree.data.MessageQueueDao;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.logic.LogicManager;
import it.rainbowbreeze.smsforfree.ui.ActivityHelper;
import android.content.Context;
import static it.rainbowbreeze.libs.common.RainbowContractHelper.checkNotNull;


public class AppEnv
{
    //---------- Private fields
    private static final String LOG_HASH = "AppEnv";
    private static final Object mSyncObject = new Object();
    
    
    
	//---------- Constructor
    private AppEnv(Context context) {
        setupVolatileData(context);
    }
	
	
	
	//---------- Public properties
    /** lazy loading singleton */
    public static AppEnv i(Context context) {
        synchronized (mSyncObject) {
            if (null == mInstance)
                mInstance = new AppEnv(context);
        }
        return mInstance;
    }
    private static AppEnv mInstance;
	
    
	/** keys for application preferences */
	public final static String APP_PREFERENCES_KEY = "SmsForFreePrefs";

	/** Application version displayed to the user (about activity etc) */
	public final static String APP_DISPLAY_VERSION = "2.2";

	/** Application name used during the ping of update site */
	public final static String APP_INTERNAL_NAME = "SmsForFree";
    
	/** Application version for internal use (update, crash report etc) */
	public final static String APP_INTERNAL_VERSION = "02.02.00";

	/** address where send log */
	public final static String EMAIL_FOR_LOG = "devel@rainbowbreeze.it";
	
	/** Tag to use in the log */
	public final static String LOG_TAG = "SmsForFree";
	
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
	public final static String ITALY_INTERNATIONAL_PREFIX = "+39";
	
	/** url where send statistics about device */
	public final static String STATISTICS_WEBSERVER_URL = "http://www.rainbowbreeze.it/devel/getlatestversion.php";
	
	/** string for lite version */
	public final static String LITE_DESCRIPTION = "Lite";

	/** platform - dependent newline char */
	public final static String LINE_SEPARATOR = System.getProperty("line.separator");	
	
	
	/** List of providers */
	protected final List<SmsProvider> mProviderList = new ArrayList<SmsProvider>();
	public List<SmsProvider> getProviderList()
	{ return mProviderList; }
	
	/** Max allowed SMS for each day */
    protected int mAllowedSmsForDay;
	public int getAllowedSmsForDay()
	{ return mAllowedSmsForDay; }

	/** Application is demo, not full version */
	protected boolean mLiteVersionApp;
	public boolean isLiteVersionApp()
	{ return mLiteVersionApp; }

	/** Application name */
	protected String mAppDisplayName;
	public String getAppDisplayName()
	{ return mAppDisplayName; }

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

	
	

	
	//---------- Public methods

    public LogFacility getLogFacility()
    { return checkNotNull(RainbowServiceLocator.get(LogFacility.class), "LogFacility"); }
    
    public LogicManager getLogicManager()
    { return checkNotNull(RainbowServiceLocator.get(LogicManager.class), "LogicManager"); }

    public ActivityHelper getActivityHelper()
    { return checkNotNull(RainbowServiceLocator.get(ActivityHelper.class), "ActivityHelper"); }

    public AppPreferencesDao getAppPreferencesDao()
    { return checkNotNull(RainbowServiceLocator.get(AppPreferencesDao.class), "AppPreferencesDao"); }

    public SmsDao getSmsDao()
    { return checkNotNull(RainbowServiceLocator.get(SmsDao.class), "SmsDao"); }

    public MessageQueueDao getMessageQueueDao()
    { return checkNotNull(RainbowServiceLocator.get(MessageQueueDao.class), "TextMessageQueue"); }

    /**
	 * Setup the volatile data of application.
	 * This is needed because sometime the system release memory
	 * without completely close the application, so all static fields
	 * will become null :(
	 * 
	 * Public only for test purpose
	 */
	public void setupVolatileData(Context context) {
		//set the log tag
		LogFacility logFacility = new LogFacility(LOG_TAG);
	    logFacility.i(LOG_HASH, "Initializing environment");

        //empty service locator
        RainbowServiceLocator.clear();
        //put log facility
        RainbowServiceLocator.put(logFacility);

        //initialize (and automatically register) crash reporter
		RainbowCrashReporter crashReport = new RainbowCrashReporter(context);
		RainbowServiceLocator.put(crashReport);
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
        MessageQueueDao messageQueueDao = new MessageQueueDao(context, logFacility);
        RainbowServiceLocator.put(messageQueueDao);
        
        //set application name
        mAppDisplayName = context.getString(R.string.common_appNameForDisplay);
        logFacility.v(LOG_HASH, "App display name: " + mAppDisplayName);
        
        //find if ads should be enabled
        String adEnabel = context.getString(R.string.config_ShowAd);
        mAdEnables = "true".equalsIgnoreCase(adEnabel);

        //init some vars
        setForceSubserviceRefresh(false);
        
        //load some application license setting
        mLiteVersionApp =
            LITE_DESCRIPTION.equalsIgnoreCase(context.getString(R.string.config_AppType));
        mAllowedSmsForDay =
            Integer.valueOf(context.getString(R.string.config_MaxAllowedSmsForDay));

        //init providers
        ResultOperation<Void> res;
        res = logicManager.addProvidersToList(context, mProviderList);
        if (res.hasErrors()) {
            //FIXME better error management
            activityHelper.reportError(context, res);
        }
	}
 
    
    
    
    //---------- Private methods
}
