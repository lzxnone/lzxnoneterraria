package com.lzxnone.terraria.item.weapon.ranged.gun;

import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.AmmoUtil;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class ClockworkAssaultRifle extends Gun {
    public static final String ID = "clockwork_assault_rifle";
    public static final Vector3f OFFSET = new Vector3f(-0.3f, -0.15f, 1.5f);
    public static final float DAMAGE_DEFAULT = 2.4f;
    public static final int PROJECTILE_COUNT = 3;
    public static final double PROJECTILE_OFFSET = -1.0;
    public static final IConfigData CONFIG_DATA = createConfigData(ID, DAMAGE_DEFAULT);
    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);

    public ClockworkAssaultRifle() {
        super(Tiers.DIAMOND, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON), ID, OFFSET, DAMAGE_DEFAULT, 24, ModSounds.SHOT7, 0.2f, 5);
    }

    @Override
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        SoundUtil.playClientSound(player, ModSounds.SHOT7.get());
        if(level.isClientSide()) return;

        Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
        Vec3 dir = MathUtil.toVec3(dirs[0]);

        for(int i = 0;i < PROJECTILE_COUNT;i++) {
            StaticSummon summon = AmmoUtil.createAmmoSummon(level, player, hand, stack, OFFSET);
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            customData.putFloat("damage", getDamage());
            customData.putFloat("knockbackScale", 0.2f);
            customData.putInt("invulnerableTime", 5);
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

            Vec3 pos = summon.position();
            summon.setPos(pos.add(dir.scale(PROJECTILE_OFFSET * i)));
            level.addFreshEntity(summon);
        }
    }
}
