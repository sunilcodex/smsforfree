package it.rainbowbreeze.smsforfree.ui;

import java.util.Map;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.data.ContactDao;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class ActivityHelper {
	//---------- Private fields
	
	


	//---------- Public properties
	public final static int REQUESTCODE_NONE = 0;
	public final static int REQUESTCODE_PICKCONTACT = 3;
	
	public final static String INTENTKEY_SMSSERVICE = "SmsService";
	public final static String INTENTKEY_SMSPROVIDER = "SmsProvider";

	
	

	//---------- Public methods
	
	/**
	 * Open Settings activity
	 */
	public static void openSettingsMain(Activity callerActivity)
	{
		openActivity(callerActivity, ActSettingsMain.class, null, false, REQUESTCODE_NONE);
	}
	
	/**
	 * Open Sms settings activity
	 */
	public static void openSettingsSmsService(Activity callerActivity, String providerId, String serviceId)
	{
        Intent intent = new Intent(callerActivity, ActSettingsSmsService.class);
		intent.putExtra(INTENTKEY_SMSPROVIDER, providerId);
		intent.putExtra(INTENTKEY_SMSSERVICE, serviceId);
		openActivity(intent, callerActivity, false, REQUESTCODE_NONE);
	}
	
	/**
	 * Open provider subservice configuration activity
	 */
	public static void openProviderSubServicesList(Activity callerActivity, String providerId)
	{
        Intent intent = new Intent(callerActivity, ActProviderSubServicesList.class);
		intent.putExtra(INTENTKEY_SMSPROVIDER, providerId);
		openActivity(intent, callerActivity, false, REQUESTCODE_NONE);
	}
	
	/**
	 * Open Provider list activity
	 */
	public static void openProvidersList(Activity callerActivity)
	{
		openActivity(callerActivity, ActProvidersList.class, null, false, REQUESTCODE_NONE);
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
	
	/**
	 * Notify an error to the user 
	 * @param callerActivity
	 * @param errorMessage
	 */
	public static void reportError(Activity callerActivity, String errorMessage)
	{
		Toast.makeText(callerActivity, errorMessage, Toast.LENGTH_LONG).show();
	}
	public static void reportError(Activity callerActivity, int errorMessageId)
	{
		reportError(callerActivity, callerActivity.getString(errorMessageId));
	}
	

	/**
	 * Notify an info message to the user
	 * @param callerActivity
	 * @param infoMessage
	 */
	public static void showInfo(Activity callerActivity, String infoMessage)
	{
		Toast.makeText(callerActivity, infoMessage, Toast.LENGTH_LONG).show();
	}
	public static void showInfo(Activity callerActivity, int infoMessageId)
	{
		showInfo(callerActivity, callerActivity.getString(infoMessageId));
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
	
	
	public static Dialog createAndShowProgressDialog(
			Activity callerActivity,
			int titleId,
			int infoMessageId
		)
	{
		String title = 0 != titleId ? callerActivity.getString(titleId) : "";
		String message = 0 != infoMessageId ? callerActivity.getString(infoMessageId) : "";
		Dialog progressDialog = null;
//		Dialog progressDialog = progressDialog.show(
//				callerActivity,
//				title,
//                message
//                );
				
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
        if (null != extraData)
        {
//	        for (Entry<String, Object> entry : extraData.entrySet())
//	        {
//	            i.putExtra(entry.getKey(), entry.getValue());
//	        	
//	        }
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
