package it.rainbowbreeze.smsforfree.domain;

import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.ProviderDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;

public abstract class SmsMultiProvider
	extends SmsProvider
{
	//---------- Ctors
	protected SmsMultiProvider(ProviderDao dao, int numberOfParameters) {
		super(dao, numberOfParameters);
		mTemplates = new ArrayList<SmsService>();
		mSubservices = new ArrayList<SmsService>();
	}

	
	
	
	//---------- Private fields

	
	
	
	//---------- Public properties

	@Override
	public boolean hasSubServices()
	{ return true; }

	@Override
	public boolean hasSubServicesToConfigure()
	{ return true; }

    protected List<SmsService> mTemplates;
    @Override
	public List<SmsService> getAllTemplates()
	{ return mTemplates; }
	public void setAllTemplateSubservices(List<SmsService> value)
	{ mTemplates = value; }
    @Override
    public SmsService getTemplate(String templateId)
    { return findServiceInList(mTemplates, templateId); }
	
    protected List<SmsService> mSubservices;
    @Override
    public List<SmsService> getAllSubservices()
    { return mSubservices; }
    public void setAllConfiguredSubservices(List<SmsService> value)
    { mSubservices = value; }
    @Override
	public SmsService getSubservice(String subserviceId)
    { return findServiceInList(mSubservices, subserviceId); }
    
    protected SmsService mSelectedService;
    public SmsService getSelectedSubservice()
    { return mSelectedService; }
    @Override
    public void setSelectedSubservice(String subserviceId) {
		mSelectedService = null;
		if (TextUtils.isEmpty(subserviceId)) return;
		for (SmsService service : mSubservices){
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
	
	/**
	 * Generally, no additional command are provided when editing
	 * subservices list
	 */
	@Override
	public List<SmsServiceCommand> getSubservicesListActivityCommands()
	{ return null; }
	



	//---------- Public methods
	
	@Override
	public ResultOperation<Void> initProvider(Context context) {
		ResultOperation<Void> res;
		
		res = loadParameters(context);
		if (res.HasErrors()) return res;
		res = loadTemplates(context);
		if (res.HasErrors()) return res;
		res = loadSubservices(context);
		if (res.HasErrors()) return res;
		
		return res;
	}
	
	public ResultOperation<Void> saveSubservices(Context context) {
		adjustSubservicesIds();
		Collections.sort(mSubservices);
		return mDao.saveProviderSubservices(context, getSubservicesFileName(), this);
	}
	
	public SmsService newSubserviceFromTemplate(String templateId) {
		//find the template
		SmsService template = getTemplate(templateId);
		if (null == template) return null;
		
		//create new services and config it
		SmsConfigurableService subservice = new SmsConfigurableService(template.getParametersNumber());
		subservice.setId(NEWSERVICEID);
		subservice.setMaxMessageLenght(template.getMaxMessageLenght());
		subservice.setName(template.getName());
		subservice.setTemplateId(templateId);
		for(int i = 0; i < template.getParametersNumber(); i++)
			subservice.setParameterDesc(i, template.getParameterDesc(i));
		
		return subservice;
	}
	
	@Override
	public boolean hasServiceParametersConfigured(String serviceId)
	{
		SmsService subservice = findServiceInList(getAllSubservices(), serviceId);
		if (null == subservice) return false;
		return subservice.hasParametersConfigured();
	}
	
	@Override
	public boolean hasTemplatesConfigured()
	{
		if (null == getAllTemplates()) return false;
		return getAllTemplates().size() > 0;
	}	
	


	//---------- Private methods
	protected ResultOperation<Void> loadTemplates(Context context){
		return mDao.loadProviderTemplates(context, getTemplatesFileName(), this);
	}

	protected ResultOperation<Void> saveTemplates(Context context){
		return mDao.saveProviderTemplates(context, getTemplatesFileName(), this);
	}

	protected ResultOperation<Void> loadSubservices(Context context) {
		return mDao.loadProviderSubservices(context, getSubservicesFileName(), this);
	}

	protected SmsService findServiceInList(List<SmsService> list, String serviceId) {
		if (TextUtils.isEmpty(serviceId)) return null;
		for (SmsService service : list){
			if (serviceId.equals(service.getId())) return service;
		}
		return null;
	}

	/**
	 * Finds all the subservices with id equals to SmsService.NEWSERVICEID and
	 * calculates new ids for these subservices. Use a numeric progression
	 */
	protected void adjustSubservicesIds() {
		//find the max a the ids
		int max = Integer.parseInt(NEWSERVICEID);
		for(SmsService service : mSubservices) {
			int newId = Integer.parseInt(service.getId());
			if (max < newId ) max = newId;
		}
		
		//now, rescan the list and set the undefined id
		for(SmsService service : mSubservices) {
			if (NEWSERVICEID.equals(service.getId())){
				max++;
				((SmsConfigurableService) service).setId(String.valueOf(max));
			}
		}
	}

}
