package it.rainbowbreeze.smsforfree.providers;

public class AimonDictionary {
	//---------- Ctors

	
	
	
	//---------- Private fields
	
	
	
	//---------- Public fields
	public final static String URL_BASE = "https://secure.apisms.it/";
	public final static String URL_GET_CREDIT = URL_BASE + "http/get_credit";
	public final static String URL_SEND_SMS = URL_BASE + "http/send_sms";
	public static final String URL_SEND_SMS_FREE = "http://aimon.it/index.php?cmd=smsgratis&sez=smsgratis";

	public final static String PARAM_USERNAME = "authlogin";
	public final static String PARAM_PASSWORD = "authpasswd";
	public final static String PARAM_SENDER = "sender";
	public final static String PARAM_DESTINATION = "destination";
	public final static String PARAM_BODY = "body";
	public final static String PARAM_ID_API = "id_api";
	
	public static final String RESULT_SENDSMS_OK = "+01 SMS Queued";
	public static final String RESULT_ERROR_ACCESS_DENIED = "-3-";
	public static final String RESULT_ERROR_MISSING_PARAMETERS = "-5-";
	public static final String RESULT_ERROR_INTERNAL_SERVER_ERROR = "-32-";
	public static final String RESULT_ERROR_INVALID_DESTINATION = "-100-";
	public static final String RESULT_ERROR_DESTINATION_NOT_ALLOWED = "-101-";
	public static final String RESULT_ERROR_BODY_HAS_INVALID_CHARS = "-102-";
	public static final String RESULT_ERROR_NOT_ENOUGH_CREDIT = "-103-";
	public static final String RESULT_ERROR_INVALID_SENDER = "-105-";


	public final static int MAX_SENDER_LENGTH_ALPHANUMERIC = 11;
	public final static int MAX_SENDER_LENGTH_NUMERIC = 21;
	public final static int MAX_BODY_LENGTH = 612;
	
	
	
	
	//---------- Public properties

	
	
	
	//---------- Public methods
	
	
	
	//---------- Private methods

}
