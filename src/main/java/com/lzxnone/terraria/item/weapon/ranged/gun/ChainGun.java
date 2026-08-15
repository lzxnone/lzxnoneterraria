package com.lzxnone.terraria.item.weapon.ranged.gun;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
public class ChainGun extends Gun {
    public static final String ID = "chain_gun";
    public static final Vector3f OFFSET = new Vector3f(-0.3f, -0.1f, 1.5f);
    public static final float DAMAGE_DEFAULT = 6.0f;
    public static final ConfigFloat DAMAGE = createDamageConfig(ID, DAMAGE_DEFAULT);
    public static final float AMMO_NOT_CONSUME_CHANCE = 0.67f;
    public static final float MAX_SPREAD_DEGREES = 15.0f;
    public static final IConfigData CONFIG_DATA = createConfigData(DAMAGE);
    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);
    public ChainGun() {
        super(Tiers.NETHERITE, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
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
        return 4;
    }
    @Override
    protected SoundEvent getShootSound(ItemStack stack, Player player) {
        return ModSounds.SHOT6.get();
    }
    @Override
    protected float getKnockbackScale(ItemStack stack, Player player) {
        return 0.1f;
    }
    @Override
    protected int getInvulnerableTime(ItemStack stack, Player player) {
        return 10;
    }

    @Override
    protected float getAmmoNotConsumeChance(ItemStack stack, LivingEntity entity) {
        return AMMO_NOT_CONSUME_CHANCE;
    }
    @Override
    protected float getSpreadDegrees(ItemStack stack, Player player, int index) {
        return getFinalSpreadDegrees(MAX_SPREAD_DEGREES, stack, player);
    }
}

