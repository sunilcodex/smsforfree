/**
 * Copyright (C) 2010 Alfredo Morresi
 * 
 * This file is part of SmsForFree project.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/>.
 */

package it.rainbowbreeze.smsforfree.data;

import it.rainbowbreeze.smsforfree.domain.ContactPhone;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

/**
 * Dao for contact retrieving
 *
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public abstract class ContactsDao
{
	//---------- Private fields
	private static final String SEPARATOR_FIELD = "A1A2A3";
	private static final String SEPARATOR_ROW = "B1B2B3";

	   


	//---------- Public properties
    /** Index of id. */
    public static final int FILTER_INDEX_ID = 0;
    /** Index of name. */
    public static final int FILTER_INDEX_NAME = 1;
    /** Index of number. */
    public static final int FILTER_INDEX_NUMBER = 2;
    /** Index of type. */
    public static final int FILTER_INDEX_TYPE = 3;

	private static ContactsDao mInstance;
    public static ContactsDao instance()
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
                className = "it.rainbowbreeze.smsforfree.data.ContactsDaoSdk5";
            } else {
                className = "it.rainbowbreeze.smsforfree.data.ContactsDaoSdk3_4";
            }

            /*
             * Find the required class by name and instantiate it.
             */
            try {
                Class<? extends ContactsDao> clazz =
                        Class.forName(className).asSubclass(ContactsDao.class);
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
     * Get {@link Uri} for filter contacts by address.
     * 
     * @return {@link Uri}
     */
    public abstract Uri getContentUri();

    /**
     * Get projection for filter contacts by address.
     * 
     * @return projection
     */
    public abstract String[] getContactNumbersContentProjection();

    /**
     * Get sort order for filter contacts.
     * 
     * @return sort
     */
    public abstract String getContactNumbersContentSort();

    /**
     * Get WHERE for filter.
     * 
     * @param filter
     *            filter
     * @return WHERE
     */
    public abstract String getContactNumbersContentWhere(final String filter);

    /**
     * Get {@link String} selecting mobiles only.
     * 
     * @return mobiles only {@link String}
     */
    public abstract String getContactNumbersMobilesOnlyString();
    
    
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