package com.github.crashdemons.displayitem_spigot.libraries.bhupesh;

import com.google.gson.*;

import java.lang.reflect.Type;

public class StringSerializer implements JsonSerializer<String>, JsonDeserializer<String> {
    @Override
    public JsonElement serialize(String src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src);
    }

    @Override
    public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        System.out.println("string "+json);
        String s = json.getAsString();
        try{
            return ""+NumberSerializer.parse(s);
        }catch (Exception ignored){}
        try{
            return BooleanSerializer.parse(s) ? "true" : "false";
        }catch (Exception ignored){
            return s;
        }
    }
}