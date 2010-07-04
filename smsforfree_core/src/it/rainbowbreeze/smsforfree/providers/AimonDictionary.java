package it.rainbowbreeze.smsforfree.providers;


public class AimonDictionary
{
	//---------- Ctors

	
	
	
	//---------- Private fields
	private static final String RESULT_FREE_ERROR_INVALID_CREDENTIALS = "Nome utente e/o password non riconosciute";
	private static final String RESULT_FREE_ERROR_NOT_ENOUGH_CREDIT = "Credito non sufficiente per spedire altri messaggi";
	private static final String RESULT_FREE_ERROR_INVALID_SENDER = "Mittente non valido";
	private static final String RESULT_FREE_ERROR_INVALID_DESTINATION = "Il destinatario deve essere un numero di telefono" ;
	private static final String RESULT_FREE_ERROR_EMPTY_DESTINATION = "Destinatario richiesto";
	private static final String RESULT_FREE_ERROR_EMPTY_BODY = "Testo richiesto";
	private static final String RESULT_FREE_ERROR_UNSUPPORTED_ENCODING = "Carattere GSM non supportato";
	private static final String RESULT_FREE_ERROR_GENERIC_SERVER_ERROR = "Messaggio non inviato per errore di spedizione";
	private static final String RESULT_FREE_ERROR_DAILY_LIMIT_REACHED = "limite massimo di sms inviabili gratis in 24 ore";
	private static final String RESULT_FREE_ERROR_MONTHLY_LIMIT_REACHED = "limite massimo di sms inviabili gratis in 30 giorni";
	private static final String RESULT_FREE_WELCOME_MESSAGE_FOR_THE_USER = "Ciao <b>%s</b>!";
	private static final String RESULT_FREE_SENT_OK = "Messaggio inviato con successo";

	private static final String SEARCH_CREDITI_SMS_START = "Credito residuo giornaliero: ";
	private static final String SEARCH_CREDITI_SMS_END = "crediti/sms";

	
	

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
	
	private static final String RESULT_SENDSMS_OK = "+01 SMS Queued";
	private static final String RESULT_ERRORCODE_ACCESS_DENIED = "-3-";
	private static final String RESULT_ERRORCODE_MISSING_PARAMETERS = "-5-";
	private static final String RESULT_ERRORCODE_INTERNAL_SERVER_ERROR = "-32-";
	private static final String RESULT_ERRORCODE_INVALID_DESTINATION = "-100-";
	private static final String RESULT_ERRORCODE_DESTINATION_NOT_ALLOWED = "-101-";
	private static final String RESULT_ERRORCODE_BODY_HAS_INVALID_CHARS_OR_TOO_LONG = "-102-";
	private static final String RESULT_ERRORCODE_NOT_ENOUGH_CREDIT = "-103-";
	private static final String RESULT_ERRORCODE_INVALID_SENDER = "-105-";
	
	public static final String FIELD_FREE_INPUT_USERNAME = "inputUsername";
	public static final String FIELD_FREE_INPUT_PASSWORD = "inputPassword";
	public static final String FIELD_FREE_SUBMIT_BUTTON = "submit";
	public static final String FIELD_FREE_DESTINATION = "destinatario";
	public static final String FIELD_FREE_MESSAGE_LENGTH = "caratteri";
	public static final String FIELD_FREE_MESSAGE = "testo";
	public static final String FIELD_FREE_SENDER = "mittente";
	public static final String FIELD_FREE_INTERNATIONAL_PREFIX = "prefisso_internazionale";
	public static final String FIELD_FREE_SENDER_TYPE = "tipomittente";
	public static final String FIELD_FREE_SMS_TYPE = "tiposms";
	public static final String FIELD_FREE_SUBMIT_BUTTON2 = "btnSubmit";

	public final static int MAX_SENDER_LENGTH_ALPHANUMERIC = 11;
	public final static int MAX_SENDER_LENGTH_NUMERIC = 21;
	public final static int MAX_BODY_LENGTH = 612;
	
	public static final String ID_API_FREE_ANONYMOUS_SENDER = "0";
	public static final String ID_API_FREE_NORMAL = "1";
	public static final String ID_API_ANONYMOUS_SENDER = "106";
	public static final String ID_API_SELECTED_SENDER_NO_REPORT = "59";
	public static final String ID_API_SELECTED_SENDER_REPORT = "84";

	
	
	
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
		return message.contains(RESULT_FREE_ERROR_INVALID_CREDENTIALS);
	}
	
	/**
	 * Checks if the send for free sms correctly sent the sms
	 */
	public boolean isFreeSmsCorrectlySent(String message)
	{ return message.contains(RESULT_FREE_SENT_OK); }
	
	public boolean isFreeSmsNotEnoughCredit(String message)
	{ return message.contains(RESULT_FREE_ERROR_NOT_ENOUGH_CREDIT); }
	
	public boolean isFreeSmsInvalidSender(String message)
	{ return message.contains(RESULT_FREE_ERROR_INVALID_SENDER); }
	
	public boolean isFreeSmsInvalidDestination(String message)
	{ 
		return message.contains(RESULT_FREE_ERROR_INVALID_DESTINATION) || 
			message.contains(RESULT_FREE_ERROR_EMPTY_DESTINATION);
	}
	
	public boolean isFreeSmsEmptyBody(String message)
	{ return message.contains(RESULT_FREE_ERROR_EMPTY_BODY); }
	
	public boolean isFreeSmsUnsupportedMessageEncoding(String message)
	{ return message.contains(RESULT_FREE_ERROR_UNSUPPORTED_ENCODING); }
	
	public boolean isFreeSmsGenericServerError(String message)
	{ return message.contains(RESULT_FREE_ERROR_GENERIC_SERVER_ERROR); }
	
	public boolean isFreeSmsDailyLimitReached(String message)
	{ return message.contains(RESULT_FREE_ERROR_DAILY_LIMIT_REACHED); }
	
	public boolean isFreeSmsMonthlyLimitReached(String message)
	{ return message.contains(RESULT_FREE_ERROR_MONTHLY_LIMIT_REACHED); }
	
	
	public boolean isLoginInvalidCredentials(String webserviceReply)
	{ return webserviceReply.startsWith(AimonDictionary.RESULT_ERRORCODE_ACCESS_DENIED); }

	public boolean isInternalServerError(String webserviceReply)
	{ return webserviceReply.startsWith(AimonDictionary.RESULT_ERRORCODE_INTERNAL_SERVER_ERROR); }
	
	public boolean isMissingParameters(String webserviceReply)
	{ return webserviceReply.startsWith(AimonDictionary.RESULT_ERRORCODE_MISSING_PARAMETERS); }

	public boolean isInvalidDestination(String webserviceReply)
	{
		return webserviceReply.startsWith(AimonDictionary.RESULT_ERRORCODE_INVALID_DESTINATION) ||
			webserviceReply.startsWith(AimonDictionary.RESULT_ERRORCODE_DESTINATION_NOT_ALLOWED);
	}

	public boolean isNotEnoughCredit(String webserviceReply)
	{ return webserviceReply.startsWith(AimonDictionary.RESULT_ERRORCODE_NOT_ENOUGH_CREDIT); }
	
	public boolean isInvalidSender(String webserviceReply)
	{ return webserviceReply.startsWith(AimonDictionary.RESULT_ERRORCODE_INVALID_SENDER); }

	public boolean isUnsupportedMessageEncodingOrTooLong(String webserviceReply)
	{ return webserviceReply.startsWith(AimonDictionary.RESULT_ERRORCODE_BODY_HAS_INVALID_CHARS_OR_TOO_LONG); }

	/**
	 * Checks if Aimon webservice return message indicates that
	 * the request operation was correctly executed
	 * 
	 * @param webserviceReply
	 * @return
	 */
	public boolean isOperationCorrectlyExecuted(String webserviceReply)
	{
		if (isSmsCorrectlySent(webserviceReply)) return true;
		
		//generally, a wrong operation result starts with the - char
		return !webserviceReply.startsWith("-");
	}
	
	/**
	 * Extract from the output of the html pages for sending free sms the remaining credits
	 * What i need is in the string
	 *   Credito residuo giornaliero: 3 crediti/sms
	 *   
	 * @param message
	 * @return
	 */
	public String findRemainingCreditsForFreeSms(String message) {
		int intBeginPos = message.indexOf(SEARCH_CREDITI_SMS_START);
		if (-1 == intBeginPos) return "0";
		
		int intEndPos = message.indexOf(SEARCH_CREDITI_SMS_END, intBeginPos);
		if (-1 == intEndPos) return "0";
		
		return message.substring(intBeginPos + SEARCH_CREDITI_SMS_START.length(), intEndPos).trim();
	}

	
	
	
	//---------- Private methods
	/**
	 * Checks if sms was sent without errors
	 * @param webserviceReply
	 * @return
	 */
	private boolean isSmsCorrectlySent(String webserviceReply) {
		if (null == webserviceReply ) return false;
		return webserviceReply.startsWith(RESULT_SENDSMS_OK);
	}


}
