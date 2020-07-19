package com.github.crashdemons.displayitem_spigot.antispam;

import java.util.UUID;
import org.bukkit.entity.Player;

/**
 * Helper class that records and detects Spammed events from a player
 *
 * @author crash
 */
public class ItemSpamPreventer extends SpamPreventer {

    private final long interactThresholdMs;

    public ItemSpamPreventer(int numRecords, long timeMS) {
        super(numRecords);
        interactThresholdMs = timeMS;
    }

    private final class ItemRecord extends SpamRecord {

        final UUID playerId;

        public ItemRecord(Player player) {
            playerId = player.getUniqueId();
        }

        boolean closeTo(ItemRecord record) {
            return (record != null) && (super.closeTo(record, interactThresholdMs));
        }
        
        @Override
        public boolean matches(SpamRecord record){
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
     * @param player The player to check a possible spam event for
     * @return The Spam-detection Result object
     */
    public SpamResult checkEvent(Player player){//TODO: need combined synchronization with recordEvent
        synchronized(records){
            return checkRecord(new ItemRecord(player), interactThresholdMs);
        }
    }
    

    /**
     * Records an interaction event internally and prepares a result after
     * analyzing the event.
     *
     * @param player The player to record a possible spam event for
     * @return The Spam-detection Result object
     * @see SpamPreventer#recordEvent(org.bukkit.event.Event)
     */
    public SpamResult recordEvent(Player player) {
        return recordEvent(player, false, false);
    }
    

    /**
     * Analyzes an interaction event internally and prepares a spam detection result.
     * Records the event depending on the state of spam result, if enabled.
     *
     * @param player The player to record a possible spam event for
     * @param recordConditionally Whether recording the event is conditional on the state of the result.
     * @param recordResultState The state of the spam-detection result that needs to be recorded.
     * @return The Spam-detection Result object
     * @see SpamPreventer#recordEvent(org.bukkit.event.Event)
     */
    public SpamResult recordEvent(Player player, boolean recordConditionally, boolean recordResultState) {
        synchronized(records){
            ItemRecord record = new ItemRecord(player);
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
