package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.SmsForFreeApplication;
import it.rainbowbreeze.smsforfree.logic.LogicManager;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ActAbout
	extends Activity
{
	//---------- Ctors

	//---------- Private fields

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
        if (SmsForFreeApplication.instance().isLiteVersionApp()) version = version + " Lite";
        lblVersion.setText(version);

        TextView lblSentSms = (TextView)findViewById(R.id.actabout_lblSentSms);
        lblSentSms.setText(String.format(
        		getString(R.string.actabout_lblSentSms), LogicManager.getSmsSentToday()));
	}

	//---------- Public methods

	//---------- Private methods

}
