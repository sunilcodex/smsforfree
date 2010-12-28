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

import it.rainbowbreeze.libs.common.RainbowServiceLocator;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.util.TestHelper;
import android.test.AndroidTestCase;


/**
 * Base class for dictionary testcases
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public abstract class BaseDictionaryTest
	extends AndroidTestCase
{
	//---------- Private fields
    protected LogFacility mLogFacility;
	
	

	//---------- Constructor
	
	
	//---------- SetUp and TearDown
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
        TestHelper.init(getContext());
        mLogFacility = RainbowServiceLocator.get(LogFacility.class);
	}




	//---------- Tests methods



	//---------- Private methods

}