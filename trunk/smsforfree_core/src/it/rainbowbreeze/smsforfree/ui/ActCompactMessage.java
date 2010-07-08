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

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.SmsForFreeApplication;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * @author rainbowbreeze
 *
 */
public class ActCompactMessage
	extends ActBaseDataEntry
{
	//---------- Private fields
	private String mOriginalText;
	private EditText mTxtMessage;
	private SeekBar mBarCompressRatio;
	private TextView mLblStatus;




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
        mLblStatus = (TextView) findViewById(R.id.actcompactmessage_lblStatus);
        
        if (null == savedInstanceState)
        	changeCompressionRatio(0);
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




	//---------- Public methods




	//---------- Private methods

	/**
	 * Completely overwrite father method, returning an intent with data
	 */
	@Override
	protected void confirmEdit()
	{
		Intent intent = new Intent();
		intent.putExtra(ActivityHelper.INTENTKEY_MESSAGE, mTxtMessage.getText().toString());
		setResult(RESULT_OK, intent);
		finish();
	}
	
	@Override
	protected void loadDataIntoViews() {
		mTxtMessage.setText(mOriginalText);
	}

	@Override
	protected boolean saveDataFromViews() {
		return true;
	}
	
	
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
		//and update status label
		mLblStatus.setText(String.format(getString(R.string.actcompactmessage_lblStatus), mOriginalText.length(), message.length()));
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
		String punctuation = ";,.:-_°<>\\/|!?\"()[]{}^=";
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

}
