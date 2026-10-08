package com.lzxnone.terraria.item.weapon.summon.whip;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.attachment.TargetMarks;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.weapon.summon.minion.MinionWeapon;
import com.lzxnone.terraria.item.weapon.summon.sentry.SentryWeapon;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.particle.StarlightParticleOptions;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.ParticleUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.joml.Vector3f;

public class Kaleidoscope extends Whip {
    public static final StarlightParticleOptions STARLIGHT_PARTICLE = new StarlightParticleOptions(
        0.8f,
        0.5f,
        25,
        ModParticles.COLORFUL_PARTICLE.colors
    );
    public static final DustParticleOptions TIP_PARTICLE = new DustParticleOptions(
        0.5f,
        0.5f,
        40,
        true,
        ModParticles.COLORFUL_PARTICLE.colors
    );
    private static final ConfigDouble RANGE = new ConfigDouble("weapon.kaleidoscope.range", "kaleidoscope_range", 32.0D, 1.0D, 256.0D);
    private static final ConfigFloat DAMAGE = new ConfigFloat("weapon.kaleidoscope.damage", "kaleidoscope_damage", 15.0F, 0.0F, 8388600.0F);
    private static final ConfigInt ROTATE = new ConfigInt("weapon.kaleidoscope.rotate", "kaleidoscope_rotate", 60, 0, 360);
    private static final ConfigInt MARK_DURATION = new ConfigInt("weapon.kaleidoscope.mark_duration", "kaleidoscope_mark_duration", 80, 1, 72000);
    private static final ConfigFloat TAG_DAMAGE = new ConfigFloat("weapon.kaleidoscope.tag_damage", "kaleidoscope_tag_damage", 2.0F, 0.0F, 8388600.0F);
    private static final ConfigInt TAG_CRIT = new ConfigInt("weapon.kaleidoscope.tag_crit", "kaleidoscope_tag_crit", 10, 0, 100);
    private static final ConfigFloat DAMAGE_FALLOFF = new ConfigFloat("weapon.kaleidoscope.damage_falloff", "kaleidoscope_damage_falloff", 0.8F, 0.0F, 1.0F);

    public Kaleidoscope() {
        super(Tiers.DIAMOND, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    protected double getRange() {
        return RANGE.get();
    }

    @Override
    protected String getTailRes(StaticSummon summon) {
        return "lzxnoneterraria:textures/vfx/kaleidoscope_projectile_tail.png";
    }

    @Override
    protected String getBodyRes(StaticSummon summon) {
        return "lzxnoneterraria:textures/vfx/kaleidoscope_projectile_body.png";
    }

    @Override
    protected String getHeadRes(StaticSummon summon) {
        return "lzxnoneterraria:textures/vfx/kaleidoscope_projectile_head.png";
    }

    @Override
    protected float getTailRatio() {
        return 0.06F;
    }

    @Override
    protected float getHeadRatio() {
        return 0.06F;
    }

    @Override
    protected float getBodyUnitRatio() {
        return 0.18F;
    }

    @Override
    protected float getDamage() {
        return DAMAGE.get();
    }

    @Override
    protected float getDamageFalloff() {
        return DAMAGE_FALLOFF.get();
    }

    @Override
    public float getTagDamage() {
        return TAG_DAMAGE.get();
    }

    @Override
    public int getTagCrit() {
        return TAG_CRIT.get();
    }

    @Override
    protected int getLifetime() {
        return 12;
    }

    @Override
    protected int getRotateAngle() {
        return ROTATE.get();
    }

    @Override
    protected String getBehavior() {
        return StaticSummonBehaviors.KALEIDOSCOPE;
    }

    @Override
    protected int getUseTime() {
        return 30;
    }

    @Override
    protected int getInvulnerableTime() {
        return 10;
    }

    @Override
    protected void onHitTarget(StaticSummon summon, Entity target) {
        if(summon.level() instanceof ServerLevel serverLevel) {
            LivingEntity owner = summon.getOwner() instanceof LivingEntity living ? living : null;
            applyMark(
                target,
                owner,
                TargetMarks.KALEIDOSCOPE,
                new TargetMarks.Mark(
                    serverLevel.getGameTime(),
                    MARK_DURATION.get(),
                    0,
                    owner == null ? null : owner.getUUID(),
                    summon.getEntityData().get(StaticSummon.STACK_SOURCE),
                    getTagDamage(),
                    getTagCrit()
                )
            );

            ParticleUtil.addParticles(
                serverLevel, ModParticles.COLORFUL_PARTICLE,
                target.getBoundingBox().getCenter(), new Vec3(0, 0, 0),
                0.15, 8
            );
        }
    }

    @Override
    protected void onTick(StaticSummon summon) {
        if(summon.level() instanceof ServerLevel serverLevel) {
            Vec3 tip = getTipPosition(summon);
            ParticleUtil.addParticles(
                serverLevel, TIP_PARTICLE,
                tip, Vec3.ZERO,
                0.02, 1
            );
        }
    }

    @Override
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        super.shoot(level, player, hand, stack);
        if(level instanceof ServerLevel serverLevel) {
            ParticleUtil.addParticles(
                serverLevel, ModParticles.COLORFUL_PARTICLE,
                player.getBoundingBox().getCenter(), Vec3.ZERO,
                0.2, 8
            );
        }
    }

    public static void markEvent(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        if(!(target.level() instanceof ServerLevel serverLevel)) return;

        TargetMarks marks = target.getData(ModAttachments.TARGET_MARKS);
        TargetMarks.Mark mark = marks.getMarks().get(TargetMarks.KALEIDOSCOPE);
        if(mark == null) return;

        if(mark.getDuration() > 0 && serverLevel.getGameTime() - mark.getStartTime() >= mark.getDuration()) {
            TargetMarks copiedMarks = marks.copy();
            copiedMarks.getMarks().remove(TargetMarks.KALEIDOSCOPE);
            target.setData(ModAttachments.TARGET_MARKS, copiedMarks);
            return;
        }

        Entity damageEntity = event.getSource().getDirectEntity();
        if(!(damageEntity instanceof StaticSummon summon)) return;

        ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
        if(sourceStack.isEmpty() || !(sourceStack.getItem() instanceof MinionWeapon || sourceStack.getItem() instanceof SentryWeapon)) return;

        ParticleUtil.addParticles(
            serverLevel, STARLIGHT_PARTICLE,
            target.getBoundingBox().getCenter(), Vec3.ZERO,
            0.2, 6
        );
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                RANGE,
                DAMAGE,
                ROTATE,
                MARK_DURATION,
                TAG_DAMAGE,
                TAG_CRIT,
                DAMAGE_FALLOFF
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "kaleidoscope",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/kaleidoscope.png"),
        Component.translatable("item.lzxnoneterraria.kaleidoscope"),
        CONFIG_DATA
    );
}
