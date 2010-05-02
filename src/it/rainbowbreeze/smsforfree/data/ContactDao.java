package it.rainbowbreeze.smsforfree.data;

import it.rainbowbreeze.smsforfree.domain.ContactPhone;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;


public abstract class ContactDao
{
	//---------- Private fields

	   


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
    public abstract ArrayList<ContactPhone> getContactNumbers(Activity callerActivity,  Uri contactUri);

}