/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/ .
 */
package com.github.crashdemons.displayitem_spigot.compatibility;

/**
 * Base for all compatibility exceptions
 * @author crashdemons (crashenator at gmail.com)
 */
public abstract class CompatibilityException extends IllegalStateException {

    /**
     * Constructor stub for exceptions
     * @param s the string message for the exception
     * @param e the exception cause
     */
    public CompatibilityException(String s, Exception e) {
        super(s, e);
    }

    /**
     * Constructor stub for exceptions
     * @param s the string message for the exception
     */
    public CompatibilityException(String s) {
        super(s);
    }
}
