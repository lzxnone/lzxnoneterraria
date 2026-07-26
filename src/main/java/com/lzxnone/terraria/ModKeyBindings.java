package com.lzxnone.terraria;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID, value = Dist.CLIENT)
public class ModKeyBindings {
    public static final KeyMapping KILL_MODE = new KeyMapping(
        "key.lzxnoneterraria.kill_mode",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_C,
        "key.categories.lzxnoneterraria"
    );

    public static final KeyMapping CLEAR_SUMMON = new KeyMapping(
        "key.lzxnoneterraria.clear_summon",
        KeyConflictContext.IN_GAME,
        KeyModifier.NONE,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_X,
        "key.categories.lzxnoneterraria"
    );

    public static final KeyMapping CLEAR_ALL_SUMMONS = new KeyMapping(
        "key.lzxnoneterraria.clear_all_summons",
        KeyConflictContext.IN_GAME,
        KeyModifier.CONTROL,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_X,
        "key.categories.lzxnoneterraria"
    );

    public static final KeyMapping TOGGLE_SUMMON_FREE_TARGETING = new KeyMapping(
        "key.lzxnoneterraria.toggle_summon_free_targeting",
        KeyConflictContext.IN_GAME,
        KeyModifier.NONE,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_V,
        "key.categories.lzxnoneterraria"
    );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(KILL_MODE);
        event.register(CLEAR_SUMMON);
        event.register(CLEAR_ALL_SUMMONS);
        event.register(TOGGLE_SUMMON_FREE_TARGETING);
    }
}
