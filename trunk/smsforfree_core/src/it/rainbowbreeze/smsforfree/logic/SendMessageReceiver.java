/**
 * Copyright (C) 2011 Alfredo Morresi
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

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.AppEnv;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.data.MessageQueueDao;
import it.rainbowbreeze.smsforfree.data.SmsDao;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.TextMessage;
import it.rainbowbreeze.smsforfree.helper.GlobalHelper;
import it.rainbowbreeze.smsforfree.ui.ActSendSms;
import it.rainbowbreeze.smsforfree.ui.ActivityHelper;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * Send a scheduled message
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class SendMessageReceiver extends BroadcastReceiver {
    //---------- Private fields
    private static final String LOG_HASH = "SendMessageReceiver";
    private LogFacility mLogFacility;

    //---------- Public properties

    
    
    //---------- Events
    /* (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        mLogFacility = AppEnv.i(context).getLogFacility();
        mLogFacility.v(LOG_HASH, "onReceive started");

        long textMessageId = intent.getLongExtra(AppEnv.INTENTKEY_MESSAGEID, -1);
        if (-1 == textMessageId) {
            mLogFacility.i(LOG_HASH, "Invalid textMessage Id received");
            return;
        }
        
        //get the text message from the queue
        MessageQueueDao dao = AppEnv.i(context).getMessageQueueDao();
        final TextMessage textMessage = dao.getById(textMessageId);
        if (null == textMessage) {
            mLogFacility.i(LOG_HASH, "Unable to retrieve message with id " + textMessageId + " from queue");
            return;
        }
        
        //checks if the maximum limit of sent/sms by day was reached
        if (!AppEnv.i(context).getLogicManager().checkIfCanSendSms(context)) {
            mLogFacility.i(LOG_HASH, "Maximum number of SMS/day reached, cannot send new messages");
            //display an alert
            showErrorNotification(context, R.string.common_msg_smsLimitReach, textMessage);
            return;
        }

        //retrieve provider for sending message
        if (TextUtils.isEmpty(textMessage.getProviderId())) {
            mLogFacility.i(LOG_HASH, "Empty provider for message with id " + textMessageId);
            showErrorNotification(context, R.string.sendmessagereceiver_msgInvalidMessage, textMessage);
            return;
        }
        final SmsProvider provider = GlobalHelper.findProviderInList(
                AppEnv.i(context).getProviderList(),
                textMessage.getProviderId()); 
        if (null == provider) {
            mLogFacility.i(LOG_HASH, "Cannot find a provider for id " + textMessage.getProviderId());
            showErrorNotification(context, R.string.sendmessagereceiver_msgInvalidMessage, textMessage);
            return;
        }
        
        final Context finalContext = context;
        
        //send the message in a separate thread (10 seconds timeout)
        new Thread(new Runnable() {
            public void run() {
                ResultOperation<String> result =  provider.sendMessage(
                        textMessage.getServiceId(), 
                        textMessage.getDestination(),
                        textMessage.getMessage());
                
                //invalid captcha request
                if (ResultOperation.RETURNCODE_SMS_CAPTCHA_REQUEST == result.getReturnCode()) {
                    //show notification
                    showErrorNotification(finalContext, R.string.sendmessagereceiver_msgCaptchaNotSupported, textMessage);

                //return with errors
                } else if (result.hasErrors()) {
                    ActivityHelper activityHelper = AppEnv.i(finalContext).getActivityHelper();
                    String errorMessage = activityHelper.getErrorMessage(result.getReturnCode(), result.getException());
                    mLogFacility.e(LOG_HASH, "Error sending message: " + errorMessage);
                    showErrorNotification(finalContext, errorMessage, textMessage);
                    return;

                } else {
                    mLogFacility.i(LOG_HASH, "Send message result: " + result.getResult());
                    //update number of messages sent in the day
                    AppEnv.i(finalContext).getLogicManager().updateSmsCounter(1);
                    //insert sms into pim, if required
                    insertSmsIntoPim(finalContext, textMessage);
                    //and show the good news
                    showSentMessageNotification(finalContext, textMessage);
                }
            }
        }).start();
    }

    
    
    
    //---------- Public methods

    
    
    
    //---------- Private methods
    /**
     * Insert sent SMS into PIM of the device
     */
    private void insertSmsIntoPim(Context context, TextMessage textMessage) {
        //insert SMS into PIM
        AppPreferencesDao appPreferencesDao = AppEnv.i(context).getAppPreferencesDao();
        if (appPreferencesDao.getInsertMessageIntoPim()) {
            SmsDao smsDao = AppEnv.i(context).getSmsDao();
            ResultOperation<Void> res = smsDao.saveSmsInSentFolder(
                    context,
                    textMessage.getDestination(),
                    textMessage.getMessage());

            if (res.hasErrors()) {
                mLogFacility.e(LOG_HASH, "Error saving message in Send folder");
            }
        }
    }
    
    private void showErrorNotification(Context context, String message, TextMessage textMessage) {
        showNotification(context, android.R.drawable.stat_notify_error, message, textMessage);
    }
    
    private void showErrorNotification(Context context, int messageId, TextMessage textMessage) {
        showNotification(
                context,
                android.R.drawable.stat_notify_error,
                context.getString(messageId),
                textMessage);
    }
    
    private void showSentMessageNotification(Context context, TextMessage textMessage) {
        showNotification(
                context,
                android.R.drawable.sym_action_email,
                context.getString(R.string.sendmessagereceiver_msgSendMessage),
                textMessage);
    }
    
    private void showNotification(
            Context context,
            int iconId,
            String message,
            TextMessage textMessage) {
        
        String appDisplayName = AppEnv.i(context).getAppDisplayName();
        //ticker-text
        CharSequence tickerText = appDisplayName;
        //notification time
        long when = System.currentTimeMillis();
        //expanded message title
        CharSequence contentTitle = appDisplayName + " " + message;
        //expanded message text
        CharSequence contentText = String.format(
                context.getString(R.string.sendmessagereceiver_msgMessageResume),
                textMessage.getDestination(),
                textMessage.getMessage().length() < 30
                        ? textMessage.getMessage()
                        : textMessage.getMessage().substring(1, 30));

        //TODO change with right intent
        Intent notificationIntent = new Intent(context, ActSendSms.class);
        //TODO vedere valori dati
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        // the next two lines initialize the Notification, using the configurations above
        Notification notification = new Notification(iconId, tickerText, when);
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        // Send a text notification to the screen.
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(appDisplayName, (int) textMessage.getId(), notification);    
    }

}
