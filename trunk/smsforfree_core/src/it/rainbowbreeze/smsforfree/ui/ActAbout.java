package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.SmsForFreeApplication;
import it.rainbowbreeze.smsforfree.logic.LogicManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ActAbout
	extends Activity
{
	//---------- Private fields
	private final static int OPTIONMENU_CHANGESLOG = 1;
	private final static int DIALOG_CHANGESLOG = 1;

	
	
	
	//---------- Public properties

	
	
	
	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.actabout);
        setTitle(String.format(
        		getString(R.string.actabout_title), SmsForFreeApplication.instance().getAppName()));
        
        TextView lblVersion = (TextView)findViewById(R.id.actabout_lblAppVersion);
        String version = GlobalDef.appVersionDescription;
        if (SmsForFreeApplication.instance().isLiteVersionApp()) version = version + " " + GlobalDef.lite_description;
        lblVersion.setText(version);

        TextView lblSentSms = (TextView)findViewById(R.id.actabout_lblSentSms);
        String sentSms = String.valueOf(LogicManager.getSmsSentToday());
        if (SmsForFreeApplication.instance().isLiteVersionApp()) sentSms = sentSms + "/" + SmsForFreeApplication.instance().getAllowedSmsForDay();
        lblSentSms.setText(String.format(
        		getString(R.string.actabout_lblSentSms), sentSms));
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!super.onCreateOptionsMenu(menu)) return false;
		
    	menu.add(0, OPTIONMENU_CHANGESLOG, 4, R.string.actabout_mnuChangeslog)
			.setIcon(R.drawable.ic_menu_archive);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case OPTIONMENU_CHANGESLOG:
			showDialog(DIALOG_CHANGESLOG);
			break;
		}
		
		return true;
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id)
	{
		Dialog returnDialog;
		
		switch (id) {
		case DIALOG_CHANGESLOG:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.actabout_msgChangeslogTitle)
				.setMessage(R.string.actabout_msgChangeslog)
				.setNeutralButton(R.string.common_btnOk, null)
				.setCancelable(false);
			returnDialog = builder.create();			
			break;

		default:
			returnDialog = super.onCreateDialog(id);
		}
		
		return returnDialog;
	}

	//---------- Public methods

	//---------- Private methods

}
