package com.github.crashdemons.displayitem_spigot.antispam;

import java.util.UUID;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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
            if (record == null) {
                return false;
            }
            if (record.playerId.equals(playerId)) {
                if (super.closeTo(record, interactThresholdMs)) {
                        return true;
                }
            }
            return false;
        }
    }
    
    @Override
    public SpamResult checkEvent(Event event){
        if (event instanceof PlayerInteractEvent) {
            return recordEvent((PlayerInteractEvent) event);
        }
        return new SpamResult(false);
    }
    

    @Override
    public SpamResult recordEvent(Event event) {
        if (event instanceof PlayerInteractEvent) {
            return recordEvent((PlayerInteractEvent) event);
        }
        return new SpamResult(false);
    }
    
    
    //Check a new record for spam against recorded ones - NOTE: this is not synchronized or thread-safe on its own!
    private SpamResult checkRecord(ItemRecord record){
        SpamResult result = new SpamResult(false);
        for (EventSpamRecord otherRecordObj : records) {
            ItemRecord otherRecord = (ItemRecord) otherRecordObj;
            if (record.closeTo(otherRecord)) {
                result.toggle();
                break;
            }
        }
        return result;
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
            return checkRecord(new ItemRecord(event));
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
        synchronized(records){
            ItemRecord record = new ItemRecord(event);
            SpamResult result = checkRecord(record);
            addRecord(record);
            return result;
        }
    }
}
