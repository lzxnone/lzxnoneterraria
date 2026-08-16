package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.weapon.melee.flail.Flail;
import com.lzxnone.terraria.utils.*;
import com.mojang.math.Axis;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;

import java.util.List;
import net.minecraft.world.item.enchantment.Enchantments;

public class Mace extends Flail {
    public static final String ID = "mace";
    public static final ConfigFloat BASE_MELEE_DAMAGE = createBaseMeleeDamageConfig(ID, 3F);
    public static final ConfigFloat BASE_MELEE_ATTACK_SPEED = createBaseMeleeAttackSpeedConfig(ID, -2.4F);
    public static final ConfigDouble PROJECTILE_SPEED = new ConfigDouble(
        "weapon.mace.projectile_speed",
        "mace_projectile_speed",
        2.0,
        0.0,
        10.0
    );
    public static final ConfigDouble GRAVITY = new ConfigDouble(
        "weapon.mace.gravity",
        "mace_gravity",
        0.75,
        0.0,
        5.0
    );
    public static final ConfigFloat DAMAGE = new ConfigFloat(
        "weapon.mace.damage",
        "mace_damage",
        4.0f,
        0.0f,
        8388600.0f
    );
    public static final ConfigInt FLY_TIME = new ConfigInt(
        "weapon.mace.fly_time",
        "mace_fly_time",
        10,
        1,
        100
    );
    public static final ConfigDouble MAX_RANGE = new ConfigDouble(
        "weapon.mace.max_range",
        "mace_max_range",
        32.0,
        1.0,
        512.0
    );
    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                BASE_MELEE_DAMAGE,
                BASE_MELEE_ATTACK_SPEED,
                PROJECTILE_SPEED,
                GRAVITY,
                DAMAGE,
                FLY_TIME,
                MAX_RANGE
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);

    public Mace() {
        super(Tiers.IRON, new Item.Properties());
    }

    @Override
    protected float getBaseMeleeDamage(ItemStack stack) {
        return BASE_MELEE_DAMAGE.get();
    }

    @Override
    protected float getBaseMeleeAttackSpeed(ItemStack stack) {
        return BASE_MELEE_ATTACK_SPEED.get();
    }

    public ItemStack getItemStack() {
        return ModItems.MACE.get().getDefaultInstance();
    }

    public ItemStack getProjectileItemStack() {
        return ModItems.MACE_PROJECTILE.get().getDefaultInstance();
    }

    public String getChainRes() {
        return "lzxnoneterraria:textures/vfx/mace_chain.png";
    }

    public boolean canDrop() {
        return true;
    }

    public float getDamage() {
        return DAMAGE.get();
    }

    public int getFlyTime() {
        return FLY_TIME.get();
    }

    public double getProjectileSpeed() {
        return PROJECTILE_SPEED.get();
    }

    public double getGravity() {
        return GRAVITY.get();
    }

    public double getMaxRange() {
        return MAX_RANGE.get();
    }

}

