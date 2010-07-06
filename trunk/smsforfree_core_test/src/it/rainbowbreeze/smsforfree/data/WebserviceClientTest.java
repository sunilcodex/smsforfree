package it.rainbowbreeze.smsforfree.data;

import it.rainbowbreeze.smsforfree.common.Def;
import it.rainbowbreeze.smsforfree.providers.AimonDictionary;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

import android.test.AndroidTestCase;

public class WebserviceClientTest
	extends AndroidTestCase
{
	//client used for http requests
	private WebserviceClient mClient;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		mClient = new WebserviceClient();		
	}
	

	/**
	 * Test a simple get request
	 */
	public void testSimpleGet()
		throws ClientProtocolException, IOException
	{
		
		String url = "http://www.rainbowbreeze.it/devel/getlatestversion.php?unique=test&swname=test&ver=test&sover=test&sores=test";
		String reply = mClient.requestGet(url);
		
		assertEquals("Not a RainbowBreeze software :(", reply);
	}
	
	
	/**
	 * Test a conversation using aimon api
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public void testAimonConversation()
		throws ClientProtocolException, IOException
	{
		String url;
		HashMap<String, String> params;
		AimonDictionary dictionary = new AimonDictionary();
		String resultMessage;
		
		mClient.startConversation();
		
        url = AimonDictionary.URL_SEND_SMS_FREE_1;
        //wrong password
        params = dictionary.getParametersForFreeSmsLogin(Def.AIMON_USERNAME, "XXXXX");
        resultMessage =  mClient.requestPost(url, null, params);
        assertTrue(dictionary.isFreeSmsLoginInvalidCredentials(resultMessage));
        
        //good credential
        params = dictionary.getParametersForFreeSmsLogin(Def.AIMON_USERNAME, Def.AIMON_PASSWORD);
        resultMessage =  mClient.requestPost(url, null, params);
        assertTrue(dictionary.isFreeSmsLoginOk(resultMessage, Def.AIMON_USERNAME));

        //go next step, send SMS with an invalid sender
        //if the invalid sender message is returned, the conversation works!
		String msg = "messaggio di test mandato da aimon con sender sbagliato";
		url = AimonDictionary.URL_SEND_SMS_FREE_2;
		params = dictionary.getParametersForFreeSmsSend("0", "XXXXX", Def.TEST_DESTINATION, msg);
        resultMessage =  mClient.requestPost(url, null, params);
        assertTrue(dictionary.isFreeSmsInvalidSender(resultMessage));
		
		//logout
		url = AimonDictionary.URL_SEND_SMS_FREE_3;
		mClient.requestGet(url);

		mClient.endConversation();
	}

}
