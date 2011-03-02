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
	public TextMessage getById(long messageId);

	/**
	 * Adds a new text message to the queue
	 * 
	 * @param message
	 * @return the id of the new webcam
	 */
	public long insert(TextMessage message);

	/**
	 * Removes a text message from the queue
	 * 
	 * @param textMessageId the id of the webcam to delete
	 * @return the deleted text message (1 if success, 0 if no webcams were found)
	 */
	public int delete(long textMessageId);

	/**
	 * Sets the processing status of a text message
	 * 
	 * @param textMessageId
	 * @param processingStatus
	 */
	public int setProcessingStatus(long textMessageId,
			int processingStatus);

	/**
	 * Completely clean the database (used in tests)
	 */
	public int clearDatabaseComplete();

	/**
	 * Returns true if database is empty and initialization is needed
	 */
	public boolean isDatabaseEmpty();
	
	/**
	 * Read from the message queue the time interval of the newest message to
	 * send and returns it
	 */
	public long getNextSendInterval();

}