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
import it.rainbowbreeze.libs.common.RainbowServiceLocator;
import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.App;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsServiceCommand;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.helper.GlobalHelper;
import it.rainbowbreeze.smsforfree.logic.ExecuteProviderCommandThread;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import static it.rainbowbreeze.libs.common.RainbowContractHelper.*;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ActSubservicesList
	extends ListActivity
{
	//---------- Private fields
    protected final static String LOG_HASH = "ActSubservicesTemplates";
	private static final int OPTIONMENU_ADDSERVICE = 10;
	private static final int CONTEXTMENU_ADDSERVICE = 1;
	private static final int CONTEXTMENU_EDITSERVICE = 2;
	private static final int CONTEXTMENU_DELETESERVICE = 3;
	
	private SmsProvider mProvider;
    ArrayAdapter<SmsService> mListAdapter;
    TextView mLblNoSubservices;

	private ExecuteProviderCommandThread mExecutedProviderCommandThread;
	private ProgressDialog mProgressDialog;
	private LogFacility mLogFacility;
	private ActivityHelper mActivityHelper;




	//---------- Public properties

	
	
	
	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        mLogFacility = checkNotNull(RainbowServiceLocator.get(LogFacility.class), "LogFacility");
        mLogFacility.logStartOfActivity(LOG_HASH, this.getClass(), savedInstanceState);
        mActivityHelper = checkNotNull(RainbowServiceLocator.get(ActivityHelper.class), "ActivityHelper");
		
		setContentView(R.layout.actsubserviceslist);
		
		getDataFromIntent(getIntent());
		
		if (null == mProvider)
			return;
		
		//update title
        setTitle(String.format(
        		getString(R.string.actsubserviceslist_title),
        		App.i().getAppDisplayName(),
        		mProvider.getName()));

        mLblNoSubservices = (TextView) findViewById(R.id.actsubservicelist_lblNoSubservices);
        mListAdapter = new ArrayAdapter<SmsService>(this, 
	              android.R.layout.simple_list_item_1, mProvider.getAllSubservices());
		setListAdapter(mListAdapter);
		
		//register the context menu to defaul ListView of the view
		//alternative method:
		//http://www.anddev.org/creating_a_contextmenu_on_a_listview-t2438.html
		registerForContextMenu(getListView());
		
		showHideInfoLabel();
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
		
		//creates it's own menu
		menu.add(0, OPTIONMENU_ADDSERVICE, 0, R.string.actsubserviceslist_mnuAddService)
			.setIcon(android.R.drawable.ic_menu_add);

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
		menu.add(0, CONTEXTMENU_ADDSERVICE, 0, R.string.actsubserviceslist_mnuAddService)
			.setIcon(android.R.drawable.ic_menu_add);
		menu.add(0, CONTEXTMENU_EDITSERVICE, 1, R.string.actsubserviceslist_mnuEditService)
			.setIcon(android.R.drawable.ic_menu_edit);
		menu.add(0, CONTEXTMENU_DELETESERVICE, 2, R.string.actsubserviceslist_mnuDeleteService)
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
		case OPTIONMENU_ADDSERVICE:
			addNewService();
			break;
		
		//execute one of the provider's command
		default:
			execureProviderCommand(mProvider, item.getItemId(), null);
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
			addNewService();
			break;
		case CONTEXTMENU_EDITSERVICE:
			//find current selected service
			service = (SmsService) getListAdapter().getItem(menuInfo.position);
			//and edit it
			mActivityHelper.openSettingsSmsService(this, mProvider.getId(), service.getTemplateId(), service.getId());
			break;
		case CONTEXTMENU_DELETESERVICE:
			service = (SmsService) getListAdapter().getItem(menuInfo.position);
			mProvider.getAllSubservices().remove(service);
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
		
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		SmsService service = mProvider.getAllSubservices().get(position);
		
		mActivityHelper.openSettingsSmsService(this, mProvider.getId(), service.getTemplateId(), service.getId());
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
			showHideInfoLabel();
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
			mProvider = GlobalHelper.findProviderInList(App.i().getProviderList(), id);
		} else {
			mProvider = null;
		}
	}
	
	
	/**
	 * Show or hide label with description
	 */
	private void showHideInfoLabel()
	{
		if (mProvider.getAllSubservices().size() == 0) {
			mLblNoSubservices.setVisibility(View.VISIBLE);
		} else {
			mLblNoSubservices.setVisibility(View.GONE);
		}
	}


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
		if (null != mListAdapter) mListAdapter.notifyDataSetChanged();
		showHideInfoLabel();
	}
	
}
