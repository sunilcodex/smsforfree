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

import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.ui.ActivityHelper;

import java.util.List;


import android.content.Context;

public abstract class SmsSingleProvider
	extends SmsProvider
{
	//---------- Ctors
	protected SmsSingleProvider(
			LogFacility logFacility,
			int numberOfParameters,
			AppPreferencesDao appPreferencesDao,
			ProviderDao providerDao,
			ActivityHelper activityHelper) {
		super(logFacility, numberOfParameters, appPreferencesDao, providerDao, activityHelper);
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
	public boolean hasSubServicesToConfigure() 
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
	
	public ResultOperation<Void> saveSubservices(Context context)
	{ return null; }
	
	public SmsService integrateSubserviceWithTemplateData(SmsConfigurableService originalService, String templateId)
	{ return null; }
	
	


	//---------- Private methods
	protected ResultOperation<Void> saveTemplates(Context context)
	{ return null; }

	protected ResultOperation<Void> loadTemplates(Context context)
	{ return null; }

	protected ResultOperation<Void> loadSubservices(Context context)
	{ return null; }

	protected String getSubservicesFileName()
	{ return null; }

	protected String getTemplatesFileName()
	{ return null; }

}
