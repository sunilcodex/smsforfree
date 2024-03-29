/**
 * Copyright (C) 2011 Alfredo Morresi
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

import android.app.Application;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class MyApplication extends Application {
    //---------- Private fields
    private static final String LOG_HASH = MyApplication.class.getSimpleName();

    
    
    
    //---------- Constructor

    
    
    
    //---------- Public properties
    
    


    //---------- Events
    @Override
    public void onCreate() {
        super.onCreate();
        
        //WARNING
        //The AppEnv lazy-loading singleton must be called at least one time here
        
        //initialize the application
        LogFacility logFacility = AppEnv.i(getBaseContext()).getLogFacility();
        logFacility.i(LOG_HASH, "Starting application " + AppEnv.APP_INTERNAL_NAME + " v" + AppEnv.APP_INTERNAL_VERSION);
    }
    
    
    
    //---------- Public methods

    
    
    
    //---------- Private methods
}
