package it.rainbowbreeze.smsforfree.domain;

/**
 * 
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class SmsServiceParameter
{
	//---------- Ctors
	public SmsServiceParameter() {
		this("", "", FORMAT_NONE);
	}
	
	public SmsServiceParameter(String desc, String value, int format)
	{
		mDesc = desc;
		mValue = value;
		mFormat = format;
	}

	
	
	
	//---------- Public fields
	public final static int FORMAT_NONE = 0;
	public final static int FORMAT_PASSWORD = 1;
	public final static int FORMAT_OPTIONAL = 2;
	

	
	
	//---------- Public properties
	/** Description of the field */
	private String mDesc;
	public String getDesc()
	{ return mDesc; }
	public void setDesc(String newValue)
	{ mDesc = newValue; }
	
	/** Value of the field */
	private String mValue;
	public String getValue()
	{ return mValue; }
	public void setValue(String newValue)
	{ mValue = newValue; }
	
	/** Metainformation about the field (password, allowed chars) */
	private int mFormat;
	public int getFormat()
	{ return mFormat; }
	public void setFormat(int newValue)
	{ mFormat = newValue; }

	
	

	//---------- Public methods
	
	public boolean isPassword()
	{ return (mFormat & FORMAT_PASSWORD) == FORMAT_PASSWORD; }

	public boolean isOptional()
	{ return (mFormat & FORMAT_OPTIONAL) == FORMAT_OPTIONAL; }

	
	
	
	//---------- Private methods

	
	
	
}
