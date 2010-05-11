package it.rainbowbreeze.smsforfree.ui;

import java.util.List;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalBag;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.ContactDao;
import it.rainbowbreeze.smsforfree.domain.ContactPhone;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.logic.LogicManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ActSendSms
	extends Activity
{
	//---------- Ctors




	//---------- Private fields
	
	private static final int DIALOG_PHONES = 10;

	//---------- Private fields
	private final static int OPTIONMENU_EXIT = 1;
	private final static int OPTIONMENU_SETTINGS = 2;
	private final static int OPTIONMENU_ABOUT = 3;

	
	private Spinner mSpiProviders;
	private Spinner mSpiSubservices;

	private SmsProvider mSelectedProvider;
	private String mSelectedServiceId;
	private EditText mTxtDestination;
	private EditText mTxtMessage;
	private TextView mLblMessageLength;
	private Button mBtnSend;
	private ImageButton mBtnPickContact;

	private List<ContactPhone> phonesToShowInDialog;

	
	
	
	//---------- Public properties




	//---------- Events
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actsendsms);
        

        //only when the activity is created for the first time
        if (null == savedInstanceState)
        {
        	LogicManager.executeBeginTask(this);
        }
        
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

        //load default configuration
        //TODO
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    
		menu.add(0, OPTIONMENU_SETTINGS, 0, R.string.actsendsms_menuSettings)
			.setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(0, OPTIONMENU_ABOUT, 2, R.string.actsendsms_menuAbout);
		menu.add(0, OPTIONMENU_EXIT, 3, R.string.actsendsms_menuExit)
			.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		
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
//			ActivityHelper.openAbout(this);
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
    		
		default:
			retDialog = super.onCreateDialog(id);
    	}
    	
    	return retDialog;
    }

    
    private OnItemSelectedListener mSpiProvidersSelectedListener = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			SmsProvider provider = (SmsProvider) parent.getItemAtPosition(pos);
			changeProvider(provider);
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
			//check provider
			if (null == mSelectedProvider) {
				ActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_noProvider);
				return;
			}
			
			//check services
			if (TextUtils.isEmpty(mSelectedServiceId)) {
				ActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_noSubservice);
				return;
			}
			
			//check destination number
			if (TextUtils.isEmpty(mTxtDestination.getText())) {
				ActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_noDestination);
				return;
			}
			
//			//check destination format
//			if (!TextUtils.isDigitsOnly(mTxtDestination.getText())) {
//				ActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_wrongDestination);
//				return;
//			}
			
			//check body
			if (TextUtils.isEmpty(mTxtMessage.getText())) {
				ActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_noMessage);
				return;
			}
			
			//check message length
			if (mTxtDestination.length() > mSelectedProvider.getMaxMessageLenght()) {
				ActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_messageTooLong);
				return;
			}
			
			//send message
			ResultOperation res = mSelectedProvider.sendMessage(
					mSelectedServiceId,
					mTxtDestination.getText().toString(),
					mTxtMessage.getText().toString());
			
			if (res.HasErrors())
				ActivityHelper.showInfo(ActSendSms.this,
						getString(R.string.actsendsms_msg_errorSendingMessage) + "\n" + res.getException().getMessage());
			else
				ActivityHelper.showInfo(ActSendSms.this, R.string.actsendsms_msg_sendOk + "\n" + res.getResultAsString());
			
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




	//---------- Public methods




    //---------- Private methods
	private void bindProvidersSpinner() {
		ArrayAdapter<SmsProvider> adapter = new ArrayAdapter<SmsProvider>(this,
				android.R.layout.simple_spinner_item, GlobalBag.providerList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpiProviders.setAdapter(adapter);
	}
    
	private void bindSubservicesSpinner(SmsProvider provider) {
		List<SmsService> subservices = provider.getConfiguredSubservices();
		ArrayAdapter<SmsService> adapter = new ArrayAdapter<SmsService>(this,
				android.R.layout.simple_spinner_item, subservices);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpiSubservices.setAdapter(adapter);
	}

	private void changeProvider(SmsProvider provider) {
		
		//already selected provider
		if (provider == mSelectedProvider) return;
		
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
	
	private void changeService(SmsService service) {
		String newServiceId = null != service ? service.getId() : null;

		if (newServiceId == mSelectedServiceId) return;
		mSelectedProvider.setSelectedSubservice(newServiceId);
		mSelectedServiceId = newServiceId;
		updateMessageLength();
	}

	
	private void updateMessageLength(){
		int maxLength = 0;

		if (null != mSelectedProvider)
			maxLength = mSelectedProvider.getMaxMessageLenght();

		mLblMessageLength.setText(mTxtMessage.getText().length() + "/" + maxLength);
	}
	
	/**
	 * Assign an phone number to destination view
	 * @param contactUri 
	 */
	private void assignContactPhone(Uri contactUri)
	{
		try{
			//get phone numbers for selected contact
			phonesToShowInDialog = ContactDao.instance().getContactNumbers(this, contactUri);
		} catch (Exception e) {
			phonesToShowInDialog = null;
			ActivityHelper.reportError(this, String.format(getText(R.string.common_msg_genericError).toString(), e.getMessage()));
		}
		
		if (null == phonesToShowInDialog)
			return;
		
		switch (phonesToShowInDialog.size()){

			//no phones for the contact selected
			case 0:
				ActivityHelper.showInfo(this, R.string.actsendsms_msg_noPhoneNumber);
				break;

			//contact has only one phone number
			case 1:
				mTxtDestination.setText(phonesToShowInDialog.get(0).getNumber());
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
		CharSequence[] items = new CharSequence[phonesToShowInDialog.size()];
		
		for (ContactPhone phone : phonesToShowInDialog) {
			items[i++] = getTranslationForPhoneType(phone.getType()) + ": " + phone.getNumber();
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getText(R.string.actsendsms_dlgPickNumber));
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		        mTxtDestination.setText(phonesToShowInDialog.get(item).getNumber());
		        removeDialog(DIALOG_PHONES);
		    }
		});
		AlertDialog alert = builder.create();
		
		return alert;
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
}