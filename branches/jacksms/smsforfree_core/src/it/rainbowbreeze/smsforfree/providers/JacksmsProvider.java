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
import it.rainbowbreeze.smsforfree.ui.ActivityHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

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
	protected AppPreferencesDao mappPreferenceDao;

	protected String[] mMessages;




	//---------- Constructors
	public JacksmsProvider(
			LogFacility logFacility,
			AppPreferencesDao appPreferencesDao,
			ProviderDao providerDao,
			ActivityHelper activityHelper)
	{
		super(logFacility, PARAM_NUMBER, appPreferencesDao, providerDao, activityHelper);
		mappPreferenceDao = appPreferencesDao;
	}




	//---------- Public properties
	public final static int COMMAND_LOADTEMPLATESERVICES = 1000;
	public final static int COMMAND_LOADUSERSERVICES = 1001;

	@Override
	public String getId()
	{ return "JackSMS"; }

	@Override
	public String getName()
	{ return "JackSMS"; }

	@Override
	public int getParametersNumber()
	{ return PARAM_NUMBER; }




	//---------- Public methods

	@Override
	public ResultOperation<Void> initProvider(Context context)
	{
		mDictionary = new JacksmsDictionary(mLogFacility);

		//provider parameters
		setParameterDesc(PARAM_INDEX_USERNAME, context.getString(R.string.jacksms_username_desc));
		setParameterDesc(PARAM_INDEX_PASSWORD, context.getString(R.string.jacksms_password_desc));
		setParameterFormat(PARAM_INDEX_PASSWORD, SmsServiceParameter.FORMAT_PASSWORD);
		setDescription(context.getString(R.string.jacksms_description));

		//save messages
		mMessages = new String[12];
		mMessages[MSG_INDEX_INVALID_CREDENTIALS] = context.getString(R.string.jacksms_msg_invalidCredentials);
		mMessages[MSG_INDEX_SERVER_ERROR_KNOW] = context.getString(R.string.jacksms_msg_serverErrorKnow);
		mMessages[MSG_INDEX_SERVER_ERROR_UNKNOW] = context.getString(R.string.jacksms_msg_serverErrorUnknow);
		mMessages[MSG_INDEX_MESSAGE_SENT] = context.getString(R.string.jacksms_msg_messageSent);
		mMessages[MSG_INDEX_NO_CAPTCHA_SESSION_ID] = context.getString(R.string.jacksms_msg_noCaptchaSessionId);
		mMessages[MSG_INDEX_NO_TEMPLATES_PARSED] = context.getString(R.string.jacksms_msg_noTemplatesParsed);
		mMessages[MSG_INDEX_NO_CAPTCHA_PARSED] = context.getString(R.string.jacksms_msg_noCaptcha);
		mMessages[MSG_INDEX_TEMPLATES_UPDATED] = context.getString(R.string.jacksms_msg_templatesListUpdated);
		mMessages[MSG_INDEX_CAPTCHA_OK] = context.getString(R.string.jacksms_msg_captchaOk);
		mMessages[MSG_INDEX_NO_TEMPLATES_TO_USE] = context.getString(R.string.jacksms_msg_noTemplatesToUse);
		mMessages[MSG_INDEX_USERSERVICES_UPDATED] = context.getString(R.string.jacksms_msg_userServicesListUpdated);
		mMessages[MSG_INDEX_NO_USERSERVICES_TO_USE] = context.getString(R.string.jacksms_msg_noUserServices);

		return super.initProvider(context);
	}

	/*
	 * get LoginString for http operations
	 */
	public ResultOperation<String> getLoginString(String username, String password){
		if(TextUtils.isEmpty(username))username = getParameterValue(PARAM_INDEX_USERNAME);
		if(TextUtils.isEmpty(password))password = getParameterValue(PARAM_INDEX_PASSWORD);

		ResultOperation<String> res = null;
		String urlStr = mDictionary.getUrlForLoginString(username, password);
		res = doSingleHttpRequest(urlStr, null, null);
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
		String loginS = mappPreferenceDao.getLoginString();

		ResultOperation<String> res = validateSendSmsParameters(username, password, destination, messageBody);
		if (res.hasErrors()) return res;
		mLogFacility.i(LOG_HASH, "No error in res.");
		//sends the sms
		SmsService service = getSubservice(serviceId);
		String url = mDictionary.getUrlForSendingMessage(loginS);
		HashMap<String, String> params = mDictionary.getParamsForSendingMessage(service, destination, messageBody);
		res = doSingleHttpRequest(url, null, params);

		//TODO marco
		// quello che c'era qui sostituiva il result con un messaggio di errore non parsabile
		// non si pu� fare
		
		//checks for errors
		if (parseReplyForErrors(res)){
			//log action data for a better error management
			logRequest(url, params, null);
			return res;
		}

		//at this point, no error happened, so checks if the sms was sent or
		//a captcha code is needed

		String reply = res.getResult();
		
		//message sent
		if (mDictionary.isSmsCorrectlySent(reply)) {
			//TODO marco questa operazione non si pu� fare in questo modo. Ma la puoi fare a livello pi� alto
			
			//se il servizio risponde con un messaggio, potrebbe dirci gli sms residui
//			if(!TextUtils.isEmpty(tokens[1]) &&
//					tokens[1].contains("residui")){
//				res.setResult("Sms residui per questo servizio: "+
//						tokens[1].replaceAll( "[^\\d]", "" ));
//			}
//			else
//				res.setResult("Oggi hai inviato "+tokens[3]+" sms con questo servizio.");
			
			//TODO questo � l'originale
			//res.setResult(String.format(mMessages[MSG_INDEX_MESSAGE_SENT], mDictionary.getTextPartFromReply(reply)));
		
	
			res.setResult(prepareOkMessageForUser(res));
		}
		//captcha request
		else if (mDictionary.isCaptchaRequest(reply)) {
			//returns captcha, message contains all captcha information
			res.setReturnCode(ResultOperation.RETURNCODE_SMS_CAPTCHA_REQUEST);
		} else {
			//other generic error not handled by the parseReplyForErrors() method
			setSmsProviderException(res, mMessages[MSG_INDEX_SERVER_ERROR_UNKNOW]);
			mLogFacility.e(LOG_HASH, "Error sending message in Jacksms Provider");
			mLogFacility.e(LOG_HASH, reply);
			
			//TODO marco
			//qui non ci dovrebbe mai arrivare!
			throw new RuntimeException("Assunzione errata");
			
		}

		return res;    	
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

		//find captcha sessionId
		String sessionId = mDictionary.getCaptchaSessionIdFromReply(providerReply);
		if (TextUtils.isEmpty(sessionId)) {
			mLogFacility.e(LOG_HASH, "Captcha session id is empty");
			return setSmsProviderException(new ResultOperation<String>(), mMessages[MSG_INDEX_NO_CAPTCHA_SESSION_ID]);
		}

		String loginS = mappPreferenceDao.getLoginString();

		//sends the captcha code
		String url = mDictionary.getUrlForSendingCaptcha(loginS);
		HashMap<String, String> headers = mDictionary.getHeaderForSendingCaptcha(sessionId, captchaCode);
		ResultOperation<String> res = doSingleHttpRequest(url, headers, null);
		res.getException();
		//checks for errors
		if (parseReplyForErrors(res)){
			//log action data for a better error management
			logRequest(url, headers, null);
			return res;
		}

		//at this point, no error happened, so the reply contains captcha submission result
		String reply = res.getResult();
		String returnMessage = mDictionary.getTextPartFromReply(reply);
		if (mDictionary.isCaptchaCorrectlySent(reply)) {
			
			//TODO marco sempre solito errore
//			String [] tokens = reply.split("\t");
//			//se il servizio risponde con un messaggio, potrebbe dirci gli sms residui
//			if(!TextUtils.isEmpty(tokens[1])){
//				returnMessage = "Sms residui per questo servizio: "+
//				tokens[1].replaceAll( "[^\\d]", "" );
//			}
//			else
//				returnMessage = "Oggi hai inviato "+tokens[3]+" sms con questo servizio.";
			
			//TODO marco modifica rispetto all'originale, non modifichiamo il messaggio ricevuto dal server
			// non sostituire il messaggio originale!!
			// il codice di alfredo sbaglia a fr
//			returnMessage = mMessages[MSG_INDEX_CAPTCHA_OK];
//			res.setResult(returnMessage);
			
			//TODO marco
			res.setResult(prepareOkMessageForUser(res));
		} else {
			mLogFacility.e(LOG_HASH, "Error sending message in Jacksms Provider");
			mLogFacility.e(LOG_HASH, reply);
			setSmsProviderException(res, returnMessage);
		}
		return res;
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
		return context.getString(R.string.jacksms_registerLink);
	}


	@Override
	protected List<SmsServiceCommand> loadSubservicesListActivityCommands(Context context)
	{
		List<SmsServiceCommand> commands = super.loadSubservicesListActivityCommands(context);

		//subservices commands list
		SmsServiceCommand command;
		mSubservicesListActivityCommands = new ArrayList<SmsServiceCommand>();
		command = new SmsServiceCommand(
				COMMAND_LOADTEMPLATESERVICES, context.getString(R.string.jacksms_commandLoadTemplateServices), 1000, R.drawable.ic_menu_refresh);
		commands.add(command);
		command = new SmsServiceCommand(
				COMMAND_LOADUSERSERVICES, context.getString(R.string.jacksms_commandLoadUserSubservices), 1001, R.drawable.ic_menu_cloud);
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

		//credential check
		if (!checkCredentialsValidity(username, password))
			return getExceptionForInvalidCredentials();

		String url = mDictionary.getUrlForDownloadTemplates(username, password);
		ResultOperation<String> res = doSingleHttpRequest(url, null, null);

		//checks for errors
		if (parseReplyForErrors(res)){
			//log action data for a better error management
			logRequest(url, null, null);
			return res;
		}

		//at this point, the provider reply should contains the list of templates
		String templatesReply = res.getResult();

		//transform the reply in the list of templates
		List<SmsService> newTemplates = mDictionary.extractTemplates(mLogFacility, templatesReply);

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
		res = downloadTemplates(context);
		if (res.hasErrors()) return res;

		mLogFacility.v(LOG_HASH, "Download user configured service");
		String username = getParameterValue(PARAM_INDEX_USERNAME);
		String password = getParameterValue(PARAM_INDEX_PASSWORD);

		//credential check
		if (!checkCredentialsValidity(username, password))
			return getExceptionForInvalidCredentials();

		//checks for templates
		if (!hasTemplatesConfigured()) {
			mLogFacility.i(LOG_HASH, "JackSMS templates are not present");
			return setSmsProviderException(new ResultOperation<String>(), mMessages[MSG_INDEX_NO_TEMPLATES_TO_USE]);
		}

		String url = mDictionary.getUrlForDownloadUserServices(username, password);
		res = doSingleHttpRequest(url, null, null);

		//checks for errors
		if (parseReplyForErrors(res)){
			//log action data for a better error management
			logRequest(url, null, null);
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

		//TODO marco
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
			
			//TODO marco,
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
	 */
	@Override
	public void saveRemoteservice(SmsService editedService){

		String username = getParameterValue(PARAM_INDEX_USERNAME);
		String password = getParameterValue(PARAM_INDEX_PASSWORD);

		String url = mDictionary.getUrlForSaveService(username, password);

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		try {
			//per il comando editService devo passare l'id del servizio associato all'account
			List<NameValuePair> nameValuePairs = mDictionary.getParamsForAccountOperation(editedService,
					"id", editedService.getId());
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			httpclient.execute(httppost);
		} catch (Exception ex){mLogFacility.e(ex);} 		
	}

	/**
	 * Add on remote account the service's 
	 * parameters
	 * 
	 * @param editedService
	 */
	@Override
	public void addRemoteservice(SmsService editedService) {
		String username = getParameterValue(PARAM_INDEX_USERNAME);
		String password = getParameterValue(PARAM_INDEX_PASSWORD);

		String url = mDictionary.getUrlForAddService(username, password);

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		try {
			//per il comando addService devo passare l'id del template del servizio
			List<NameValuePair> nameValuePairs = mDictionary.getParamsForAccountOperation(editedService,
					"service_id", editedService.getTemplateId());
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			httpclient.execute(httppost);
		} catch (Exception ex){mLogFacility.e(ex);}
	}

	/**
	 * Delete a service from list stored
	 * online on user's account
	 * 
	 * @param service
	 */
	@Override
	public void removeRemoteService(SmsService delService) {
		String username = getParameterValue(PARAM_INDEX_USERNAME);
		String password = getParameterValue(PARAM_INDEX_PASSWORD);

		String url = mDictionary.getUrlForDeleteService(username, password);
		final HttpClient httpclient = new DefaultHttpClient();
		final HttpPost httppost = new HttpPost(url);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("ids", delService.getId()));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (Exception ex){mLogFacility.e(ex);}
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					for(int i=0;i<3;i++){
						httpclient.execute(httppost);
						Thread.sleep(500);
					}
				} catch (Exception e) {mLogFacility.e(e);}
			}				
		});
		t.start();
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
	private String prepareOkMessageForUser(ResultOperation<String> result){
		result.getReturnCode();
		result.getResult();
		if(result.hasErrors())
			throw new RuntimeException("Errore in sviluppo");
		String textPartFromReply = mDictionary.getTextPartFromReply(result.getResult());
		
//		if(TextUtils.isEmpty(textPartFromReply)) // nessun messaggio esplicito
//			return mMessages[MSG_INDEX_MESSAGE_SENT];
		
		String messageInLowerCase=textPartFromReply.toLowerCase();
		String[] split = result.getResult().split(JacksmsDictionary.TAB_SEPARATOR);
		//TODO marco, dovrebbe essere internazionalizzato
		if(!messageInLowerCase.contains("residui") &&
		   !messageInLowerCase.contains("residuo") &&
		   !messageInLowerCase.contains("rimanenti") &&
		   !messageInLowerCase.contains("rimanente"))
			return "Oggi hai inviato "+split[3]+" sms con questo servizio.";	
		else
			return textPartFromReply;
	}
}