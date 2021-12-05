/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/ .
 */
package com.github.crashdemons.displayitem_spigot.compatibility;

/**
 * Exception indicating that an implementation providing compatibility for the
 * current server couldn't be found or is otherwise unavailable.
* <p>
 * This occurs when either Compatibility initialization has exhausted all 
 * options or the current method cannot load the specified provider.
* <p>
 * The second case may occur if version support was indicated to exist, but was 
 * not shaded in - this was common in legacy backports that only supported 
 * specifics versions, but modern builds  usually have 1:1 representation in 
 * CompatibilitySupport.
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class CompatibilityUnavailableException extends CompatibilityException {

    /**
     * Constructor for unavailable-compatibility exceptions
     * @param s the string message for the exception
     * @param e the exception cause
     */
    public CompatibilityUnavailableException(String s, Exception e) {
        super(s, e);
    }

    /**
     * Constructor for unavailable-compatibility exceptions
     * @param s the string message for the exception
     */
    public CompatibilityUnavailableException(String s) {
        super(s);
    }
}
