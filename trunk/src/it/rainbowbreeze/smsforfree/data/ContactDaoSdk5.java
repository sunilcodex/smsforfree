/**
 * 
 */
package it.rainbowbreeze.smsforfree.data;

import it.rainbowbreeze.smsforfree.domain.ContactPhone;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * @author rainbowbreeze
 *
 */
public class ContactDaoSdk5
	extends ContactDao
{
	//---------- Ctors

	
	
	
	//---------- Private fields

	
	
	
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
	public ArrayList<ContactPhone> getContactNumbers(Activity callerActivity, Uri contactUri) {
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

	
	
	
	//---------- Private methods

}
