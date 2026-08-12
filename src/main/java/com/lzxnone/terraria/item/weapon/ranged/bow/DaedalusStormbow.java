package com.lzxnone.terraria.item.weapon.ranged.bow;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DaedalusStormbow extends Bow {
    public static final String ID = "daedalus_stormbow";
    public static final int USE_TIME = 19;
    public static final int ARROW_COUNT = 3;
    public static final double RANGE = 64.0D;
    public static final double SPAWN_HEIGHT = 20.0D;
    public static final double SPAWN_OFFSET = 6.0D;
    public static final double HIT_OFFSET = 2D;

    public static final ConfigFloat DAMAGE = new ConfigFloat("weapon.daedalus_stormbow.damage", "daedalus_stormbow_damage", 4.0F, 0.0F, 8388600.0F);
    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DAMAGE);
        }
    };
    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
            ID,
            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/" + ID + ".png"),
            Component.translatable("item.lzxnoneterraria." + ID),
            CONFIG_DATA
    );

    public DaedalusStormbow() {
        super(Tiers.IRON, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public int getUseTime(ItemStack weaponStack, LivingEntity entity) {
        return USE_TIME;
    }

    @Override
    protected float getDamage(ItemStack weaponStack, LivingEntity entity, ItemStack ammoStack) {
        return DAMAGE.get();
    }

    @Override
    protected float getKnockbackScale(ItemStack weaponStack, LivingEntity entity, ItemStack ammoStack) {
        return 1.0f;
    }

    @Override
    protected int getInvulnerableTime(ItemStack weaponStack, LivingEntity entity, ItemStack ammoStack) {
        return 10;
    }

    @Override
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack weaponStack) {
        SoundUtil.playClientSound(player, ModSounds.BOW_SHOOT.get());
        if(level.isClientSide()) return;

        for(int i = 0; i < ARROW_COUNT; i++) {
            StaticSummon summon = createArrowSummon(level, player, hand, weaponStack);
            Vec3 targetPos = MathUtil.getCrosshairPos(player, level, RANGE);
            Vec3 spawnPos = targetPos.add(
                    (level.random.nextDouble() * 2.0D - 1.0D) * SPAWN_OFFSET,
                    SPAWN_HEIGHT + i * 1.5D,
                    (level.random.nextDouble() * 2.0D - 1.0D) * SPAWN_OFFSET
            );
            Vec3 hitPos = targetPos.add(
                    (level.random.nextDouble() * 2.0D - 1.0D) * HIT_OFFSET,
                    0.0D,
                    (level.random.nextDouble() * 2.0D - 1.0D) * HIT_OFFSET
            );
            Vec3 direction = hitPos.subtract(spawnPos).normalize();

            summon.setPos(spawnPos);
            float[] xyRot = MathUtil.computeXYRot(direction.toVector3f());
            summon.setXRot(xyRot[0]);
            summon.setYRot(xyRot[1]);
            summon.xRotO = summon.getXRot();
            summon.yRotO = summon.getYRot();
            level.addFreshEntity(summon);
        }
    }
}
