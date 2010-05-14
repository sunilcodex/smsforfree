package it.rainbowbreeze.smsforfree.domain;

import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.ProviderDao;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;

public abstract class SmsMultiProvider
	extends SmsProvider
{
	//---------- Ctors
	protected SmsMultiProvider(ProviderDao dao, int numberOfParameters) {
		super(dao, numberOfParameters);
	}

	
	
	
	//---------- Private fields

	
	
	
	//---------- Public properties

	@Override
	public boolean hasSubServices()
	{ return true; }

    protected List<SmsService> mTemplateSubservices;
    @Override
	public List<SmsService> getAllTemplate()
	{ return mTemplateSubservices; }
	public void setAllTemplateSubservices(List<SmsService> value)
	{ mTemplateSubservices = value; }
    @Override
    public SmsService getTemplate(String templateId)
    { return findServiceInList(mTemplateSubservices, templateId); }
	
    protected List<SmsService> mConfiguredSubservice;
    @Override
    public List<SmsService> getAllSubservices()
    { return mConfiguredSubservice; }
    public void setAllConfiguredSubservices(List<SmsService> value)
    { mConfiguredSubservice = value; }
    @Override
	public SmsService getSubservice(String subserviceId)
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
	public ResultOperation loadTemplates(Context context){
		return null;
	}

	public ResultOperation saveTemplates(Context context){
		return null;
	}

	public ResultOperation loadSubservices(Context context) {
		return null;
	}

	public ResultOperation saveSubservices(Context context) {
		return null;
	}
	
	public SmsService newSubserviceFromTemplate(String templateId) {
		//find the template
		SmsService template = getTemplate(templateId);
		if (null == template) return null;
		
		//create new services and config it
		SmsConfigurableService subservice = new SmsConfigurableService(template.getParametersNumber());
		subservice.setId(NEWSUBSERVICEID);
		subservice.setName(template.getName());
		subservice.setTemplateId(templateId);
		for(int i = 0; i < template.getParametersNumber(); i++)
			subservice.setParameterDesc(i, template.getParameterDesc(i));
		
		return subservice;
	}
	

	
	
	
	//---------- Private methods
	protected SmsService findServiceInList(List<SmsService> list, String serviceId) {
		if (TextUtils.isEmpty(serviceId)) return null;
		for (SmsService service : list){
			if (serviceId.equals(service.getId())) return service;
		}
		return null;
	}
	
}
