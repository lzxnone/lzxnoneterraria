package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.particle.DustParticleOptions;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class BetsysCurseEffect extends MobEffect {
    public static final ConfigFloat ARMOR_REDUCTION_PER_LEVEL = new ConfigFloat("effect.betsys_curse.armor_reduction_per_level", "betsys_curse_armor_reduction_per_level", 15.0f, 0.0f, 1024.0f);
    private static final ResourceLocation ARMOR_REDUCTION_ID = ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "betsys_curse_armor_reduction");

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(ARMOR_REDUCTION_PER_LEVEL);
        }
    };

    public static float getArmorReductionPerLevel() {
        return ARMOR_REDUCTION_PER_LEVEL.get();
    }

    public static void applyArmorReduction(LivingEntity entity) {
        removeArmorReduction(entity);

        MobEffectInstance instance = entity.getEffect(ModEffects.BETSYS_CURSE);
        if(instance == null) return;

        double reduction = getArmorReductionPerLevel() * (instance.getAmplifier() + 1);
        if(reduction <= 0.0D) return;

        AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
        if(armor != null) {
            armor.addTransientModifier(new AttributeModifier(
                ARMOR_REDUCTION_ID,
                -reduction,
                AttributeModifier.Operation.ADD_VALUE
            ));
        }
    }

    public static void removeArmorReduction(LivingEntity entity) {
        AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
        if(armor != null) armor.removeModifier(ARMOR_REDUCTION_ID);
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "betsys_curse",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect/betsys_curse.png"),
        Component.translatable("effect.lzxnoneterraria.betsys_curse"),
        CONFIG_DATA
    );

    public BetsysCurseEffect() {
        super(MobEffectCategory.HARMFUL, 0x8A2BE2);
    }

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.08f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(0.85F, 0.25F, 1.0F),
            new Vector3f(0.6F, 0.0F, 0.85F),
            new Vector3f(0.35F, 0.0F, 0.55F),
            new Vector3f(0.2F, 0.0F, 0.35F),
        }
    );

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if(entity.level() instanceof ServerLevel serverLevel) {
             ParticleUtil.addParticles(
                serverLevel, PARTICLE,
                entity.getBoundingBox().getCenter(), new Vec3(0, 0, 0),
                0.25, 1
            );
        }
        return true;
    }
}
