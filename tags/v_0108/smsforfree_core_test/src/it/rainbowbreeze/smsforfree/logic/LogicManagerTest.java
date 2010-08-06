package it.rainbowbreeze.smsforfree.logic;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.common.TestUtils;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.logic.LogicManager;
import it.rainbowbreeze.smsforfree.util.GlobalUtils;
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
	
	
	public void testChecksTemplatesValues() {
		String[] expectedTemplates;
		String[] newTemplates;
		ResultOperation<Void> res;
		
		//add a templates to the list message templates
		expectedTemplates = AppPreferencesDao.instance().getMessageTemplates();
		expectedTemplates = (String[]) GlobalUtils.resizeArray(expectedTemplates, expectedTemplates.length + 1);
		expectedTemplates[expectedTemplates.length - 1] = "New inserted templates";
		AppPreferencesDao.instance().setMessageTemplates(expectedTemplates);
		AppPreferencesDao.instance().save();
		//execute templates check
		res = LogicManager.checksTemplatesValues(getContext());
		assertFalse("Operation has errors", res.hasErrors());
		//compare old and new templates
		newTemplates = AppPreferencesDao.instance().getMessageTemplates();
		assertEquals("Wrong number of templates", expectedTemplates.length, newTemplates.length);
		for (int i = 0; i < expectedTemplates.length; i++)
			assertEquals("Wrong template value at index " + i, expectedTemplates[i], newTemplates[i]);
		
		//reset templates
		expectedTemplates = new String[]{};
		AppPreferencesDao.instance().setMessageTemplates(expectedTemplates);
		AppPreferencesDao.instance().save();
		res = LogicManager.checksTemplatesValues(getContext());
		assertFalse("Operation has errors", res.hasErrors());
		//compare old and new templates
		expectedTemplates = getContext().getString(R.string.common_defaultMessageTemplates).split("§§§§");
		newTemplates = AppPreferencesDao.instance().getMessageTemplates();
		assertEquals("Wrong number of templates", expectedTemplates.length, newTemplates.length);
		for (int i = 0; i < expectedTemplates.length; i++)
			assertEquals("Wrong template value at index " + i, expectedTemplates[i], newTemplates[i]);
	}

	//---------- Private methods

}
