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

package it.rainbowbreeze.smsforfree.common;

public class GlobalDef {
	/** keys for application preferences */
	public static final String appPreferencesKeys = "SmsForFreePrefs"; 
	
	/** Application version, displayed to the user */
	public static final String appVersionDescription = "1.3";

	/** Application version, for internal use */
	public static final String appVersion = "01.03.00";

	/** file name for providers preferences */
	public static final String jacksmsParametersFileName = "jacksms_parameters.xml"; 
	/** file name for providers templates list */
	public static final String jacksmsmTemplatesFileName = "jascksms_templates.xml";
	/** file name for providers subservices list */
	public static final String jacksmsSubservicesFileName = "jacksms_subservices.xml"; 

	/** file name for providers preferences */
	public static final String aimonParametersFileName = "aimon_parameters.xml"; 
	
	/** file name for providers preferences */
	public static String voipstuntParametersFileName = "voipstunt_parameters.xml";

	public static String subitosmsParametersFileName = "subitosms_parameters.xml";

	/** international prefix for Italy */
	public static final String italyInternationalPrefix = "+39";
	
	/** url where send statistics about device */
	public static final String statisticsUrl = "http://www.rainbowbreeze.it/devel/getlatestversion.php";
	
	/** string for lite version */
	public static final String lite_description = "Lite";

}
