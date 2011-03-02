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
    private IMessageQueueDao mDao;
    private boolean mForceReload = false;

	
	
	
	//---------- Test initialization
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestHelper.init(getContext(), mForceReload);
        mForceReload = false;
        
        mDao = AppEnv.i(getContext()).getMessageQueueDao();
        mDao.clearDatabaseComplete();
    }

	
	
	
	//---------- Test cases	
    public void testInsert() {
        TextMessage textMessage;

        //insert first message
        textMessage = TestHelper.createTextMessage1();
        long textMessageId1 = mDao.insert(textMessage);
        TestHelper.compareWithTextMessage1(mDao, textMessageId1);
        
        //insert second text message
        textMessage = TestHelper.createTextMessage2();
        long textMessageId2 = mDao.insert(textMessage);
        TestHelper.compareWithTextMessage2(mDao, textMessageId2);
    }
        
	public void testDelete() {
        TextMessage textMessage;
        TextMessage loadedTextMessage1;
        TextMessage loadedTextMessage2;
    	
        //insert text messages
        textMessage = TestHelper.createTextMessage1();
        long TextMessageId1 = mDao.insert(textMessage);
        textMessage = TestHelper.createTextMessage2();
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
    	
        textMessage = TestHelper.createTextMessage1();
        long textMessageId = mDao.insert(textMessage);
    	TestHelper.compareWithTextMessage1(mDao, textMessageId);

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
        TextMessage textMessage = TestHelper.createTextMessage1();
        mDao.insert(textMessage);
    	assertFalse("Database is empty", mDao.isDatabaseEmpty());
    	
    	mDao.clearDatabaseComplete();
    	assertTrue("Database not empty", mDao.isDatabaseEmpty());
    }
    
    
    public void testClearDatabaseComplete() {
		long textMessageId1 = mDao.insert(TestHelper.createTextMessage1());
		assertNotSame("Cannot create text message", 0, textMessageId1);
		long textMessageId2 = mDao.insert(TestHelper.createTextMessage2());
		assertNotSame("Cannot create text message", 0, textMessageId2);
		long textMessageId3 = mDao.insert(TestHelper.createTextMessage3());
		assertNotSame("Cannot create TextMessage", 0, textMessageId3);
		
		mDao.clearDatabaseComplete();
		assertNull("TextMessage 1 still exists", mDao.getById(textMessageId1));
		assertNull("TextMessage 2 still exists", mDao.getById(textMessageId2));
		assertNull("TextMessage 3 still exists", mDao.getById(textMessageId3));
    }
    
    

    
	//---------- Private methods
    
}
