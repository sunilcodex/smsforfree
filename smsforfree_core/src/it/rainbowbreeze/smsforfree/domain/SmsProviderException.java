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
 * Represents an error during provider operation
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class SmsProviderException extends Exception {
    //---------- Private fields
    private static final long serialVersionUID = 1049647949315321958L;

    //---------- Constructor
    public SmsProviderException() {
    }
    
    public SmsProviderException(String message) {
        super(message);
    }

    //---------- SetUp and TearDown

    //---------- Tests methods

    //---------- Private methods

}
