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

import android.test.AndroidTestCase;
import android.text.TextUtils;

public class LogFacilityTest
	extends AndroidTestCase
{
	//---------- Private fields
	private final static String LINE_SEPARATOR = System.getProperty("line.separator");




	//---------- Constructor




	//---------- SetUp and TearDown
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mContext = getContext();
		
		LogFacility.init("SmsForFreeCore-Test");
	}




	//---------- Tests methods
	
	/**
	 * Test if the reset of log works
	 */
	public void testLogFacilityReset(){
		String content;
		
		//fill log with data
		fillLogWithSpareData();
		//verify that in the log some data is present
		content = getLogContent(LogFacility.getApplicationLogData());
		assertFalse("Log is empty!", TextUtils.isEmpty(content));
		
		//reset the log
		ResultOperation<Void> res = LogFacility.reset();
		assertFalse("Wrong result operation", res.hasErrors());
		content = getLogContent(LogFacility.getApplicationLogData());
		assertTrue("Log is not empty!", TextUtils.isEmpty(content));
	}

	/**
	 * @throws InterruptedException 
	 */
	public void testLogDataInsertion() {
		fillLogWithSpareData();
		fillLogWithSpareData();

		String content = getLogContent(LogFacility.getApplicationLogData());
		compareLogContent(getLogSpareDataRepresentation(), content);
	}
	
	/**
	 * @throws InterruptedException 
	 */
	public void testAllLogDataRetrieve() {
		LogFacility.reset();
		String content;
		
		//check that log has data
		LogFacility.i("TESTLOG", "starting log test");
		content = getLogContent(LogFacility.getLogData());
		assertFalse("log is empty", TextUtils.isEmpty(content));
		
		String logBefore = getLogContent(LogFacility.getLogData());
		//add some log
		fillLogWithSpareData();
		content = getLogContent(LogFacility.getApplicationLogData());
		assertFalse("Logs are equal", content.equals(logBefore));
		
		LogFacility.e("TESTTAG", "test log");
		content = getLogContent(LogFacility.getLogData(new String[]{ "TESTTAG"}));
		compareLogContent(
				content,
				"07-11 00:44:10.466 E/TESTTAG ( 2114): test log");
	}



	//---------- Private methods
	
	/**
	 * Log some spare data
	 */
	private void fillLogWithSpareData() {
		//write some data inside logger
		LogFacility.e("An error message");
		LogFacility.v("A verbose message");
		LogFacility.i("An info message");
	}
	
	/**
	 * 
	 * @return
	 */
	private String getLogSpareDataRepresentation() {
		return
			"07-11 00:44:10.466 E/SmsForFreeCore-Test( 2114): An error message" +
			LINE_SEPARATOR +
			"07-11 00:44:10.466 V/SmsForFreeCore-Test( 2114): A verbose message" + 
			LINE_SEPARATOR  + 
			"07-11 00:44:10.466 I/SmsForFreeCore-Test( 2114): An info message" + 
			LINE_SEPARATOR;
	}
	
	/**
	 * Compare log content, only the last lines of result with the
	 * expected lines
	 * 
	 * @param expected
	 * @param result
	 * @return
	 */
	private void compareLogContent(String expected, String result){
		//ok, both are empty
		if (TextUtils.isEmpty(expected) && TextUtils.isEmpty(result))
			return;
		
		String[] expectedLines = expected.split(LINE_SEPARATOR );
		String[] resultLines = result.split(LINE_SEPARATOR );
		
		//compares line by line the logs, starting from the end
		//and for the lines of the expected result
		for(int i = expectedLines.length -1; i >= 0 ; i--) {
			String expCleanLine = cleanLogFromDateTime(expectedLines[i]);
			String resCleanLine = cleanLogFromDateTime(resultLines[i]);
			assertEquals("Log data is different", expCleanLine, resCleanLine);
		}
	}

	/**
	 * Remove date and time and the progressive number
	 * from the log tag.
	 * Input: 07-11 00:44:10.466 E/SmsForFreeCore-Test( 2114): An error message
	 * output: E/SmsForFreeCore-Test(*****): An error message
	 * @param string
	 * @return
	 */
	private String cleanLogFromDateTime(String logLine) {
		//remove progressive number after the logtag
		int intEndPos = logLine.indexOf("): ");
		String before = logLine.substring(0, intEndPos - 5);
		String after = logLine.substring(intEndPos);
		String newLine = before + "*****" + after;
		//remove date and time info
		return newLine.substring(19);
	}

	
	/**
	 * Extracts the log from the {@link ResultOperation}
	 * @param logData
	 * @return
	 */
	private String getLogContent(ResultOperation<String> logData) {
		assertFalse("Wrong return code", logData.hasErrors());
		return logData.getResult();
	}
}
