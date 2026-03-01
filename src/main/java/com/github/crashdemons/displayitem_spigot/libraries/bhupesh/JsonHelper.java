package com.github.crashdemons.displayitem_spigot.libraries.bhupesh;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

public class JsonHelper {
    public static Gson getMCJSONParser(){
        BooleanSerializer booleanSerializer = new BooleanSerializer();
        NumberSerializer numberSerializer = new NumberSerializer();
        StringSerializer stringSerializer = new StringSerializer();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(String.class, stringSerializer);
        gsonBuilder.registerTypeAdapter(Boolean.class, booleanSerializer);
        gsonBuilder.registerTypeAdapter(Number.class, numberSerializer);
        return gsonBuilder.create();
    }
}
