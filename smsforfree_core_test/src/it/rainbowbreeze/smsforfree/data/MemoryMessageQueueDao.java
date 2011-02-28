/**
 * Copyright (C) 2011 Alfredo Morresi
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

import java.util.HashMap;

import it.rainbowbreeze.smsforfree.domain.TextMessage;

/**
 * Implementation for a memory message queue
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class MemoryMessageQueueDao implements IMessageQueueDao {
	//---------- Private fields
	private HashMap<Long, TextMessage> mTextMessageStorage;
	private static long mCurrentId = 0;

	
	
	
	//---------- Constructor
	public MemoryMessageQueueDao() {
		mTextMessageStorage = new HashMap<Long, TextMessage>();
	}


	//---------- Private methods
	@Override
	public TextMessage getById(long messageId) {
		return mTextMessageStorage.get(messageId);
	}

	@Override
	public long insert(TextMessage message) {
	    mCurrentId++;
		mTextMessageStorage.put(mCurrentId, message);
		return mCurrentId;
	}

	@Override
	public int delete(long textMessageId) {
		if (mTextMessageStorage.containsKey(textMessageId)) {
			mTextMessageStorage.remove(textMessageId);
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public int setProcessingStatus(long textMessageId, int processingStatus) {
		if (mTextMessageStorage.containsKey(textMessageId)) {
			mTextMessageStorage.get(textMessageId).setProcessingStatus(processingStatus);
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public int clearDatabaseComplete() {
		int size = mTextMessageStorage.size();
		mTextMessageStorage.clear();
		return size;
	}

	@Override
	public boolean isDatabaseEmpty() {
		return mTextMessageStorage.isEmpty();
	}

}
