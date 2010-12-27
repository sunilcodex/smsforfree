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
import it.rainbowbreeze.libs.common.RainbowServiceLocator;
import it.rainbowbreeze.libs.ui.RainbowSplashScreenActivity;
import it.rainbowbreeze.smsforfree.common.App;
import it.rainbowbreeze.smsforfree.domain.TextMessage;
import it.rainbowbreeze.smsforfree.logic.LogicManager;
import static it.rainbowbreeze.libs.common.RainbowContractHelper.*;

/**
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ActSplashScreen
    extends RainbowSplashScreenActivity
{

    //---------- Private fields
    protected ActivityHelper mActivityHelper;
    protected LogicManager mLogicManager;
    protected TextMessage intentMessage;

    
    
    
    //---------- Public properties

    
    
    
    //---------- Events
    
    


    //---------- Public methods

    
    
    
    //---------- Private methods

    /* (non-Javadoc)
     * @see it.rainbowbreeze.libs.ui.BaseSplashScreenActivity#additionalInitialization(android.os.Bundle)
     */
    @Override
    protected void additionalInitialization(Bundle savedInstanceState) {
        mActivityHelper = checkNotNull(RainbowServiceLocator.get(ActivityHelper.class), "ActivityHelper");
        mLogicManager = checkNotNull(RainbowServiceLocator.get(LogicManager.class), "LogicManager");
    }

    /* (non-Javadoc)
     * @see it.rainbowbreeze.libs.ui.BaseSplashScreenActivity#beginTaskFailed(it.rainbowbreeze.libs.common.BaseResultOperation)
     */
    @Override
    protected void beginTaskFailed(RainbowResultOperation<Void> result) {
        mBaseLogFacility.e("Cannot launch the application, error during initialization");
        //report the errors
        mBaseActivityHelper.reportError(this, result);
    }


    /* (non-Javadoc)
     * @see it.rainbowbreeze.libs.ui.BaseSplashScreenActivity#beginTasksCompleted(it.rainbowbreeze.libs.common.BaseResultOperation)
     */
    @Override
    protected void beginTasksCompleted(RainbowResultOperation<Void> result) {
        //checks if a message is in the intent
        Intent i = getIntent();
        TextMessage message = mLogicManager.getMessageFromIntent(i);

        //call main activity
        mActivityHelper.openSendSms(this, message);
    }

    @Override
    protected String getApplicationInternalName() {
        return App.APP_INTERNAL_NAME;
    }

    @Override
    protected String getApplicationInternalVersion() {
        return App.APP_INTERNAL_VERSION;
    }

    @Override
    protected String getEmailForLog() {
        return App.EMAIL_FOR_LOG;
    }

    @Override
    protected String getLogTag() {
        return App.LOG_TAG;
    }
}