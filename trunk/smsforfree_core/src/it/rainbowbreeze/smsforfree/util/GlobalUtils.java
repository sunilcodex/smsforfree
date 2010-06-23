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
