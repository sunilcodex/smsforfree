/**
 * 
 */
package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.SmsForFreeApplication;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

/**
 * @author rainbowbreeze
 *
 */
public class ActCompactMessage
	extends Activity
{
	//---------- Private fields
	private String mOriginalText;
	private EditText mTxtMessage;
	private SeekBar mBarCompressRatio;



	//---------- Public properties




	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setTitle(String.format(
        		getString(R.string.actcompactmessage_title), SmsForFreeApplication.instance().getAppName()));
        setContentView(R.layout.actcompactmessage);
        
		//retrieve the message to compact
		Intent intent = getIntent();
		mOriginalText = intent.getStringExtra(ActivityHelper.INTENTKEY_MESSAGE);
		
        mBarCompressRatio = (SeekBar) findViewById(R.id.actcompactmessage_barCompressRatio);
        mBarCompressRatio.setOnSeekBarChangeListener(mOnSeekBarChange);
        mTxtMessage = (EditText) findViewById(R.id.actcompactmessage_txtMessage);
		mTxtMessage.setText(mOriginalText);
		((Button) findViewById(R.id.actcompactmessage_btnOk)).setOnClickListener(mBtnOkClickListener);
		((Button) findViewById(R.id.actcompactmessage_btnCancel)).setOnClickListener(mBtnCancelClickListener);
	}
	
	
	
	private SeekBar.OnSeekBarChangeListener mOnSeekBarChange = new SeekBar.OnSeekBarChangeListener() {
		
		public void onStartTrackingTouch(SeekBar seekBar) {
		}
		
		public void onStopTrackingTouch(SeekBar seekBar) {
		}
		
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			//change the compression ratio of the text
			changeCompressionRatio(progress);
		}
	};
	
	/**
	 * Intercept when the user press the Back button and create an event tracking
	 * of the event
	 * @param keyCode
	 * @param event
	 * @return
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//        if (keyCode == KeyEvent.KEYCODE_BACK
//                && event.getRepeatCount() == 0) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

	/**
	 * Intercept when the user release the Back button, call the method for
	 * saving data and close the activity
	 * @param keyCode
	 * @param event
	 * @return
	 */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
                && !event.isCanceled()) {
            confirmEdit();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }	

    
	private OnClickListener mBtnOkClickListener = new OnClickListener() {
		public void onClick(View v) {
			confirmEdit();
		}
	};


	private OnClickListener mBtnCancelClickListener = new OnClickListener() {
		public void onClick(View v) {
			cancelEdit();
		}
	};
	
	

	
	//---------- Public methods




	//---------- Private methods
	
	/**
	 * Compress the text by the selected compression ratio
	 * 
	 * @param ratio
	 */
	private void changeCompressionRatio(int ratio)
	{
		String message = mOriginalText;

		//recompress the message
		for (int step = 1; step <= ratio; step ++)
		{
			switch (step) {
			case 0:
				//no compression
				break;
				
			case 1:
				message = compressTextLevel1(message);
				break;
				
			case 2:
				message = compressTextLevel2(message);
				break;

			}
		}
		//and update the text message
		mTxtMessage.setText(message);
	} 
	
	
	/**
	 * Compress the given string trimming white space after and before
	 * punctuation characters and trim double white spaces
	 * 
	 * @param messageToCompress message to compress
	 * @return the compressed message
	 */
	private String compressTextLevel1(String messageToCompress)
	{
		String result = messageToCompress;
		int oldLenght;
		int newLenght;

		//trims double white spaces
		oldLenght = result.length();
		newLenght = 0;
		while(oldLenght != newLenght) {
			oldLenght = result.length(); 
			result = result.replace("  ", " ");
			newLenght = result.length(); 
		}
		
		//trims spaces before and after punctuation characters
		String punctuation = ";,.:-_°<>\\/|!?'\"()[]{}^=";
		for (int index = 0; index < punctuation.length(); index++) {
			char singleChar = punctuation.charAt(index);
			result = result.replace(" " + singleChar + " ", singleChar + "");
			result = result.replace(" " + singleChar, singleChar + "");
			result = result.replace(singleChar + " ", singleChar + "");
		}
		
		return result;
	}

	
	/**
	 * Compress the given string replacing white spaces between words
	 * uppercasing first letter of each word 
	 * @param messageToCompress
	 * @return
	 */
	private String compressTextLevel2(String messageToCompress)
	{
		String result = messageToCompress;
		
		String letters = "abcdefghijklmnopqrstuvxywz";
		for (int index = 0; index < letters.length(); index++) {
			String letterLowercase = (" " + letters.charAt(index)).toLowerCase();
			String letterUppercase = (" " + letters.charAt(index)).toUpperCase();
			String firstLetter = ("" + letters.charAt(index)).toUpperCase();
			result = result.replace(letterLowercase, firstLetter);
			result = result.replace(letterUppercase, firstLetter);
		}
		
		return result;
	}
	
	
	private void confirmEdit()
	{
		Intent intent = new Intent();
		intent.putExtra(ActivityHelper.INTENTKEY_MESSAGE, mTxtMessage.getText().toString());
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private void cancelEdit()
	{
		setResult(RESULT_CANCELED);
		finish();
	}

}
