/**
 * Copyright (C) 2010 Alfredo Morresi
 * 
 * This file is part of RainbowLibs project.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.rainbowbreeze.smsforfree.util;

import android.content.Context;
import android.util.Log;
import it.rainbowbreeze.libs.common.RainbowResultOperation;
import it.rainbowbreeze.libs.common.RainbowServiceLocator;
import it.rainbowbreeze.smsforfree.common.App;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.domain.SmsServiceParameter;
import it.rainbowbreeze.smsforfree.logic.LogicManager;

/**
 * General helper for tests
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class TestHelper
{
    //---------- Private fields
    protected static boolean mInitialized = false;
    

    //---------- Constructors

    
    //---------- Public properties

    
    //---------- Public methods
    
    /**
     * Initialize objects and put them inside
     * {@link RainbowServiceLocator}
     */
    public static void init(Context context) {
        //execute the following operation only one time
        if (mInitialized) return;
        
        App app = new App();
        app.setupEnvironment(context);
        mInitialized = true;
    }

    
    /**
     * Backup the values of service's parameters
     * 
     * @param service service to backup
     * @return an array with the backup of the parameters
     */
    public static SmsServiceParameter[] backupServiceParameters(SmsService service)
    {
        if (null == service) return null;
        
        SmsServiceParameter[] params;
        
        int numberOfParams = service.getParametersNumber();
        params = new SmsServiceParameter[numberOfParams];
        
        for (int i = 0; i < numberOfParams; i++) {
            SmsServiceParameter param = new SmsServiceParameter();
            param.setDesc(service.getParameterDesc(i));
            param.setValue(service.getParameterValue(i));
            param.setFormat(service.getParameterFormat(i));
            params[i] = param;
        }
            
        return params;
    }

    /**
     * Restore the value of the parameters in a service
     * @param service
     * @param params
     */
    public static void restoreServiceParameters(SmsService service, SmsServiceParameter[] params)
    {
        if (null == service || null == params) return;

        
        for (int i = 0; i < params.length; i++) {
            SmsServiceParameter param = params[i];
            service.setParameterDesc(i, param.getDesc());
            service.setParameterValue(i, param.getValue());
            service.setParameterFormat(i, param.getFormat());
        }
    }
    
    
    /**
     * Initialize the context
     * 
     * Similar to what happens in the {@link SmsForFreeApplication#onCreate()}
     * 
     * @param context
     * @return
     */
    public static boolean beginTask(Context context)
    {
        boolean result;

        LogicManager logicManager = RainbowServiceLocator.get(LogicManager.class);
        
        //executes begin task
        RainbowResultOperation<Void> res = logicManager.executeBeginTasks(context);
        if (res.hasErrors()) {
            Log.e("SmsForFreeTest", String.valueOf(res.getReturnCode()));
            result = false;
        } else {
            result = true;
        }
        
        return result;
    }

    //---------- Private methods
}
