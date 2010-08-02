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
	public final static String appPreferencesKeys = "SmsForFreePrefs"; 
	
	/** Application version, displayed to the user */
	public final static String appVersionDescription = "1.8b";

	/** Application version, for internal use */
	public final static String appVersion = "01.08.00b";

	/** file name for providers preferences */
	public final static String jacksmsParametersFileName = "jacksms_parameters.xml"; 
	/** file name for providers templates list */
	public final static String jacksmsmTemplatesFileName = "jascksms_templates.xml";
	/** file name for providers subservices list */
	public final static String jacksmsSubservicesFileName = "jacksms_subservices.xml"; 

	/** file name for providers preferences */
	public final static String aimonParametersFileName = "aimon_parameters.xml"; 
	
	/** file name for providers preferences */
	public final static String voipstuntParametersFileName = "voipstunt_parameters.xml";

	public final static String subitosmsParametersFileName = "subitosms_parameters.xml";

	/** international prefix for Italy */
	public final static String italyInternationalPrefix = "+39";
	
	/** url where send statistics about device */
	public final static String statisticsUrl = "http://www.rainbowbreeze.it/devel/getlatestversion.php";
	
	/** string for lite version */
	public final static String lite_description = "Lite";

	/** address where send log */
	public final static String EMAIL_FOR_LOG = "devel@rainbowbreeze.it";
	
	/** tag used in the log */
	public final static String LOG_TAG = "SmsForFree";

	/** platform - dependent newline char */
	public final static String LINE_SEPARATOR = System.getProperty("line.separator");	
}
