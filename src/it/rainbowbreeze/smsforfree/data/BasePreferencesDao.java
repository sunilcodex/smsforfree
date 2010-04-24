/**
 * 
 */
package it.rainbowbreeze.smsforfree.data;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * 
 *
 * @author Alfredo Morresi
 */
public abstract class BasePreferencesDao
{
	protected SharedPreferences settings;
	protected SharedPreferences.Editor editor;
	
	protected final static String BACKUP_SUFFIX = "_backup";

	/**
	 * Load the preferences
	 */
	public void load(Activity a) {
	    settings = a.getSharedPreferences(getPreferencesKey(), 0);
	    editor = settings.edit();
	}

	/**
	 * Store the preferences
	 */
	public boolean save() {
	    return editor.commit();
	}


	/**
	 * Backup preferences
	 */
    public void backup(Activity a)
    {
        //load normal preferences, if needed
        if (null == settings)
        	load(a);

        //backup settings into backup shared preferences
        SharedPreferences settingsBackup = a.getSharedPreferences(getPreferencesKey() + BACKUP_SUFFIX, 0);
        SharedPreferences.Editor editorBackup = settingsBackup.edit();
        backupProperties(editorBackup);
    	editorBackup.commit();
    }
    
    
    /**
     * Restore a previously backup data
     * @param a
     */
    public void restore(Activity a)
    {
        //if settings is null, no backup was made at the settings
        if (null == settings)
        	return;
    	
        //load backup preferences
    	SharedPreferences settingsBackup = a.getSharedPreferences(getPreferencesKey() + BACKUP_SUFFIX, 0);
    	restoreProperties(settingsBackup);
        //backup settings into backup shared preferences
		save();
    }

    
    protected abstract void backupProperties(SharedPreferences.Editor editorBackup);
	
	protected abstract void restoreProperties(SharedPreferences settingsBackup);
	
	protected abstract String getPreferencesKey();
}