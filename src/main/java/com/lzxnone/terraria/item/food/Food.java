package com.lzxnone.terraria.item.food;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class Food extends Item {
    private static final int DEFAULT_USE_DURATION = 32;

    private final int useDuration;

    public Food(Properties properties) {
        this(properties, DEFAULT_USE_DURATION);
    }

    public Food(Properties properties, int useDuration) {
        super(properties);
        this.useDuration = useDuration;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(!canEat(level, player, stack)) return InteractionResultHolder.fail(stack);

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if(livingEntity instanceof Player player && canEat(level, player, stack)) {
            if(!level.isClientSide() && onEat(level, player, stack) && !player.hasInfiniteMaterials()) {
                stack.shrink(1);
            }
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return useDuration;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    protected boolean canEat(Level level, Player player, ItemStack stack) {
        return true;
    }

    protected abstract boolean onEat(Level level, Player player, ItemStack stack);
}
