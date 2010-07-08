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

package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.SmsForFreeApplication;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * @author rainbowbreeze
 *
 */
public class ActProvidersList
	extends ListActivity
{
	//---------- Private fields

	
	
	
	//---------- Public properties

	
	
	
	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setTitle(String.format(
        		getString(R.string.actproviderslist_title), SmsForFreeApplication.instance().getAppName()));
		setContentView(R.layout.acttemplateslist);

		setListAdapter(new ArrayAdapter<SmsProvider>(this, 
	              android.R.layout.simple_list_item_1, SmsForFreeApplication.instance().getProviderList()));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		SmsProvider provider = SmsForFreeApplication.instance().getProviderList().get(position);
		
		ActivityHelper.openSettingsSmsService(this, provider.getId());
	}

	
	
	
	//---------- Public methods

	
	
	
	//---------- Private methods

}
