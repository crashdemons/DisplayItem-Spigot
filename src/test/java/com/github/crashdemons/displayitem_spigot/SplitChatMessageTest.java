/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot;

import com.github.crashdemons.displayitem_spigot.chat.TextUtils;
import com.github.crashdemons.displayitem_spigot.chat.SplitChatMessage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class SplitChatMessageTest {

    public SplitChatMessageTest() {
    }

    protected void assertSplitValues(SplitChatMessage msg, String prefix, String content, String suffix) {
        assertEquals(TextUtils.toLegacyText(msg.prefix,false), prefix);
        assertEquals(TextUtils.toLegacyText(msg.content,false), content);
        assertEquals(TextUtils.toLegacyText(msg.suffix,false), suffix);
    }

    @Test
    public void testFrom() {
        System.out.println("from");

        SplitChatMessage result;

        result = SplitChatMessage.from("[i]message", "[i]");
        assertSplitValues(result, "", "[i]", "message");

        result = SplitChatMessage.from("mes[i]sage", "[i]");
        assertSplitValues(result, "mes", "[i]", "sage");

        result = SplitChatMessage.from("message[i]", "[i]");
        assertSplitValues(result, "message", "[i]", "");

        try {
            SplitChatMessage.from("message", "[i]");
            fail("the expected exception did not occur: IllegalArgumentException; none");
        } catch (Exception e) {
            if (!(e instanceof IllegalArgumentException)) {
                fail("the expected exception did not occur: IllegalArgumentException; "+e.getClass().getName());
            }
        }

    }
    /*
    @Test
    public void testFromWithExternalReplacement() {
        System.out.println("fromWithExternalReplacement");
        String message = "";
        String elementString = "";
        String externalReplacement = "";
        String externalReplaceWith = "";
        SplitChatMessage expResult = null;
        SplitChatMessage result = SplitChatMessage.fromWithExternalReplacement(message, elementString, externalReplacement, externalReplaceWith);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testToComponents() {
        System.out.println("toComponents");
        SplitChatMessage instance = new SplitChatMessage();
        BaseComponent[] expResult = null;
        BaseComponent[] result = instance.toComponents();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
     */

}
