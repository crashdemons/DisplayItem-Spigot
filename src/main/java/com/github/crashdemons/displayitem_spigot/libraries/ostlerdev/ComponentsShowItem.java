package com.github.crashdemons.displayitem_spigot.libraries.ostlerdev;

import com.google.gson.JsonElement;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Content;

public final class ComponentsShowItem extends Content {
    private final String id;
    private final int count;
    private final JsonElement components;

    public ComponentsShowItem(final String id, final int count, final JsonElement components) {
        this.id = id;
        this.count = count;
        this.components = components;
    }

    @Override
    public HoverEvent.Action requiredAction() {
        return HoverEvent.Action.SHOW_ITEM;
    }
}