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
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/>.
 */

package it.rainbowbreeze.smsforfree.provider;


import java.util.List;

import android.os.Bundle;
import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.Def;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.domain.SmsConfigurableService;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.providers.JacksmsDictionary;
import it.rainbowbreeze.smsforfree.providers.JacksmsProvider;

/**
 * 
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class JacksmsProviderTest
	extends BaseProviderTest
{
	//---------- Private fields
	private static final String newLine = String.valueOf((char) 10);

	private String providers = "1	Vodafone-SMS	360	username	password	sim		Invia 10 SMS al giorno tramite il sito Vodafone. È possibile inviare SMS solo verso numeri Vodafone, occorre un numero registrato sul sito." + newLine + 
							   "3	Communicator	141	username	password	domain		Invia 10 SMS al giorno tramite il sito di Virgilio. Richiede un abbonamento Tin.it" + newLine +
							   "40	JackSMS-Messenger	8000	username	password			Invia messaggi gratuiti ad utenti di JackSMS" + newLine + 
							   "2	Rossoalice	146	username	password	login_account	spc	Invia 10 SMS al giorno tramite il sito di Alice. Richiede un abbonamento flat Alice Adsl per registrarsi al sito." + newLine +
							   "61	Aimon	612	username	password	mittente		Invia gli SMS che hai acquistato o guadagnato con Aimon.";
	
	
	
	//---------- Constructor




	//---------- SetUp and TearDown




	//---------- Tests methods
	
	@Override
	public void testAndroidTestCaseSetupProperly()
	{
		super.testAndroidTestCaseSetupProperly();
		
		//check if password and sender number was changed
		//before running this test
		assertFalse("You must change the password...", "XXXX".equals(Def.JACKSMS_PASSWORD));
		assertFalse("You must change destination", "XXXX".equals(Def.TEST_DESTINATION));
	}
	
	/**
	 * Test if the command for retrieving JackSMS stored user account works
	 */
	public void testImportStoredAccount_WrongCredentials()
	{
		ResultOperation<String> res;
		
		Bundle bundle = putCredentialsInBundle();
		mProvider.setParameterValue(1, "XXXX");
		//inject some templates into the code
		injectTemplates(mProvider);
		
		res = mProvider.executeCommand(JacksmsProvider.COMMAND_LOADUSERSERVICES, getContext(), bundle);
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_PROVIDER_ERROR, res.getReturnCode());
		//return a string with all user services
		assertEquals("Wrong command text", getContext().getString(R.string.jacksms_msg_invalidCredentials), res.getResult());
	}

	/**
	 * Test if the command for retrieving JackSMS stored user account works
	 * No templates, so no user services can be added
	 */
	public void testImportStoredAccount_NoTemplates()
	{
		ResultOperation<String> res;
		
		Bundle bundle = putCredentialsInBundle();
		//clear the list of provider's templages
		mProvider.getAllTemplates().clear();
		res = mProvider.executeCommand(JacksmsProvider.COMMAND_LOADUSERSERVICES, getContext(), bundle);
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_PROVIDER_ERROR, res.getReturnCode());
		assertEquals("Wrong command text", getContext().getString(R.string.jacksms_msg_noTemplatesToUse), res.getResult());
	}
	
	/**
	 * Test if the command for retrieving JackSMS stored user account works
	 * No previous user services, so all stored services are added
	 */
	public void testImportStoreAccount_NoPreviousServices()
	{
		ResultOperation<String> res;
		
		injectTemplates(mProvider);
		Bundle bundle = putCredentialsInBundle();
		//clear the list of provider's services
		mProvider.getAllSubservices().clear();
		res = mProvider.executeCommand(JacksmsProvider.COMMAND_LOADUSERSERVICES, getContext(), bundle);
		//check results messages
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_OK, res.getReturnCode());
		assertEquals("Wrong command text", getContext().getString(R.string.jacksms_msg_userServicesListUpdated), res.getResult());
		//checks results
		List<SmsService> services = mProvider.getAllSubservices();
		assertEquals("Wrong number of user services", 3, services.size());
		assertEquals("Wrong id of user service 1", "58302", services.get(0).getId());
		assertEquals("Wrong id of user service 2", "57727", services.get(1).getId());
		assertEquals("Wrong id of user service 3", "57926", services.get(2).getId());
	}
	
	/**
	 * Test if the command for retrieving JackSMS stored user account works
	 * Add same stored user services twice
	 */
	public void testImportStoreAccount_AllTwins()
	{
		ResultOperation<String> res;
		
		injectTemplates(mProvider);
		Bundle bundle = putCredentialsInBundle();
		//clear the list of provider's services
		mProvider.getAllSubservices().clear();
		res = mProvider.executeCommand(JacksmsProvider.COMMAND_LOADUSERSERVICES, getContext(), bundle);
		//check results messages
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_OK, res.getReturnCode());
		assertEquals("Wrong command text", getContext().getString(R.string.jacksms_msg_userServicesListUpdated), res.getResult());
		//re-execute the request
		res = mProvider.executeCommand(JacksmsProvider.COMMAND_LOADUSERSERVICES, getContext(), bundle);
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_OK, res.getReturnCode());
		assertEquals("Wrong command text", getContext().getString(R.string.jacksms_msg_userServicesListUpdated), res.getResult());
		//checks results
		List<SmsService> services = mProvider.getAllSubservices();
		assertEquals("Wrong number of user services", 3, services.size());
		assertEquals("Wrong id of user service 1", "58302", services.get(0).getId());
		assertEquals("Wrong id of user service 2", "57727", services.get(1).getId());
		assertEquals("Wrong id of user service 3", "57926", services.get(2).getId());
	}
	

	/**
	 * Test if the command for retrieving JackSMS stored user account works
	 * Add same stored user services twice
	 */
	public void testImportStoreAccount_SomeTwins()
	{
		ResultOperation<String> res;
		SmsService service;
		
		injectTemplates(mProvider);
		Bundle bundle = putCredentialsInBundle();
		//clear the list of provider's services
		mProvider.getAllSubservices().clear();
		res = mProvider.executeCommand(JacksmsProvider.COMMAND_LOADUSERSERVICES, getContext(), bundle);
		//check results messages
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_OK, res.getReturnCode());
		assertEquals("Wrong command text", getContext().getString(R.string.jacksms_msg_userServicesListUpdated), res.getResult());
		//remove some service
		mProvider.getAllSubservices().remove(2);
		mProvider.getAllSubservices().remove(1);
		assertEquals("Wrong number of subservices", 1, mProvider.getAllSubservices().size());
		//add new subservice
		service = new SmsConfigurableService("999", "1", "00Service", new String[]{"myusername", "mypassword"});
		mProvider.getAllSubservices().add(service);
		//re-execute the request
		res = mProvider.executeCommand(JacksmsProvider.COMMAND_LOADUSERSERVICES, getContext(), bundle);
		assertEquals("Wrong returncode", ResultOperation.RETURNCODE_OK, res.getReturnCode());
		assertEquals("Wrong command text", getContext().getString(R.string.jacksms_msg_userServicesListUpdated), res.getResult());
		//checks results
		List<SmsService> services = mProvider.getAllSubservices();
		assertEquals("Wrong number of user services", 4, services.size());
		assertEquals("Wrong id of user service 1", "999", services.get(0).getId());
		assertEquals("Wrong id of user service 2", "58302", services.get(1).getId());
		assertEquals("Wrong id of user service 3", "57727", services.get(2).getId());
		assertEquals("Wrong id of user service 4", "57926", services.get(3).getId());
		
		//check all data of one service
		service = services.get(1);
		assertEquals("Wrong service name", "AimonFree", service.getName());
		assertEquals("Wrong service parameters number", 3, service.getParametersNumber());
		assertEquals("Wrong service parameters 0 value", "aaaa", service.getParameterValue(0));
		assertEquals("Wrong service parameters 1 value", "bbbb", service.getParameterValue(1));
		assertEquals("Wrong service parameters 2 value", "cccc", service.getParameterValue(2));
		assertEquals("Wrong max message size", 612, service.getMaxMessageLenght());
	}
	



	//---------- Private methods
	
	@Override
	protected SmsProvider createProvider() {
		return new JacksmsProvider(mDao);
	}
	
	@Override
	protected void initProviderParams() {
		//set test parameters
		mProvider.setParameterValue(0, Def.JACKSMS_USERNAME);
		mProvider.setParameterValue(1, Def.JACKSMS_PASSWORD);
	}

	/**
	 * @return
	 */
	private Bundle putCredentialsInBundle() {
		//user with right password
		Bundle bundle = new Bundle();
		bundle.putString("0", Def.JACKSMS_USERNAME);
		bundle.putString("1", Def.JACKSMS_PASSWORD);
		return bundle;
	}


	private void injectTemplates(SmsProvider provider) {

		List<SmsService> templates = provider.getAllTemplates();
		JacksmsDictionary dictionary = new JacksmsDictionary();

		List<SmsService> newTemplates = dictionary.extractTemplates(providers);
		assertEquals("Wrong extracted templates", 5, newTemplates.size());

		//copy extracted templates into provider templates
		templates.clear();
		for (SmsService template : newTemplates)
			templates.add(template);
	}
}
