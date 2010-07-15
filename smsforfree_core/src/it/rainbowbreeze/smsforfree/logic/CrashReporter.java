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

package it.rainbowbreeze.smsforfree.logic;

import it.rainbowbreeze.smsforfree.common.ResultOperation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;

/**
 * Collect application crash information
 * 
 * Inspiration from this posts
 *  http://androidblogger.blogspot.com/2009/12/how-to-improve-your-application-crash.html
 *  http://androidblogger.blogspot.com/2010/03/crash-reporter-for-android-slight.html
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class CrashReporter implements Thread.UncaughtExceptionHandler
{
	//---------- Private fields
	public final static String LINE_SEPARATOR = System.getProperty("line.separator");	

	HashMap<String, String> mCustomParameters = new HashMap< String, String>();
	private Thread.UncaughtExceptionHandler mPreviousHandler;
	private Context mContext;

	private final static int MAX_REPORTS_TO_COLLECT = 5;

	
	
	
	//---------- Ctors

	
	

	//---------- Public properties
	private static CrashReporter mInstance;
	public static CrashReporter instance()
	{
		if ( mInstance == null )
			mInstance = new CrashReporter();
		return mInstance;
	}


	
	
	
	//---------- Public methods
	public void init(Context context)
	{
		mPreviousHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
		mContext = context;
	}

	public void addCustomData( String Key, String Value )
	{
		mCustomParameters.put( Key, Value );
	}

	private String createCustomInfoString()
	{
		StringBuilder customInfo = new StringBuilder();
		Iterator<String> iterator = mCustomParameters.keySet().iterator();
		while (iterator.hasNext()) {
			String currentKey = iterator.next();
			String currentVal = mCustomParameters.get(currentKey);
			customInfo.append(currentKey)
				.append(" = ")
				.append(currentVal)
				.append(LINE_SEPARATOR);
		}
		return customInfo.toString();
	}

	
	/**
	 * Called when an exception occurs
	 */
	public void uncaughtException(Thread t, Throwable e)
	{
		//get the error stack trace
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		String stacktrace = result.toString();

		//create the report
		StringBuilder report = new StringBuilder();
		report.append("Error Report collected on: ")
			.append(getCurrentDayHash())
			.append(" - ")
			.append(getCurrentTimeHash())
			.append(LINE_SEPARATOR)
			.append(LINE_SEPARATOR)
			.append("Informations :")
			.append(LINE_SEPARATOR)
			.append("==============")
			.append(LINE_SEPARATOR)
			.append(LINE_SEPARATOR)
			.append(createInformationString())
			.append("Custom Informations :")
			.append(LINE_SEPARATOR)
			.append("=====================")
			.append(LINE_SEPARATOR)
			.append(createCustomInfoString())
			.append(LINE_SEPARATOR)
			.append(LINE_SEPARATOR)
			.append("Stack:")
			.append(LINE_SEPARATOR)
			.append("=======")
			.append(LINE_SEPARATOR)
			.append(stacktrace)
			.append(LINE_SEPARATOR)
			.append("Cause:")
			.append(LINE_SEPARATOR)
			.append("=======")
			.append(LINE_SEPARATOR);
		
		// If the exception was thrown in a background thread inside
		// AsyncTask, then the actual exception can be found with getCause
		Throwable cause = e.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			report.append(result.toString());
			cause = cause.getCause();
		}
		printWriter.close();
		report.append("****  End of current crash report ***");
		saveAsFile(report.toString());
		
		//call previous handler
		mPreviousHandler.uncaughtException(t, e);
	}

	
	/**
	 * Find if previous crash reports are present
	 * @return
	 */
	public boolean isCrashReportPresent(Context context) {
		try {
			String baseFilePath = getBaseFilePath(context);
			String[] errorFilesList = getErrorFilesList(baseFilePath);
			return errorFilesList.length > 0;
			
		} catch (Exception e){
			return false;
		}
	}
	
	/**
	 * Checks if there are previous stack trace files 
	 * @param context
	 */
	public ResultOperation<String> getPreviousCrashReports(Context context)
	{
		
		try {
			String baseFilePath = getBaseFilePath(context);
			String[] errorFilesList = getErrorFilesList(baseFilePath);
			
			int curIndex = 0;
			StringBuffer wholeErrorText = new StringBuffer();

			for (String errorFile : errorFilesList) {
				if (curIndex++ <= MAX_REPORTS_TO_COLLECT) {
					wholeErrorText.append("New Trace collected:")
							.append(LINE_SEPARATOR)
							.append("=====================")
							.append(LINE_SEPARATOR);

					String filePath = baseFilePath + File.pathSeparator + errorFile;
					
					//read file content
					BufferedReader input =  new BufferedReader(new FileReader(filePath));
					String line;
					while ((line = input.readLine()) != null) {
						wholeErrorText.append(line)
								.append(LINE_SEPARATOR);
					}
					input.close();
				}

				//delete all reports files
				File curFile = new File(baseFilePath + File.pathSeparator + errorFile);
				curFile.delete();
			}
			wholeErrorText.append(LINE_SEPARATOR);
			return new ResultOperation<String>(wholeErrorText.toString());
			
		} catch(Exception e) {
			return new ResultOperation<String>(e, ResultOperation.RETURNCODE_ERROR_GENERIC);
		}
	}

	
	
	//---------- Private methods
	
	/**
	 * Gathers information from the system and creates a report with them
	 */
	private String createInformationString()
	{
		String versionName = null;
		String packageName = null;
		String phoneModel = null;
		String androidVersion = null;
		String board = null;
		String brand = null;
		String device = null;
		String display = null;
		String fingerPrint = null;
		String host = null;
		String id = null;
		String model = null;
		String product = null;
		String tags = null;
		long time = 0;
		String type = null;
		String user = null;
		
		try {
			PackageManager pm = mContext.getPackageManager();
			PackageInfo pi;
			// Version
			pi = pm.getPackageInfo(mContext.getPackageName(), 0);
			versionName = pi.versionName;
			// Package name
			packageName = pi.packageName;
			// Device model
			phoneModel = android.os.Build.MODEL;
			// Android version
			androidVersion = android.os.Build.VERSION.RELEASE;
			board = android.os.Build.BOARD;
			brand = android.os.Build.BRAND;
			device = android.os.Build.DEVICE;
			display = android.os.Build.DISPLAY;
			fingerPrint = android.os.Build.FINGERPRINT;
			host = android.os.Build.HOST;
			id = android.os.Build.ID;
			model = android.os.Build.MODEL;
			product = android.os.Build.PRODUCT;
			tags = android.os.Build.TAGS;
			time = android.os.Build.TIME;
			type = android.os.Build.TYPE;
			user = android.os.Build.USER;

		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		StringBuffer returnMsg = new StringBuffer();
		returnMsg.append("Version : " + versionName)
			.append(LINE_SEPARATOR)
			.append("Package : " + packageName)
			.append(LINE_SEPARATOR)
			.append("FilePath : " + getBaseFilePath(mContext))
			.append(LINE_SEPARATOR)
			.append("Phone Model" + phoneModel)
			.append(LINE_SEPARATOR)
			.append("Android Version : " + androidVersion)
			.append(LINE_SEPARATOR)
			.append("Board : " + board)
			.append(LINE_SEPARATOR)
			.append("Brand : " + brand)
			.append(LINE_SEPARATOR)
			.append("Device : " + device)
			.append(LINE_SEPARATOR)
			.append("Display : " + display)
			.append(LINE_SEPARATOR)
			.append("Finger Print : " + fingerPrint)
			.append(LINE_SEPARATOR)
			.append("Host : " + host)
			.append(LINE_SEPARATOR)
			.append("ID : " + id)
			.append(LINE_SEPARATOR)
			.append("Model : " + model)
			.append(LINE_SEPARATOR)
			.append("Product : " + product)
			.append(LINE_SEPARATOR)
			.append("Tags : " + tags)
			.append(LINE_SEPARATOR)
			.append("Time : " + time)
			.append(LINE_SEPARATOR)
			.append("Type : " + type)
			.append(LINE_SEPARATOR)
			.append("User : " + user)
			.append(LINE_SEPARATOR)
			.append("Total Internal memory: " + getTotalInternalMemorySize())
			.append(LINE_SEPARATOR)
			.append("Available Internal memory: " + getAvailableInternalMemorySize())
			.append(LINE_SEPARATOR);
		
		return returnMsg.toString();
	}


	private long getAvailableInternalMemorySize()
	{
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}
     
	private long getTotalInternalMemorySize()
	{
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	/**
	 * Save the string inside a private file
	 * @param ErrorContent
	 */
	private void saveAsFile(String ErrorContent) {
		try {
			Random generator = new Random();
			int random = generator.nextInt(99999);
			String fileName = "stack-" + random + ".stacktrace";
			FileOutputStream trace = mContext.openFileOutput( fileName, Context.MODE_PRIVATE);
			trace.write(ErrorContent.getBytes());
			trace.close();
		} catch( Exception e ) {
			// ...
		}
	}

	/**
	 * 
	 * @param baseSearchPath
	 * @return
	 */
	private String[] getErrorFilesList(String baseSearchPath)
	{
		File dir = new File(baseSearchPath + File.pathSeparator);
		// Try to create the files folder if it doesn't exist
		dir.mkdir();
		// Filter for ".stacktrace" files
		FilenameFilter filter = new FilenameFilter() {
		         public boolean accept(File dir, String name) {
		                 return name.endsWith(".stacktrace");
		                 }
		         };
         return dir.list(filter);
	}

	/**
	 * Return base path for private storage
	 * @return
	 */
	private String getBaseFilePath(Context context) {
		return context.getFilesDir().getAbsolutePath();
	}


	/**
	 * Returns current date in format YYYY-MM-DD
	 * 
	 * @return
	 */
	private String getCurrentDayHash()
	{
        final Calendar c = Calendar.getInstance();
        StringBuilder dateHash = new StringBuilder();
        dateHash.append(c.get(Calendar.YEAR))
        	.append("-")
        	.append(c.get(Calendar.MONTH))
        	.append("-")
        	.append(c.get(Calendar.DAY_OF_MONTH));
        return dateHash.toString();
	}

	/**
	 * Returns current time in format HH:MM:SS
	 * 
	 * @return
	 */
	private String getCurrentTimeHash()
	{
        final Calendar c = Calendar.getInstance();
        StringBuilder dateHash = new StringBuilder();
        dateHash.append(c.get(Calendar.HOUR_OF_DAY))
        	.append(":")
        	.append(c.get(Calendar.MINUTE))
        	.append(":")
        	.append(c.get(Calendar.SECOND));
        return dateHash.toString();
	}
}



	

