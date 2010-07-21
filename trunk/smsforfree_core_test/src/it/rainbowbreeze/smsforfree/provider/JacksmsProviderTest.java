/**
 * 
 */
package it.rainbowbreeze.smsforfree.provider;


import java.util.List;

import android.os.Bundle;
import it.rainbowbreeze.smsforfree.common.Def;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.providers.AimonDictionary;
import it.rainbowbreeze.smsforfree.providers.JacksmsDictionary;
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
	
	@Override
	public void testAndroidTestCaseSetupProperly()
	{
		super.testAndroidTestCaseSetupProperly();
		
		//check if password and sender number was changed
		//before running this test
		assertFalse("You must change the password...", "XXXX".equals(Def.JACKSMS_PASSWORD));
		assertFalse("You must change destination", "XXXX".equals(Def.TEST_DESTINATION));
	}
	
	/**
	 * Test if translation of user saved account works
	 */
	public void testTranslateSingleStoredUserAccount()
	{
		JacksmsDictionary dictionary = new JacksmsDictionary();
		
		String providerReply = "57926	2	Rossoalice	YWFhYQ==	YmJiYg==	Y2NjYw==	ZGRkZA==";

		List<SmsService> services = dictionary.extractUserServices(providerReply);
		assertEquals("Wrong service number", 1, services.size());
		SmsService service = services.get(0);
		assertEquals("Wrong service id", "57926", service.getId());
		assertEquals("Wrong service template id", "2", service.getTemplateId());
		assertEquals("Wrong service name", "Rossoalice", service.getName());
		assertEquals("Wrong service parameters number", 4, service.getParametersNumber());
		assertEquals("Wrong service parameters 0 value", "aaaa", service.getParameterValue(0));
		assertEquals("Wrong service parameters 1 value", "bbbb", service.getParameterValue(1));
		assertEquals("Wrong service parameters 2 value", "cccc", service.getParameterValue(2));
		assertEquals("Wrong service parameters 3 value", "dddd", service.getParameterValue(3));
		
	}
	
	/**
	 * Test if translation of user saved account works
	 */
	public void testTranslateMultipleStoredUserAccounts()
	{
		JacksmsDictionary dictionary = new JacksmsDictionary();
		
		String providerReply = "57926	2	Rossoalice	YWFhYQ==	YmJiYg==	Y2NjYw==	ZGRkZA==\n" +
						"57727	40	JackSMS Messenger	Zi5tYXJ0aW5lbGxp	c21zZjByZnIzMw==\n" +		
						"57922	61	Aimon	Zi5tYXJ0aW5lbGxp	dGVzdHB3ZA==	dGVzdHB3ZA==";
		List<SmsService> services = dictionary.extractUserServices(providerReply);
		assertEquals("Wrong service number", 3, services.size());
	}
	
	
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
