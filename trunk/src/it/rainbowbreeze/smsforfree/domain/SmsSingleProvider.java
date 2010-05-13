package it.rainbowbreeze.smsforfree.domain;

import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.ProviderDao;

import java.util.List;


import android.content.Context;

public abstract class SmsSingleProvider
	extends SmsProvider
{
	//---------- Ctors
	protected SmsSingleProvider(ProviderDao dao, int numberOfParameters) {
		super(dao, numberOfParameters);
	}
	
	
	
	
	//---------- Private fields


	
	
	//---------- Public properties
	@Override
	public List<SmsService> getAllTemplateSubservices() {
		return null;
	}
	@Override
	public SmsService getTemplateSubservice(String templateId) {
		return null;
	}

	@Override
	public List<SmsService> getAllConfiguredSubservices() {
		return null;
	}

	@Override
	public SmsService getConfiguredSubservice(String subserviceId) {
		return null;
	}

	@Override
	public boolean hasSubServices() {
		return false;
	}
	
	@Override
	public void setSelectedSubservice(String subserviceId)
	{ }

	
	
	
	//---------- Public methods
	public ResultOperation saveTemplates(Context context)
	{ return null; }

	public ResultOperation loadTemplates(Context context)
	{ return null; }

	public ResultOperation saveSubservices(Context context)
	{ return null; }

	public ResultOperation loadSubservices(Context context)
	{ return null; }

	
	
	
	//---------- Private methods
	
}
