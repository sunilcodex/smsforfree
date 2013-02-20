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
package it.rainbowbreeze.smsforfree.logic;

import java.util.Calendar;

import it.rainbowbreeze.smsforfree.common.AppEnv;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.data.IMessageQueueDao;
import it.rainbowbreeze.smsforfree.data.MemoryMessageQueueDao;
import it.rainbowbreeze.smsforfree.domain.TextMessage;
import it.rainbowbreeze.smsforfree.helper.GlobalHelper;
import it.rainbowbreeze.smsforfree.providers.MockSingleServiceProvider;
import it.rainbowbreeze.smsforfree.util.TestHelper;
import android.content.Context;
import android.content.Intent;
import android.test.ServiceTestCase;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class SendMessageServiceTest extends ServiceTestCase<SendMessageService> {
    //---------- Private fields
	IMessageQueueDao mMessageQueueDao;
	MockSingleServiceProvider mSmsProvider;
    private boolean mForceReload = false;
	private static AppEnv.ObjectsFactory mCustomObjectsFactory =
	    new CustomObjectsFactory();
	

    
	//---------- Constructor
    public SendMessageServiceTest() {
        super(SendMessageService.class);
    }



    
	//---------- SetUp and TearDown
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        if (mForceReload) {
            TestHelper.init(getContext(), mCustomObjectsFactory, mForceReload);
            mForceReload = false;
            
            //add mock providers
            MockSingleServiceProvider mockSingleServiceProvider = new MockSingleServiceProvider(
                    AppEnv.i(getContext()).getLogFacility(),
                    AppEnv.i(getContext()).getAppPreferencesDao(),
                    AppEnv.i(getContext()).getProviderDao(),
                    AppEnv.i(getContext()).getActivityHelper());
            AppEnv.i(getContext()).getProviderList().add(mockSingleServiceProvider);
        }

        mSmsProvider = (MockSingleServiceProvider) GlobalHelper.findProviderInList(AppEnv.i(getContext()).getProviderList(), MockSingleServiceProvider.ID);
        assertNotNull("Wrong mock provider", mSmsProvider);
        mMessageQueueDao = AppEnv.i(getContext()).getMessageQueueDao();
    }
    
    @Override
    protected void tearDown() throws Exception {
    	super.tearDown();
    	//restore right provider
//        AppEnv.i(getContext()).setMessageQueueDao(mSaveMessageQueueDao);
    }

    
    
    
	//---------- Tests methods
	public void testOnReceive() {
	    //TODO check if it's correct
		//creates the intent for sending message
		TextMessage textMessageSource = TextMessage.Factory.create(
				123l,
				"+393314729472",
				"Test message from Alfredo's phone! It's all ok?", 
				MockSingleServiceProvider.ID,
				MockSingleServiceProvider.ID,
				0l,
				TextMessage.PROCESSING_NONE);

		//inserts message into queue
		long textMessageId = mMessageQueueDao.insert(textMessageSource);
		
		//calls the receiver
		callAndWaitServiceWork(getContext(), textMessageId);
		
		//checks results
		TextMessage textMessageDest = mSmsProvider.getLastSendMessage();

		assertNotNull("Wrong text message", textMessageDest);
		assertEquals("Wrong destination", textMessageSource.getDestination(), textMessageDest.getDestination());
		assertEquals("Wrong message", textMessageSource.getMessage(), textMessageDest.getMessage());
		assertEquals("Wrong provider", textMessageSource.getProviderId(), textMessageDest.getProviderId());
		assertEquals("Wrong service", textMessageSource.getServiceId(), textMessageDest.getServiceId());
		assertEquals("Wrong processing status", TextMessage.PROCESSING_SENT, textMessageDest.getProcessingStatus());
	}

	
	
	
	//---------- Private methods
	/**
	 * Calls the receiver and wait some moment to allow {@link SendMessageService} to execute
	 * its job
	 */
	private void callAndWaitServiceWork(Context context, long textMessageId) {
		// get a Calendar object with current time
		Calendar cal = Calendar.getInstance();
		// add 1 second to the calendar object
		cal.add(Calendar.SECOND, 1);
		Intent intent = new Intent(context, SendMessageService.class);
		intent.putExtra(AppEnv.INTENTKEY_MESSAGEID, textMessageId);
//		//create the pending intent
//		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//		
//		// Get the AlarmManager service
//		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
		startService(intent);
		
		//now wait some time to execute the alarm
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			//nothing to do
		}
	}

    
    
    
    //---------- Private classes
	public static class CustomObjectsFactory extends AppEnv.ObjectsFactory {
	    @Override
	    public IMessageQueueDao createMessageQueueDao(Context context, LogFacility logFacility) {
	        return new MemoryMessageQueueDao();
	    }
	}
}
