package it.rainbowbreeze.smsforfree.domain;

public class ContactPhone {
	//---------- Ctors
	public ContactPhone()
	{}
	
	public ContactPhone(String type, String number) {
		setType(type);
		setNumber(number);
	}
	
	
	
	
	//---------- Private fields

	
	
	
	//---------- Public properties
	private String mType;
	public String getType()
	{ return mType; }
	public void setType(String value)
	{ mType = value; }

	private String mNumber;
	public String getNumber()
	{ return mNumber; }
	public void setNumber(String value)
	{ mNumber = value; }


	
	
	//---------- Public methods

	
	
	
	
	//---------- Private methods

}
