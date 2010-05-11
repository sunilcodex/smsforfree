/**
 * 
 */
package it.rainbowbreeze.smsforfree.logic;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalBag;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.providers.AimonProvider;
import it.rainbowbreeze.smsforfree.providers.JacksmsProvider;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class LogicManager {
	//---------- Ctors

	//---------- Private fields

	//---------- Public properties

	//---------- Public methods
	public static ResultOperation executeBeginTask(Context context)
	{
		ResultOperation res = new ResultOperation();
		
		String usernameDesc = context.getString(R.string.common_username);
		String passwordDesc = context.getString(R.string.common_password);
		String senderDesc = context.getString(R.string.common_sender);
		String aimonIdApiDesc = context.getString(R.string.common_aimon_idapi);
		
		//initialize provider list
		GlobalBag.providerList = new ArrayList<SmsProvider>();
		JacksmsProvider jackProv = new JacksmsProvider(usernameDesc, passwordDesc);
		jackProv.loadAllServices();
		jackProv.loadConfiguredServices();
		GlobalBag.providerList.add(jackProv);
		AimonProvider aimonProv = new AimonProvider(usernameDesc, passwordDesc, senderDesc, aimonIdApiDesc);
		GlobalBag.providerList.add(aimonProv);
		
		Collections.sort(GlobalBag.providerList);

		return res;
	}
	
	
	public static ResultOperation executeEndTast()
	{
		ResultOperation res = new ResultOperation();

		return res;
	}

	//---------- Private methods

}
