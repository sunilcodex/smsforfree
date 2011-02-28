package it.rainbowbreeze.smsforfree.data;

import it.rainbowbreeze.smsforfree.domain.TextMessage;

public interface IMessageQueueDao {

	//---------- Public methods
	/**
	 * Retrieves a textMessage by its id
	 * 
	 * @param messageId
	 * @return message object or null if not found
	 */
	public abstract TextMessage getById(long messageId);

	/**
	 * Add a new text message to the queue
	 * 
	 * @param message
	 * @return the id of the new webcam
	 */
	public abstract long insert(TextMessage message);

	/**
	 * Remove a text message from the queue
	 * 
	 * @param textMessageId the id of the webcam to delete
	 * @return the deleted text message (1 if success, 0 if no webcams were found)
	 */
	public abstract int delete(long textMessageId);

	/**
	 * Set the processing status of a text message
	 * 
	 * @param textMessageId
	 * @param processingStatus
	 */
	public abstract int setProcessingStatus(long textMessageId,
			int processingStatus);

	/**
	 * Completely clean the database (used in tests)
	 */
	public abstract int clearDatabaseComplete();

	/**
	 * Return true if database is empty and initialization is needed
	 */
	public abstract boolean isDatabaseEmpty();

}