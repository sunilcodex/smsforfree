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

package it.rainbowbreeze.smsforfree.util;

/**
 * Common constrains used in the tests
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public interface Def
{
	public String TEST_DESTINATION = "XXXX";

	public String AIMON_USERNAME = "f.martinelli@aimon.it";
	public String AIMON_PASSWORD = "smsf0rfr33";
	public String AIMON_SENDER = "3912345678";
	
	public String SUBITOSMS_USERNAME = "fmartinelli";
	public String SUBITOSMS_PASSWORD = "outtura";
	public String SUBITOSMS_SENDER = "3912345678";
	
	public String JACKSMS_USERNAME = "f.martinelli";
	public String JACKSMS_PASSWORD = "smsf0rfr33";
	
	public String VOIPSTUNT_USERNAME = "f.martinelli";
	public String VOIPSTUNT_PASSWORD = "smsf0rfr33";
	public String VOIPSTUNT_SENDER = "3912345678";
}
