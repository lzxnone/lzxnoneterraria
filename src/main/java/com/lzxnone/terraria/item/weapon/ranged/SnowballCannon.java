package com.lzxnone.terraria.item.weapon.ranged;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.weapon.RangedWeapon;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.*;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class SnowballCannon extends RangedWeapon {
    public static final String ID = "snowball_cannon";
    public static final Vector3f OFFSET = new Vector3f(-0.3f, -0.1f, 1.5f);
    public static final ConfigDouble SPEED = new ConfigDouble("weapon.snowball_cannon.speed", "snowball_cannon_speed", 1.5D, 0.0D, 24.0D);
    public static final ConfigDouble GRAVITY = new ConfigDouble("weapon.snowball_cannon.gravity", "snowball_cannon_gravity", 0.05D, 0.0D, 10.0D);
    public static final ConfigFloat DAMAGE = new ConfigFloat("weapon.snowball_cannon.damage", "snowball_cannon_damage", 2.5f, 0.0f, 8388600.0f);

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(SPEED, GRAVITY, DAMAGE);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        ID,
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/" + ID + ".png"),
        Component.translatable("item.lzxnoneterraria." + ID),
        CONFIG_DATA
    );

    public SnowballCannon() {
        super(Tiers.STONE, new Item.Properties().stacksTo(1));
    }

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            Vec3 motion = summon.getDeltaMovement().add(0, -GRAVITY.get(), 0);
            Vec3 start = summon.position();
            Vec3 end = start.add(motion);

            if(summon.level().isClientSide()) {
                summon.setDeltaMovement(motion);
                return;
            }

            //碰撞检测
            AABB hitBox = new AABB(start, end).inflate(0.25);
            List<Entity> targets = summon.level().getEntitiesOfClass(
                Entity.class,
                hitBox,
                FilterUtil.createTargetFilter(summon, summon.getOwner())
            );
            if(!targets.isEmpty()) {
                ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
                Entity target = targets.getFirst();
                if(DamageUtil.rangedAttack(summon, target, sourceStack, DAMAGE.get(), 0.5f, 10)) {
                    this.onDied(summon);
                    return;
                }
            }

            //方块检测
            BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, end);
            if(blockHitResult.getType() != HitResult.Type.MISS) {
                this.onDied(summon);
                return;
            }

            summon.setDeltaMovement(motion);
        }

        @Override
        public void onDied(StaticSummon summon) {
            if(summon.level() instanceof ServerLevel serverLevel) {
                ParticleUtil.addParticles(
                    serverLevel,
                    new ItemParticleOption(ParticleTypes.ITEM, Items.SNOWBALL.getDefaultInstance()),
                    summon.position(),
                    new Vec3(0.08, 0.08, 0.08),
                    0.05,
                    8
                );
                SoundUtil.playServerSound(summon.level(), ModSounds.SNOW_BREAK.get(), summon.position());
                summon.discard();
            }
        }
    };

    @Override
    public boolean canUseAmmo(ItemStack weaponStack, ItemStack ammoStack) {
        return ammoStack.is(Items.SNOWBALL);
    }

    @Override
    protected ResourceLocation getDefaultAmmo(ItemStack weaponStack) {
        return ResourceLocation.withDefaultNamespace("snowball");
    }

    @Override
    public int getUseTime(ItemStack weaponStack, LivingEntity entity) {
        return 19;
    }

    @Override
    public float getTooltipDamage(ItemStack weaponStack, LivingEntity entity) {
        float damage = entity instanceof Player player ? DamageUtil.applyPlayerDamageEffects(player, DAMAGE.get()) : DAMAGE.get();
        return applyRangedDamageBonus(weaponStack, entity, damage);
    }

    @Override
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        SoundUtil.playClientSound(player, ModSounds.SHOT2.get());
        if(!level.isClientSide()) {
            StaticSummon summon = AmmoUtil.createAmmoSummon(level, player, hand, stack, OFFSET);
            summon.setDeltaMovement(summon.getLookAngle().normalize().scale(SPEED.get()));
            level.addFreshEntity(summon);
        }
    }
}
