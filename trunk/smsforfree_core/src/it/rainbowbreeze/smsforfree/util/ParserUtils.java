package it.rainbowbreeze.smsforfree.util;

import android.text.TextUtils;

public class ParserUtils {
	//---------- Ctors

	//---------- Private fields

	//---------- Public properties

	//---------- Public methods
	public static String getStringBetween(String source, String paramValue, String paramsSeparator)
	{
		String result = "";
		
		if (TextUtils.isEmpty(source) || TextUtils.isEmpty(paramValue))
			return result;
		
		int posInit = source.indexOf(paramValue);
		if (-1 == posInit) return result;
		posInit = posInit += paramValue.length();
		
		int posEnd;
		if (TextUtils.isEmpty(paramsSeparator)) {
			posEnd = source.length();
		} else {
			posEnd = source.indexOf(paramsSeparator, posInit);
			if (-1 == posEnd) posEnd = source.length(); 
		}
		
		result = source.substring(posInit, posEnd);
		
		return result;
	}

	
	/**
	 * Return the latest char of the string before the variable part
	 * @param stringToCheck
	 * @return
	 */
	
	public static String getInvariableStringFinalBoundary(String stringToCheck)
	{
		if (TextUtils.isEmpty(stringToCheck)) return stringToCheck;
		//ok, i know it isn't the best way, but it works as a workaround
		//for the presence of %s parameter in the source message string ;)
		int pos = stringToCheck.indexOf("%");
		return (pos >= 0) ? stringToCheck.substring(0, pos) : stringToCheck;
	}
	

	//---------- Private methods

}
