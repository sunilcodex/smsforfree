package it.rainbowbreeze.smsforfree.services;

public class JacksmsUserService {
	//---------- Ctors
	



	//---------- Private fields




	//---------- Public properties
	private String[] mParameters = new String[4];
	public String[] getParameters()
	{ return mParameters; }

	public String getUsername()
	{ return mParameters[0]; }
	public void setUsername(String value)
	{ mParameters[0] = value; }

	public String getPassword()
	{ return mParameters[1]; }
	public void setPassword(String value)
	{ mParameters[1] = value; }

	public String getFreeField1()
	{ return mParameters[2]; }
	public void setFreeField1(String value)
	{ mParameters[2] = value; }

	public String getFreeField2()
	{ return mParameters[3]; }
	public void setFreeField2(String value)
	{ mParameters[3] = value; }

	private int mId;
	public int getId()
	{ return mId; }
	public void setId(int value)
	{ mId = value; }




	//---------- Public methods




	//---------- Private methods
}
