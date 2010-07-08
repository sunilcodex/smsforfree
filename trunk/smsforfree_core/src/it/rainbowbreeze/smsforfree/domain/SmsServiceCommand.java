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

/**
 * Define a command the provider can implement
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class SmsServiceCommand
{
	//---------- Ctors

	public SmsServiceCommand(
			int commandId,
			String commandDescription,
			int commandOrder)
	{
		this(commandId, commandDescription, commandOrder, NO_ICON);
	}

	public SmsServiceCommand(
			int commandId,
			String commandDescription,
			int commandOrder,
			int commandIcon)
	{
		mCommandId = commandId;
		mCommandDescription = commandDescription;
		mCommandOrder = commandOrder;
		mCommandIcon = commandIcon;
	}
	
	
	
	//---------- Private fields
	public static final int NO_ICON = -1;


	
	
	
	//---------- Public properties
	protected int mCommandId;
	public int getCommandId()
	{ return mCommandId; }

	protected String mCommandDescription;
	public String getCommandDescription()
	{ return mCommandDescription; }

	protected int mCommandOrder;
	public int getCommandOrder()
	{ return mCommandOrder; }

	protected int mCommandIcon;
	public int getCommandIcon()
	{ return mCommandIcon; }


	
	//---------- Public methods
	public boolean hasIcon()
	{ return NO_ICON != mCommandIcon; }
	
	
	
	//---------- Private methods

}
