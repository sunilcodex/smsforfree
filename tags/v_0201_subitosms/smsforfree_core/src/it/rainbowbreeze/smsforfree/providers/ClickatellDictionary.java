/**
 * Copyright (C) 2011 Alfredo Morresi
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

package it.rainbowbreeze.smsforfree.providers;

import it.rainbowbreeze.smsforfree.common.LogFacility;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.text.TextUtils;
import android.util.Xml;
import static it.rainbowbreeze.libs.common.RainbowContractHelper.*;

/**
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ClickatellDictionary
{
	//---------- Private fields
    private static final String LOG_HASH = "ClickatellDictionary";
	protected static final String PROVIDER_BASE_URL =
		"http://api.clickatell.com/http/sendmsg";
	
	protected static final String XMLNODE_RESULTCODE = "result";
	protected static final String XMLNODE_RESULTSTRING = "resultstring";
    protected static final String XMLNODE_DESCRIPTION = "description";

	protected final LogFacility mLogFacility;



	//---------- Constructors
	public ClickatellDictionary(LogFacility logFacility) {
		mLogFacility = checkNotNull(logFacility, "LogFacility");
	}




	//---------- Public properties



	
	//---------- Public methods
	public String getUrlForMessage(
			String username,
			String password,
			String sender,
			String destination,
			String body)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(PROVIDER_BASE_URL)
			.append("?")
			.append("username=")
			.append(urlEncode(username))
			.append("&")
			.append("password=")
			.append(urlEncode(password))
			.append("&")
			.append("from=")
			.append(urlEncode(sender))
			.append("&")
			.append("to=")
			.append(urlEncode(destination))
			.append("&")
			.append("text=")
			.append(urlEncode(body));		

		return sb.toString();
	}
	
	/**
     * Checks if an error for message too long (or with strange chars) was returned from provider
	 */
    public boolean isMessageTooLong(ProviderReply providerReply) {
        if (null == providerReply || TextUtils.isEmpty(providerReply.description)) return false;
        //return providerReply.equalsIgnoreCase("The text message you are trying to send is larger than 160 characters, please shorten your message.");
        return providerReply.description.toLowerCase().contains("please shorten your message");
    }
	
	/**
	 * Parses the provider reply and search if the message was sent
	 */
	public boolean messageWasSent(ProviderReply providerReply) {
		if (null == providerReply || TextUtils.isEmpty(providerReply.resultString)) return false;
		return "success".equalsIgnoreCase(providerReply.resultString);
	}

	
	/**
	 * Deserializes provider reply from xml input.
	 * Public for testing purpose
	 * 
	 * @return provider reply text, or null in case of parsing errors
	 */
	public ProviderReply deserializeProviderReply(String providerReply)
	{
	    InputStream in;
        try {
            in = new ByteArrayInputStream(providerReply.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            mLogFacility.e(LOG_HASH, "Error parsing provider result: UnsupportedEncodingException");
            mLogFacility.e(e);
            return null;
        }
	    
		XmlPullParser parser = Xml.newPullParser();
		int resultCode = 0;
		String resultString = null;
		String description = null;

		try {
            parser.setInput(in, null);
		    int eventType = parser.getEventType();
		
    		while (eventType != XmlPullParser.END_DOCUMENT) {
    			String name = null;
    			switch (eventType) {
    			case XmlPullParser.START_DOCUMENT:
    				break;
    				
    			case XmlPullParser.START_TAG:
    				name = parser.getName();
    				
    				if (name.equalsIgnoreCase(XMLNODE_RESULTCODE)) {
    					resultCode = Integer.parseInt(parser.nextText());
    				} else if (name.equalsIgnoreCase(XMLNODE_RESULTSTRING)) {
    					resultString = parser.nextText();
                    } else if (name.equalsIgnoreCase(XMLNODE_DESCRIPTION)) {
                        description = parser.nextText();
                    }
    				break;
    				
    			case XmlPullParser.END_TAG:
    				break;
    			
    			}
    			
    			eventType = parser.next();
    		}
        } catch (XmlPullParserException e) {
            mLogFacility.e(LOG_HASH, "Error parsing provider result: XmlPullParserException");
            mLogFacility.e(e);
            return null;
        } catch (NumberFormatException e) {
            mLogFacility.e(LOG_HASH, "Error parsing provider result: NumberFormatException");
            mLogFacility.e(e);
            return null;
        } catch (IOException e) {
            mLogFacility.e(LOG_HASH, "Error parsing provider result: IOException");
            mLogFacility.e(e);
            return null;
        }
		
		return new ProviderReply(resultCode, resultString, description);
	}




    //---------- Private methods
	protected String urlEncode(String stringToEncode) {
		String result;
		
		try {
			result = URLEncoder.encode(stringToEncode, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			result = stringToEncode;
		}
		return result;
	}
	
	
	
	/**
	 * Voipstunt provider reply
	 *
	 */
	public class ProviderReply {
	    public final int resultCode;
	    public final String resultString;
        public final String description;
	    
        public ProviderReply(int resultCode, String resultString, String description) {
            this.resultCode = resultCode;
            this.resultString = resultString;
            this.description = description;
        }
	}
}
