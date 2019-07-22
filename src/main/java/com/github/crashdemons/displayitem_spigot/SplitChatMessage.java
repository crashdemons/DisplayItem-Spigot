/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class SplitChatMessage {
    public BaseComponent[] prefix;
    public BaseComponent[] content;
    public BaseComponent[] suffix;
    
    
    public static SplitChatMessage from(String message,String elementString){
        int start = message.indexOf(elementString);
        int end = start + elementString.length();
        String prefix = message.substring(0, start);
        String element = message.substring(start, end);
        String suffix = message.substring(end);
        return new SplitChatMessage(prefix,TextComponent.fromLegacyText(element),suffix);
    }
    
    public SplitChatMessage(BaseComponent[] prefix, BaseComponent[] content, BaseComponent[] suffix){
        this.prefix=prefix;
        this.content=content;
        this.suffix=suffix;
    }
    
    public SplitChatMessage(){
        this(TextComponent.fromLegacyText(""),TextComponent.fromLegacyText(""),TextComponent.fromLegacyText(""));
    }

    public SplitChatMessage(String legacyPrefix, BaseComponent[] element, String legacySuffix){
        this(TextComponent.fromLegacyText(legacyPrefix), element, TextComponent.fromLegacyText(legacySuffix));
    }
    
    public BaseComponent[] toComponents(){
        return new ComponentBuilder("").append(prefix).append(content).append(suffix).create();
    }
}
