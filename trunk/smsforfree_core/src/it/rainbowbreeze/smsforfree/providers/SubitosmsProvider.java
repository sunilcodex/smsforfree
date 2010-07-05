/**
 * 
 */
package it.rainbowbreeze.smsforfree.providers;

import java.util.List;

import android.content.Context;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.domain.SmsServiceCommand;

/**
 * @author rainbowbreeze
 *
 */
public class SubitosmsProvider extends SmsProvider {

	public SubitosmsProvider(ProviderDao dao) {
		super(dao, 3);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#getAllSubservices()
	 */
	@Override
	public List<SmsService> getAllSubservices() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#getAllTemplates()
	 */
	@Override
	public List<SmsService> getAllTemplates() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#getCaptchaContentFromProviderReply(java.lang.String)
	 */
	@Override
	public ResultOperation<Object> getCaptchaContentFromProviderReply(
			String providerReply) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#getParametersFileName()
	 */
	@Override
	protected String getParametersFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#getSubservice(java.lang.String)
	 */
	@Override
	public SmsService getSubservice(String subserviceId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#getSubservicesFileName()
	 */
	@Override
	protected String getSubservicesFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#getSubservicesListActivityCommands()
	 */
	@Override
	public List<SmsServiceCommand> getSubservicesListActivityCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#getTemplate(java.lang.String)
	 */
	@Override
	public SmsService getTemplate(String templateId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#getTemplatesFileName()
	 */
	@Override
	protected String getTemplatesFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#hasServiceParametersConfigured(java.lang.String)
	 */
	@Override
	public boolean hasServiceParametersConfigured(String serviceId) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#hasSubServices()
	 */
	@Override
	public boolean hasSubServices() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#hasSubServicesToConfigure()
	 */
	@Override
	public boolean hasSubServicesToConfigure() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#hasTemplatesConfigured()
	 */
	@Override
	public boolean hasTemplatesConfigured() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#initProvider(android.content.Context)
	 */
	@Override
	public ResultOperation<Void> initProvider(Context context) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#loadSubservices(android.content.Context)
	 */
	@Override
	protected ResultOperation<Void> loadSubservices(Context context) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#loadTemplates(android.content.Context)
	 */
	@Override
	protected ResultOperation<Void> loadTemplates(Context context) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#newSubserviceFromTemplate(java.lang.String)
	 */
	@Override
	public SmsService newSubserviceFromTemplate(String templateId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#saveSubservices(android.content.Context)
	 */
	@Override
	public ResultOperation<Void> saveSubservices(Context context) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#saveTemplates(android.content.Context)
	 */
	@Override
	protected ResultOperation<Void> saveTemplates(Context context) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#sendCaptcha(java.lang.String, java.lang.String)
	 */
	@Override
	public ResultOperation<String> sendCaptcha(String providerReply,
			String captchaCode) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#sendMessage(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public ResultOperation<String> sendMessage(String serviceId,
			String destination, String body) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsProvider#setSelectedSubservice(java.lang.String)
	 */
	@Override
	public void setSelectedSubservice(String subserviceId) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsService#getId()
	 */
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsService#getMaxMessageLenght()
	 */
	@Override
	public int getMaxMessageLenght() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsService#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.domain.SmsService#getParametersNumber()
	 */
	@Override
	public int getParametersNumber() {
		// TODO Auto-generated method stub
		return 0;
	}
	//---------- Ctors

	//---------- Private fields

	//---------- Public properties

	//---------- Events

	//---------- Public methods

	//---------- Private methods

}
