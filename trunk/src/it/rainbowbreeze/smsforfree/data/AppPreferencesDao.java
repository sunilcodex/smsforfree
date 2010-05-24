package it.rainbowbreeze.smsforfree.data;

import it.rainbowbreeze.smsforfree.common.GlobalDef;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class AppPreferencesDao
	extends BasePreferencesDao
{
	//---------- Private fields
	
    private static final String PROP_APPVERSION = "appVersion";
    private static final String PROP_AUTO_CLEAR_MESSAGE = "clearmessage";
    private static final String PROP_INSERT_MESSAGE_INTO_PIM = "insertmessageintopim";
    private static final String PROP_SIGNATURE = "signature";

    private static final String PROP_LASTUSED_PROVIDERID = "lastusedProvider";
    private static final String PROP_LASTUSED_SUBSERVICEID = "lastusedSubservice";
    private static final String PROP_LASTUSED_DESTINATION = "lastusedDestination";
    private static final String PROP_LASTUSED_MESSAGE = "lastusedMessage";
    
    

	//---------- Public Properties

    private static AppPreferencesDao mInstance;
    public static AppPreferencesDao instance()
    {
    	if (null == mInstance)
    		mInstance = new AppPreferencesDao();
    	return mInstance;
    }




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

    public String getAppVersion()
    {
    	String appVersion = mSettings.getString(PROP_APPVERSION, "");
    	//very first load of application
    	if (TextUtils.isEmpty(appVersion)) {
    		//set the app version
    		appVersion = GlobalDef.appVersion;
    		//and store it
    		setAppVersion(appVersion);
    		save();
    	}
    	return appVersion;
	}
    public void setAppVersion(String newValue)
    { mEditor.putString(PROP_APPVERSION, newValue); }
    
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
    
    

    //---------- Protected Methods

    /* (non-Javadoc)
	 * @see com.angurialab.postino.data.BasePreferencesDao#backupProperties()
	 */
	@Override
	protected void backupProperties(SharedPreferences.Editor editorBackup)
	{
    	editorBackup.putBoolean(PROP_AUTO_CLEAR_MESSAGE, getAutoClearMessage());
    	editorBackup.putBoolean(PROP_INSERT_MESSAGE_INTO_PIM, getInsertMessageIntoPim());
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
    	setLastUsedProviderId(settingsBackup.getString(PROP_LASTUSED_PROVIDERID, ""));
    	setLastUsedSubserviceId(settingsBackup.getString(PROP_LASTUSED_SUBSERVICEID, ""));
    	setLastUsedDestination(settingsBackup.getString(PROP_LASTUSED_DESTINATION, ""));
    	setLastUsedMessage(settingsBackup.getString(PROP_LASTUSED_MESSAGE, ""));
	}
    
	
	/* (non-Javadoc)
	 * @see com.angurialab.postino.data.BasePreferencesDao#getPreferencesKey()
	 */
	@Override
	protected String getPreferencesKey() {
		return GlobalDef.appPreferencesKeys;
	}
}