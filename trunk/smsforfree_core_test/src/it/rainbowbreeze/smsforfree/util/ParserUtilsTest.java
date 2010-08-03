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
	
	
	public void testJoin() {
		String[] startingArray = new String[] { "Hi", "i'm", "Rainbowbreeze", ",", "who", "are", "you", "?" };
		
		String joinString = ParserUtils.join(startingArray, "##");
		String [] endingArray = joinString.split("##");
		
		assertEquals("Wrong size of returning array", startingArray.length, endingArray.length);
		for (int i = 0; i < startingArray.length; i++) {
			assertEquals("Wrong returning element at position " + i, startingArray[i], endingArray[i]);
		}
	}
	
	
	public void testGetStringBetween() {
		assertEquals("Wrong string", "rainbowbreeze", ParserUtils.getStringBetween("last rainbowbreeze commit", "last ", " commit"));
		assertEquals("Wrong string", "let me know", ParserUtils.getStringBetween("why don't you let me know?", "you ", "?"));
		assertEquals("Wrong string", "", ParserUtils.getStringBetween("why don't you let me know?", "", "?"));
		assertEquals("Wrong string", "don't you let me know?", ParserUtils.getStringBetween("why don't you let me know?", "why ", ""));
		assertEquals("Wrong string", "", ParserUtils.getStringBetween("why don't you let me know?", "not found", "?"));
	}
	
	public void testGetInvariableStringFinalBoundary() {
		assertEquals("Wrong string", "let me know ", ParserUtils.getInvariableStringFinalBoundary("let me know %s the fish"));
		assertEquals("Wrong string", "let me know ", ParserUtils.getInvariableStringFinalBoundary("let me know %s"));
		assertEquals("Wrong string", "", ParserUtils.getInvariableStringFinalBoundary("%s is the right key!"));
	}




	//---------- Private methods

}
