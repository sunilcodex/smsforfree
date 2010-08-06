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
	private final static Uri CONTENT_PROVIDER_SENT_SMS = Uri.parse("content://sms/sent");
	private final static Uri CONTENT_PROVIDER_INBOX_SMS = Uri.parse("content://sms/inbox");

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
     * Verify if standard sent sms content provider is accessible on the device
     */
    public boolean isSentSmsProviderAvailable(Context context)
    {
    	Cursor cursor = context.getContentResolver().query(CONTENT_PROVIDER_SENT_SMS, null, null, null, null);
    	boolean exists = (null != cursor);
    	if (exists) cursor.close();
    	return exists;
    }
    
    /**
     * Verify if standard inbox sms content provider is accessible on the device
     */
    public boolean isInboxSmsProviderAvailable(Context context)
    {
    	Cursor cursor = context.getContentResolver().query(CONTENT_PROVIDER_INBOX_SMS, null, null, null, null);
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
    public ResultOperation<Void> saveSmsInSentFolder(Context context, String destination, String body)
    {
    	LogFacility.i("Adding SMS into Sent folder");
    	try {
	    	ContentValues values = new ContentValues();
	    	values.put("address", destination);
	    	values.put("body", body);
	    	context.getContentResolver().insert(CONTENT_PROVIDER_SENT_SMS, values);
    	} catch (Exception e) {
    		LogFacility.e("Error retrieving SMS content provider");
    		LogFacility.e(e);
    		return new ResultOperation<Void>(e, ResultOperation.RETURNCODE_ERROR_APPLICATION_ARCHITECTURE);
		}
    	
    	return new ResultOperation<Void>();
    }
    
    
    public int getMessagesNumberInSentFolder(Context context)
    {
        Cursor cursor = context.getContentResolver().query(CONTENT_PROVIDER_SENT_SMS, null, null, null,  null);                                   
        int smsTotal = cursor.getCount();;
        cursor.close();
        
        return smsTotal;
    }
    
    
    /**
     * Return the number of the last received SMS in the inbox system folder
     * @param context
     * @return
     */
    public ResultOperation<String> getLastSmsReceivedNumber(Context context)
    {
    	LogFacility.i("Getting number of latest SMS in the Inbox");

    	final String[] projection = new String[] { "_id", "thread_id", "address", "date", "body" };
		String[] selectionArgs = null;
		final String sortOrder = "date DESC";
		String numberSms = null;
		int count = 0;

		Cursor cursor = context.getContentResolver().query(
				CONTENT_PROVIDER_INBOX_SMS, projection, null, selectionArgs,
				sortOrder);

		if (cursor != null) {
			try {
				count = cursor.getCount();
				if (count > 0) {
					cursor.moveToFirst();
					numberSms = cursor.getString(2);
				}
			} catch (Exception e) {
				return new ResultOperation<String>(e, ResultOperation.RETURNCODE_ERROR_APPLICATION_ARCHITECTURE);
			} finally {
				cursor.close();
			}
		}
		
		return new ResultOperation<String>(numberSms);
    }
    

	//---------- Private methods

}
