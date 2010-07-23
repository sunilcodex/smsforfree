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
		
		String returnChar = String.valueOf((char) 10);
		String providerReply = "57926	2	Rossoalice	YWFhYQ==	YmJiYg==	Y2NjYw==	ZGRkZA==" + returnChar +
						"57922	61	AimonTest	YWFhYQ==	YmJiYg==	Y2NjYw==	";
		List<SmsService> services = dictionary.extractUserServices(providerReply);
		assertEquals("Wrong service number", 2, services.size());

		SmsService service = services.get(0);
		assertEquals("Wrong service id", "57922", service.getId());
		assertEquals("Wrong number of service parameters", new Integer(3), new Integer(service.getParametersNumber()));
		assertEquals("Wrong service template id", "61", service.getTemplateId());
		assertEquals("Wrong service name", "AimonTest", service.getName());
		assertEquals("Wrong service parameters number", 3, service.getParametersNumber());
		assertEquals("Wrong service parameters 0 value", "aaaa", service.getParameterValue(0));
		assertEquals("Wrong service parameters 1 value", "bbbb", service.getParameterValue(1));
		assertEquals("Wrong service parameters 2 value", "cccc", service.getParameterValue(2));

		service = services.get(1);
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

	/**
	 * Test if code for find if the sms was sent, a captcha is needed or there was another
	 * error works
	 */
	public void testWebserviceReplyRecognition() {
		String serverReply;
		JacksmsDictionary dictionary = new JacksmsDictionary();

		//empty
		serverReply = "";
		assertFalse("Wrong sent sms identification", dictionary.isSmsCorrectlySend(serverReply));
		assertFalse("Wrong captcha identification", dictionary.isCaptchaRequest(serverReply));
		assertTrue("Wrong error reply identification", dictionary.isErrorReply(serverReply));
		
		//captcha
		serverReply = "3617	iVBORw0KGgoAAAANSUhEUgAAAFUAAAAWCAIAAAA+W0fPAAABHklEQVRYhe1YXQ/DIAgcy/7/X2YPTRqjcIcU23TdPVZEPg7Eiqq+HowPXhaR7sser3HJRBtfsMVMQyc/yuwCYAlreHsGeQi6XaIWc7PEEpL/l59Az7iIjKlTREZ5VcUC5inblgj1pvOPsYIdqtoVnYgA5k+B558WoYl1bZXya0w+sIf7P2qPMB+vJqq9LYRCcP5rg+2LaUdLSFqlo0yEL4ktFDX1n6vG4NXoyZTQYc5/s9MeaUWzISjHnP8gA2k20srqlmpDxv2XBq0FtWwEIcAUO3iu28xDmyvynz7CC4p5/7n603Yn5v+DR8Tn/3gIpu//M0Ed2IaC+DxiRHDpoHbh43rV+y+OW/xZuDJFJ4A2gh/3n2Ih/2+Bv//PxhfjgdscfiyFSwAAAABJRU5ErkJggg==	1";
		assertFalse("Wrong sent sms identification", dictionary.isSmsCorrectlySend(serverReply));
		assertTrue("Wrong captcha identification", dictionary.isCaptchaRequest(serverReply));
		assertFalse("Wrong error reply identification", dictionary.isErrorReply(serverReply));

		//sent sms
		serverReply = "1	messaggio spedito con successo";
		assertTrue("Wrong sent sms identification", dictionary.isSmsCorrectlySend(serverReply));
		assertFalse("Wrong captcha identification", dictionary.isCaptchaRequest(serverReply));
		assertFalse("Wrong error reply identification", dictionary.isErrorReply(serverReply));
		
		//strange reply from service
		serverReply = "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">" + "\n" +
			"<html><head>" + "\n" +
			"<title>400 Bad Request</title>" + "\n" +
			"</head><body>" + "\n" +
			"<h1>Bad Request</h1>" + "\n" +
			"<p>Your browser sent a request that this server could not understand.<br />" + "\n" +
			"</p>" + "\n" +
			"</body></html>";
		assertFalse("Wrong sent sms identification", dictionary.isSmsCorrectlySend(serverReply));
		assertFalse("Wrong captcha identification", dictionary.isCaptchaRequest(serverReply));
		assertTrue("Wrong error reply identification", dictionary.isErrorReply(serverReply));
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
