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

import java.util.ArrayList;
import java.util.List;

import com.jacksms.android.data.SendService;
import com.jacksms.android.data.SendServiceList;
import com.jacksms.android.gui.ComposeMessageActivity;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.AppEnv;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.helper.GlobalHelper;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ActTemplatesList
	extends ListActivity
{
	//---------- Private fields
    protected final static String LOG_HASH = "ActTemplatesList";
	private SmsProvider mProvider;
	private LogFacility mLogFacility;
	private List<SendService> mTemplates;
	
	
	//---------- Public properties

	
	
	
	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        mLogFacility = AppEnv.i(getBaseContext()).getLogFacility();
        mLogFacility.logStartOfActivity(LOG_HASH, this.getClass(), savedInstanceState);

		getDataFromIntent(getIntent());
        setTitle(String.format(
        		getString(R.string.acttemplateslist_title),
        		AppEnv.i(getBaseContext()).getAppDisplayName()));
		setContentView(R.layout.acttemplateslist);
		
		if (null == mProvider) return;
		SendServiceList sendServiceList = new SendServiceList(this, mProvider.getAllTemplates(), false, false);
		sendServiceList.setTemplateMode(true);
		mTemplates = sendServiceList.getSimpleList();

		setListAdapter(new ArrayAdapter<SendService>(this, R.layout.user_service_item, mTemplates){

			@Override
			public View getView(int position, View convertView,
					ViewGroup parent) {
				if(convertView==null)
					convertView = ActTemplatesList.this.getLayoutInflater().inflate(R.layout.user_service_item, null);
				ImageView iconView = (ImageView)convertView.findViewById(R.id.service_icon);
				TextView nameView = (TextView)convertView.findViewById(R.id.service_name);
				iconView.setImageDrawable(mTemplates.get(position).getIcon());
				nameView.setText(mTemplates.get(position).getName());
				
				return convertView;
			}});
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String service_id = mTemplates.get(position).getId();
		
		//return to caller activity the template id
		Intent intent = new Intent();
		intent.putExtra(ActivityHelper.INTENTKEY_SMSTEMPLATEID, service_id);
		setResult(RESULT_OK, intent);
		finish();
	}

	
	
	
	//---------- Public methods

	
	
	
	//---------- Private methods
	private void getDataFromIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		//checks if intent 
		if(extras != null) {
			String id = extras.getString(ActivityHelper.INTENTKEY_SMSPROVIDERID);
			mProvider = GlobalHelper.findProviderInList(AppEnv.i(getBaseContext()).getProviderList(), id);
		} else {
			mProvider = null;
		}
	}

}
