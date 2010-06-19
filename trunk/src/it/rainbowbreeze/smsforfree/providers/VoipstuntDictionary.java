package it.rainbowbreeze.smsforfree.providers;

import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.text.TextUtils;
import android.util.Xml;

/**
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class VoipstuntDictionary
{
	//---------- Private fields
	private final static String VOIPSTUNT_BASE_URL =
		"https://www.voipstunt.com/myaccount/sendsms.php";
	
	private final static String XMLNODE_RESULTCODE = "result";
	private final static String XMLNODE_RESULTSTRING = "resultstring";




	//---------- Public properties



	
	//---------- Events




	//---------- Public methods
	public String getUrlForMessage(
			String username,
			String password,
			String sender,
			String destination,
			String body)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(VOIPSTUNT_BASE_URL)
			.append("?")
			.append("username=")
			.append(username)
			.append("&")
			.append("password=")
			.append(password)
			.append("&")
			.append("from=")
			.append(sender)
			.append("&")
			.append("to=")
			.append(destination)
			.append("&")
			.append("text=")
			.append(body);		
		
		return sb.toString();
	}
	
	
	/**
	 * Parses the provider reply and search if the message was sent
	 * 
	 * @param providerReply
	 * @return
	 */
	public boolean messageWasSent(String providerReply)
	{
		if (TextUtils.isEmpty(providerReply))
			return false;
		
	
		String providerResult = null;
		InputStream is;
		try {
			is = new ByteArrayInputStream(providerReply.getBytes("UTF-8"));
			providerResult = deserializeProviderReply(is);
			
		//TODO advice for providers problem
		} catch (UnsupportedEncodingException e) {
			LogFacility.e(e);
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			LogFacility.e(e);
		} catch (IOException e) {
			LogFacility.e(e);
		}
		
		return "success".equalsIgnoreCase(providerResult);
	}




	//---------- Private methods
	/**
	 * Deserializes provider reply from xml input
	 */
	private String deserializeProviderReply(InputStream in)
		throws XmlPullParserException, IOException
	{
		XmlPullParser parser = Xml.newPullParser();
		int resultCode = 0;
		String resultString = "";

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
				}
				break;
				
			case XmlPullParser.END_TAG:
				break;
			
			}
			
			eventType = parser.next();
		}
		
		return resultString;
		
	}
}
