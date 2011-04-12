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



/**
 * 
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
import it.rainbowbreeze.libs.common.RainbowServiceLocator;
import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.providers.VoipstuntProvider;
import it.rainbowbreeze.smsforfree.ui.ActivityHelper;
import it.rainbowbreeze.smsforfree.util.Def;

public class VoipstuntProviderTest
	extends BaseProviderTest
{

	//---------- Private fields

	
	
	
	//---------- Constructor

	
	
	
	//---------- SetUp and TearDown

	
	
	
	//---------- Tests methods
	
	@Override
	public void testAndroidTestCaseSetupProperly()
	{
		super.testAndroidTestCaseSetupProperly();
		
		//check if password and sender number was changed
		//before running this test
		assertFalse("You must change the password...", "XXXX".equals(Def.VOIPSTUNT_PASSWORD));
	}
	
	public void testProviderInitialization()
	{
		assertTrue("Providers hasn't settings activity commands", mProvider.hasSettingsActivityCommands());
		assertEquals("Wrong number of settings activity commands", 1, mProvider.getSettingsActivityCommands().size());
		
		assertFalse("Providers has subservices activity commands", mProvider.hasSubservicesListActivityCommands());
	}

	public void testMaxMessageLenght() {
	}
	
    /**
     * Test the call for right credential
     */
    public void testSendMessage_WrongCredential() {
        ResultOperation<String> res;
        String saveData;

        //wrong username
        saveData = mProvider.getParameterValue(0);
        res = mProvider.sendMessage(null, Def.TEST_DESTINATION, "message body");
        mProvider.setParameterValue(0, saveData);
        assertTrue("No errors", res.hasErrors());
        assertEquals("Wrong return code",
                ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY,
                res.getReturnCode());
        assertEquals("Wrong error message",
                getContext().getString(R.string.voipstunt_msg_messageNotSent),
                res.getException().getMessage());

        //wrong password
        saveData = mProvider.getParameterValue(1);
        res = mProvider.sendMessage(null, Def.TEST_DESTINATION, "message body");
        mProvider.setParameterValue(1, saveData);
        assertTrue("No errors", res.hasErrors());
        assertEquals("Wrong return code",
                ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY,
                res.getReturnCode());
        assertEquals("Wrong error message",
                getContext().getString(R.string.voipstunt_msg_messageNotSent),
                res.getException().getMessage());
    }   

    public void testSendMessage_WrongSender() {
        ResultOperation<String> res;
        String saveData;

        //wrong destination
        saveData = mProvider.getParameterValue(2);
        res = mProvider.sendMessage(null, Def.TEST_DESTINATION, "message body");
        mProvider.setParameterValue(2, saveData);
        assertTrue("No errors", res.hasErrors());
        assertEquals("Wrong return code",
                ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY,
                res.getReturnCode());
        assertEquals("Wrong error message",
                getContext().getString(R.string.voipstunt_msg_messageNotSent),
                res.getException().getMessage());
    }   

    public void testSendMessage_WrongDestination() {
        ResultOperation<String> res;

        //wrong destination
        res = mProvider.sendMessage(null, "adbase", "message body");
        assertTrue("No errors", res.hasErrors());
        assertEquals("Wrong return code",
                ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY,
                res.getReturnCode());
        assertEquals("Wrong error message",
                getContext().getString(R.string.voipstunt_msg_messageNotSent),
                res.getException().getMessage());
    }   

    public void testSendMessage_WithStrangeChars() {
	    ResultOperation<String> res;
	    
	    res = mProvider.sendMessage(null, Def.TEST_DESTINATION, "èàè#$");
	    assertTrue("No errors", res.hasErrors());
	    assertEquals("Wrong return code",
	            ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY,
	            res.getReturnCode());
        assertEquals("Wrong error message",
                getContext().getString(R.string.voipstunt_msg_messageNotSent),
                res.getException().getMessage());
	}
    
    
    public void testSendMessage_TooLongMessage() {
        ResultOperation<String> res;
        
        res = mProvider.sendMessage(null, Def.TEST_DESTINATION, "Ehi ma ciao ti ho mandato una cifra di messaggi sulla vodafone ma ho visto che é spenta..vado a fare una doccia,se puoi ti chiamo a casa tra un quarto d'ora..");
        assertTrue("No errors", res.hasErrors());
        assertEquals("Wrong return code",
                ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY,
                res.getReturnCode());
        assertEquals("Wrong error message",
                getContext().getString(R.string.voipstunt_msg_messageNotSent),
                res.getException().getMessage());
    }
   



	//---------- Private methods
	@Override
	protected SmsProvider createProvider() {
        return new VoipstuntProvider(
                mLogFacility,
                RainbowServiceLocator.get(AppPreferencesDao.class),
                mProviderDao,
                RainbowServiceLocator.get(ActivityHelper.class));
	}

	@Override
	protected void initProviderParams() {
		//set test parameters
		mProvider.setParameterValue(0, Def.VOIPSTUNT_USERNAME);
		mProvider.setParameterValue(1, Def.VOIPSTUNT_PASSWORD);
		mProvider.setParameterValue(2, Def.VOIPSTUNT_SENDER);
	}

}
