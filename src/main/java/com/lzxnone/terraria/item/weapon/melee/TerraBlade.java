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
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigBoolean;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.*;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
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

import java.util.List;

public class TerraBlade extends MeleeWeapon {
    public static final String ID = "terra_blade";
    public static final ConfigFloat BASE_MELEE_DAMAGE = createBaseMeleeDamageConfig(ID, 10F);
    public static final ConfigFloat BASE_MELEE_ATTACK_SPEED = createBaseMeleeAttackSpeedConfig(ID, -1.0F);
    public static final ConfigBoolean PROJECTILE_ALIGN_TO_SWORD_BEAM = new ConfigBoolean(
        "weapon.terra_blade.projectile_align_to_sword_beam",
        "terra_projectile_align_to_blade",
        true
    );
    public static final ConfigInt SWORD_BEAM_ROTATE_RANGE = new ConfigInt(
        "weapon.terra_blade.sword_beam_rotate_range",
        "terra_blade_rotate_range",
        45,
        0,
        90
    );
    public static final ConfigInt PROJECTILE_ROTATE_RANGE = new ConfigInt(
        "weapon.terra_blade.projectile_rotate_range",
        "terra_projectile_rotate_range",
        45,
        0,
        90
    );
    public static final ConfigInt SWORD_BEAM_MAX_HIT_COUNT = new ConfigInt(
        "weapon.terra_blade.sword_beam_max_hit_count",
        "terra_blade_max_hit_count",
        3,
        0,
        100
    );
    public static final ConfigFloat SWORD_BEAM_DAMAGE = new ConfigFloat(
        "weapon.terra_blade.sword_beam_damage",
        "terra_blade_damage",
        11.0f,
        0.0f,
        8388600.0f
    );
    public static final ConfigFloat PROJECTILE_DAMAGE = new ConfigFloat(
        "weapon.terra_blade.projectile_damage",
        "terra_projectile_damage",
        11.0f,
        0.0f,
        8388600.0f
    );
    public static final ConfigDouble PROJECTILE_SPEED = new ConfigDouble(
        "weapon.terra_blade.projectile_speed",
        "terra_projectile_speed",
        2.0,
        0.0,
        10.0
    );
    public static final ConfigInt PROJECTILE_LIFETIME = new ConfigInt(
        "weapon.terra_blade.projectile_lifetime",
        "terra_projectile_lifetime",
        60,
        1,
        1200
    );
    public static final ConfigFloat PROJECTILE_DAMAGE_DECAY = new ConfigFloat(
        "weapon.terra_blade.projectile_damage_decay",
        "terra_projectile_damage_decay",
        0.75f,
        0.0f,
        1.0f
    );

    public TerraBlade() {
        super(Tiers.DIAMOND, new Item.Properties().rarity(Rarity.RARE));
    }

    @Override
    protected float getBaseMeleeDamage(ItemStack stack) {
        return BASE_MELEE_DAMAGE.get();
    }

    @Override
    protected float getBaseMeleeAttackSpeed(ItemStack stack) {
        return BASE_MELEE_ATTACK_SPEED.get();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                BASE_MELEE_DAMAGE,
                BASE_MELEE_ATTACK_SPEED,
                PROJECTILE_ALIGN_TO_SWORD_BEAM,
                SWORD_BEAM_ROTATE_RANGE,
                PROJECTILE_ROTATE_RANGE,
                SWORD_BEAM_MAX_HIT_COUNT,
                SWORD_BEAM_DAMAGE,
                PROJECTILE_DAMAGE,
                PROJECTILE_SPEED,
                PROJECTILE_LIFETIME,
                PROJECTILE_DAMAGE_DECAY
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);

    public static final CompoundTag BEAM_DATA = Util.make(new CompoundTag(), tag -> {
        tag.putString("behavior", "terra_blade");
        tag.putInt("lifetime", 5);
        tag.putInt("cooldown", 5);
        tag.putFloat("color0R", 0.173f);
        tag.putFloat("color0G", 0.482f);
        tag.putFloat("color0B", 0.796f);
        tag.putFloat("color1R", 0.431f);
        tag.putFloat("color1G", 0.729f);
        tag.putFloat("color1B", 0.396f);
        tag.putFloat("color2R", 0.478f);
        tag.putFloat("color2G", 0.663f);
        tag.putFloat("color2B", 0.220f);

        CompoundTag customData = new CompoundTag();
        customData.putInt("hitEntityCount", 0);
        tag.put("customData", customData);
    });

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.075f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(0.5F, 1.0F, 0.5F),
            new Vector3f(0.0F, 1.0F, 0.2F),
            new Vector3f(0.7F, 0.7F, 0.7F)
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
                        if(count < SWORD_BEAM_MAX_HIT_COUNT.get()) {
                            if(DamageUtil.meleeAttack(beam, target, beam.getEntityData().get(SwordBeam.STACK_SOURCE), SWORD_BEAM_DAMAGE.get(), 0.1f, 20)) {
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
            int randomAngle = (int) (SWORD_BEAM_ROTATE_RANGE.get() * (Math.random() * 2 - 1));
            beamData.putInt("rotate", randomAngle);
            ISwordBeamBehavior.super.generate(entity, beamData);
            if(entity instanceof Player player) summon(player, randomAngle);
        }
    };

    public static final Vector3f COLOR0 = new Vector3f(0.255f, 0.420f, 0.302f);
    public static final Vector3f COLOR1 = new Vector3f(0.173f, 0.482f, 0.796f);
    public static final Vector3f COLOR2 = new Vector3f(0.431f, 0.729f, 0.396f);

    public static final ResourceLocation RES0 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/terra_beam0.png");
    public static final ResourceLocation RES1 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/terra_beam3.png");
    public static final ResourceLocation RES2 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/terra_beam4.png");
    public static final ResourceLocation RES3 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/beam_sparkle.png");

    public static final float FADE_IN = 0.33f;
    public static final float FADE_OUT = 0.67f;

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {

        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);

            //运动逻辑
            if(!customData.contains("dead")) {
                float progress = summon.getEntityData().get(StaticSummon.AGE) / (float) summon.getEntityData().get(StaticSummon.LIFETIME);
                if(progress > FADE_IN) {
                    double projectileSpeed = PROJECTILE_SPEED.get();
                    summon.setDeltaMovement(summon.getLookAngle().normalize().scale(projectileSpeed - projectileSpeed * (progress - FADE_IN) / (1.0f - FADE_IN)));
                }else {
                    summon.setDeltaMovement(summon.getLookAngle().normalize().scale(PROJECTILE_SPEED.get()));
                }
            }else {
                summon.setDeltaMovement(summon.getLookAngle().normalize().scale(0.1));
                return;
            }

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
                List<Entity> targets = summon.level().getEntitiesOfClass(Entity.class, summon.getBoundingBox(), FilterUtil.createTargetFilter(summon, summon.getOwner()));
                for(Entity target : targets) {
                    if(DamageUtil.meleeAttack(summon, target, summon.getEntityData().get(StaticSummon.STACK_SOURCE), PROJECTILE_DAMAGE.get() * (float)Math.pow(PROJECTILE_DAMAGE_DECAY.get(), count), 0.1f, 12)) {
                        count++;
                        ParticleUtil.addParticles(
                            (ServerLevel) summon.level(), ModParticles.TERRA_BEAM_HIT_PARTICLE.get(),
                            new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2.0, target.getZ()), new Vec3(0, 0, 0),
                            0, 1
                        );
                    }
                }
                if(!targets.isEmpty()) customData.putInt("hitCount", count);
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
                customData.putBoolean("dead", true);
            }

            //粒子
            if(summon.level().isClientSide() && summon.tickCount % 4 == 0) {
                Vec3 dir = new Vec3(
                    customData.contains("dirX") ? customData.getFloat("dirX") : 0,
                    customData.contains("dirY") ? customData.getFloat("dirY") : 0,
                    customData.contains("dirZ") ? customData.getFloat("dirZ") : 0
                );
                Vec3 right = new Vec3(
                    customData.contains("rightX") ? customData.getFloat("rightX") : 0,
                    customData.contains("rightY") ? customData.getFloat("rightY") : 0,
                    customData.contains("rightZ") ? customData.getFloat("rightZ") : 0
                );

                Vec3 speed = dir.scale(summon.getDeltaMovement().length() / 4);
                for(int i = 0;i < 5;i++) {
                    Vec3 delta = right.scale((Math.random() - 0.5) * 6);
                    ParticleUtil.addParticle(
                        summon.level(), PARTICLE,
                        summon.position().add(delta), 0.0,
                        speed, 0.0
                    );
                }
            }
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
        }
    };

    public static void summon(Player player, int randomAngle) {
        StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), player.level());
        summon.setOwner(player);
        summon.getEntityData().set(StaticSummon.STACK_SOURCE, player.getWeaponItem().copy());
        Vec3 pos = new Vec3(player.getX(), player.getEyeY() - 0.1, player.getZ());
        summon.setPos(pos);

        if(!PROJECTILE_ALIGN_TO_SWORD_BEAM.get()) randomAngle = (int) (PROJECTILE_ROTATE_RANGE.get() * (Math.random() * 2 - 1));

        Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
        dirs = MathUtil.rotateCoordinateSystem(dirs[0], dirs[2], randomAngle);
        float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
        summon.setXRot(xyRot[0]);
        summon.xRotO = xyRot[0];
        summon.setYRot(xyRot[1]);
        summon.yRotO = xyRot[1];

        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.TERRA_BLADE_BEAM);
        summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
        summon.getEntityData().set(StaticSummon.LIFETIME, PROJECTILE_LIFETIME.get());
        summon.getEntityData().set(StaticSummon.RZP, randomAngle);
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
            if(item instanceof TerraBlade && !player.getCooldowns().isOnCooldown(item)) {
                PacketDistributor.sendToServer(new SwordBeamPayload("terra_blade", BEAM_DATA));
                SoundUtil.playClientSound(player, ModSounds.WAVE.get());
                SoundUtil.playClientSound(player, ModSounds.WAVE3.get());
            }
        }
        public void onAttackEntity(AttackEntityEvent event) {
            Player player = event.getEntity();
            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.isEmpty()) return;
            Item item = itemStack.getItem();
            if(item instanceof TerraBlade && !player.getCooldowns().isOnCooldown(item)) {
                if(!player.level().isClientSide()) {
                    SwordBeamBehaviors.getBehavior("terra_blade").generate(player, BEAM_DATA);
                }else {
                    SoundUtil.playClientSound(player, ModSounds.WAVE.get());
                    SoundUtil.playClientSound(player, ModSounds.WAVE3.get());
                }
            }
            event.setCanceled(true);
        }
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(!level.isClientSide()) {
            SwordBeamBehaviors.getBehavior("terra_blade").generate(player, BEAM_DATA);
        }else {
            SoundUtil.playClientSound(player, ModSounds.WAVE.get());
            SoundUtil.playClientSound(player, ModSounds.WAVE3.get());
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}

