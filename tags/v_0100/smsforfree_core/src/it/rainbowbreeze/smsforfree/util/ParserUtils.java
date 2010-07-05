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
	

	//---------- Private methods

}
