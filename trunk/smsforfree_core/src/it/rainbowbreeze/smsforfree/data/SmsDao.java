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

import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Store SMS in the sent forlder of the device
 *
 * @author Alfredo Morresi
 */
public class SmsDao {
	//---------- Ctors

	//---------- Private fields
	private final static Uri SMS_CONTENT_PROVIDER = Uri.parse("content://sms/sent");

	//---------- Public properties

    private static SmsDao mInstance;
    public static SmsDao instance()
    {
    	if (null == mInstance)
    		mInstance = new SmsDao();
    	return mInstance;
    }

	//---------- Public methods
    
    /**
     * Verify if standard sms content provider is accessible on the device
     */
    public boolean isSmsProviderAvailable(Context context)
    {
    	Cursor cursor = context.getContentResolver().query(SMS_CONTENT_PROVIDER, null, null, null, null);
    	boolean exists = (null != cursor);
    	if (exists) cursor.close();

    	return exists;
    }
    
    /**
     * Insert destination and body as new sms in the sms sent folder
     * 
     * @param context
     * @param destination
     * @param body
     * @return
     */
    public ResultOperation<String> saveSmsInSentFolder(Context context, String destination, String body)
    {
    	try {
	    	ContentValues values = new ContentValues();
	    	values.put("address", destination);
	    	values.put("body", body);
	    	context.getContentResolver().insert(SMS_CONTENT_PROVIDER, values);
    	} catch (Exception e) {
    		LogFacility.e("Error retrieving SMS content provider");
    		LogFacility.e(e);
    		return new ResultOperation<String>(e, ResultOperation.RETURNCODE_ERROR_APPLICATION_ARCHITECTURE);
		}
    	
    	return new ResultOperation<String>();
    }
    
    
    public int getMessagesNumberInSentFolder(Context context)
    {
        Cursor cursor = context.getContentResolver().query(SMS_CONTENT_PROVIDER, null, null, null,  null);                                   
        //String body = null;
        int smsTotal = 0;

        if(cursor.moveToFirst()){
        	smsTotal ++;
        }
        while (cursor.moveToNext()) {
        	smsTotal++;
        }
        cursor.close();
        
        return smsTotal;
		
        //body = cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();               
    }
    

	//---------- Private methods

}
