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

package it.rainbowbreeze.smsforfree.providers;

import static it.rainbowbreeze.libs.common.RainbowContractHelper.checkNotNull;
import it.rainbowbreeze.libs.helper.RainbowArrayHelper;
import it.rainbowbreeze.smsforfree.common.AppEnv;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.domain.SmsConfigurableService;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.helper.Base64Helper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.text.TextUtils;

import com.freesmee.android.data.Contact;

/**
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class JacksmsDictionary
{

	private static JacksmsDictionary sJacksmsDictionary ;

	//---------- Private fields
	protected static final String LOG_HASH = "JacksmsDictionary";
	protected final LogFacility mLogFacility;

	private static final String FORMAT_CSV = "csv";
	private static final String FORMAT_XML = "xml";
	//private static final String FORMAT_JSON = "jsn";

	public static final String STREAM_HOSTNAME = "stream.freesmee.com";
	public static final int STEAM_PORT= 80;
	
	public static final String URL_HOST_AVAILABLE = "http://"+STREAM_HOSTNAME+"/";
	private static final String URL_STREAM_BASE = "https://"+STREAM_HOSTNAME+"/";
	private static final String URL_Q_BASE = "https://api.freesmee.com/";
	// simple reply
	private static final String ACTION_GET_ALL_TEMPLATES = "getProviders";
	// complete reply
	private static final String ACTION_GET_ALL_VERSIONED_TEMPLATES = "getVersionedProviders";
	
	private static final String ACTION_STREAM_SEND_MESSAGE = "send";
	private static final String ACTION_STREAM_SEND_CAPTCHA = "continue";
	private static final String ACTION_STREAM_GET_QUEUE = "getQueue";
	private static final String ACTION_STREAM_QUEUE = "queue";
	private static final String ACTION_STREAM_STREAM = "stream";
	
	private static final String ACTION_GET_USER_SERVICES = "getServicesFull";
	private static final String ACTION_GET_USER_LOGINSTRING = "getLoginString";
	private static final String ACTION_GET_EDITSERVICE = "editService";
	private static final String ACTION_GET_ADDSERVICE = "addService";
	private static final String ACTION_GET_DELSERVICE = "delService";
	private static final String ACTION_GET_ADDRESSBOOK = "getAddressBook";
	private static final String ACTION_IMPORT_ADDRESSBOOK = "importAbook";
	private static final String ACTION_ADDRESSBOOK_NO_BK = "carrierAbook";
	private static final String ACTION_SET_NOTIFY_TYPE = "setNotifyType";
	private static final String ACTION_START_REGISTER = "startRegister"; 
	private static final String ACTION_GET_ADVERTISE = "getAd";
	private static final String ACTION_VERIFY_REGISTER = "verifyRegister";
	
	//TODO: get version from global variable or settings
	public static final String PARAM_CLIENTVERSION_VALUE = "android="+AppEnv.APP_DISPLAY_VERSION+"_"+AppEnv.APP_INTERNAL_VERSION;
	public static final String TAB_SEPARATOR = "\t";

	private static final String USER_TEST = "guest";

	/** Max number of parameters a JackSMS service can have */
	private static final int MAX_SERVICE_PARAMETERS = 4;

	//message sent
	private static final String PREFIX_RESULT_OK = "1" + TAB_SEPARATOR;
	//JackSMS has different error signatures
	private static final String[] PREFIX_RESULT_ERROR_ARRAY = {
		"error" + TAB_SEPARATOR,
		"0" + TAB_SEPARATOR
	};



	public static JacksmsDictionary getInstance(LogFacility logFacility){
		synchronized (JacksmsDictionary.class) {
			if(sJacksmsDictionary==null)
				sJacksmsDictionary = new JacksmsDictionary(logFacility);
		}
		return sJacksmsDictionary;
	}


	//---------- Constructors
	private JacksmsDictionary(LogFacility logFacility) {
		mLogFacility = checkNotNull(logFacility, "LogFacility");
	}



	//---------- Public properties




	//---------- Public methods
	public String getStreamUrlForSendingMessage(String token)
	{ return getUrlForCommand(URL_STREAM_BASE, token , ACTION_STREAM_SEND_MESSAGE, null); }

	public String getStreamUrlForQueue(String token)
	{ return getUrlForCommand(URL_STREAM_BASE, token , ACTION_STREAM_QUEUE, null);}
	
	public String getStreamUrlForGetQueue(String token)
	{ return getUrlForCommand(URL_STREAM_BASE, token , ACTION_STREAM_GET_QUEUE, null);}
	
	public String getStreamUrlForSendingCaptcha(String token)
	{ return getUrlForCommand(URL_STREAM_BASE, token, ACTION_STREAM_SEND_CAPTCHA, null); }
	
	//freesmee 
	public String getUrlForDownloadVersionedTemplates(String token)
	{ return getUrlForCommand(URL_Q_BASE, token, ACTION_GET_ALL_VERSIONED_TEMPLATES, FORMAT_CSV); }

	//freesmee
	public String getUrlForDownloadUserServices(String token)
	{ return getUrlForCommand(URL_Q_BASE, token, ACTION_GET_USER_SERVICES, FORMAT_CSV); }

	//freesmee
	public String getUrlForLoginString()
	{ return getUrlForCommand(URL_Q_BASE, null, ACTION_GET_USER_LOGINSTRING, FORMAT_CSV); }

	//freesmee
	public String getUrlForEditService(String token) {
		return getUrlForCommand(URL_Q_BASE, token, ACTION_GET_EDITSERVICE, FORMAT_CSV);
	}

	//freesmee
	public String getUrlForAddService(String token) {
		return getUrlForCommand(URL_Q_BASE, token, ACTION_GET_ADDSERVICE, FORMAT_CSV);
	}

	//freesmee
	public String getUrlForDeleteService(String token) {
		return getUrlForCommand(URL_Q_BASE, token, ACTION_GET_DELSERVICE, FORMAT_CSV);
	}

	//freesmee
	public String getUrlForAddressBook(String token){
		return getUrlForCommand(URL_Q_BASE, token, ACTION_GET_ADDRESSBOOK, FORMAT_CSV);
	}
	
	//freesmee
	public String getUrlForAdvertise(String token, Location location, String message){
		//TODO mancano alcuni parametri
		String url = getUrlForCommand(URL_Q_BASE, token, ACTION_GET_ADVERTISE, FORMAT_CSV);
		StringBuilder sb = new StringBuilder(url);
		try {
			sb.append("&lat=").append(Double.toString(location.getLatitude()))
			.append("&long=").append(Double.toString(location.getLongitude()))
			.append("&text=").append(URLEncoder.encode(message,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			//TODO log
		}
		return sb.toString();
	}

	//freesmee
	public String getUrlForImportAddressBook(String token){
		return getUrlForCommand(URL_Q_BASE, token, ACTION_IMPORT_ADDRESSBOOK, FORMAT_CSV);
	}
	
	//freesmee
	public String getUrlForNoBkAddressBook(String token){
		return getUrlForCommand(URL_Q_BASE, token, ACTION_ADDRESSBOOK_NO_BK, FORMAT_CSV);
	}
	
	//freesmee
	public String getUrlForStartRegister(){
		return getUrlForCommand(URL_Q_BASE, null, ACTION_START_REGISTER, FORMAT_CSV);
	}
	
	//freesmee
	public String getUrlForVerifyRegister(){
		return getUrlForCommand(URL_Q_BASE, null, ACTION_VERIFY_REGISTER, FORMAT_CSV);
	}
	
	//freesmee
	//TODO non testato
	public String getUrlForSetNotifyType(String token) {
		return getUrlForCommand(URL_Q_BASE, token, ACTION_SET_NOTIFY_TYPE, FORMAT_CSV);
	}
	

	public List<NameValuePair> getParamsForAccountOperation(SmsService editedService,
			String firstParam, String secondParam){
		List<NameValuePair> nVp = new ArrayList<NameValuePair>(5);
		nVp.add(new BasicNameValuePair(firstParam, secondParam));
		nVp.add(new BasicNameValuePair("account_name", editedService.getName()));
		nVp.add(new BasicNameValuePair("data_1",editedService.getParameterValue(0)));
		nVp.add(new BasicNameValuePair("data_2",editedService.getParameterValue(1)));
		nVp.add(new BasicNameValuePair("data_3",editedService.getParameterValue(2)));
		nVp.add(new BasicNameValuePair("data_4",editedService.getParameterValue(3)));
		return nVp;
	}
	
	
	public List<NameValuePair> getParamsForAddressBook(List<Contact> contacts){
		List<NameValuePair> nVp = new ArrayList<NameValuePair>(contacts.size());
		for(Contact contact:contacts){
			nVp.add(new BasicNameValuePair(contact.getNumber(), contact.getName()));
		}
		return nVp;
	}

	/**
	 * Builds headers used in send sms api
	 * prototipo di richiesta:
	 * J-X: service_id \t recipient \t data1 \t data2 \t data3 \t data4 \t message
	 * 
	 * @param service
	 * @param destination
	 * @param message
	 * @return
	 */
	public List<NameValuePair> getParamsForSendingMessage(
			SmsService service,
			String destination,
			String message)
			{
		
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// service_id, srv_data1, srv_data2,srv_data3,srv_data4,recipient,message
			params.add(new BasicNameValuePair("service_id", service.getTemplateId()));
			params.add(new BasicNameValuePair("srv_data1", service.getParameterValue(0)));
			if(!TextUtils.isEmpty(service.getParameterValue(1)))
				params.add(new BasicNameValuePair("srv_data2", service.getParameterValue(1)));
			if(!TextUtils.isEmpty(service.getParameterValue(2)))
				params.add(new BasicNameValuePair("srv_data3", service.getParameterValue(2)));
			if(!TextUtils.isEmpty(service.getParameterValue(3)))
			params.add(new BasicNameValuePair("srv_data4", service.getParameterValue(3)));
			params.add(new BasicNameValuePair("recipient", destination));
			params.add(new BasicNameValuePair("message", adjustMessageBody(message)));
	
			return params;
		}
	
	public List<NameValuePair> getParamsForSendingJms(String destination,String message){
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("account_id", "1"));
			params.add(new BasicNameValuePair("recipient", destination));
			params.add(new BasicNameValuePair("message", adjustMessageBody(message)));
	
			return params;
		}

	/**
	 * Builds headers used in the captcha api
	 * @param sessionId
	 * @param captchaCode
	 * @return
	 */
	public List<NameValuePair> getParamsForSendingCaptcha(String sessionId, String captchaCode)
	{
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// service_id, srv_data1, srv_data2,srv_data3,srv_data4,recipient,message
		params.add(new BasicNameValuePair("session_id", sessionId));
		params.add(new BasicNameValuePair("value", captchaCode));
		return params;
	}

	/**
	 * Replace illegal chars in the message
	 * @param body
	 * @return
	 */
	protected String adjustMessageBody(String body) {
		body = body.replace("\n", " ");
		body = body.replace("\t", " ");
		body = body.replace("\r", " ");
		return body;
	}

	/**
	 * Extracts captcha image content from provider's reply
	 * @param reply
	 * @return
	 */
	public byte[] getCaptchaImageContentFromReply(String reply)
	{
		//captcha content is the text part of the reply
		String captchaBase64 = null;
		try {
			JSONObject json = new JSONObject(reply);
			captchaBase64 = json.getString("message");
		} catch (JSONException e) {
			//TODO log
		}

		//is encoded in base64, so decode id
		byte[] decodedCaptcha;
		try {
			//decodedCaptcha = new String(Base64.decode(content), "UTF-8");
			decodedCaptcha = Base64Helper.decode(captchaBase64);
		} catch (IOException e) {
			decodedCaptcha = null;
		}
		return decodedCaptcha;
	}

	public List<SmsService> extractVersionedTemplates(LogFacility logFacility, String providerReply){
//		type //0
//		id   //1
//		version  //2
//		name  //3
//		maxlen  //4
//		p1, p2, p3, p4 //5,6,7,8
//		desc  //9
//		recipients // 10
		//1	1	1.7	Vodafone-SMS	360	username	password	sim		Invia 10 SMS al giorno tramite il sito Vodafone. È possibile inviare SMS solo verso numeri Vodafone, occorre un numero registrato sul sito.	1

		List<SmsService> templates = new ArrayList<SmsService>();

		//examine the reply, line by line
		String[] lines = providerReply.split("\n");

		for(String templateLine : lines) {
			String[] pieces = templateLine.trim().split(TAB_SEPARATOR);
			try {
				int idx = 0; 
				String type = pieces[idx++]; //0
				String serviceId = pieces[idx++]; //1
				String version = pieces[idx++];//2
				String serviceName = pieces[idx++]; //3
				int maxChar = Integer.parseInt(pieces[idx++]); //4
				

				ArrayList<String> tmpParams = new ArrayList<String>();
				for(int i = 0; i<MAX_SERVICE_PARAMETERS;i++){ //5,6,7,8
					String param = pieces[idx++];
					if(!TextUtils.isEmpty(param))
						tmpParams.add(param);
				}
				
				String[] parametersDesc = tmpParams.toArray(new String[tmpParams.size()]);
				//create new service
				SmsService newTemplate = new SmsConfigurableService(
						logFacility, serviceId, serviceName, maxChar, parametersDesc);
				//sometimes the service description could be unavailable
				
				if (pieces.length > idx)
					newTemplate.setDescription(pieces[idx]);//9
				idx++;
				if(pieces.length>idx)
					newTemplate.setSupportedOperators(pieces[idx]); //10
				
				newTemplate.setServiceType(Integer.parseInt(type));
				newTemplate.setVersion(version);
				templates.add(newTemplate);
			} catch (Exception e) {
				//do nothing, simply skips to next service
			}
		}

		Collections.sort(templates);
		return templates;
	}
	
	public List<SmsService> extractTemplates(LogFacility logFacility, String providerReply)
	{
		List<SmsService> templates = new ArrayList<SmsService>();

		//examine the reply, line by line
		String[] lines = providerReply.split("\n");

		for(String templateLine : lines) {
			String[] pieces = templateLine.split(TAB_SEPARATOR);
			try {
				String serviceId = pieces[0];
				String serviceName = pieces[1];
				int maxChar = Integer.parseInt(pieces[2]);
				String[] parametersDesc = new String[MAX_SERVICE_PARAMETERS];

				int numberOfParameters = MAX_SERVICE_PARAMETERS;
				for(int i = 0; i < MAX_SERVICE_PARAMETERS; i++) {
					parametersDesc[i] = pieces[3+i];
					//find the total number of parameter
					if (TextUtils.isEmpty(parametersDesc[i])) numberOfParameters--;
				}
				//create new service
				parametersDesc = (String[]) RainbowArrayHelper.resizeArray(parametersDesc, numberOfParameters);
				SmsService newTemplate = new SmsConfigurableService(
						logFacility, serviceId, serviceName, maxChar, parametersDesc);
				//sometimes the service description could be unavailable
				if (pieces.length > 7)
					newTemplate.setDescription(pieces[7]);
			} catch (Exception e) {
				//do nothing, simply skips to next service
			}
		}

		Collections.sort(templates);
		return templates;
	}


	public List<SmsConfigurableService> extractUserServices(String providerReply)
	{
		mLogFacility.v(LOG_HASH, "Extract user services");
		List<SmsConfigurableService> services = new ArrayList<SmsConfigurableService>();

		//examine the reply, line by line
		String[] lines = providerReply.split(String.valueOf((char) 10));

		for(String serviceLine : lines) {
			String[] pieces = serviceLine.split(TAB_SEPARATOR);
			try {
				String serviceId = pieces[0];
				String templateId = pieces[1];
				String serviceName = pieces[2];
				String[] parametersValue = new String[MAX_SERVICE_PARAMETERS];

				int numberOfParameters = MAX_SERVICE_PARAMETERS;
				for(int i = 0; i < MAX_SERVICE_PARAMETERS; i++) {
					if (pieces.length > 3+i)
						parametersValue[i] = new String(Base64Helper.decode(pieces[3+i]));
					else
						parametersValue[i] = "";
					//find the total number of parameter
					if (TextUtils.isEmpty(parametersValue[i])) numberOfParameters--;
				}
				mLogFacility.v(LOG_HASH, "Found new service:" +
						"\n service id: " + serviceId +
						"\n template id: " + templateId +
						"\n service name: " + serviceName +
						"\n parameters: " + numberOfParameters);
				//create new service
				parametersValue = (String[]) RainbowArrayHelper.resizeArray(parametersValue, numberOfParameters);
				SmsConfigurableService newService = new SmsConfigurableService(
						mLogFacility, serviceId, templateId, serviceName, parametersValue);
				services.add(newService);
			} catch (Exception e) {
				//do nothing, simply skips to next service
			}
		}

		mLogFacility.v(LOG_HASH, "Total services found: " + services.size());
		Collections.sort(services);
		return services;
	}


	public boolean isSmsCorrectlySent(String webserviceReply) {
		if (TextUtils.isEmpty(webserviceReply)) return false;
		return webserviceReply.startsWith(JacksmsDictionary.PREFIX_RESULT_OK);
	}

	public boolean isCaptchaRequest(String webserviceReply) {
		if (TextUtils.isEmpty(webserviceReply)) return false;

		//find first part of the message
		int pos = webserviceReply.indexOf(TAB_SEPARATOR);
		if (pos < 0) return false;

		//find the number at the start of the message
		String token = webserviceReply.substring(0, pos);

		int number = 1;
		try {
			number = Integer.parseInt(token);
		} catch (Exception e) {
			return false;
		}
		return 1 != number; 
	}

	/**
	 * Checks if captcha code was correctly received by JackSMS
	 * server and sent to sms provider
	 * @param webserviceReply
	 * @return
	 */
	public boolean isCaptchaCorrectlySent(String webserviceReply) {
		return isSmsCorrectlySent(webserviceReply);
	}

	/**
	 * Checks if the reply for webservice contains errors or not
	 * @param webserviceReply
	 * @return
	 */
	public boolean isErrorReply(String webserviceReply)
	{
		//TODO marco
		// cambiato a false, una risposta vuota non ha errori espliciti,
		// qui si stanno cercando errori comunicati dal server!!!

		if (TextUtils.isEmpty(webserviceReply)) return false;

		//explicit error return string
		for (String errSignature : PREFIX_RESULT_ERROR_ARRAY) {
			if (webserviceReply.startsWith(errSignature)) {
				return true;
			}
		}

		return false;
	}

	
	/**
	 * Extracts the message text from provider's reply
	 * @param reply
	 * @return
	 */
	public String getTextPartFromReply(String reply)
	{
		
		String[] split = reply.split(TAB_SEPARATOR);
		if(split.length>0)
		return split[1];
		else return "";
	}
	public boolean isInvalidCredetials(String webserviceReply)
	{
		if (TextUtils.isEmpty(webserviceReply)) return false;

		//TODO marco, siamo sicuri che in questo caso non ci sia il tab???

		return ("error	Dati di accesso JackSMS non validi").equals(webserviceReply);
	}

	/**
	 * Checks if the reply for webservice contains some strange strings
	 * that are not recognized as errors, but don't allow the app to work
	 * @param webserviceReply
	 * @return
	 */
	public boolean isUnmanagedErrorReply(String webserviceReply)
	{
		if (TextUtils.isEmpty(webserviceReply)) return true;

		return (webserviceReply.startsWith("<!DOCTYPE HTML PUBLIC"));
	}

	//---------- Private methods
	//freesme
	private String getUrlForCommand(String baseUrl, String token, String command, String format)
	{

		StringBuilder sb = new StringBuilder();
		sb.append(baseUrl);
		sb.append(command)
		.append("?")
		.append(PARAM_CLIENTVERSION_VALUE);
		if(token!=null){
			sb.append("&token=")
		     .append(token);
		}
		if(format!=null){
			sb.append("&o=")
			.append(format);
		}

		String	url = sb.toString();

		return url;
	}

	private String replaceNotAllowedChars(String sourceString)
	{
		return sourceString.replace("+", "-").replace("/", "_");
	}


	private String replaceServiceParameter(String parameter)
	{
		return TextUtils.isEmpty(parameter) ? "" : parameter;
	}



	public JackReply parseReply(String reply){
		if(TextUtils.isEmpty(reply))
			return null;
		JackReply jackReply = new JackReply(reply);



		return jackReply;
	}

	public static enum ReplyType{
		SEND_RESULT,
		NEED_CAPTCHA
	}

	public static class JackReply{

		public static final int RESULT_CODE_SUCCESS=1;
		public static final int RESULT_CODE_ERROR = 0;

		private static final int INVALID_INT=-1;

		private ReplyType _type;

		private boolean mIsValid = true;

		private String mReply;

		private int mReplyType = INVALID_INT;

		private String mResultCode; //0

		private int mResultCodeAsInt = INVALID_INT; 

		private String mText; //1

		private String mUnread; //2

		private int mUnreadAsInt = INVALID_INT; 

		private String mSentToday; //3

		private int mSentTodayAsInt = INVALID_INT;

		private String mOperator; //4

		private int mOperatorAsInt = INVALID_INT;

		private String mIsJack; //5

		private int mIsJackAsInt = INVALID_INT;

		private String mNewVersion; // 6 - new api??

		protected JackReply(String reply){
			mReply = reply.trim();

			String[] split = reply.split(TAB_SEPARATOR);

			//0 - result code
			try {
				int v = Integer.parseInt(split[0]);
				switch(v){
				case 0:
				case 1:
					mResultCode=split[0];
					mResultCodeAsInt=v;
					_type =ReplyType.SEND_RESULT;
					break;
				default:
					if(v>1){
						mResultCode=split[0];
						mResultCodeAsInt=v;
						_type = ReplyType.NEED_CAPTCHA;
					}else
						mIsValid = false;
				}
			} catch (NumberFormatException e) {
				mIsValid = false;
			}

			// 1 - text
			if(split.length>1 && !TextUtils.isEmpty(split[1]))
				mText=split[1];
			else
				mText="";

			// 2 - unread
			if(split.length>2 && !TextUtils.isEmpty(split[2])){
				mUnread=split[2];
				try {
					mUnreadAsInt = Integer.parseInt(mUnread);
				} catch (NumberFormatException e) {
					mIsValid = false;
				}
			}

			// 3 - sent today
			if(split.length>3 && !TextUtils.isEmpty(split[3])){
				mSentToday=split[3];
				try {
					mSentTodayAsInt = Integer.parseInt(mSentToday);
				} catch (NumberFormatException e) {
					mIsValid = false;
				}
			}

			// 4 - operator
			if(split.length>4 && !TextUtils.isEmpty(split[4])){
				mOperator=split[4];
				try {
					mOperatorAsInt = Integer.parseInt(mOperator);
				} catch (NumberFormatException e) {
					mIsValid = false;
				}
			}

			// 5 - isJack
			if(split.length>5 && !TextUtils.isEmpty(split[5])){
				mIsJack=split[5];
				try {
					mIsJackAsInt = Integer.parseInt(mIsJack);
				} catch (NumberFormatException e) {
					mIsValid = false;
				}
			}

			//6 TODO
			mNewVersion = null;
		}

		public boolean isValid() {
			return mIsValid;
		}

		public String getReply() {
			return mReply;
		}

		public int getReplyType() {
			return mReplyType;
		}

		public String getResultCode() {
			return mResultCode;
		}

		public int getResultCodeAsInt() {
			return mResultCodeAsInt;
		}

		/**
		 * 
		 * @return never null
		 */
		public String getText() {
			return mText;
		}

		public String getUnread() {
			return mUnread;
		}

		public int getUnreadAsInt() {
			return mUnreadAsInt;
		}

		public String getSentToday() {
			return mSentToday;
		}

		public int getSentTodayAsInt() {
			return mSentTodayAsInt;
		}

		public String getOperator() {
			return mOperator;
		}

		public int getOperatorAsInt() {
			return mOperatorAsInt;
		}

		public String getIsJack() {
			return mIsJack;
		}

		public int getIsJackAsInt() {
			return mIsJackAsInt;
		}

		public String getmNewVersion() {
			return mNewVersion;
		}

	}

	
	public static class Operators {
		
		public static final int UNKNOWN = 0;
		
		public static final int VODA = 1;
		public static final int TIM = 2;
		public static final int H3G = 3;
		public static final int WIND = 4;
		public static final int JACK = 5;
		
		public static final String ALL_REPLY = "*";
		public static final int ALL = Integer.MAX_VALUE;
		
		public static final int[] LIST = new int[]{
			UNKNOWN,
			VODA,
			TIM,
			H3G,
			WIND,
			JACK
		};
	}
	
	public static class NotifyType{
		
		public static class P{
			public static final String NOTIFY_TYPE = "notifyType";
			public static final String REGISTRATION_ID = "registration_id";
		}
		
		public static final String NONE = "0";
		public static final String SQUILLO = "1";
		public static final String GOOGLE_PUSH = "2";
		
		
	}
	
	
	public boolean checkServerNotifyType(String reply, String expecetedType){
		return TextUtils.equals(expecetedType, getNotifyTypeFromReply(reply));
	} 
	
	public String getNotifyTypeFromReply(String reply){
		String[] split = reply.trim().split(TAB_SEPARATOR);
		if(split.length>=2)
			if(split[0].trim().equals(NotifyType.P.NOTIFY_TYPE)){
				return split[1];
			}
		return null; 
	}
	/** Contenuto dell'array tokens dell'esito 
	 * [0] = esito {1|0}
	 * [1] = \t [eventuale risultato operazione] 
	 * [2] = \t messaggi da leggere 
	 * [3] = \t n messaggi inviati oggi con l'account usato 
	 * [4] = \t codice identificativo operatore destinatario 
	 * [5] = \t flag appartenenza al network JackSMS
	 */
	
	
	public static class ServiceType{
		public static final int FREE =1;
		public static final int LOWCOST =2;
		public static final int OTHER=3;
	}
	
	

}