package it.rainbowbreeze.smsforfree.common;

import java.util.List;

import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.logic.LogicManager;
import android.app.Application;

public class SmsForFreeApplication
	extends Application
{
	//---------- Private fields

	
	
	
	//---------- Public properties

	//singleton
    private static SmsForFreeApplication mInstance;
    public static SmsForFreeApplication instance()
    {
    	if (null == mInstance)
    		mInstance = new SmsForFreeApplication();
    	return mInstance;
    }
	
    /** List of providers */
	protected List<SmsProvider> mProviderList;
	public List<SmsProvider> getProviderList()
	{ return mProviderList; }
	public void setProviderList(List<SmsProvider> newValue)
	{ mProviderList = newValue; }
	
	/** Max allowed SMS for each day */
	protected int mAllowedSmsForDay;
	public int getAllowedSmsForDay()
	{ return mAllowedSmsForDay; }
	public void setAllowedSmsForDay(int newValue)
	{ mAllowedSmsForDay = newValue; }

	/** Application is demo, not full version */
	protected boolean mLiteVersionApp;
	public boolean isLiteVersionApp()
	{ return mLiteVersionApp; }
	public void setLiteVersionApp(boolean newValue)
	{ mLiteVersionApp = newValue; }

	/** Application is expired */
	protected boolean mAppExpired;
	public boolean isAppExpired()
	{ return mAppExpired; }
	public void setAppExpired(boolean newValue)
	{ mAppExpired = newValue; }

	/** Application name */
	protected String mAppName;
	public String getAppName()
	{ return mAppName; }
	public void setAppName(String newValue)
	{ mAppName = newValue; }



	
	//---------- Events
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		//execute begin task
		LogicManager.executeBeginTask(this);
	}
	
	
	
	@Override
	public void onTerminate() {
		//
		LogicManager.executeEndTast(this);
		super.onTerminate();
	}

	
	
	
	//---------- Public methods

	
	
	
	//---------- Private methods

}
