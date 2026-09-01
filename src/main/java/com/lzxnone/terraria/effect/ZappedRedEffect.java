package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.particle.ArcParticleOptions;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.ParticleUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class ZappedRedEffect extends MobEffect {
    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {}
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "zapped_red",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect/zapped_red.png"),
        Component.translatable("effect.lzxnoneterraria.zapped_red"),
        CONFIG_DATA
    );

    public ZappedRedEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF2222);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if(entity.level() instanceof ServerLevel serverLevel) {
            float width = entity.getBbWidth();
            float height = entity.getBbHeight();
            float avgSize = (width + height) * 0.5F;

            float arcSize = net.minecraft.util.Mth.clamp(avgSize * 0.035F, 0.02F, 0.15F);
            float arcLength = net.minecraft.util.Mth.clamp(avgSize * 0.5F, 0.25F, 3.5F);

            ArcParticleOptions options = new ArcParticleOptions(
                arcSize,
                8,
                new Vector3f(1.0F, 0.15F, 0.15F),
                arcLength
            );

            AABB box = entity.getBoundingBox();
            double px = box.minX + entity.getRandom().nextDouble() * (box.maxX - box.minX);
            double py = box.minY + entity.getRandom().nextDouble() * (box.maxY - box.minY);
            double pz = box.minZ + entity.getRandom().nextDouble() * (box.maxZ - box.minZ);
            Vec3 pos = new Vec3(px, py, pz);
            ParticleUtil.addParticles(
                serverLevel,
                options,
                pos,
                new Vec3(0, 0, 0),
                0.0D,
                1
            );
        }
        return true;
    }
}
