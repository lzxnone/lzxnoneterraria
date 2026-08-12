package com.lzxnone.terraria.item.weapon.ranged.gun;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import org.joml.Vector3f;
public class FlintlockPistol extends Gun {
    public static final String ID = "flintlock_pistol";
    public static final Vector3f OFFSET = new Vector3f(-0.3f, -0.05f, 1.5f);
    public static final float DAMAGE_DEFAULT = 2.0f;
    public static final ConfigFloat DAMAGE = createDamageConfig(ID, DAMAGE_DEFAULT);
    public static final IConfigData CONFIG_DATA = createConfigData(DAMAGE);
    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);
    public FlintlockPistol() {
        super(Tiers.IRON, new Item.Properties().stacksTo(1));
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
        return 16;
    }
    @Override
    protected SoundEvent getShootSound(ItemStack stack, Player player) {
        return ModSounds.SHOT4.get();
    }
    @Override
    protected float getKnockbackScale(ItemStack stack, Player player) {
        return 0.25f;
    }
    @Override
    protected int getInvulnerableTime(ItemStack stack, Player player) {
        return 10;
    }
}

