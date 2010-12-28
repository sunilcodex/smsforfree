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

import it.rainbowbreeze.smsforfree.providers.AimonDictionary;
import junit.framework.TestCase;

/**
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class AimonDictionaryTest
	extends TestCase
{
	//---------- Private fields
	private AimonDictionary mDictionary;

	//---------- Constructor

	//---------- SetUp and TearDown
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		mDictionary = new AimonDictionary();
	}

	//---------- Tests methods
	public void testFreeSmsRemainigCredits()
	{
		String credits;
		
		//no credit info in the message
		credits = mDictionary.findRemainingCreditsForFreeSms("Credito residuo giornali");
		assertEquals("Wrong credits", "--", credits);
		//credit info in the message
		credits = mDictionary.findRemainingCreditsForFreeSms("Credito residuo giornaliero: 3 crediti/sms");
		assertEquals("Wrong credits", "3", credits);
	}


	//---------- Private methods

}
