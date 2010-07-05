package it.rainbowbreeze.smsforfree.common;

import java.util.List;

import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.logic.LogicManager;
import it.rainbowbreeze.smsforfree.ui.ActivityHelper;
import android.app.Application;

public class SmsForFreeApplication
	extends Application
{
	//---------- Constructor
	public SmsForFreeApplication()
	{
		super();
		//this is the first instruction, so no fear that mInstance is null is following calls
		mInstance = this;
	}
	
	
	
	//---------- Private fields

	
	
	
	//---------- Public properties

	//singleton
    private static SmsForFreeApplication mInstance;
    public static SmsForFreeApplication instance()
    { return mInstance; }
	
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

	/** Force the refresh of subservice list */
	protected boolean mForceSubserviceRefresh;
	public boolean getForceSubserviceRefresh()
	{ return mForceSubserviceRefresh; }
	public void setForceSubserviceRefresh(boolean newValue)
	{ mForceSubserviceRefresh = newValue; }
	
	/** the application was correctly initialized */
	protected boolean mIsCorrectlyInitialized;
	public boolean isCorrectlyInitialized()
	{ return mIsCorrectlyInitialized; }

	/** Show o don't show the ads */
	protected boolean mAdEnables;
	public boolean isAdEnables()
	{ return mAdEnables; }
	public void setAdEnables(boolean newValue)
	{ mAdEnables = newValue; }
	



	
	//---------- Events
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		//execute begin task
		ResultOperation<Void> res = LogicManager.executeBeginTask(this);
		if (res.HasErrors()) {
			mIsCorrectlyInitialized = false;
			ActivityHelper.reportError(this, res.getException(), res.getReturnCode());
		} else {
			mIsCorrectlyInitialized = true;
		}
	}
	
	
	
	@Override
	public void onTerminate()
	{
		//execute end tasks
		ResultOperation<Void> res = LogicManager.executeEndTast(this);
		if (res.HasErrors()) {
			ActivityHelper.reportError(this, res.getException(), res.getReturnCode());
		}
		super.onTerminate();
	}

	
	
	
	//---------- Public methods

	
	
	
	//---------- Private methods

}
