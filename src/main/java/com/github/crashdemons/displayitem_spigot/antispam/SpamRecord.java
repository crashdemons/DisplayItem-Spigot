/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/ .
 */
package com.github.crashdemons.displayitem_spigot.antispam;

/**
 * Defines an abstract event record for antispam features.
 * <p>
 * Implements basing record timestamp recording and comparison common to all
 * records.
 *
 * @author crash
 */
public abstract class SpamRecord {

    private final long timestamp;

    /**
     * Determines the time between this record and another, represented as an absolute value.
     * @param record the other record to compare.
     * @return the (positive) number of miliseconds between the two records.
     */
    public long timeFrom(SpamRecord record){
        return  Math.abs(record.timestamp - timestamp);
    }
    
    /**
     * Determines if the record is "close to" another record by comparing
     * timestamps against a time threshold.
     *
     * @param record the record to compare with
     * @param thresholdMs the threshold of milliseconds within which the record
     * should be considered recent / spam.
     * @return Whether the record is close to the other record, given the
     * parameters.
     */
    public boolean closeTo(SpamRecord record, long thresholdMs) {
        return timeFrom(record) < thresholdMs;
    }

    /**
     * Gets the timestamp associated with this record's creation.
     *
     * @return the timestamp as a long integer.
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Determine whether the spam record matches another record, using non-time-related criteria.
     * This determines whether the records can be used for time comparison in the first place.
     * Examples of matching criteria: event player causing the spam record, event location where the spam occurred, etc.
     * @param record the spam record to compare
     * @return whether the provided spam record matches
     */
    public abstract boolean matches(SpamRecord record);
    

    /**
     * Constructs the event record.
     * <p>
     * Note: this base class does not store any event-specific information
     * except the time, that is up to child classes to do.
     *
     */
    public SpamRecord() {
        timestamp = System.currentTimeMillis();
    }
}
