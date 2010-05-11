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

    protected List<SmsService> mTemplateSubservices;
    @Override
	public List<SmsService> getAllTemplateSubservices()
	{ return mTemplateSubservices; }
    @Override
    public SmsService getTemplateSubservice(String templateId)
    { return findServiceInList(mTemplateSubservices, templateId); }
	
    protected List<SmsService> mConfiguredSubservice;
    @Override
    public List<SmsService> getAllConfiguredSubservices()
    { return mConfiguredSubservice; }
    @Override
	public SmsService getConfiguredSubservice(String subserviceId)
    { return findServiceInList(mConfiguredSubservice, subserviceId); }
    
    protected SmsService mSelectedService;
    public SmsService getSelectedSubservice()
    { return mSelectedService; }
    @Override
    public void setSelectedSubservice(String subserviceId) {
		mSelectedService = null;
		if (TextUtils.isEmpty(subserviceId)) return;
		for (SmsService service : mConfiguredSubservice){
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
	protected SmsService findServiceInList(List<SmsService> list, String serviceId) {
		if (TextUtils.isEmpty(serviceId)) return null;
		for (SmsService service : list){
			if (serviceId.equals(service.getId())) return service;
		}
		return null;
	}
	
}
