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

package it.rainbowbreeze.smsforfree.logic;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.common.App;
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
		LogFacility.i("Collecting statistic information about the application");
		Activity activity = params[0];
		String screenres = ActivityHelper.getScreenWidth(activity) + "x" + ActivityHelper.getScreenHeight(activity);
		String appName = App.instance().getAppName();
		if (App.instance().isLiteVersionApp()) appName = appName + "-" + GlobalDef.lite_description;
		
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
			LogFacility.e(e);
		} catch (IOException e) {
			LogFacility.e(e);
		}

		//doen't care about results or other
		return null;
	}



	//---------- Public methods

	//---------- Private methods

}
