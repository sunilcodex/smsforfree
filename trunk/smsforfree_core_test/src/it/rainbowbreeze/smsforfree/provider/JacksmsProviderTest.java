/**
 * 
 */
package it.rainbowbreeze.smsforfree.provider;


import android.os.Bundle;
import it.rainbowbreeze.smsforfree.common.Def;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.providers.JacksmsProvider;

/**
 * @author rainbowbreeze
 *
 */
public class JacksmsProviderTest
	extends BaseProviderTest
{
	//---------- Private fields

	
	
	
	//---------- Constructor




	//---------- SetUp and TearDown




	//---------- Tests methods
	
	
	/**
	 * Test if the command for retrieving JackSMS stored user account works
	 */
	public void testImportStoredAccount()
	{
		ResultOperation<String> res;
		
		//user with right password
		Bundle bundle = new Bundle();
		bundle.putString("0", Def.JACKSMS_USERNAME);
		bundle.putString("1", Def.JACKSMS_PASSWORD);
		//clear the list of provider's services
		mProvider.getAllSubservices().clear();
		res = mProvider.executeCommand(JacksmsProvider.COMMAND_LOADUSERSERVICES, getContext(), bundle);
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_OK, res.getReturnCode());
		//return a string with all user services
		assertEquals("Wrong number of services", new Integer(3), new Integer(mProvider.getAllSubservices().size()));
	}




	//---------- Private methods
	
	@Override
	protected SmsProvider createProvider() {
		return new JacksmsProvider(mDao);
	}
	
	@Override
	protected void initProviderParams() {
		//set test parameters
		mProvider.setParameterValue(0, Def.JACKSMS_USERNAME);
		mProvider.setParameterValue(1, Def.JACKSMS_PASSWORD);
	}

}
