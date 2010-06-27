package it.rainbowbreeze.smsforfree.data;

import it.rainbowbreeze.smsforfree.domain.ContactPhone;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;


public abstract class ContactDao
{
	//---------- Private fields
	private static final String SEPARATOR_FIELD = "A1A2A3";
	private static final String SEPARATOR_ROW = "B1B2B3";

	   


	//---------- Public properties

	private static ContactDao mInstance;
    public static ContactDao instance()
    {
    	if (null == mInstance)
    	{
            /*
             * Check the version of the SDK we are running on. Choose an
             * implementation class designed for that version of the SDK.
             *
             * Unfortunately we have to use strings to represent the class
             * names. If we used the conventional ContactAccessorSdk5.class.getName()
             * syntax, we would get a ClassNotFoundException at runtime on pre-Eclair SDKs.
             * Using the above syntax would force Dalvik to load the class and try to
             * resolve references to all other classes it uses. Since the pre-Eclair
             * does not have those classes, the loading of ContactAccessorSdk5 would fail.
             */
            String className;
            if (Integer.parseInt(Build.VERSION.SDK) >= Build.VERSION_CODES.ECLAIR){
                className = "it.rainbowbreeze.smsforfree.data.ContactDaoSdk5";
            } else {
                className = "it.rainbowbreeze.smsforfree.data.ContactDaoSdk3_4";
            }

            /*
             * Find the required class by name and instantiate it.
             */
            try {
                Class<? extends ContactDao> clazz =
                        Class.forName(className).asSubclass(ContactDao.class);
                mInstance = clazz.newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
    	}

    	return mInstance;
    }


    


    //---------- Public methods

    
    /**
     * Returns the correct intent for opening Contact activity
     */
    public abstract Intent getPickContactIntent();
    
    /**
     * Returns all phone numbers for a given contact
     * 
     * @param callerActivity
     * @param contactUri
     */
    public abstract List<ContactPhone> getContactNumbers(Activity callerActivity,  Uri contactUri);

    
    /**
     * Serialize a List of ContactPhone into a string
     * @return
     */
    public String SerializeContactPhones(List<ContactPhone> phones)
    {
    	if (null == phones) return "";
    	
    	StringBuilder sb = new StringBuilder();
    	int index = 0;
    	for(ContactPhone phone : phones)
    	{
    		sb.append(phone.getType())
    			.append(SEPARATOR_FIELD)
    			.append(phone.getNumber());
    		if (index < phones.size() -1) sb.append(SEPARATOR_ROW);
    		index++;
    	}
    	
    	return sb.toString();
    }
    
    /**
     * Deserialize a string onto a List of ContactPhone
     * @return
     */
    public List<ContactPhone> deserializeContactPhones(String phones)
    {
    	List<ContactPhone> contactPhones = new ArrayList<ContactPhone>();
    	if (TextUtils.isEmpty(phones)) return contactPhones;

    	String[] rows = phones.split(SEPARATOR_ROW);
    	for(String row : rows){
    		String[] fields = row.split(SEPARATOR_FIELD);
    		try{
    			String numberType = fields[0];
    			String number = fields[1];
    			contactPhones.add(new ContactPhone(numberType, number));
    		} catch (Exception e) {
    			//simpy, continue because never should arrive here
    		}
    	}
    	
    	return contactPhones;
    }
}