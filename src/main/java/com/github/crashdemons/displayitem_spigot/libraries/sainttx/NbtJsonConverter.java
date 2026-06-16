package com.github.crashdemons.displayitem_spigot.libraries.sainttx;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;

import java.util.Map;
import java.util.Set;

public final class NbtJsonConverter {
    private static final Set<String> BOOLEAN_KEYS = Set.of(
            "bold",
            "italic",
            "underlined",
            "strikethrough",
            "obfuscated",
            "show_icon",
            "show_particles",
            "show_in_tooltip",
            "minecraft:enchantment_glint_override",
            "ambient"
    );

    private NbtJsonConverter() {}

    public static JsonObject parseCompound(String snbt) {
        if (snbt == null || snbt.isBlank()) {
            return new JsonObject();
        }
        try {
            return toJsonObject(TagParser.parseCompoundFully(snbt));
        } catch (CommandSyntaxException e) {
            throw new JsonParseException("Failed to parse SNBT", e);
        }
    }

    public static String parseCompoundString(String snbt) {
        return parseCompound(snbt).toString();
    }

    private static JsonObject toJsonObject(CompoundTag compoundTag) {
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, Tag> entry : compoundTag.entrySet()) {
            jsonObject.add(entry.getKey(), toJson(entry.getKey(), entry.getValue()));
        }
        return jsonObject;
    }

    private static JsonElement toJson(String key, Tag tag) {
        if (tag instanceof CompoundTag compoundTag) {
            return toJsonObject(compoundTag);
        }
        if (tag instanceof ListTag listTag) {
            JsonArray jsonArray = new JsonArray();
            for (Tag child : listTag) {
                jsonArray.add(toJson(null, child));
            }
            return jsonArray;
        }
        if (tag instanceof ByteArrayTag byteArrayTag) {
            JsonArray jsonArray = new JsonArray();
            for (byte value : byteArrayTag.getAsByteArray()) {
                jsonArray.add(value);
            }
            return jsonArray;
        }
        if (tag instanceof IntArrayTag intArrayTag) {
            JsonArray jsonArray = new JsonArray();
            for (int value : intArrayTag.getAsIntArray()) {
                jsonArray.add(value);
            }
            return jsonArray;
        }
        if (tag instanceof LongArrayTag longArrayTag) {
            JsonArray jsonArray = new JsonArray();
            for (long value : longArrayTag.getAsLongArray()) {
                jsonArray.add(value);
            }
            return jsonArray;
        }
        if (tag instanceof StringTag stringTag) {
            return new JsonPrimitive(stringTag.value());
        }
        if (tag instanceof NumericTag numericTag) {
            if (key != null && isBooleanKey(key) && isBooleanLikeNumber(numericTag)) {
                return new JsonPrimitive(numericTag.longValue() != 0);
            }
            return new JsonPrimitive(numericTag.box());
        }

        throw new JsonParseException("Unsupported NBT tag type: " + tag.getClass().getName());
    }

    private static boolean isBooleanKey(String key) {
        return BOOLEAN_KEYS.contains(key) || key.startsWith("is_") || key.startsWith("has_");
    }

    private static boolean isBooleanLikeNumber(NumericTag numericTag) {
        double value = numericTag.doubleValue();
        return value == 0D || value == 1D;
    }
}
