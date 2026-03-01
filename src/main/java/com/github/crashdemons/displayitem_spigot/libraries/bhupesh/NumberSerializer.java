package com.github.crashdemons.displayitem_spigot.libraries.bhupesh;

import com.google.gson.*;

import java.lang.reflect.Type;

public class NumberSerializer implements JsonSerializer<Number>, JsonDeserializer<Number> {

    public static Number parse(String s){
        String digits = s.replaceAll("[^0-9.-]","");
        String lastchar = s.substring(s.length()-1).toUpperCase();


        switch(lastchar){
            case "B": return Byte.parseByte(digits);
            case "S": return Short.parseShort(digits);
            case "L": return Long.parseLong(digits);
            case "F": return Float.parseFloat(digits);
            case "D": return Double.parseDouble(digits);
            default:
                try{
                    return Integer.parseInt(digits);
                }catch (Exception ex){
                    return Double.parseDouble(digits);
                }

        }
    }


    @Override
    public JsonElement serialize(Number src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src);
    }

    @Override
    public Number deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String s = json.getAsString();
        String digits = s.replaceAll("[^0-9.-]","");
        String lastchar = s.substring(s.length()-1).toUpperCase();


        switch(lastchar){
            case "B": return Byte.parseByte(digits);
            case "S": return Short.parseShort(digits);
            case "L": return Long.parseLong(digits);
            case "F": return Float.parseFloat(digits);
            case "D": return Double.parseDouble(digits);
            default:
                try{
                    return Integer.parseInt(digits);
                }catch (Exception ex){
                    return Double.parseDouble(digits);
                }

        }
    }
}