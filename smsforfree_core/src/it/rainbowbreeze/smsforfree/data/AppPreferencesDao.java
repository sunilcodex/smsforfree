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

import it.rainbowbreeze.libs.data.RainbowAppPreferencesDao;
import it.rainbowbreeze.libs.helper.RainbowStringHelper;
import it.rainbowbreeze.smsforfree.common.App;
import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferencesDao
	extends RainbowAppPreferencesDao
{
	//---------- Private fields
	
    private static final String PROP_AUTO_CLEAR_MESSAGE = "clearmessage";
    private static final String PROP_INSERT_MESSAGE_INTO_PIM = "insertmessageintopim";
    private static final String PROP_DEFAULT_INTERNATIONAL_PREFIX = "defaultInternationalPrefix";
    private static final String PROP_SIGNATURE = "signature";
    private static final String PROP_SMSCOUNTER_DATE = "smsCounterDate";
    private static final String PROP_SMSCOUNTER_NUMBERFORCURRENTDAY = "smsCounterNumber";
    private static final String PROP_SMSCOUNTER_TOTAL = "smsCounterTotal";
    private static final String PROP_MESSAGETEMPLATES = "messageTemplates";
    
    private static final String PROP_LASTUSED_PROVIDERID = "lastusedProvider";
    private static final String PROP_LASTUSED_SUBSERVICEID = "lastusedSubservice";
    private static final String PROP_LASTUSED_DESTINATION = "lastusedDestination";
    private static final String PROP_LASTUSED_MESSAGE = "lastusedMessage";
    
    private static final String TEMPLATES_SEPARATOR = "§§§§";
    
    
    

	//---------- Public Properties
	public AppPreferencesDao(Context context, String preferenceKey) {
		super(context, preferenceKey);
	}




	//---------- Public Properties




	//---------- Public Methods

    public boolean getAutoClearMessage()
    { return mSettings.getBoolean(PROP_AUTO_CLEAR_MESSAGE, false); }
    public void setAutoClearMessage(boolean newValue)
    { mEditor.putBoolean(PROP_AUTO_CLEAR_MESSAGE, newValue); }
    
    public boolean getInsertMessageIntoPim()
    { return mSettings.getBoolean(PROP_INSERT_MESSAGE_INTO_PIM, false); }
    public void setInsertMessageIntoPim(boolean newValue)
    { mEditor.putBoolean(PROP_INSERT_MESSAGE_INTO_PIM, newValue); }
    
    public String getSignature()
    { return mSettings.getString(PROP_SIGNATURE, ""); }
    public void setSignature(String newValue)
    { mEditor.putString(PROP_SIGNATURE, newValue); }

    public String getDefaultInternationalPrefix()
    { return mSettings.getString(PROP_DEFAULT_INTERNATIONAL_PREFIX, App.italyInternationalPrefix); }
    public void setDefaultInternationalPrefix(String newValue)
    { mEditor.putString(PROP_DEFAULT_INTERNATIONAL_PREFIX, newValue); }

    public String getLastUsedProviderId()
    { return mSettings.getString(PROP_LASTUSED_PROVIDERID, ""); }
    public void setLastUsedProviderId(String newValue)
    { mEditor.putString(PROP_LASTUSED_PROVIDERID, newValue); }
    
    public String getLastUsedSubserviceId()
    { return mSettings.getString(PROP_LASTUSED_SUBSERVICEID, ""); }
    public void setLastUsedSubserviceId(String newValue)
    { mEditor.putString(PROP_LASTUSED_SUBSERVICEID, newValue); }
    
    public String getLastUsedDestination()
    { return mSettings.getString(PROP_LASTUSED_DESTINATION, ""); }
    public void setLastUsedDestination(String newValue)
    { mEditor.putString(PROP_LASTUSED_DESTINATION, newValue); }
    
    public String getLastUsedMessage()
    { return mSettings.getString(PROP_LASTUSED_MESSAGE, ""); }
    public void setLastUsedMessage(String newValue)
    { mEditor.putString(PROP_LASTUSED_MESSAGE, newValue); }
    
    public String getSmsCounterDate()
    { return mSettings.getString(PROP_SMSCOUNTER_DATE, ""); }
    public void setSmsCounterDate(String newValue)
    { mEditor.putString(PROP_SMSCOUNTER_DATE, newValue); }
    
    public int getSmsCounterNumberForCurrentDay()
    { return mSettings.getInt(PROP_SMSCOUNTER_NUMBERFORCURRENTDAY, 0); }
    public void setSmsCounterNumberForCurrentDay(int newValue)
    { mEditor.putInt(PROP_SMSCOUNTER_NUMBERFORCURRENTDAY, newValue); }
    
    public int getSmsTotalNumber()
    { return mSettings.getInt(PROP_SMSCOUNTER_TOTAL, 0); }
    public void setSmsTotalNumber(int newValue)
    { mEditor.putInt(PROP_SMSCOUNTER_TOTAL, newValue); }

	public String[] getMessageTemplates()
	{ return mSettings.getString(PROP_MESSAGETEMPLATES, "").split(TEMPLATES_SEPARATOR); }
	public void setMessageTemplates(String[] newValue)
	{ mEditor.putString(PROP_MESSAGETEMPLATES, RainbowStringHelper.join(newValue, TEMPLATES_SEPARATOR)); }
    

    //---------- Protected Methods

    /* (non-Javadoc)
	 * @see com.angurialab.postino.data.BasePreferencesDao#backupProperties()
	 */
	@Override
	protected void backupProperties(SharedPreferences.Editor editorBackup)
	{
    	editorBackup.putBoolean(PROP_AUTO_CLEAR_MESSAGE, getAutoClearMessage());
    	editorBackup.putBoolean(PROP_INSERT_MESSAGE_INTO_PIM, getInsertMessageIntoPim());
    	editorBackup.putString(PROP_SIGNATURE, getSignature());
    	editorBackup.putString(PROP_DEFAULT_INTERNATIONAL_PREFIX, getDefaultInternationalPrefix());
    	editorBackup.putString(PROP_LASTUSED_PROVIDERID, getLastUsedProviderId());
    	editorBackup.putString(PROP_LASTUSED_SUBSERVICEID, getLastUsedSubserviceId());
    	editorBackup.putString(PROP_LASTUSED_DESTINATION, getLastUsedDestination());
    	editorBackup.putString(PROP_LASTUSED_MESSAGE, getLastUsedMessage());
	}

	/* (non-Javadoc)
	 * @see com.angurialab.postino.data.BasePreferencesDao#restoreProperties()
	 */
	@Override
	protected void restoreProperties(SharedPreferences settingsBackup)
	{
    	setAutoClearMessage(settingsBackup.getBoolean(PROP_AUTO_CLEAR_MESSAGE, false));
    	setInsertMessageIntoPim(settingsBackup.getBoolean(PROP_INSERT_MESSAGE_INTO_PIM, false));
    	setSignature(settingsBackup.getString(PROP_SIGNATURE, ""));
    	setDefaultInternationalPrefix(settingsBackup.getString(PROP_DEFAULT_INTERNATIONAL_PREFIX, ""));
    	setLastUsedProviderId(settingsBackup.getString(PROP_LASTUSED_PROVIDERID, ""));
    	setLastUsedSubserviceId(settingsBackup.getString(PROP_LASTUSED_SUBSERVICEID, ""));
    	setLastUsedDestination(settingsBackup.getString(PROP_LASTUSED_DESTINATION, ""));
    	setLastUsedMessage(settingsBackup.getString(PROP_LASTUSED_MESSAGE, ""));
	}
}