/**
 * 
 */
package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalBag;
import it.rainbowbreeze.smsforfree.common.GlobalUtils;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsService;
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
		setContentView(R.layout.acttemplateslist);
		getDataFromIntent(getIntent());
		
		if (null == mProvider) return;

		setListAdapter(new ArrayAdapter<SmsService>(this, 
	              android.R.layout.simple_list_item_1, mProvider.getAllTemplate()));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		SmsService template = mProvider.getAllTemplate().get(position);
		
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
			mProvider = GlobalUtils.findProviderInList(GlobalBag.providerList, id);
		} else {
			mProvider = null;
		}
	}

}
