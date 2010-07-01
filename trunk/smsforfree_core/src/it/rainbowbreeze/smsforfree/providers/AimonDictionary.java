package it.rainbowbreeze.smsforfree.providers;


public class AimonDictionary
{
	//---------- Ctors

	
	
	
	//---------- Private fields
	private static final String RESULT_FREE_ERRORMSG_INVALID_CREDENTIALS = "Nome utente e/o password non riconosciute";
	private static final String RESULT_FREE_WELCOME_MESSAGE_FOR_THE_USER = "Ciao <b>%s</b>!";
	private static final String RESULT_FREE_ERRORMSG_NOT_ENOUGH_CREDIT = "Credito non sufficiente per spedire altri messaggi";
	private static final String RESULT_FREE_SENT_OK = "Messaggio inviato con successo";

	


	//---------- Public fields
	public final static String URL_BASE = "https://secure.apisms.it/";
	public final static String URL_GET_CREDIT = URL_BASE + "http/get_credit";
	public final static String URL_SEND_SMS = URL_BASE + "http/send_sms";
	public final static String URL_SEND_SMS_FREE_1 = "http://aimon.it/?cmd=smsgratis";
	public static final String URL_SEND_SMS_FREE_2 = "http://aimon.it/index.php?cmd=smsgratis&sez=smsgratis";
	public static final String URL_SEND_SMS_FREE_3 = "http://aimon.it/index.php?cmd=smsgratis&sez=smsgratis&azione=logout";

	public final static String PARAM_USERNAME = "authlogin";
	public final static String PARAM_PASSWORD = "authpasswd";
	public final static String PARAM_SENDER = "sender";
	public final static String PARAM_DESTINATION = "destination";
	public final static String PARAM_BODY = "body";
	public final static String PARAM_ID_API = "id_api";
	
	public static final String RESULT_SENDSMS_OK = "+01 SMS Queued";
	public static final String RESULT_ERRORCODE_ACCESS_DENIED = "-3-";
	public static final String RESULT_ERRORCODE_MISSING_PARAMETERS = "-5-";
	public static final String RESULT_ERRORCODE_INTERNAL_SERVER_ERROR = "-32-";
	public static final String RESULT_ERRORCODE_INVALID_DESTINATION = "-100-";
	public static final String RESULT_ERRORCODE_DESTINATION_NOT_ALLOWED = "-101-";
	public static final String RESULT_ERRORCODE_BODY_HAS_INVALID_CHARS = "-102-";
	public static final String RESULT_ERRORCODE_NOT_ENOUGH_CREDIT = "-103-";
	public static final String RESULT_ERRORCODE_INVALID_SENDER = "-105-";
	
	public final static int MAX_SENDER_LENGTH_ALPHANUMERIC = 11;
	public final static int MAX_SENDER_LENGTH_NUMERIC = 21;
	public final static int MAX_BODY_LENGTH = 612;
	
	
	
	
	//---------- Public properties



	
	//---------- Public methods
	/**
	 * Checks if the login for free sms is valid
	 */
	public boolean isFreeSmsLoginOk(String message, String username) {
		return message.contains(
				String.format(RESULT_FREE_WELCOME_MESSAGE_FOR_THE_USER, username));
	}

	/**
	 * Checks if the login for free sms returns an invalid credential page
	 */
	public boolean isFreeSmsLoginInvalidCredentials(String message) {
		return message.contains(RESULT_FREE_ERRORMSG_INVALID_CREDENTIALS);
	}
	
	/**
	 * Checks if the send for free sms correctly sent the sms
	 */
	public boolean isFreeSmsSendMessageOk(String message) {
		return message.contains(RESULT_FREE_SENT_OK);
	}
	
	
	
	//---------- Private methods

}
