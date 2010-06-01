package it.rainbowbreeze.smsforfree.util;

import java.util.List;

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
		if (TextUtils.isEmpty(id)) return -1;
		
		for(int i = 0; i < list.size(); i++) {
			if (id.equals(list.get(i).getId())) return i;
		}
		return -1;
	}
	
	//---------- Private methods

}
