package it.rainbowbreeze.smsforfree.services;


/**
 * One of the JackSMS template configured by the user
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class JacksmsTemplateService {
	//---------- Ctors
	
	/**
	 * 
	 * @param id
	 * @param name
	 * @param version
	 * @param singleLen
	 * @param maxLen
	 */
	public JacksmsTemplateService(
			int id,
			String name,
			String version,
			int singleLen,
			int maxLen
	) {
		setId(id);
		setName(name);
		setVersion(version);
		setSingleLen(singleLen);
		setMaxLen(maxLen);
	}



	//---------- Private fields




	//---------- Public properties
	private int mId;
	public int getId()
	{ return mId; }
	public void setId(int value)
	{ mId = value; }

	private String mName;
	public String getName()
	{ return mName; }
	public void setName(String value)
	{ mName = value; }

	private int mSingleLen;
	public int getSingleLen()
	{ return mSingleLen; }
	public void setSingleLen(int value)
	{ mSingleLen = value; }

	private int mMaxLen;
	public int getMaxLen()
	{ return mMaxLen; }
	public void setMaxLen(int value)
	{ mMaxLen = value; }

	private String mVersion;
	public String getVersion()
	{ return mVersion; }
	public void setVersion(String value)
	{ mVersion = value; }

	private String[] mParametersDesc = new String[4];
	public String[] getParametersDesc()
	{ return mParametersDesc; }

	public String getParameterDesc1()
	{ return mParametersDesc[0]; }
	public void setParameterDesc1(String value)
	{ mParametersDesc[0] = value; }

	public String getParameterDesc2()
	{ return mParametersDesc[1]; }
	public void setParameterDesc2(String value)
	{ mParametersDesc[1] = value; }

	public String getParameterDesc3()
	{ return mParametersDesc[2]; }
	public void setParameterDesc3(String value)
	{ mParametersDesc[2] = value; }

	public String getParameterDesc4()
	{ return mParametersDesc[3]; }
	public void setParameterDesc4(String value)
	{ mParametersDesc[3] = value; }




	//---------- Public methods




	//---------- Private methods
}
