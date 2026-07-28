package com.lzxnone.terraria.item.weapon.ranged;

import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.weapon.RangedWeapon;
import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.ConfigUtil;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.AmmoUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public class SniperRifle extends BasicGun {
    public static final String ID = "sniper_rifle";
    public static final Vector3f OFFSET = new Vector3f(-0.3f, -0.1f, 1.5f);
    public static final float DAMAGE_DEFAULT = 35.0f;
    public static final double MAX_ZOOM = 10.0D;

    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";
    public static final String ZOOM_SCROLL_SPEED_PATH = "weapon.sniper_rifle.zoom_scroll_speed";
    public static final double ZOOM_SCROLL_SPEED_DEFAULT = 0.5D;
    public static final double ZOOM_SCROLL_SPEED_MIN = 0.0D;
    public static final double ZOOM_SCROLL_SPEED_MAX = 10.0D;

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            createConfigData(ID, DAMAGE_DEFAULT).onConfigLoad();
            ConfigFactory.loadDoubleConfig(
                ZOOM_SCROLL_SPEED_PATH,
                Component.translatable(CONFIG_TRANSLATION_PREFIX + "sniper_rifle_zoom_scroll_speed"),
                Component.translatable(CONFIG_TRANSLATION_PREFIX + "sniper_rifle_zoom_scroll_speed.tooltip"),
                ZOOM_SCROLL_SPEED_DEFAULT,
                ZOOM_SCROLL_SPEED_MIN,
                ZOOM_SCROLL_SPEED_MAX
            );
        }
    };
    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);

    public SniperRifle() {
        super(Tiers.DIAMOND, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC), ID, OFFSET, DAMAGE_DEFAULT, 36, ModSounds.SHOT, 2.0f, 10);
    }

    public static double getZoomScrollSpeed() {
        return Math.clamp(ConfigUtil.readDouble(ZOOM_SCROLL_SPEED_PATH, ZOOM_SCROLL_SPEED_DEFAULT), ZOOM_SCROLL_SPEED_MIN, ZOOM_SCROLL_SPEED_MAX);
    }

    @Override
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        SoundUtil.playClientSound(player, ModSounds.SHOT.get());
        if(level.isClientSide()) return;

        StaticSummon summon = AmmoUtil.createAmmoSummon(level, player, hand, stack, OFFSET);
        ItemStack ammoStack = RangedWeapon.getAmmoStack(stack);
        if(ammoStack.is(ModItems.MUSKET_BALL.get()) || ammoStack.is(ModItems.TUNGSTEN_BULLET.get())) {
            AmmoUtil.setHighVelocityBullet(summon);
        }

        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
        customData.putFloat("damage", getDamage());
        customData.putFloat("knockbackScale", 2.0f);
        customData.putInt("invulnerableTime", 10);
        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
        level.addFreshEntity(summon);
    }
}
