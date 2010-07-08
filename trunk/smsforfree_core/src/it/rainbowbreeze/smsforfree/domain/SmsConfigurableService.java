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

public class SmsConfigurableService
	extends SmsService
{
	//---------- Ctors
	public SmsConfigurableService(int numberOfParameters) {
		super(numberOfParameters);
		mNumberOfParameters = numberOfParameters;
	}
	
	public SmsConfigurableService(String id, String name, int maxLen, int numberOfParameters) {
		this(numberOfParameters);
		setId(id);
		setName(name);
		setMaxMessageLenght(maxLen);
	}
	
	public SmsConfigurableService(String id, String templateId, String name, int maxLen, int numberOfParameters) {
		this(numberOfParameters);
		setId(id);
		setTemplateId(templateId);
		setName(name);
		setMaxMessageLenght(maxLen);
	}
	


	
	
	
	//---------- Private fields
	
	
	
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
