package it.rainbowbreeze.smsforfree.services;


/**
 * One of the JackSMS template configured by the user
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class JacksmsUserService {
	//---------- Ctors
	
	/**
	 * 
	 */
	public JacksmsUserService(
			int id,
			int serviceId,
			String username,
			String password,
			String freeField1,
			String freeField2
	) {
		setId(id);
		setServiceId(serviceId);
		setUsername(username);
		setPassword(password);
		setFreeField1(freeField1);
		setFreeField2(freeField2);
	}



	//---------- Private fields




	//---------- Public properties
	private int mId;
	public int getId()
	{ return mId; }
	public void setId(int value)
	{ mId = value; }

	private int mServiceId;
	public int getServiceId()
	{ return mServiceId; }
	public void setServiceId(int value)
	{ mServiceId = value; }

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




	//---------- Public methods




	//---------- Private methods
}
