/**
 * Copyright (C) 2010 Alfredo Morresi
 * 
 * This file is part of SmsForFree project.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/>.
 */

package it.rainbowbreeze.smsforfree.data;

import it.rainbowbreeze.libs.common.RainbowServiceLocator;
import it.rainbowbreeze.libs.net.IRainbowRestfulClient.ServerResponse;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.providers.AimonDictionary;
import it.rainbowbreeze.smsforfree.util.Def;
import it.rainbowbreeze.smsforfree.util.TestHelper;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

import android.test.AndroidTestCase;

/**
 * Test class for {@link WebserviceClient}
 *
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class WebserviceClientTest extends AndroidTestCase {
    //---------- Constructor

    
    
    
    //---------- Private fields
    //client used for http requests
    private WebserviceClient mClient;
    private boolean mForceReload = false;

    
    
    
    //---------- Test initialization
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestHelper.init(getContext(), mForceReload);
        mForceReload = false;
		
		LogFacility logFacility = RainbowServiceLocator.get(LogFacility.class);
		mClient = new WebserviceClient(logFacility);		
	}

    
    
    
    //---------- Test cases 
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
		ServerResponse resultMessage;
		
		mClient.startConversation();
		
        url = AimonDictionary.URL_SEND_SMS_FREE_1;
        //wrong password
        params = dictionary.getParametersForFreeSmsLogin(Def.AIMON_USERNAME, "XXXXX");
        resultMessage =  mClient.requestPost(url, null, params);
        assertTrue(dictionary.isFreeSmsLoginInvalidCredentials(resultMessage.getResponseMessage()));
        
        //good credential
        params = dictionary.getParametersForFreeSmsLogin(Def.AIMON_USERNAME, Def.AIMON_PASSWORD);
        resultMessage =  mClient.requestPost(url, null, params);
        assertTrue(dictionary.isFreeSmsLoginOk(resultMessage.getResponseMessage(), Def.AIMON_USERNAME));

        //go next step, send SMS with an invalid sender
        //if the invalid sender message is returned, the conversation works!
		String msg = "messaggio di test mandato da aimon con sender sbagliato";
		url = AimonDictionary.URL_SEND_SMS_FREE_2;
		params = dictionary.getParametersForFreeSmsSend("0", "XXXXX", Def.TEST_DESTINATION, msg);
        resultMessage =  mClient.requestPost(url, null, params);
        assertTrue(dictionary.isFreeSmsInvalidSender(resultMessage.getResponseMessage()));
		
		//logout
		url = AimonDictionary.URL_SEND_SMS_FREE_3;
		mClient.requestGet(url);

		mClient.endConversation();
	}

}
