package it.rainbowbreeze.smsforfree.common;

/**
 * Activity to link with ASyncTask that send message to a provider
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public interface ISendCaptchaActivity {
	
	/**
	 * Called when the background activity has sent the message
	 * @param result result of sending message
	 */
	void sendCaptchaComplete(ResultOperation result);
}
