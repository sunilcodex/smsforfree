package it.rainbowbreeze.smsforfree.logic;

import java.lang.ref.WeakReference;

import it.rainbowbreeze.smsforfree.common.ResultOperation;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Helper class for background computation during main UI thread.
 * This class simply prepare a progress dialog and show it when
 * operations are executed in background.
 * 
 * In the class is also implement a object/observer pattern
 * 
 * Derived class must declare doInBackground methods where execute
 * tasks.
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public abstract class ProgressDialogAsyncTask<T>
	extends AsyncTask<String, Void, ResultOperation>
{

	//---------- Ctors
	/**
	 * Create the progress dialog
	 * @param context
	 * @param progessTitle Title of progress dialog
	 */
	public ProgressDialogAsyncTask(Context context, T callerActivity, String progessTitle)
	{
		//create the progress dialog
		registrerNewContext(context);
		registrerCallerActity(callerActivity);
		mDialog = new ProgressDialog(getContext());
		mDialog.setMessage(progessTitle);
	}

	
	
	
	//---------- Private fields
	protected final ProgressDialog mDialog;
	protected WeakReference<Context> mWeakContext; 
	protected T mCallerActivity;
	
	
	//---------- Public properties
	
	
	
	//---------- Public methods
	/**
	 * Register new context
	 */
	public void registrerNewContext(Context context) {
		mWeakContext = new WeakReference<Context>(context);
	}
		
	/**
	 * Register a new calling activity
	 */
	public void registrerCallerActity(T newCallerActivity) {
		mCallerActivity = newCallerActivity;
	}
	
	/**
	 * Unregister the calling activity
	 */
	public void unregisterCallerActivity(){
		mCallerActivity = null;
	}
	
	
	
	//---------- Private methods

	/**
	 * Get the caller context from the WeakReference object
	 */
	protected Context getContext()
	{ return mWeakContext.get(); }
	
		
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
