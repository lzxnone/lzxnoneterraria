package com.lzxnone.terraria.ui.config;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ConfigListItem {
    String id;
    ResourceLocation icon;
    Component name;
    IConfigData data;

    public ConfigListItem(String id, ResourceLocation icon, Component name, IConfigData data) {
        this.id = id;
        this.icon = icon;
        this.name = name;
        this.data = data;
    }

    public void onConfigLoad() {
        data.onConfigLoad();
    }

    public void onConfigSave() {
        data.onConfigSave();
    }
}
