/**
 * Copyright (C) 2010 Alfredo Morresi
 * 
 * This file is part of SmsForFree project.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/>.
 */

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
		if (res.hasErrors()) return res;
		res = loadTemplates(context);
		if (res.hasErrors()) return res;
		res = loadSubservices(context);
		if (res.hasErrors()) return res;
		
		return res;
	}
	
	public ResultOperation<Void> saveSubservices(Context context) {
		adjustSubservicesIds();
		Collections.sort(mSubservices);
		return mDao.saveProviderSubservices(context, getSubservicesFileName(), this);
	}
	
	public SmsService integrateSubserviceWithTemplateData(SmsConfigurableService originalService, String templateId) {
		//find the template
		SmsService template = getTemplate(templateId);
		if (null == template) return null;
		
		SmsConfigurableService subservice;
		if (null == originalService) {
			//create new services and configure it
			subservice = new SmsConfigurableService(template.getParametersNumber());
			subservice.setId(NEWSERVICEID);
		} else {
			subservice = originalService;
		}
		if (0 == subservice.getMaxMessageLenght()) subservice.setMaxMessageLenght(template.getMaxMessageLenght());
		if (TextUtils.isEmpty(subservice.getName())) subservice.setName(template.getName());
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
