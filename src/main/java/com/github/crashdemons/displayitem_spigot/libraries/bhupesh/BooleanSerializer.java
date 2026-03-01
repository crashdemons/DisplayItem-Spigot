package com.github.crashdemons.displayitem_spigot.libraries.bhupesh;

import com.google.gson.*;

import java.lang.reflect.Type;

public class BooleanSerializer implements JsonSerializer<Boolean>, JsonDeserializer<Boolean> {

    public static Boolean parse(String json){
        if("1b".equalsIgnoreCase(json) || "1".equalsIgnoreCase(json)) return true;
        if("0b".equalsIgnoreCase(json) || "0".equalsIgnoreCase(json)) return false;
        throw new IllegalArgumentException();
    }

    @Override
    public JsonElement serialize(Boolean src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src?"1b":"0b");
    }

    @Override
    public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // return json.toString().equalsIgnoreCase("Y"); // Wrong code
        return "1b".equalsIgnoreCase(json.getAsString()) || "1".equalsIgnoreCase(json.getAsString());
        // json.getAsString() is the right way to get json element value
    }
}