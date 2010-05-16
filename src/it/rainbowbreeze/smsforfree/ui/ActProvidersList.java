/**
 * 
 */
package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalBag;
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
		setContentView(R.layout.acttemplateslist);

		setListAdapter(new ArrayAdapter<SmsProvider>(this, 
	              android.R.layout.simple_list_item_1, GlobalBag.providerList));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		SmsProvider provider = GlobalBag.providerList.get(position);
		
		ActivityHelper.openSettingsSmsService(this, provider.getId());
	}

	
	
	
	//---------- Public methods

	
	
	
	//---------- Private methods

}
