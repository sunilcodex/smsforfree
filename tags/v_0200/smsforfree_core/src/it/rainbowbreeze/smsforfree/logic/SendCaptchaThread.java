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

package it.rainbowbreeze.smsforfree.logic;

import it.rainbowbreeze.libs.logic.RainbowBaseBackgroundThread;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import android.content.Context;
import android.os.Handler;



/**
 * Send a captcha
 */
public class SendCaptchaThread
	extends RainbowBaseBackgroundThread<String>
{

	//---------- Private fields
	private SmsProvider mProvider;
	private String mProviderCaptchaData;
	private String mCaptchaCode;

	
	

    //---------- Constructors
    public SendCaptchaThread(
            Context context,
            Handler handler,
            SmsProvider provider,
            String providerCaptchaData,
            String captchaCode)
    {
        super(context, handler);
        mProvider = provider;
        mProviderCaptchaData = providerCaptchaData;
        mCaptchaCode = captchaCode;
    }




	//---------- Public fields
	public final static int WHAT_SENDCAPTCHA = 1003;
	



	//---------- Public methods
	@Override
	public void run() {
		//execute the command
		mResultOperation = mProvider.sendCaptcha(mProviderCaptchaData, mCaptchaCode);
		//and call the caller activity handler when the execution is terminated
		callHandlerAndRetry(WHAT_SENDCAPTCHA);
	}
	



	//---------- Private methods
}
