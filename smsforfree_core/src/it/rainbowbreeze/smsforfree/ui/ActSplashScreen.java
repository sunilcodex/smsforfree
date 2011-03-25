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

package it.rainbowbreeze.smsforfree.ui;

import android.content.Intent;
import android.os.Bundle;
import it.rainbowbreeze.libs.common.RainbowResultOperation;
import it.rainbowbreeze.libs.logic.RainbowSendStatisticsTask;
import it.rainbowbreeze.libs.ui.RainbowSplashScreenActivity;
import it.rainbowbreeze.smsforfree.common.AppEnv;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.domain.TextMessage;
import it.rainbowbreeze.smsforfree.logic.LogicManager;

/**
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ActSplashScreen
    extends RainbowSplashScreenActivity
{

    //---------- Private fields
    AppPreferencesDao mAppPreferencesDao;
    protected ActivityHelper mActivityHelper;
    protected LogicManager mLogicManager;
    protected TextMessage intentMessage;

    
    
    
    //---------- Public properties

    
    
    
    //---------- Events
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

    }
    


    //---------- Public methods

    
    
    
    //---------- Private methods

    /* (non-Javadoc)
     * @see it.rainbowbreeze.libs.ui.BaseSplashScreenActivity#additionalInitialization(android.os.Bundle)
     */
    @Override
    protected void additionalInitialization(Bundle savedInstanceState) {
        mActivityHelper = AppEnv.i(getBaseContext()).getActivityHelper();
        mLogicManager = AppEnv.i(getBaseContext()).getLogicManager();
        mAppPreferencesDao = AppEnv.i(getBaseContext()).getAppPreferencesDao();
    }
   

    /* (non-Javadoc)
     * @see it.rainbowbreeze.libs.ui.BaseSplashScreenActivity#beginTaskFailed(it.rainbowbreeze.libs.common.BaseResultOperation)
     */
    @Override
    protected void beginTaskFailed(RainbowResultOperation<Void> result) {
        sendStatistics();
        mBaseLogFacility.e(LOG_HASH, "Cannot launch the application, error during initialization");
        //report the errors
        mBaseActivityHelper.reportError(this, result);
    }


    /* (non-Javadoc)
     * @see it.rainbowbreeze.libs.ui.BaseSplashScreenActivity#beginTasksCompleted(it.rainbowbreeze.libs.common.BaseResultOperation)
     */
    @Override
    protected void beginTasksCompleted(RainbowResultOperation<Void> result) {
        sendStatistics();
        //checks if a message is in the intent
        Intent i = getIntent();
        TextMessage message = mLogicManager.getMessageFromIntent(i);

        //call main activity
        mActivityHelper.openSendSms(this, message);
        finish();
    }

    @Override
    protected String getApplicationInternalName() {
        return AppEnv.APP_INTERNAL_NAME;
    }

    @Override
    protected String getApplicationInternalVersion() {
        return AppEnv.APP_INTERNAL_VERSION;
    }

    @Override
    protected String getEmailForLog() {
        return AppEnv.EMAIL_FOR_LOG;
    }

    @Override
    protected String getLogTag() {
        return AppEnv.LOG_TAG;
    }

    /**
     * Called when the users 
     */
    protected void sendStatistics() {
        try {
            //ping statistic webservice during application first starts
            String appName = AppEnv.i(getBaseContext()).getAppDisplayName();
            //send statistics data first time the app runs
            RainbowSendStatisticsTask statsTask = new RainbowSendStatisticsTask(
                    mBaseLogFacility,
                    mActivityHelper,
                    this,
                    AppEnv.STATISTICS_WEBSERVER_URL,
                    appName,
                    AppEnv.APP_INTERNAL_VERSION,
                    String.valueOf(mAppPreferencesDao.getUniqueId()));
            Thread t = new Thread(statsTask);
            t.start();
        } catch (Exception e) {
            //nothing to do
        }
    }
}
