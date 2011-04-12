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

package it.rainbowbreeze.smsforfree.domain;

/**
 * A simple message representation
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class TextMessage {
    //---------- Private fields

    
    //---------- Constructors
    public TextMessage() {
    }
    
    public TextMessage(String destination, String message) {
        mDestination = destination;
        mMessage = message;
    }
    
    //---------- Public properties
    protected String mDestination;
    public String getDestination()
    { return mDestination; }
    public TextMessage setDestination(String newValue)
    { mDestination = newValue; return this; }

    protected String mMessage;
    public String getMessage()
    { return mMessage; }
    public TextMessage setMessage(String newValue)
    { mMessage = newValue; return this; }


    //---------- Public methods

    //---------- Private methods
}
