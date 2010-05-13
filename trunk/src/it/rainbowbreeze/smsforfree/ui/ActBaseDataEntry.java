/**
 * 
 */
package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.data.BasePreferencesDao;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * A basic data editing activity, with backup and restore of
 * preferences and other goodies
 *
 * @author Alfredo Morresi
 */
public abstract class ActBaseDataEntry
	extends Activity
{
	//---------- Ctors

	
	
	
	//---------- Private fields
	protected boolean booCancelEdit;

	protected final static int OPTIONMENU_CANCEL = 1000;
	
	/** True when the onResume event is called after a child activity return */
	protected boolean returnedFromStartedActivity;

	
	
	
	//---------- Public properties

	
	
	
	//---------- Events
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//backup data if the activity is created
		startEdit(savedInstanceState);
		
		returnedFromStartedActivity = false;
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
     * A call-back for when the user presses the "Postino" button.
     */
    OnClickListener mBackToMenuListener = new OnClickListener() {
        public void onClick(View v) {
        	//close the activity
        }
    };	


    /**
     * A call-back for when the user presses the "Save" button.
     */
    OnClickListener mEditSaveListener = new OnClickListener() {
        public void onClick(View v) {
        	//close the activity
        }
    };


    @Override
    protected void onPause() {
    	super.onPause();
    	
    	if (!booCancelEdit)
    	{
	    	//save the content of the activity
    		saveData();
    	}
    };
    
    
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
    	super.onResume();
		
    	//this method occurs also in the calling activity when the caller activity returns
    	if (!returnedFromStartedActivity)
			//restore data
			loadData();
    	else
			returnedFromStartedActivity = false;

    }
    

    /* (non-Javadoc)
     * @see android.app.Activity#startActivity(android.content.Intent)
     */
    @Override
    public void startActivity(Intent intent) {
    	returnedFromStartedActivity = true;
    	super.startActivity(intent);
    }
    
    
    /* (non-Javadoc)
     * @see android.app.Activity#startActivityForResult(android.content.Intent, int)
     */
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
    	returnedFromStartedActivity = true;
    	super.startActivityForResult(intent, requestCode);
    }
    


	//---------- Public methods




	//---------- Private methods
    
	/**
	 * Save values of the controls into the storage. Should manage saving errors
	 */
	protected abstract void saveData();
	
	/**
	 * Load data from the storage and put it into the controls
	 */
	protected abstract void loadData();
	
	/**
	 * Implements a way to backup values of the storage
	 * (called before the edit start)
	 */
	protected abstract void backupData();
	
	/**
	 * Implements a way to restore values of the storage
	 * (called when the user select the "cancel edit" action)
	 */
	protected abstract void restoreData();
	
	
    /**
     * Execute some common tasks, must be called at the end of OnCreate method
     */
    protected void startEdit(Bundle savedInstanceState)
    {
    	//if the activity is resumed, doesn't backup current data
    	if (null != savedInstanceState)
    		return;

    	//backup the message value
    	backupData();
        //edit mode
        booCancelEdit = false;

        //loadDataIntoControls();
        //is called by OnResume method, after the onCreate
    }
    
    
	/**
	 * Cancel the edit of the data and restore original values
	 */
	protected void cancelEdit()
    {
		restoreData();
    	booCancelEdit = true;
    	finish();
    }

}
