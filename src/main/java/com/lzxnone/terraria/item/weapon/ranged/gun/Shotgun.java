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
public class Shotgun extends Gun {
    public static final String ID = "shotgun";
    public static final Vector3f OFFSET = new Vector3f(-0.3f, -0.05f, 1.5f);
    public static final float DAMAGE_DEFAULT = 4.0f;
    public static final ConfigFloat DAMAGE = createDamageConfig(ID, DAMAGE_DEFAULT);
    public static final int MIN_PROJECTILE_COUNT = 4;
    public static final int MAX_PROJECTILE_COUNT = 5;
    public static final float MAX_SPREAD_DEGREES = 10.0f;
    public static final IConfigData CONFIG_DATA = createConfigData(DAMAGE);
    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);
    public Shotgun() {
        super(Tiers.IRON, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
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
        return 45;
    }
    @Override
    protected SoundEvent getShootSound(ItemStack stack, Player player) {
        return ModSounds.SHOT5.get();
    }
    @Override
    protected float getKnockbackScale(ItemStack stack, Player player) {
        return 1.25f;
    }
    @Override
    protected int getInvulnerableTime(ItemStack stack, Player player) {
        return 10;
    }

    @Override
    protected int getProjectileCount(ItemStack stack, Player player) {
        return MIN_PROJECTILE_COUNT + player.getRandom().nextInt(MAX_PROJECTILE_COUNT - MIN_PROJECTILE_COUNT + 1);
    }
    @Override
    protected float getSpreadDegrees(ItemStack stack, Player player, int index) {
        return getFinalSpreadDegrees(MAX_SPREAD_DEGREES, stack, player);
    }
}

