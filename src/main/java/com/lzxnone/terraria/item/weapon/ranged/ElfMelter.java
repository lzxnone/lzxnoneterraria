package com.lzxnone.terraria.item.weapon.ranged;

import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.weapon.RangedWeapon;
import com.lzxnone.terraria.item.weapon.ranged.gun.Gun;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class ElfMelter extends RangedWeapon {
    public static final String ID = "elf_melter";
    public static final ConfigFloat DAMAGE = new ConfigFloat("weapon.elf_melter.damage", "elf_melter_damage", 4.0f, 0.0f, 8388600.0f);
    public static final ConfigDouble SPEED = new ConfigDouble("weapon.elf_melter.speed", "elf_melter_speed", 4.0D, 0.1D, 24.0D);
    public static final ConfigDouble RANGE = new ConfigDouble("weapon.elf_melter.range", "elf_melter_range", 32.0D, 1.0D, 128.0D);
    public static final ConfigInt FREEZE_TICKS = new ConfigInt("weapon.elf_melter.freeze_ticks", "elf_melter_freeze_ticks", 400, 0, 72000);
    public static final Vector3f OFFSET = new Vector3f(-0.3f, -0.1f, 1.5f);
    public static final float FLAME_RED = 0.35f;
    public static final float FLAME_GREEN = 0.85f;
    public static final float FLAME_BLUE = 1.0f;

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DAMAGE, SPEED, RANGE, FREEZE_TICKS);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = Gun.createConfigListItem(ID, CONFIG_DATA);

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            double a = customData.contains("a") ? customData.getDouble("a") : 1;
            boolean dead = customData.contains("dead");
            int t = summon.getEntityData().get(StaticSummon.AGE);
            double v = dead ? 0 : SPEED.get() - a * t;

            Vec3 motion = summon.getLookAngle().normalize().scale(v);
            Vec3 start = summon.position();
            Vec3 end = start.add(motion);

            if(summon.level().isClientSide()) {
                summon.setPos(end);
                return;
            }

            //碰撞检测
            Vec3 collisionStart = new Vec3(
                customData.contains("startX") ? customData.getFloat("startX") : summon.getX(),
                customData.contains("startY") ? customData.getFloat("startY") : summon.getY(),
                customData.contains("startZ") ? customData.getFloat("startZ") : summon.getZ()
            );
            AABB hitBox = new AABB(collisionStart, end).inflate(1.0);
            List<Entity> targets = summon.level().getEntitiesOfClass(
                Entity.class,
                hitBox,
                FilterUtil.createTargetFilter(summon, summon.getOwner())
            );
            for(Entity target : targets) {
                if(target.getBoundingBox().inflate(1.0).clip(collisionStart, end).isEmpty()) continue;
                ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
                if(DamageUtil.rangedAttack(summon, target, sourceStack, DAMAGE.get(), 0.25f)) {
                    target.invulnerableTime = 12;
                    if(target instanceof LivingEntity livingTarget) {
                        livingTarget.setTicksFrozen(Math.max(livingTarget.getTicksFrozen(), FREEZE_TICKS.get()));
                    }
                }
            }

            //方块检测
            BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, end);
            if(blockHitResult.getType() != HitResult.Type.MISS) {
                customData.putBoolean("dead", true);
                int left = Math.min(10, summon.getEntityData().get(StaticSummon.LIFETIME) - t);
                summon.getEntityData().set(StaticSummon.AGE, left);
                summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
                return;
            }

            summon.setPos(end);
        }

        @Override
        public AABB getBoundingBoxForCulling(StaticSummon summon) {
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            Vec3 start = new Vec3(
                customData.contains("startX") ? customData.getFloat("startX") : summon.getX(),
                customData.contains("startY") ? customData.getFloat("startY") : summon.getY(),
                customData.contains("startZ") ? customData.getFloat("startZ") : summon.getZ()
            );
            return new AABB(start, summon.position()).inflate(4);
        }
    };

    public ElfMelter() {
        super(Tiers.IRON, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public boolean canUseAmmo(ItemStack weaponStack, ItemStack ammoStack) {
        return ammoStack.is(Items.SLIME_BALL);
    }

    @Override
    protected ResourceLocation getDefaultAmmo(ItemStack weaponStack) {
        return ResourceLocation.withDefaultNamespace("slime_ball");
    }

    @Override
    public int getUseTime(ItemStack weaponStack, LivingEntity entity) {
        return 15;
    }

    @Override
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        SoundUtil.playClientSound(player, ModSounds.SHOT9.get());
        if(!level.isClientSide()) {
            StaticSummon summon = AmmoUtil.createAmmoSummon(level, player, hand, stack, OFFSET);
            summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.ELF_MELTER_FLAME);
            double projectileSpeed = SPEED.get();
            double projectileRange = RANGE.get();
            double a = projectileSpeed * projectileSpeed / (2 * projectileRange);
            CompoundTag customData = new CompoundTag();
            customData.putDouble("a", a);
            customData.putFloat("startX", (float) summon.getX());
            customData.putFloat("startY", (float) summon.getY());
            customData.putFloat("startZ", (float) summon.getZ());
            customData.putFloat("flameRed", FLAME_RED);
            customData.putFloat("flameGreen", FLAME_GREEN);
            customData.putFloat("flameBlue", FLAME_BLUE);
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
            summon.getEntityData().set(StaticSummon.LIFETIME, (int) (projectileSpeed / a));
            level.addFreshEntity(summon);
        }
    }
}
