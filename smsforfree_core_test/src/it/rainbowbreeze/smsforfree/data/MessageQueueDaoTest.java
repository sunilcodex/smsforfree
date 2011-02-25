/**
 * Copyright (C) 2011 Alfredo Morresi
 * 
 * This file is part of TextMessageHolmes project.
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
package it.rainbowbreeze.smsforfree.data;

import it.rainbowbreeze.smsforfree.common.AppEnv;
import it.rainbowbreeze.smsforfree.domain.TextMessage;
import it.rainbowbreeze.smsforfree.util.TestHelper;
import android.test.AndroidTestCase;

/**
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class MessageQueueDaoTest extends AndroidTestCase {

	//---------- Constructor

	
	
	
	//---------- Private fields
    private MessageQueueDao mDao;

	
	
	
	//---------- Test initialization
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestHelper.init(getContext());
        
        mDao = AppEnv.i(getContext()).getMessageQueueDao();
        mDao.clearDatabaseComplete();
    }

	
	
	
	//---------- Test cases	
    public void testInsert() {
        TextMessage textMessage;

        //insert first message
        textMessage = createTextMessage1();
        long textMessageId1 = mDao.insert(textMessage);
        compareWithTextMessage1(textMessageId1);
        
        //insert second text message
        textMessage = createTextMessage2();
        long textMessageId2 = mDao.insert(textMessage);
        compareWithTextMessage2(textMessageId2);
    }
        
	public void testDelete() {
        TextMessage textMessage;
        TextMessage loadedTextMessage1;
        TextMessage loadedTextMessage2;
    	
        //insert text messages
        textMessage = createTextMessage1();
        long TextMessageId1 = mDao.insert(textMessage);
        textMessage = createTextMessage2();
        long TextMessageId2 = mDao.insert(textMessage);
        
        //check if TextMessages could be retrieved
        loadedTextMessage1 = mDao.getById(TextMessageId1);
        loadedTextMessage2 = mDao.getById(TextMessageId2);
        assertNotNull("TextMessage1 doesn't exists", loadedTextMessage1);
        assertNotNull("TextMessage2 doesn't exists", loadedTextMessage2);
        //delete first TextMessage
        mDao.delete(TextMessageId1);
        loadedTextMessage1 = mDao.getById(TextMessageId1);
        loadedTextMessage2 = mDao.getById(TextMessageId2);
        assertNull("TextMessage1 still exists", loadedTextMessage1);
        assertNotNull("TextMessage2 doesn't exists", loadedTextMessage2);
        //delete second TextMessage
        mDao.delete(TextMessageId2);
        loadedTextMessage1 = mDao.getById(TextMessageId1);
        loadedTextMessage2 = mDao.getById(TextMessageId2);
        assertNull("TextMessage1 still exists", loadedTextMessage1);
        assertNull("TextMessage2 still exists", loadedTextMessage2);
    }
    
    public void testGetById_NoTextMessage(){
    	TextMessage TextMessage = mDao.getById(1243123);
    	assertNull("TextMessage exists", TextMessage);
    }

    public void testSetProcessingStatus() {
    	TextMessage textMessage;
    	int count;
    	
    	count = mDao.setProcessingStatus(11231, 4);
    	assertEquals("Wrong count operation", 0, count);
    	
        textMessage = createTextMessage1();
        long textMessageId = mDao.insert(textMessage);
    	compareWithTextMessage1(textMessageId);

        count = mDao.setProcessingStatus(textMessageId, TextMessage.PROCESSING_SENT);
    	assertEquals("Wrong count operation", 1, count);
        textMessage = mDao.getById(textMessageId);
        assertEquals("Wrong preferred status", TextMessage.PROCESSING_SENT, textMessage.getProcessingStatus());

        count = mDao.setProcessingStatus(textMessageId, TextMessage.PROCESSING_ERROR_SENDING);
    	assertEquals("Wrong count operation", 1, count);
        textMessage = mDao.getById(textMessageId);
        assertEquals("Wrong preferred status", TextMessage.PROCESSING_ERROR_SENDING, textMessage.getProcessingStatus());
    }
    
    public void testIsDatabaseEmpty() {
    	assertTrue("Database not empty", mDao.isDatabaseEmpty());
    	
    	//insert a TextMessage
        TextMessage textMessage = createTextMessage1();
        mDao.insert(textMessage);
    	assertFalse("Database is empty", mDao.isDatabaseEmpty());
    	
    	mDao.clearDatabaseComplete();
    	assertTrue("Database not empty", mDao.isDatabaseEmpty());
    }
    
    
    public void testClearDatabaseComplete() {
		long textMessageId1 = mDao.insert(createTextMessage1());
		assertNotSame("Cannot create text message", 0, textMessageId1);
		long textMessageId2 = mDao.insert(createTextMessage2());
		assertNotSame("Cannot create text message", 0, textMessageId2);
		long textMessageId3 = mDao.insert(createTextMessage3());
		assertNotSame("Cannot create TextMessage", 0, textMessageId3);
		
		mDao.clearDatabaseComplete();
		assertNull("TextMessage 1 still exists", mDao.getById(textMessageId1));
		assertNull("TextMessage 2 still exists", mDao.getById(textMessageId2));
		assertNull("TextMessage 3 still exists", mDao.getById(textMessageId3));
    }
    
    

    
	//---------- Private methods
	private TextMessage createTextMessage1() {
		return TextMessage.Factory.create(
				123,
				"+393331234567",
				"Test message from Alfredo's phone! It's all ok?", 
				"JACKSMS",
				"Vodafone",
				TextMessage.PROCESSING_NONE);
	}

	/**
	 * @param textMessageId
	 */
	private void compareWithTextMessage1(long textMessageId) {
		TextMessage textMessage;
		textMessage = mDao.getById(textMessageId);
        assertEquals("Wrong id", textMessageId, textMessage.getId());
        assertEquals("Wrong destination", "+393331234567", textMessage.getDestination());
        assertEquals("Wrong message", "Test message from Alfredo's phone! It's all ok?", textMessage.getMessage());
        assertEquals("Wrong providerId", "JACKSMS", textMessage.getProviderId());
        assertEquals("Wrong serviceId", "Vodafone", textMessage.getServiceId());
        assertEquals("Wrong processingStatus", TextMessage.PROCESSING_NONE, textMessage.getProcessingStatus());
	}
    
    private TextMessage createTextMessage2() {
		return TextMessage.Factory.create(
				76,
				"+399877654321",
				"Another message to test, this time!!!%%$$", 
				"INTERNAL",
				null,
				TextMessage.PROCESSING_QUEUED);
	}

    private void compareWithTextMessage2(long textMessageId) {
		TextMessage textMessage;
		textMessage = mDao.getById(textMessageId);
        assertEquals("Wrong id", textMessageId, textMessage.getId());
        assertEquals("Wrong destination", "+399877654321", textMessage.getDestination());
        assertEquals("Wrong message", "Another message to test, this time!!!%%$$", textMessage.getMessage());
        assertEquals("Wrong providerId", "INTERNAL", textMessage.getProviderId());
        assertNull("Wrong serviceId", textMessage.getServiceId());
        assertEquals("Wrong processingStatus", TextMessage.PROCESSING_QUEUED, textMessage.getProcessingStatus());
	}

    private TextMessage createTextMessage3() {
		return TextMessage.Factory.create(
				99,
				"+002-(635)21-34235",
				"Loooong message to my american friends. how do you do? I hope well for you, it' all ok? let me know when next visit will happens. Cheers", 
				"VOIPSTUNT",
				null,
				TextMessage.PROCESSING_ERROR_SENDING);
	}

}
