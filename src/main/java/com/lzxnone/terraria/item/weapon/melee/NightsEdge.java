package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.beam.ISwordBeamBehavior;
import com.lzxnone.terraria.entity.beam.SwordBeam;
import com.lzxnone.terraria.entity.beam.SwordBeamBehaviors;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.IItemWaveBehavior;
import com.lzxnone.terraria.network.payload.SwordBeamPayload;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.utils.*;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigBoolean;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;

import java.util.List;

public class NightsEdge extends MeleeWeapon {
    public static final String ID = "nights_edge";
    public static final ConfigFloat BASE_MELEE_DAMAGE = createBaseMeleeDamageConfig(ID, 6.0F);
    public static final ConfigFloat BASE_MELEE_ATTACK_SPEED = createBaseMeleeAttackSpeedConfig(ID, -1.5F);
    public static final ConfigBoolean PROJECTILE_ALIGN_TO_BLADE = new ConfigBoolean(
        "weapon.nights_edge.projectile_align_to_blade",
        "nights_edge_projectile_align_to_blade",
        true
    );
    public static final ConfigInt ROTATE_RANGE = new ConfigInt(
        "weapon.nights_edge.rotate_range",
        "nights_edge_rotate_range",
        45,
        0,
        90
    );
    public static final ConfigInt PROJECTILE_ROTATE_RANGE = new ConfigInt(
        "weapon.nights_edge.projectile_rotate_range",
        "nights_edge_projectile_rotate_range",
        40,
        0,
        90
    );
    public static final ConfigInt MAX_HIT_COUNT = new ConfigInt(
        "weapon.nights_edge.max_hit_count",
        "nights_edge_max_hit_count",
        2,
        0,
        100
    );
    public static final ConfigInt PROJECTILE_MAX_HIT_COUNT = new ConfigInt(
        "weapon.nights_edge.projectile_max_hit_count",
        "nights_edge_projectile_max_hit_count",
        2,
        0,
        100
    );
    public static final ConfigFloat DAMAGE = new ConfigFloat(
        "weapon.nights_edge.damage",
        "nights_edge_damage",
        7.0f,
        0.0f,
        8388600.0f
    );
    public static final ConfigFloat PROJECTILE_DAMAGE = new ConfigFloat(
        "weapon.nights_edge.projectile_damage",
        "nights_edge_projectile_damage",
        7.0f,
        0.0f,
        8388600.0f
    );
    public static final ConfigDouble PROJECTILE_SPEED = new ConfigDouble(
        "weapon.nights_edge.projectile_speed",
        "nights_edge_projectile_speed",
        0.25,
        0.0,
        10.0
    );
    public static final ConfigInt PROJECTILE_LIFETIME = new ConfigInt(
        "weapon.nights_edge.projectile_lifetime",
        "nights_edge_projectile_lifetime",
        10,
        1,
        1200
    );
    public static final ConfigInt PROJECTILE_ROTATION_SPEED = new ConfigInt(
        "weapon.nights_edge.projectile_rotation_speed",
        "nights_edge_projectile_rotation_speed",
        36,
        0,
        360
    );
    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                BASE_MELEE_DAMAGE,
                BASE_MELEE_ATTACK_SPEED,
                PROJECTILE_ALIGN_TO_BLADE,
                ROTATE_RANGE,
                PROJECTILE_ROTATE_RANGE,
                MAX_HIT_COUNT,
                PROJECTILE_MAX_HIT_COUNT,
                DAMAGE,
                PROJECTILE_DAMAGE,
                PROJECTILE_SPEED,
                PROJECTILE_LIFETIME,
                PROJECTILE_ROTATION_SPEED
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);

    public NightsEdge() {
        super(Tiers.IRON, new Item.Properties().rarity(Rarity.UNCOMMON));
    }

    @Override
    protected float getBaseMeleeDamage(ItemStack stack) {
        return BASE_MELEE_DAMAGE.get();
    }

    @Override
    protected float getBaseMeleeAttackSpeed(ItemStack stack) {
        return BASE_MELEE_ATTACK_SPEED.get();
    }

    public static final CompoundTag BEAM_DATA = Util.make(new CompoundTag(), tag -> {
        tag.putString("behavior", "nights_edge");
        tag.putInt("lifetime", 7);
        tag.putInt("cooldown", 7);
        tag.putFloat("color0R", 0.165f);
        tag.putFloat("color0G", 0.098f);
        tag.putFloat("color0B", 0.247f);
        tag.putFloat("color1R", 0.278f);
        tag.putFloat("color1G", 0.141f);
        tag.putFloat("color1B", 0.588f);
        tag.putFloat("color2R", 0.325f);
        tag.putFloat("color2G", 0.212f);
        tag.putFloat("color2B", 0.553f);

        CompoundTag customData = new CompoundTag();
        customData.putInt("hitEntityCount", 0);
        tag.put("customData", customData);
    });

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.075f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(0.463F, 0.196F, 0.918F),
            new Vector3f(0.408F, 0.373F, 0.494F)
        }
    );

    public static final ISwordBeamBehavior SWORD_BEAM_BEHAVIOR = new ISwordBeamBehavior() {

        @Override
        public void onMoving(SwordBeam beam) {
            if(beam.currentPosition == null) return;
            ParticleUtil.addParticle(
                beam.level(), PARTICLE,
                beam.currentPosition, 0.2,
                new Vec3(0, 0, 0), 0.2
            );
        }

        @Override
        public void onHitEntity(SwordBeam beam, EntityHitResult result) {
            if(!beam.level().isClientSide()) {
                Entity target = result.getEntity();
                if(beam.getOwner() instanceof Player player && FilterUtil.createTargetFilter(player).test(target)) {
                    CompoundTag custom_data = beam.getEntityData().get(SwordBeam.CUSTOM_DATA);
                    if(custom_data.contains("hitEntityCount")) {
                        int count = custom_data.getInt("hitEntityCount");
                        if(count < MAX_HIT_COUNT.get()) {
                            if(DamageUtil.meleeAttack(beam, target, beam.getEntityData().get(SwordBeam.STACK_SOURCE), (float) DAMAGE.get(), 1.0f, 20)) {
                                ParticleUtil.addParticles(
                                    (ServerLevel) target.level(), ModParticles.NIGHTS_EDGE_HIT_PARTICLE.get(),
                                    new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2.0, target.getZ()), new Vec3(0, 0, 0),
                                    0, 1
                                );
                                count++;
                                custom_data.putInt("hitEntityCount", count);
                                beam.getEntityData().set(SwordBeam.CUSTOM_DATA, custom_data);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void generate(Entity entity, CompoundTag beamData) {
            int randomAngle = (int) (ROTATE_RANGE.get() * (Math.random() * 2 - 1));
            beamData.putInt("rotate", randomAngle);
            ISwordBeamBehavior.super.generate(entity, beamData);
            if(entity instanceof Player player) summon(player, randomAngle);
        }
    };

    public static final Vector3f COLOR0 = new Vector3f(0.278f, 0.141f, 0.588f);

    public static final ResourceLocation RES0 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/terra_beam0.png");
    public static final ResourceLocation RES1 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/terra_beam3.png");
    public static final ResourceLocation RES2 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/beam_sparkle.png");

    public static final float FADE_IN = 0.33f;
    public static final float FADE_OUT = 0.67f;

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {

        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);

            //碰撞箱计算
            Vec3 center = summon.position().add(summon.getLookAngle().normalize().scale(SwordBeam.DIST));
            if(customData.contains("extX") && customData.contains("extY") && customData.contains("extZ")) {
                summon.setBoundingBox(new AABB(
                    center.x - customData.getFloat("extX"), center.y - customData.getFloat("extY"), center.z - customData.getFloat("extZ"),
                    center.x + customData.getFloat("extX"), center.y + customData.getFloat("extY"), center.z + customData.getFloat("extZ")
                ));
            }

            //碰撞计算
            if(!summon.level().isClientSide() && customData.contains("hitCount") && summon.getOwner() instanceof Player player) {
                int count = customData.getInt("hitCount");
                if(count < PROJECTILE_MAX_HIT_COUNT.get()) {
                    List<Entity> targets = summon.level().getEntitiesOfClass(Entity.class, summon.getBoundingBox(), FilterUtil.createTargetFilter(summon, summon.getOwner()));
                    for(Entity target : targets) {
                        if(count >= PROJECTILE_MAX_HIT_COUNT.get()) break;
                        if(DamageUtil.meleeAttack(summon, target, summon.getEntityData().get(StaticSummon.STACK_SOURCE), (float) PROJECTILE_DAMAGE.get(), 1.0f, 20)) {
                            count++;
                            ParticleUtil.addParticles(
                                (ServerLevel) target.level(), ModParticles.NIGHTS_EDGE_HIT_PARTICLE.get(),
                                new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2.0, target.getZ()), new Vec3(0, 0, 0),
                                0, 1
                            );
                        }
                    }
                    if(!targets.isEmpty()) customData.putInt("hitCount", count);
                }
                if(count >= PROJECTILE_MAX_HIT_COUNT.get()) this.onDied(summon);
            }

            ClipContext context = new ClipContext(
                summon.position(),
                summon.position().add(summon.getDeltaMovement()),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                summon
            );
            BlockHitResult blockHit = summon.level().clip(context);
            if(blockHit.getType() != HitResult.Type.MISS) {

            }

            //粒子
            if(summon.level().isClientSide()) {
                ParticleUtil.addParticle(
                    summon.level(), PARTICLE,
                    summon.position(), 2.5,
                    new Vec3(0, 0, 0), 0.5
                );
            }
        }
    };

    public static void summon(Player player, int randomAngle) {
        StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), player.level());
        summon.setOwner(player);
        summon.getEntityData().set(StaticSummon.STACK_SOURCE, player.getWeaponItem().copy());
        Vec3 pos = new Vec3(player.getX(), player.getEyeY() - 0.1, player.getZ());
        summon.setPos(pos);

        if(!PROJECTILE_ALIGN_TO_BLADE.get()) randomAngle = (int) (PROJECTILE_ROTATE_RANGE.get() * (Math.random() * 2 - 1));

        Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
        dirs = MathUtil.rotateCoordinateSystem(dirs[0], dirs[2], randomAngle);
        float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
        summon.setXRot(xyRot[0]);
        summon.xRotO = xyRot[0];
        summon.setYRot(xyRot[1]);
        summon.yRotO = xyRot[1];

        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.NIGHTS_EDGE_BEAM);
        summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
        summon.getEntityData().set(StaticSummon.LIFETIME, PROJECTILE_LIFETIME.get());
        summon.getEntityData().set(StaticSummon.RZP, randomAngle);
        summon.getEntityData().set(StaticSummon.RZPS, PROJECTILE_ROTATION_SPEED.get());
        summon.getEntityData().set(StaticSummon.GLOW, true);
        summon.setNoGravity(true);
        summon.noPhysics = true;

        CompoundTag customData = new CompoundTag();

        Quaternionf rotation = new Quaternionf()
            .fromAxisAngleRad(dirs[0], (float) Math.toRadians(Math.abs(dirs[0].y) > 0.999 ? 0 : randomAngle))
            .rotateY((float) Math.toRadians(-xyRot[1]))
            .rotateX((float) Math.toRadians(xyRot[0]))
            .rotateX((float) Math.toRadians(-90.0))
            .rotateZ((float) Math.toRadians(-90.0));

        Vector3f axisX = new Vector3f(1, 0, 0).rotate(rotation);
        Vector3f axisY = new Vector3f(0, 1, 0).rotate(rotation);
        Vector3f axisZ = new Vector3f(0, 0, 1).rotate(rotation);

        float extX = SwordBeam.HALF_WIDTH * SwordBeam.SCALE * Math.abs(axisX.x()) + SwordBeam.HALF_HEIGHT * SwordBeam.SCALE * Math.abs(axisY.x()) + SwordBeam.HALF_THICKNESS * SwordBeam.SCALE * Math.abs(axisZ.x());
        float extY = SwordBeam.HALF_WIDTH * SwordBeam.SCALE * Math.abs(axisX.y()) + SwordBeam.HALF_HEIGHT * SwordBeam.SCALE * Math.abs(axisY.y()) + SwordBeam.HALF_THICKNESS * SwordBeam.SCALE * Math.abs(axisZ.y());
        float extZ = SwordBeam.HALF_WIDTH * SwordBeam.SCALE * Math.abs(axisX.z()) + SwordBeam.HALF_HEIGHT * SwordBeam.SCALE * Math.abs(axisY.z()) + SwordBeam.HALF_THICKNESS * SwordBeam.SCALE * Math.abs(axisZ.z());

        customData.putInt("hitCount", 0);
        customData.putFloat("extX", extX);
        customData.putFloat("extY", extY);
        customData.putFloat("extZ", extZ);
        customData.putFloat("dirX", dirs[0].x);
        customData.putFloat("dirY", dirs[0].y);
        customData.putFloat("dirZ", dirs[0].z);
        customData.putFloat("upX", dirs[1].x);
        customData.putFloat("upY", dirs[1].y);
        customData.putFloat("upZ", dirs[1].z);
        customData.putFloat("rightX", dirs[2].x);
        customData.putFloat("rightY", dirs[2].y);
        customData.putFloat("rightZ", dirs[2].z);
        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

        summon.setDeltaMovement(player.getLookAngle().normalize().scale(PROJECTILE_SPEED.get()));

        player.level().addFreshEntity(summon);
    }

    public static final IItemWaveBehavior ITEM_WAVE_BEHAVIOR = new IItemWaveBehavior() {
        public void onLeftClickAir(PlayerInteractEvent.LeftClickEmpty event) {
            Player player = event.getEntity();
            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.isEmpty()) return;
            Item item = itemStack.getItem();
            if(item instanceof NightsEdge && !player.getCooldowns().isOnCooldown(item)) {
                PacketDistributor.sendToServer(new SwordBeamPayload("nights_edge", BEAM_DATA));
                SoundUtil.playClientSound(player, ModSounds.WAVE.get());
            }
        }
        public void onAttackEntity(AttackEntityEvent event) {
            Player player = event.getEntity();
            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.isEmpty()) return;
            Item item = itemStack.getItem();
            if(item instanceof NightsEdge && !player.getCooldowns().isOnCooldown(item)) {
                if(!player.level().isClientSide()) {
                    SwordBeamBehaviors.getBehavior("nights_edge").generate(player, BEAM_DATA);
                }else {
                    SoundUtil.playClientSound(player, ModSounds.WAVE.get());
                }
            }
            event.setCanceled(true);
        }
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(!level.isClientSide()) {
            SwordBeamBehaviors.getBehavior("nights_edge").generate(player, BEAM_DATA);
        }else {
            SoundUtil.playClientSound(player, ModSounds.WAVE.get());
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

}

