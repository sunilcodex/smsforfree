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
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

/**
 * @author rainbowbreeze
 *
 */
public class ContactsDaoSdk5
	extends ContactsDao
{
	//---------- Private fields
    /** Projection for persons query, show. */
    private static final String[] CONTACT_NUMBERS_PROJECTION_CONTENT = new String[] {
            BaseColumns._ID, // 0
            ContactsContract.Data.DISPLAY_NAME, // 1
            ContactsContract.CommonDataKinds.Phone.NUMBER, // 2
            ContactsContract.CommonDataKinds.Phone.TYPE // 3
        };

    /** SQL to select mobile numbers only. */
    private static final String CONTACT_NUMBERS_MOBILES_ONLY = ") AND (" + 
            ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE + ")";
	
    /** Sort Order. */
    private static final String CONTACT_NUMBERS_QUERY_SORT_ORDER = // .
            ContactsContract.CommonDataKinds.Phone.STARRED + " DESC, " + 
            ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED + " DESC, " +
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC, " +
            ContactsContract.CommonDataKinds.Phone.TYPE;
	
	
    //---------- Constructors

    
    
    
	//---------- Public properties
	
	
	
	
	//---------- Public methods
	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.data.ContactDao#getPickContactIntent()
	 */
	@Override
	public Intent getPickContactIntent() {
		return(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI));
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.data.ContactDao#getContactNumbers(android.app.Activity, android.net.Uri)
	 */
	@Override
	public List<ContactPhone> getContactNumbers(Activity callerActivity, Uri contactUri)
	{
 		ArrayList<ContactPhone> phones = new ArrayList<ContactPhone>();
 		
		String id = String.valueOf(ContentUris.parseId(contactUri));
 		Cursor pCur = callerActivity.managedQuery(
 				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
 				null, 
 				ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
 				new String[]{id}, null);
 		while (pCur.moveToNext()) {
 			String numberType = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)); 
 			String number = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
 			phones.add(new ContactPhone(numberType, number));
 		} 
 		pCur.close();
 		return phones;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContactNumbersContentWhere(final String filter) {
        String f = DatabaseUtils.sqlEscapeString('%' + filter.toString() + '%');
        StringBuilder s = new StringBuilder();
        s.append("(" + ContactsContract.Data.DISPLAY_NAME + " LIKE ");
        s.append(f);
        s.append(") OR (" + ContactsContract.CommonDataKinds.Phone.DATA1
                + " LIKE ");
        s.append(f);
        s.append(")");
        return s.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getContactNumbersMobilesOnlyString() {
        return CONTACT_NUMBERS_MOBILES_ONLY;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Uri getContentUri() {
        return ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getContactNumbersContentProjection() {
        return CONTACT_NUMBERS_PROJECTION_CONTENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContactNumbersContentSort() {
        return CONTACT_NUMBERS_QUERY_SORT_ORDER;
    }
    
	
	
	
	//---------- Private methods

}
