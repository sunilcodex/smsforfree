package it.rainbowbreeze.smsforfree.data;

import it.rainbowbreeze.smsforfree.common.Def;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class JacksmsPreferencesDao
	extends BasePreferencesDao
{
	//---------- Private fields
    private static final String PROP_USER_NAME = "username";
    private static final String PROP_PASSWORD = "password";

    
    

	//---------- Public properties
    private static JacksmsPreferencesDao mInstance;
    public static JacksmsPreferencesDao instance()
    {
    	if (null == mInstance)
    		mInstance = new JacksmsPreferencesDao();
    	return mInstance;
    }

    
    
    
	//---------- Public methods
    public String getUserName()
    { return settings.getString(PROP_USER_NAME, ""); }
    public void setUserName(String newValue)
    { editor.putString(PROP_USER_NAME, newValue); }
    
    public String getPassword()
    { return settings.getString(PROP_PASSWORD, ""); }
    public void setPassword(String newValue)
    { editor.putString(PROP_PASSWORD, newValue); }

    
    
    
	//---------- Private methods
    /* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree,data.BasePreferencesDao#backupProperties()
	 */
	@Override
	protected void backupProperties(Editor editorBackup) {
    	editorBackup.putString(PROP_USER_NAME, getUserName());
    	editorBackup.putString(PROP_PASSWORD, getPassword());
	}

    /* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree,data.BasePreferencesDao#restoreProperties()
	 */
	@Override
	protected void restoreProperties(SharedPreferences settingsBackup) {
    	setUserName(settingsBackup.getString(PROP_USER_NAME, ""));
    	setPassword(settingsBackup.getString(PROP_PASSWORD, ""));
	}
    
    /* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree,data.BasePreferencesDao#getPreferencesKey()
	 */
	@Override
	protected String getPreferencesKey() {
		// TODO Auto-generated method stub
		return Def.SharedPreferencesKeys;
	}
}
