package com.github.crashdemons.displayitem_spigot.antispam;

import com.github.crashdemons.displayitem_spigot.concurrency.DeferredChatEventParameters;
import java.util.UUID;

/**
 * Helper class that records and detects PlayerInteractEvent right-click spam
 * for playerheads.
 *
 * @author crash
 */
public class ItemSpamPreventer extends DeferredEventSpamPreventer {

    private final long interactThresholdMs;

    public ItemSpamPreventer(int numRecords, long timeMS) {
        super(numRecords);
        interactThresholdMs = timeMS;
    }

    private final class ItemRecord extends DeferredEventSpamRecord {

        final UUID playerId;

        public ItemRecord(DeferredChatEventParameters params) {
            super(params);
            playerId = params.playerId;
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
    public synchronized SpamResult recordEventParams(DeferredChatEventParameters params) {
        SpamResult result = new SpamResult(false);
        ItemRecord record = new ItemRecord(params);
        for (DeferredEventSpamRecord otherRecordObj : records) {
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
