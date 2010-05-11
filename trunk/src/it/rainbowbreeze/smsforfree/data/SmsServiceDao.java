/**
 * 
 */
package it.rainbowbreeze.smsforfree.data;

import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsService;

/**
 * @author rainbowbreeze
 *
 */
public class SmsServiceDao {
	//---------- Ctors

	//---------- Private fields

	
	
	//---------- Public properties
	private SmsServiceDao mInstance;
	public SmsServiceDao instance(){
		if (null == mInstance) {
			mInstance = new SmsServiceDao();
		}
		return mInstance;
	}


	
	
	//---------- Public methods
	
	public ResultOperation saveProviderParameters(SmsProvider provider) {
		return null;
	}
	
	
	public ResultOperation saveSubServiceParameters(SmsProvider provider, SmsService service) {
		return null;
	}
	

	//---------- Private methods

}
