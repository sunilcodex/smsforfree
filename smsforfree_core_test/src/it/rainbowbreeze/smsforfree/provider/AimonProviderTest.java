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
import android.content.Context;
import android.os.Bundle;
import android.test.AndroidTestCase;


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
	private static final String USER_CREDITS = "65";

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
		mProvider.initProvider(mContext);

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
		assertFalse("You must change destination", "XXXX".equals(Def.AIMON_DESTINATION));
	}
	
	
	/**
	 * Test the call for right credential
	 */
	public void testCheckCredential()
	{
		ResultOperation<String> res;
		
		//user with wrong password
		Bundle bundle = new Bundle();
		bundle.putString("0", Def.AIMON_USERNAME);
		bundle.putString("1", "XXXXXXX");
		res = mProvider.executeCommand(AimonProvider.COMMAND_CHECKCREDENTIALS, mContext, bundle);
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR, res.getReturnCode());
		assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_invalidCredentials), res.getResult());

		//user with good password
		bundle.putString("1", Def.AIMON_PASSWORD);
		res = mProvider.executeCommand(AimonProvider.COMMAND_CHECKCREDENTIALS, mContext, bundle);
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_OK, res.getReturnCode());
		assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_validCredentials), res.getResult());
	}


	/**
	 * Test the call for credit
	 */
	public void testCheckCredit()
	{
		ResultOperation<String> res;
		
		//user with wrong password
		Bundle bundle = new Bundle();
		bundle.putString("0", Def.AIMON_USERNAME);
		bundle.putString("1", Def.AIMON_PASSWORD);
		res = mProvider.executeCommand(AimonProvider.COMMAND_CHECKCREDITS, mContext, bundle);
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_OK, res.getReturnCode());
		String remainingCredits = String.format(
    			mContext.getString(R.string.aimon_msg_remainingCredits), USER_CREDITS);
		assertEquals("Wrong return message", remainingCredits, res.getResult());
	}



	public void testFreeSmsWrongCredentials()
	{
		ResultOperation<String> res;
		
		//wrong password
		mProvider.setParameterValue(1, "XXXXX");
		res = mProvider.sendMessage(AimonDictionary.ID_API_FREE_NORMAL, Def.AIMON_DESTINATION, "ciao da me");
		
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR, res.getReturnCode());
		assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_invalidCredentials), res.getResult());
	}


	public void testFreeSmsSendMessage()
	{
		ResultOperation<String> res;
		
		//wrong sender
		mProvider.setParameterValue(2, "+4323242343");
		res = mProvider.sendMessage(AimonDictionary.ID_API_FREE_NORMAL, Def.AIMON_DESTINATION, "ciao da me");
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR, res.getReturnCode());
		assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_invalidSender), res.getResult());
		mProvider.setParameterValue(2, Def.AIMON_SENDER);

		//wrong destination
		res = mProvider.sendMessage(AimonDictionary.ID_API_FREE_NORMAL, "+4323242343", "ciao da me");
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR, res.getReturnCode());
		assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_invalidDestination), res.getResult());

		//correct send a message
		res = mProvider.sendMessage(AimonDictionary.ID_API_FREE_NORMAL, Def.AIMON_DESTINATION, "ciao da me");
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_OK, res.getReturnCode());
		//assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_messageQueued), res.getResult());
	}




	//---------- Private methods



}