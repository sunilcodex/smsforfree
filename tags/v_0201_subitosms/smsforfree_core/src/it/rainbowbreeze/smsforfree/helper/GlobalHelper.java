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

package it.rainbowbreeze.smsforfree.helper;

import java.util.List;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import it.rainbowbreeze.libs.helper.RainbowGlobalHelper;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;

public class GlobalHelper extends RainbowGlobalHelper {
	//---------- Private fields




	//---------- Public properties




	//---------- Public methods
	public static SmsProvider findProviderInList(List<SmsProvider> providers, String id)
	{
		if (TextUtils.isEmpty(id)) return null;
		
		for(SmsProvider provider : providers) {
			if (id.equals(provider.getId())) return provider;
		}
		return null;
	}
	
	public static int findProviderPositionInList(List<SmsProvider> providers, String id)
	{
		if (TextUtils.isEmpty(id) || null == providers) return -1;
		
		for(int i = 0; i < providers.size(); i++) {
			if (id.equals(providers.get(i).getId())) return i;
		}
		return -1;
	}
	
	
	public static String getMyPhoneNumber(Context context) {
		try {
	    	TelephonyManager mTelephonyMgr;
	    	mTelephonyMgr = (TelephonyManager)
	    		context.getSystemService(Context.TELEPHONY_SERVICE); 
	    	return mTelephonyMgr.getLine1Number();
		} catch (Exception ex) {
			return null;
		}
	}
	

	
	//---------- Private methods

}
