/**
 * Copyright (C) 2010 Alfredo Morresi
 * 
 * This file is part of WebcamHolmes project.
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
import it.rainbowbreeze.smsforfree.domain.TextMessage;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static it.rainbowbreeze.libs.common.RainbowContractHelper.*;

/**
 * Provider for categories and webcams
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class MessageQueueDao
{
    //---------- Private fields
    private static final String DATABASE_NAME = "smsforfree.db";
    private static final int DATABASE_VERSION = 1;
    private final LogFacility mLogFacility;

    /**
     * Standard projection for the interesting columns of a webcam.
     */
    private static final String[] TEXTMESSAGE_FULL_PROJECTION = new String[] {
        TextMessage.FIELD_ID, // 0
        TextMessage.FIELD_DESTINATION, // 1
        TextMessage.FIELD_MESSAGE, // 2
        TextMessage.FIELD_PROVIDERID, // 3
        TextMessage.FIELD_SERVICEID, // 4
        TextMessage.FIELD_PROCESSING_STATUS, // 5
    };

    
    
    
    //---------- Constructor    
    public MessageQueueDao(Context context, LogFacility logFacility) {
        mLogFacility = checkNotNull(logFacility, "Log Facility");
        mOpenHelper = new DatabaseHelper(context, mLogFacility);
    }
    

    
    //---------- Inner classes
    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        private final LogFacility mLogFacility;

        DatabaseHelper(Context context, LogFacility logFacility) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mLogFacility = logFacility;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TextMessage.TABLE_NAME + " ("
                    + TextMessage.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + TextMessage.FIELD_DESTINATION + " TEXT NOT NULL,"
                    + TextMessage.FIELD_MESSAGE + " TEXT,"
                    + TextMessage.FIELD_PROVIDERID + " TEXT NOT NULL,"
                    + TextMessage.FIELD_SERVICEID + " TEXT,"
                    + TextMessage.FIELD_PROCESSING_STATUS + " SMALL"
                    + ");");
       }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            mLogFacility.i("Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TextMessage.TABLE_NAME);
            onCreate(db);
        }

    }

    private DatabaseHelper mOpenHelper;




    //---------- Public properties



    
    //---------- Public methods
    /**
     * Retrieves a textMessage by its id
     * @param webcamId
     * @return webcam object or null
     */
    public TextMessage getById(long messageId) {
        List<TextMessage> messages = getTextMessageFromDatabase(TextMessage.FIELD_ID + "=" + messageId);

        //returns first webcam found or null
        return messages.size() > 0 ? messages.get(0) : null;
    }

    /**
     * Add a new text message to the queue
     * @param message
     * @return the id of the new webcam
     */
    public long insert(TextMessage message) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TextMessage.FIELD_DESTINATION, message.getDestination());
        values.put(TextMessage.FIELD_MESSAGE, message.getMessage());
        values.put(TextMessage.FIELD_PROVIDERID, message.getProviderId());
        values.put(TextMessage.FIELD_SERVICEID, message.getServiceId());
        values.put(TextMessage.FIELD_PROCESSING_STATUS, message.getProcessingStatus());

        long webcamId = db.insert(TextMessage.TABLE_NAME, null, values);
        message.setId(webcamId);

        return webcamId;
    }

    /**
     * Remove a text message from the queue
     * @param textMessageId the id of the webcam to delete
     * @return the deleted text message (1 if success, 0 if no webcams were found)
     */
    public int delete(long textMessageId) {
        int count;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        count = db.delete(
                TextMessage.TABLE_NAME,
                TextMessage.FIELD_ID + "=" + textMessageId,
                null);
        db.close();
        return count;
    }

    /**
     * Set the processing status of a text message
     * @param textMessageId
     * @param processingStatus
     */
    public int setProcessingStatus(long textMessageId, int processingStatus) {
        ContentValues values = new ContentValues();
        values.put(TextMessage.FIELD_PROCESSING_STATUS, processingStatus);
        return updateTextMessageFields(textMessageId, values);
    }

    /**
     * Completely clean the database (used in tests)
     */
    public int clearDatabaseComplete() {
        int count;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        //delete all text messages
        count = db.delete(
                TextMessage.TABLE_NAME,
                null,
                null);
        db.close();
        return count;
    }
    
    
    /**
     * Return true if database is empty and initialization is needed
     */
    public boolean isDatabaseEmpty() {
        boolean messagesExist;
        Cursor cur;
        
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        cur = db.query(TextMessage.TABLE_NAME,
                new String[]{TextMessage.FIELD_ID},
                null, null, null, null, null);
        messagesExist = cur.moveToFirst();
        cur.close();
        db.close();
        
        return !(messagesExist);
    }
    
    



    
    //---------- Private methods
    /**
     * Read the requested webcams from the database
     * 
     * @param where
     */
    private List<TextMessage> getTextMessageFromDatabase(String where) {
        List<TextMessage> list = new ArrayList<TextMessage>();
        
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cur = db.query(TextMessage.TABLE_NAME,
                TEXTMESSAGE_FULL_PROJECTION,
                where,
                null,
                null,
                null,
                TextMessage.DEFAULT_SORT_ORDER);
        
        if (cur.moveToFirst()) {
            do {
                long id = cur.getLong(0);
                String destination = cur.getString(1);
                String message = cur.getString(2);
                String providerId = cur.getString(3);
                String serviceId = cur.getString(4);
                int processingStatus = cur.getInt(5);
                TextMessage textMessage = TextMessage.Factory.create(
                		id,
                		destination,
                		message,
                		providerId,
                		serviceId,
                		processingStatus);

                list.add(textMessage);
            } while (cur.moveToNext());
        }
        cur.close();
        db.close();
        cur = null;

        return list;
    }
    
    /**
     * Changes values in a webcam item
     * @param textMessageId id of the category
     * @param valuesToUpdate values to change
     * @return number of items updated (generally 1)
     */
    private int updateTextMessageFields(long textMessageId, ContentValues valuesToUpdate) {
        int count;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        count = db.update(
                TextMessage.TABLE_NAME,
                valuesToUpdate,
                TextMessage.FIELD_ID + "=" + textMessageId,
                null);
        db.close();
        return count;
    }
}
