/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot.libraries.sainttx;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class ItemJsonLengthException extends Exception {
    private final int jsonLength;
    private final int jsonLimit;

    public int getJsonLength() {
        return jsonLength;
    }

    public int getJsonLimit() {
        return jsonLimit;
    }
    public ItemJsonLengthException(String message, int length, int limit){
        super(message); 
        jsonLength = length;
        jsonLimit=limit;
    }
}
