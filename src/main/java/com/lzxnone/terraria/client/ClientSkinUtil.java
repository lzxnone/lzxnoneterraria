package com.lzxnone.terraria.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.Entity;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ClientSkinUtil {
    private static final Map<UUID, GameProfile> PROFILE_CACHE = new ConcurrentHashMap<>();
    private static final Set<UUID> PENDING_PROFILES = ConcurrentHashMap.newKeySet();

    public static PlayerSkin getPlayerSkin(UUID uuid, Entity entity) {
        if(entity instanceof AbstractClientPlayer clientPlayer && clientPlayer.getUUID().equals(uuid)) {
            return clientPlayer.getSkin();
        }

        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level != null) {
            for(AbstractClientPlayer player : minecraft.level.players()) {
                if(player.getUUID().equals(uuid)) {
                    return player.getSkin();
                }
            }
        }

        if(minecraft.getConnection() != null) {
            PlayerInfo playerInfo = minecraft.getConnection().getPlayerInfo(uuid);
            if(playerInfo != null) {
                return playerInfo.getSkin();
            }
        }

        GameProfile cachedProfile = PROFILE_CACHE.get(uuid);
        if(cachedProfile != null) {
            return minecraft.getSkinManager().getInsecureSkin(cachedProfile);
        }

        fetchProfile(uuid);
        return minecraft.getSkinManager().getInsecureSkin(new GameProfile(uuid, ""));
    }

    private static void fetchProfile(UUID uuid) {
        if(!PENDING_PROFILES.add(uuid)) return;

        CompletableFuture.supplyAsync(() -> {
            try {
                MinecraftSessionService sessionService = Minecraft.getInstance().getMinecraftSessionService();
                ProfileResult result = sessionService.fetchProfile(uuid, true);
                return result == null ? null : result.profile();
            }catch (Exception ignored) {
                return null;
            }
        }).thenAccept(profile -> {
            if(profile != null) {
                PROFILE_CACHE.put(uuid, profile);
            }
            PENDING_PROFILES.remove(uuid);
        });
    }
}
