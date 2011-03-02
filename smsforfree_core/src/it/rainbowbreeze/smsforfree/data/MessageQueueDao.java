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
import java.util.GregorianCalendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static it.rainbowbreeze.libs.common.RainbowContractHelper.*;

/**
 * Database provider for text messages
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class MessageQueueDao implements IMessageQueueDao
{
    //---------- Private fields
    private static final String DATABASE_NAME = "smsforfree.db";
    private static final int DATABASE_VERSION = 1;
    private final LogFacility mLogFacility;

    /**
     * Standard projection for all the columns of a textmessage.
     */
    private static final String[] TEXTMESSAGE_FULL_PROJECTION = new String[] {
        TextMessage.FIELD_ID, // 0
        TextMessage.FIELD_DESTINATION, // 1
        TextMessage.FIELD_MESSAGE, // 2
        TextMessage.FIELD_PROVIDERID, // 3
        TextMessage.FIELD_SERVICEID, // 4
        TextMessage.FIELD_PROCESSING_STATUS, // 5
        TextMessage.FIELD_SEND_INTERVAL, // 6
    };

    /**
     * Projection for the send interval of a textmessage
     */
    private static final String[] TEXTMESSAGE_SEND_INTERVAL_PROJECTION = new String[] {
        TextMessage.FIELD_ID, // 0
        TextMessage.FIELD_SEND_INTERVAL, // 1
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
    /* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.data.IMessageQueueDao#getById(long)
	 */
    public TextMessage getById(long messageId) {
        List<TextMessage> messages = getTextMessageFromDatabase(TextMessage.FIELD_ID + "=" + messageId);

        //returns first webcam found or null
        return messages.size() > 0 ? messages.get(0) : null;
    }

    /* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.data.IMessageQueueDao#insert(it.rainbowbreeze.smsforfree.domain.TextMessage)
	 */
    public long insert(TextMessage message) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TextMessage.FIELD_DESTINATION, message.getDestination());
        values.put(TextMessage.FIELD_MESSAGE, message.getMessage());
        values.put(TextMessage.FIELD_PROVIDERID, message.getProviderId());
        values.put(TextMessage.FIELD_SERVICEID, message.getServiceId());
        values.put(TextMessage.FIELD_PROCESSING_STATUS, message.getProcessingStatus());

        long textMessageId = db.insert(TextMessage.TABLE_NAME, null, values);
        message.setId(textMessageId);

        return textMessageId;
    }

    /* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.data.IMessageQueueDao#delete(long)
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

    /* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.data.IMessageQueueDao#setProcessingStatus(long, int)
	 */
    public int setProcessingStatus(long textMessageId, int processingStatus) {
        ContentValues values = new ContentValues();
        values.put(TextMessage.FIELD_PROCESSING_STATUS, processingStatus);
        return updateTextMessageFields(textMessageId, values);
    }

    /* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.data.IMessageQueueDao#clearDatabaseComplete()
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
    
    /* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.data.IMessageQueueDao#isDatabaseEmpty()
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
    
    /* (non-Javadoc)
     * @see it.rainbowbreeze.smsforfree.data.IMessageQueueDao#getNextSendInterval()
     */
    public long getNextSendInterval() {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        
        //get current time
        long currentInterval = getCurrentTime();
        String where = TextMessage.FIELD_SEND_INTERVAL + " >= " + currentInterval + " AND " +
                TextMessage.FIELD_PROCESSING_STATUS + " = " + TextMessage.PROCESSING_QUEUED;
        
        //search for the next message that must be sent
        Cursor cur = db.query(TextMessage.TABLE_NAME,
                TEXTMESSAGE_SEND_INTERVAL_PROJECTION,
                where,
                null,
                null,
                null,
                TextMessage.FIELD_SEND_INTERVAL + " ASC");
        
        long sendInterval = 0;
        if (cur.moveToFirst()) {
            do {
                //long id = cur.getLong(0);
                sendInterval = cur.getLong(1);
                break;
            } while (cur.moveToNext());
        }
        cur.close();
        db.close();
        cur = null;

        return sendInterval;
    }




    //---------- Private methods
    /**
     * Read the requested text message from the database
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
                long sendInterval = cur.getLong(6);
                TextMessage textMessage = TextMessage.Factory.create(
                		id,
                		destination,
                		message,
                		providerId,
                		serviceId,
                        sendInterval,
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
     * Changes values in a text message item
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
    
    /**
     * Gets current time interval in milliseconds
     */
    private long getCurrentTime() {
        return new GregorianCalendar().getTimeInMillis();
    }
}
