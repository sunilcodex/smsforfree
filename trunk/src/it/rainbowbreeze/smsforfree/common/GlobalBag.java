package it.rainbowbreeze.smsforfree.common;

import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import java.util.List;

public class GlobalBag {
	//---------- Public properties
	
	//code of current session
	public static  String jacksms_jcode;
	public static String jacksms_sentmessage;
	public static boolean jacksms_logged;
	
	public static List<SmsProvider> providerList;


}
