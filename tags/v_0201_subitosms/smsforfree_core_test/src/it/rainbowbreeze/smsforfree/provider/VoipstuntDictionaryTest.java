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

import it.rainbowbreeze.smsforfree.providers.VoipstuntDictionary;

/**
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class VoipstuntDictionaryTest
	extends BaseDictionaryTest
{
	//---------- Private fields
	private VoipstuntDictionary mDictionary;

	
	//---------- Constructor

	
	//---------- SetUp and TearDown
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		mDictionary = new VoipstuntDictionary(mLogFacility);
	}

	//---------- Tests methods
	public void testDeserializeProviderReply()
	{
		String providerString = 
		    "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
		    "<SmsResponse>\n" + 
		    "<version>1</version>\n" + 
		    "<result>0</result>\n" + 
		    "<resultstring>failure</resultstring>\n" + 
		    "<description>The text message you are trying to send is larger than 160 characters, please shorten your message.</description>\n" + 
		    "<endcause></endcause>\n" + 
		    "</SmsResponse>";
		
		VoipstuntDictionary.ProviderReply providerReply = mDictionary.deserializeProviderReply(providerString);
		assertNotNull("Null parse result", providerReply);
        assertEquals("Wrong error code", 0, providerReply.resultCode);
		assertEquals("Wrong error description",
		        "The text message you are trying to send is larger than 160 characters, please shorten your message.",
		        providerReply.description);
	}


	//---------- Private methods

}
