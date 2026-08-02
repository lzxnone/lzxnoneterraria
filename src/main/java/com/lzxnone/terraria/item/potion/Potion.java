package com.lzxnone.terraria.item.potion;

import com.lzxnone.terraria.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class Potion extends Item {
    private static final int DEFAULT_USE_DURATION = 10;

    public Potion() {
        super(new Item.Properties().stacksTo(99));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(!canDrink(level, player, stack)) return InteractionResultHolder.fail(stack);

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if(!level.isClientSide() && livingEntity instanceof ServerPlayer player) {
            tryDrink(player, stack);
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return DEFAULT_USE_DURATION;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    public boolean tryDrink(ServerPlayer player, ItemStack stack) {
        if(!canDrink(player.level(), player, stack)) return false;
        if(!onDrink(player, stack)) return false;

        playDrinkSound(player);
        if(!player.hasInfiniteMaterials()) stack.shrink(1);
        return true;
    }

    protected boolean canDrink(Level level, Player player, ItemStack stack) {
        return true;
    }

    protected abstract boolean onDrink(ServerPlayer player, ItemStack stack);

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(getTooltipKey(), getTooltipArgs()).withStyle(ChatFormatting.GRAY));
    }

    protected abstract String getTooltipKey();

    protected Object[] getTooltipArgs() {
        return new Object[0];
    }

    public static void playDrinkSound(ServerPlayer player) {
        player.playNotifySound(ModSounds.DRINK.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
    }
}
