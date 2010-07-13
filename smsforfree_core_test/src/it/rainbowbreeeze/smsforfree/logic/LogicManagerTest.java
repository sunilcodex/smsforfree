package it.rainbowbreeeze.smsforfree.logic;

import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.TestUtils;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.logic.LogicManager;
import android.test.AndroidTestCase;

public class LogicManagerTest extends AndroidTestCase {
	//---------- Private fields

	//---------- Constructor

	//---------- SetUp and TearDown
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		//mock some values of SmsForFreeApplication
		TestUtils.loadAppPreferences(mContext);
	}

	//---------- Tests methods
	
	/**
	 * Test when the application should be upgraded or not
	 */
	public void testApplicationUpgrade() {
		
		String saveSwVersion = AppPreferencesDao.instance().getAppVersion();
		
		//no need to upgrade
		AppPreferencesDao.instance().setAppVersion(GlobalDef.appVersion);
		assertFalse("The app doesn't need to be upgraded", LogicManager.isNewAppVersion());
		
		//upgrade needed
		AppPreferencesDao.instance().setAppVersion("00.00.00");
		AppPreferencesDao.instance().save();
		assertTrue("The app must be upgraded", LogicManager.isNewAppVersion());
		
		AppPreferencesDao.instance().setAppVersion(saveSwVersion);
		AppPreferencesDao.instance().save();
	}

	//---------- Private methods

}
