package com.lzxnone.terraria.item.weapon.ranged;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.weapon.RangedWeapon;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

public class StarCannon extends RangedWeapon {
    public static final String ID = "star_cannon";
    public static final Vector3f OFFSET = new Vector3f(-0.3f, -0.1f, 1.5f);
    public static final ConfigDouble SPEED = new ConfigDouble("weapon.star_cannon.speed", "star_cannon_speed", 2.0D, 0.0D, 24.0D);
    public static final ConfigFloat DAMAGE = new ConfigFloat("weapon.star_cannon.damage", "star_cannon_damage", 8.0f, 0.0f, 8388600.0f);

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(SPEED, DAMAGE);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        ID,
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/" + ID + ".png"),
        Component.translatable("item.lzxnoneterraria." + ID),
        CONFIG_DATA
    );

    public StarCannon() {
        super(Tiers.IRON, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
    }

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            Vec3 motion = summon.getLookAngle().normalize().scale(SPEED.get());
            Vec3 start = summon.position();
            Vec3 end = start.add(motion);

            if(summon.level().isClientSide()) {
                ParticleUtil.addParticle(
                    summon.level(), ModParticles.STAR_PARTICLE.get(),
                    summon.position(), 0.0,
                    new Vec3(0, 0, 0), 0.2
                );
                summon.setPos(end);
                return;
            }

            //碰撞检测
            AABB hitBox = new AABB(start, end).inflate(0.25);
            List<Entity> targets = summon.level().getEntitiesOfClass(
                Entity.class,
                hitBox,
                FilterUtil.createTargetFilter(summon, summon.getOwner())
            );
            for(Entity target : targets) {
                ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
                if(DamageUtil.rangedAttack(summon, target, sourceStack, DAMAGE.get(), 0.25f, 10)) {
                }
            }

            //方块检测
            BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, end);
            if(blockHitResult.getType() != HitResult.Type.MISS) {
                SoundUtil.playServerSound(summon.level(), ModSounds.STAR_COLLIDE.get(), summon.position());
                this.onDied(summon);
                return;
            }

            summon.setPos(end);
        }
    };

    @Override
    public boolean canUseAmmo(ItemStack weaponStack, ItemStack ammoStack) {
        return ammoStack.is(ModItems.FALLEN_STAR.get());
    }

    @Override
    protected ResourceLocation getDefaultAmmo(ItemStack weaponStack) {
        return ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "fallen_star");
    }

    @Override
    public int getUseTime(ItemStack weaponStack, LivingEntity entity) {
        return 12;
    }

    @Override
    public float getTooltipDamage(ItemStack weaponStack, LivingEntity entity) {
        float damage = entity instanceof Player player ? DamageUtil.applyPlayerDamageEffects(player, DAMAGE.get()) : DAMAGE.get();
        return applyRangedDamageBonus(weaponStack, entity, damage);
    }

    @Override
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        SoundUtil.playClientSound(player, ModSounds.STAR_FALL.get());
        if(!level.isClientSide()) {
            StaticSummon summon = AmmoUtil.createAmmoSummon(level, player, hand, stack, OFFSET);
            summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.FALLEN_STAR);
            summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.FALLEN_STAR.get()));
            level.addFreshEntity(summon);
        }
    }
}
