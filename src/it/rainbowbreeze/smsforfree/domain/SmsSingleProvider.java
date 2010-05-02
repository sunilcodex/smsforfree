package it.rainbowbreeze.smsforfree.domain;

import java.util.List;

public abstract class SmsSingleProvider
	extends SmsProvider
{
	//---------- Ctors
	protected SmsSingleProvider()
	{ super(); };
	
	protected SmsSingleProvider(int numberOfParameters) {
		super(numberOfParameters);
	}
	
	
	
	
	//---------- Private fields


	
	
	//---------- Public properties
	@Override
	public List<SmsService> getTemplateSubservices() {
		return null;
	}

	@Override
	public List<SmsService> getConfiguredSubservices() {
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

	//---------- Private methods
	
}
