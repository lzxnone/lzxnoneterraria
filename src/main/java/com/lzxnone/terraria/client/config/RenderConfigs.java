package com.lzxnone.terraria.client.config;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigBoolean;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class RenderConfigs {
    public static final ConfigBoolean ENABLE_RENDER_HAND = new ConfigBoolean(
        "render.mixin.render_hand",
        "render_mixin_render_hand",
        true
    );
    public static final ConfigBoolean ENABLE_RENDER_ITEM = new ConfigBoolean(
        "render.mixin.render_item",
        "render_mixin_render_item",
        true
    );
    public static final ConfigBoolean ENABLE_SETUP_ANIM = new ConfigBoolean(
        "render.mixin.setup_anim",
        "render_mixin_setup_anim",
        true
    );
    public static final ConfigInt MANA_BAR_X = new ConfigInt(
        "render.mana_bar.x",
        "render_mana_bar_x",
        10,
        -4096,
        4096
    );
    public static final ConfigInt MANA_BAR_Y = new ConfigInt(
        "render.mana_bar.y",
        "render_mana_bar_y",
        28,
        -4096,
        4096
    );

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                ENABLE_RENDER_HAND,
                ENABLE_RENDER_ITEM,
                ENABLE_SETUP_ANIM,
                MANA_BAR_X,
                MANA_BAR_Y
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "render",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/ui/nav_render_icon.png"),
        Component.translatable("lzxnoneterraria.configuration.render_settings"),
        CONFIG_DATA
    );
}
