/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/ .
 */
package com.github.crashdemons.displayitem_spigot.antispam;

import org.jetbrains.annotations.Nullable;

/**
 * Defines an abstract spam preventer that defines methods common to all spam-preventers.
 * 
 * Implements basic record storage for spam preventers.
 * Child classes are expected to create their own EventSpamRecord implementation and handle adding internal records through this class.
 * @author crash
 */
public abstract class SpamPreventer {
    /**
     * The number of internal records to keep for spam preventers. Default is 5.
     */
    protected final int recordCount; // = 5;
    /**
     * The buffer of internal spam records held by the spam preventer instance.
     * 
     * These are generally filled circularly by addRecord()
     * @see #addRecord(com.github.crashdemons.playerheads.antispam.EventSpamRecord) 
     */
    protected final SpamRecord[] records; // = new EventSpamRecord[RECORDS];
    private volatile int next = 0;
    
    public SpamPreventer(int numRecords){
        recordCount=numRecords;
        records=new SpamRecord[recordCount];
    }
    
    /**
     * Adds a record to internal (circular) storage.
     * @param record the record to add.
     */
    protected synchronized void addRecord(SpamRecord record){
        records[next] = record;
        next = (next+1)%recordCount;
    }
    
    /**
     * Retrieves the spam record that is most recent to the 
     * @param record
     * @return 
     */
    @Nullable
    protected synchronized SpamRecord getMostRecentRecord(SpamRecord record){
        int i = Math.floorMod(next-1,recordCount);//start at the previously-assigned record
        
        long minTimeBetween=-1;
        SpamRecord minRecord=null;
        
        for(int n=0; n<recordCount; n++){//only check each of the records once.
            if(records[i]!=null && records[i].matches(record)){//the records are validly matched (using event criteria)
                long timeBetween = records[i].timeFrom(record);
                if(minRecord==null || timeBetween < minTimeBetween){
                    minTimeBetween = timeBetween;
                    minRecord = records[i];
                }
            }
            i = Math.floorMod(i-1,recordCount);//move backwards along the array
        }
        return minRecord;
    }
    
    protected SpamResult checkRecord(SpamRecord record, long thresholdMs){
        SpamRecord matchedRecord = getMostRecentRecord(record);
        if(matchedRecord==null) return new SpamResult(false,0);
        return new SpamResult(matchedRecord.closeTo(record, thresholdMs), matchedRecord.timeFrom(record));
    }
}
