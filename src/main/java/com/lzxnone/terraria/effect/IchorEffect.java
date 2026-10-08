package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class IchorEffect extends MobEffect {
    public static final ConfigFloat ARMOR_REDUCTION_PER_LEVEL = new ConfigFloat("effect.ichor.armor_reduction_per_level", "ichor_armor_reduction_per_level", 6.0f, 0.0f, 1024.0f);
    private static final ResourceLocation ARMOR_REDUCTION_ID = ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "ichor_armor_reduction");

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

        MobEffectInstance instance = entity.getEffect(ModEffects.ICHOR);
        if(instance == null) return;

        double reduction = getArmorReductionPerLevel() * (instance.getAmplifier() + 1);
        if(reduction <= 0.0D) return;

        AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
        if(armor != null) {
            armor.addOrUpdateTransientModifier(new AttributeModifier(
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
        "ichor",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect/ichor.png"),
        Component.translatable("effect.lzxnoneterraria.ichor"),
        CONFIG_DATA
    );

    public IchorEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFFF00);
    }
}
