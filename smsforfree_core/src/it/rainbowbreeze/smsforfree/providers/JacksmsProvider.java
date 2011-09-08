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

import it.jamiroproductions.smswest.Preferences;
import it.jamiroproductions.smswest.StorageMessage;
import it.jamiroproductions.smswest.StorageService;
import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.AppEnv;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.domain.SmsConfigurableService;
import it.rainbowbreeze.smsforfree.domain.SmsMultiProvider;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.domain.SmsServiceCommand;
import it.rainbowbreeze.smsforfree.domain.SmsServiceParameter;
import it.rainbowbreeze.smsforfree.helper.Base64Helper;
import it.rainbowbreeze.smsforfree.providers.JacksmsDictionary.NotifyType;
import it.rainbowbreeze.smsforfree.ui.ActivityHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.SlidingDrawer;

import com.freesmee.android.data.Contact;
import com.freesmee.android.data.DataService;
import com.freesmee.android.data.SendService;
import com.freesmee.android.logic.JmsLogic;
import com.freesmee.android.logic.JmsNotificationLogic;
import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZInputStream;
import com.jcraft.jzlib.ZOutputStream;

/**
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 * @author Saverio Guardato 
 *
 */
public class JacksmsProvider
extends SmsMultiProvider
{
	//---------- Private fields
	protected final static String LOG_HASH = "JacksmsProvider";

	protected final static int PARAM_NUMBER = 2;
	protected final static int PARAM_INDEX_USERNAME = 0;
	protected final static int PARAM_INDEX_PASSWORD = 1;

	protected final static int MSG_INDEX_INVALID_CREDENTIALS = 0;
	protected final static int MSG_INDEX_SERVER_ERROR_KNOW = 1;
	protected final static int MSG_INDEX_SERVER_ERROR_UNKNOW = 2;
	protected final static int MSG_INDEX_MESSAGE_SENT = 3;
	protected final static int MSG_INDEX_NO_CAPTCHA_SESSION_ID = 4;
	protected final static int MSG_INDEX_NO_TEMPLATES_PARSED = 5;
	protected final static int MSG_INDEX_NO_CAPTCHA_PARSED = 6;
	protected final static int MSG_INDEX_TEMPLATES_UPDATED = 7;
	protected final static int MSG_INDEX_CAPTCHA_OK = 8;
	protected final static int MSG_INDEX_NO_TEMPLATES_TO_USE = 9;
	protected final static int MSG_INDEX_USERSERVICES_UPDATED = 10;
	protected final static int MSG_INDEX_NO_USERSERVICES_TO_USE = 11;

	protected JacksmsDictionary mDictionary;
	protected AppPreferencesDao mAppPreferenceDao;

	protected String[] mMessages;

	private final ReplyRequestBuffer mReplyRequestBuffer= new ReplyRequestBuffer();

	private Context mBaseContext;

	
	private static final int LONG_TIMEOUT = 180000; //very long, slow site. 
	private static final String MYJOY_TEMPLATE_ID = Integer.toString(86);
	private static final String GRATISMS_EU = Integer.toString(127);
	private static final int TIMEOUT_SHORT = 15000;
	

	//---------- Constructors
	public JacksmsProvider(
			LogFacility logFacility,
			AppPreferencesDao appPreferencesDao,
			ProviderDao providerDao,
			ActivityHelper activityHelper)
	{
		super(logFacility, PARAM_NUMBER, appPreferencesDao, providerDao, activityHelper);
		mAppPreferenceDao = appPreferencesDao;
	}




	//---------- Public properties
	public final static int COMMAND_LOADTEMPLATESERVICES = 1000;
	public final static int COMMAND_LOADUSERSERVICES = 1001;

	@Override
	public String getId()
	{ return "Freesmee"; }

	@Override
	public String getName()
	{ return "Freesmee"; }

	@Override
	public int getParametersNumber()
	{ return PARAM_NUMBER; }




	//---------- Public methods

	@Override
	public ResultOperation<Void> initProvider(Context context)
	{
		mDictionary = JacksmsDictionary.getInstance(mLogFacility);

		mBaseContext = context.getApplicationContext();

		//provider parameters
		setParameterDesc(PARAM_INDEX_USERNAME, context.getString(R.string.freesmee_username_desc));
		setParameterDesc(PARAM_INDEX_PASSWORD, context.getString(R.string.freesmee_password_desc));
		setParameterFormat(PARAM_INDEX_PASSWORD, SmsServiceParameter.FORMAT_PASSWORD);
		setDescription(context.getString(R.string.freesmee_description));

		//save messages
		mMessages = new String[12];
		mMessages[MSG_INDEX_INVALID_CREDENTIALS] = context.getString(R.string.freesmee_msg_invalidCredentials);
		mMessages[MSG_INDEX_SERVER_ERROR_KNOW] = context.getString(R.string.freesmee_msg_serverErrorKnow);
		mMessages[MSG_INDEX_SERVER_ERROR_UNKNOW] = context.getString(R.string.freesmee_msg_serverErrorUnknow);
		mMessages[MSG_INDEX_MESSAGE_SENT] = context.getString(R.string.freesmee_msg_messageSent);
		mMessages[MSG_INDEX_NO_CAPTCHA_SESSION_ID] = context.getString(R.string.freesmee_msg_noCaptchaSessionId);
		mMessages[MSG_INDEX_NO_TEMPLATES_PARSED] = context.getString(R.string.freesmee_msg_noTemplatesParsed);
		mMessages[MSG_INDEX_NO_CAPTCHA_PARSED] = context.getString(R.string.freesmee_msg_noCaptcha);
		mMessages[MSG_INDEX_TEMPLATES_UPDATED] = context.getString(R.string.freesmee_msg_templatesListUpdated);
		mMessages[MSG_INDEX_CAPTCHA_OK] = context.getString(R.string.freesmee_msg_captchaOk);
		mMessages[MSG_INDEX_NO_TEMPLATES_TO_USE] = context.getString(R.string.freesmee_msg_noTemplatesToUse);
		mMessages[MSG_INDEX_USERSERVICES_UPDATED] = context.getString(R.string.freesmee_msg_userServicesListUpdated);
		mMessages[MSG_INDEX_NO_USERSERVICES_TO_USE] = context.getString(R.string.freesmee_msg_noUserServices);

		return super.initProvider(context);
	}

	/*
	 * get LoginString for http operations
	 */
	public ResultOperation<String> getLoginString(String username, String password){
		if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password))
			return getExceptionForInvalidCredentials();
		
		String url = mDictionary.getUrlForLoginString();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user", username));
		params.add(new BasicNameValuePair("password", password));
		HttpClient client = createDefaultClient();
		ResultOperation<String> res = performPost(client, url, params);
		// login\tcdwadwdawdadawda\n
		//TODO error handling
		return res;
	}


	private ResultOperation<String> downloadQueueUDP(String token){
		String command = mDictionary.getUDPParamsForGetQueue(token, mAppPreferenceDao.getUdpConfirmationTimestamp());
		ResultOperation<byte[]> res = performUDPCommand(command, TIMEOUT_SHORT);
		if(res.hasErrors())
			return new ResultOperation<String>(res.getException(),res.getReturnCode());
		
		
//		"\n" -> nessun nuovo messaggio
//		"E descrizione_errore" -> un qualche errore
//		"nuovo_timestamp\nmsg_time1 sender1 message1\nmsg_time2 sender2 message2" (etc)
		
//		OkNr
//		2011-08-16 19:01:10	+39.349.7206078	jjojojoj
		//check not null
		String reply = new String(res.getResult());
		if(reply.equals("\n")){
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("status", 1);
			} catch (JSONException e) {
				//ignore
			} // it's ok
			return new ResultOperation<String>(jsonObject.toString());
		}else if(reply.toUpperCase().startsWith("E"+JacksmsDictionary.TAB_SEPARATOR)){
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("status", 0);
				String[] split = reply.split(JacksmsDictionary.TAB_SEPARATOR);
				if(split.length>1)
					jsonObject.put("message", split[1]);
			} catch (JSONException e) {
				//ignore
			}
			return new ResultOperation<String>(jsonObject.toString());
		}else {
			String[] reply_raws = reply.split("\n");
			if(reply_raws.length>1){
				
				JSONObject jsonObject = new JSONObject();
				
				JSONArray queue = null;
				try {
					jsonObject.put("status", 1);
					
					queue = new JSONArray();
					jsonObject.put("queue", queue);
				} catch (JSONException e) {
					//ignore
				}
				
				for(int i = 1 ; i< reply_raws.length ; i++){
					String raw =reply_raws[i];
					String[] tokens = raw.split(JacksmsDictionary.TAB_SEPARATOR);
					if(tokens.length>=3){
						JSONObject obj = new JSONObject();
						//"msg_id":"10000387","msg_time":"2011-05-24 23:33:50","timediff":"1863", sender, message
						try {
							obj.put("msg_time", tokens[0]);
							obj.put("sender", tokens[1]);
							obj.put("message", tokens[2]);
							obj.put("msg_id", 0);
							long parseFreesmeeDate = JmsLogic.i(mBaseContext).parseFreesmeeDate(tokens[0]);
							obj.put("timediff", Math.max(0, System.currentTimeMillis()-parseFreesmeeDate)%1000);
							
							queue.put(i-1, obj);
						} catch (JSONException e) {
							//ignore
						}
						
					}
				}
				if(queue.length()!=0){
					String cnf_timestamp=reply_raws[0];
					mAppPreferenceDao.setUdpConfirmationTimestamp(cnf_timestamp);
					mAppPreferenceDao.save();
				}
				
				return new ResultOperation<String>(jsonObject.toString());
			}else
				return new ResultOperation<String>(new Exception(), ResultOperation.RETURNCODE_ERROR_GENERIC);
		}
	}

	/**
	 * scarica la lista dei messaggi da leggere "manualmente"
	 * - l'implementazione http utilizza le api senza ACK
	 * - l'implementazione udp invece fornisce ack al prossimo tentativo di scaricamento
	 * @return
	 */
	public synchronized ResultOperation<String> downloadQueue(){
		
		// questo metodo può essere chiamato da thread diversi per cui è sincronizzato
		// altrimenti potrebbe succedere di ricevere i messaggi duplicati
		
		String loginS = mAppPreferenceDao.getLoginString();
		if(TextUtils.isEmpty(loginS))
			return getExceptionForInvalidCredentials();

		ResultOperation<String> res;
		
		String connection_mode = Preferences.getConnectionMode(mBaseContext);
		if(TextUtils.equals(connection_mode, Preferences.FREESMEE_CONNECTION_MODE_HTTPS)){
			String url = mDictionary.getStreamUrlForGetQueue(loginS);
			HttpClient client = createDefaultClient(TIMEOUT_SHORT);
			res = performPost(client, url, null);
		}else if(TextUtils.equals(connection_mode, Preferences.FREESMEE_CONNECTION_MODE_UDP)){
			res = downloadQueueUDP(loginS);
		}else throw new RuntimeException("Modalità di connessione sconosciuta");

		//checks for errors
		if(res.hasErrors())
			return res;

		
		return parseDownloadQueueWithoutAck(res);
		
	}
	
	private ResultOperation<String> parseDownloadQueueWithoutAck(ResultOperation<String> res){
		String reply = res.getResult();
		try {
			JSONObject json = new JSONObject(reply);
			//{ "status": 1, 
			//"queue": [{"msg_id":"10000114","msg_time":"2011-05-24 23:18:01","timediff":"2812","message":"testo","sender":"+39.331.7359550"},
			//{"msg_id":"10000387","msg_time":"2011-05-24 23:33:50","timediff":"1863","message":"blablab ","sender":"+39.331.7359550"}] }
			int status = json.getInt("status");
			JSONArray queue = json.optJSONArray("queue");
			switch (status) {
			case 0:
				String errorMessage = json.optString("message");
				if(TextUtils.isEmpty(errorMessage))
					errorMessage= mMessages[MSG_INDEX_SERVER_ERROR_UNKNOW];
				//fail
				mLogFacility.e(LOG_HASH, "JacksmsProvider error reply");
				mLogFacility.e(LOG_HASH, errorMessage);
				mLogFacility.e(LOG_HASH, reply);
				setSmsProviderException(res, errorMessage);
				break;
			case 1:
				res = processQueue(queue, res);				
				break;
			default:
				setSmsProviderException(res, mMessages[MSG_INDEX_SERVER_ERROR_UNKNOW]);
				mLogFacility.e(LOG_HASH, "Error sending message in Jacksms Provider");
				mLogFacility.e(LOG_HASH, reply);
				break;
			}
		} catch (JSONException e) {
			setSmsProviderException(res, mMessages[MSG_INDEX_SERVER_ERROR_UNKNOW]);
			mLogFacility.e(LOG_HASH, "Error sending message in Jacksms Provider");
			mLogFacility.e(LOG_HASH, reply);
		}

		return res;
	}



	public ResultOperation<String> processQueue(JSONArray queue, ResultOperation<String> res){
		if(res==null)
			res = new ResultOperation<String>();
		long currentTime = System.currentTimeMillis();
		ArrayList<StorageMessage> messages = null;
		if(queue!=null){
			long mostRecent=0;
			
			messages = new ArrayList<StorageMessage>(queue.length());
			JmsLogic jl = JmsLogic.i(mBaseContext);
			for(int i = 0 ; i < queue.length(); i++){
				StorageMessage storageMessage = null;
				String sender = "";
				try{

					JSONObject msg = queue.getJSONObject(i);
					msg.getInt("msg_id");
					String date = msg.getString("msg_time");
					long diff= msg.getLong("timediff");
					String message = msg.getString("message");
					sender = msg.getString("sender");

					storageMessage = StorageMessage.prepareNewReceivedMessage(message, sender, System.currentTimeMillis());
					
					//FIXME
					//storageMessage = StorageMessage.prepareNewReceivedMessage(message, sender, currentTime-diff*1000);
					messages.add(storageMessage);
					mostRecent = Math.max(mostRecent, jl.parseFreesmeeDate(date));
				}catch (Exception e) {
					setSmsProviderException(res, mMessages[MSG_INDEX_SERVER_ERROR_UNKNOW]);
					mLogFacility.e(LOG_HASH, "Error parsing message Queue");
					mLogFacility.e(LOG_HASH, queue.toString());
				}

				if(storageMessage!=null){
					Intent intent = DataService.updatePhoneNumberIntent(mBaseContext, sender, null, "1", SendService.Ids.JMS);
					if(intent!=null)
						mBaseContext.startService(intent);
				}
			}
			
			jl.updateMostRecentJmsTimestamp(mostRecent);
		}
		if(messages==null || messages.isEmpty()){
			res.setResult(mBaseContext.getString(R.string.Jms_download_ok_no_new_message));
		}else{
			res.setResult(mBaseContext.getResources().getQuantityString(R.plurals.Jms_download_ok, messages.size(), messages.size()));
			Intent intent = StorageService.getIntentSaveMessages(mBaseContext, messages, true, null);
			mBaseContext.startService(intent);
		}
		return res;
	}
	/** Contenuto dell'array tokens dell'esito 
	 * [0] = esito {1|0}
	 * [1] = \t [eventuale risultato operazione] 
	 * [2] = \t messaggi da leggere 
	 * [3] = \t n messaggi inviati oggi con l'account usato 
	 * [4] = \t codice identificativo operatore destinatario 
	 * [5] = \t flag appartenenza al network JackSMS
	 */
	@Override
	public ResultOperation<String> sendMessage(
			String serviceId,
			String destination,
			String messageBody)
			{

		String username = getParameterValue(PARAM_INDEX_USERNAME);
		String password = getParameterValue(PARAM_INDEX_PASSWORD);
		String loginS = mAppPreferenceDao.getLoginString();

		ResultOperation<String> res = validateSendSmsParameters(username, password, loginS, destination, messageBody);
		if (res.hasErrors()) return res;
		mLogFacility.i(LOG_HASH, "No error in res.");
		//sends the sms
		

		SmsService service = null;
		if(!serviceId.equals(SendService.Ids.JMS))
			service = getSubservice(serviceId);
		
		int timeout;
		if(service!=null && (TextUtils.equals(service.getTemplateId(),MYJOY_TEMPLATE_ID)
			|| TextUtils.equals(service.getTemplateId(),GRATISMS_EU))){
			timeout = LONG_TIMEOUT;
		}else
			timeout = TIMEOUT;
		
		String connection_mode = Preferences.getConnectionMode(mBaseContext);
		if(TextUtils.equals(connection_mode, Preferences.FREESMEE_CONNECTION_MODE_HTTPS)){
			String url = mDictionary.getStreamUrlForSendingMessage(loginS);
			List<NameValuePair> params;
			if(serviceId.equals(SendService.Ids.JMS))
				params = mDictionary.getParamsForSendingJms(destination, messageBody);
			else
				params = mDictionary.getParamsForSendingMessage(service, destination, messageBody);
			HttpClient client = createDefaultClient(timeout);
			
			res = performPost(client, url, params);
		}else if(TextUtils.equals(connection_mode, Preferences.FREESMEE_CONNECTION_MODE_UDP)){
			res = sendMessageUDP(loginS, serviceId, destination, messageBody, timeout);
		}else throw new RuntimeException("Modalità di connessione sconosciuta");
		
		//checks for errors
		if(res.hasErrors())
			return res;

		//at this point, no error happened, so checks if the sms was sent or
		//a captcha code is needed

		return parseSendAndCaptchaReply(res, destination , serviceId);

		}


	private ResultOperation<String> parseSendAndCaptchaReply(ResultOperation<String> res, String destination, String serviceId){
		String reply = res.getResult();
		if(reply==null)
			reply=""; //lasciamo che il problema sia individuato dopo
		try {

			//"status":1
			//"result":1,
			//"message":"Messaggio inviato",
			//"queue":0,
			//"sent":"3",
			//"carrier":"1",
			//"isfs":1,
			//"upgrade":0}

			JSONObject json = new JSONObject(reply);

			int status = json.getInt("status"); //0 = errore -> message
			int result = json.optInt("result"); //0,1, N>1
			String message = json.optString("message");
			int queue = json.optInt("queue"); // optional?
			int sent = json.optInt("sent");  //
			int carrier = json.optInt("carrier");
			int isfs = json.optInt("isfs");
			int upgrade = json.optInt("upgrade"); // c'è una nuova versione del client? non gestito

			int choose; // status indica un errore dovuta alla richiseta
			// result invece un errore dovuto al servizio di invio
			if(status==0)
				choose=status;
			else
				choose=result;
			switch (choose) {
			case 0:
				//fail
				mLogFacility.e(LOG_HASH, "JacksmsProvider error reply");
				mLogFacility.e(LOG_HASH, message);
				mLogFacility.e(LOG_HASH, reply);
				setSmsProviderException(res, message);
				break;
			case 1:
				//ok
				updatePhoneNumberData(destination, serviceId, carrier, isfs);
				res.setResult(prepareOkMessageForUser(message,sent));
				if(queue!=0){
					String connection_mode = Preferences.getConnectionMode(mBaseContext);
					if(!TextUtils.equals(connection_mode, Preferences.FREESMEE_CONNECTION_MODE_UDP))
						JmsLogic.i(mBaseContext).receiveJms(true);
				}
				break;
			default:
				//captcha
				res.setReturnCode(ResultOperation.RETURNCODE_SMS_CAPTCHA_REQUEST);
				mReplyRequestBuffer.add(new ReplyRequest(Integer.toString(result), destination, serviceId));
				break;
			}
		} catch (JSONException e) {
			setSmsProviderException(res, mMessages[MSG_INDEX_SERVER_ERROR_UNKNOW]);
			mLogFacility.e(LOG_HASH, "Error sending message in Jacksms Provider");
			mLogFacility.e(LOG_HASH, reply);
		}
		return res;  
	}

	public ResultOperation<String> getAdvertise(Location location, String message){
		mLogFacility.v(LOG_HASH, "Get advertise url");
		String token = mAppPreferencesDao.getLoginString();

		//TODO check credentials

		String url = mDictionary.getUrlForAdvertise(token,location, message);
		HttpClient client = createDefaultClient(15000);
		ResultOperation<String> reply= performPost(client, url, null);
		if(reply.hasErrors())
			return reply;
		if(!TextUtils.isEmpty(reply.getResult())){
			// url\thttp://ad.jacksms.it/get/p_koeu718ur4v4xtzh_552kx7s?d=apple
			String[] split = reply.getResult().trim().split(JacksmsDictionary.TAB_SEPARATOR);
			if(split.length>=2){
				if(split[0].toLowerCase().equals("url")){
					reply.setResult(split[1]);
					return reply;
				}

			}
		}
		else{
			reply.setReturnCode(ResultOperation.RETURNCODE_ERROR_EMPTY_REPLY);
			mLogFacility.i(LOG_HASH,"[no advertise link returned]");
		}
		return reply;

	}


	@Override
	public ResultOperation<Object> getCaptchaContentFromProviderReply(String providerReply)
	{
		mLogFacility.v(LOG_HASH, "Get captcha content");

		//captcha content is the text part of the reply
		byte[] content = mDictionary.getCaptchaImageContentFromReply(providerReply);

		if (null == content) {
			//errors in parsing captcha
			mLogFacility.e(LOG_HASH, "Null content for captcha image");
			return setSmsProviderException(new ResultOperation<Object>(), mMessages[MSG_INDEX_NO_CAPTCHA_PARSED]);
		}

		return new ResultOperation<Object>(content);
	}

	/** Contenuto dell'array tokens dell'esito 
	 * [0] = esito {1|0}
	 * [1] = \t [eventuale risultato operazione] 
	 * [2] = \t messaggi da leggere 
	 * [3] = \t n messaggi inviati oggi con l'account usato 
	 * [4] = \t codice identificativo operatore destinatario 
	 * [5] = \t flag appartenenza al network JackSMS
	 */
	@Override
	public ResultOperation<String> sendCaptcha(String providerReply, String captchaCode) {
		mLogFacility.v(LOG_HASH, "Captcha return message analysis");

		JSONObject json = null;
		try {
			json = new JSONObject(providerReply);
		} catch (JSONException e) {
			// is is json or error would have been detected earlier
		}
		String sessionId = json.optString("result");

		if (TextUtils.isEmpty(sessionId)) {
			mLogFacility.e(LOG_HASH, "Captcha session id is empty");
			return setSmsProviderException(new ResultOperation<String>(), mMessages[MSG_INDEX_NO_CAPTCHA_SESSION_ID]);
		}

		String loginS = mAppPreferenceDao.getLoginString();

		//sends the captcha code
		ResultOperation<String> res;
		
		String connection_mode = Preferences.getConnectionMode(mBaseContext);
		if(TextUtils.equals(connection_mode, Preferences.FREESMEE_CONNECTION_MODE_HTTPS)){
			res = sendCaptchaHTTP(loginS, sessionId, captchaCode);
		}else if(TextUtils.equals(connection_mode, Preferences.FREESMEE_CONNECTION_MODE_UDP)){
			res = sendCaptchaUDP(loginS, sessionId, captchaCode);
		}else throw new RuntimeException("Modalità di connessione sconosciuta");
		
		

		if(res.hasErrors())
			return res;

		String destination = null;
		String serviceId = null;
		ReplyRequest rr = mReplyRequestBuffer.remove(sessionId);
		if(rr!=null){
			destination = rr.getNumber();
			serviceId = rr.getServiceId();
		}
		return parseSendAndCaptchaReply(res, destination , serviceId);

	}
	
	
	private ResultOperation<String> sendCaptchaHTTP(String token, String sessionId, String captchaCode){
		String url = mDictionary.getStreamUrlForSendingCaptcha(token);
		List<NameValuePair> params = mDictionary.getParamsForSendingCaptcha(sessionId, captchaCode);
		HttpClient client = createDefaultClient();
		return performPost(client, url, params);
	}
	
	private ResultOperation<String> sendCaptchaUDP(String token, String sessionId, String captchaCode){
		String command = mDictionary.getUDPParamsForSendingCaptcha(token, sessionId, captchaCode);
		ResultOperation<byte[]> rawReply = performUDPCommand(command, TIMEOUT);
		if(rawReply.hasErrors())
			return new ResultOperation<String>(rawReply.getException(), rawReply.getReturnCode());
		return parseSendAndCaptchaReplyUDP(rawReply);
	}



	@Override
	public ResultOperation<String> executeCommand(int commandId, Context context, Bundle extraData) {
		ResultOperation<String> res;

		//execute commands
		switch (commandId) {
		case COMMAND_LOADTEMPLATESERVICES:
			res = downloadTemplates(context);
			break;

		case COMMAND_LOADUSERSERVICES:
			res = downloadUserConfiguredServices(context);
			break;

		default:
			res = super.executeCommand(commandId, context, extraData);
		}

		return res;
	}



	//---------- Private methods
	@Override
	protected String getParametersFileName()
	{ return AppEnv.jacksmsParametersFileName; }

	@Override
	protected String getTemplatesFileName()
	{ return AppEnv.jacksmsmTemplatesFileName; }

	@Override
	protected String getSubservicesFileName()
	{ return AppEnv.jacksmsSubservicesFileName; }

	@Override
	protected String getProviderRegistrationUrl(Context context) {
		throw new UnsupportedOperationException();
	}


	@Override
	protected List<SmsServiceCommand> loadSubservicesListActivityCommands(Context context)
	{
		List<SmsServiceCommand> commands = super.loadSubservicesListActivityCommands(context);

		//subservices commands list
		SmsServiceCommand command;
		mSubservicesListActivityCommands = new ArrayList<SmsServiceCommand>();
		command = new SmsServiceCommand(
				COMMAND_LOADTEMPLATESERVICES, context.getString(R.string.freesmee_commandLoadTemplateServices), 1000, R.drawable.ic_menu_refresh);
		commands.add(command);
		command = new SmsServiceCommand(
				COMMAND_LOADUSERSERVICES, context.getString(R.string.freesmee_commandLoadUserSubservices), 1001, R.drawable.ic_menu_cloud);
		commands.add(command);

		return commands;
	};


	/**
	 * Downloads all service templates available from JackSMS site
	 * @return
	 */
	private ResultOperation<String> downloadTemplates(Context context)
	{
		mLogFacility.v(LOG_HASH, "Download provider templates");
		String username = getParameterValue(PARAM_INDEX_USERNAME);
		String password = getParameterValue(PARAM_INDEX_PASSWORD);
		String token    = mAppPreferenceDao.getLoginString();

		//credential check
		if (!checkCredentialsValidity(username, password))
			return getExceptionForInvalidCredentials();

		if(TextUtils.isEmpty(token))
			return getExceptionForInvalidCredentials();

		String url = mDictionary.getUrlForDownloadVersionedTemplates(token);
		HttpClient client = createDefaultClient();
		ResultOperation<String> res= performPost(client, url, null);

		//checks for errors
		if (parseReplyForErrors(res)){
			//log action data for a better error management
			logRequest(url, (HashMap<String,String>)null, null);
			return res;
		}

		//at this point, the provider reply should contains the list of templates
		String templatesReply = res.getResult();

		//transform the reply in the list of templates
		List<SmsService> newTemplates = mDictionary.extractVersionedTemplates(mLogFacility, templatesReply);

		if (newTemplates.size() <= 0) {
			//retain old templates
			res.setResult(mMessages[MSG_INDEX_NO_TEMPLATES_PARSED]);
			return res;
		}

		//try to set password fields
		for (SmsService template : newTemplates) {
			//generally, jacksms use second parameter as password
			String description = template.getParameterDesc(1);
			if (!TextUtils.isEmpty(description) && description.toUpperCase().contains("PASSWORD"))
				template.setParameterFormat(1, SmsServiceParameter.FORMAT_PASSWORD);
		}

		//override current templates with new one
		mTemplates = newTemplates;
		//save the template list
		ResultOperation<Void> saveResult = saveTemplates(context);
		//and checks for errors in saving
		if (saveResult.hasErrors()) {
			saveResult.translateError(res);
			return res;
		}

		//all done, set the result message
		res.setResult(mMessages[MSG_INDEX_TEMPLATES_UPDATED]);
		return res;
	}


	/**
	 * Downloads all services configured for the user from JackSMS site
	 * @return
	 */
	private ResultOperation<String> downloadUserConfiguredServices(Context context) {
		ResultOperation<String> res;

		//first of all, download new templates configuration, because it seems that
		//templates schema changes in the server and this could cause problems
		res = downloadTemplates(context); //TODO ok, ma bisogna evitare di riscaricare tutti i servizi ogni volta
		if (res.hasErrors()) return res;

		mLogFacility.v(LOG_HASH, "Download user configured service");
		String username = getParameterValue(PARAM_INDEX_USERNAME);
		String password = getParameterValue(PARAM_INDEX_PASSWORD);
		String token = mAppPreferencesDao.getLoginString();

		//credential check
		if (!checkCredentialsValidity(username, password))
			return getExceptionForInvalidCredentials();
		if(TextUtils.isEmpty(token))
			return getExceptionForInvalidCredentials();

		//checks for templates
		if (!hasTemplatesConfigured()) {
			mLogFacility.i(LOG_HASH, "JackSMS templates are not present");
			return setSmsProviderException(new ResultOperation<String>(), mMessages[MSG_INDEX_NO_TEMPLATES_TO_USE]);
		}

		String url = mDictionary.getUrlForDownloadUserServices(token);
		HttpClient client = createDefaultClient();
		res = performPost(client, url, null);

		if(res.hasErrors())
			return res;

		if(TextUtils.isEmpty(res.getResult())){
			res.setResult(mMessages[MSG_INDEX_NO_USERSERVICES_TO_USE]);
			return res;
		}

		//at this point, the provider reply should contains the list of user saved subservices
		String providerReply = res.getResult();

		//transform the reply in the list of user services
		List<SmsConfigurableService> newServices = mDictionary.extractUserServices(providerReply);

		//no stored user services
		if (newServices.size() <= 0) {
			res.setResult(mMessages[MSG_INDEX_NO_USERSERVICES_TO_USE]);
			return res;
		}

		//search for twin
		for (SmsConfigurableService service : newServices) {
			searchForServicesTwinAndAdd(service);
		}
		//sort subservices
		Collections.sort(getAllSubservices());

		//save the subservices list
		ResultOperation<Void> saveResult = saveSubservices(context);
		//and checks for errors in saving
		if (saveResult.hasErrors()) {
			saveResult.translateError(res);
			return res;
		}

		res.setResult(mMessages[MSG_INDEX_USERSERVICES_UPDATED]);
		return res;
	}


	/**
	 * Parse the webservice reply searching for know errors code.
	 * If one of them is found, the ResultOperation object is modified
	 * whit the error message to display
	 * 
	 * @param resultToAnalyze
	 * @return true if a JackSMS error is found, otherwise false
	 */
	public boolean parseReplyForErrors(ResultOperation<String> resultToAnalyze)
	{

		if (resultToAnalyze.hasErrors()) return true;

		if(resultToAnalyze.getResult()==null)
			throw new RuntimeException("Errore in sviluppo, assunzione sbagliata");

		String errorMessage = "";
		String reply = resultToAnalyze.getResult();

		//no reply from server is already handled in doRequest method

		//invalid credentials
		if (mDictionary.isInvalidCredetials(reply)) {
			errorMessage = mMessages[MSG_INDEX_INVALID_CREDENTIALS];
			//generic JackSMS internal error
		} else if (mDictionary.isErrorReply(reply)) {
			errorMessage = mDictionary.getTextPartFromReply(reply);

			//TODO marco originale
			//errorMessage = String.format(mMessages[MSG_INDEX_SERVER_ERROR_KNOW], mDictionary.getTextPartFromReply(reply));

			//TODO externalize
			if(TextUtils.isEmpty(errorMessage))
				errorMessage = "Nessun dettaglio sull'errore...";
			//JackSMS unknown internal error
		} else if (mDictionary.isUnmanagedErrorReply(reply)) {
			errorMessage = mMessages[MSG_INDEX_SERVER_ERROR_UNKNOW];
		}

		//errors are internal to JackSMS, not related to communication issues.
		//so no application errors (like network issues) should be returned, but
		//the JackSMS error must stops the execution of the calling method
		if (!TextUtils.isEmpty(errorMessage)) {
			mLogFacility.e(LOG_HASH, "JacksmsProvider error reply");
			mLogFacility.e(LOG_HASH, errorMessage);
			mLogFacility.e(LOG_HASH, reply);
			setSmsProviderException(resultToAnalyze, errorMessage);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Search for service twins in the list of provider services and check also if the
	 * services has the corresponding template
	 * @param newServiceToAdd
	 */
	private void searchForServicesTwinAndAdd(SmsConfigurableService newServiceToAdd)
	{
		mLogFacility.v(LOG_HASH, "Search for service twins:"
				+ "\n Service name: " + newServiceToAdd.getName()
				+ "\n Service id: " + newServiceToAdd.getId()
				+ "\n Service parameters: " + newServiceToAdd.getParametersNumber()
				+ "\n Service template id: " + newServiceToAdd.getTemplateId());
		boolean canAdd = true;

		//twin search
		if (canAdd) {
			for (SmsService service : mSubservices) {
				//already loaded service
				if (service.getId().equalsIgnoreCase(newServiceToAdd.getId())) canAdd = false;

				//exit if the new service cannot be added to the list of services
				if (!canAdd) break;
			}
		}

		//checks if the template for given service exists
		if (canAdd) {
			boolean existsTemplate = false;
			for (SmsService template : mTemplates) {
				if (template.getId().equalsIgnoreCase(newServiceToAdd.getTemplateId())) {
					existsTemplate = true;
					break;
				}
			}
			if (!existsTemplate) {
				canAdd = false;
				//log the error, because is not normal that the template doesn't exist
				mLogFacility.e(LOG_HASH, "Template " + newServiceToAdd.getTemplateId() + " for JackSMS doesn't exist in the provider's templates");
			}
		}

		//add template information
		if (canAdd) {
			newServiceToAdd = (SmsConfigurableService) integrateSubserviceWithTemplateData(newServiceToAdd, newServiceToAdd.getTemplateId());
		}

		if (canAdd) mSubservices.add(newServiceToAdd);
	}

	/**
	 * Save on remote account the service
	 * parameters
	 * 
	 * @param editedService
	 * @author Saverio Guardato
	 */
	@Override
	public ResultOperation<String> saveRemoteservice(SmsService editedService){
		ResultOperation<String> res ;
		String token = mAppPreferencesDao.getLoginString();

		if(TextUtils.isEmpty(token))
			return getExceptionForInvalidCredentials();

		String url = mDictionary.getUrlForEditService(token);

		final HttpClient httpclient = createDefaultClient();
		//per il comando editService devo passare l'id del servizio associato all'account
		List<NameValuePair> nameValuePairs = mDictionary.getParamsForAccountOperation(editedService,
				"id", editedService.getId());
		res = performPost(httpclient, url, nameValuePairs);
		return res;
	}

	/**
	 * Add on remote account the service's 
	 * parameters
	 * 
	 * @param editedService
	 * @author Saverio Guardato
	 */
	@Override
	public ResultOperation<String> addRemoteservice(SmsService editedService) {
		ResultOperation<String> res;

		String token = mAppPreferencesDao.getLoginString();

		if(TextUtils.isEmpty(token))
			return getExceptionForInvalidCredentials();

		String url = mDictionary.getUrlForAddService(token);

		HttpClient httpclient = createDefaultClient();
		//per il comando addService devo passare l'id del template del servizio
		List<NameValuePair> nVp = mDictionary.getParamsForAccountOperation(editedService,
				"service_id", editedService.getTemplateId());
		res = performPost(httpclient, url, nVp);
		return res;
	}

	/**
	 * Delete a service from list stored
	 * online on user's account
	 * 
	 * @param service
	 * @author Saverio Guardato
	 */
	@Override
	public ResultOperation<String> removeRemoteService(SmsService delService) {
		ResultOperation<String> res;
		String token = mAppPreferencesDao.getLoginString();

		if(TextUtils.isEmpty(token))
			return getExceptionForInvalidCredentials();

		final String url = mDictionary.getUrlForDeleteService(token);
		final HttpClient httpclient = createDefaultClient();
		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("ids", delService.getId()));
		res = performPost(httpclient, url, nameValuePairs);
		Thread t = new Thread(new Runnable(){

			// se riesce a cancellare il servizio dal servere bene,
			// altrimenti amen
			@Override
			public void run() {
				try {
					for(int i=0;i<3;i++){
						performPost(httpclient, url, nameValuePairs);
						Thread.sleep(1000);
					}
				} catch (Exception e) {mLogFacility.e(e);}
			}				
		});
		t.start();
		return res;
	}	

	/**
	 * invia la rubrica al server, e riscaricala con i parametri quali operatore
	 * e se è un numero jacksms. Questa operazione salva la rubrica sul sito
	 * @param mContext
	 * @param contatti : la richiesta di POST deve avere come parametro i contatti da
	 * aggiungere nella forma numero=nome&numero=nome&numero=nome..
	 * @return
	 * 
	 * @author Saverio Guardato
	 */

	//freesmee ok
	public ResultOperation<String> getAddressBookWithBk(Context context, List<Contact> contacts) {
		if(contacts==null || contacts.isEmpty())
			return new ResultOperation<String>();
		
		ResultOperation<String> res = null;

		String token = mAppPreferencesDao.getLoginString();
		String url1 = mDictionary.getUrlForImportAddressBook(token);
		mLogFacility.v(LOG_HASH, "[getAddressBookWithBk]Invio comando a:\n"+url1);
		HttpClient httpclient = createDefaultClient();
		//compongo la richiesta POST passando la stringa dei contatti concatenati
		List<NameValuePair> nameValuePairs = mDictionary.getParamsForAddressBook(contacts);
		res = performPost(httpclient, url1, nameValuePairs);

		if (parseReplyForErrors(res))
			logRequest(url1, (HashMap<String,String>)null, null);
		else{
			//questo punto devo riscaricare la lista modificata
			String url2 = mDictionary.getUrlForAddressBook(token);
			mLogFacility.v(LOG_HASH, "[getAddressBookWithBk]Invio comando a:\n"+url2);
			res = performPost(httpclient, url2, null);

			if (parseReplyForErrors(res)){
				logRequest(url2, (HashMap<String,String>)null, null);
				return res;
			}

			if(!TextUtils.isEmpty(res.getResult())){
				String[] rows = res.getResult().split("\n");
				for(String row:rows){
					String[] split = row.trim().split(JacksmsDictionary.TAB_SEPARATOR);

					String operator = null;
					String isJack = null;
					if(split.length>=3){
						String number = split[2];
						String name = split[1];
						String remoteId = split[0];
						if(split.length>3)
							isJack = split[3];
						if(split.length>4)
							operator = split[4];
						Intent updatePhoneNumberIntent = DataService.updatePhoneNumberIntent(context, number, operator, isJack, null);
						if(updatePhoneNumberIntent!=null)
							context.startService(updatePhoneNumberIntent);
					}
				}
			}
		}
		//la lista con i parametri è ora disponibile nel formato scaricato 
		return res;
	}

	/**
	 * fondamentalmente fa la stessa operazione del metodo precedente, ma
	 * usa un API diversa che non salva la rubrica sul server
	 * @param contatti
	 * @return
	 */
	public ResultOperation<String> getAddressBookNoBk(Context context, List<Contact> contacts) {

		if(contacts==null || contacts.isEmpty())
			return new ResultOperation<String>();
		
		ResultOperation<String> res = null;
		String token = mAppPreferenceDao.getLoginString();
		String url = mDictionary.getUrlForNoBkAddressBook(token);
		mLogFacility.v(LOG_HASH, "[getAddressBookNoBk]Invio comando a:\n"+url);

		final HttpClient httpclient = createDefaultClient();
		List<NameValuePair> nvp = mDictionary.getParamsForAddressBook(contacts);
		res = performPost(httpclient, url, nvp);

		if(!TextUtils.isEmpty(res.getResult())){
			String[] rows = res.getResult().split("\n");
			for(String row:rows){
				String[] split = row.trim().split(JacksmsDictionary.TAB_SEPARATOR);

				String operator = null;
				String isJack = null;
				if(split.length>=4){
					String number = split[2];
					isJack = split[3];
					operator = split[4];
					Intent updatePhoneNumberIntent = DataService.updatePhoneNumberIntent(context, number, operator, isJack, null);
					if(updatePhoneNumberIntent!=null)
						context.startService(updatePhoneNumberIntent);
				}
			}
		}
		return res;
	}


	/**
	 * metodo per la registrazione di un nuovo account
	 * che invia una richiesta HTTP post al server q e 
	 * ottiene come risposta due possibilità:
	 * 
	 * <register result="0" msg="Numero di telefono gia' registrato" />
	 * 
	 * oppure
	 * 
	 * <register result="1" user_id="xxxx" code="xxxx" />
	 * 
	 * @author Marco Bettiol
	 * @author Saverio Guardato
	 */

	//TESTED OK
	public ResultOperation<String> registerAccount(String number, String password, String email) {
		ResultOperation<String> res = null;
		//https://api.freesme.com/startRegister?android=3.0&o=csv
		String url = mDictionary.getUrlForStartRegister();
		HttpClient httpclient = createDefaultClient(20000);
		List<NameValuePair> nVp = new ArrayList<NameValuePair>(3);
		nVp.add(new BasicNameValuePair("number", number));
		nVp.add(new BasicNameValuePair("password", password));
		nVp.add(new BasicNameValuePair("email", email));
		res = performPost(httpclient, url, nVp);
		return detectedInvalidReply(res);
	}	

	/**
	 * metodo per inviare il codice di conferma e verificare il proprio
	 * account
	 * 
	 * @author marcobettiol
	 * @author Saverio Guardato
	 */

	//TESTED OK
	public ResultOperation<String> confirmAccount(String userId, String code) {
		ResultOperation<String> res = null;
		String url = mDictionary.getUrlForVerifyRegister();
		HttpClient httpclient = createDefaultClient();
		List<NameValuePair> nVp = new ArrayList<NameValuePair>(2);
		nVp.add(new BasicNameValuePair("user_id", userId));
		nVp.add(new BasicNameValuePair("code", code));
		res = performPost(httpclient, url, nVp);
		return detectedInvalidReply(res);
	}


	private ResultOperation<String> detectedInvalidReply(ResultOperation<String> res){
		if(!res.hasErrors() && !TextUtils.isEmpty(res.getResult())){
			String reply = res.getResult();
			if(reply.startsWith("1"+JacksmsDictionary.TAB_SEPARATOR) ||
					reply.startsWith("0"+JacksmsDictionary.TAB_SEPARATOR)){
				return res;
			}
		}
		setSmsProviderException(res, mMessages[MSG_INDEX_SERVER_ERROR_UNKNOW]);
		return res;
	}

	/**
	 *
	 * metodo che prepara il messagio per l'utente,
	 * 
	 * non viene mai chiamato in caso di errori!
	 * @param result
	 * @return
	 * 
	 * @author marcobettiol 
	 */
	private String prepareOkMessageForUser(String detailMessage, int sent){
		if(detailMessage==null || TextUtils.equals(detailMessage, "null"))
			detailMessage="";
		String messageInLowerCase=detailMessage.toLowerCase();
		//TODO marco, dovrebbe essere internazionalizzato
		if(!messageInLowerCase.contains("residui") &&
				!messageInLowerCase.contains("residuo") &&
				!messageInLowerCase.contains("rimanenti") &&
				!messageInLowerCase.contains("rimanente"))
			//TODO multilingua, esternalizza
			return "Oggi hai inviato "+sent+" sms con questo servizio.";	
		else
			return detailMessage;
	}

	private void updatePhoneNumberData(String phoneNumber, String serviceId, int operator, int isJack){
		if(TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(serviceId))
			return;
		Intent intent = DataService.updatePhoneNumberIntent(mBaseContext, phoneNumber, Integer.toString(operator), Integer.toString(isJack), serviceId);
		if(intent!=null)
			mBaseContext.startService(intent);
	}

	private static class ReplyRequest{

		private final String mToken;
		private final String mServiceId;
		private final String mNumber;

		public ReplyRequest(String token, String number, String serviceId) {
			mToken = token;
			mNumber = number;
			mServiceId = serviceId;
		}

		public String getToken(){
			return mToken;
		}

		public String getServiceId(){
			return mServiceId;
		}

		public String getNumber(){
			return mNumber;
		}
	}

	private static class ReplyRequestBuffer{

		private static final int SIZE = 5;
		int i=0;
		private final ReplyRequest[] mReplyRequests = new ReplyRequest[SIZE];

		public void add(ReplyRequest rr){
			if(rr==null)
				return;
			mReplyRequests[i]=rr;
			i= (i+1) % SIZE;
		}

		public ReplyRequest remove(String token){
			if(token==null)
				return null;
			//dummy search
			for(int t=0;t<SIZE;t++){
				ReplyRequest rr=mReplyRequests[t];
				if(rr!=null && rr.getToken().equals(token)){
					mReplyRequests[t]=null;
					return rr;
				}	
			}
			return null;
		}

	}

	public ResultOperation<String> setNotifyType(String notifyType, String registration_id) {
		String token = mAppPreferencesDao.getLoginString();

		if(TextUtils.isEmpty(token)){
			return getExceptionForInvalidCredentials();
		}

		String url = mDictionary.getUrlForSetNotifyType(token);
		final HttpClient httpclient = createDefaultClient();
		List<NameValuePair> nvp = new ArrayList<NameValuePair>(2);
		nvp.add(new BasicNameValuePair(NotifyType.P.NOTIFY_TYPE, notifyType));
		nvp.add(new BasicNameValuePair(NotifyType.P.REGISTRATION_ID, registration_id));
		ResultOperation<String> res = performPost(httpclient, url, nvp);

		if(res.hasErrors())
			return res;

		if(mDictionary.checkServerNotifyType(res.getResult(), notifyType))
			return res; // ok
		else{
			setSmsProviderException(res, "Risposta del server non valida");
			logRequest(url, nvp, res);
		}
		return res;
	}	


	private ResultOperation<String> performPost(HttpClient client, String url, List<NameValuePair> nvp){
		String reply = "";

		try {
			HttpPost httpPost = new HttpPost(url);
			if(nvp!=null)
				httpPost.setEntity(new UrlEncodedFormEntity(nvp, HTTP.UTF_8));
			HttpResponse response = client.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if(entity!=null) {
				//reply = EntityUtils.toString(entity); accenti errati
				reply = new String (EntityUtils.toByteArray(entity));
			}
				
		} catch (IllegalArgumentException e) {
			return new ResultOperation<String>(e, ResultOperation.RETURNCODE_ERROR_APPLICATION_ARCHITECTURE);
		} catch (ClientProtocolException e) {
			return new ResultOperation<String>(e, ResultOperation.RETURNCODE_ERROR_COMMUNICATION);
		}catch (ConnectTimeoutException e){
			return new ResultOperation<String>(new SocketTimeoutException(ERROR_MSG_TIMEOUT), ResultOperation.RETURNCODE_ERROR_COMMUNICATION);
		}catch (SocketTimeoutException e) {
			return new ResultOperation<String>(new SocketTimeoutException(ERROR_MSG_TIMEOUT), ResultOperation.RETURNCODE_ERROR_COMMUNICATION);
		}catch (IOException e) {
			return new ResultOperation<String>(e, ResultOperation.RETURNCODE_ERROR_COMMUNICATION);
		}

		//return the reply
		return new ResultOperation<String>(reply);
	}


	private static final int TIMEOUT = 60000;

	private HttpClient createDefaultClient(){
		return createDefaultClient(TIMEOUT);
	}

	private HttpClient createDefaultClient(int timeout){

		HttpParams params = new BasicHttpParams();

		HttpConnectionParams.setTcpNoDelay(params, true);
		HttpConnectionParams.setSoTimeout(params, timeout);
		HttpConnectionParams.setConnectionTimeout(params, timeout);

	

//		SSLSocketFactory sf = null;
//		try {
//			sf = new MySSLSocketFactory(trustStore);
//		} catch (KeyManagementException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (KeyStoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnrecoverableKeyException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);


		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		registry.register(new Scheme("https", MySSLSocketFactory.getInstance(), 443));

		ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

		return new DefaultHttpClient(ccm, params);
	}


	/**
	 * Write headers and url in the log, so a better debugger could be made
	 * when a report is sent to the developer
	 * 
	 * @param url
	 * @param headers
	 */
	protected void logRequest(String url, List<NameValuePair> nvp, ResultOperation<String> res)
	{
		mLogFacility.e(LOG_HASH, getName() + " provider request content");
		if (!TextUtils.isEmpty(url)) {
			mLogFacility.e(LOG_HASH, "Url");
			mLogFacility.e(LOG_HASH, Base64Helper.encodeBytes(url.getBytes()));
		}
		if (null != nvp) {
			mLogFacility.e(LOG_HASH, "Post Params");
			for (NameValuePair param : nvp) {
				mLogFacility.e(LOG_HASH, Base64Helper.encodeBytes(param.getName().getBytes()));
				mLogFacility.e(LOG_HASH, Base64Helper.encodeBytes(param.getValue().getBytes()));
			}
		}
		if (null != res) {
			StringBuilder sb = new StringBuilder();
			sb.append("Result:\n")
			.append(Base64Helper.encodeBytes(res.getResult().getBytes()));
			if(res.getException()!=null)
				sb.append("\n Exception:")
				.append(Base64Helper.encodeBytes(res.getClass().getName().getBytes()))
				.append("\n")
				.append(Base64Helper.encodeBytes(res.getException().getMessage().getBytes()))
				.append("\n ReturnCode :")
				.append(res.getReturnCode());
			mLogFacility.e(LOG_HASH, sb.toString());
		}
	}


	public static class MySSLSocketFactory extends SSLSocketFactory {

		private static MySSLSocketFactory mySSLSocketFactory;
		
		public static SSLSocketFactory getInstance(){
			synchronized (MySSLSocketFactory.class) {
				if(mySSLSocketFactory==null){
					KeyStore trustStore = null;
						try {
							trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
						} catch (KeyStoreException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							trustStore.load(null, null);
						} catch (NoSuchAlgorithmException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (CertificateException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						};
						
						try {
							mySSLSocketFactory = new MySSLSocketFactory(trustStore);
						} catch (KeyManagementException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (KeyStoreException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnrecoverableKeyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mySSLSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
						
				}
				return mySSLSocketFactory;
			}
		}
		
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}


	//http://foo.jasonhudgins.com/2009/08/http-connection-reuse-in-android.html

	public static class HttpClientFactory {

		private static DefaultHttpClient client;

		public synchronized static DefaultHttpClient getThreadSafeClient() {

			if (client != null)
				return client;

			client = new DefaultHttpClient();

			ClientConnectionManager mgr = client.getConnectionManager();

			HttpParams params = client.getParams();
			client = new DefaultHttpClient(
					new ThreadSafeClientConnManager(params,
							mgr.getSchemeRegistry()), params);

			return client;
		} 
	}


	/**
	 * Create an exception for empty username or password
	 * @return
	 */
	protected ResultOperation<String> getExceptionForInvalidCredentials() {
		mLogFacility.v(LOG_HASH, "Invalid user credential :(");
		return new ResultOperation<String>(new Exception(mMessages[MSG_INDEX_INVALID_CREDENTIALS]),ResultOperation.RETURNCODE_ERROR_INVALID_CREDENTIAL);
	}

	
	private ResultOperation<String> sendMessageUDP(String token, String serviceId, String destination, String message, int timeout){
		
		String command;
		if(TextUtils.equals(serviceId,SendService.Ids.JMS))
			command = mDictionary.getUDPParamsForSendingJms(token, destination, message);
		else{
			SmsService service = getSubservice(serviceId);
			command = mDictionary.getUDPParamsForSendingMessageByTemplate(token, service, destination, message);
		}
		ResultOperation<byte[]> res = performUDPCommand(command, timeout);
		if(res.hasErrors())
			return new ResultOperation<String>(res.getException(), res.getReturnCode());
		return parseSendAndCaptchaReplyUDP(res);
		
	}
	
	private ResultOperation<String> parseSendAndCaptchaReplyUDP(ResultOperation<byte[]> res){
		byte[] rawReply = res.getResult();
		String reply = new String(rawReply);
		JSONObject fakeReply = null;
		
		if(!TextUtils.isEmpty(reply)){
			//"result queue sent carrier isfs upgrade message"
			//"session_id immagine" (tra session_id e immagine c'è tab, l'immagine è binaria)
			
			//"status":1
			//"result":1,
			//"message":"Messaggio inviato",
			//"queue":0,
			//"sent":"3",
			//"carrier":"1",
			//"isfs":1,
			//"upgrade":0}
			
			String[] split = reply.split(JacksmsDictionary.TAB_SEPARATOR);
			fakeReply = new JSONObject();
			
			
			try {
				
				int result= Integer.parseInt(split[0]);
				fakeReply.put("status", 1);//  suppose request is ok
				
				fakeReply.put("result", result);
				if(split.length>1 && result>=0){
					switch (result) {
					case 0:{
						
						String error = "";
						if(split.length>6 && split[6]!=null)
							error = split[6];
						return new ResultOperation<String>(new Exception(error),ResultOperation.RETURNCODE_ERROR_PROVIDER_ERROR_REPLY); 
						}
					case 1:
						fakeReply.put("queue", Integer.parseInt(split[1]));
						break;
					default:
						int tabPos = reply.indexOf(JacksmsDictionary.TAB_SEPARATOR);
						
						fakeReply.put("message", Base64Helper.encodeBytes(rawReply, tabPos+1, rawReply.length-tabPos-1));
						return new ResultOperation<String>(fakeReply.toString());
						// non proseguire con gli split poichè possono essere presenti dei dati 
						// binari che vengono interpetati erratamente (contengono \t)
					}
					
				}
				if(split.length>2)
					fakeReply.put("sent", Integer.parseInt(split[2]));
				if(split.length>3)
					fakeReply.put("carrier", Integer.parseInt(split[3]));
				if(split.length>4)
					fakeReply.put("isfs", Integer.parseInt(split[4]));
				if(split.length>5)
					fakeReply.put("upgrade", Integer.parseInt(split[5]));
				if(split.length>6)
					fakeReply.put("message", split[6]);
				
				return new ResultOperation<String>(fakeReply.toString());
				
			}catch (Exception e) {
				// cannot appen
				return new ResultOperation<String>(new Exception(), ResultOperation.RETURNCODE_ERROR_GENERIC);
			} 
		}else{
			//empty reply
			return new ResultOperation<String>(new Exception(), ResultOperation.RETURNCODE_ERROR_EMPTY_REPLY);
		}
	}
	
	private ResultOperation<byte[]> performUDPCommand(String messageStr, int timeout){
		DatagramSocket s = null;
		byte[] reply = null;
		try{
			try {
				s = new DatagramSocket();
				s.setSoTimeout(timeout);
			} catch (SocketException e) {
				return new ResultOperation<byte[]>(e, ResultOperation.RETURNCODE_ERROR_COMMUNICATION);
			}
			InetAddress host;
			try {
				host = InetAddress.getByName(JacksmsDictionary.UDP_HOST);
			} catch (UnknownHostException e) {
				return new ResultOperation<byte[]>(e, ResultOperation.RETURNCODE_ERROR_COMMUNICATION);
			}
			
			byte[] message;
			byte header;
			{
				 byte[] originalBytes = messageStr.getBytes();
	
			     ByteArrayOutputStream baos = new ByteArrayOutputStream();
			     
			     ZOutputStream zOut=new ZOutputStream(baos, JZlib.Z_BEST_COMPRESSION);
			     
			     try {
			    	 zOut.write(originalBytes, 0, originalBytes.length);
					 zOut.finish();
				 } catch (IOException e) {
					//cannot happen
				}
				 
			     byte[] compressedBytes = baos.toByteArray();
			     
			     header = compressedBytes[0];
			     
			     if(originalBytes.length>compressedBytes.length-1){
			    	 byte[] stripHeader = new byte[compressedBytes.length-1];
			    	 System.arraycopy(compressedBytes, 1, stripHeader, 0, stripHeader.length);
			    	 
			    	 message = stripHeader;
			     }else
			    	 message = originalBytes;
			}
			
			DatagramPacket p = new DatagramPacket(message, message.length,host,JacksmsDictionary.UDP_PORT);
			try {
				s.send(p);
			} catch (IOException e) {
				return new ResultOperation<byte[]>(e, ResultOperation.RETURNCODE_ERROR_COMMUNICATION);
			}
			
			DatagramPacket receivedPkt = new DatagramPacket(new byte[Short.MAX_VALUE], Short.MAX_VALUE);
			try {
				s.receive(receivedPkt);
			} catch (IOException e) {
				return new ResultOperation<byte[]>(new SocketTimeoutException(ERROR_MSG_TIMEOUT), ResultOperation.RETURNCODE_ERROR_COMMUNICATION);
			}
		
			
				// check if decompression is required
				byte[] rawBytes = new byte[receivedPkt.getLength()];
				System.arraycopy(receivedPkt.getData(), 0, rawBytes, 0, receivedPkt.getLength());
		
				if(rawBytes.length>0){
					try{
						
						byte[] compressed = new byte[rawBytes.length+1];
						System.arraycopy(rawBytes, 0, compressed, 1, rawBytes.length);
						compressed[0] = header;
						ZInputStream zin = new ZInputStream(new ByteArrayInputStream(compressed));
						byte[] buf = new  byte[8192];
						int read;
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						while((read = zin.read(buf))!=-1)
							baos.write(buf, 0, read);
						reply = baos.toByteArray();
					}catch (IOException e) {
						reply = rawBytes;
					}
				
				}
				return new ResultOperation<byte[]>(reply);
			
		}finally{
			if(s!=null)
				s.close();
		}
	}
}
