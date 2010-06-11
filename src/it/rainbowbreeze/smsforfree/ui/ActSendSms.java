package it.rainbowbreeze.smsforfree.ui;

import java.util.List;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.SmsForFreeApplication;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.data.ContactDao;
import it.rainbowbreeze.smsforfree.domain.ContactPhone;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.logic.LogicManager;
import it.rainbowbreeze.smsforfree.logic.SendStatisticsAsyncTask;
import it.rainbowbreeze.smsforfree.util.GlobalUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.AdapterView.OnItemSelectedListener;

public class ActSendSms
	extends Activity
{
	//---------- Private fields
	private static final int DIALOG_PHONES = 10;
	private final static int DIALOG_CAPTCHA = 11;
	
	private final static String BUNDLEKEY_CONTACTPHONES = "ContactPhones";
	private final static String BUNDLEKEY_CAPTCHASTORAGE = "CaptchaStorage";




	//---------- Private fields
	private final static int OPTIONMENU_EXIT = 1;
	private final static int OPTIONMENU_SETTINGS = 2;
	private final static int OPTIONMENU_SIGNATURE = 3;
	private final static int OPTIONMENU_ABOUT = 4;
	private final static int OPTIONMENU_RESETDATA = 5;
	private final static int OPTIONMENU_COMPRESS = 6;
	private final static int OPTIONMENU_DIALOG = 7;

	
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
            setTitle(R.string.actexpired_title);
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


    	//executed when the app first runs
        if (null == savedInstanceState) {
        	//send statistics data first time the app runs
	        SendStatisticsAsyncTask statsTask = new SendStatisticsAsyncTask();
	        statsTask.execute(this);
            //load values of view from previous application execution
        	restoreLastRunViewValues();
        	
    	//executed when the activity is reloaded (rotate, for example)
        } else {
        	//load volatile data
        	reloadVolatileData(savedInstanceState);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
		menu.add(0, OPTIONMENU_ABOUT, 4, R.string.actsendsms_mnuAbout)
		.setIcon(android.R.drawable.ic_menu_info_details);
    	if (SmsForFreeApplication.instance().isAppExpired()) return true;

    	menu.add(0, OPTIONMENU_SIGNATURE, 0, R.string.actsendsms_mnuSignature)
			.setIcon(android.R.drawable.ic_menu_edit);
    	//menu.add(0, OPTIONMENU_COMPRESS, 1, R.string.actsendsms_mnuCompress);
		menu.add(0, OPTIONMENU_RESETDATA, 2, R.string.actsendsms_mnuResetData)
			.setIcon(android.R.drawable.ic_menu_delete);
    	menu.add(0, OPTIONMENU_SETTINGS, 3, R.string.actsendsms_mnuSettings)
		.setIcon(android.R.drawable.ic_menu_preferences);
//		menu.add(0, OPTIONMENU_EXIT, 4, R.string.actsendsms_menuExit)
//			.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
//		menu.add(0, OPTIONMENU_DIALOG, 5, "Test dialog");
		
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
			if (mTxtMessage.length() <= 0)
				ActivityHelper.showInfo(this, R.string.actsendsms_msg_noMessage);
			else
				ActivityHelper.openCompactMessage(this, mTxtMessage.getText().toString());
			break;

		case OPTIONMENU_RESETDATA:
			cleanDataFields();
			break;
			
		case OPTIONMENU_DIALOG:
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
    		
    	case DIALOG_CAPTCHA:
    		retDialog = createCaptchaDialog(mCaptchaStorage);
    		break;
    		
		default:
			retDialog = super.onCreateDialog(id);
    	}
    	
    	return retDialog;
    }
    
    
    @Override
    protected void onPause() {
    	super.onPause();
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
	 * Save volatile data (for example, when the activity is
	 * rotated
	 */
	protected void onSaveInstanceState(Bundle outState)
	{
		String phones = ContactDao.instance().SerializeContactPhones(mPhonesToShowInDialog);
		outState.putString(BUNDLEKEY_CONTACTPHONES, phones);
		outState.putString(BUNDLEKEY_CAPTCHASTORAGE, mCaptchaStorage);
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
		ResultOperation res = mSelectedProvider.getCaptchaContentFromProviderReply(providerReply);
		if (res.HasErrors()) {
			//show errors
			ActivityHelper.showInfo(this, res.getResultAsString());
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
		byte[] imageData = res.getResultAsByteArray();
		BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inSampleSize = 1;
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);
		mImgCaptcha.setImageBitmap(bitmap);
		
		//
		final EditText mTxtCode = (EditText) layout.findViewById(R.id.dlgcaptcha_txtCode);

        ((Button) layout.findViewById(R.id.dlgcaptcha_btnOk)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				removeDialog(DIALOG_CAPTCHA);
				String code = mTxtCode.getText().toString();
				sendCaptcha(providerReply, code);
			}
		});
        ((Button) layout.findViewById(R.id.dlgcaptcha_btnCancel)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				removeDialog(DIALOG_CAPTCHA);
			}
		});

		builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		alertDialog = builder.create();
		alertDialog.setTitle(R.string.dlgcaptcha_title);
		return alertDialog;
	}
	

	private String getTranslationForPhoneType(String type) {
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
	 * 
	 */
	private void restoreLastRunViewValues() {
		//text and message
		mTxtDestination.setText(AppPreferencesDao.instance().getLastUsedDestination());
		mTxtMessage.setText(AppPreferencesDao.instance().getLastUsedMessage());

		//reassing spinner values and status of class inner fields
		reloadSpinnerValues();
	}
    
    
	
	/**
	 * Reassign spinner values and class inner fields value.
	 * Used when activity is loaded for the first time and when is rotated
	 * 
	 * @param providerId
	 * @param subserviceId
	 */
	private void reloadSpinnerValues() {
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
		
		
		//preparing the background task for sending message
		SendMessageTask task = new SendMessageTask(
				this,
				getString(R.string.actsendsms_msg_sendingMessage),
				mSelectedProvider, mSelectedServiceId);
		//and send the message
		task.execute(mTxtDestination.getText().toString(), mTxtMessage.getText().toString());
		//at the end of the execution, the sendMessageComplete() method will be called
	}
	
	
	/**
	 * Called when the background activity has sent the message
	 * @param result result of sending message
	 */
	private void sendMessageComplete(ResultOperation result)
	{
		//return with errors
		if (result.HasErrors()) {
			ActivityHelper.reportError(ActSendSms.this, String.format(
					//TODO
					//change standard error message
					getString(R.string.common_msg_genericError), result.getException().getMessage()));
			return;
		}
		
		//message sent
		if (ResultOperation.RETURNCODE_OK == result.getReturnCode()) {
			//display returning message of the provider
			ActivityHelper.showInfo(ActSendSms.this, result.getResultAsString());
			//update number of messages sent in the day
			LogicManager.updateSmsCounter();
			//check if the text should be deleted
			if (AppPreferencesDao.instance().getAutoClearMessage()) {
				cleanDataFields();
			}
			return;
		}
		
		//captcha required
		if (ResultOperation.RETURNCODE_CAPTCHA_REQUEST == result.getReturnCode()) {
			//save captcha data
			mCaptchaStorage = result.getResultAsString();
			//launch captcha request
			showDialog(DIALOG_CAPTCHA);
			return;
		}
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
		
		//preparing the background task for sending captcha code
		SendCaptchaTask task = new SendCaptchaTask(
				this,
				getString(R.string.actsendsms_msg_sendingCaptcha),
				mSelectedProvider);
		//and send the captcha
		task.execute(providerReply, captchaCode);
		//at the end of the execution, the sendCaptchaComplete() method will be called
	}

	
	/**
	 * Called by AsyncTask when the captcha sending completed
	 * @param res
	 */
	private void sendCaptchaComplete(ResultOperation res) {
		//update number of messages sent in the day
		LogicManager.updateSmsCounter();
		//show the results of the send
		ActivityHelper.showCommandExecutionResult(this.getBaseContext(), res);
	}
	
	
	/**
	 * Reset destination and message body
	 */
	private void cleanDataFields() {
		mTxtDestination.setText("");
		mTxtMessage.setText("");
	}
	

	/**
	 * Load volatile data into inner fields, for example when the
	 * activity is rotated
	 * 
	 * @param savedInstanceState
	 */
	private void reloadVolatileData(Bundle savedInstanceState) {
		//restore volatile values
		mCaptchaStorage = savedInstanceState.getString(BUNDLEKEY_CAPTCHASTORAGE);
		mPhonesToShowInDialog = ContactDao.instance().deserializeContactPhones(
				savedInstanceState.getString(BUNDLEKEY_CONTACTPHONES));
		
		//when the activity is rotated, in the onPause event the values of
		//provider, text values and subservice are persisted, so i can rely on this value
		//for reassigning them
		restoreLastRunViewValues();
	}


	/**
	 * Send a sms
	 */
	private class SendMessageTask
		extends ProgressDialogAsyncTask
	{
	
		//---------- Ctors
		public SendMessageTask(Context context, String progressTitle, SmsProvider provider, String servideId)
		{
			super(context, progressTitle);
			mProvider = provider;
			mServiceId = servideId;
		}
		
		//---------- Private fields
		private SmsProvider mProvider;
		private String mServiceId;
		



		//---------- Private methods
		protected ResultOperation doInBackground(String... params)
		{
			//params.length must be equals to 2
				
			String destination = params[0];
			String messageBody = params[1];
			
			return mProvider.sendMessage(mServiceId, destination, messageBody);
		}
		
		@Override
		protected void onPostExecute(ResultOperation result) {
			//close progress dialog
			super.onPostExecute(result);
			//and pass the control to caller activity with the result
			sendMessageComplete(result);
		}
	}

	/**
	 * Send a captcha
	 */
	private class SendCaptchaTask
		extends ProgressDialogAsyncTask
	{
	
		//---------- Ctors
		public SendCaptchaTask(Context context, String progressTitle, SmsProvider provider)
		{
			super(context, progressTitle);
			mProvider = provider;
		}
		
		//---------- Private fields
		private SmsProvider mProvider;
		



		//---------- Private methods
		protected ResultOperation doInBackground(String... params)
		{
			//params.length must be equals to 2
				
			String providerReply = params[0];
			String captchaCode = params[1];
			
			return mProvider.sendCaptcha(providerReply, captchaCode);
		}
		
		@Override
		protected void onPostExecute(ResultOperation result) {
			//close progress dialog
			super.onPostExecute(result);
			//and pass the control to caller activity with the result
			sendCaptchaComplete(result);
		}
	}
	
}