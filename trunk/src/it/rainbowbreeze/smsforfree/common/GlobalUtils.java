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
	public static SmsProvider findProviderInList(List<SmsProvider> list, String id)
	{
		if (TextUtils.isEmpty(id)) return null;
		
		for(SmsProvider provider : list) {
			if (id.equals(provider.getId())) return provider;
		}
		return null;
	}
	
	public static SmsService findSubserviceInList(SmsProvider provider, String subserviceId)
	{
		if (TextUtils.isEmpty(subserviceId)) return null;
		if (null == provider) return null;

		return provider.getSubservice(subserviceId);
	}

	public static SmsService findTemplateInList(SmsProvider provider, String templateId)
	{
		if (TextUtils.isEmpty(templateId)) return null;
		if (null == provider) return null;

		return provider.getTemplate(templateId);
	}

	//---------- Private methods

}
