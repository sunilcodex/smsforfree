package it.rainbowbreeze.smsforfree.services;

public class AimonDictionary {
	//---------- Ctors

	
	
	
	//---------- Private fields
	private final static String URL_BASE = "https://secure.apisms.it/";
	private final static String URL_GET_CREDIT = URL_BASE + "http/get_credit";
	private final static String URL_SEND_SMS = URL_BASE + "http/send_sms";

	
	
	
	//---------- Public fields
	public final static String PARAM_USERNAME = "authlogin";
	public final static String PARAM_PASSWORD = "authpasswd";
	public static final String PARAM_SENDER = "sender";
	public static final String PARAM_DESTINATION = "destination";
	public static final String PARAM_BODY = "body";
	public static final String PARAM_ID_API = "id_api";
	
	public static final String ID_API_MITTENTE_FISSO = "106"; 
	public static final String ID_API_MITTENTE_LIBERO = "59"; 
	public static final String ID_API_MITTENTE_LIBERO_REPORT = "84"; 
	
	public static final String RESULT_OK = "+01 SMS Queued";
	public static final String RESULT_ERROR_ACCESS_DENIED = "-3";
	public static final String RESULT_ERROR_INTERNAL_SERVER_ERROR = "-32";
	public static final String RESULT_ERROR_INVALID_DESTINATION = "-100";
	public static final String RESULT_ERROR_DESTINATION_NOT_ALLOWED = "-101";
	public static final String RESULT_ERROR_BODY_HAS_INVALID_CHARS = "-102";
	public static final String RESULT_ERROR_NOT_ENOUGH_CREDIT = "-103";
	public static final String RESULT_ERROR_INVALID_SENDER = "-105";


	public final static int MAX_SENDER_LENGTH_ALPHANUMERIC = 11;
	public final static int MAX_SENDER_LENGTH_NUMERIC = 21;
	public final static int MAX_BODY_LENGTH = 612;
	
	
	
	
	//---------- Public properties
	public String getUrlGetCredit()
	{ return URL_GET_CREDIT; }

	public String getUrlSendSms()
	{ return URL_SEND_SMS; }

	
	
	
	//---------- Public methods

	
	
	
	//---------- Private methods

}
