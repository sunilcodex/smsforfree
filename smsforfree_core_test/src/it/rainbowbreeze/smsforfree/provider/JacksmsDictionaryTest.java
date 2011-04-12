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

package it.rainbowbreeze.smsforfree.provider;

import java.util.List;

import android.test.AndroidTestCase;

import it.rainbowbreeze.libs.common.RainbowServiceLocator;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.domain.SmsConfigurableService;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.providers.JacksmsDictionary;
import it.rainbowbreeze.smsforfree.util.TestHelper;

/**
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class JacksmsDictionaryTest extends AndroidTestCase {
	//---------- Private fields
	private JacksmsDictionary mDictionary;
	private LogFacility mLogFacility;




	//---------- Constructor




	//---------- SetUp and TearDown
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		TestHelper.init(getContext());
        mLogFacility = RainbowServiceLocator.get(LogFacility.class);
		mDictionary = new JacksmsDictionary(mLogFacility);
	}

	
	
	
	//---------- Tests methods
	/**
	 * Test if translation of user saved account works
	 */
	public void testTranslateSingleStoredUserAccount1()
	{
		String providerReply = "57926	2	Rossoalice	YWFhYQ==	YmJiYg==	Y2NjYw==	ZGRkZA==";

		List<SmsConfigurableService> services = mDictionary.extractUserServices(providerReply);
		assertEquals("Wrong service number", 1, services.size());
		SmsService service = services.get(0);
		assertEquals("Wrong service id", "57926", service.getId());
		assertEquals("Wrong service template id", "2", service.getTemplateId());
		assertEquals("Wrong service name", "Rossoalice", service.getName());
		assertEquals("Wrong service parameters number", 4, service.getParametersNumber());
		assertEquals("Wrong service parameters 0 value", "aaaa", service.getParameterValue(0));
		assertEquals("Wrong service parameters 1 value", "bbbb", service.getParameterValue(1));
		assertEquals("Wrong service parameters 2 value", "cccc", service.getParameterValue(2));
		assertEquals("Wrong service parameters 3 value", "dddd", service.getParameterValue(3));
	}
	

	/**
	 * Test if translation of user saved account works
	 */
	public void testTranslateSingleStoredUserAccount2()
	{
		String providerReply = "58302	61	AimonFree	YWFhYQ==	YmJiYg==	Y2NjYw==	";

		List<SmsConfigurableService> services = mDictionary.extractUserServices(providerReply);
		assertEquals("Wrong service number", 1, services.size());
		SmsService service = services.get(0);
		assertEquals("Wrong service id", "58302", service.getId());
		assertEquals("Wrong service template id", "61", service.getTemplateId());
		assertEquals("Wrong service name", "AimonFree", service.getName());
		assertEquals("Wrong service parameters number", 3, service.getParametersNumber());
		assertEquals("Wrong service parameters 0 value", "aaaa", service.getParameterValue(0));
		assertEquals("Wrong service parameters 1 value", "bbbb", service.getParameterValue(1));
		assertEquals("Wrong service parameters 2 value", "cccc", service.getParameterValue(2));
	}

	/**
	 * Test if translation of user saved account works
	 */
	public void testTranslateMultipleStoredUserAccounts()
	{
		String returnChar = String.valueOf((char) 10);
		String providerReply = "57926	2	Rossoalice	YWFhYQ==	YmJiYg==	Y2NjYw==	ZGRkZA==" + returnChar +
						"57922	61	AimonTest	YWFhYQ==	YmJiYg==	Y2NjYw==	";
		List<SmsConfigurableService> services = mDictionary.extractUserServices(providerReply);
		assertEquals("Wrong service number", 2, services.size());

		SmsService service = services.get(0);
		assertEquals("Wrong service id", "57922", service.getId());
		assertEquals("Wrong number of service parameters", new Integer(3), new Integer(service.getParametersNumber()));
		assertEquals("Wrong service template id", "61", service.getTemplateId());
		assertEquals("Wrong service name", "AimonTest", service.getName());
		assertEquals("Wrong service parameters number", 3, service.getParametersNumber());
		assertEquals("Wrong service parameters 0 value", "aaaa", service.getParameterValue(0));
		assertEquals("Wrong service parameters 1 value", "bbbb", service.getParameterValue(1));
		assertEquals("Wrong service parameters 2 value", "cccc", service.getParameterValue(2));
		//i don't know the max length of the message
		assertEquals("Wrong max message size", 0, service.getMaxMessageLenght());

		service = services.get(1);
		assertEquals("Wrong service id", "57926", service.getId());
		assertEquals("Wrong service template id", "2", service.getTemplateId());
		assertEquals("Wrong service name", "Rossoalice", service.getName());
		assertEquals("Wrong service parameters number", 4, service.getParametersNumber());
		assertEquals("Wrong service parameters 0 value", "aaaa", service.getParameterValue(0));
		assertEquals("Wrong service parameters 1 value", "bbbb", service.getParameterValue(1));
		assertEquals("Wrong service parameters 2 value", "cccc", service.getParameterValue(2));
		assertEquals("Wrong service parameters 3 value", "dddd", service.getParameterValue(3));
		//i don't know the max length of the message
		assertEquals("Wrong max message size", 0, service.getMaxMessageLenght());
	}
	
	
	

	/**
	 * Test if code for find if the sms was sent, a captcha is needed or there was another
	 * error works
	 */
	public void testWebserviceReplyRecognition() {
		String serverReply;

		//empty
		serverReply = "";
		assertFalse("Wrong sent sms identification", mDictionary.isSmsCorrectlySent(serverReply));
		assertFalse("Wrong captcha identification", mDictionary.isCaptchaRequest(serverReply));
		assertTrue("Wrong error reply identification", mDictionary.isErrorReply(serverReply));
		assertTrue("Wrong unmanaged error reply identification", mDictionary.isUnmanagedErrorReply(serverReply));
		
		//captcha
		serverReply = "3617	iVBORw0KGgoAAAANSUhEUgAAAFUAAAAWCAIAAAA+W0fPAAABHklEQVRYhe1YXQ/DIAgcy/7/X2YPTRqjcIcU23TdPVZEPg7Eiqq+HowPXhaR7sser3HJRBtfsMVMQyc/yuwCYAlreHsGeQi6XaIWc7PEEpL/l59Az7iIjKlTREZ5VcUC5inblgj1pvOPsYIdqtoVnYgA5k+B558WoYl1bZXya0w+sIf7P2qPMB+vJqq9LYRCcP5rg+2LaUdLSFqlo0yEL4ktFDX1n6vG4NXoyZTQYc5/s9MeaUWzISjHnP8gA2k20srqlmpDxv2XBq0FtWwEIcAUO3iu28xDmyvynz7CC4p5/7n603Yn5v+DR8Tn/3gIpu//M0Ed2IaC+DxiRHDpoHbh43rV+y+OW/xZuDJFJ4A2gh/3n2Ih/2+Bv//PxhfjgdscfiyFSwAAAABJRU5ErkJggg==	1";
		assertFalse("Wrong sent sms identification", mDictionary.isSmsCorrectlySent(serverReply));
		assertTrue("Wrong captcha identification", mDictionary.isCaptchaRequest(serverReply));
		assertFalse("Wrong error reply identification", mDictionary.isErrorReply(serverReply));
		assertFalse("Wrong unmanaged error reply identification", mDictionary.isUnmanagedErrorReply(serverReply));

		//sent sms
		serverReply = "1	messaggio spedito con successo";
		assertTrue("Wrong sent sms identification", mDictionary.isSmsCorrectlySent(serverReply));
		assertFalse("Wrong captcha identification", mDictionary.isCaptchaRequest(serverReply));
		assertFalse("Wrong error reply identification", mDictionary.isErrorReply(serverReply));
		assertFalse("Wrong unmanaged error reply identification", mDictionary.isUnmanagedErrorReply(serverReply));
		
		//generic error
		serverReply = "error	generic error message";
		assertFalse("Wrong sent sms identification", mDictionary.isSmsCorrectlySent(serverReply));
		assertFalse("Wrong captcha identification", mDictionary.isCaptchaRequest(serverReply));
		assertTrue("Wrong error reply identification", mDictionary.isErrorReply(serverReply));
		assertFalse("Wrong unmanaged error reply identification", mDictionary.isUnmanagedErrorReply(serverReply));
		
		//strange reply from service
		serverReply = "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">" + "\n" +
			"<html><head>" + "\n" +
			"<title>400 Bad Request</title>" + "\n" +
			"</head><body>" + "\n" +
			"<h1>Bad Request</h1>" + "\n" +
			"<p>Your browser sent a request that this server could not understand.<br />" + "\n" +
			"</p>" + "\n" +
			"</body></html>";
		assertFalse("Wrong sent sms identification", mDictionary.isSmsCorrectlySent(serverReply));
		assertFalse("Wrong captcha identification", mDictionary.isCaptchaRequest(serverReply));
		assertFalse("Wrong error reply identification", mDictionary.isErrorReply(serverReply));
		assertTrue("Wrong unmanaged error reply identification", mDictionary.isUnmanagedErrorReply(serverReply));
	}
	
	
	public void testAdjustMessageBody() {
		assertEquals("Wrong string manipulation", "ciao da me", mDictionary.adjustMessageBody("ciao\nda me"));
		assertEquals("Wrong string manipulation", "ciao da me", mDictionary.adjustMessageBody("ciao\rda me"));
		assertEquals("Wrong string manipulation", "ciao da me", mDictionary.adjustMessageBody("ciao\tda me"));
	}

	
	
	
	
	//---------- Private methods

}
