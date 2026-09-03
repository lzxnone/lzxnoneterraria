package com.lzxnone.terraria.item.weapon.magic.gem_staff;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import org.joml.Vector3f;

public class TopazStaff extends GemStaff {
    public static final Vector3f COLOR = new Vector3f(1.0F, 0.75F, 0.15F);
    public static final ConfigFloat DAMAGE = new ConfigFloat("weapon.topaz_staff.damage", "topaz_staff_damage", 4.5F, 0.0F, 8388600.0F);
    public static final ConfigFloat EXTRA_DAMAGE = new ConfigFloat("weapon.topaz_staff.extra_damage", "topaz_staff_extra_damage", 4.0F, 0.0F, 8388600.0F);
    public static final ConfigDouble MANA_CONSUME = new ConfigDouble("weapon.topaz_staff.mana_consume", "topaz_staff_mana_consume", 7.0D, 0.0D, 10000.0D);
    public static final ConfigDouble EXPLOSION_RANGE = new ConfigDouble("weapon.topaz_staff.explosion_range", "topaz_staff_explosion_range", 3.0D, 0.0D, 64.0D);
    public static final ConfigFloat EXPLOSION_DAMAGE = new ConfigFloat("weapon.topaz_staff.explosion_damage", "topaz_staff_explosion_damage", 3.0F, 0.0F, 8388600.0F);
    public static final int SKILL_MASK = SKILL_TOPAZ;

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                DAMAGE,
                EXTRA_DAMAGE,
                MANA_CONSUME,
                EXPLOSION_RANGE,
                EXPLOSION_DAMAGE
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "topaz_staff",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/topaz_staff.png"),
        Component.translatable("item.lzxnoneterraria.topaz_staff"),
        CONFIG_DATA
    );

    public TopazStaff() {
        super(Tiers.IRON, new Item.Properties().stacksTo(1), SKILL_MASK);
    }

    @Override
    public float getDamage(ItemStack stack, LivingEntity entity) {
        return DAMAGE.get();
    }

    @Override
    public float getExtraDamage(ItemStack stack, LivingEntity entity) {
        return EXTRA_DAMAGE.get();
    }

    @Override
    protected double getManaConsumeRate(ItemStack stack, LivingEntity entity) {
        return MANA_CONSUME.get();
    }

    @Override
    public int getUseTime(ItemStack weaponStack, LivingEntity entity) {
        return computeUseTime(36, weaponStack, entity);
    }

    @Override
    public Vector3f getColor() {
        return COLOR;
    }
}
