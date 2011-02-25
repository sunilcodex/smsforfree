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

public class ContactPhone {
	//---------- Ctors
	public ContactPhone()
	{}
	
	public ContactPhone(String type, String number) {
		setType(type);
		setNumber(number);
	}
	
	
	
	
	//---------- Private fields

	
	
	
	//---------- Public properties
	private String mType;
	public String getType()
	{ return mType; }
	public void setType(String value)
	{ mType = value; }

	private String mNumber;
	public String getNumber()
	{ return mNumber; }
	public void setNumber(String value)
	{ mNumber = value; }


	
	
	//---------- Public methods

	
	
	
	
	//---------- Private methods

}
