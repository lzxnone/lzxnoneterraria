package com.lzxnone.terraria.item.weapon.magic;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.weapon.MagicWeapon;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.ParticleUtil;
import com.lzxnone.terraria.utils.SearchUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class ArcSurge extends MagicWeapon {
    // 基础属性配置
    public static final ConfigFloat DAMAGE = new ConfigFloat("weapon.arc_surge.damage", "arc_surge_damage", 20.0F, 0.0F, 8388600.0F);
    public static final ConfigDouble MANA_CONSUME = new ConfigDouble("weapon.arc_surge.mana_consume", "arc_surge_mana_consume", 18.0D, 0.0D, 10000.0D);

    // 索敌与电弧生成配置参数
    public static final ConfigInt MAX_EXTRA_ARCS = new ConfigInt("weapon.arc_surge.max_extra_arcs", "arc_surge_max_extra_arcs", 2, 0, 100);
    public static final ConfigDouble SEARCH_RANGE = new ConfigDouble("weapon.arc_surge.search_range", "arc_surge_search_range", 32.0D, 0.0D, 256.0D);
    public static final ConfigDouble MAX_ANGLE = new ConfigDouble("weapon.arc_surge.max_angle", "arc_surge_max_angle", 60.0D, 0.0D, 180.0D);

    // 分形闪电形状与生命周期参数
    public static final int LIGHTNING_DEPTH = 5;             // 分形细分递归深度
    public static final float LIGHTNING_JITTER = 0.18F;       // 闪电节点随机扰动因子
    public static final int LIGHTNING_LIFETIME = 8;          // 闪电实体存活 tick 数
    public static final int SUB_BRANCH_COUNT = 2;            // 次级分叉数量
    public static final float SUB_BRANCH_SPREAD = 0.35F;     // 次级分叉散布角
    public static final float SUB_BRANCH_LENGTH_SCALE = 0.5F;// 次级分叉长度比例
    public static final float LIGHTNING_RADIUS = 0.08F;      // 闪电半径
    public static final double COLLISION_INFLATE = 0.35D;    // 碰撞检测膨胀大小

    public static final DustParticleOptions DUST_PARTICLE = new DustParticleOptions(
        0.075f, 0.2f, 25, true, new Vector3f[]{
            new Vector3f(1.0F, 0.15F, 0.15F),
            new Vector3f(0.9F, 0.05F, 0.1F)
        }
    );

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DAMAGE, MANA_CONSUME, MAX_EXTRA_ARCS, SEARCH_RANGE, MAX_ANGLE);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "arc_surge",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/arc_surge.png"),
        Component.translatable("item.lzxnoneterraria.arc_surge"),
        CONFIG_DATA
    );

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            checkBeforeTick(summon);
            if(summon.level().isClientSide()) return;

            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            List<List<Vec3>> branches = getAllLightningBranches(customData, summon.level(), summon);
            if(branches.isEmpty()) return;

            // 收集本次判定命中的实体，防止同一道电弧在单 tick 内对同一实体重复造成多次伤害
            Set<Entity> hitEntities = new HashSet<>();
            Predicate<Entity> targetFilter = FilterUtil.createTargetFilter(summon, summon.getOwner());
            ServerLevel serverLevel = (ServerLevel) summon.level();

            for(List<Vec3> branch : branches) {
                int n = branch.size();
                for(int i = 0; i < n - 1; i++) {
                    Vec3 p0 = branch.get(i);
                    Vec3 p1 = branch.get(i + 1);

                    // 每条线段的中点都有 0.2 的概率生成一个红色的 dust 粒子
                    if(summon.getRandom().nextFloat() < 0.02F) {
                        Vec3 mid = p0.add(p1).scale(0.5D);
                        ParticleUtil.addParticles(
                            serverLevel,
                            DUST_PARTICLE,
                            mid,
                            new Vec3(0.05D, 0.05D, 0.05D),
                            0.02D,
                            1
                        );
                    }

                    AABB segmentBox = new AABB(p0, p1).inflate(COLLISION_INFLATE);
                    List<Entity> candidates = summon.level().getEntitiesOfClass(Entity.class, segmentBox, targetFilter);

                    for(Entity target : candidates) {
                        if(hitEntities.add(target)) {
                            AABB targetBox = target.getBoundingBox().inflate(COLLISION_INFLATE);
                            if(targetBox.clip(p0, p1).isPresent() || targetBox.contains(p0) || targetBox.contains(p1)) {
                                DamageUtil.magicAttack(
                                    summon,
                                    target,
                                    summon.getEntityData().get(StaticSummon.STACK_SOURCE),
                                    DAMAGE.get(),
                                    0.2F,
                                    10
                                );
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onDied(StaticSummon summon) {
            if(!summon.level().isClientSide()) summon.discard();
        }

        @Override
        public AABB getBoundingBoxForCulling(StaticSummon summon) {
            double m = SEARCH_RANGE.get() + 8.0D;
            Vec3 p = summon.position();
            return new AABB(p.x - m, p.y - m, p.z - m, p.x + m, p.y + m, p.z + m);
        }
    };

    public ArcSurge() {
        super(Tiers.DIAMOND, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public int getUseTime(ItemStack weaponStack, LivingEntity entity) {
        return 10;
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
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        if(level.isClientSide()) return;

        player.playNotifySound(ModSounds.BEAM.get(), SoundSource.PLAYERS, 1.5F, 1.2F);

        double searchRange = SEARCH_RANGE.get();
        double maxAngle = MAX_ANGLE.get();
        int maxExtraArcs = MAX_EXTRA_ARCS.get();

        // 1. 计算主电弧的起点（玩家碰撞箱中心）与终点（准星位置）
        Vec3 startPos = player.getBoundingBox().getCenter();
        Vec3 mainEndPos = MathUtil.getCrosshairPos(player, level, searchRange, true, true);

        // 2. 搜索玩家指定范围内的敌人
        AABB searchBox = player.getBoundingBox().inflate(searchRange);
        List<Entity> enemies = SearchUtil.searchEnemies(player, player, searchBox);

        Vec3 eyePos = player.getEyePosition();
        Vec3 lookAngle = player.getLookAngle().normalize();

        // 3. 筛选可视、夹角 <= maxAngle 的有效敌人
        List<Entity> validTargets = new ArrayList<>();
        for(Entity enemy : enemies) {
            if(!enemy.isAlive()) continue;
            if(!player.hasLineOfSight(enemy)) continue;
            if(enemy.distanceToSqr(player) > searchRange * searchRange) continue;

            Vec3 toEnemy = enemy.getBoundingBox().getCenter().subtract(eyePos);
            if(toEnemy.lengthSqr() < 1.0E-4D) continue;
            double dot = lookAngle.dot(toEnemy.normalize());
            double angleDeg = Math.toDegrees(Math.acos(Mth.clamp(dot, -1.0D, 1.0D)));
            if(angleDeg <= maxAngle) {
                validTargets.add(enemy);
            }
        }

        // 4. 按距玩家距离升序排序
        validTargets.sort(Comparator.comparingDouble(e -> e.distanceToSqr(player)));

        // 5. 最多额外生成 maxExtraArcs 道电弧
        int extraCount = Math.min(maxExtraArcs, validTargets.size());

        // 6. 构建并保存同步数据到 customData
        CompoundTag customData = new CompoundTag();
        long seed = player.getRandom().nextLong();
        customData.putLong("seed", seed);

        customData.putDouble("startX", startPos.x);
        customData.putDouble("startY", startPos.y);
        customData.putDouble("startZ", startPos.z);
        customData.putDouble("endX", mainEndPos.x);
        customData.putDouble("endY", mainEndPos.y);
        customData.putDouble("endZ", mainEndPos.z);

        customData.putInt("extraCount", extraCount);
        for(int i = 0; i < extraCount; i++) {
            Entity target = validTargets.get(i);
            Vec3 targetCenter = target.getBoundingBox().getCenter();
            customData.putDouble("extraEndX_" + i, targetCenter.x);
            customData.putDouble("extraEndY_" + i, targetCenter.y);
            customData.putDouble("extraEndZ_" + i, targetCenter.z);
            customData.putLong("extraSeed_" + i, player.getRandom().nextLong());
        }

        // 7. 生成 StaticSummon 实体
        StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), level);
        summon.setOwner(player);
        summon.setPos(startPos);
        summon.getEntityData().set(StaticSummon.STACK_SOURCE, stack.copy());
        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.ARC_SURGE_LIGHTNING);
        summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
        summon.getEntityData().set(StaticSummon.LIFETIME, LIGHTNING_LIFETIME);
        summon.getEntityData().set(StaticSummon.GLOW, true);
        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
        summon.setNoGravity(true);
        summon.noPhysics = true;

        level.addFreshEntity(summon);
    }

    // ================= 分形闪电构建与算法实现 =================

    /**
     * 根据 customData 中的确定性数据构建全部闪电分支（主电弧及其次级分叉，所有额外电弧及其次级分叉）
     * 并结合方块与液体射线检测进行打断裁剪
     */
    public static List<List<Vec3>> getAllLightningBranches(CompoundTag customData, @Nullable Level level, @Nullable Entity summon) {
        if(!customData.contains("startX") || !customData.contains("endX") || !customData.contains("seed")) {
            return List.of();
        }

        List<List<Vec3>> allBranches = new ArrayList<>();

        Vec3 start = new Vec3(customData.getDouble("startX"), customData.getDouble("startY"), customData.getDouble("startZ"));
        Vec3 mainEnd = new Vec3(customData.getDouble("endX"), customData.getDouble("endY"), customData.getDouble("endZ"));
        long seed = customData.getLong("seed");

        // 1. 主电弧
        Random mainRand = new Random(seed);
        List<Vec3> rawMainBranch = generateBranch(start, mainEnd, mainRand, LIGHTNING_DEPTH, LIGHTNING_JITTER);
        List<Vec3> clippedMain = clipBranchWithBlocksAndFluids(rawMainBranch, level, summon);
        if(clippedMain.size() >= 2) {
            allBranches.add(clippedMain);
            // 2. 主电弧次级分叉（仅从裁剪后有效的主路径节点衍生）
            addSubBranches(allBranches, clippedMain, start, mainEnd, seed, 0, level, summon);
        }

        // 3. 额外电弧
        int extraCount = customData.getInt("extraCount");
        for(int i = 0; i < extraCount; i++) {
            if(!customData.contains("extraEndX_" + i)) continue;
            Vec3 extraEnd = new Vec3(
                customData.getDouble("extraEndX_" + i),
                customData.getDouble("extraEndY_" + i),
                customData.getDouble("extraEndZ_" + i)
            );
            long extraSeed = customData.getLong("extraSeed_" + i);
            Random extraRand = new Random(extraSeed);
            List<Vec3> rawExtraBranch = generateBranch(start, extraEnd, extraRand, LIGHTNING_DEPTH, LIGHTNING_JITTER);
            List<Vec3> clippedExtra = clipBranchWithBlocksAndFluids(rawExtraBranch, level, summon);
            if(clippedExtra.size() >= 2) {
                allBranches.add(clippedExtra);
                // 4. 额外电弧次级分叉
                addSubBranches(allBranches, clippedExtra, start, extraEnd, extraSeed, i + 1, level, summon);
            }
        }

        return allBranches;
    }

    public static List<List<Vec3>> getAllLightningBranches(CompoundTag customData) {
        return getAllLightningBranches(customData, null, null);
    }

    /**
     * 逐线段检测方块与液体碰撞：若碰撞则将该线段终点截断至碰撞点，并丢弃后续所有线段
     */
    public static List<Vec3> clipBranchWithBlocksAndFluids(List<Vec3> rawBranch, @Nullable Level level, @Nullable Entity summon) {
        if(rawBranch.size() < 2 || level == null) return rawBranch;

        List<Vec3> clipped = new ArrayList<>();
        clipped.add(rawBranch.getFirst());

        for(int i = 0; i < rawBranch.size() - 1; i++) {
            Vec3 p0 = rawBranch.get(i);
            Vec3 p1 = rawBranch.get(i + 1);

            BlockHitResult hit = level.clip(new ClipContext(
                p0,
                p1,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.ANY,
                summon
            ));

            if(hit.getType() != HitResult.Type.MISS) {
                clipped.add(hit.getLocation());
                break; // 击中方块或液体，立即截断并停止后续线段
            }else {
                clipped.add(p1);
            }
        }
        return clipped;
    }

    private static void addSubBranches(
        List<List<Vec3>> allBranches,
        List<Vec3> parentBranch,
        Vec3 start,
        Vec3 end,
        long baseSeed,
        int branchIndex,
        @Nullable Level level,
        @Nullable Entity summon
    ) {
        if(parentBranch.size() < 3) return;
        Vec3 dir = end.subtract(start).normalize();
        double totalLength = start.distanceTo(end);

        for(int s = 0; s < SUB_BRANCH_COUNT; s++) {
            Random subRand = new Random(baseSeed + 1000L * (branchIndex + 1) + s);
            int nodeIdx = 1 + subRand.nextInt(parentBranch.size() - 2);
            Vec3 subStart = parentBranch.get(nodeIdx);

            Vec3 subDir = spreadDir(dir, subRand, SUB_BRANCH_SPREAD);
            double subLen = totalLength * SUB_BRANCH_LENGTH_SCALE * (0.6D + subRand.nextDouble() * 0.4D);
            Vec3 subEnd = subStart.add(subDir.scale(subLen));

            List<Vec3> rawSubBranch = generateBranch(subStart, subEnd, subRand, Math.max(1, LIGHTNING_DEPTH - 1), LIGHTNING_JITTER);
            List<Vec3> clippedSub = clipBranchWithBlocksAndFluids(rawSubBranch, level, summon);
            if(clippedSub.size() >= 2) {
                allBranches.add(clippedSub);
            }
        }
    }

    public static List<Vec3> generateBranch(Vec3 start, Vec3 end, Random rand, int depth, float jitter) {
        List<Vec3> branch = new ArrayList<>();
        branch.add(start);
        subdivide(start, end, depth, jitter, rand, branch);
        branch.add(end);
        return branch;
    }

    private static void subdivide(Vec3 start, Vec3 end, int depth, float jitter, Random rand, List<Vec3> out) {
        if(depth <= 0) return;
        Vec3 mid = start.add(end).scale(0.5D);
        Vec3 seg = end.subtract(start);
        Vector3f[] dirs = MathUtil.computeCoordinateSystem(seg.toVector3f(), 0);
        mid = getSurfaceRandomPosInRadius(mid, MathUtil.toVec3(dirs[2]), MathUtil.toVec3(dirs[1]), seg.length() * jitter, rand);
        subdivide(start, mid, depth - 1, jitter, rand, out);
        out.add(mid);
        subdivide(mid, end, depth - 1, jitter, rand, out);
    }

    public static Vec3 getSurfaceRandomPosInRadius(Vec3 pos, Vec3 axisX, Vec3 axisY, double radius, Random random) {
        double s = Math.sqrt(random.nextDouble());
        axisX = axisX.normalize().scale(radius).scale(s);
        axisY = axisY.normalize().scale(radius).scale(s);
        double rad = Math.PI * 2 * random.nextDouble();
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        return new Vec3(
            pos.x + cos * axisX.x + sin * axisY.x,
            pos.y + cos * axisX.y + sin * axisY.y,
            pos.z + cos * axisX.z + sin * axisY.z
        );
    }

    public static Vec3 spreadDir(Vec3 dir, Random rand, float maxAngle) {
        float angle = (rand.nextFloat() - 0.5F) * 2.0F * maxAngle;
        Vec3 axisV = new Vec3(rand.nextDouble() - 0.5, rand.nextDouble() - 0.5, rand.nextDouble() - 0.5).normalize();
        if(axisV.lengthSqr() < 0.01D) axisV = new Vec3(1, 0, 0);
        axisV = axisV.cross(dir).normalize();
        if(axisV.lengthSqr() < 0.01D) axisV = new Vec3(0, 0, 1);
        Vector3f d = dir.toVector3f().normalize();
        new Quaternionf().fromAxisAngleRad(axisV.toVector3f().normalize(), angle).transform(d);
        return new Vec3(d.x(), d.y(), d.z());
    }
}
