package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.particle.CircleParticleOptions;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.ParticleUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class DemonicFlamesEffect extends MobEffect {
    public static final ConfigFloat DAMAGE_PER_LEVEL = new ConfigFloat("effect.demonic_flames.damage_per_level", "demonic_flames_damage_per_level", 2.5f, 0.0f, 8388600.0f);

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DAMAGE_PER_LEVEL);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "demonic_flames",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect/demonic_flames.png"),
        Component.translatable("effect.lzxnoneterraria.demonic_flames"),
        CONFIG_DATA
    );

    public DemonicFlamesEffect() {
        super(MobEffectCategory.HARMFUL, 0xCC2DC7);
    }

    public static final CircleParticleOptions PARTICLE = new CircleParticleOptions(0.075f, 20, new Vector3f(0.8f, 0.176f, 0.78f));

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if(entity.level() instanceof ServerLevel serverLevel) {
            MobEffectInstance instance = entity.getEffect(ModEffects.DEMONIC_FLAMES);
            if(instance != null) {
                if(entity.tickCount % 4 == 0) {
                    entity.invulnerableTime = 5;
                    entity.hurt(entity.damageSources().magic(), (float) DAMAGE_PER_LEVEL.get() * (amplifier + 1));
                }
            }

            ParticleUtil.addParticles(
                serverLevel, PARTICLE,
                entity.getBoundingBox().getCenter(), new Vec3(0, 0, 0),
                0.2, 1
            );
        }
        return true;
    }
}

