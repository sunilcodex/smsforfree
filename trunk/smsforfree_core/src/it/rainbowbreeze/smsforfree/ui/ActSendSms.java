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
 * import android.util.Log;

 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/>.
 */

package it.rainbowbreeze.smsforfree.ui;

import java.util.List;

import com.admob.android.ads.AdView;

import it.rainbowbreeze.libs.common.RainbowResultOperation;
import it.rainbowbreeze.libs.common.RainbowServiceLocator;
import it.rainbowbreeze.libs.helper.RainbowStringHelper;
import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.common.App;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.data.ContactDao;
import it.rainbowbreeze.smsforfree.data.SmsDao;
import it.rainbowbreeze.smsforfree.domain.ContactPhone;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.helper.GlobalHelper;
import it.rainbowbreeze.smsforfree.logic.LogicManager;
import it.rainbowbreeze.smsforfree.logic.SendCaptchaThread;
import it.rainbowbreeze.smsforfree.logic.SendMessageThread;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import static it.rainbowbreeze.libs.common.RainbowContractHelper.*;

public class ActSendSms
	extends Activity
{
	//---------- Private fields
	private static final int DIALOG_PHONES = 10;
	private final static int DIALOG_CAPTCHA_REQUEST = 11;
	private final static int DIALOG_SENDING_MESSAGE = 12;
	private final static int DIALOG_SENDING_CAPTCHA = 13;
	private static final int DIALOG_STARTUP_INFOBOX = 14;
	private static final int DIALOG_TEMPLATES = 16;
	
	private final static String BUNDLEKEY_CONTACTPHONES = "ContactPhones";
	private final static String BUNDLEKEY_CAPTCHASTORAGE = "CaptchaStorage";

	private final static int OPTIONMENU_SETTINGS = 2;
	private final static int OPTIONMENU_SIGNATURE = 3;
	private final static int OPTIONMENU_ABOUT = 4;
	private final static int OPTIONMENU_RESETDATA = 5;
	private final static int OPTIONMENU_COMPRESS = 6;
	private final static int OPTIONMENU_TEMPLATES = 7;

	
	private Spinner mSpiProviders;
	private Spinner mSpiSubservices;

	private SmsProvider mSelectedProvider;
	private String mSelectedServiceId;
	private EditText mTxtDestination;
	private EditText mTxtBody;
	private TextView mLblMessageLength;
	private Button mBtnSend;
	private ImageButton mBtnPickContact;
	private ImageButton mBtnGetLastSmsReceivedNumber;
	private TextView mLblProvider;

	private List<ContactPhone> mPhonesToShowInDialog;
	private String mCaptchaStorage;

	private SendMessageThread mSendMessageThread;
	private SendCaptchaThread mSendCaptchaThread;
	
	private LogFacility mLogFacility;
	private ActivityHelper mActivityHelper;
	private AppPreferencesDao mAppPreferencesDao;
	private LogicManager mLogicManager;
    private SmsDao mSmsDao;

	
	
	//---------- Public properties




	//---------- Events
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLogFacility = checkNotNull(RainbowServiceLocator.get(LogFacility.class), "LogFacility");
        mLogFacility.logStartOfActivity(this.getClass(), savedInstanceState);
        mActivityHelper = checkNotNull(RainbowServiceLocator.get(ActivityHelper.class), "ActivityHelper");
        mAppPreferencesDao = checkNotNull(RainbowServiceLocator.get(AppPreferencesDao.class), "AppPreferencesDao");
        mLogicManager = checkNotNull(RainbowServiceLocator.get(LogicManager.class), "LogicManager");
        mSmsDao = checkNotNull(RainbowServiceLocator.get(SmsDao.class), "SmsDao");

        //checks for app validity
    	if (App.i().isAppExpired()) {
    		mLogFacility.i("App expired");
    		//application is expired
            setContentView(R.layout.actexpired);
            setTitle(String.format(
            		getString(R.string.actexpired_title), App.i().getAppName()));
    		mActivityHelper.showInfo(this, R.string.common_msg_appExpired);
    		return;
    	}
    	
        setContentView(R.layout.actsendsms);
        setTitle(String.format(
        		getString(R.string.actsendsms_title), App.i().getAppName()));

        mSpiProviders = (Spinner) findViewById(R.id.actsendsms_spiProviders);
        mSpiSubservices = (Spinner) findViewById(R.id.actsendsms_spiServices);
        mTxtDestination = (EditText) findViewById(R.id.actsendsms_txtDestination);
        mTxtBody = (EditText) findViewById(R.id.actsendsms_txtMessage);
        mLblMessageLength = (TextView) findViewById(R.id.actsendsms_lblMessageLength);
        mLblProvider = (TextView) findViewById(R.id.actsendsms_lblProvider);
        mBtnSend = (Button) findViewById(R.id.actsendsms_btnSend);
        mBtnPickContact = (ImageButton) findViewById(R.id.actsendsms_btnPickContact);
        mBtnGetLastSmsReceivedNumber = (ImageButton) findViewById(R.id.actsendsms_btnGetLastSmsReceivedNumber);
        
        //eventually remove ad view
        if (!App.i().isAdEnables()) {
        	AdView adView = (AdView) findViewById(R.id.actsendsms_adview);
        	LinearLayout parent = (LinearLayout) adView.getParent();
        	parent.removeView(adView);
        }

        //set listeners
        mSpiProviders.setOnItemSelectedListener(mSpiProvidersSelectedListener);
		mSpiSubservices.setOnItemSelectedListener(mSpiSubservicesSelectedListener);
        mBtnSend.setOnClickListener(mBtnSendClickListener);
        mBtnPickContact.setOnClickListener(mBtnPickContactListener);
        mBtnGetLastSmsReceivedNumber.setOnClickListener(mBtnGetLastSmsReceivedNumberListener);
        mTxtBody.addTextChangedListener(mTxtBodyTextChangedListener);

        //populate Spinner with values
        bindProvidersSpinner();
        
        //hide views
        if (mSmsDao.isInboxSmsProviderAvailable(this)) {
        	mBtnGetLastSmsReceivedNumber.setVisibility(View.INVISIBLE);
        }

    	//executed when the application first runs
        if (null == savedInstanceState) {
            //load values of view from previous application execution
        	restoreLastRunViewValues();
        	//check if the application was called as intent action
        	processIntentData(getIntent());
        	//show info dialog, if needed
        	if (App.i().isFirstRunAfterUpdate())
        		showDialog(DIALOG_STARTUP_INFOBOX);
        }
    }

	@Override
	protected void onStart() {
		super.onStart();
		
//    	if (mSendCrashReport) {
//    		showDialog(DIALOG_SEND_CRASH_REPORTS);
//    	}

		
		Object savedThread = getLastNonConfigurationInstance();
		//nothing saved
		if (null == savedThread) return;
	
		//saved object is a SendMessageThread
		if (savedThread instanceof SendMessageThread) {
			mSendMessageThread = (SendMessageThread) savedThread;
			//register new handler
			mSendMessageThread.registerCallerHandler(mActivityHandler);
		
		//saved object is a SendCaptchaThread
		} else {
			mSendCaptchaThread = (SendCaptchaThread) savedThread;
			//register new handler
			mSendCaptchaThread.registerCallerHandler(mActivityHandler);
		}
	}

	@Override
	protected void onStop() {
		if (null != mSendMessageThread) {
			//unregister handler from background thread
			mSendMessageThread.registerCallerHandler(null);
		} else if (null != mSendCaptchaThread) {
			//unregister handler from background thread
			mSendCaptchaThread.registerCallerHandler(null);
		}
		super.onStop();
	}
		
	@Override
	public Object onRetainNonConfigurationInstance() {
		//save eventually open background thread
		if (null != mSendMessageThread) {
			return mSendMessageThread;
		} else if (null != mSendCaptchaThread) {
			return mSendCaptchaThread;
		}
		return null;
	}
    
    
    @Override
    protected void onPause() {
    	super.onPause();
    	
    	//save current fields value to prefs
    	//provider and subservice was already saved in change event of the spinner
    	mAppPreferencesDao.setLastUsedDestination(
    			TextUtils.isEmpty(mTxtDestination.getText()) ? "" : mTxtDestination.getText().toString());
    	mAppPreferencesDao.setLastUsedMessage(
    			TextUtils.isEmpty(mTxtBody.getText()) ? "" : mTxtBody.getText().toString());
		
		//and save new selected provider
		mAppPreferencesDao.setLastUsedProviderId(mSelectedProvider.getId());
		mAppPreferencesDao.setLastUsedSubserviceId(mSelectedServiceId);
    	mAppPreferencesDao.save();
    }
	
	
	/**
	 * Save volatile data (for example, when the activity is
	 * rotated
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		String phones = ContactDao.instance().SerializeContactPhones(mPhonesToShowInDialog);
		outState.putString(BUNDLEKEY_CONTACTPHONES, phones);
		outState.putString(BUNDLEKEY_CAPTCHASTORAGE, mCaptchaStorage);
	};

	
	@Override
	/**
	 * Load volatile data into inner fields, for example when the
	 * activity is rotated
	 * 
	 * @param savedInstanceState
	 */
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		//restore volatile values
		mCaptchaStorage = savedInstanceState.getString(BUNDLEKEY_CAPTCHASTORAGE);
		mPhonesToShowInDialog = ContactDao.instance().deserializeContactPhones(
				savedInstanceState.getString(BUNDLEKEY_CONTACTPHONES));
		
		//when the activity is rotated, in the onPause event the values of
		//provider, text values and subservice are persisted, so i can rely on this value
		//for reassigning them
		restoreLastRunViewValues();
	}

	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	if (!super.onCreateOptionsMenu(menu)) return false;
    	
    	menu.add(0, OPTIONMENU_ABOUT, 4, R.string.actsendsms_mnuAbout)
    		.setIcon(android.R.drawable.ic_menu_info_details);
    	//menu ends here if the application is expired
    	if (App.i().isAppExpired()) return true;

    	menu.add(0, OPTIONMENU_SIGNATURE, 0, R.string.actsendsms_mnuSignature)
			.setIcon(android.R.drawable.ic_menu_edit);
    	menu.add(0, OPTIONMENU_TEMPLATES, 0, R.string.actsendsms_mnuTemplates)
			.setIcon(R.drawable.ic_menu_friendslist);
    	menu.add(0, OPTIONMENU_COMPRESS, 1, R.string.actsendsms_mnuCompress)
    		.setIcon(R.drawable.ic_menu_cut);
		menu.add(0, OPTIONMENU_RESETDATA, 2, R.string.actsendsms_mnuResetData)
			.setIcon(android.R.drawable.ic_menu_delete);
    	menu.add(0, OPTIONMENU_SETTINGS, 3, R.string.actsendsms_mnuSettings)
		.setIcon(android.R.drawable.ic_menu_preferences);
		
		return true;    	
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case OPTIONMENU_SETTINGS:
			mActivityHelper.openSettingsMain(this);
			break;
			
		case OPTIONMENU_ABOUT:
			mActivityHelper.openAbout(this);
			break;

		case OPTIONMENU_SIGNATURE:
			addSignature();
			break;

		case OPTIONMENU_COMPRESS:
			String message = mTxtBody.getText().toString();
			mActivityHelper.openCompactMessage(this, message);
			break;

		case OPTIONMENU_RESETDATA:
			cleanDataFields(true);
			break;
			
		case OPTIONMENU_TEMPLATES:
			showDialog(DIALOG_TEMPLATES);
			break;
			
		}
		return super.onOptionsItemSelected(item);
	}
    

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	//process cancel action from various activities
    	if (RESULT_OK != resultCode) {
    		if (ActivityHelper.REQUESTCODE_PICKCONTACT == requestCode ||
    				ActivityHelper.REQUESTCODE_COMPACTMESSAGE == requestCode) {
    			return;
    		}
    	}

    	switch (requestCode) {
    		case (ActivityHelper.REQUESTCODE_PICKCONTACT):  
				Uri contactData = data.getData();
    			assignContactPhone(contactData);
    		break;  
    		
    		case (ActivityHelper.REQUESTCODE_COMPACTMESSAGE):  
				String message = data.getStringExtra(ActivityHelper.INTENTKEY_MESSAGE);
    			if (!TextUtils.isEmpty(message))
    				mTxtBody.setText(message);
    		break;
    		
    		case (ActivityHelper.REQUESTCODE_SETTINGS):
    			//refresh subservices list if subservices of a provider was edited
    			if (App.i().getForceSubserviceRefresh()) {
    				App.i().setForceSubserviceRefresh(false);
    				changeProvider(mSelectedProvider, true);
    			}
    		break;  
    	}  
	}


    /* (non-Javadoc)
     * @see android.app.Activity#onCreateDialog(int)
     */
    @Override
    protected Dialog onCreateDialog(int id) {
    	Dialog retDialog = null;
    	
    	switch (id) {
    	case DIALOG_PHONES:
    		retDialog = createPhonesDialog();
    		break;
    		
    	case DIALOG_TEMPLATES:
    		retDialog = createTemplatesDialog();
    		break;
    		
    	case DIALOG_CAPTCHA_REQUEST:
    		retDialog = createCaptchaDialog(mCaptchaStorage);
    		break;
    		
    	case DIALOG_SENDING_MESSAGE:
    		retDialog = mActivityHelper.createProgressDialog(this, 0, R.string.actsendsms_msg_sendingMessage);
    		break;
    		
    	case DIALOG_SENDING_CAPTCHA:
    		retDialog = mActivityHelper.createProgressDialog(this, 0, R.string.actsendsms_msg_sendingCaptcha);
    		break;
    		
    	case DIALOG_STARTUP_INFOBOX:
    		retDialog = mActivityHelper.createInformativeDialog(this,
    				this.getString(R.string.actsendsms_msg_infobox_title),
    				this.getString(R.string.actabout_lblDescription) + "\n\n" + this.getString(R.string.actabout_msgChangeslog),
    				this.getString(R.string.common_btnOk));
    		break;
    	
		default:
			retDialog = super.onCreateDialog(id);
    	}
    	
    	return retDialog;
    }
    
    
    private OnItemSelectedListener mSpiProvidersSelectedListener = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			SmsProvider provider = (SmsProvider) parent.getItemAtPosition(pos);
			changeProvider(provider, false);
		}

		public void onNothingSelected(AdapterView<?> parent) {
          // Do nothing.
		}
	};    
    
    private OnItemSelectedListener mSpiSubservicesSelectedListener = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			SmsService service = (SmsService) parent.getItemAtPosition(pos);
			changeService(service);
		}

		public void onNothingSelected(AdapterView<?> parent) {
          // Do nothing.
		}
	};
	
	private OnClickListener mBtnPickContactListener = new OnClickListener() {
		public void onClick(View v) {
    		//launch the pick contact intent
        	mActivityHelper.openPickContact(ActSendSms.this);
		}
	};
	
	
	private OnClickListener mBtnGetLastSmsReceivedNumberListener = new OnClickListener() {
		public void onClick(View v) {
			ResultOperation<String> res = mSmsDao.getLastSmsReceivedNumber(ActSendSms.this);
			if (res.hasErrors()) {
				mActivityHelper.reportError(ActSendSms.this, R.string.actsendsms_msg_error_retrieving_inbox_sms);
				return;
			}
			
			if (TextUtils.isEmpty(res.getResult())) {
				mActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_empty_inbox_sms);
			} else {
				mTxtDestination.setText(res.getResult());
			}
		}
	};
	
	
	private OnClickListener mBtnSendClickListener = new OnClickListener() {
		public void onClick(View v) {
			sendMessage();
		}
	};
	
	private TextWatcher mTxtBodyTextChangedListener = new TextWatcher() {
		
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			updateMessageLength();
		}
		
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}
		
		public void afterTextChanged(Editable s) {
		}
	};


	/**
	 * Hander to call when a message is sent or a captcha code is inserted
	 */
	private Handler mActivityHandler = new Handler() {
		public void handleMessage(Message msg)
		{
			mLogFacility.i("Returned to ActSendSms from external thread");
			//check if the message is for this handler
			if (msg.what != SendMessageThread.WHAT_SENDMESSAGE && 
					msg.what != SendCaptchaThread.WHAT_SENDCAPTCHA)
				return;
			
			RainbowResultOperation<String> res;
			switch (msg.what) {
			case SendMessageThread.WHAT_SENDMESSAGE:
				//pass data to method
				res = mSendMessageThread.getResult();
				mSendMessageThread = null;
				sendMessageComplete(res);
				break;

			case SendCaptchaThread.WHAT_SENDCAPTCHA:
				//pass data to method
				res = mSendCaptchaThread.getResult();
				mSendCaptchaThread = null;
				displayMessageSendResult(res, true);
				break;

			}
		};
	};




	//---------- Public methods




    //---------- Private methods
	private void bindProvidersSpinner()
	{
		ArrayAdapter<SmsProvider> adapter = new ArrayAdapter<SmsProvider>(this,
				android.R.layout.simple_spinner_item, App.i().getProviderList());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpiProviders.setAdapter(adapter);
	}
    
	private void bindSubservicesSpinner(SmsProvider provider)
	{
		List<SmsService> subservices = provider.getAllSubservices();

		//display a message when subservices are not configured
		if (null == subservices || subservices.size() <= 0)
			mActivityHelper.showInfo(this, String.format(
					getString(R.string.actsendsms_msg_subservicesNotPresent), mSelectedProvider.getName()));
		
		ArrayAdapter<SmsService> adapter = new ArrayAdapter<SmsService>(this,
				android.R.layout.simple_spinner_item, subservices);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpiSubservices.setAdapter(adapter);
	}

	/**
	 * Called when the selected provider changes
	 * @param provider
	 * @param forceRefresh refresh the provider and service list also if
	 *        the same provider was selected
	 */
	private void changeProvider(SmsProvider provider, boolean forceRefresh)
	{
		//already selected provider
		if (!forceRefresh && provider == mSelectedProvider) return;
		
		//first, check if selected service has subservices
		if (provider.hasSubServices()) {
			mSelectedProvider = provider;
			changeService(null);
			
			//configure subservice spinner
			bindSubservicesSpinner(provider);
			mSpiSubservices.setVisibility(View.VISIBLE);
			mLblProvider.setText(R.string.actsendsms_lblProviderMulti);

		} else {
			mSelectedProvider = provider;
			mSelectedServiceId = provider.getId();

			mSpiSubservices.setVisibility(View.GONE);
			mLblProvider.setText(R.string.actsendsms_lblProviderSingle);
			updateMessageLength();
		}
	}

	/**
	 * Called when the selected subservice changes
	 * @param service
	 */
	private void changeService(SmsService service)
	{
		String newServiceId = null != service ? service.getId() : null;

		//already selected subservice
		if (newServiceId == mSelectedServiceId) return;
		
		mSelectedProvider.setSelectedSubservice(newServiceId);
		mSelectedServiceId = newServiceId;
		updateMessageLength();
	}

	
	private void updateMessageLength(){
		int maxLength = 0;

		if (null != mSelectedProvider)
			maxLength = mSelectedProvider.getMaxMessageLenght();

		//set length text
		mLblMessageLength.setText(mTxtBody.length() + "/" + maxLength);
		//set text color
		boolean tooLong = mTxtBody.length() > maxLength;
		if (tooLong)
			mLblMessageLength.setTextColor(Color.RED);
		else
			mLblMessageLength.setTextColor(Color.GRAY);
	}
	
	
	/**
	 * Assign an phone number to destination view
	 * @param contactUri 
	 */
	private void assignContactPhone(Uri contactUri)
	{
		try{
			//get phone numbers for selected contact
			mPhonesToShowInDialog = ContactDao.instance().getContactNumbers(this, contactUri);
		} catch (Exception e) {
			mPhonesToShowInDialog = null;
			mActivityHelper.reportError(this, String.format(getText(R.string.common_msg_genericError).toString(), e.getMessage()));
		}
		
		if (null == mPhonesToShowInDialog)
			return;
		
		switch (mPhonesToShowInDialog.size()){

			//no phones for the contact selected
			case 0:
				mActivityHelper.showInfo(this, R.string.actsendsms_msg_noPhoneNumber);
				break;

			//contact has only one phone number
			case 1:
				mTxtDestination.setText(mPhonesToShowInDialog.get(0).getNumber());
				break;
		
			//contact has more than one phone number
			default:
				showDialog(DIALOG_PHONES);
				break;
		}
		
	}
	
	/**
	 * Create a list dialog box that shows all the contact phone numbers
	 * @return
	 */
	private Dialog createPhonesDialog()
	{
		//create the phone numbers selections
		//final CharSequence[] items = {"Red", "Green", "Blue"};
		int i=0;
		CharSequence[] items = new CharSequence[mPhonesToShowInDialog.size()];
		
		for (ContactPhone phone : mPhonesToShowInDialog) {
			items[i++] = getTranslationForPhoneType(phone.getType()) + ": " + phone.getNumber();
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getText(R.string.actsendsms_dlgPickNumber));
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		        mTxtDestination.setText(mPhonesToShowInDialog.get(item).getNumber());
		        removeDialog(DIALOG_PHONES);
		    }
		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
		        removeDialog(DIALOG_PHONES);
			}
		});
		AlertDialog alert = builder.create();
		
		return alert;
	}


	/**
	 * Create dialog for captcha code
	 * 
	 * @return
	 */
	private Dialog createCaptchaDialog(final String providerReply)
	{
		ResultOperation<Object> res = mSelectedProvider.getCaptchaContentFromProviderReply(providerReply);
		if (res.hasErrors()) {
			//show errors
			mActivityHelper.showInfo(this, res.getResult().toString());
			//and returns with no dialog created
			return null;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog alertDialog;

		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(
				R.layout.dlgcaptcha, (ViewGroup) findViewById(R.id.dlgcaptcha_layoutroot));
		
		//load the image
		ImageView mImgCaptcha = (ImageView) layout.findViewById(R.id.dlgcaptcha_imgCaptcha);
		byte[] imageData = (byte[]) res.getResult();
		mLogFacility.i("Captcha lenght: " + imageData.length);
		BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inSampleSize = 1;
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);
		mImgCaptcha.setImageBitmap(bitmap);
		
		//
		final EditText mTxtCode = (EditText) layout.findViewById(R.id.dlgcaptcha_txtCode);

        ((Button) layout.findViewById(R.id.dlgcaptcha_btnOk)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				removeDialog(DIALOG_CAPTCHA_REQUEST);
				String code = mTxtCode.getText().toString();
				sendCaptcha(providerReply, code);
			}
		});
        ((Button) layout.findViewById(R.id.dlgcaptcha_btnCancel)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				removeDialog(DIALOG_CAPTCHA_REQUEST);
			}
		});

		builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		alertDialog = builder.create();
		alertDialog.setTitle(R.string.dlgcaptcha_title);
		return alertDialog;
	}
	

	/**
	 * Associates phone type to a label
	 * 
	 * @param type
	 * @return
	 */
	private String getTranslationForPhoneType(String type)
	{
		if ("1".equals(type))
			return getString(R.string.actsendsms_phoneType_home);
		else if ("2".equals(type))
			return getString(R.string.actsendsms_phoneType_mobile);
		else if ("3".equals(type))
			return getString(R.string.actsendsms_phoneType_work);
		else if ("4".equals(type))
			return getString(R.string.actsendsms_phoneType_workfax);
		else if ("5".equals(type))
			return getString(R.string.actsendsms_phoneType_homefax);
		else
			return getString(R.string.actsendsms_phoneType_other);
	}
	
	/**
	 * Add signature to current message
	 */
	private void addSignature() {
		String signature = mAppPreferencesDao.getSignature();
		String message = mTxtBody.getText().toString();

		//check if the signature was already added
		if (!message.endsWith(signature))
			mTxtBody.append(signature);
	}
	
	
	/**
	 * Called when activity is first loaded, restore
	 * previous status of input views. Called also when the activity is rotated,
	 * because not always the text view values are preserver
	 */
	private void restoreLastRunViewValues()
	{
		if (App.i().isAppExpired()) return;
		
		//text and message
		mTxtDestination.setText(mAppPreferencesDao.getLastUsedDestination());
		mTxtBody.setText(mAppPreferencesDao.getLastUsedMessage());

		//reassign spinner values and status of class inner fields
		String providerId = mAppPreferencesDao.getLastUsedProviderId();
		String subserviceId = mAppPreferencesDao.getLastUsedSubserviceId();

		//assign provider
		SmsProvider provider = GlobalHelper.findProviderInList(App.i().getProviderList(), providerId);
		changeProvider(provider, false);
		//i cannot rely on the call to changeProvider inside the SelectionChangeListener event
		//in the provider spinner, because is execute at the end of this method, but i need it
		//before assign subservice
		int providerPos = GlobalHelper.findProviderPositionInList(App.i().getProviderList(), providerId);
		if (providerPos >= 0) mSpiProviders.setSelection(providerPos);
		
		if (null != provider && provider.hasSubServices() && !TextUtils.isEmpty(subserviceId)){
			int subservicePos = mSelectedProvider.findSubservicePositionInList(subserviceId);
			if (subservicePos >= 0) mSpiSubservices.setSelection(subservicePos);
			//call changeSubservice after this function, but it's ok now
		}
	}


	/**
	 * Send message
	 */
	private void sendMessage()
	{
		//check if can send another SMS
		if (!mLogicManager.checkIfCanSendSms()) {
			mActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_smsLimitReach, Toast.LENGTH_LONG);
			return;
		}
		
		//check provider
		if (null == mSelectedProvider) {
			mActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_noProviderSelected);
			return;
		}
		
		//check provider parameters
		if (!mSelectedProvider.hasParametersConfigured()) {
			mActivityHelper.showInfo(ActSendSms.this, String.format(
					getString(R.string.actsendsms_msg_providerNotConfigured), mSelectedProvider.getName()));
			return;
		}
		
		//check service
		if (mSelectedProvider.hasSubServices()){
			//checks if a subservices is selected
			if (TextUtils.isEmpty(mSelectedServiceId)) {
				mActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_noSubserviceSelected);
				return;
			}
			//check if service has parameters configured
			if (!mSelectedProvider.hasServiceParametersConfigured(mSelectedServiceId)) {
				mActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_subserviceNotConfigured);
				return;
			}
		}
		
		
		//check destination number
		if (TextUtils.isEmpty(mTxtDestination.getText())) {
			mActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_noDestination);
			return;
		}
		
//		//check destination format
//		if (!TextUtils.isDigitsOnly(mTxtDestination.getText())) {
//			ActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_wrongDestination);
//			return;
//		}
		
		//check body
		if (TextUtils.isEmpty(mTxtBody.getText())) {
			mActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_noMessage);
			return;
		}
		
		//check message length
		if (mTxtBody.length() > mSelectedProvider.getMaxMessageLenght()) {
			mActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_messageTooLong);
			return;
		}
		
		//log the message sending
		String destination = mTxtDestination.getText().toString().trim();
		String message = mTxtBody.getText().toString();
		StringBuilder logMessage = new StringBuilder();
		logMessage.append("Sending message to " + RainbowStringHelper.scrambleNumber(destination) + " using provider " + mSelectedProvider.getId());
		SmsService service = mSelectedProvider.getSubservice(mSelectedServiceId);
		if (null != service) {
			logMessage.append(" and service ")
				.append(service.getName())
				.append(" (id: ")
				.append(mSelectedServiceId)
				.append(" - templateId ")
				.append(service.getTemplateId())
				.append(")");
		}
		mLogFacility.i(logMessage.toString());

		//create new progress dialog
		showDialog(DIALOG_SENDING_MESSAGE);

		//preparing the background task for sending message
		mSendMessageThread = new SendMessageThread(
				this, mActivityHandler,
				mSelectedProvider, mSelectedServiceId,
				destination, message);
		mSendMessageThread.start();
		//at the end of the execution, the activity handler will be called
	}
	
	
	/**
	 * Send the captcha code to the server
	 * @param providerReply
	 * @param captchaCode
	 */
	private void sendCaptcha(String providerReply, String captchaCode)
	{
		mLogFacility.i("Captcha code: " + captchaCode);
		if (TextUtils.isEmpty(captchaCode)) {
			mActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_emptyCaptchaCode);
			return;
		}

		//create new progress dialog
		showDialog(DIALOG_SENDING_CAPTCHA);
		
		//preparing the background task for sending captcha code
		mSendCaptchaThread = new SendCaptchaThread(
				this, mActivityHandler,
				mSelectedProvider,
				providerReply,
				captchaCode);
		//and execute the command
		mSendCaptchaThread.start();
		//at the end of the execution, a caller to handler will be make
	}
	
	
	/**
	 * Reset destination and message body
	 */
	private void cleanDataFields(boolean force) {
		if (force || mAppPreferencesDao.getAutoClearMessage()) {
			mTxtDestination.setText("");
			mTxtBody.setText("");
		}
	}
	
	
	/**
	 * Called when the background activity has sent the message
	 * @param result result of sending message
	 */
	private void sendMessageComplete(RainbowResultOperation<String> result)
	{
		//dismiss progress dialog
		removeDialog(DIALOG_SENDING_MESSAGE);
		
		//captcha required
		if (ResultOperation.RETURNCODE_SMS_CAPTCHA_REQUEST == result.getReturnCode()) {
			//save captcha data
			mCaptchaStorage = result.getResult();
			mLogFacility.i("Captcha request from provider: " + mCaptchaStorage);
			//launch captcha request
			showDialog(DIALOG_CAPTCHA_REQUEST);
		
		//return with errors or message sent
		} else  {
			displayMessageSendResult(result, false);
		}
	}

	
	/**
	 * Display result of the send message action, eventually processing low level errors
	 * 
	 * @param result
	 */
	private void displayMessageSendResult(RainbowResultOperation<String> result, boolean returnFromCaptcha)
	{
		if (returnFromCaptcha) {
			//dismiss captcha progress dialog
			removeDialog(DIALOG_SENDING_CAPTCHA);
		}
		
		//return with errors
		if (result.hasErrors()) {
			mActivityHelper.reportError(ActSendSms.this, result);
		} else {
			mLogFacility.i(result.getResult());
			//display returning message of the provider
			mActivityHelper.showInfo(ActSendSms.this, result.getResult(), Toast.LENGTH_LONG);

			//only if sms was sent
			if (ResultOperation.RETURNCODE_OK == result.getReturnCode()) {
				mLogFacility.i("Sms correctly sent");
				//update number of messages sent in the day
				mLogicManager.updateSmsCounter(1);
				//insert sms into pim, if required
				insertSmsIntoPim();
				//check if the text should be deleted
				cleanDataFields(false);
			}
		}
	}

	/**
	 * Insert sent SMS into PIM of the device
	 */
	private void insertSmsIntoPim() {
		//insert SMS into PIM
		if (mAppPreferencesDao.getInsertMessageIntoPim()) {
			ResultOperation<Void> res = mSmsDao.saveSmsInSentFolder(
					getApplicationContext(),
					mTxtDestination.getText().toString(),
					mTxtBody.getText().toString());

			if (res.hasErrors()) {
				mActivityHelper.reportError(getApplicationContext(), R.string.actsendsms_msg_error_saving_sent_sms);
			}
		}
	}
	
	/**
	 * Called when the activity first start, process data passed via intent in reply to
	 * intent-filter android.intent.action.SENDTO or android.intent.action.SEND 
	 * 
	 * @param intent 
	 * 
	 */
	private void processIntentData(Intent intent)
	{
		if (null == intent || null == intent.getExtras()) return;
	
		String destination = intent.getExtras().getString(ActivityHelper.INTENTKEY_MESSAGE_DESTIONATION);
        String text = intent.getExtras().getString(ActivityHelper.INTENTKEY_MESSAGE_TEXT);
        
        if (!TextUtils.isEmpty(destination))
            mTxtDestination.setText(destination);
        if (!TextUtils.isEmpty(text))
            mTxtBody.setText(text);
	}

	
	/**
	 * Create a list dialog box that shows all the templates available
	 * @return
	 */
	private Dialog createTemplatesDialog()
	{
		String[] templates = mAppPreferencesDao.getMessageTemplates();
		CharSequence[] items = new CharSequence[templates.length];
		
		int i=0;
		for (String template : templates) {
			items[i++] = template;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getText(R.string.actsendsms_dlgPickTemplate));
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		        String template = (String) ((AlertDialog) dialog).getListView().getItemAtPosition(item);
				mTxtBody.append(template);
		        removeDialog(DIALOG_TEMPLATES);
		    }
		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
		        removeDialog(DIALOG_TEMPLATES);
			}
		});
		AlertDialog alert = builder.create();
		
		return alert;
	}
}