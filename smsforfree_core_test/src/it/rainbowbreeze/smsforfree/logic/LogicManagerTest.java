package it.rainbowbreeze.smsforfree.logic;

import it.rainbowbreeze.libs.common.RainbowServiceLocator;
import it.rainbowbreeze.libs.helper.RainbowArrayHelper;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.logic.LogicManager;
import it.rainbowbreeze.smsforfree.util.TestHelper;
import android.test.AndroidTestCase;

public class LogicManagerTest extends AndroidTestCase {
	//---------- Private fields
    private boolean mForceReload = false;

    
    
    
    //---------- Test initialization
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestHelper.init(getContext(), mForceReload);
        mForceReload = false;
	}
    
    
    

	//---------- Tests methods
	
	public void testChecksTemplatesValues() {
		String[] expectedTemplates;
		String[] newTemplates;
		ResultOperation<Void> res;

		AppPreferencesDao appPreferencesDao = RainbowServiceLocator.get(AppPreferencesDao.class);
        LogicManager logicManager = RainbowServiceLocator.get(LogicManager.class);
		
		//add a templates to the list message templates
		expectedTemplates = appPreferencesDao.getMessageTemplates();
		expectedTemplates = (String[]) RainbowArrayHelper.resizeArray(expectedTemplates, expectedTemplates.length + 1);
		expectedTemplates[expectedTemplates.length - 1] = "New inserted templates";
		appPreferencesDao.setMessageTemplates(expectedTemplates);
		appPreferencesDao.save();
		//execute templates check
		res = logicManager.checksTemplatesValues(getContext());
		assertFalse("Operation has errors", res.hasErrors());
		//compare old and new templates
		newTemplates = appPreferencesDao.getMessageTemplates();
		assertEquals("Wrong number of templates", expectedTemplates.length, newTemplates.length);
		for (int i = 0; i < expectedTemplates.length; i++)
			assertEquals("Wrong template value at index " + i, expectedTemplates[i], newTemplates[i]);
		
		//reset templates
		expectedTemplates = new String[]{};
		appPreferencesDao.setMessageTemplates(expectedTemplates);
		appPreferencesDao.save();
		res = logicManager.checksTemplatesValues(getContext());
		assertFalse("Operation has errors", res.hasErrors());
		//compare old and new templates
		expectedTemplates = getContext().getString(R.string.common_defaultMessageTemplates).split("§§§§");
		newTemplates = appPreferencesDao.getMessageTemplates();
		assertEquals("Wrong number of templates", expectedTemplates.length, newTemplates.length);
		for (int i = 0; i < expectedTemplates.length; i++)
			assertEquals("Wrong template value at index " + i, expectedTemplates[i], newTemplates[i]);
	}

	//---------- Private methods

}
