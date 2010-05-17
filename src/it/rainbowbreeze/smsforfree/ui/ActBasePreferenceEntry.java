package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * To save any dynamic instance state before the activity
 * is put in background, call onSaveInstanceState(Bundle). To
 * restore these data, use the Bundle parameter of onCreate
 * 
 * http://developer.android.com/reference/android/app/Activity.html#ProcessLifecycle
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public abstract class ActBasePreferenceEntry
	extends PreferenceActivity
{
	//---------- Private fields
	protected final static int OPTIONMENU_CANCEL = 1000;
	
	/** True when the started activity returns, and reactivate this activity */
	protected boolean mReturnedFromStartedActivity;
	
	protected boolean mRecreatedAfterARotation;

	
	
	
	//---------- Public properties

	
	
	
	//---------- Events
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mRecreatedAfterARotation = null != savedInstanceState;
		
		if(mRecreatedAfterARotation)
			getDataFromTemporaryStore(savedInstanceState);

		//onCreate is not executed if a started activity return.
		//the only exception is when the screen rotate while
		//the started activity is in foreground
		mReturnedFromStartedActivity = false;
	}
	
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		
		menu.add(0, OPTIONMENU_CANCEL, 9, R.string.common_mnuCancel)
			.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		
		return true;
	};
	
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
		case OPTIONMENU_CANCEL:
			cancelEdit();
			break;
		}
		
		return super.onOptionsItemSelected(item);
	};


	/**
	 * Called when back button is pressed, from SDK 5 and above.
	 * SDK 4 and below don't call this method
	 */
    public void onBackPressed() {
    	//save data
    	saveData();
    };


//    @Override
//    protected void onPause() {
//    	super.onPause();
//    	
//    	if (!booCancelEdit)
//    	{
//	    	//save the content of the activity
//    		saveData();
//    	}
//    };
//    

    /* (non-Javadoc)
     * @see android.app.Activity#onRestart()
     */
    @Override
    protected void onRestart() {
    	super.onRestart();
    	mReturnedFromStartedActivity = true;
    	//next call onStart() and then onResume()
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
    	super.onResume();
		
    	//this method occurs also in the calling activity when the caller activity returns
    	if (!mReturnedFromStartedActivity)
			//restore data
			loadData();
    	else
			mReturnedFromStartedActivity = false;

    }
    

//    /* (non-Javadoc)
//     * @see android.app.Activity#startActivity(android.content.Intent)
//     */
//    @Override
//    public void startActivity(Intent intent) {
//    	returnedFromStartedActivity = true;
//    	super.startActivity(intent);
//    }
    
    
//    /* (non-Javadoc)
//     * @see android.app.Activity#startActivityForResult(android.content.Intent, int)
//     */
//    @Override
//    public void startActivityForResult(Intent intent, int requestCode) {
//    	returnedFromStartedActivity = true;
//    	super.startActivityForResult(intent, requestCode);
//    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	putDataIntoTemporaryStore(outState);
    }
    


	//---------- Public methods




	//---------- Private methods
//    /**
//     * Execute some common tasks, must be called at the end of OnCreate method
//     */
//    protected void startEdit(Bundle savedInstanceState)
//    {
//    	//if the activity is resumed, doesn't backup current data
//    	if (null != savedInstanceState)
//    		return;
//
//    	//backup the message value
//    	backupData();
//        //edit mode
//        booCancelEdit = false;
//
//        //loadDataIntoControls();
//        //is called by OnResume method, after the onCreate
//    }
    
    
	/**
	 * Cancel the edit of the data and restore original values
	 */
	protected void cancelEdit()
    {
//		restoreData();
//    	booCancelEdit = true;
    	finish();
    }

    
	/**
	 * Save values of the controls into the storage. Should manage saving errors
	 */
	protected abstract void saveData();
	
	/**
	 * Load data from the storage and put it into the controls
	 */
	protected abstract void loadData();
	
	/**
	 * Implements a way to save any dynamic instance state
	 * (called before the activity is placed in background)
	 */
	protected abstract void putDataIntoTemporaryStore(Bundle outState);
	
	/**
	 * Implements a way to restore values of the storage
	 * (called when the user select the "cancel edit" action)
	 */
	protected abstract void getDataFromTemporaryStore(Bundle savedInstanceState);
}
