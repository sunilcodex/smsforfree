package it.rainbowbreeze.smsforfree.util;

import android.test.AndroidTestCase;

public class ParserUtilsTest extends AndroidTestCase {
	//---------- Private fields




	//---------- Constructor




	//---------- SetUp and TearDown




	//---------- Tests methods
	public void testScrambleNumber() {
		assertEquals("Wrong scambling procedure", "12345***", ParserUtils.scrambleNumber("12345678"));
	}




	//---------- Private methods

}
