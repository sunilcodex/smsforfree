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

package it.rainbowbreeze.smsforfree.data;

import it.rainbowbreeze.smsforfree.common.Def;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import android.test.AndroidTestCase;
import android.text.TextUtils;
import android.util.Log;

/**
 * Test class for {@link SmsDao}
 *
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class SmsDaoTest extends AndroidTestCase {
	//---------- Private fields

	
	
	
	//---------- Constructor
	
	
	

	//---------- SetUp and TearDown

	
	
	
	//---------- Tests methods
	public void testIsSentSmsProviderAvailable()
	{
		assertTrue(SmsDao.instance().isSentSmsProviderAvailable(getContext()));
	}
	
	public void testIsInboxSmsProviderAvailable()
	{
		assertTrue(SmsDao.instance().isInboxSmsProviderAvailable(getContext()));
	}
	
	public void testSaveSmsInSentFolder()
	{
		int totalSmsBefore = SmsDao.instance().getMessagesNumberInSentFolder(getContext());
		Log.i("SmsForFree-Test", "SMS before the insert: " + totalSmsBefore);
		assertTrue("Wrong number of SMS in Sent Folder", totalSmsBefore >= 0);
		
		ResultOperation<Void> res = SmsDao.instance().saveSmsInSentFolder(
				getContext(), Def.TEST_DESTINATION, "Test message");
		assertFalse("Error in request", res.hasErrors());
		
		int totalSmsAfter = SmsDao.instance().getMessagesNumberInSentFolder(getContext());
		Log.i("SmsForFree-Test", "SMS after the insert: " + totalSmsAfter);
		assertTrue("Wrong number of SMS in Sent Folder", totalSmsAfter >= 0);
		
		assertTrue("Sms insert doesn't work", totalSmsAfter > totalSmsBefore);
	}
	
	/**
	 * If this test fails in the emulator / device, add at least an SMS to the queue
	 * (the emulator has it's own panel to do this
	 */
	public void testRetrieveLastSmsRecievedNumber()
	{
		ResultOperation<String> res = SmsDao.instance().getLastSmsReceivedNumber(getContext());
		assertFalse("Error in request", res.hasErrors());
		Log.i("SmsForFree-Test", "SMS last sender: " + res.getResult());
		assertFalse("Empty message number", TextUtils.isEmpty(res.getResult()));
	}
	
	
	//---------- Private methods

}
