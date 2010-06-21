/**
 * 
 */
package it.rainbowbreeze.smsforfree.logic;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.SmsForFreeApplication;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.data.WebserviceClient;
import it.rainbowbreeze.smsforfree.ui.ActivityHelper;
import android.app.Activity;
import android.os.AsyncTask;

/**
 * Send info about the app version and OS where installed and screen resolution
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class SendStatisticsAsyncTask
	extends AsyncTask<Activity, Void, Void>
{
	//---------- Private fields

	//---------- Public properties
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground(Activity... params)
	{
		Activity activity = params[0];
		String screenres = ActivityHelper.getScreenWidth(activity) + "x" + ActivityHelper.getScreenHeight(activity);
		String appName = SmsForFreeApplication.instance().getAppName();
		if (SmsForFreeApplication.instance().isLiteVersionApp()) appName = appName + "-" + GlobalDef.lite_description;
		
		//prepare data to send
		StringBuilder url = new StringBuilder();
		url.append(GlobalDef.statisticsUrl)
			.append("?")
			.append("unique=")
			.append(AppPreferencesDao.instance().getUniqueId())
			.append("&")
			.append("swname=")
			.append(appName)
			.append("&")
			.append("ver=")
			.append(GlobalDef.appVersion)
			.append("&")
			.append("sover=")
			.append("android-"+ android.os.Build.VERSION.SDK_INT)
			.append("&")
			.append("sores=")
			.append(screenres);
		
		WebserviceClient client = new WebserviceClient();
		try {
			client.requestGet(url.toString());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//doen't care about results or other
		return null;
	}



	//---------- Public methods

	//---------- Private methods

}
