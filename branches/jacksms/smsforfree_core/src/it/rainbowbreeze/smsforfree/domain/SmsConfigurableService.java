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

public class SmsConfigurableService
	extends SmsService
{
	//---------- Private fields


	
	
	//---------- Constructors
	public SmsConfigurableService(
			LogFacility logFacility,
			int numberOfParameters) {
		super(logFacility, numberOfParameters);
		mNumberOfParameters = numberOfParameters;
	}
	
	public SmsConfigurableService(
			LogFacility logFacility,
			String id, 
			String name,
			int maxLen,
			String[] parametersDesc) {
		this(logFacility, parametersDesc.length);
		setId(id);
		setName(name);
		setMaxMessageLenght(maxLen);
		for (int i = 0; i < parametersDesc.length; i++) {
			setParameterDesc(i, parametersDesc[i]);
		}
	}
	
	public SmsConfigurableService(
			LogFacility logFacility,
			String id,
			String templateId,
			String name,
			String[] parametersValue){
		this(logFacility, parametersValue.length);
		setId(id);
		setTemplateId(templateId);
		setName(name);
		for (int i = 0; i < parametersValue.length; i++) {
			setParameterValue(i, parametersValue[i]);
		}
	}

	
	
	
	//---------- Public properties
	
	/** The id of the service */
	protected String mId;
	@Override
	public String getId()
	{ return mId; }
	public void setId(String value)
	{ mId = value; }
	
	/** The display name of the service */
	protected String mName;
	@Override
	public String getName()
	{ return mName; }
	public void setName(String value)
	{ mName = value; }
	
	/** Number of parameters of service */
	protected int mNumberOfParameters;
	@Override
	public int getParametersNumber()
	{ return mNumberOfParameters; }

	protected int mMaxMessageLenght;
	@Override
	public int getMaxMessageLenght()
	{ return mMaxMessageLenght; }
	public void setMaxMessageLenght(int value)
	{ mMaxMessageLenght = value; }

	
	
	
	//---------- Public methods

	
	
	
	//---------- Private methods

}
