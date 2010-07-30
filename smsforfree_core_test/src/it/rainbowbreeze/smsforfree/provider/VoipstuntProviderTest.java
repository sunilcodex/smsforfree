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



/**
 * 
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
import it.rainbowbreeze.smsforfree.common.Def;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.providers.VoipstuntProvider;

public class VoipstuntProviderTest
	extends BaseProviderTest
{

	//---------- Private fields

	//---------- Constructor

	//---------- SetUp and TearDown

	//---------- Tests methods

	public void testMaxMessageLenght() {
		String smsMessageLenght;
	}
	

	//---------- Private methods
	@Override
	protected SmsProvider createProvider() {
		return new VoipstuntProvider(mDao);
	}

	@Override
	protected void initProviderParams() {
		//set test parameters
		mProvider.setParameterValue(0, Def.VOIPSTUNT_USERNAME);
		mProvider.setParameterValue(1, Def.VOIPSTUNT_PASSWORD);
		mProvider.setParameterValue(2, Def.VOIPSTUNT_SENDER);
	}

}
