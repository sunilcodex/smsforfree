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

import junit.framework.TestCase;
import android.content.Context;
import android.util.Log;
import it.rainbowbreeze.libs.common.RainbowResultOperation;
import it.rainbowbreeze.libs.common.RainbowServiceLocator;
import it.rainbowbreeze.smsforfree.common.AppEnv;
import it.rainbowbreeze.smsforfree.data.IMessageQueueDao;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.domain.SmsServiceParameter;
import it.rainbowbreeze.smsforfree.domain.TextMessage;
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
     * Initialize objects and put them inside {@link RainbowServiceLocator}
     * 
     * @return true if initialization was done, otherwise false (data already initialized)
     */
    public static boolean init(Context context, boolean forceReload) {
        return init(context, AppEnv.getDefaultObjectsFactory(), forceReload);
    }
    
    public static boolean init(
            Context context,
            AppEnv.ObjectsFactory overriddingObjectsFactory,
            boolean forceReload) {
        //execute the following operation only one time
        if (mInitialized && !forceReload) return false;
        
        //initialize the environment
        AppEnv.i(context, overriddingObjectsFactory);
        mInitialized = true;
        return true;
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
    
    /**
     * Create an example text message
     * @return
     */
	public static TextMessage createTextMessage1() {
		return TextMessage.Factory.create(
				123L,
				"+393331234567",
				"Test message from Alfredo's phone! It's all ok?", 
				"JACKSMS",
				"Vodafone",
				1234567890L,
				TextMessage.PROCESSING_NONE);
	}

    /**
     * Compare a {@link TextMessage} with example text message 1
     * @param textMessageToCompare
     */
    public static void compareWithTextMessage1(TextMessage textMessageToCompare) {
    	compareWithTextMessage1(textMessageToCompare, 123);
    }
    
    /**
     * Compare a {@link TextMessage} with example text message 1
     * @param textMessageToCompare
     * @param newId new id to compare, not the original in example test message 1
     */
    public static void compareWithTextMessage1(TextMessage textMessageToCompare, long newId) {
        TestCase.assertEquals("Wrong id", newId, textMessageToCompare.getId());
        TestCase.assertEquals("Wrong destination", "+393331234567", textMessageToCompare.getDestination());
        TestCase.assertEquals("Wrong message", "Test message from Alfredo's phone! It's all ok?", textMessageToCompare.getMessage());
        TestCase.assertEquals("Wrong providerId", "JACKSMS", textMessageToCompare.getProviderId());
        TestCase.assertEquals("Wrong serviceId", "Vodafone", textMessageToCompare.getServiceId());
        TestCase.assertEquals("Wrong send interval", 1234567890, textMessageToCompare.getSendInterval());
        TestCase.assertEquals("Wrong processingStatus", TextMessage.PROCESSING_NONE, textMessageToCompare.getProcessingStatus());
    }
    
    /**
     * Compare a {@link TextMessage} with the data on database
     * @param textMessageId
     */
    public static void compareWithTextMessage1(IMessageQueueDao dao, long textMessageId) {
        TextMessage textMessageToCompare = dao.getById(textMessageId);
        compareWithTextMessage1(textMessageToCompare, textMessageId);
    }
    


    /**
     * Create an example text message
     * @return
     */
    public static TextMessage createTextMessage2() {
		return TextMessage.Factory.create(
				76,
				"+399877654321",
				"Another message to test, this time!!!%%$$", 
				"INTERNAL",
				null,
				987654321,
				TextMessage.PROCESSING_QUEUED);
	}

    /**
     * Compare a {@link TextMessage} with example text message 1
     * @param textMessageToCompare
     * @param newId new id to compare, not the original in example test message 1
     */
    public static void compareWithTextMessage2(TextMessage textMessageToCompare, long newId) {
        TestCase.assertEquals("Wrong id", newId, textMessageToCompare.getId());
        TestCase.assertEquals("Wrong destination", "+399877654321", textMessageToCompare.getDestination());
        TestCase.assertEquals("Wrong message", "Another message to test, this time!!!%%$$", textMessageToCompare.getMessage());
        TestCase.assertEquals("Wrong providerId", "INTERNAL", textMessageToCompare.getProviderId());
        TestCase.assertNull("Wrong serviceId", textMessageToCompare.getServiceId());
        TestCase.assertEquals("Wrong send interval", 987654321, textMessageToCompare.getSendInterval());
        TestCase.assertEquals("Wrong processingStatus", TextMessage.PROCESSING_QUEUED, textMessageToCompare.getProcessingStatus());
    }

    /**
     * Compare a {@link TextMessage} with the data on database
     * @param textMessageId
     */
    public static void compareWithTextMessage2(IMessageQueueDao dao, long textMessageId) {
        TextMessage textMessageToCompare = dao.getById(textMessageId);
        compareWithTextMessage2(textMessageToCompare, textMessageId);
    }

    /**
     * Create an example text message
     * @return
     */
    public static TextMessage createTextMessage3() {
		return TextMessage.Factory.create(
				99,
				"+002-(635)21-34235",
				"Loooong message to my american friends. how do you do? I hope well for you, it' all ok? let me know when next visit will happens. Cheers", 
				"VOIPSTUNT",
				null,
				0L,
				TextMessage.PROCESSING_ERROR_SENDING);
	}

    

    //---------- Private methods
}
