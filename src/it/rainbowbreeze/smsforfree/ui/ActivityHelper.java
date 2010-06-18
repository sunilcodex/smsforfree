package it.rainbowbreeze.smsforfree.ui;

import java.util.Map;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.ContactDao;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.view.Display;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class ActivityHelper {
	//---------- Private fields
	
	


	//---------- Public properties
	public final static int REQUESTCODE_NONE = 0;
	public final static int REQUESTCODE_PICKCONTACT = 3;
	public final static int REQUESTCODE_PICKTEMPLATE = 4;
	public final static int REQUESTCODE_COMPACTMESSAGE = 5;
	public final static int REQUESTCODE_SETTINGS = 6;
	public final static int REQUESTCODE_SERVICESETTINGS = 7;
	
	public final static String INTENTKEY_SMSSERVICEID = "SmsService";
	public final static String INTENTKEY_SMSPROVIDERID = "SmsProvider";
	public final static String INTENTKEY_SMSTEMPLATEID = "SmsTemplate";
	public final static String INTENTKEY_PICKTEMPLATE = "PickTemplate";
	public final static String INTENTKEY_MESSAGE = "Message";

	
	

	//---------- Public methods
	
	/**
	 * Open Settings activity
	 * 
	 * @param callerActivity caller activity
	 */
	public static void openSettingsMain(Activity callerActivity)
	{
		openActivity(callerActivity, ActSettingsMain.class, null, true, REQUESTCODE_SETTINGS);
	}
	
	/**
	 * Open Sms settings activity for editing a subservice preferences
	 * 
	 * @param callerActivity caller activity
	 * @param providerId the id of the provider that own the subservice
	 * @param templateId the id of the template on which the service is
	 *                   based
	 * @param serviceId the id of the subservice to edit.
	 *                  SmsService.NEWSERVICEID when adding a new service
	 */
	public static void openSettingsSmsService(Activity callerActivity, String providerId, String templateId, String serviceId)
	{
        Intent intent = new Intent(callerActivity, ActSettingsSmsService.class);
		intent.putExtra(INTENTKEY_SMSPROVIDERID, providerId);
		intent.putExtra(INTENTKEY_SMSTEMPLATEID, templateId);
		intent.putExtra(INTENTKEY_SMSSERVICEID, serviceId);
		openActivity(intent, callerActivity, true, REQUESTCODE_SERVICESETTINGS);
	}
	
	/**
	 * Open Sms settings activity for editing a provider preferences
	 * 
	 * @param callerActivity caller activity
	 * @param providerId the id of the provider to edit
	 */
	public static void openSettingsSmsService(Activity callerActivity, String providerId)
	{
		//putting null into templateId and subserviceId, the ActSettingsSmsService knows what entity edit
		openSettingsSmsService(callerActivity, providerId, null, null);
	}
	
	/**
	 * Open provider list activity
	 */
	public static void openProvidersList(Activity callerActivity)
	{
		openActivity(callerActivity, ActProvidersList.class, null, false, REQUESTCODE_NONE);
	}
	
	/**
	 * Open about activity
	 */
	public static void openAbout(Activity callerActivity)
	{
		openActivity(callerActivity, ActAbout.class, null, false, REQUESTCODE_NONE);
	}
	
	/**
	 * Open compact message activity
	 * @param callerActivity
	 * @param message the message to compact
	 */
	public static void openCompactMessage(Activity callerActivity, String message)
	{
        Intent intent = new Intent(callerActivity, ActCompactMessage.class);
		intent.putExtra(INTENTKEY_MESSAGE, message);
		openActivity(intent, callerActivity, true, REQUESTCODE_COMPACTMESSAGE);
	}

	/**
	 * Open provider's templates list activity
	 */
	public static void openTemplatesList(Activity callerActivity, String providerId)
	{
        Intent intent = new Intent(callerActivity, ActTemplatesList.class);
		intent.putExtra(INTENTKEY_SMSPROVIDERID, providerId);
		openActivity(intent, callerActivity, true, REQUESTCODE_PICKTEMPLATE);
	}
	/**
	 * Open provider subservice configuration activity
	 */
	public static void openSubservicesList(Activity callerActivity, String providerId)
	{
        Intent intent = new Intent(callerActivity, ActSubservicesList.class);
		intent.putExtra(INTENTKEY_SMSPROVIDERID, providerId);
		openActivity(intent, callerActivity, false, REQUESTCODE_NONE);
	}
	
	/**
	 * 
	 * @param callerActivity
	 */
	public static void openPickContact(Activity callerActivity)
	{
		Intent intent = ContactDao.instance().getPickContactIntent();
		openActivity(intent, callerActivity, true, REQUESTCODE_PICKCONTACT);
	}
	
	
	public static void openBrowser(Context context, String urlToOpen, boolean openInNewTask)
	{
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_BROWSABLE);
		intent.setData(Uri.parse(urlToOpen));
		if (openInNewTask) intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent); 		
	}
	
	/**
	 * Notify an error to the user 
	 * @param context
	 * @param errorMessage
	 */
	public static void reportError(Context context, String errorMessage)
	{ Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show(); }
	
	public static void reportError(Context context, int errorMessageId)
	{ reportError(context, context.getString(errorMessageId)); }
	
	public static void reportError(Context context, ResultOperation<String> res)
	{ reportError(context, res.getException(), res.getReturnCode()); }

	public static void reportError(Context context, Exception exception, int returnCode)
	{
		//First of all, examines return code for standard errors
		String userMessage;
		switch (returnCode) {
		case ResultOperation.RETURNCODE_ERROR_APPLICATION_ARCHITECTURE:
			userMessage = String.format(
					context.getString(R.string.common_msg_architecturalError), exception.getMessage());
			break;
		case ResultOperation.RETURNCODE_ERROR_COMMUNICATION:
			userMessage = String.format(
					context.getString(R.string.common_msg_communicationError), exception.getMessage());
			break;
		case ResultOperation.RETURNCODE_ERROR_GENERIC:
			userMessage = String.format(
					context.getString(R.string.common_msg_genericError), exception.getMessage());
			break;
		case ResultOperation.RETURNCODE_ERROR_EMPTY_REPLY:
			userMessage = context.getString(R.string.common_msg_noReplyFromProvider);
			break;
		case ResultOperation.RETURNCODE_ERROR_LOAD_PROVIDER_DATA:
			userMessage = String.format(
					context.getString(R.string.common_msg_cannotLoadProviderData), exception.getMessage());
			break;
		case ResultOperation.RETURNCODE_ERROR_NOCREDENTIAL:
			userMessage = context.getString(R.string.common_msg_noCredentials);
			break;
		case ResultOperation.RETURNCODE_ERROR_SAVE_PROVIDER_DATA:
			userMessage = String.format(
					context.getString(R.string.common_msg_cannotSaveProviderData), exception.getMessage());
			break;
		default:
			userMessage = String.format(
					context.getString(R.string.common_msg_architecturalError), "No error result code managed.");
			break;
		}
		
		//display the error to the user
		reportError(context, userMessage);
		
		//and log it
		//TODO
		exception.printStackTrace();
	}
	

	/**
	 * Notify an info message to the user
	 * @param context
	 * @param infoMessage
	 */
	public static void showInfo(Context context, int infoMessageId)
	{
		showInfo(context, context.getString(infoMessageId), Toast.LENGTH_SHORT);
	} 
	public static void showInfo(Context context, String infoMessage)
	{
		showInfo(context, infoMessage, Toast.LENGTH_LONG);
	}
	public static void showInfo(Context context, String infoMessage, int messageLenght)
	{
		Toast.makeText(context, infoMessage, messageLenght).show();
	}
	public static void showInfo(Context context, int infoMessageId, int messageLenght)
	{
		Toast.makeText(context, infoMessageId, messageLenght).show();
	} 


	/**
	 * Process in a standard way the result of SmsService extended command
	 * execution
	 * 
	 * @param context
	 * @param result
	 */
	public static void showCommandExecutionResult(Context context, ResultOperation<String> result)
	{
		//show command results
		if (result.HasErrors()) {
			ActivityHelper.reportError(context, result);
		} else {
			//shows the output of the command
			ActivityHelper.showInfo(context, result.getResult());
		}		
	}
	
	
	/**
	 * 
	 * @param callerActivity
	 * @param infoMessageId
	 * @param yesListner
	 * @param noListener
	 * @return
	 */
	public static Dialog createYesNoDialog(
			Activity callerActivity,
			int infoMessageId,
			OnClickListener yesListner,
			OnClickListener noListener
		)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(callerActivity);
		String yesMessage = callerActivity.getString(R.string.common_btnYes);
		String noMessage = callerActivity.getString(R.string.common_btnNo);

		builder.setMessage(infoMessageId)
		       .setCancelable(false)
		       .setPositiveButton(yesMessage, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                //MyActivity.this.finish();
		           }
		       })
		       .setNegativeButton(noMessage, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		return alert;
	}
	
	
	public static ProgressDialog createAndShowProgressDialog(
			Activity callerActivity,
			int messageId
		)
	{
		String message = 0 != messageId ? callerActivity.getString(messageId) : "";
		ProgressDialog progressDialog = new ProgressDialog(callerActivity);
		progressDialog.setMessage(message);
		progressDialog.show();
		return progressDialog;
	}


	public static ProgressDialog createProgressDialog(
			Activity callerActivity,
			int messageId
		)
	{
		String message = 0 != messageId ? callerActivity.getString(messageId) : "";
		ProgressDialog progressDialog = new ProgressDialog(callerActivity);
		progressDialog.setMessage(message);
		return progressDialog;
	}


	/**
	 * 
	 * @param callerActivity
	 * @return
	 */
	public static int getScreenWidth(Activity callerActivity)
	{
        WindowManager w = callerActivity.getWindowManager();
        Display d = w.getDefaultDisplay();
        int width = d.getWidth();
        //int height = d.getHeight();
        return width;
	}


	/**
	 * 
	 * @param callerActivity
	 * @return
	 */
	public static int getScreenHeight(Activity callerActivity)
	{
        WindowManager w = callerActivity.getWindowManager();
        Display d = w.getDefaultDisplay();
        int height = d.getHeight();
        return height;
	}
	
	/**
	 * 
	 * @param callerActivity
	 * @return
	 */
	public static Point getScreenSize(Activity callerActivity)
	{
        WindowManager w = callerActivity.getWindowManager();
        Display d = w.getDefaultDisplay();
        Point p = new Point(d.getWidth(), d.getHeight());
        return p;
	}
	
	
	/**
	 * Return activity's current orientation
	 * 
	 * @param callerActivity
	 * @return Configuration.ORIENTATION_PORTRAIT, Configuration.ORIENTATION_LANDSCAPE or
	 *         Configuration.ORIENTATION_SQUARE
	 */
	public static int getCurrentOrientation(Context callerContext)
	{
		return callerContext.getResources().getConfiguration().orientation;
	}

	/**
	 * Assign the desired orientation to the activity
	 * 
	 * @param callerActivity
	 * @param newOrientation: valuers from Configuration.ORIENTATION_XXXXX
	 */
	public static void setCurrentOrientation(Activity callerActivity, int newOrientation) {
		switch (newOrientation) {
		case Configuration.ORIENTATION_LANDSCAPE:
			callerActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;

		case Configuration.ORIENTATION_PORTRAIT:
			callerActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		}
		
		//do nothing in the other cases
	}
	
	
	/**
	 * Toggle the orientation of the activity
	 * 
	 * @param callerActivity
	 */
	public static void toggleCurrentOrientation(Activity callerActivity){
		if (getCurrentOrientation(callerActivity) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
			setCurrentOrientation(callerActivity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		else if (getCurrentOrientation(callerActivity) == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
			setCurrentOrientation(callerActivity, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
	
	
	
	/**
	 * Is the current activity orientation portrait? 
	 * 
	 * @param callerActivity
	 * @return
	 */
	public static boolean isPortrait(Activity callerActivity)
	{
//        WindowManager w = callerActivity.getWindowManager();
//        Display d = w.getDefaultDisplay();
//        return (d.getWidth() > d.getHeight());
		return callerActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
	}


	/**
	 * Is the current activity orientation landscape? 
	 * 
	 * @param callerActivity
	 * @return
	 */
	public static boolean isLandscape(Activity callerActivity)
	{
		//return !isPortrait(callerActivity);
		return callerActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}

	


	

	//---------- Private methods
	
	/**
	 * Generic method for activity opening
	 * 
	 * @param callerActivity
	 * @param cls
	 * @param extraData
	 * @param mustReturn
	 * @param requestCode
	 */
	private static void openActivity(
		Activity callerActivity,
		Class<?> cls,
		Map<String, Object> extraData,
		boolean mustReturn,
		int requestCode
	)
	{
		Intent i = new Intent(callerActivity.getBaseContext(), cls);

		//put the data in the intent
		if (null != extraData) {
//			for (Entry<String, Object> entry : extraData.entrySet()) {
//				i.putExtra(entry.getKey(), entry.getValue());
//			}
		}

		openActivity(i, callerActivity, mustReturn, requestCode);
	}
    
	private static void openActivity(
		Intent intent,
		Activity callerActivity,
		boolean mustReturn,
		int requestCode
	)
	{
		if (mustReturn)
			callerActivity.startActivityForResult(intent, requestCode);
		else
			callerActivity.startActivity(intent);
	}


}
