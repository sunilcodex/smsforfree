package it.rainbowbreeze.smsforfree.util;

import junit.framework.TestCase;

public class GlobalUtilsTest extends TestCase {
	//---------- Private fields

	//---------- Constructor

	//---------- SetUp and TearDown

	//---------- Tests methods
	
	public void testResizeArray() {
		
		String[] array = new String[]{ "greetings", "from", "me" };
		array = (String[]) GlobalUtils.resizeArray(array, 2);
		assertEquals("Wrong array size", new Integer(2), new Integer(array.length));
		assertEquals("Wrong array element", "greetings", array[0]);
		assertEquals("Wrong array element", "from", array[1]);
	}

	//---------- Private methods

}
