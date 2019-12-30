/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/ .
 */
package com.github.crashdemons.displayitem_spigot.antispam;

/**
 * Defines a spam-detection result object.
 * <p>
 * Used as semantic-sugar to make code self-documenting (eg:
 * `if(spampreventer.recordEvent(evt).isSpam())` is more descriptive than
 * `if(spampreventer.recordEvent(evt))`)
 * <p>
 */
public class SpamResult {

    private long minTimeBetween;
    private boolean spam;

    /**
     * <b><i>[INTERNAL - DO NOT USE]</i></b> Constructs a spam result
     *
     * @param spam the internal boolean state to abstract
     * @param minTimeBetween the smallest time between spam records. The time before the next allowed event would be (threshold-minTimeBetween)
     */
    public SpamResult(boolean spam, long minTimeBetween) {
        this.spam = spam;
        this.minTimeBetween = minTimeBetween;
    }

    /**
     * Reports whether the spam result indicates that the record was spam or
     * not.
     * <p>
     * Reports the internal boolean state this class abstracts.
     *
     * @return true: it was detected as spam. false: it was not
     */
    public boolean isSpam() {
        return spam;
    }
    
    /**
     * the smallest time (in milliseconds) between spam records. The time before the next allowed event would be (threshold-minTimeBetween)
     * @return the (minimum) time between the event and a matching spam record.
     */
    public long getDetectionTime(){
        return minTimeBetween;
    }
    
    /**
     * Gets the time in milliseconds until a threshold is fulfilled,
     * considering the time between the event and the most recent spam record.
     * @param thresholdMs the threshold between spam records to fulfill
     * @return the time remaining until the threshold is fulfilled.
     */
    public long getTimeUntil(long thresholdMs){
        long diff = thresholdMs-minTimeBetween;
        if(diff<0) return 0;
        return diff;
    }

    /**
     * <b><i>[INTERNAL - DO NOT USE]</i></b> Toggles the internal boolean state
     * the class abstracts.
     */
    public void toggle() {
        spam = !spam;
    }
}
