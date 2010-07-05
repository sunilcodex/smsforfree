/**
 * 
 */
package it.rainbowbreeze.smsforfree.provider;

import it.rainbowbreeze.smsforfree.common.Def;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.common.TestUtils;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsServiceParameter;
import it.rainbowbreeze.smsforfree.providers.SubitosmsProvider;
import android.content.Context;
import android.test.AndroidTestCase;

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
	private static final String TAG = "SmsForFree-SubitoSMSProviderTest";
	
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
		mProvider.setParameterValue(2, Def.SUBITOSMS_SENDER);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		//restore modified parameters.
		TestUtils.restoreServiceParameters(mProvider, mBackupParameters);
	}



	//---------- Tests methods

	//---------- Private methods

}
