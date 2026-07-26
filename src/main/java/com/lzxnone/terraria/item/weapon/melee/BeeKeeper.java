package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.BeeSummon;
import com.lzxnone.terraria.item.IItemWaveBehavior;
import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.ConfigUtil;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.MathUtil;
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
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

public class BeeKeeper extends MeleeWeapon {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    public static final String MAX_BEES_PATH = "weapon.bee_keeper.max_bees";
    public static final int MAX_BEES_DEFAULT = 3;
    public static final int MAX_BEES_MIN = 0;
    public static final int MAX_BEES_MAX = 10;

    public static final String CONFUSION_DURATION_PATH = "weapon.bee_keeper.confusion_duration";
    public static final int CONFUSION_DURATION_DEFAULT = 40;
    public static final int CONFUSION_DURATION_MIN = 0;
    public static final int CONFUSION_DURATION_MAX = 1200;

    public static final String BEE_DAMAGE_PATH = "weapon.bee_keeper.bee_damage";
    public static final float BEE_DAMAGE_DEFAULT = 2.0f;
    public static final float BEE_DAMAGE_MIN = 0.0f;
    public static final float BEE_DAMAGE_MAX = 8388600.0f;

    public BeeKeeper() {
        super(Tiers.IRON, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 5, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -2.4, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ));
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigFactory.loadIntConfig(MAX_BEES_PATH, configText("bee_keeper_max_bees"), configTooltip("bee_keeper_max_bees"), MAX_BEES_DEFAULT, MAX_BEES_MIN, MAX_BEES_MAX);
            ConfigFactory.loadIntConfig(CONFUSION_DURATION_PATH, configText("bee_keeper_confusion_duration"), configTooltip("bee_keeper_confusion_duration"), CONFUSION_DURATION_DEFAULT, CONFUSION_DURATION_MIN, CONFUSION_DURATION_MAX);
            ConfigFactory.loadFloatConfig(BEE_DAMAGE_PATH, configText("bee_keeper_bee_damage"), configTooltip("bee_keeper_bee_damage"), BEE_DAMAGE_DEFAULT, BEE_DAMAGE_MIN, BEE_DAMAGE_MAX);
        }
    };

    private static Component configText(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key);
    }

    private static Component configTooltip(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key + ".tooltip");
    }

    public static int getMaxBees() {
        return Math.clamp(ConfigUtil.readInt(MAX_BEES_PATH, MAX_BEES_DEFAULT), MAX_BEES_MIN, MAX_BEES_MAX);
    }

    public static int getConfusionDuration() {
        return Math.clamp(ConfigUtil.readInt(CONFUSION_DURATION_PATH, CONFUSION_DURATION_DEFAULT), CONFUSION_DURATION_MIN, CONFUSION_DURATION_MAX);
    }

    public static float getBeeDamage() {
        return Math.clamp(ConfigUtil.readFloat(BEE_DAMAGE_PATH, BEE_DAMAGE_DEFAULT), BEE_DAMAGE_MIN, BEE_DAMAGE_MAX);
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "bee_keeper",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/bee_keeper.png"),
        Component.translatable("item.lzxnoneterraria.bee_keeper"),
        CONFIG_DATA
    );

    public static final IItemWaveBehavior ITEM_WAVE_BEHAVIOR = new IItemWaveBehavior() {
        @Override
        public void onAttackEntity(AttackEntityEvent event) {
            Player player = event.getEntity();
            if(!player.level().isClientSide()) {
                ItemStack itemStack = player.getMainHandItem();
                if(itemStack.isEmpty()) return;
                Item item = itemStack.getItem();
                if(item instanceof BeeKeeper && !player.getCooldowns().isOnCooldown(item)) {
                    Entity target = event.getTarget();
                    if(target instanceof LivingEntity livingTarget && FilterUtil.createLivingTargetFilter(player).test(livingTarget)) {
                        livingTarget.addEffect(new MobEffectInstance(
                            ModEffects.CONFUSED,
                            getConfusionDuration(),
                            0
                        ));
                    }

                    int maxBees = getMaxBees();
                    int count = maxBees > 0 ? player.level().random.nextInt(maxBees) + 1 : 0;
                    while(count-- > 0) {
                        BeeSummon bee = ModEntities.BEE_SUMMON.get().create(player.level());
                        if(bee != null) {
                            bee.owner = player;
                            bee.stackSource = itemStack.copy();
                            bee.setPos(new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ()));
                            player.level().addFreshEntity(bee);
                        }
                    }
                    player.getCooldowns().addCooldown(item, 40);
                }
            }
        }
    };
}

