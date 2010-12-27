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
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsServiceParameter;
import it.rainbowbreeze.smsforfree.util.Def;
import it.rainbowbreeze.smsforfree.util.TestHelper;
import android.test.AndroidTestCase;


/**
 * Base class for provider testcases
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public abstract class BaseProviderTest
	extends AndroidTestCase
{
	//---------- Private fields
	protected SmsProvider mProvider;
	protected ProviderDao mProviderDao;
    protected LogFacility mLogFacility;
	protected SmsServiceParameter[] mBackupParameters;
	
	

	//---------- Constructor
	public BaseProviderTest() {
		super();
		
		TestHelper.init(getContext());
		mLogFacility = RainbowServiceLocator.get(LogFacility.class);
		mProviderDao = new ProviderDao();
	}
	
	
	//---------- SetUp and TearDown
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		mProvider = createProvider();
		ResultOperation<Void> res = mProvider.initProvider(getContext());
		assertFalse("provider initialization with errors", res.hasErrors());

		//mock some values of SmsForFreeApplication
		//FIXME
		//TestHelper.loadAppPreferences(getContext());

		//save provider parameters
		mBackupParameters = TestHelper.backupServiceParameters(mProvider);
		
		initProviderParams();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		//restore modified parameters.
		TestHelper.restoreServiceParameters(mProvider, mBackupParameters);
	}



	//---------- Tests methods
	@Override
	public void testAndroidTestCaseSetupProperly() {
		super.testAndroidTestCaseSetupProperly();
		
		assertFalse("You must change destination", "XXXX".equals(Def.TEST_DESTINATION));
	}



	//---------- Private methods

	/**
	 * Create a new instance of a provider used in the tests
	 */
	protected abstract SmsProvider createProvider();
	
	/**
	 * Initialize provider parameters
	 */
	protected abstract void initProviderParams();


}