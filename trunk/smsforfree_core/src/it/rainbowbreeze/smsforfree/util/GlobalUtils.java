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

package it.rainbowbreeze.smsforfree.util;

import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import it.rainbowbreeze.smsforfree.domain.SmsProvider;

public class GlobalUtils {
	//---------- Private fields




	//---------- Public properties




	//---------- Public methods
	public static SmsProvider findProviderInList(List<SmsProvider> list, String id)
	{
		if (TextUtils.isEmpty(id)) return null;
		
		for(SmsProvider provider : list) {
			if (id.equals(provider.getId())) return provider;
		}
		return null;
	}
	
	public static int findProviderPositionInList(List<SmsProvider> list, String id)
	{
		if (TextUtils.isEmpty(id) || null == list) return -1;
		
		for(int i = 0; i < list.size(); i++) {
			if (id.equals(list.get(i).getId())) return i;
		}
		return -1;
	}
	
	
	/**
	 * Check for the availability of network connection
	 * @param context
	 * @return
	 */
	public static boolean isConnectionAvailable(Context context)
	{
		ConnectivityManager mgr = (ConnectivityManager)
			context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = mgr.getActiveNetworkInfo();

		if (netInfo != null)
			return netInfo.isAvailable();
		else
			return false;
	}


	//---------- Private methods

}
