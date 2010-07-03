package it.rainbowbreeze.smsforfree.provider;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsServiceParameter;
import it.rainbowbreeze.smsforfree.providers.AimonProvider;
import it.rainbowbreeze.smsforfree.util.TestUtils;
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
	private static final String USERNAME = "f.martinelli@aimon.it";
	private static final String PASSWORD = "smsf0rfr33";
	private static final String SENDER = "+393912345678";
	private static final String DESTINATION = "XXXXX";
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
		
		//save provider parameters
		mBackupParameters = TestUtils.backupServiceParameters(mProvider);
		
		//set test parameters
		mProvider.setParameterValue(0, USERNAME);
		mProvider.setParameterValue(1, PASSWORD);
		mProvider.setParameterValue(2, SENDER);
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
		assertFalse("You must change the password...", "XXXX".equals(PASSWORD));
		assertFalse("You must change destination", "XXXX".equals(DESTINATION));
	}
	
	
	/**
	 * Test the call for right credential
	 */
	public void testCheckCredential()
	{
		ResultOperation<String> res;
		
		//user with wrong password
		Bundle bundle = new Bundle();
		bundle.putString("0", USERNAME);
		bundle.putString("1", "XXXXXXX");
		res = mProvider.executeCommand(AimonProvider.COMMAND_CHECKCREDENTIALS, mContext, bundle);
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR, res.getReturnCode());
		assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_invalidCredentials), res.getResult());

		//user with good password
		bundle.putString("1", PASSWORD);
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
		bundle.putString("0", USERNAME);
		bundle.putString("1", PASSWORD);
		res = mProvider.executeCommand(AimonProvider.COMMAND_CHECKCREDITS, mContext, bundle);
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_OK, res.getReturnCode());
		String remainingCredits = String.format(
    			mContext.getString(R.string.aimon_msg_remainingCredits), USER_CREDITS);
		assertEquals("Wrong return message", remainingCredits, res.getResult());
	}



	public void _testFreeSmsWrongCredentials()
	{
		ResultOperation<String> res;
		
		//wrong password
		mProvider.setParameterValue(1, "XXXXX");
		res = mProvider.sendMessage(AimonProvider.ID_API_FREE, DESTINATION, "ciao da me");
		
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_INTERNAL_PROVIDER_ERROR, res.getReturnCode());
		assertEquals("Wrong return message", getContext().getString(R.string.aimon_msg_validCredentials), res.getResult());
	}




	//---------- Private methods



}