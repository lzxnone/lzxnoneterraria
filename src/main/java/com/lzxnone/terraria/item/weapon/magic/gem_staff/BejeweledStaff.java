package com.lzxnone.terraria.item.weapon.magic.gem_staff;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.weapon.MagicWeapon;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BejeweledStaff extends MagicWeapon {
    public static final ConfigFloat DAMAGE = new ConfigFloat("weapon.bejeweled_staff.damage", "bejeweled_staff_damage", 8.0F, 0.0F, 8388600.0F);
    public static final ConfigDouble SPEED = new ConfigDouble("weapon.bejeweled_staff.speed", "bejeweled_staff_speed", 2.0D, 0.1D, 100.0D);
    public static final ConfigInt LIFETIME = new ConfigInt("weapon.bejeweled_staff.lifetime", "bejeweled_staff_lifetime", 50, 1, 100000);
    public static final ConfigDouble MANA_CONSUME = new ConfigDouble("weapon.bejeweled_staff.mana_consume", "bejeweled_staff_mana_consume", 15.0D, 0.0D, 10000.0D);

    public static final int ANGLE = 15;

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                DAMAGE,
                SPEED,
                LIFETIME,
                MANA_CONSUME
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "bejeweled_staff",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/bejeweled_staff.png"),
        Component.translatable("item.lzxnoneterraria.bejeweled_staff"),
        CONFIG_DATA
    );

    public BejeweledStaff() {
        super(Tiers.DIAMOND, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
    }

    public static final Vector3f[] COLORS = new Vector3f[] {
        AmethystStaff.COLOR,
        TopazStaff.COLOR,
        SapphireStaff.COLOR,
        EmeraldStaff.COLOR,
        AmberStaff.COLOR,
        RubyStaff.COLOR,
        DiamondStaff.COLOR
    };

    public Vector3f getColor() {
        return COLORS[java.util.concurrent.ThreadLocalRandom.current().nextInt(COLORS.length)];
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

    @Override
    public int getUseTime(ItemStack weaponStack, LivingEntity entity) {
        return computeUseTime(22, weaponStack, entity);
    }

    @Override
    public void shoot(Level level, Player player, InteractionHand hand, ItemStack weaponStack) {
        if (level.isClientSide()) return;

        SoundUtil.playServerSound(player, ModSounds.BEAM3.get(), 1.0F, 1.0F);

        Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
        Quaternionf rot = new Quaternionf().rotateAxis((float) Math.toRadians(ANGLE), dirs[1].x(), dirs[1].y(), dirs[1].z());
        Quaternionf rot2 = new Quaternionf().rotateAxis((float) Math.toRadians(-ANGLE), dirs[1].x(), dirs[1].y(), dirs[1].z());
        Vector3f[] shootDirs = {
            new Vector3f(dirs[0]),
            new Vector3f(dirs[0]).rotate(rot),
            new Vector3f(dirs[0]).rotate(rot2),
        };

        int skill = GemStaff.SKILL_AMETHYST | GemStaff.SKILL_TOPAZ | GemStaff.SKILL_EMERALD | GemStaff.SKILL_AMBER | GemStaff.SKILL_DIAMOND;
        Vector3f color = getColor();
        Vec3 basePos = player.getBoundingBox().getCenter();
        for(int i = 0;i < 3;i++) {
            StaticSummon core = new StaticSummon(ModEntities.STATIC_SUMMON.get(), level);
            core.setOwner(player);
            core.setPos(basePos);
            core.setDeltaMovement(MathUtil.toVec3(shootDirs[i]).normalize().scale(SPEED.get()));
            core.getEntityData().set(StaticSummon.STACK_SOURCE, weaponStack.copy());
            core.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.GEM_STAFF_PROJECTILE);
            core.getEntityData().set(StaticSummon.LIFETIME, LIFETIME.get());
            core.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
            core.getEntityData().set(StaticSummon.GLOW, false);
            core.setNoGravity(true);
            core.noPhysics = true;

            float[] xyRot = MathUtil.computeXYRot(shootDirs[i], dirs[1]);
            core.setXRot(xyRot[0]);
            core.setYRot(xyRot[1]);
            core.xRotO = xyRot[0];
            core.yRotO = xyRot[1];

            StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), level);
            projectile.setOwner(core);

            CompoundTag coreData = new CompoundTag();
            coreData.putInt("skill", skill);
            coreData.putUUID("projectileUUID", projectile.getUUID());
            core.getEntityData().set(StaticSummon.CUSTOM_DATA, coreData);
            level.addFreshEntity(core);

            projectile.setPos(basePos);
            projectile.getEntityData().set(StaticProjectile.STACK_SOURCE, weaponStack.copy());
            projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.GEM_STAFF_PROJECTILE);
            projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "custom");
            projectile.getEntityData().set(StaticProjectile.GLOW, true);
            projectile.getEntityData().set(StaticProjectile.LIFETIME, LIFETIME.get());
            projectile.getEntityData().set(StaticProjectile.ORIGIN, basePos.toVector3f());

            projectile.getEntityData().set(StaticProjectile.COLOR_R, color.x);
            projectile.getEntityData().set(StaticProjectile.COLOR_G, color.y);
            projectile.getEntityData().set(StaticProjectile.COLOR_B, color.z);

            CompoundTag projData = new CompoundTag();
            projData.putInt("skill", skill);
            projData.putUUID("playerUUID", player.getUUID());
            projData.putFloat("damage", DAMAGE.get() / 3.0F);
            projData.putFloat("knockback", 0.5F);
            projData.putFloat("colorR", color.x);
            projData.putFloat("colorG", color.y);
            projData.putFloat("colorB", color.z);

            projectile.getEntityData().set(StaticProjectile.CUSTOM_DATA, projData);
            level.addFreshEntity(projectile);
        }
    }
}
