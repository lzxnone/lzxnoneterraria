package com.lzxnone.terraria.item.weapon.ranged;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.ModItemTags;
import com.lzxnone.terraria.item.weapon.RangedWeapon;
import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.ConfigUtil;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.AmmoUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;

public class SDMG extends RangedWeapon {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    public static final String AMMO_NOT_CONSUME_CHANCE_PATH = "weapon.sdmg.ammo_not_consume_chance";
    public static final float AMMO_NOT_CONSUME_CHANCE_DEFAULT = 0.66f;
    public static final float AMMO_NOT_CONSUME_CHANCE_MIN = 0.0f;
    public static final float AMMO_NOT_CONSUME_CHANCE_MAX = 1.0f;

    public static final String DAMAGE_PATH = "weapon.sdmg.damage";
    public static final float DAMAGE_DEFAULT = 1.0f;
    public static final float DAMAGE_MIN = 0.0f;
    public static final float DAMAGE_MAX = 8388600.0f;

    public SDMG() {
        super(Tiers.NETHERITE, new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC));
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigFactory.loadFloatConfig(AMMO_NOT_CONSUME_CHANCE_PATH, configText("sdmg_ammo_not_consume_chance"), configTooltip("sdmg_ammo_not_consume_chance"), AMMO_NOT_CONSUME_CHANCE_DEFAULT, AMMO_NOT_CONSUME_CHANCE_MIN, AMMO_NOT_CONSUME_CHANCE_MAX);
            ConfigFactory.loadFloatConfig(DAMAGE_PATH, configText("sdmg_damage"), configTooltip("sdmg_damage"), DAMAGE_DEFAULT, DAMAGE_MIN, DAMAGE_MAX);
        }
    };

    private static Component configText(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key);
    }

    private static Component configTooltip(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key + ".tooltip");
    }

    public static float getAmmoNotConsumeChance() {
        return Math.clamp(ConfigUtil.readFloat(AMMO_NOT_CONSUME_CHANCE_PATH, AMMO_NOT_CONSUME_CHANCE_DEFAULT), AMMO_NOT_CONSUME_CHANCE_MIN, AMMO_NOT_CONSUME_CHANCE_MAX);
    }

    public static float getDamage() {
        return Math.clamp(ConfigUtil.readFloat(DAMAGE_PATH, DAMAGE_DEFAULT), DAMAGE_MIN, DAMAGE_MAX);
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "sdmg",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/sdmg.png"),
        Component.translatable("item.lzxnoneterraria.sdmg"),
        CONFIG_DATA
    );

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(!tryShoot(level, player, hand, stack)) return InteractionResultHolder.fail(stack);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public boolean canUseAmmo(ItemStack weaponStack, ItemStack ammoStack) {
        return ammoStack.is(ModItemTags.BULLET_AMMO);
    }

    @Override
    public int getUseTime(ItemStack weaponStack, LivingEntity entity) {
        return 5;
    }

    @Override
    public int getAmmoConsumeAmount(ItemStack weaponStack, LivingEntity entity) {
        return entity.getRandom().nextFloat() < getAmmoNotConsumeChance() ? 0 : 1;
    }

    @Override
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        SoundUtil.playClientSound(player, ModSounds.SHOT.get());
        if(!level.isClientSide()) {
            StaticSummon summon = AmmoUtil.createAmmoSummon(level, player, hand, stack);
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            customData.putFloat("damage", getDamage());
            customData.putFloat("knockbackScale", 0.1f);
            customData.putInt("invulnerableTime", 5);
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
            level.addFreshEntity(summon);
        }
    }
}
