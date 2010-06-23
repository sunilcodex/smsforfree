package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree_core.R;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;
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
	//---------- Ctors

	
	
	
	//---------- Private fields
	protected final static int OPTIONMENU_CANCEL = 1000;
	
	protected final static String KEY_MUSTRETURNFROMCALLEDACTIVITY = "mustReturnFromCalledActivity";
	
	/** True when the onResume event is called after a child activity return */
	protected boolean mMustReturnedFromStartedActivity;
	
	/** True when the activity is loaded for the first time, false in all the other cases
	 *   (for example, when screen is rotated)
	 */
	protected boolean mFirstStart;

	
	
	
	//---------- Public properties

	
	
	
	//---------- Events
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (null != savedInstanceState){
			//reload volatile data (screen rotated etc)
			loadVolatileData(savedInstanceState);
			mFirstStart = false;
		} else {
			//first start
			mFirstStart = true;
			//no called activity
			mMustReturnedFromStartedActivity = false;
		}
		
		//if a called activity return and the screen was rotate inside the
		//called activity, a call to this method is done, and after a call to
		//onStart will be made, where data are put inside views.
	}
	

	/**
	 * Called when the activity starts.
	 * When a caller activity returns, the onRestart and the onStart methods are called
	 * When a caller activity returns and the screen was rotated, only onStart is called.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		
		//load data inside controls if the activity is started for the first time
		if (mFirstStart) {
			loadDataIntoViews();
			mFirstStart = false;
		}
		
		if (mMustReturnedFromStartedActivity)
			returnFromStartedActivity();
		mMustReturnedFromStartedActivity = false;
	}
	

	/**
	 * Create option menu with the cancel button
	 * @param menu
	 * @return
	 */
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		
		menu.add(0, OPTIONMENU_CANCEL, 9, R.string.common_mnuCancel)
			.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		
		return true;
	};

	/**
	 * Manage the selection of cancel button in the option menu
	 * @param item
	 * @return
	 */
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
	 * Called when an activity must be destroyed (like when the user rotate
	 * the screen
	 * @param outState
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveVolatileData(outState);
	}
	
	
	/**
	 * Intercept when the user press the Back button and create an event tracking
	 * of the event
	 * @param keyCode
	 * @param event
	 * @return
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//        if (keyCode == KeyEvent.KEYCODE_BACK
//                && event.getRepeatCount() == 0) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

	/**
	 * Intercept when the user release the Back button, call the method for
	 * saving data and close the activity
	 * @param keyCode
	 * @param event
	 * @return
	 */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
                && !event.isCanceled()) {
            if (saveDataFromViews()) finish();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }	
	
    /* (non-Javadoc)
     * @see android.app.Activity#startActivity(android.content.Intent)
     */
    @Override
    public void startActivity(Intent intent) {
    	mMustReturnedFromStartedActivity = true;
    	super.startActivity(intent);
    }
    
    
    /* (non-Javadoc)
     * @see android.app.Activity#startActivityForResult(android.content.Intent, int)
     */
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
    	mMustReturnedFromStartedActivity = true;
    	super.startActivityForResult(intent, requestCode);
    }
    


	//---------- Public methods




	//---------- Private methods
    
	/**
	 * Save values modified in views of the activity. Should manage saving errors.
	 * Called when the user press the back button
	 * 
	 * @return true if everything went well, otherwise false
	 */
	protected abstract boolean saveDataFromViews();
	
	/**
	 * Load values into activity's views.
	 * Called when activity first start, and not when return from called activity
	 * or after a screen rotation, because if the views have an id, the system
	 * automatically restores their values
	 */
	protected abstract void loadDataIntoViews();
	
	/**
	 * Save volatile activity data into Bundle object. Called before the rotation of
	 * the screen, for example. Save data that can change during the activity lifecycle
	 * and that is not stored inside views (data inside views with id is automatically restored)
	 */
	protected void saveVolatileData(Bundle outState)
	{
		outState.putBoolean(KEY_MUSTRETURNFROMCALLEDACTIVITY, mMustReturnedFromStartedActivity);
	}
	
	/**
	 * Load volatile activity data from Bundle object. Called when an activity is
	 * restore after a screen rotation, for example.
	 */
	protected void loadVolatileData(Bundle savedInstanceState)
	{
		mMustReturnedFromStartedActivity = savedInstanceState.getBoolean(KEY_MUSTRETURNFROMCALLEDACTIVITY);
	}
	
	
	/**
	 * Called when the user select the "Cancel" button in option menu
	 */
	protected void cancelEdit()
    {
    	finish();
    }
	
	/**
	 * Called when this activity calls another activiy and the called activity returns
	 */
	protected void returnFromStartedActivity()
	{	}


}
