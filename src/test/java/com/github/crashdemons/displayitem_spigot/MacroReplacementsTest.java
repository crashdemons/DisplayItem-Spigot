/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class MacroReplacementsTest {
    
    public MacroReplacementsTest() {
    }

    @Test
    public void testReplaceIgnoreCase_NormalWithB() {
        String haystack = "bob%item%";
        String needle = "%item%";
        String needlereplacement = "[itemnamehere]";
        String actual = MacroReplacements.replaceIgnoreCase(haystack, needle, needlereplacement);
        
        String expected = "bob[itemnamehere]";
        
        assertEquals(expected, actual);
    }
    @Test
    public void testReplaceIgnoreCase_NormalWithA() {
        String haystack = "%item%dale";
        String needle = "%item%";
        String needlereplacement = "[itemnamehere]";
        String actual = MacroReplacements.replaceIgnoreCase(haystack, needle, needlereplacement);
        
        String expected = "[itemnamehere]dale";
        
        assertEquals(expected, actual);
    }
    

    @Test
    public void testReplaceIgnoreCase_NormalWithBA() {
        String haystack = "bob%item%dale";
        String needle = "%item%";
        String needlereplacement = "[itemnamehere]";
        String actual = MacroReplacements.replaceIgnoreCase(haystack, needle, needlereplacement);
        
        String expected = "bob[itemnamehere]dale";
        
        assertEquals(expected, actual);
    }
    @Test
    public void testReplaceIgnoreCase_NormalWithout() {
        String haystack = "bobdale";
        String needle = "%item%";
        String needlereplacement = "[itemnamehere]";
        String actual = MacroReplacements.replaceIgnoreCase(haystack, needle, needlereplacement);
        
        String expected = "bobdale";
        
        assertEquals(expected, actual);
    }
    
    
    @Test
    public void testReplaceIgnoreCase_SpecialWith() {
        String haystack = "bob%item%dale$\\";
        String needle = "%item%";
        String needlereplacement = "\\$Iᗰᑭ ᗷᑌᑕK$\\"; //https://github.com/crashdemons/DisplayItem-Spigot/issues/14
        String actual = MacroReplacements.replaceIgnoreCase(haystack, needle, needlereplacement);
        
        String expected = "bob\\$Iᗰᑭ ᗷᑌᑕK$\\dale$\\";
        
        assertEquals(expected, actual);
    }
    @Test
    public void testReplaceIgnoreCase_SpecialWithout() {
        String haystack = "bobdale$\\";
        String needle = "%item%";
        String needlereplacement = "\\$Iᗰᑭ ᗷᑌᑕK$\\"; //https://github.com/crashdemons/DisplayItem-Spigot/issues/14
        String actual = MacroReplacements.replaceIgnoreCase(haystack, needle, needlereplacement);
        
        String expected = haystack;
        
        assertEquals(expected, actual);
    }
    
}
