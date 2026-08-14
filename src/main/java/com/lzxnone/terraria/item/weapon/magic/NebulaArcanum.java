package com.lzxnone.terraria.item.weapon.magic;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.weapon.MagicWeapon;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.DamageUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;

public class NebulaArcanum extends MagicWeapon {
    public static final ConfigFloat DAMAGE = new ConfigFloat("weapon.nebula_arcanum.damage", "nebula_arcanum_damage", 0.0F, 0.0F, 8388600.0F);
    public static final ConfigDouble MANA_CONSUME = new ConfigDouble("weapon.nebula_arcanum.mana_consume", "nebula_arcanum_mana_consume", 0.0D, 0.0D, 10000.0D);

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DAMAGE, MANA_CONSUME);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "nebula_arcanum",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/nebula_arcanum.png"),
        Component.translatable("item.lzxnoneterraria.nebula_arcanum"),
        CONFIG_DATA
    );

    public NebulaArcanum() {
        super(Tiers.DIAMOND, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public float getTooltipDamage(ItemStack weaponStack, LivingEntity entity) {
        float damage = entity instanceof Player player ? DamageUtil.applyPlayerDamageEffects(player, DAMAGE.get()) : DAMAGE.get();
        return applyMagicDamageBonus(weaponStack, entity, damage);
    }

    @Override
    protected double getManaConsumeRate(ItemStack stack, LivingEntity entity) {
        return MANA_CONSUME.get();
    }
}
