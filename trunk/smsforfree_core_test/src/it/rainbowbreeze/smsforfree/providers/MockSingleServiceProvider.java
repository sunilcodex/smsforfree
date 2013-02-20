/**
 * 
 */
package it.rainbowbreeze.smsforfree.providers;

import android.content.Context;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.domain.SmsSingleProvider;
import it.rainbowbreeze.smsforfree.domain.TextMessage;
import it.rainbowbreeze.smsforfree.ui.ActivityHelper;

/**
 * Implementation for a fake provider with no subservices
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class MockSingleServiceProvider extends SmsSingleProvider {
	//---------- Private fields
	private static final String LOG_HASH = "MockSingleServiceProvider";


	
	
	//---------- Constructor
	public MockSingleServiceProvider(
			LogFacility logFacility,
			AppPreferencesDao appPreferencesDao,
			ProviderDao providerDao,
			ActivityHelper activityHelper) {
		super(logFacility, 0, appPreferencesDao, providerDao, activityHelper);
	}
	
	
	
	
	//---------- Public properties
	public static final String ID = "MockProviderSingle";
	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsService#getId()
	 */
	@Override
	public String getId() {
		return ID;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsService#getName()
	 */
	@Override
	public String getName() {
		return "MockProvider Single";
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsService#getParametersNumber()
	 */
	@Override
	public int getParametersNumber() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsService#getMaxMessageLenght()
	 */
	@Override
	public int getMaxMessageLenght() {
		return 160;
	}
	
	protected TextMessage mLastSendMessage;
	public TextMessage getLastSendMessage()
	{ return mLastSendMessage; }
	
	
	
	
	//---------- Public methods

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#getCaptchaContentFromProviderReply(java.lang.String)
	 */
	@Override
	public ResultOperation<Object> getCaptchaContentFromProviderReply(String providerReply) {
		return null;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#sendCaptcha(java.lang.String, java.lang.String)
	 */
	@Override
	public ResultOperation<String> sendCaptcha(String providerReply, String captchaCode) {
		return null;
	}
	
	@Override
	public ResultOperation<String> sendMessage(
			String serviceId,
			String destination,
			String messageBody) {
		super.sendMessage(serviceId, destination, messageBody);
		
		mLogFacility.v(LOG_HASH, "Send message [" + messageBody + "] to " + destination);
		
		mLastSendMessage = TextMessage.Factory.create(
				100l,
				destination,
				messageBody,
				getId(),
				serviceId,
				0,
				TextMessage.PROCESSING_SENT);
		
		ResultOperation<String> res = new ResultOperation<String>("Sent OK");
		return res;
	}

	
	
	
	//---------- Private methods
	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#getParametersFileName()
	 */
	@Override
	protected String getParametersFileName() {
		return "mockprovider_parameters.xml";
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#getProviderRegistrationUrl(android.content.Context)
	 */
	@Override
	protected String getProviderRegistrationUrl(Context context) {
		return null;
	}


}
