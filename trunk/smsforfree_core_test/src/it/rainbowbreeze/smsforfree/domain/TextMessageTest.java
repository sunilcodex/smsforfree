/**
 * Copyright (C) 2011 Alfredo Morresi
 * 
 * This file is part of TextMessageHolmes project.
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
package it.rainbowbreeze.smsforfree.domain;

import it.rainbowbreeze.smsforfree.domain.TextMessage;
import it.rainbowbreeze.smsforfree.util.TestHelper;
import android.content.Intent;
import android.test.AndroidTestCase;

/**
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class TextMessageTest extends AndroidTestCase {

    //---------- Constructor

    
    
    
    //---------- Private fields
    private boolean mForceReload = false;

    
    
    
    //---------- Test initialization
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestHelper.init(getContext(), mForceReload);
        mForceReload = false;
    }

    
    
    
    //---------- Test cases    
    public void testSerializeAndDeserializeToIntent() {
        //create first message and serialize it
        Intent intent = TestHelper.createTextMessage1().serializeToIntent(null);
        
        //now extract message from intent
        TextMessage textMessageDest = TextMessage.Factory.create(intent);
        
        TestHelper.compareWithTextMessage1(textMessageDest);
    }
    
    
    

    //---------- Private methods

}
