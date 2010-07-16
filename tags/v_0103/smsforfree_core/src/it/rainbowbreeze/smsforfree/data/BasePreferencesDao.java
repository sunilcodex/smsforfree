/**
 * Copyright (C) 2010 Alfredo Morresi
 * 
 * This file is part of SmsForFree project.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/>.
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