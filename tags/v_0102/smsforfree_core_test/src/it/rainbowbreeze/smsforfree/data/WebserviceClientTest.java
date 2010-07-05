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
        params = new HashMap<String, String>();
        params.put(AimonDictionary.FIELD_FREE_INPUT_USERNAME, Def.AIMON_USERNAME);
        params.put(AimonDictionary.FIELD_FREE_SUBMIT_BUTTON, "procedi");

        //wrong password
        params.put(AimonDictionary.FIELD_FREE_INPUT_PASSWORD, "XXXX");
        resultMessage =  mClient.requestPost(url, null, params);
        assertTrue(dictionary.isFreeSmsLoginInvalidCredentials(resultMessage));
        
        //good credential
        params.put(AimonDictionary.FIELD_FREE_INPUT_PASSWORD, Def.AIMON_PASSWORD);
        resultMessage =  mClient.requestPost(url, null, params);
        assertTrue(dictionary.isFreeSmsLoginOk(resultMessage, Def.AIMON_USERNAME));

        //go next step, send SMS with an invalid sender
        //if the invalid sender message is returned, the conversation works!
		String msg = "ciao è ora che ti Alzi!!! Per il resto, come stai?";
		url = AimonDictionary.URL_SEND_SMS_FREE_2;
		params.clear();
		params.put(AimonDictionary.FIELD_FREE_SMS_TYPE, "0");  //1 credit sms, fixed sender
		params.put(AimonDictionary.FIELD_FREE_SENDER_TYPE, "1");
		params.put(AimonDictionary.FIELD_FREE_INTERNATIONAL_PREFIX, "39 (Italy)");
		params.put(AimonDictionary.FIELD_FREE_SENDER, "XXXXX");
		params.put(AimonDictionary.FIELD_FREE_MESSAGE, msg);
		params.put(AimonDictionary.FIELD_FREE_MESSAGE_LENGTH, String.valueOf(msg.length()));
		params.put(AimonDictionary.FIELD_FREE_DESTINATION, Def.AIMON_DESTINATION);
		params.put(AimonDictionary.FIELD_FREE_SUBMIT_BUTTON2, "Invia SMS");		
        resultMessage =  mClient.requestPost(url, null, params);
        assertTrue(dictionary.isFreeSmsInvalidSender(resultMessage));
		
		//logout
		url = AimonDictionary.URL_SEND_SMS_FREE_3;
		mClient.requestGet(url);

		mClient.endConversation();
	}

}
