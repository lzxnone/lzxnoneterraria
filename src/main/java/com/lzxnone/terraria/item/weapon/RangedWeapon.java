package com.lzxnone.terraria.item.weapon;

import com.lzxnone.terraria.enchantment.ModEnchantmentConfigs;
import com.lzxnone.terraria.enchantment.ModEnchantments;
import com.lzxnone.terraria.item.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import java.util.List;

public class RangedWeapon extends Weapon {
    public static final String AMMO_KEY = "ammo";

    public RangedWeapon(Tier tier, Properties properties) {
        super(tier, properties);
    }

    public boolean canUseAmmo(ItemStack weaponStack, ItemStack ammoStack) {
        return false;
    }

    public int getUseTime(ItemStack weaponStack, LivingEntity entity) {
        return 20;
    }

    public int getAmmoConsumeAmount(ItemStack weaponStack, LivingEntity entity) {
        return 1;
    }

    protected ResourceLocation getDefaultAmmo(ItemStack weaponStack) {
        return null;
    }

    public int getFinalAmmoConsumeAmount(ItemStack weaponStack, LivingEntity entity, int amount) {
        if(amount <= 0) return 0;
        int exhaustionLevel = getEnchantmentLevel(entity, weaponStack, ModEnchantments.AMMO_EXHAUSTION);
        return Math.max(0, amount + exhaustionLevel * ModEnchantmentConfigs.getAmmoExhaustionConsumeBonus());
    }

    public boolean shouldSkipAmmoConsume(ItemStack weaponStack, LivingEntity entity) {
        int bulletHellLevel = getEnchantmentLevel(entity, weaponStack, ModEnchantments.BULLET_HELL);
        if(bulletHellLevel <= 0) return false;

        double triggerChance = Math.pow(1.0D - ModEnchantmentConfigs.getBulletHellNotConsumeChance(), bulletHellLevel);
        double skipChance = 1.0D - triggerChance;
        return entity.getRandom().nextDouble() < skipChance;
    }

    public boolean shouldShootThisTick(ItemStack weaponStack, LivingEntity entity, int remainingUseTicks) {
        int useTime = Math.max(1, getUseTime(weaponStack, entity));
        int elapsedMinecraftTicks = getUseDuration(weaponStack, entity) - remainingUseTicks;
        if(elapsedMinecraftTicks <= 0) return false;

        int currentShot = elapsedMinecraftTicks * 3 / useTime;
        int previousShot = (elapsedMinecraftTicks - 1) * 3 / useTime;
        return currentShot > previousShot;
    }

    public boolean canShoot(ItemStack weaponStack, Player player) {
        ItemStack ammoStack = getAmmoStack(weaponStack);
        if(ammoStack.isEmpty() || !canUseAmmo(weaponStack, ammoStack)) return false;
        if(player.hasInfiniteMaterials()) return true;

        Item ammoItem = ammoStack.getItem();
        for(ItemStack inventoryStack : player.getInventory().items) {
            if(inventoryStack.is(ammoItem)) return true;
        }
        return false;
    }

    public int getAmmoCount(ItemStack weaponStack, Player player) {
        ItemStack ammoStack = getAmmoStack(weaponStack);
        if(ammoStack.isEmpty() || !canUseAmmo(weaponStack, ammoStack)) return 0;

        Item ammoItem = ammoStack.getItem();
        int count = 0;
        for(ItemStack inventoryStack : player.getInventory().items) {
            if(inventoryStack.is(ammoItem)) count += inventoryStack.getCount();
        }
        return count;
    }

    public boolean consumeAmmo(ItemStack weaponStack, Player player, int amount) {
        if(amount <= 0 || player.hasInfiniteMaterials()) return true;
        if(shouldSkipAmmoConsume(weaponStack, player)) return true;

        amount = getFinalAmmoConsumeAmount(weaponStack, player, amount);
        if(amount <= 0) return true;

        ItemStack ammoStack = getAmmoStack(weaponStack);
        if(ammoStack.isEmpty() || !canUseAmmo(weaponStack, ammoStack)) return false;
        if(ammoStack.is(ModItems.ENDLESS_MUSKET_POUCH.get())) return true;

        Item ammoItem = ammoStack.getItem();
        int available = 0;
        for(ItemStack inventoryStack : player.getInventory().items) {
            if(inventoryStack.is(ammoItem)) {
                available += inventoryStack.getCount();
                if(available >= amount) break;
            }
        }
        if(available < amount) return false;

        int remaining = amount;
        for(ItemStack inventoryStack : player.getInventory().items) {
            if(!inventoryStack.is(ammoItem)) continue;

            int consumed = Math.min(remaining, inventoryStack.getCount());
            inventoryStack.shrink(consumed);
            remaining -= consumed;
            if(remaining <= 0) return true;
        }

        return false;
    }

    public boolean tryShoot(Level level, Player player, InteractionHand hand, ItemStack weaponStack) {
        if(!canShoot(weaponStack, player)) return false;
        if(!level.isClientSide() && !consumeAmmo(weaponStack, player, getAmmoConsumeAmount(weaponStack, player))) return false;

        shoot(level, player, hand, weaponStack);
        return true;
    }

    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack weaponStack) {}

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(player.getCooldowns().isOnCooldown(stack.getItem()) || !tryShoot(level, player, hand, stack)) return InteractionResultHolder.fail(stack);
        player.startUsingItem(hand);
        player.getCooldowns().addCooldown(stack.getItem(), Math.max(1, getUseTime(stack, player) / 3));
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseTicks) {
        if(!(entity instanceof Player player)) return;
        if(!shouldShootThisTick(stack, entity, remainingUseTicks)) return;

        if(!tryShoot(level, player, player.getUsedItemHand(), stack)) {
            player.stopUsingItem();
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    public static ResourceLocation getAmmo(ItemStack weaponStack) {
        String ammo = weaponStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag()
                .getString(AMMO_KEY);
        if(!ammo.isEmpty()) return ResourceLocation.parse(ammo);
        if(weaponStack.getItem() instanceof RangedWeapon rangedWeapon) {
            return rangedWeapon.getDefaultAmmo(weaponStack);
        }
        return null;
    }

    public static ItemStack getAmmoStack(ItemStack weaponStack) {
        ResourceLocation ammoId = getAmmo(weaponStack);
        if(ammoId == null) return ItemStack.EMPTY;

        Item ammoItem = BuiltInRegistries.ITEM.get(ammoId);
        return ammoItem == Items.AIR ? ItemStack.EMPTY : ammoItem.getDefaultInstance();
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot,
                                           ClickAction action, Player player, SlotAccess access) {
        if(action != ClickAction.PRIMARY || other.isEmpty() || !canUseAmmo(stack, other)) return false;

        ResourceLocation ammoId = BuiltInRegistries.ITEM.getKey(other.getItem());
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString(AMMO_KEY, ammoId.toString()));
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        ItemStack ammoStack = getAmmoStack(stack);
        if(ammoStack.isEmpty()) return;

        tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.ammo",
                ammoStack.getHoverName()
        ).withStyle(style -> style.withColor(0x55FF55)));
    }

    protected static int getEnchantmentLevel(LivingEntity entity, ItemStack stack, ResourceKey<Enchantment> enchantment) {
        return entity.registryAccess()
            .lookupOrThrow(Registries.ENCHANTMENT)
            .get(enchantment)
            .map(stack::getEnchantmentLevel)
            .orElse(0);
    }
}
