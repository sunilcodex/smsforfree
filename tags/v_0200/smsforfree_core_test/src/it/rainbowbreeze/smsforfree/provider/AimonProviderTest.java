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
import it.rainbowbreeze.libs.helper.RainbowStringHelper;
import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.providers.AimonDictionary;
import it.rainbowbreeze.smsforfree.providers.AimonProvider;
import it.rainbowbreeze.smsforfree.ui.ActivityHelper;
import it.rainbowbreeze.smsforfree.util.Def;
import android.os.Bundle;
import android.util.Log;


/**
 * 
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class AimonProviderTest
	extends BaseProviderTest
{
	//---------- Private fields
	private static final String LOG_HASH = "SmsForFree-AimonProviderTest";
	
	private static final String USER_CREDITS = "60.0000";

	
	

	//---------- Constructor

	


	//---------- SetUp and TearDown
	
	
	

	//---------- Tests methods
	
	@Override
	public void testAndroidTestCaseSetupProperly()
	{
		super.testAndroidTestCaseSetupProperly();
		
		//check if password and sender number was changed
		//before running this test
		assertFalse("You must change the password...", "XXXX".equals(Def.AIMON_PASSWORD));
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

		//empty credentials
        bundle.putString("0", "");
        bundle.putString("1", "");
        res = mProvider.executeCommand(AimonProvider.COMMAND_CHECKCREDENTIALS, getContext(), bundle);
        assertTrue("No error", res.hasErrors());
        assertEquals("Wrong returncode", ResultOperation.RETURNCODE_ERROR_INVALID_CREDENTIAL, res.getReturnCode());
		
		//wrong username and password
		bundle.putString("0", "XXXX");
		bundle.putString("1", "XXXX");
		res = mProvider.executeCommand(AimonProvider.COMMAND_CHECKCREDENTIALS, getContext(), bundle);
		assertTrue("No error", res.hasErrors());
		assertEquals("Wrong returncode",
		        ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY,
		        res.getReturnCode());
		assertEquals("Wrong error message",
		        getContext().getString(R.string.aimon_msg_invalidCredentials),
		        res.getException().getMessage());

		//user with wrong password
		bundle.clear();
		bundle.putString("0", Def.AIMON_USERNAME);
		bundle.putString("1", "XXXXXXX");
		res = mProvider.executeCommand(AimonProvider.COMMAND_CHECKCREDENTIALS, getContext(), bundle);
        assertEquals("Wrong returncode",
                ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY,
                res.getReturnCode());
		assertEquals("Wrong return message",
		        getContext().getString(R.string.aimon_msg_invalidCredentials),
		        res.getException().getMessage());

		//user with good password
		bundle.clear();
		bundle.putString("0", Def.AIMON_USERNAME);
		bundle.putString("1", Def.AIMON_PASSWORD);
		res = mProvider.executeCommand(AimonProvider.COMMAND_CHECKCREDENTIALS, getContext(), bundle);
		assertEquals("Wrong returncode",
		        ResultOperation.RETURNCODE_OK,
		        res.getReturnCode());
		assertEquals("Wrong return message",
		        getContext().getString(R.string.aimon_msg_validCredentials),
		        res.getResult());
	}


	/**
	 * Test the call for credit
	 */
	public void testApiCheckCredit()
	{
		ResultOperation<String> res;
		
		//user with good password
		Bundle bundle = new Bundle();
		bundle.putString("0", Def.AIMON_USERNAME);
		bundle.putString("1", Def.AIMON_PASSWORD);
		res = mProvider.executeCommand(AimonProvider.COMMAND_CHECKCREDITS, getContext(), bundle);
		String remainingCredits = String.format(
    			getContext().getString(R.string.aimon_msg_remainingCredits), USER_CREDITS);
		Log.v(LOG_HASH, "Remaining credits: " + res.getResult());
		assertEquals("Wrong return message", remainingCredits, res.getResult());
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_OK, res.getReturnCode());
	}

	
	/**
	 * Test the send of a message using Aimon API
	 */
	public void testApiSendMessageErrors()
	{
		ResultOperation<String> res;
		
		//empty sender
		mProvider.setParameterValue(2, "");
		res = mProvider.sendMessage(AimonDictionary.ID_API_ANONYMOUS_SENDER, Def.TEST_DESTINATION, "aimon test");
        assertEquals("Wrong returncode",
                ResultOperation.RETURNCODE_ERROR_INVALID_SENDER,
                res.getReturnCode());
		mProvider.setParameterValue(2, Def.AIMON_SENDER);

		//empty destination
		res = mProvider.sendMessage(AimonDictionary.ID_API_ANONYMOUS_SENDER, "", "aimon test");
        assertEquals("Wrong returncode",
                ResultOperation.RETURNCODE_ERROR_INVALID_DESTINATION,
                res.getReturnCode());

		//wrong destination
		res = mProvider.sendMessage(AimonDictionary.ID_API_ANONYMOUS_SENDER, "XXXX", "aimon test");
        assertEquals("Wrong returncode",
                ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY,
                res.getReturnCode());
		assertEquals("Wrong error message",
		        getContext().getString(R.string.aimon_msg_invalidDestination),
                res.getException().getMessage());

		//wrong destination
		res = mProvider.sendMessage(AimonDictionary.ID_API_ANONYMOUS_SENDER, "+8732147763", "aimon test");
        assertEquals("Wrong returncode",
                ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY,
                res.getReturnCode());
		assertEquals("Wrong error message",
		        getContext().getString(R.string.aimon_msg_invalidDestination),
                res.getException().getMessage());

		//empty message
		res = mProvider.sendMessage(AimonDictionary.ID_API_ANONYMOUS_SENDER, "+393211234567", "");
        assertEquals("Wrong returncode",
                ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY,
                res.getReturnCode());
		assertEquals("Wrong error message",
		        getContext().getString(R.string.aimon_msg_emptyMessage),
                res.getException().getMessage());

		//wrong message encoding
		res = mProvider.sendMessage(AimonDictionary.ID_API_ANONYMOUS_SENDER, Def.TEST_DESTINATION, "aimon\t\ttest!\n");
		assertEquals("Wrong returncode",
		        ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY,
		        res.getReturnCode());
        assertEquals("Wrong error message",
                getContext().getString(R.string.aimon_msg_invalidMessageEncodingOrTooLong),
                res.getException().getMessage());

		//TODO:
		//should test also not enough credit
		//but i don't know how to automatize it :(
	}

	/**
	 * Test the send of a message using Aimon API
	 */
	public void testApiSendMessageOk()
	{
//		ResultOperation<String> res = mProvider.sendMessage(AimonDictionary.ID_API_SELECTED_SENDER_NO_REPORT, Def.TEST_DESTINATION, "test messaggio ok dalle api");
//		//the final part of the message is variable, so cut it!
//		String currentResult = ParserUtils.getInvariableStringFinalBoundary(getContext().getString(R.string.aimon_msg_messageQueued));
//		assertTrue("Wrong return message", res.getResult().startsWith(currentResult));
//		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_OK, res.getReturnCode());
	}


	/**
	 * Test different error cases when send a free message using Aimon http form
	 * 
	 */
	public void testFreeSmsCheckCredentials()
	{
		ResultOperation<String> res;
		
		//wrong username
		mProvider.setParameterValue(0, "XXXXX");
		res = mProvider.sendMessage(AimonDictionary.ID_API_FREE_NORMAL, Def.TEST_DESTINATION, "ciao da me");
		assertEquals("Wrong return code",
		        ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY,
                res.getReturnCode());
		assertEquals("Wrong error message",
		        getContext().getString(R.string.aimon_msg_invalidCredentials),
		        res.getException().getMessage());
		mProvider.setParameterValue(0, Def.AIMON_USERNAME);

		//wrong password
		mProvider.setParameterValue(1, "XXXXX");
		res = mProvider.sendMessage(AimonDictionary.ID_API_FREE_NORMAL, Def.TEST_DESTINATION, "ciao da me");
		assertEquals("Wrong return code",
		        ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY, res.getReturnCode());
		assertEquals("Wrong error message",
		        getContext().getString(R.string.aimon_msg_invalidCredentials),
                res.getException().getMessage());
		mProvider.setParameterValue(1, Def.AIMON_PASSWORD);
	}


	/**
	 * Test different error cases when send a free message using Aimon http form
	 * 
	 */
	public void testFreeSmsSendMessageErrors()
	{
		ResultOperation<String> res;
		
		//empty sender
		mProvider.setParameterValue(2, "");
		res = mProvider.sendMessage(AimonDictionary.ID_API_FREE_ANONYMOUS_SENDER, Def.TEST_DESTINATION, "ciao da me");
		assertEquals("Wrong error message",
		        getContext().getString(R.string.aimon_msg_invalidSender),
		        res.getException().getMessage());
		assertEquals("Wrong returncode",
		        ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY,
		        res.getReturnCode());
		mProvider.setParameterValue(2, Def.AIMON_SENDER);

		//wrong sender
		mProvider.setParameterValue(2, "+4323242343");
		res = mProvider.sendMessage(AimonDictionary.ID_API_FREE_ANONYMOUS_SENDER, Def.TEST_DESTINATION, "ciao da me");
        assertEquals("Wrong returncode",
                ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY,
                res.getReturnCode());
		assertEquals("Wrong error message",
		        getContext().getString(R.string.aimon_msg_invalidSender),
		        res.getException().getMessage());
		mProvider.setParameterValue(2, Def.AIMON_SENDER);

		//empty destination
		res = mProvider.sendMessage(AimonDictionary.ID_API_FREE_ANONYMOUS_SENDER, "", "ciao da me");
        assertEquals("Wrong returncode",
                ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY,
                res.getReturnCode());
		assertEquals("Wrong error message",
		        getContext().getString(R.string.aimon_msg_invalidDestination),
		        res.getException().getMessage());

		//wrong destination
		res = mProvider.sendMessage(AimonDictionary.ID_API_FREE_ANONYMOUS_SENDER, "+4323242343", "ciao da me");
        assertEquals("Wrong returncode",
                ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY,
                res.getReturnCode());
		assertEquals("Wrong return message",
		        getContext().getString(R.string.aimon_msg_invalidDestination),
		        res.getException().getMessage());

		//empty message
		res = mProvider.sendMessage(AimonDictionary.ID_API_FREE_ANONYMOUS_SENDER, "+393211234567", "");
        assertEquals("Wrong returncode",
                ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY,
                res.getReturnCode());
		assertEquals("Wrong error message",
		        getContext().getString(R.string.aimon_msg_emptyMessage),
		        res.getException().getMessage());

		//TODO
		//find first invalid characters
		//wrong message encoding
//		res = mProvider.sendMessage(AimonDictionary.ID_API_FREE_NORMAL, Def.AIMON_DESTINATION, "ciao da me\t\tciao!\n");
//		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY, res.getReturnCode());
//		assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_invalidMessageEncoding), res.getResult());

		//TODO:
		//should test also limit for monthly message, not enough credit
		//but i don't know how to automatize it :(
	}
	
	
	/**
	 * Test different error cases when send a free message using Aimon http form
	 * 
	 * Execute this test only if you have enough free credits for sending at least one message, elsewhere the test will fails
	 * 
	 */
	public void testFreeSmsSendMessageOk()
	{
		ResultOperation<String> res = mProvider.sendMessage(AimonDictionary.ID_API_FREE_NORMAL, Def.TEST_DESTINATION, "ciao da me, test1 - free sms");
		//the final part of the message is variable, so cut it!
		Log.i(LOG_HASH, res.getResult());
		String currentResult = RainbowStringHelper.getInvariableStringFinalBoundary(getContext().getString(R.string.aimon_msg_messageQueued));
		assertEquals("Wrong return message",
		        currentResult,
		        res.getResult().substring(0, currentResult.length()));
		assertEquals("Wrong returncode",
		        ResultOperation.RETURNCODE_OK,
		        res.getReturnCode());
		
		//if i resend the same message again, i get an error
		res = mProvider.sendMessage(AimonDictionary.ID_API_FREE_NORMAL, Def.TEST_DESTINATION, "ciao da me, test 2 - free sms");
		Log.i(LOG_HASH, res.getResult());
        assertEquals("Wrong returncode",
                ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY,
                res.getReturnCode());
		assertTrue("Wrong error message",
				getContext().getString(R.string.aimon_msg_freeSmsDailyLimitReached).equals(res.getResult()) ||
				getContext().getString(R.string.aimon_msg_freeSmsMonthlyLimitReached).equals(res.getResult()));
	}




	//---------- Private methods

	@Override
	protected void initProviderParams() {
		//set test parameters
		mProvider.setParameterValue(0, Def.AIMON_USERNAME);
		mProvider.setParameterValue(1, Def.AIMON_PASSWORD);
		mProvider.setParameterValue(2, Def.AIMON_SENDER);
	}
	
	@Override
	protected SmsProvider createProvider() {
		return new AimonProvider(
		        RainbowServiceLocator.get(LogFacility.class),
		        RainbowServiceLocator.get(AppPreferencesDao.class),
		        mProviderDao,
		        RainbowServiceLocator.get(ActivityHelper.class));
	}


}