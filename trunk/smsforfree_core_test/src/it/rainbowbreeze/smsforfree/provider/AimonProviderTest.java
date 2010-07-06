package it.rainbowbreeze.smsforfree.provider;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.Def;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.common.TestUtils;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsServiceParameter;
import it.rainbowbreeze.smsforfree.providers.AimonDictionary;
import it.rainbowbreeze.smsforfree.providers.AimonProvider;
import it.rainbowbreeze.smsforfree.util.ParserUtils;
import android.content.Context;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.util.Log;


/**
 * 
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class AimonProviderTest
	extends AndroidTestCase
{
	//---------- Private fields
	private static final String TAG = "SmsForFree-AimonProviderTest";
	
	private static final String USER_CREDITS = "60.0000";

	private SmsProvider mProvider;
	private Context mContext;
	private ProviderDao mDao;
	private SmsServiceParameter[] mBackupParameters;
	
	

	//---------- Constructor
	public AimonProviderTest() {
		super();
		
		mDao = new ProviderDao();
	}
	
	
	//---------- SetUp and TearDown
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		mContext = getContext();
		mProvider = new AimonProvider(mDao);
		ResultOperation<Void> res = mProvider.initProvider(mContext);
		assertFalse("provider initialization with errors", res.HasErrors());

		//mock some values of SmsForFreeApplication
		TestUtils.loadAppPreferences(mContext);

		//save provider parameters
		mBackupParameters = TestUtils.backupServiceParameters(mProvider);
		
		//set test parameters
		mProvider.setParameterValue(0, Def.AIMON_USERNAME);
		mProvider.setParameterValue(1, Def.AIMON_PASSWORD);
		mProvider.setParameterValue(2, Def.AIMON_SENDER);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		//restore modified parameters.
		TestUtils.restoreServiceParameters(mProvider, mBackupParameters);
	}



	//---------- Tests methods
	
	@Override
	public void testAndroidTestCaseSetupProperly()
	{
		super.testAndroidTestCaseSetupProperly();
		
		//check if password and sender number was changed
		//before running this test
		assertFalse("You must change the password...", "XXXX".equals(Def.AIMON_PASSWORD));
		assertFalse("You must change destination", "XXXX".equals(Def.TEST_DESTINATION));
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
		res = mProvider.executeCommand(AimonProvider.COMMAND_CHECKCREDENTIALS, mContext, bundle);
		assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_invalidCredentials), res.getResult());
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR, res.getReturnCode());

		//user with wrong password
		bundle.clear();
		bundle.putString("0", Def.AIMON_USERNAME);
		bundle.putString("1", "XXXXXXX");
		res = mProvider.executeCommand(AimonProvider.COMMAND_CHECKCREDENTIALS, mContext, bundle);
		assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_invalidCredentials), res.getResult());
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR, res.getReturnCode());

		//user with good password
		bundle.clear();
		bundle.putString("0", Def.AIMON_USERNAME);
		bundle.putString("1", Def.AIMON_PASSWORD);
		res = mProvider.executeCommand(AimonProvider.COMMAND_CHECKCREDENTIALS, mContext, bundle);
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_OK, res.getReturnCode());
		assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_validCredentials), res.getResult());
	}


	/**
	 * Test the call for credit
	 */
	public void testApiCheckCredit()
	{
		ResultOperation<String> res;
		
		//user with wrong password
		Bundle bundle = new Bundle();
		bundle.putString("0", Def.AIMON_USERNAME);
		bundle.putString("1", Def.AIMON_PASSWORD);
		res = mProvider.executeCommand(AimonProvider.COMMAND_CHECKCREDITS, mContext, bundle);
		String remainingCredits = String.format(
    			mContext.getString(R.string.aimon_msg_remainingCredits), USER_CREDITS);
		Log.i(TAG, "Remaining credits: " + res.getResult());
		assertEquals("Wrong return message", remainingCredits, res.getResult());
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_OK, res.getReturnCode());
	}

	
	/**
	 * Test the send of a message using Aimon API
	 */
	public void testApiSendMessageErrors()
	{
		ResultOperation<String> res;
		
		//wrong sender
		mProvider.setParameterValue(2, "");
		res = mProvider.sendMessage(AimonDictionary.ID_API_ANONYMOUS_SENDER, Def.TEST_DESTINATION, "ciao da me");
		assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_invalidSender), res.getResult());
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR, res.getReturnCode());
		mProvider.setParameterValue(2, Def.AIMON_SENDER);

		//wrong destination
		res = mProvider.sendMessage(AimonDictionary.ID_API_ANONYMOUS_SENDER, "", "ciao da me");
		assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_invalidDestination), res.getResult());
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR, res.getReturnCode());

		//empty destination
		res = mProvider.sendMessage(AimonDictionary.ID_API_ANONYMOUS_SENDER, "", "ciao da me");
		assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_invalidDestination), res.getResult());
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR, res.getReturnCode());

		//empty message
		res = mProvider.sendMessage(AimonDictionary.ID_API_ANONYMOUS_SENDER, "+393211234567", "");
		assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_emptyMessage), res.getResult());
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR, res.getReturnCode());

		//wrong message encoding
		res = mProvider.sendMessage(AimonDictionary.ID_API_ANONYMOUS_SENDER, Def.TEST_DESTINATION, "ciao da me\t\tciao!\n");
		assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_invalidMessageEncodingOrTooLong), res.getResult());
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR, res.getReturnCode());

		//TODO:
		//should test also not enough credit
		//but i don't know how to automatize it :(
	}

	/**
	 * Test the send of a message using Aimon API
	 */
	public void testApiSendMessageOk()
	{
//		ResultOperation<String> res = mProvider.sendMessage(AimonDictionary.ID_API_SELECTED_SENDER_NO_REPORT, Def.AIMON_DESTINATION, "test messaggio ok dalle api");
//		//the final part of the message is variable, so cut it!
//		int pos = ParserUtils.getInvariableStringFinalBoundary(getContext().getString(R.string.aimon_msg_messageQueued));
//		assertTrue("Wrong return message", res.getResult().startsWith(getContext().getString(R.string.aimon_msg_messageQueued).substring(0, pos)));
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
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR, res.getReturnCode());
		assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_invalidCredentials), res.getResult());
		mProvider.setParameterValue(0, Def.AIMON_USERNAME);

		//wrong password
		mProvider.setParameterValue(1, "XXXXX");
		res = mProvider.sendMessage(AimonDictionary.ID_API_FREE_NORMAL, Def.TEST_DESTINATION, "ciao da me");
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR, res.getReturnCode());
		assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_invalidCredentials), res.getResult());
		mProvider.setParameterValue(1, Def.AIMON_PASSWORD);
	}


	/**
	 * Test different error cases when send a free message using Aimon http form
	 * 
	 */
	public void testFreeSmsSendMessageErrors()
	{
		ResultOperation<String> res;
		
		//wrong sender
		mProvider.setParameterValue(2, "+4323242343");
		res = mProvider.sendMessage(AimonDictionary.ID_API_FREE_ANONYMOUS_SENDER, Def.TEST_DESTINATION, "ciao da me");
		assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_invalidSender), res.getResult());
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR, res.getReturnCode());
		mProvider.setParameterValue(2, Def.AIMON_SENDER);

		//wrong destination
		res = mProvider.sendMessage(AimonDictionary.ID_API_FREE_ANONYMOUS_SENDER, "+4323242343", "ciao da me");
		assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_invalidDestination), res.getResult());
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR, res.getReturnCode());

		//empty destination
		res = mProvider.sendMessage(AimonDictionary.ID_API_FREE_ANONYMOUS_SENDER, "", "ciao da me");
		assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_invalidDestination), res.getResult());
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR, res.getReturnCode());

		//empty message
		res = mProvider.sendMessage(AimonDictionary.ID_API_FREE_ANONYMOUS_SENDER, "+393211234567", "");
		assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_emptyMessage), res.getResult());
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR, res.getReturnCode());

		//TODO
		//find first invalid characters
		//wrong message encoding
//		res = mProvider.sendMessage(AimonDictionary.ID_API_FREE_NORMAL, Def.AIMON_DESTINATION, "ciao da me\t\tciao!\n");
//		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR, res.getReturnCode());
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
		ResultOperation<String> res = mProvider.sendMessage(AimonDictionary.ID_API_FREE_NORMAL, Def.TEST_DESTINATION, "ciao da me - free sms");
		//the final part of the message is variable, so cut it!
		Log.e(TAG, res.getResult());
		int pos = ParserUtils.getInvariableStringFinalBoundary(getContext().getString(R.string.aimon_msg_messageQueued));
		assertTrue("Wrong return message", res.getResult().startsWith(getContext().getString(R.string.aimon_msg_messageQueued).substring(0, pos)));
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_OK, res.getReturnCode());
		
		//if i resend the same message again, i get an error
		res = mProvider.sendMessage(AimonDictionary.ID_API_FREE_NORMAL, Def.TEST_DESTINATION, "ciao da me - free sms");
		Log.e(TAG, res.getResult());
		assertTrue("Wrong return message",
				getContext().getString(R.string.aimon_msg_freeSmsDailyLimitReached).equals(res.getResult()) ||
				getContext().getString(R.string.aimon_msg_freeSmsMonthlyLimitReached).equals(res.getResult()));
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR, res.getReturnCode());
	}




	//---------- Private methods



}