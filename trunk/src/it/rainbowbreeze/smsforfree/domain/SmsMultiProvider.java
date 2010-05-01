package it.rainbowbreeze.smsforfree.domain;

import java.util.List;

import android.text.TextUtils;

public abstract class SmsMultiProvider
	extends SmsProvider
{
	//---------- Ctors
	protected SmsMultiProvider()
	{ super(); };
	
	protected SmsMultiProvider(int numberOfParameters) {
		super(numberOfParameters);
	}

	
	
	
	//---------- Private fields

	
	
	
	//---------- Public properties

	@Override
	public boolean hasSubServices()
	{ return true; }

    protected List<SmsService> mAllSubservice;
    @Override
	public List<SmsService> getAllSubservice()
	{ return mAllSubservice; }
	
    @Override
	public SmsService getSubservice(String subserviceId) {
		if (TextUtils.isEmpty(subserviceId)) return null;
		for (SmsService service : mAllSubservice){
			if (subserviceId.equals(service.getId())) return service;
		}
		return null;
	}
    
    protected List<SmsConfigurableService> mConfiguredSubservice;
    @Override
    public List<SmsConfigurableService> getAllConfiguredSubservice()
    { return mConfiguredSubservice; }
    
    protected SmsService mSelectedService;
    public SmsService getSelectedSubservice()
    { return mSelectedService; }
    
    @Override
    public void setSelectedSubservice(String subserviceId) {
		mSelectedService = null;
		if (TextUtils.isEmpty(subserviceId)) return;
		for (SmsService service : mAllSubservice){
			if (subserviceId.equals(service.getId())){
				mSelectedService = service;
				return;
			}
		}
    }

	@Override
	public int getMaxMessageLenght() {
		if (null == mSelectedService){
			return 0;
		} else {
			return mSelectedService.getMaxMessageLenght();
		}
	}

	
	//---------- Public methods

	
	
	
	//---------- Private methods
	
}
