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

package it.rainbowbreeze.smsforfree.util;

import android.text.TextUtils;

public class ParserUtils {
	//---------- Ctors

	//---------- Private fields

	//---------- Public properties

	//---------- Public methods
	public static String getStringBetween(String source, String tokenBefore, String tokenAfter)
	{
		String result = "";
		
		if (TextUtils.isEmpty(source) || TextUtils.isEmpty(tokenBefore))
			return result;
		
		int posInit = source.indexOf(tokenBefore);
		if (-1 == posInit) return result;
		posInit = posInit += tokenBefore.length();
		
		int posEnd;
		if (TextUtils.isEmpty(tokenAfter)) {
			posEnd = source.length();
		} else {
			posEnd = source.indexOf(tokenAfter, posInit);
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
	
	
	/**
	 * Hides the last digits of the telephone number
	 * @param numberToScramble
	 * @return
	 */
	public static String scrambleNumber(String numberToScramble)
	{
		if (TextUtils.isEmpty(numberToScramble) || numberToScramble.length() < 3) {
			return numberToScramble;
		} else {
			return numberToScramble.substring(0, numberToScramble.length() - 3) + "***";
		}
	}
	

	//---------- Private methods

}
