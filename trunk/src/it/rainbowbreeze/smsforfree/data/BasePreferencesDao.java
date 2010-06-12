/**
 * 
 */
package it.rainbowbreeze.smsforfree.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 
 *
 * @author Alfredo Morresi
 */
public abstract class BasePreferencesDao
{
	protected SharedPreferences mSettings;
	protected SharedPreferences.Editor mEditor;
	
	protected final static String BACKUP_SUFFIX = "_backup";

	/**
	 * Load the preferences
	 */
	public void load(Context context) {
	    mSettings = context.getSharedPreferences(getPreferencesKey(), 0);
	    mEditor = mSettings.edit();
	}

	/**
	 * Store the preferences
	 */
	public boolean save() {
	    return mEditor.commit();
	}


	/**
	 * Backup preferences
	 */
    public void backup(Context context)
    {
        //load normal preferences, if needed
        if (null == mSettings)
        	load(context);

        //backup settings into backup shared preferences
        SharedPreferences settingsBackup = context.getSharedPreferences(getPreferencesKey() + BACKUP_SUFFIX, 0);
        SharedPreferences.Editor editorBackup = settingsBackup.edit();
        backupProperties(editorBackup);
    	editorBackup.commit();
    }
    
    
    /**
     * Restore a previously backup data
     * @param context
     */
    public void restore(Context context)
    {
        //if settings is null, no backup was made at the settings
        if (null == mSettings)
        	return;
    	
        //load backup preferences
    	SharedPreferences settingsBackup = context.getSharedPreferences(getPreferencesKey() + BACKUP_SUFFIX, 0);
    	restoreProperties(settingsBackup);
        //backup settings into backup shared preferences
		save();
    }

    
    protected abstract void backupProperties(SharedPreferences.Editor editorBackup);
	
	protected abstract void restoreProperties(SharedPreferences settingsBackup);
	
	protected abstract String getPreferencesKey();
}