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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

/**
 * Logging facility.
 * 
 * Use the logcat command in order to collect log application
 * 
 * Thank to Xtralogic, Inc. and the android-log-collector project
 * for the inspiration
 *  http://code.google.com/p/android-log-collector/
 *
 * 
 * Usage: logcat [options] [filterspecs]
        options include:
          -s              Set default filter to silent.
                          Like specifying filterspec '*:s'
          -f <filename>   Log to file. Default to stdout
          -r [<kbytes>]   Rotate log every kbytes. (16 if unspecified). Requires -f
          -n <count>      Sets max number of rotated logs to <count>, default 4
          -v <format>     Sets the log print format, where <format> is one of:

                          brief process tag thread raw time threadtime long

          -c              clear (flush) the entire log and exit
          -d              dump the log and then exit (don't block)
          -g              get the size of the log's ring buffer and exit
          -b <buffer>     request alternate ring buffer
                          ('main' (default), 'radio', 'events')
          -B              output the log in binary
        filterspecs are a series of
          <tag>[:priority]

        where <tag> is a log component tag (or * for all) and priority is:
          V    Verbose
          D    Debug
          I    Info
          W    Warn
          E    Error
          F    Fatal
          S    Silent (supress all output)

        '*' means '*:d' and <tag> by itself means <tag>:v

        If not specified on the commandline, filterspec is set from ANDROID_LOG_TAGS.
        If no filterspec is found, filter defaults to '*:I'

        If not specified with -v, format is set from ANDROID_PRINTF_LOG
        or defaults to "brief"
 *
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class LogFacility {
	//---------- Private fields
	private final static String LINE_SEPARATOR =
		System.getProperty("line.separator");		

	/** Default log tag */
	private static String mTag = "SmsForFree";

	
	
	
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
	
	
	public static void e(String tag, Exception e)
	{ log(Log.ERROR, tag, e.getMessage()); }

	public static void e(String tag, String message)
	{ log(Log.ERROR, tag, message); }

	public static void i(String tag, String message)
	{ log(Log.INFO, tag, message); }

	public static void v(String tag, String message)
	{ log(Log.VERBOSE, tag, message); }
	

	public static void init(String tag) {
		mTag = tag;
	}

	
	/**
	 * Reset the log
	 * @return
	 */
	public static ResultOperation<Void> reset() {
        try{
        	//dump the log and order by tag and by time
            ArrayList<String> commandLine = new ArrayList<String>();
            commandLine.add("logcat");
            commandLine.add("-c");
            
            //execute the command
            Runtime.getRuntime().exec(commandLine.toArray(new String[0]));
        } 
        catch (IOException e){
        	return new ResultOperation<Void>(e, ResultOperation.RETURNCODE_ERROR_GENERIC);
        } 

        return new ResultOperation<Void>();
	}

	/**
	 * Return only logs that belong to the specified tags
	 * 
	 * @param tagFilters array of string with tags to include. null to include all
	 * @return
	 */
	public static ResultOperation<String> getLogData(String[] tagFilters) {
        final StringBuilder log = new StringBuilder();
        try{
        	//dump the log and order by tag and by time
            ArrayList<String> commandLine = new ArrayList<String>();
            commandLine.add("logcat");
            commandLine.add("-d");
            commandLine.add("-v");
            commandLine.add("tag");
            commandLine.add("-v");
            commandLine.add("time");
            
            //execute the command
            Process process = Runtime.getRuntime().exec(commandLine.toArray(new String[0]));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String line;
            boolean includeLine;
            while ((line = bufferedReader.readLine()) != null){
            	//apply filters
                if (null == tagFilters) {
                	includeLine = true;
                } else {
                	includeLine = false;
                	//for each line
                	for(String tag : tagFilters) {
                		//refine tag string
	                    String tagToFind = "/" + tag;
	                	if (line.contains(tagToFind)) {
	                		includeLine = true;
	                		break;
	                	}
                	}
                }
            	//get logs lines
            	if (includeLine) {
	                log.append(line);
	                log.append(LINE_SEPARATOR);
            	}
            }
        } 
        catch (IOException e){
        	return new ResultOperation<String>(e, ResultOperation.RETURNCODE_ERROR_GENERIC);
        } 

        return new ResultOperation<String>(log.toString());
	}

	/**
	 * Return all the log content
	 * @return
	 */
	public static ResultOperation<String> getLogData() {
		return getLogData(null);
	}

	/**
	 * Return the log content related to the application
	 * @return
	 */
	public static ResultOperation<String> getApplicationLogData() {
		return getLogData(new String[]{ mTag });
	}




	//---------- Private methods
	private static void log(int level, String msg)
	{ log(level, mTag, msg); }

	private static void log(int level, String tag, String msg)
	{
		switch (level){

		case Log.ERROR:
			Log.e(tag, msg);
			break;

		case Log.INFO:
			Log.i(tag, msg);
			break;

		case Log.VERBOSE:
			Log.v(tag, msg);
			break;
		}
	}

}
