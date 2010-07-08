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

package it.rainbowbreeze.smsforfree.common;

import android.util.Log;

/**
 * Logging facility 
 *
 * @author Alfredo "Rainbowbreeze" Morresi
 */

//TODO
//print stack trace in error
public class LogFacility {
	//---------- Private fields
	protected static final String LOG_TAG = "SmsForFree";

	
	
	
	//---------- Public properties



	
	//---------- Public methods
	public static void e(Exception e)
	{ log(Log.ERROR, e.getMessage()); }

	public static void e(String message)
	{ log(Log.ERROR, message); }

	public static void i(String message)
	{ log(Log.INFO, message); }

	public static void v(String message)
	{ log(Log.VERBOSE, message); }




	//---------- Private methods
	protected static void log(int level, String msg)
	{
		switch (level){

		case Log.ERROR:
			Log.e(LOG_TAG, msg);
			break;

		case Log.INFO:
			Log.i(LOG_TAG, msg);
			break;

		case Log.VERBOSE:
			Log.v(LOG_TAG, msg);
			break;
		}
	}

}
