package com.lzxnone.terraria.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import net.minecraft.client.Minecraft;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class SkinFetch {
    private static final Map<UUID, GameProfile> PROFILE_CACHE = new ConcurrentHashMap<>();

    public static void fetchProfile(UUID uuid) {
        CompletableFuture.supplyAsync(() -> {
            try {
                MinecraftSessionService sessionService = Minecraft.getInstance().getMinecraftSessionService();
                ProfileResult result = sessionService.fetchProfile(uuid, true);
                if(result != null) {
                    GameProfile filled = result.profile();
                    PROFILE_CACHE.put(uuid, filled);
                    return filled;
                }
            }catch (Exception ignored) {}
            GameProfile fallback = new GameProfile(uuid, "");
            PROFILE_CACHE.put(uuid, fallback);
            return fallback;
        });
    }

    public static GameProfile getCachedProfile(UUID uuid) {
        GameProfile cached = PROFILE_CACHE.get(uuid);
        if(cached != null) return cached;
        fetchProfile(uuid);
        return new GameProfile(uuid, "");
    }
}
