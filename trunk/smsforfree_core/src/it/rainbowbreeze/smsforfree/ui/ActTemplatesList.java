/**
 * 
 */
package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree_core.R;
import it.rainbowbreeze.smsforfree.common.SmsForFreeApplication;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.util.GlobalUtils;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * @author rainbowbreeze
 *
 */
public class ActTemplatesList
	extends ListActivity
{
	//---------- Private fields
	SmsProvider mProvider;

	
	
	
	//---------- Public properties

	
	
	
	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        setTitle(String.format(
        		getString(R.string.acttemplateslist_title), SmsForFreeApplication.instance().getAppName()));

		setContentView(R.layout.acttemplateslist);
		getDataFromIntent(getIntent());
		
		if (null == mProvider) return;

		setListAdapter(new ArrayAdapter<SmsService>(this, 
	              android.R.layout.simple_list_item_1, mProvider.getAllTemplates()));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		SmsService template = mProvider.getAllTemplates().get(position);
		
		//return to caller activity the template id
		Intent intent = new Intent();
		intent.putExtra(ActivityHelper.INTENTKEY_SMSTEMPLATEID, template.getId());
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
			mProvider = GlobalUtils.findProviderInList(SmsForFreeApplication.instance().getProviderList(), id);
		} else {
			mProvider = null;
		}
	}

}
