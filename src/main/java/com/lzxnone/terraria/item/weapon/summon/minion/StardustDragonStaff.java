package com.lzxnone.terraria.item.weapon.summon.minion;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.attachment.PlayerSummon;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.network.PlayerSummonSync;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StardustDragonStaff extends MinionWeapon {
    public static final ConfigFloat DAMAGE = new ConfigFloat("weapon.stardust_dragon_staff.damage", "stardust_dragon_staff_damage", 10.0F, 0.0F, 8388600.0F);
    public static final ConfigFloat ADD_DAMAGE = new ConfigFloat("weapon.stardust_dragon_staff.add_damage", "stardust_dragon_staff_add_damage", 5.0F, 0.0F, 8388600.0F);
    public static final ConfigDouble RANGE = new ConfigDouble("weapon.stardust_dragon_staff.range", "stardust_dragon_staff_range", 64.0D, 1.0D, 256.0D);

    public static final double SEGMENT_SPACING = 0.65D;
    public static final float ROTATION_LERP = 0.25f;

    public static final ConfigDouble WANDER_SPEED = new ConfigDouble("weapon.stardust_dragon_staff.wander_speed", "stardust_dragon_staff_wander_speed", 0.85D, 0.01D, 32.0D);
    public static final ConfigDouble CHASE_SPEED = new ConfigDouble("weapon.stardust_dragon_staff.chase_speed", "stardust_dragon_staff_chase_speed", 2.0D, 0.01D, 32.0D);
    public static final ConfigDouble DASH_SPEED = new ConfigDouble("weapon.stardust_dragon_staff.dash_speed", "stardust_dragon_staff_dash_speed", 1.0D, 0.01D, 32.0D);
    public static final ConfigFloat WANDER_FRI = new ConfigFloat("weapon.stardust_dragon_staff.wander_fri", "stardust_dragon_staff_wander_fri", 0.88F, 0.0F, 1.0F);
    public static final ConfigFloat DASH_FRI = new ConfigFloat("weapon.stardust_dragon_staff.dash_fri", "stardust_dragon_staff_dash_fri", 0.78F, 0.0F, 1.0F);

    public enum State {
        IDLE,
        WANDER,
        FIGHT,
        CHASE,
        DASH,
        DASH_DONE
    };

    public StardustDragonStaff() {
        super(Tiers.NETHERITE, new Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC));
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                DAMAGE,
                ADD_DAMAGE,
                RANGE,
                WANDER_SPEED,
                CHASE_SPEED,
                DASH_SPEED,
                WANDER_FRI,
                DASH_FRI
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "stardust_dragon_staff",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/stardust_dragon_staff.png"),
        Component.translatable("item.lzxnoneterraria.stardust_dragon_staff"),
        CONFIG_DATA
    );

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.1f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(0.0F, 1.0F, 1.0F),
            new Vector3f(0.1F, 0.4F, 1.0F)
        }
    );

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            if(summon.getOwner() != null) {
                double dist = summon.getOwner().position().subtract(summon.position()).length();
                if(dist > MinionWeapon.MAX_RANGE) {
                    summon.setPos(summon.getOwner().position());
                }
            }
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            if(customData.contains("head")) {
                ParticleUtil.addParticle(
                    summon.level(), PARTICLE,
                    summon.position(), 0.0,
                    new Vec3(0, 0, 0), 0.1
                );
            }
            if(!summon.isAlive() || !(summon.level() instanceof ServerLevel serverLevel)) return;


            if(customData.contains("head")) {
                if(summon.getOwner() instanceof Player player) {
                    onState(summon, player);
                    tickDragonSegments(summon, player, serverLevel);
                }
            }else if(customData.contains("bodyA")) {
                if(!wasChainUpdatedThisTick(summon, serverLevel)) tickSegment(summon, customData);
            }else if(customData.contains("bodyB")) {
                if(!wasChainUpdatedThisTick(summon, serverLevel)) tickSegment(summon, customData);
            }else if(customData.contains("tail")) {
                if(!wasChainUpdatedThisTick(summon, serverLevel)) {
                    if(summon.getOwner() instanceof Player player) customData = repairTailPrevious(summon, player, serverLevel, customData);
                    tickSegment(summon, customData);
                }
            }else {
                onDied(summon);
            }
            if(!customData.contains("head")) {
                if(customData.contains("uuid")) {
                    Entity entity = serverLevel.getEntity(customData.getUUID("uuid"));
                    if(!(entity instanceof StaticSummon summon1) || !summon1.isAlive() || !summon1.getEntityData().get(StaticSummon.BEHAVIOR).equals(StaticSummonBehaviors.STARDUST_DRAGON_STAFF)) onDied(summon);
                }else {
                    onDied(summon);
                }
            }
            int state = customData.contains("state") ? customData.getInt("state") : 0;
            if(state == State.FIGHT.ordinal() || state == State.CHASE.ordinal() || state == State.DASH.ordinal() || state == State.DASH_DONE.ordinal()) {
                AABB hitBox = new AABB(summon.position(), summon.position().add(summon.getDeltaMovement())).inflate(1.0);
                List<Entity> targets = summon.level().getEntitiesOfClass(
                    Entity.class,
                    hitBox,
                    FilterUtil.createTargetFilter(summon, summon.getOwner())
                );
                ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
                int stardustDragonSlotCount = 0;
                if(summon.getOwner() instanceof Player player) {
                    for(PlayerSummon.SummonSlot slot : player.getData(ModAttachments.PLAYER_SUMMON).getMinionSlots()) {
                        if(slot.getId().equals(StaticSummonBehaviors.STARDUST_DRAGON_STAFF)) stardustDragonSlotCount++;
                    }
                }
                float damage = DAMAGE.get() + ADD_DAMAGE.get() * Math.max(0, stardustDragonSlotCount - 1);

                for(Entity target : targets) {
                    if(DamageUtil.summonAttack(summon, target, sourceStack, damage, 1.0f)) target.invulnerableTime = 15;
                }
            }
        }
    };

    public static void tickDragonSegments(StaticSummon head, Player player, ServerLevel serverLevel) {
        List<PlayerSummon.SummonSlot> stardustDragonSlots = new ArrayList<>();
        for(PlayerSummon.SummonSlot slot : player.getData(ModAttachments.PLAYER_SUMMON).getMinionSlots()) {
            if(slot.getId().equals(StaticSummonBehaviors.STARDUST_DRAGON_STAFF)) stardustDragonSlots.add(slot);
        }
        if(stardustDragonSlots.isEmpty()) return;

        PlayerSummon.SummonSlot tailSlot = stardustDragonSlots.getLast();
        List<UUID> tailSlotSummons = tailSlot.getSummons();
        if(tailSlotSummons.size() < 2 || !tailSlotSummons.getFirst().equals(head.getUUID())) return;

        Vec3 previousPos = head.position().add(head.getDeltaMovement());
        Vec3 previousDir = head.getDeltaMovement().lengthSqr() > 1.0E-6D ? head.getDeltaMovement().normalize() : head.getLookAngle().normalize();
        if(previousDir.lengthSqr() < 1.0E-6D) previousDir = new Vec3(0.0D, 0.0D, 1.0D);

        for(int i = 1; i < tailSlotSummons.size() - 1; i++) {
            StaticSummon segment = tickDragonSegmentTowards(serverLevel, tailSlotSummons.get(i), previousPos, previousDir);
            if(segment != null) {
                previousDir = previousPos.subtract(segment.position()).normalize();
                previousPos = segment.position();
            }
        }
        for(int i = stardustDragonSlots.size() - 2; i >= 0; i--) {
            for(UUID uuid : stardustDragonSlots.get(i).getSummons()) {
                StaticSummon segment = tickDragonSegmentTowards(serverLevel, uuid, previousPos, previousDir);
                if(segment != null) {
                    previousDir = previousPos.subtract(segment.position()).normalize();
                    previousPos = segment.position();
                }
            }
        }

        Entity tailEntity = serverLevel.getEntity(tailSlotSummons.getLast());
        if(tailEntity instanceof StaticSummon tailSummon && tailSummon.isAlive()) {
            repairTailPrevious(tailSummon, player, serverLevel, tailSummon.getEntityData().get(StaticSummon.CUSTOM_DATA));
            tickSegmentTowards(tailSummon, previousPos, previousDir);
            markChainUpdated(tailSummon, serverLevel);
        }
    }

    private static StaticSummon tickDragonSegmentTowards(ServerLevel serverLevel, UUID uuid, Vec3 previousPos, Vec3 fallbackDir) {
        Entity entity = serverLevel.getEntity(uuid);
        if(entity instanceof StaticSummon segment && segment.isAlive()) {
            tickSegmentTowards(segment, previousPos, fallbackDir);
            markChainUpdated(segment, serverLevel);
            return segment;
        }
        return null;
    }

    private static CompoundTag repairTailPrevious(StaticSummon tailSummon, Player player, ServerLevel serverLevel, CompoundTag customData) {
        Entity previous = customData.contains("uuid") ? serverLevel.getEntity(customData.getUUID("uuid")) : null;
        if(previous instanceof StaticSummon previousSummon && previousSummon.isAlive() && previousSummon.getEntityData().get(StaticSummon.BEHAVIOR).equals(StaticSummonBehaviors.STARDUST_DRAGON_STAFF)) {
            return customData;
        }

        UUID fallbackUUID = null;
        PlayerSummon.SummonSlot tailSlot = null;
        for(PlayerSummon.SummonSlot slot : player.getData(ModAttachments.PLAYER_SUMMON).getMinionSlots()) {
            if(!slot.getId().equals(StaticSummonBehaviors.STARDUST_DRAGON_STAFF)) continue;
            if(slot.getSummons().contains(tailSummon.getUUID())) {
                tailSlot = slot;
                continue;
            }
            if(!slot.getSummons().isEmpty()) {
                UUID candidateUUID = slot.getSummons().getLast();
                Entity candidate = serverLevel.getEntity(candidateUUID);
                if(candidate instanceof StaticSummon candidateSummon && candidateSummon.isAlive()) {
                    fallbackUUID = candidateUUID;
                    break;
                }
            }
        }
        if(fallbackUUID == null && tailSlot != null && tailSlot.getSummons().size() >= 2) {
            UUID candidateUUID = tailSlot.getSummons().get(tailSlot.getSummons().size() - 2);
            Entity candidate = serverLevel.getEntity(candidateUUID);
            if(candidate instanceof StaticSummon candidateSummon && candidateSummon.isAlive()) fallbackUUID = candidateUUID;
        }
        if(fallbackUUID == null) return customData;

        CompoundTag updatedCustomData = customData.copy();
        updatedCustomData.putUUID("uuid", fallbackUUID);
        tailSummon.getEntityData().set(StaticSummon.CUSTOM_DATA, updatedCustomData);
        return updatedCustomData;
    }

    private static boolean wasChainUpdatedThisTick(StaticSummon summon, ServerLevel serverLevel) {
        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
        return customData.contains("chainTick") && customData.getLong("chainTick") == serverLevel.getGameTime();
    }

    private static void markChainUpdated(StaticSummon summon, ServerLevel serverLevel) {
        if(!summon.isAlive()) return;
        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA).copy();
        customData.putLong("chainTick", serverLevel.getGameTime());
        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
    }

    public static void onState(StaticSummon summon, Player player) {
        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
        int state = customData.contains("state") ? customData.getInt("state") : 0;
        if(state == State.IDLE.ordinal()) {
            storeSummonTarget(summon, findSummonTarget(summon, player, RANGE.get()));
        }else if(state == State.WANDER.ordinal()) {
            storeSummonTarget(summon, findSummonTarget(summon, player, RANGE.get()));

            int wanderTime = customData.contains("wanderTime") ? customData.getInt("wanderTime") : 0;
            if(wanderTime > 0) {
                wanderTime--;
                customData.putInt("wanderTime", wanderTime);
                summon.setDeltaMovement(summon.getDeltaMovement().normalize().scale(WANDER_SPEED.get()));
            }else {
                summon.setDeltaMovement(summon.getDeltaMovement().scale(WANDER_FRI.get()));
            }
            float[] xyRot = MathUtil.computeXYRot(summon.getDeltaMovement().toVector3f());
            summon.setXRot(Mth.rotLerp(ROTATION_LERP, summon.getXRot(), xyRot[0]));
            summon.setYRot(Mth.rotLerp(ROTATION_LERP, summon.getYRot(), xyRot[1]));
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
        }else if(state == State.FIGHT.ordinal()) {
            storeSummonTarget(summon, findSummonTarget(summon, player, RANGE.get()));
        }else if(state == State.CHASE.ordinal()) {
            Entity target = getStoredSummonTarget(summon);
            if(target != null) {
                summon.setDeltaMovement(target.getBoundingBox().getCenter().subtract(summon.position()).normalize().scale(CHASE_SPEED.get()));
            }
            float[] xyRot = MathUtil.computeXYRot(summon.getDeltaMovement().toVector3f());
            summon.setXRot(Mth.rotLerp(ROTATION_LERP, summon.getXRot(), xyRot[0]));
            summon.setYRot(Mth.rotLerp(ROTATION_LERP, summon.getYRot(), xyRot[1]));
        }else if(state == State.DASH.ordinal()) {
            int dashTime = customData.contains("dashTime") ? customData.getInt("dashTime") : 0;
            if(dashTime > 0) {
                dashTime--;
                customData.putInt("dashTime", dashTime);
                summon.setDeltaMovement(summon.getDeltaMovement().normalize().scale(DASH_SPEED.get()));
            }else {
                summon.setDeltaMovement(summon.getDeltaMovement().scale(DASH_FRI.get()));
            }
            float[] xyRot = MathUtil.computeXYRot(summon.getDeltaMovement().toVector3f());
            summon.setXRot(Mth.rotLerp(ROTATION_LERP, summon.getXRot(), xyRot[0]));
            summon.setYRot(Mth.rotLerp(ROTATION_LERP, summon.getYRot(), xyRot[1]));
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
        }else if(state == State.DASH_DONE.ordinal()) {
            int dashDoneTime = customData.contains("dashDoneTime") ? customData.getInt("dashDoneTime") : 0;
            if(dashDoneTime > 0) {
                dashDoneTime--;
                customData.putInt("dashDoneTime", dashDoneTime);
                summon.setDeltaMovement(summon.getDeltaMovement().normalize().scale(DASH_SPEED.get()));
            }else {
                summon.setDeltaMovement(summon.getDeltaMovement().scale(DASH_FRI.get()));
            }
            float[] xyRot = MathUtil.computeXYRot(summon.getDeltaMovement().toVector3f());
            summon.setXRot(Mth.rotLerp(ROTATION_LERP, summon.getXRot(), xyRot[0]));
            summon.setYRot(Mth.rotLerp(ROTATION_LERP, summon.getYRot(), xyRot[1]));
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
        }

        int nextState = getNextState(summon, state);
        if(nextState != state) changeState(summon, state, nextState);
    }

    public static int getNextState(StaticSummon summon, int state) {
        if(state == State.IDLE.ordinal()) {
            Entity target = getStoredSummonTarget(summon);
            if(target == null) return State.WANDER.ordinal();
            else return State.FIGHT.ordinal();
        }else if(state == State.WANDER.ordinal()) {
            Entity target = getStoredSummonTarget(summon);
            if(target != null) return State.FIGHT.ordinal();
            if(summon.getDeltaMovement().length() < 0.05) return State.IDLE.ordinal();
            return State.WANDER.ordinal();
        }else if(state == State.FIGHT.ordinal()) {
            Entity target = getStoredSummonTarget(summon);
            if(target != null) return State.CHASE.ordinal();
            else return State.IDLE.ordinal();
        }else if(state == State.CHASE.ordinal()) {
            Entity target = getStoredSummonTarget(summon);
            if(target == null) return State.FIGHT.ordinal();
            if(target.getBoundingBox().getCenter().subtract(summon.position()).length() < CHASE_SPEED.get() * 1.5) return State.DASH.ordinal();
            else return State.CHASE.ordinal();
        }else if(state == State.DASH.ordinal()) {
            if(summon.getDeltaMovement().length() < 0.5) return State.DASH_DONE.ordinal();
            return State.DASH.ordinal();
        }else if(state == State.DASH_DONE.ordinal()) {
            if(summon.getDeltaMovement().length() < 0.5) return State.FIGHT.ordinal();
            return State.DASH_DONE.ordinal();
        }
        return State.IDLE.ordinal();
    }

    public static void changeState(StaticSummon summon, int from, int to) {
        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA).copy();
        customData.putInt("state", to);
        if(to == State.IDLE.ordinal()) {
            summon.setDeltaMovement(Vec3.ZERO);
        }else if(to == State.FIGHT.ordinal()) {
            summon.setDeltaMovement(Vec3.ZERO);
        }else if(to == State.WANDER.ordinal()) {
            if(summon.getOwner() == null) return;
            Vec3 targetPos = MathUtil.getRandomPosInRadius(summon.getOwner().position(), 12);
            double dist = targetPos.subtract(summon.position()).length();
            int wanderTime = (int) (dist / WANDER_SPEED.get());
            customData.putInt("wanderTime", wanderTime);
            Vec3 wanderDir = targetPos.subtract(summon.position()).normalize();
            summon.setDeltaMovement(wanderDir.scale(WANDER_SPEED.get()));
        }else if(to == State.DASH.ordinal()) {
            Entity target = getStoredSummonTarget(summon);
            if(target != null) {
                Vec3 targetPos = target.getBoundingBox().getCenter();
                int segmentCount = 1;
                if(summon.getOwner() instanceof Player player) {
                    for(PlayerSummon.SummonSlot slot : player.getData(ModAttachments.PLAYER_SUMMON).getMinionSlots()) {
                        if(slot.getId().equals(StaticSummonBehaviors.STARDUST_DRAGON_STAFF)) {
                            segmentCount = Math.max(segmentCount, slot.getSummons().size());
                            break;
                        }
                    }
                }
                double passDistance = (segmentCount - 1) * SEGMENT_SPACING + target.getBbWidth() + 1.0D;
                int dashTime = Math.max(1, (int)Math.ceil((passDistance) / DASH_SPEED.get()));
                customData.putInt("dashTime", dashTime);
                Vec3 dashDir = targetPos.subtract(summon.position()).normalize();
                summon.setDeltaMovement(dashDir.scale(DASH_SPEED.get()));
            }
        }else if(to ==State.DASH_DONE.ordinal()) {
            Vec3 targetPos = MathUtil.getRandomPosInRadius(summon.position(), 4);
            double dist = targetPos.subtract(summon.position()).length();
            int dashDoneTime = (int) (dist / DASH_SPEED.get());
            customData.putInt("dashDoneTime", dashDoneTime);
            Vec3 dashDoneDir = targetPos.subtract(summon.position()).normalize();
            summon.setDeltaMovement(dashDoneDir.scale(DASH_SPEED.get()));
        }
        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
    }

    public static void tickSegment(StaticSummon summon, CompoundTag customData) {
        if(!customData.contains("uuid") || !(summon.level() instanceof ServerLevel serverLevel)) {
            summon.discard();
            return;
        }

        Entity previous = serverLevel.getEntity(customData.getUUID("uuid"));
        if(previous == null || !previous.isAlive()) {
            summon.discard();
            return;
        }

        tickSegmentTowards(summon, previous.position(), previous.getLookAngle());
    }

    private static void tickSegmentTowards(StaticSummon summon, Vec3 previousPos, Vec3 fallbackDir) {
        Vec3 toPrevious = previousPos.subtract(summon.position());
        Vec3 dir;
        if(toPrevious.lengthSqr() > 1.0E-6D) {
            dir = toPrevious.normalize();
        }else {
            dir = fallbackDir.normalize();
            if(dir.lengthSqr() < 1.0E-6D) dir = new Vec3(0.0D, 0.0D, 1.0D);
        }

        Vec3 targetPos = previousPos.subtract(dir.scale(SEGMENT_SPACING));
        summon.setDeltaMovement(Vec3.ZERO);
        summon.setPos(targetPos);

        Vec3 lookDir = previousPos.subtract(summon.position());
        if(lookDir.lengthSqr() > 1.0E-6D) {
            float[] xyRot = MathUtil.computeXYRot(lookDir.normalize().toVector3f());
            summon.setXRot(Mth.rotLerp(ROTATION_LERP, summon.getXRot(), xyRot[0]));
            summon.setYRot(Mth.rotLerp(ROTATION_LERP, summon.getYRot(), xyRot[1]));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(stack.is(ModItems.STARDUST_DRAGON_STAFF.get()) && level instanceof ServerLevel serverLevel) {
            PlayerSummon summonData = player.getData(ModAttachments.PLAYER_SUMMON);
            List<PlayerSummon.SummonSlot> slots = cleanSummonSlots(serverLevel, summonData.getMinionSlots(), player.position());
            List<PlayerSummon.SummonSlot> stardustDragonSlots = new ArrayList<>();
            int maxSummonCount = Math.max(1, getMaxSummonCount(player));
            slots.removeIf(slot -> {
                boolean stardustDragon = slot.getId().equals(StaticSummonBehaviors.STARDUST_DRAGON_STAFF);
                if(stardustDragon) stardustDragonSlots.add(slot);
                return stardustDragon;
            });
            if(stardustDragonSlots.isEmpty()) {
                summonData.setMinionSlots(slots);
                PlayerSummonSync.setAndSync(player, summonData);
            }
            if(stardustDragonSlots.isEmpty()) {
                StaticSummon headSummon = createSummon(level, player, hand);
                StaticSummon bodySummonA = createSummon(level, player, hand);
                StaticSummon bodySummonB = createSummon(level, player, hand);
                StaticSummon tailSummon = createSummon(level, player, hand);

                headSummon.getEntityData().set(StaticSummon.SCALE_X, 1.75f);
                headSummon.getEntityData().set(StaticSummon.SCALE_Y, 1.75f);
                headSummon.getEntityData().set(StaticSummon.SCALE_Z, 1.75f);
                CompoundTag headCustomData = new CompoundTag();
                headCustomData.putBoolean("head", true);
                headCustomData.putInt("state", State.IDLE.ordinal());
                headSummon.getEntityData().set(StaticSummon.CUSTOM_DATA, headCustomData);

                CompoundTag bodyACustomData = new CompoundTag();
                bodyACustomData.putBoolean("bodyA", true);
                bodyACustomData.putUUID("uuid", headSummon.getUUID());
                bodySummonA.getEntityData().set(StaticSummon.CUSTOM_DATA, bodyACustomData);

                CompoundTag bodyBCustomData = new CompoundTag();
                bodyBCustomData.putBoolean("bodyB", true);
                bodyBCustomData.putUUID("uuid", bodySummonA.getUUID());
                bodySummonB.getEntityData().set(StaticSummon.CUSTOM_DATA, bodyBCustomData);

                CompoundTag tailCustomData = new CompoundTag();
                tailCustomData.putBoolean("tail", true);
                tailCustomData.putUUID("uuid", bodySummonB.getUUID());
                tailSummon.getEntityData().set(StaticSummon.CUSTOM_DATA, tailCustomData);

                MinionWeapon.addFreshSummon(player, StaticSummonBehaviors.STARDUST_DRAGON_STAFF, List.of(headSummon, bodySummonA, bodySummonB, tailSummon));
            }else {
                PlayerSummon.SummonSlot stardustDragonTailSlot = stardustDragonSlots.getLast();
                UUID tailUUID = stardustDragonTailSlot.getSummons().getLast();
                if(!(serverLevel.getEntity(tailUUID) instanceof StaticSummon tailSummon) || !tailSummon.isAlive()) {
                    slots.addAll(stardustDragonSlots);
                    summonData.setMinionSlots(slots);
                    PlayerSummonSync.setAndSync(player, summonData);
                    SoundUtil.playClientSound(player, ModSounds.SUMMON.get());
                    return InteractionResultHolder.consume(stack);
                }

                while(slots.size() + stardustDragonSlots.size() + 1 > maxSummonCount) {
                    PlayerSummon.SummonSlot removedSlot;
                    if(!slots.isEmpty()) {
                        removedSlot = slots.removeFirst();
                    }else if(stardustDragonSlots.size() > 1) {
                        removedSlot = stardustDragonSlots.removeFirst();
                    }else {
                        slots.addAll(stardustDragonSlots);
                        summonData.setMinionSlots(slots);
                        PlayerSummonSync.setAndSync(player, summonData);
                        player.displayClientMessage(
                            Component.translatable("message.lzxnoneterraria.current_minions", slots.size(), maxSummonCount)
                                .withStyle(net.minecraft.ChatFormatting.RED),
                            true
                        );
                        SoundUtil.playClientSound(player, ModSounds.SUMMON.get());
                        return InteractionResultHolder.consume(stack);
                    }

                    for(UUID uuid : removedSlot.getSummons()) {
                        Entity entity = serverLevel.getEntity(uuid);
                        if(entity != null) entity.discard();
                    }
                }

                CompoundTag tailCustomData = tailSummon.getEntityData().get(StaticSummon.CUSTOM_DATA).copy();
                UUID previousUUID = null;
                if(tailCustomData.contains("uuid")) {
                    UUID tailPreviousUUID = tailCustomData.getUUID("uuid");
                    Entity tailPrevious = serverLevel.getEntity(tailPreviousUUID);
                    if(tailPrevious instanceof StaticSummon previousSummon && previousSummon.isAlive() && previousSummon.getEntityData().get(StaticSummon.BEHAVIOR).equals(StaticSummonBehaviors.STARDUST_DRAGON_STAFF)) {
                        previousUUID = tailPreviousUUID;
                    }
                }
                if(previousUUID == null) {
                    for(PlayerSummon.SummonSlot slot : stardustDragonSlots) {
                        if(slot == stardustDragonTailSlot) continue;
                        if(!slot.getSummons().isEmpty()) {
                            previousUUID = slot.getSummons().getLast();
                            break;
                        }
                    }
                }
                if(previousUUID == null && stardustDragonTailSlot.getSummons().size() >= 2) {
                    previousUUID = stardustDragonTailSlot.getSummons().get(stardustDragonTailSlot.getSummons().size() - 2);
                }
                if(previousUUID == null) {
                    slots.addAll(stardustDragonSlots);
                    summonData.setMinionSlots(slots);
                    PlayerSummonSync.setAndSync(player, summonData);
                    SoundUtil.playClientSound(player, ModSounds.SUMMON.get());
                    return InteractionResultHolder.consume(stack);
                }

                StaticSummon bodySummonA = createSummon(level, player, hand);
                StaticSummon bodySummonB = createSummon(level, player, hand);

                CompoundTag bodyACustomData = new CompoundTag();
                bodyACustomData.putBoolean("bodyA", true);
                bodyACustomData.putUUID("uuid", previousUUID);
                bodySummonA.getEntityData().set(StaticSummon.CUSTOM_DATA, bodyACustomData);

                CompoundTag bodyBCustomData = new CompoundTag();
                bodyBCustomData.putBoolean("bodyB", true);
                bodyBCustomData.putUUID("uuid", bodySummonA.getUUID());
                bodySummonB.getEntityData().set(StaticSummon.CUSTOM_DATA, bodyBCustomData);

                tailCustomData.putBoolean("tail", true);
                tailCustomData.putUUID("uuid", bodySummonB.getUUID());
                tailSummon.getEntityData().set(StaticSummon.CUSTOM_DATA, tailCustomData);

                stardustDragonSlots.addFirst(new PlayerSummon.SummonSlot(StaticSummonBehaviors.STARDUST_DRAGON_STAFF, List.of(bodySummonA.getUUID(), bodySummonB.getUUID())));
                slots.addAll(stardustDragonSlots);
                summonData.setMinionSlots(slots);
                PlayerSummonSync.setAndSync(player, summonData);
                player.displayClientMessage(
                    Component.translatable("message.lzxnoneterraria.current_minions", slots.size(), maxSummonCount)
                        .withStyle(slots.size() < maxSummonCount ? net.minecraft.ChatFormatting.GREEN : net.minecraft.ChatFormatting.RED),
                    true
                );
                level.addFreshEntity(bodySummonA);
                level.addFreshEntity(bodySummonB);
            }
        }
        SoundUtil.playClientSound(player, ModSounds.SUMMON.get());
        return InteractionResultHolder.consume(stack);
    }

    public static StaticSummon createSummon(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), level);
        summon.setOwner(player);
        summon.getEntityData().set(StaticSummon.STACK_SOURCE, stack.copy());
        summon.setPos(player.position());
        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.STARDUST_DRAGON_STAFF);
        summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
        summon.getEntityData().set(StaticSummon.LIFETIME, StaticSummon.INFINITE_LIFETIME);
        summon.getEntityData().set(StaticSummon.GLOW, true);
        summon.setNoGravity(true);
        summon.noPhysics = true;
        return summon;
    }
}
