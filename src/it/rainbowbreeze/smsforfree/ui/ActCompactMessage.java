/**
 * 
 */
package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.SmsForFreeApplication;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * @author rainbowbreeze
 *
 */
public class ActCompactMessage
	extends Activity
{
	//---------- Private fields
	private String mOriginalText;
	private TextView mLblResult;

	//---------- Public properties

	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setTitle(String.format(
        		getString(R.string.actcompactmessage_title), SmsForFreeApplication.instance().getAppName()));
		
		Intent intent = getIntent();
		
		//retrieve the message to compact
		mOriginalText = intent.getStringExtra(ActivityHelper.INTENTKEY_MESSAGE);
	}

	//---------- Public methods

	//---------- Private methods

}
