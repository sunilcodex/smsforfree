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
