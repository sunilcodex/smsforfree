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
	public boolean hasTemplatesConfigured()
	{ return false; }
	
	@Override
	public List<SmsService> getAllTemplates()
	{ return null; }
	
	@Override
	public SmsService getTemplate(String templateId)
	{ return null; }

	@Override
	public List<SmsService> getAllSubservices()
	{ return null; }

	@Override
	public SmsService getSubservice(String subserviceId)
	{ return null; }

	@Override
	public boolean hasSubServices() 
	{ return false; }
	
	@Override
	public void setSelectedSubservice(String subserviceId)
	{ }
	
	@Override
	public boolean hasServiceParametersConfigured(String serviceId)
	{ return false; }	

	
	/** No subservices, so no additional command to show */
	@Override
	public List<SmsServiceCommand> getSubservicesListActivityCommands()
	{ return null; }
	
	
	//---------- Public methods
	public ResultOperation saveTemplates(Context context)
	{ return null; }

	public ResultOperation loadTemplates(Context context)
	{ return null; }

	public ResultOperation saveSubservices(Context context)
	{ return null; }

	public ResultOperation loadSubservices(Context context)
	{ return null; }
	
	public SmsService newSubserviceFromTemplate(String templateId)
	{ return null; }

	
	
	
	//---------- Private methods
	
}
