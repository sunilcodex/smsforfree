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
import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.providers.SubitosmsProvider;
import it.rainbowbreeze.smsforfree.ui.ActivityHelper;
import it.rainbowbreeze.smsforfree.util.Def;
import android.os.Bundle;

/**
 * Test class for SubitoSMS provider
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class SubitosmsProviderTest
	extends BaseProviderTest
{
	//---------- Private fields
	private static final String USER_CREDITS = "0";
	
	
	

	//---------- Constructor




	//---------- SetUp and TearDown




	//---------- Tests methods

	@Override
	public void testAndroidTestCaseSetupProperly()
	{
		super.testAndroidTestCaseSetupProperly();
		
		//check if password and sender number was changed
		//before running this test
		assertFalse("You must change the password...", "XXXX".equals(Def.SUBITOSMS_PASSWORD));
	}
	
	
	public void testProviderInitialization()
	{
		assertTrue("Providers hasn't settings activity commands", mProvider.hasSettingsActivityCommands());
		assertEquals("Wrong number of settings activity commands", 3, mProvider.getSettingsActivityCommands().size());
		
		assertFalse("Providers has subservices activity commands", mProvider.hasSubservicesListActivityCommands());
	}
	
	/**
	 * Test the call for right credential
	 */
	public void testApiCheckCredential()
	{
		ResultOperation<String> res;
		Bundle bundle = new Bundle();
		
		//wrong username and password
		bundle.putString("0", "XXXX");
		bundle.putString("1", "XXXX");
		res = mProvider.executeCommand(SubitosmsProvider.COMMAND_CHECKCREDENTIALS, getContext(), bundle);
        assertEquals("Wrong returncode",
                ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY,
                res.getReturnCode());
		assertEquals("Wrong error message",
		        getContext().getString(R.string.subitosms_msg_invalidCredentials),
		        res.getException().getMessage());

		//user with wrong password
		bundle.clear();
		bundle.putString("0", Def.SUBITOSMS_USERNAME);
		bundle.putString("1", "XXXX");
		res = mProvider.executeCommand(SubitosmsProvider.COMMAND_CHECKCREDENTIALS, getContext(), bundle);
        assertEquals("Wrong return code",
                ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY,
                res.getReturnCode());
		assertEquals("Wrong error message",
		        getContext().getString(R.string.subitosms_msg_invalidCredentials),
		        res.getException().getMessage());

		//user with good password
		bundle.clear();
		bundle.putString("0", Def.SUBITOSMS_USERNAME);
		bundle.putString("1", Def.SUBITOSMS_PASSWORD);
		res = mProvider.executeCommand(SubitosmsProvider.COMMAND_CHECKCREDENTIALS, getContext(), bundle);
		assertEquals("Wrong returncode",
		        ResultOperation.RETURNCODE_OK,
		        res.getReturnCode());
		assertEquals("Wrong return message",
		        getContext().getString(R.string.subitosms_msg_validCredentials),
		        res.getResult());
	}	


	public void testApiCheckCredits()
	{
		ResultOperation<String> res;
		
		//user with right password
		Bundle bundle = new Bundle();
		bundle.putString("0", Def.SUBITOSMS_USERNAME);
		bundle.putString("1", Def.SUBITOSMS_PASSWORD);
		res = mProvider.executeCommand(SubitosmsProvider.COMMAND_CHECKCREDITS, getContext(), bundle);
		String remainingCredits = String.format(
    			getContext().getString(R.string.subitosms_msg_remainingCredits), USER_CREDITS);
		assertEquals("Wrong return message",
		        remainingCredits,
		        res.getResult());
		assertEquals("Wrong returncode",
		        ResultOperation.RETURNCODE_OK,
		        res.getReturnCode());
	}

	
	/**
	 * Test the send of a message using SubitoSMS API
	 */
	public void testApiSendMessageErrors()
	{
		ResultOperation<String> res;
		
		//empty sender
		mProvider.setParameterValue(2, "");
		res = mProvider.sendMessage(null, Def.TEST_DESTINATION, "subitosms test");
        assertTrue("No error", res.hasErrors());
        assertEquals("Wrong returncode",
                ResultOperation.RETURNCODE_ERROR_INVALID_SENDER,
                res.getReturnCode());
		mProvider.setParameterValue(2, Def.SUBITOSMS_SENDER);

//		//wrong sender
//		mProvider.setParameterValue(2, "asdasdasdfafasf");
//		res = mProvider.sendMessage(null, Def.TEST_DESTINATION, "subitosms test");
//		assertEquals("Wrong return message", getContext().getString(R.string.subitosms_msg_invalidSender), res.getResult());
//		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY, res.getReturnCode());
//		mProvider.setParameterValue(2, Def.SUBITOSMS_SENDER);

		//empty destination
		res = mProvider.sendMessage(null, "", "subitosms test");
		assertTrue("No error", res.hasErrors());
        assertEquals("Wrong returncode",
                ResultOperation.RETURNCODE_ERROR_INVALID_DESTINATION,
                res.getReturnCode());

//		//wrong message encoding
//		res = mProvider.sendMessage(null, Def.TEST_DESTINATION, "subitosms\t\ttest!\n");
//		assertEquals("Wrong return message", getContext().getString(R.string.subitosms_msg_invalidMessageEncodingOrTooLong), res.getResult());
//		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY, res.getReturnCode());

		//message ok, but no credit
		res = mProvider.sendMessage(null, Def.TEST_DESTINATION, "test messaggio subito sms ok senza credito");
        assertTrue("No error", res.hasErrors());
		assertEquals("Wrong error message",
		        getContext().getString(R.string.subitosms_msg_notEnoughCredit),
		        res.getException().getMessage());
		assertEquals("Wrong returncode",
		        ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY,
		        res.getReturnCode());
	}

	/**
	 * Test the send of a message using API
	 */
	public void testApiSendMessageOk()
	{
//		ResultOperation<String> res = mProvider.sendMessage(null, Def.TEST_DESTINATION, "test messaggio subito sms ok");
//		//the final part of the message is variable, so cut it!
//		Log.i(TAG, res.getResult());
//		String currentResult = ParserUtils.getInvariableStringFinalBoundary(getContext().getString(R.string.subitosms_msg_messageQueued));
//		assertTrue("Wrong return message", res.getResult().startsWith(currentResult));
//		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_OK, res.getReturnCode());
	}	

	//---------- Private methods
	
	@Override
	protected void initProviderParams() {
		//set test parameters
		mProvider.setParameterValue(0, Def.SUBITOSMS_USERNAME);
		mProvider.setParameterValue(1, Def.SUBITOSMS_PASSWORD);
	}
	
	@Override
	protected SmsProvider createProvider() {
		return new SubitosmsProvider(
		        mLogFacility,
		        RainbowServiceLocator.get(AppPreferencesDao.class),
		        mProviderDao,
		        RainbowServiceLocator.get(ActivityHelper.class));
	}

}
