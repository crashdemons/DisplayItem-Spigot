package com.github.crashdemons.displayitem_spigot.antispam;

import java.util.UUID;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Helper class that records and detects PlayerInteractEvent right-click spam
 * for playerheads.
 *
 * @author crash
 */
public class ItemSpamPreventer extends EventSpamPreventer {

    private final long interactThresholdMs;

    public ItemSpamPreventer(int numRecords, long timeMS) {
        super(numRecords);
        interactThresholdMs = timeMS;
    }

    private final class ItemRecord extends EventSpamRecord {

        final UUID playerId;

        public ItemRecord(AsyncPlayerChatEvent event) {
            super(event);
            playerId = event.getPlayer().getUniqueId();
        }

        boolean closeTo(ItemRecord record) {
            return (record != null) && (super.closeTo(record, interactThresholdMs));
        }
        
        @Override
        public boolean matches(EventSpamRecord record){
            if(record instanceof ItemRecord) return matchesItemRecord((ItemRecord) record);
            return false;
        }
        
        public boolean matchesItemRecord(ItemRecord record){
            return (record != null) && (record.playerId.equals(playerId));
        }
        

    }
    
    
    /**
     * Checks an interaction event internally and prepares a result after
     * analyzing the event.
     *
     * @param event The PlayerInteractEvent to send to the spam-preventer.
     * @return The Spam-detection Result object
     * @see EventSpamPreventer#recordEvent(org.bukkit.event.Event)
     */
    public SpamResult checkEvent(AsyncPlayerChatEvent event){//TODO: need combined synchronization with recordEvent
        synchronized(records){
            return checkRecord(new ItemRecord(event), interactThresholdMs);
        }
    }
    

    /**
     * Records an interaction event internally and prepares a result after
     * analyzing the event.
     *
     * @param event The PlayerInteractEvent to send to the spam-preventer.
     * @return The Spam-detection Result object
     * @see EventSpamPreventer#recordEvent(org.bukkit.event.Event)
     */
    public SpamResult recordEvent(AsyncPlayerChatEvent event) {
        return recordEvent(event, false, false);
    }
    

    /**
     * Analyzes an interaction event internally and prepares a spam detection result.
     * Records the event depending on the state of spam result, if enabled.
     *
     * @param event The PlayerInteractEvent to send to the spam-preventer.
     * @param recordConditionally Whether recording the event is conditional on the state of the result.
     * @param recordResultState The state of the spam-detection result that needs to be recorded.
     * @return The Spam-detection Result object
     * @see EventSpamPreventer#recordEvent(org.bukkit.event.Event)
     */
    public SpamResult recordEvent(AsyncPlayerChatEvent event, boolean recordConditionally, boolean recordResultState) {
        synchronized(records){
            ItemRecord record = new ItemRecord(event);
            SpamResult result = checkRecord(record, interactThresholdMs);
            if(recordConditionally){
                if(result.isSpam() == recordResultState) addRecord(record);
            }else{
                addRecord(record);
            }
            return result;
        }
    }
}
