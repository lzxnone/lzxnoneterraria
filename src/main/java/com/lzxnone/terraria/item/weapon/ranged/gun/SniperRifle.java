package com.lzxnone.terraria.item.weapon.ranged.gun;

import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.effect.ScopeZoomModifier;
import com.lzxnone.terraria.item.weapon.RangedWeapon;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

import java.util.List;

public class SniperRifle extends Gun implements ScopeZoomModifier {
    public static final String ID = "sniper_rifle";
    public static final Vector3f OFFSET = new Vector3f(-0.3f, -0.1f, 1.5f);
    public static final float DAMAGE_DEFAULT = 35.0f;
    public static final ConfigFloat DAMAGE = createDamageConfig(ID, DAMAGE_DEFAULT);
    public static final double MAX_ZOOM = 10.0D;

    public static final ConfigDouble ZOOM_SCROLL_SPEED = new ConfigDouble(
        "weapon.sniper_rifle.zoom_scroll_speed",
        "sniper_rifle_zoom_scroll_speed",
        0.5D,
        0.0D,
        10.0D
    );

    public static final IConfigData CONFIG_DATA = createConfigData(DAMAGE, ZOOM_SCROLL_SPEED);
    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);

    public SniperRifle() {
        super(Tiers.DIAMOND, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    public static double getZoomScrollSpeed() {
        return ZOOM_SCROLL_SPEED.get();
    }

    @Override
    public boolean canZoom(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    protected Vector3f getOffset(ItemStack stack, Player player) {
        return OFFSET;
    }

    @Override
    protected float getDamage(ItemStack stack, Player player) {
        return DAMAGE.get();
    }

    @Override
    public int getUseTime(ItemStack stack, LivingEntity entity) {
        return 36;
    }

    @Override
    protected SoundEvent getShootSound(ItemStack stack, Player player) {
        return ModSounds.SHOT.get();
    }

    @Override
    protected float getKnockbackScale(ItemStack stack, Player player) {
        return 2.0f;
    }

    @Override
    protected int getInvulnerableTime(ItemStack stack, Player player) {
        return 20;
    }

    @Override
    protected ItemStack getBulletAmmoStack(Level level, Player player, InteractionHand hand, ItemStack weaponStack) {
        ItemStack ammoStack = RangedWeapon.getAmmoStack(weaponStack);
        if(ammoStack.is(ModItems.MUSKET_BALL.get()) || ammoStack.is(ModItems.TUNGSTEN_BULLET.get()) || ammoStack.is(ModItems.ENDLESS_MUSKET_POUCH.get())) {
            return ModItems.HIGH_VELOCITY_BULLET.get().getDefaultInstance();
        }
        return ammoStack;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(
            "tooltip.lzxnoneterraria.sniper_rifle"
        ).withStyle(ChatFormatting.GRAY));
    }
}

