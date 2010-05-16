/**
 * 
 */
package it.rainbowbreeze.smsforfree.ui;

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
		
		Intent intent = getIntent();
		
		//retrieve the message to compact
		mOriginalText = intent.getStringExtra(ActivityHelper.INTENTKEY_MESSAGE);
	}

	//---------- Public methods

	//---------- Private methods

}
