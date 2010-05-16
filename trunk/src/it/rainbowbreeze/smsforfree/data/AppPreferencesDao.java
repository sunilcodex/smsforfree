package it.rainbowbreeze.smsforfree.data;

import it.rainbowbreeze.smsforfree.common.GlobalDef;
import android.content.SharedPreferences;

public class AppPreferencesDao	extends BasePreferencesDao
{
	//---------- Private fields
	
    private static final String PROP_AUTO_CLEAR_MESSAGE = "clearmessage";
    private static final String PROP_INSERT_MESSAGE_INTO_PIM = "insertmessageintopim";
    private static final String PROP_USE_LAST_PROVIDER = "uselastprovider";
    private static final String PROP_PREFERRED_PROVIDER = "preferredprovider";
    private static final String PROP_SIGNATURE = "signature";

    
    

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
    
    public boolean getUseLastProvider()
    { return mSettings.getBoolean(PROP_USE_LAST_PROVIDER, false); }
    public void setUseLastProvider(boolean newValue)
    { mEditor.putBoolean(PROP_USE_LAST_PROVIDER, newValue); }
    
    public String getPreferredProvider()
    { return mSettings.getString(PROP_PREFERRED_PROVIDER, ""); }
    public void setPreferredProvider(String newValue)
    { mEditor.putString(PROP_PREFERRED_PROVIDER, newValue); }

    public String getSignature()
    { return mSettings.getString(PROP_SIGNATURE, ""); }
    public void setSignature(String newValue)
    { mEditor.putString(PROP_SIGNATURE, newValue); }

    
    

    //---------- Protected Methods

    /* (non-Javadoc)
	 * @see com.angurialab.postino.data.BasePreferencesDao#backupProperties()
	 */
	@Override
	protected void backupProperties(SharedPreferences.Editor editorBackup)
	{
    	editorBackup.putBoolean(PROP_AUTO_CLEAR_MESSAGE, getAutoClearMessage());
    	editorBackup.putBoolean(PROP_INSERT_MESSAGE_INTO_PIM, getInsertMessageIntoPim());
    	editorBackup.putString(PROP_PREFERRED_PROVIDER, getPreferredProvider());
    	editorBackup.putBoolean(PROP_USE_LAST_PROVIDER, getUseLastProvider());
	}

	/* (non-Javadoc)
	 * @see com.angurialab.postino.data.BasePreferencesDao#restoreProperties()
	 */
	@Override
	protected void restoreProperties(SharedPreferences settingsBackup)
	{
    	setAutoClearMessage(settingsBackup.getBoolean(PROP_AUTO_CLEAR_MESSAGE, false));
    	setInsertMessageIntoPim(settingsBackup.getBoolean(PROP_INSERT_MESSAGE_INTO_PIM, false));
    	setPreferredProvider(settingsBackup.getString(PROP_PREFERRED_PROVIDER, ""));
    	setUseLastProvider(settingsBackup.getBoolean(PROP_USE_LAST_PROVIDER, false));
	}
    
	
	/* (non-Javadoc)
	 * @see com.angurialab.postino.data.BasePreferencesDao#getPreferencesKey()
	 */
	@Override
	protected String getPreferencesKey() {
		return GlobalDef.AppPreferencesKeys;
	}
}