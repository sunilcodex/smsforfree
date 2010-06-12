package it.rainbowbreeze.smsforfree.common;

/**
 * Activity to link with ASyncTask that execute a service command
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public interface IExecuteProviderCommandActivity {
	
	/**
	 * Called by AsyncTask when the command execution completed
	 * @param res
	 */
	void executeCommandComplete(ResultOperation res);
}
