package com.github.crashdemons.displayitem_spigot.antispam;

import java.util.UUID;
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
    public SpamResult recordEvent(org.bukkit.event.Event event) {
        if (event instanceof PlayerInteractEvent) {
            return recordEvent((PlayerInteractEvent) event);
        }
        return new SpamResult(false);
    }

    /**
     * Records an interaction event internally and prepares a result after
     * analyzing the event.
     * <p>
     * For the current implementation, a click to the same block location by the
     * same user within 1 second is considered spam (within 5 click records).
     *
     * @param event The PlayerInteractEvent to send to the spam-preventer.
     * @return The Spam-detection Result object
     * @see EventSpamPreventer#recordEvent(org.bukkit.event.Event)
     */
    public synchronized SpamResult recordEvent(AsyncPlayerChatEvent event) {
        SpamResult result = new SpamResult(false);
        ItemRecord record = new ItemRecord(event);
        for (EventSpamRecord otherRecordObj : records) {
            ItemRecord otherRecord = (ItemRecord) otherRecordObj;
            if (record.closeTo(otherRecord)) {
                result.toggle();
                break;
            }
        }
        addRecord(record);
        return result;
    }
}
