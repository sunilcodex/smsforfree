package it.rainbowbreeze.smsforfree.common;

import java.util.List;

import android.text.TextUtils;

import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsService;

public class GlobalUtils {
	//---------- Ctors

	//---------- Private fields

	//---------- Public properties

	//---------- Events

	//---------- Public methods
	public static SmsService findProviderInList(List<SmsProvider> list, String id)
	{
		if (TextUtils.isEmpty(id)) return null;
		
		for(SmsService service : list) {
			if (id.equals(service.getId())) return service;
		}
		return null;
	}

	//---------- Private methods

}
