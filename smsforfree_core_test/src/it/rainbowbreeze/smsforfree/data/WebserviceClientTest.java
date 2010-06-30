package it.rainbowbreeze.smsforfree.data;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

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
	 */
	public void testAimonConversation()
	{
		String url;
		HashMap<String, String> params;
		
		mClient.startConversation();
		
        url = "http://aimon.it/?cmd=smsgratis";
        params = new HashMap<String, String>();
        params.put("inputUsername", "rainbowbreeze@aimon.it");
        params.put("inputPassword", "XXXXXXXXXXX");
        params.put("submit", "procedi");
		
		
		String msg = "ciao è ora che ti Alzi!!! Per il resto, come stai?";
		url = "http://aimon.it/index.php?cmd=smsgratis&sez=smsgratis";
		params.clear();
		params.put("tiposms", "1");
		params.put("tipomittente", "1");
		params.put("prefisso_internazionale", "39 (Italy)");
		params.put("mittente", "3927686894");
		params.put("testo", msg);
		params.put("caratteri", String.valueOf(msg.length()));
		params.put("destinatario", "3927686894");
		params.put("btnSubmit", "Invia SMS");		
		
		mClient.endConversation();
	}

}
