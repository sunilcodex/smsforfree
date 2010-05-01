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
	public List<SmsService> getAllSubservice() {
		return null;
	}

	@Override
	public List<SmsConfigurableService> getAllConfiguredSubservice() {
		return null;
	}

	@Override
	public SmsService getSubservice(String subserviceId) {
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
