/**
 * 
 */
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
import it.rainbowbreeze.smsforfree.providers.SubitosmsProvider;
import android.content.Context;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Test class for SubitoSMS provider
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class SubitosmsProviderTest
	extends AndroidTestCase
{
	//---------- Private fields
	private static final String TAG = "SmsForFree-SubitosmsProviderTest";

	private static final String USER_CREDITS = "3";
	
	private SmsProvider mProvider;
	private Context mContext;
	private ProviderDao mDao;
	private SmsServiceParameter[] mBackupParameters;
	
	

	//---------- Constructor
	public SubitosmsProviderTest() {
		super();
		
		mDao = new ProviderDao();
	}
	
	
	//---------- SetUp and TearDown
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		mContext = getContext();
		mProvider = new SubitosmsProvider(mDao);
		ResultOperation<Void> res = mProvider.initProvider(mContext);
		assertFalse("provider initialization with errors", res.HasErrors());

		//mock some values of SmsForFreeApplication
		TestUtils.loadAppPreferences(mContext);

		//save provider parameters
		mBackupParameters = TestUtils.backupServiceParameters(mProvider);
		
		//set test parameters
		mProvider.setParameterValue(0, Def.SUBITOSMS_USERNAME);
		mProvider.setParameterValue(1, Def.SUBITOSMS_PASSWORD);
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



	public void testSubitosmsCheckCredits()
	{
		ResultOperation<String> res;
		
		//user with wrong password
		Bundle bundle = new Bundle();
		bundle.putString("0", Def.SUBITOSMS_USERNAME);
		bundle.putString("1", Def.SUBITOSMS_PASSWORD);
		res = mProvider.executeCommand(SubitosmsProvider.COMMAND_CHECKCREDITS, mContext, bundle);
		String remainingCredits = String.format(
    			mContext.getString(R.string.aimon_msg_remainingCredits), USER_CREDITS);
		Log.i(TAG, "Remaining credits: " + res.getResult());
		assertEquals("Wrong return message", remainingCredits, res.getResult());
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_OK, res.getReturnCode());
	}


	//---------- Private methods

}
