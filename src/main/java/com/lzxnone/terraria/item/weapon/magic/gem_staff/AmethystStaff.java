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

public class AmethystStaff extends GemStaff {
    public static final Vector3f COLOR = new Vector3f(0.70F, 0.25F, 0.85F);
    public static final ConfigFloat DAMAGE = new ConfigFloat("weapon.amethyst_staff.damage", "amethyst_staff_damage", 4.0F, 0.0F, 8388600.0F);
    public static final ConfigFloat EXTRA_DAMAGE = new ConfigFloat("weapon.amethyst_staff.extra_damage", "amethyst_staff_extra_damage", 4.0F, 0.0F, 8388600.0F);
    public static final ConfigDouble MANA_CONSUME = new ConfigDouble("weapon.amethyst_staff.mana_consume", "amethyst_staff_mana_consume", 7.0D, 0.0D, 10000.0D);
    public static final ConfigDouble TARGET_RANGE = new ConfigDouble("weapon.amethyst_staff.target_range", "amethyst_staff_target_range", 16.0D, 0.0D, 128.0D);
    public static final int SKILL_MASK = SKILL_AMETHYST;

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                DAMAGE,
                EXTRA_DAMAGE,
                MANA_CONSUME,
                TARGET_RANGE
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "amethyst_staff",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/amethyst_staff.png"),
        Component.translatable("item.lzxnoneterraria.amethyst_staff"),
        CONFIG_DATA
    );

    public AmethystStaff() {
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
        return computeUseTime(37, weaponStack, entity);
    }

    @Override
    public Vector3f getColor() {
        return COLOR;
    }
}
