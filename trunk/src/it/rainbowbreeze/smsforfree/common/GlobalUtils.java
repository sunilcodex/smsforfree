package it.rainbowbreeze.smsforfree.common;

import java.util.List;

import android.text.TextUtils;

import it.rainbowbreeze.smsforfree.domain.SmsProvider;

public class GlobalUtils {
	//---------- Ctors

	//---------- Private fields

	//---------- Public properties

	//---------- Events

	//---------- Public methods
	public static SmsProvider findProviderInList(List<SmsProvider> list, String id)
	{
		if (TextUtils.isEmpty(id)) return null;
		
		for(SmsProvider provider : list) {
			if (id.equals(provider.getId())) return provider;
		}
		return null;
	}
	
	//---------- Private methods

}
