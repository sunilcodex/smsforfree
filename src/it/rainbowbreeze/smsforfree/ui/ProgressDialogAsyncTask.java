package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree.common.ResultOperation;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Helper class for background computation during main UI thread.
 * This class simply prepare a progress dialog and show it when
 * operations are executed in background.
 * 
 * Derived class must declare doInBackground methods where execute
 * tasks.
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public abstract class ProgressDialogAsyncTask
	extends AsyncTask<String, Void, ResultOperation> {

		//---------- Ctors
	/**
	 * Create the progress dialog
	 * @param context
	 * @param progessTitle Title of progress dialog
	 */
	public ProgressDialogAsyncTask(Context context, String progessTitle)
	{
		//create the progress dialog
		mDialog = new ProgressDialog(context);
		mDialog.setMessage(progessTitle);
	}

	
	
	
	//---------- Private fields
	private final ProgressDialog mDialog;

	
	
	
	//---------- Public properties
	
	
	
	
	//---------- Public methods
	
	
	
	
	//---------- Private methods
	@Override
	protected void onPreExecute() {
		mDialog.show();
	}
	
	
	@Override
	protected void onPostExecute(ResultOperation result) {
		if (mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}
}
