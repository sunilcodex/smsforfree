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

import it.rainbowbreeze.libs.common.RainbowResultOperation;
import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.AppEnv;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.domain.SmsServiceCommand;
import it.rainbowbreeze.smsforfree.helper.GlobalHelper;
import it.rainbowbreeze.smsforfree.logic.ExecuteProviderCommandThread;
import it.rainbowbreeze.smsforfree.providers.JacksmsProvider;

import java.io.IOException;
import java.util.List;

import com.jacksms.android.data.SendService;
import com.jacksms.android.data.SendServiceList;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ActSubservicesList
extends ListActivity
{
	//---------- Private fields
	protected final static String LOG_HASH = "ActSubservicesList";
	private static final int CONTEXTMENU_ADDSERVICE = 1;
	private static final int CONTEXTMENU_EDITSERVICE = 2;
	private static final int CONTEXTMENU_DELETESERVICE = 3;

	private SmsProvider mProvider;
	private ServicesAdapter mAdapter;

	private ExecuteProviderCommandThread mExecutedProviderCommandThread;
	private ProgressDialog mProgressDialog;
	private LogFacility mLogFacility;
	private ActivityHelper mActivityHelper;


	private SmsService mTempService;

	//---------- Public properties




	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mLogFacility = AppEnv.i(getBaseContext()).getLogFacility();
		mActivityHelper = AppEnv.i(getBaseContext()).getActivityHelper();

		setContentView(R.layout.actsubserviceslist);

		final ListView list = getListView();
		View header = getLayoutInflater().inflate(R.layout.actsubservicelist_newservice, null);
		list.addHeaderView(header);

		getDataFromIntent(getIntent());

		if (null == mProvider)
			mProvider = AppEnv.i(getApplicationContext()).getProviderList().get(0);

		//update title
		setTitle(String.format(
				getString(R.string.actsubserviceslist_title),
				AppEnv.i(getBaseContext()).getAppDisplayName(),
				mProvider.getName()));

		List<SmsService> svList = mProvider.getAllSubservices();
		mAdapter = new ServicesAdapter(this, svList);
		setListAdapter(mAdapter);

		//register the context menu to defaul ListView of the view
		//alternative method:
		//http://www.anddev.org/creating_a_contextmenu_on_a_listview-t2438.html
		registerForContextMenu(getListView());

	}	

	@Override
	protected void onStart() {
		super.onStart();
		mExecutedProviderCommandThread = (ExecuteProviderCommandThread) getLastNonConfigurationInstance();
		if (null != mExecutedProviderCommandThread) {
			//create and show a new progress dialog
			mProgressDialog = mActivityHelper.createAndShowProgressDialog(this, 0, R.string.common_msg_executingCommand);
			//register new handler
			mExecutedProviderCommandThread.registerCallerHandler(mExecutedCommandHandler);
		}
	}

	@Override
	protected void onStop() {
		if (null != mExecutedProviderCommandThread) {
			//unregister handler from background thread
			mExecutedProviderCommandThread.registerCallerHandler(null);
		}
		super.onStop();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		//save eventually open background thread
		return mExecutedProviderCommandThread;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		boolean canContinue = super.onCreateOptionsMenu(menu);
		if (!canContinue) return canContinue;

		//checks for provider's extended commands
		if (null != mProvider && mProvider.hasSubservicesListActivityCommands()) {
			for (SmsServiceCommand command : mProvider.getSubservicesListActivityCommands()) {
				MenuItem item = menu.add(0,
						command.getCommandId(), command.getCommandOrder(), command.getCommandDescription());
				if (command.hasIcon()) item.setIcon(command.getCommandIcon());
			}
		}
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.setHeaderTitle(R.string.actsubserviceslist_mnuHeaderTitle);
		menu.add(0, CONTEXTMENU_EDITSERVICE, 0, R.string.actsubserviceslist_mnuEditService)
		.setIcon(android.R.drawable.ic_menu_edit);
		menu.add(0, CONTEXTMENU_DELETESERVICE, 1, R.string.actsubserviceslist_mnuDeleteService)
		.setIcon(android.R.drawable.ic_menu_delete);
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshSubservicesList();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case JacksmsProvider.COMMAND_LOADTEMPLATESERVICES:
			mLogFacility.i("Scaricamento template in corso");
			execureProviderCommand(mProvider, item.getItemId(), null);
			break;

		case JacksmsProvider.COMMAND_LOADUSERSERVICES:
			mLogFacility.i("Pulizia della lista e aggiornamento servizi");
			if(GlobalHelper.isNetworkAvailable(getApplicationContext())){
				execureProviderCommand(mProvider, item.getItemId(), null);
			}
			else{
				mActivityHelper.createInformativeDialog(this, "",
						getText(com.jacksms.android.R.string.connection_error).toString(), "Ok").show();
			}
			break;			
		}

		return true;
	}


	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		SmsService service;
		// This is actually where the magic happens.
		// As we use an adapter view (which the ListView is)
		// We can cast item.getMenuInfo() to AdapterContextMenuInfo 
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();

		switch (item.getItemId()) {
		case CONTEXTMENU_ADDSERVICE:
			mLogFacility.i("Scelto add service");
			addNewService();
			break;
		case CONTEXTMENU_EDITSERVICE:
			//find current selected service
			mLogFacility.i("Scelto edit service");
			service = (SmsService) getListAdapter().getItem(menuInfo.position-1);
			//and edit it
			mActivityHelper.openSettingsSmsService(this, mProvider.getId(), service.getTemplateId(), service.getId());
			break;
		case CONTEXTMENU_DELETESERVICE:
			mLogFacility.i("Scelto delete service");
			service = (SmsService) getListAdapter().getItem(menuInfo.position-1);
			mTempService = service;
			mProvider.getAllSubservices().remove(service);
			Dialog d = mActivityHelper.createYesNoDialog(this,
					"JackSms",
					"Vuoi che il servizio venga cancellato anche dall'account sul sito?",
					deleteYesListener, null);
			d.show();
			refreshSubservicesList();
			ResultOperation<Void> res = mProvider.saveSubservices(this);
			if (res.hasErrors()) {
				mActivityHelper.reportError(this, res.getException(), res.getReturnCode());
				return false;
			}
			break;
		}

		return true;
	}

	/**
	 *	Listeners needed for the YES/NO dialog
	 *	after a delete action
	 */
	Dialog.OnClickListener deleteYesListener = new Dialog.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			mProvider.removeRemoteService(mTempService);
		}
	};

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (v.getId()) {

		case R.id.actsubservicelist_new_message_view:
			addNewService();
			break;

		case R.id.actsubservice_item:
			SmsService service = mProvider.getAllSubservices().get(position-1);
			mActivityHelper.openSettingsSmsService(this, mProvider.getId(), service.getTemplateId(), service.getId());			
			break;
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (RESULT_OK != resultCode) {
			return;
		}

		switch (requestCode) {
		case ActivityHelper.REQUESTCODE_PICKTEMPLATE:
			//get templateId
			String templateId = data.getExtras().getString(ActivityHelper.INTENTKEY_SMSTEMPLATEID);
			//and launch the creation of new subservice
			mActivityHelper.openSettingsSmsService(this, mProvider.getId(), templateId, SmsService.NEWSERVICEID);
			break;

		case ActivityHelper.REQUESTCODE_SERVICESETTINGS:
			break;
		}
	}


	/**
	 * Hander to call when the execute command menu option ended
	 */
	private Handler mExecutedCommandHandler = new Handler() {
		public void handleMessage(Message msg)
		{
			//check if the message is for this handler
			if (msg.what != ExecuteProviderCommandThread.WHAT_EXECUTEDPROVIDERCOMMAND)
				return;

			//dismisses progress dialog
			if (null != mProgressDialog && mProgressDialog.isShowing())
				mProgressDialog.dismiss();
			RainbowResultOperation<String> res = mExecutedProviderCommandThread.getResult();
			//and show the result
			mActivityHelper.showCommandExecutionResult(ActSubservicesList.this, res);
			//free the thread
			mExecutedProviderCommandThread = null;
			//refresh subservices list
			refreshSubservicesList();
		};
	};	




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


	/*
	 Show or hide label with description
	private void showHideInfoLabel()
	{
		if (mProvider.getAllSubservices().size() == 0) {
			mLblNoSubservices.setVisibility(View.VISIBLE);
		} else {
			mLblNoSubservices.setVisibility(View.GONE);
		}
	}*/


	/**
	 * Add new service 
	 */
	private void addNewService() {
		if (!mProvider.hasTemplatesConfigured()) {
			mActivityHelper.showInfo(this, R.string.actsubserviceslist_msgNoTemplates);
		} else {
			//launch the activity for selecting subservice template
			mActivityHelper.openTemplatesList(this, mProvider.getId());
		}
	}

	/**
	 * Launch the background executions of a provider command
	 * 
	 * @param provider
	 * @param subserviceId
	 * @param extraData
	 */
	private void execureProviderCommand(SmsProvider provider, int subserviceId, Bundle extraData)
	{
		//create new progress dialog
		mProgressDialog = mActivityHelper.createAndShowProgressDialog(this, 0, R.string.common_msg_executingCommand);

		//preparing the background thread for executing provider command
		mExecutedProviderCommandThread = new ExecuteProviderCommandThread(
				this.getApplicationContext(),
				mExecutedCommandHandler,
				provider,
				subserviceId,
				extraData);
		//and execute the command
		mExecutedProviderCommandThread.start();
		//at the end of the execution, the handler will be called
	}

	/**
	 * refresh the list of provider's subservices
	 */
	private void refreshSubservicesList() {
		//update the list and avoid the IllegalStateException when a new subservice is added
		if (null != mAdapter) mAdapter.notifyDataSetChanged();
	}

	/**
	 * classe interna per la gestione dell'adapter
	 * dei servizi e le loro icone
	 * 
	 * @author Saverio Guardato
	 */
	private class ServicesAdapter extends BaseAdapter {

		private Context context;
		private SendServiceList servList;

		/*
		 * Costruttore
		 */
		public ServicesAdapter(ActSubservicesList actSbL,
				List<SmsService> svList) {
			context = actSbL;
			servList = new SendServiceList(actSbL, svList, false, false);
		}

		@Override
		public int getCount() {
			return servList.getCount();
		}

		@Override
		public Object getItem(int position) {
			return mProvider.getSubservice(servList.getSimpleList().get(position).getId());
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				convertView = new ServicesAdapterView(context);
			}
			TextView name = (TextView)convertView.findViewById(1);
			ImageView image = (ImageView) convertView.findViewById(2);
			SendService sendService = servList.getSimpleList().get(position);
			name.setText(sendService.getName());
			image.setImageDrawable(sendService.getIcon());
			return convertView;
		}
	}

	/**
	 * Altra classe interna per la lista dei servizi
	 * questa si occupa del linearLayout 
	 * @author Saverio Guardato
	 */
	private class ServicesAdapterView extends RelativeLayout{

		public ServicesAdapterView(Context context) {
			super(context);
						
			RelativeLayout.LayoutParams nameParams = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			nameParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			
			TextView name = new TextView(context);
			name.setTextSize(20f);
			name.setId(1);
			name.setPadding(10, 10, 10, 10);
			addView(name, nameParams);

			RelativeLayout.LayoutParams logoParams = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			logoParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			
			ImageView icon = new ImageView(context);
			icon.setId(2);
			addView(icon, logoParams);

		}
	}

}
