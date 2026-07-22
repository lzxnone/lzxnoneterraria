package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.item.IItemWaveBehavior;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.ConfigUtil;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.ParticleUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class BloodButcherer extends SwordItem {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    public static final String EFFECT_DURATION_PATH = "weapon.blood_butcherer.effect_duration";
    public static final int EFFECT_DURATION_DEFAULT = 180;
    public static final int EFFECT_DURATION_MIN = 0;
    public static final int EFFECT_DURATION_MAX = 72000;

    public static final String MAX_LEVEL_PATH = "weapon.blood_butcherer.max_level";
    public static final int MAX_LEVEL_DEFAULT = 4;
    public static final int MAX_LEVEL_MIN = 0;
    public static final int MAX_LEVEL_MAX = 255;

    public BloodButcherer() {
        super(Tiers.IRON, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 3.5, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -1.5, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ));
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigFactory.loadIntConfig(EFFECT_DURATION_PATH, configText("blood_butcherer_effect_duration"), configTooltip("blood_butcherer_effect_duration"), EFFECT_DURATION_DEFAULT, EFFECT_DURATION_MIN, EFFECT_DURATION_MAX);
            ConfigFactory.loadIntConfig(MAX_LEVEL_PATH, configText("blood_butcherer_max_level"), configTooltip("blood_butcherer_max_level"), MAX_LEVEL_DEFAULT, MAX_LEVEL_MIN, MAX_LEVEL_MAX);
        }
    };

    private static Component configText(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key);
    }

    private static Component configTooltip(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key + ".tooltip");
    }

    public static int getEffectDuration() {
        return Math.clamp(ConfigUtil.readInt(EFFECT_DURATION_PATH, EFFECT_DURATION_DEFAULT), EFFECT_DURATION_MIN, EFFECT_DURATION_MAX);
    }

    public static int getMaxLevel() {
        return Math.clamp(ConfigUtil.readInt(MAX_LEVEL_PATH, MAX_LEVEL_DEFAULT), MAX_LEVEL_MIN, MAX_LEVEL_MAX);
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "blood_butcherer",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/blood_butcherer.png"),
        Component.translatable("item.lzxnoneterraria.blood_butcherer"),
        CONFIG_DATA
    );

    public static final IItemWaveBehavior ITEM_WAVE_BEHAVIOR = new IItemWaveBehavior() {
        @Override
        public void onLeftClickAir(PlayerInteractEvent.LeftClickEmpty event) {
            Player player = event.getEntity();
            if(player.level().isClientSide()) {
                ItemStack itemStack = player.getMainHandItem();
                if(itemStack.isEmpty()) return;
                Item item = itemStack.getItem();
                if(item instanceof BloodButcherer) {
                    ParticleUtil.addParticles(
                        player.level(), ModParticles.BLOOD_BUTCHERED_PARTICLE.get(),
                        player.getBoundingBox().getCenter(), 0.2,
                        new Vec3(0, 0, 0), 0.2,
                        20
                    );
                }
            }
        }

        @Override
        public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
            Player player = event.getEntity();
            if(player.level().isClientSide()) {
                ItemStack itemStack = player.getMainHandItem();
                if(itemStack.isEmpty()) return;
                Item item = itemStack.getItem();
                if(item instanceof BloodButcherer) {
                    ParticleUtil.addParticles(
                        player.level(), ModParticles.BLOOD_BUTCHERED_PARTICLE.get(),
                        player.getBoundingBox().getCenter(), 0.2,
                        new Vec3(0, 0, 0), 0.2,
                        20
                    );
                }
            }
        }

        @Override
        public void onAttackEntity(AttackEntityEvent event) {
            Player player = event.getEntity();
            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.isEmpty()) return;
            Item item = itemStack.getItem();
            if(item instanceof BloodButcherer) {
                if(!player.level().isClientSide()) {
                    if(!player.getCooldowns().isOnCooldown(item)) {
                        Entity target = event.getTarget();
                        if(target instanceof LivingEntity livingTarget && FilterUtil.createLivingTargetFilter(player).test(livingTarget)) {
                            int currentLevel = -1;
                            if(livingTarget.hasEffect(ModEffects.BLOOD_BUTCHERED)) {
                                MobEffectInstance instance = livingTarget.getEffect(ModEffects.BLOOD_BUTCHERED);
                                if(instance != null) {
                                    currentLevel = instance.getAmplifier();
                                }
                            }
                            int maxLevel = getMaxLevel();
                            if(maxLevel > 0) {
                                if(currentLevel >= maxLevel) currentLevel = maxLevel - 1;
                                livingTarget.addEffect(new MobEffectInstance(
                                    ModEffects.BLOOD_BUTCHERED,
                                    getEffectDuration(),
                                    currentLevel + 1
                                ));
                            }
                        }
                        player.getCooldowns().addCooldown(item, 7);
                    }
                }else {
                    ParticleUtil.addParticles(
                        player.level(), ModParticles.BLOOD_BUTCHERED_PARTICLE.get(),
                        player.getBoundingBox().getCenter(), 0.2,
                        new Vec3(0, 0, 0), 0.2,
                        20
                    );
                }
            }
        }
    };
}
