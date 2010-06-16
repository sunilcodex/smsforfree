package it.rainbowbreeze.smsforfree.ui;

import java.util.List;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.common.SmsForFreeApplication;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.data.ContactDao;
import it.rainbowbreeze.smsforfree.domain.ContactPhone;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.logic.LogicManager;
import it.rainbowbreeze.smsforfree.logic.SendCaptchaThread;
import it.rainbowbreeze.smsforfree.logic.SendMessageThread;
import it.rainbowbreeze.smsforfree.logic.SendStatisticsAsyncTask;
import it.rainbowbreeze.smsforfree.util.GlobalUtils;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class ActSendSms
	extends Activity
{
	//---------- Private fields
	private static final int DIALOG_PHONES = 10;
	private final static int DIALOG_CAPTCHA_REQUEST = 11;
	private final static int DIALOG_SENDING_MESSAGE = 12;
	private final static int DIALOG_SENDING_CAPTCHA = 13;
	
	private final static String BUNDLEKEY_CONTACTPHONES = "ContactPhones";
	private final static String BUNDLEKEY_CAPTCHASTORAGE = "CaptchaStorage";




	//---------- Private fields
	private final static int OPTIONMENU_EXIT = 1;
	private final static int OPTIONMENU_SETTINGS = 2;
	private final static int OPTIONMENU_SIGNATURE = 3;
	private final static int OPTIONMENU_ABOUT = 4;
	private final static int OPTIONMENU_RESETDATA = 5;
	private final static int OPTIONMENU_COMPRESS = 6;

	
	private Spinner mSpiProviders;
	private Spinner mSpiSubservices;

	private SmsProvider mSelectedProvider;
	private String mSelectedServiceId;
	private EditText mTxtDestination;
	private EditText mTxtMessage;
	private TextView mLblMessageLength;
	private Button mBtnSend;
	private ImageButton mBtnPickContact;

	private List<ContactPhone> mPhonesToShowInDialog;
	private String mCaptchaStorage;

	private SendMessageThread mSendMessageThread;
	private SendCaptchaThread mSendCaptchaThread;
	

	
	
	
	//---------- Public properties




	//---------- Events
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //checks for app validity
    	if (SmsForFreeApplication.instance().isAppExpired()) {
    		//application is expired
            setContentView(R.layout.actexpired);
            setTitle(String.format(
            		getString(R.string.actexpired_title), SmsForFreeApplication.instance().getAppName()));
    		ActivityHelper.showInfo(this, R.string.common_msg_appExpired);
    		return;
    	}
    	
        setContentView(R.layout.actsendsms);
        setTitle(String.format(
        		getString(R.string.actsendsms_title), SmsForFreeApplication.instance().getAppName()));

        mSpiProviders = (Spinner) findViewById(R.id.actsendsms_spiProviders);
        mSpiSubservices = (Spinner) findViewById(R.id.actsendsms_spiServices);
        mTxtDestination = (EditText) findViewById(R.id.actsendsms_txtDestination);
        mTxtMessage = (EditText) findViewById(R.id.actsendsms_txtMessage);
        mLblMessageLength = (TextView) findViewById(R.id.actsendsms_lblMessageLength);
        mBtnSend = (Button) findViewById(R.id.actsendsms_btnSend);
        mBtnPickContact = (ImageButton) findViewById(R.id.actsendsms_btnPickContact);

        //set listeners
        mSpiProviders.setOnItemSelectedListener(mSpiProvidersSelectedListener);
		mSpiSubservices.setOnItemSelectedListener(mSpiSubservicesSelectedListener);
        mBtnSend.setOnClickListener(mBtnSendClickListener);
        mBtnPickContact.setOnClickListener(mBtnPickContactListener);
        mTxtMessage.addTextChangedListener(mTxtBodyTextChangedListener);

        //populate Spinner with values
        bindProvidersSpinner();


    	//executed when the application first runs
        if (null == savedInstanceState) {
        	//send statistics data first time the app runs
	        SendStatisticsAsyncTask statsTask = new SendStatisticsAsyncTask();
	        statsTask.execute(this);
            //load values of view from previous application execution
        	restoreLastRunViewValues();
        }
    }
	
	@Override
	protected void onStart() {
		super.onStart();
		Object savedThread = getLastNonConfigurationInstance();
		
		//nothing saved
		if (null == savedThread) return;
	
		//saved object is a SendMessageThread
		if (savedThread instanceof SendMessageThread) {
//			mProgressDialog = ActivityHelper.createAndShowProgressDialog(this, R.string.actsendsms_msg_sendingMessage);
			mSendMessageThread = (SendMessageThread) savedThread;
			//register new handler
			mSendMessageThread.registerCallerHandler(mActivityHandler);
		
		//saved object is a SendCaptchaThread
		} else {
//			mProgressDialog = ActivityHelper.createAndShowProgressDialog(this, R.string.actsendsms_msg_sendingCaptcha);
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
    	
    	if (SmsForFreeApplication.instance().isAppExpired()) return;
    	
    	//save current fields value to prefs
    	//provider and subservice was already saved in change event of the spinner
    	AppPreferencesDao.instance().setLastUsedDestination(
    			TextUtils.isEmpty(mTxtDestination.getText()) ? "" : mTxtDestination.getText().toString());
    	AppPreferencesDao.instance().setLastUsedMessage(
    			TextUtils.isEmpty(mTxtMessage.getText()) ? "" : mTxtMessage.getText().toString());
		
		//and save new selected provider
		AppPreferencesDao.instance().setLastUsedProviderId(mSelectedProvider.getId());
		AppPreferencesDao.instance().setLastUsedSubserviceId(mSelectedServiceId);
    	AppPreferencesDao.instance().save();
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
    	super.onCreateOptionsMenu(menu);
    	
		menu.add(0, OPTIONMENU_ABOUT, 4, R.string.actsendsms_mnuAbout)
		.setIcon(android.R.drawable.ic_menu_info_details);
    	if (SmsForFreeApplication.instance().isAppExpired()) return true;

    	menu.add(0, OPTIONMENU_SIGNATURE, 0, R.string.actsendsms_mnuSignature)
			.setIcon(android.R.drawable.ic_menu_edit);
    	menu.add(0, OPTIONMENU_COMPRESS, 1, R.string.actsendsms_mnuCompress);
		menu.add(0, OPTIONMENU_RESETDATA, 2, R.string.actsendsms_mnuResetData)
			.setIcon(android.R.drawable.ic_menu_delete);
    	menu.add(0, OPTIONMENU_SETTINGS, 3, R.string.actsendsms_mnuSettings)
		.setIcon(android.R.drawable.ic_menu_preferences);
		
		return true;    	
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case OPTIONMENU_EXIT:
			finish();
			break;
			
		case OPTIONMENU_SETTINGS:
			ActivityHelper.openSettingsMain(this);
			break;
			
		case OPTIONMENU_ABOUT:
			ActivityHelper.openAbout(this);
			break;

		case OPTIONMENU_SIGNATURE:
			addSignature();
			break;

		case OPTIONMENU_COMPRESS:
			String message = mTxtMessage.getText().toString();
			ActivityHelper.openCompactMessage(this, message);
			break;

		case OPTIONMENU_RESETDATA:
			cleanDataFields();
			break;
			
		}
		return super.onOptionsItemSelected(item);
	}
    

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	//process cancel action from various activities
    	if (RESULT_OK != resultCode) {
    		if (ActivityHelper.REQUESTCODE_PICKCONTACT == requestCode) {
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
    				mTxtMessage.setText(message);
    		break;
    		
    		case (ActivityHelper.REQUESTCODE_SETTINGS):
    			//refresh subservices list if subservices of a provider was edited
    			if (SmsForFreeApplication.instance().getForceSubserviceRefresh()) {
    				SmsForFreeApplication.instance().setForceSubserviceRefresh(false);
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
    		
    	case DIALOG_CAPTCHA_REQUEST:
    		retDialog = createCaptchaDialog(mCaptchaStorage);
    		break;
    		
    	case DIALOG_SENDING_MESSAGE:
    		retDialog = ActivityHelper.createProgressDialog(this, R.string.actsendsms_msg_sendingMessage);
    		break;
    		
    	case DIALOG_SENDING_CAPTCHA:
    		retDialog = ActivityHelper.createProgressDialog(this, R.string.actsendsms_msg_sendingCaptcha);
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
        	ActivityHelper.openPickContact(ActSendSms.this);
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
			//check if the message is for this handler
			if (msg.what != SendMessageThread.WHAT_SENDMESSAGE && 
					msg.what != SendCaptchaThread.WHAT_SENDCAPTCHA)
				return;
			
			ResultOperation<String> res;
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
				android.R.layout.simple_spinner_item, SmsForFreeApplication.instance().getProviderList());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpiProviders.setAdapter(adapter);
	}
    
	private void bindSubservicesSpinner(SmsProvider provider)
	{
		List<SmsService> subservices = provider.getAllSubservices();

		//display a message when subservices are not configured
		if (null == subservices || subservices.size() <= 0)
			ActivityHelper.showInfo(this, String.format(
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

		} else {
			mSelectedProvider = provider;
			mSelectedServiceId = provider.getId();

			mSpiSubservices.setVisibility(View.GONE);
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
		mLblMessageLength.setText(mTxtMessage.length() + "/" + maxLength);
		//set text color
		boolean tooLong = mTxtMessage.length() > maxLength;
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
			ActivityHelper.reportError(this, String.format(getText(R.string.common_msg_genericError).toString(), e.getMessage()));
		}
		
		if (null == mPhonesToShowInDialog)
			return;
		
		switch (mPhonesToShowInDialog.size()){

			//no phones for the contact selected
			case 0:
				ActivityHelper.showInfo(this, R.string.actsendsms_msg_noPhoneNumber);
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
		if (res.HasErrors()) {
			//show errors
			ActivityHelper.showInfo(this, res.getResult().toString());
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
		//TODO
		//check the links between numbers and values
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
		String signature = AppPreferencesDao.instance().getSignature();
		String message = mTxtMessage.getText().toString();

		//check if the signature was already added
		if (!message.endsWith(signature))
			mTxtMessage.append(signature);
	}
	
	
	/**
	 * Called when activity is first loaded, restore
	 * previous status of input views. Called also when the activity is rotated,
	 * because not always the text view values are preserver
	 */
	private void restoreLastRunViewValues()
	{
		if (SmsForFreeApplication.instance().isAppExpired()) return;
		
		//text and message
		mTxtDestination.setText(AppPreferencesDao.instance().getLastUsedDestination());
		mTxtMessage.setText(AppPreferencesDao.instance().getLastUsedMessage());

		//reassign spinner values and status of class inner fields
		String providerId = AppPreferencesDao.instance().getLastUsedProviderId();
		String subserviceId = AppPreferencesDao.instance().getLastUsedSubserviceId();

		//assign provider
		SmsProvider provider = GlobalUtils.findProviderInList(SmsForFreeApplication.instance().getProviderList(), providerId);
		changeProvider(provider, false);
		//i cannot rely on the call to changeProvider inside the SelectionChangeListener event
		//in the provider spinner, because is execute at the end of this method, but i need it
		//before assign subservice
		int providerPos = GlobalUtils.findProviderPositionInList(SmsForFreeApplication.instance().getProviderList(), providerId);
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
		if (!LogicManager.checkIfCanSendSms()) {
			ActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_smsLimitReach);
		}
		
		//check provider
		if (null == mSelectedProvider) {
			ActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_noProviderSelected);
			return;
		}
		
		//check provider parameters
		if (!mSelectedProvider.hasParametersConfigured()) {
			ActivityHelper.showInfo(ActSendSms.this, String.format(
					getString(R.string.actsendsms_msg_providerNotConfigured), mSelectedProvider.getName()));
			return;
		}
		
		//check service
		if (mSelectedProvider.hasSubServices()){
			//checks if a subservices is selected
			if (TextUtils.isEmpty(mSelectedServiceId)) {
				ActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_noSubserviceSelected);
				return;
			}
			//check if service has parameters configured
			if (!mSelectedProvider.hasServiceParametersConfigured(mSelectedServiceId)) {
				ActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_subserviceNotConfigured);
				return;
			}
		}
		
		
		//check destination number
		if (TextUtils.isEmpty(mTxtDestination.getText())) {
			ActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_noDestination);
			return;
		}
		
//		//check destination format
//		if (!TextUtils.isDigitsOnly(mTxtDestination.getText())) {
//			ActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_wrongDestination);
//			return;
//		}
		
		//check body
		if (TextUtils.isEmpty(mTxtMessage.getText())) {
			ActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_noMessage);
			return;
		}
		
		//check message length
		if (mTxtMessage.length() > mSelectedProvider.getMaxMessageLenght()) {
			ActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_messageTooLong);
			return;
		}
		
		//create new progress dialog
		showDialog(DIALOG_SENDING_MESSAGE);

		//preparing the background task for sending message
		String destination = mTxtDestination.getText().toString();
		String message = mTxtMessage.getText().toString();
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
		if (TextUtils.isEmpty(captchaCode)) {
			ActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_emptyCaptchaCode);
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
	private void cleanDataFields() {
		mTxtDestination.setText("");
		mTxtMessage.setText("");
	}
	
	
	/**
	 * Called when the background activity has sent the message
	 * @param result result of sending message
	 */
	private void sendMessageComplete(ResultOperation<String> result)
	{
		//dismiss progress dialog
		removeDialog(DIALOG_SENDING_MESSAGE);
		
		//captcha required
		if (ResultOperation.RETURNCODE_CAPTCHA_REQUEST == result.getReturnCode()) {
			//save captcha data
			mCaptchaStorage = result.getResult();
			//launch captcha request
			showDialog(DIALOG_CAPTCHA_REQUEST);
		
		//return with errors or message sent
		} else if (result.HasErrors() || ResultOperation.RETURNCODE_OK == result.getReturnCode()) {
			displayMessageSendResult(result, false);
		}
	}

	
	/**
	 * Display result of the send message action, eventually processing low level errors
	 * 
	 * @param result
	 */
	private void displayMessageSendResult(ResultOperation<String> result, boolean returnFromCaptcha)
	{
		if (returnFromCaptcha) {
			//dismiss captcha progress dialog
			removeDialog(DIALOG_SENDING_CAPTCHA);
		}

		//return with errors
		if (result.HasErrors()) {
			ActivityHelper.reportError(ActSendSms.this, result);
		} else {
			//display returning message of the provider
			ActivityHelper.showInfo(ActSendSms.this, result.getResult(), Toast.LENGTH_LONG);
			//update number of messages sent in the day
			LogicManager.updateSmsCounter(1);
			//check if the text should be deleted
			if (AppPreferencesDao.instance().getAutoClearMessage()) {
				cleanDataFields();
			}
		}
	}
}