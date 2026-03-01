package com.github.crashdemons.displayitem_spigot;

import com.github.crashdemons.displayitem_spigot.libraries.bhupesh.JsonHelper;
import com.github.crashdemons.displayitem_spigot.libraries.sainttx.HoverComponentManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
        System.out.println(nbt2);
        Gson g = JsonHelper.getMCJSONParser();
        JsonObject jo = g.fromJson(nbt2, JsonObject.class);
        System.out.println(jo.toString());
    }
}
