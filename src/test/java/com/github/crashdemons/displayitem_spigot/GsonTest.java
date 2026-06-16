package com.github.crashdemons.displayitem_spigot;

import com.github.crashdemons.displayitem_spigot.libraries.bhupesh.JsonHelper;
import com.github.crashdemons.displayitem_spigot.libraries.sainttx.HoverComponentManager;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class GsonTest {
    public GsonTest(){}


    @Test
    public void testReplaceIgnoreCase_NormalWithB() {
        String nbt = "{components: {\"minecraft:lore\": [{underlined: 1b, text: \"x\", bold: 1b}]}, count: 1, Slot: 0b, id: \"minecraft:ice\",\"has_trail\":1,a:1}";
        String nbt2 = HoverComponentManager.fixNBTJson(nbt);;
        Gson g = JsonHelper.getMCJSONParser();
        JsonObject jo = g.fromJson(nbt2, JsonObject.class);
        JsonObject lore = jo.getAsJsonObject("components")
                .getAsJsonArray("minecraft:lore")
                .get(0)
                .getAsJsonObject();

        assertTrue(lore.get("underlined").getAsBoolean());
        assertTrue(lore.get("bold").getAsBoolean());
        assertFalse(jo.get("Slot").getAsBoolean());
        assertTrue(jo.get("has_trail").getAsBoolean());
    }

    @Test
    public void testFixNbtJson_PreservesNumericBytes() {
        String nbt = "{\"minecraft:consumable\":{on_consume_effects:[{effects:[{amplifier:1b,duration:100,id:\"minecraft:regeneration\",show_icon:1b}],type:\"minecraft:apply_effects\"}]}}";

        String nbt2 = HoverComponentManager.fixNBTJson(nbt);
        Gson g = JsonHelper.getMCJSONParser();
        JsonObject jo = g.fromJson(nbt2, JsonObject.class);
        JsonObject effect = jo.getAsJsonObject("minecraft:consumable")
                .getAsJsonArray("on_consume_effects")
                .get(0)
                .getAsJsonObject()
                .getAsJsonArray("effects")
                .get(0)
                .getAsJsonObject();

        JsonElement amplifier = effect.get("amplifier");
        JsonElement showIcon = effect.get("show_icon");

        assertEquals(1, amplifier.getAsInt());
        assertTrue(amplifier.isJsonPrimitive() && amplifier.getAsJsonPrimitive().isNumber());
        assertTrue(showIcon.getAsBoolean());
    }
}
