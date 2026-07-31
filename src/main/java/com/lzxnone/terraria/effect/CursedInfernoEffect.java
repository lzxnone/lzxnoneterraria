package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.particle.DustParticleOptions;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class CursedInfernoEffect extends MobEffect {
    public static final ConfigFloat DAMAGE_PER_LEVEL = new ConfigFloat("effect.cursed_inferno.damage_per_level", "cursed_inferno_damage_per_level", 2.0f, 0.0f, 8388600.0f);

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DAMAGE_PER_LEVEL);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "cursed_inferno",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect/cursed_inferno.png"),
        Component.translatable("effect.lzxnoneterraria.cursed_inferno"),
        CONFIG_DATA
    );

    public CursedInfernoEffect() {
        super(MobEffectCategory.HARMFUL, 0x66FF00);
    }

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.075f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(0.5F, 1.0F, 0.5F),
            new Vector3f(0.0F, 1.0F, 0.2F)
        }
    );

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if(entity.level() instanceof ServerLevel serverLevel) {
            if(entity.tickCount % 20 == 0) {
                if(entity.hurt(entity.damageSources().magic(), DAMAGE_PER_LEVEL.get() * (amplifier + 1))) entity.invulnerableTime = 10;
            }
            ParticleUtil.addParticles(
                    serverLevel, PARTICLE,
                    entity.getBoundingBox().getCenter(), new Vec3(0, 0, 0),
                    0.1, 1
            );
        }
        return true;
    }
}
