package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.particle.ModParticles;
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

public class AcidVenomEffect extends MobEffect {
    public static final ConfigFloat DAMAGE_PER_LEVEL = new ConfigFloat("effect.acid_venom.damage_per_level", "acid_venom_damage_per_level", 2.0f, 0.0f, 8388600.0f);

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DAMAGE_PER_LEVEL);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "acid_venom",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect/acid_venom.png"),
        Component.translatable("effect.lzxnoneterraria.acid_venom"),
        CONFIG_DATA
    );

    public AcidVenomEffect() {
        super(MobEffectCategory.HARMFUL, 0x8A2BE2);
    }

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
                serverLevel, ModParticles.BLACK_DUST_PARTICLE.get(),
                entity.getBoundingBox().getCenter(), new Vec3(0, 0, 0),
                0.1, 1
            );
        }
        return true;
    }
}
