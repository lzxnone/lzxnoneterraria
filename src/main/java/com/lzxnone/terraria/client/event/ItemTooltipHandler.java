package com.lzxnone.terraria.client.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.weapon.MagicWeapon;
import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.item.weapon.RangedWeapon;
import com.lzxnone.terraria.item.weapon.SummonWeapon;
import com.lzxnone.terraria.item.weapon.summon.minion.MinionWeapon;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID, value = Dist.CLIENT)
public class ItemTooltipHandler {
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        addEnchantedBookTooltip(event);
        addMinionWeaponTooltip(event);
        addMeleeWeaponTooltip(event);
        addRangedWeaponTooltip(event);
        addSummonWeaponTooltip(event);
        addMagicWeaponTooltip(event);
    }

    private static void addEnchantedBookTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if(stack.is(Items.ENCHANTED_BOOK)) {
            ItemEnchantments storedEnchantments = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
            if(storedEnchantments.size() == 1) {
                for(Holder<Enchantment> enchantmentHolder : storedEnchantments.keySet()) {
                    ResourceLocation enchantmentId = enchantmentHolder.unwrapKey()
                            .map(ResourceKey::location)
                            .orElse(null);
                    if(enchantmentId != null && enchantmentId.getNamespace().equals(LzxnoneTerraria.MODID)) {
                        MutableComponent component = Component.translatable(
                            "tooltip.lzxnoneterraria.enchantment." + enchantmentId.getPath()
                        );
                        event.getToolTip().add(component.withStyle(ChatFormatting.GRAY));
                    }
                }
            }
        }
    }

    private static void addMinionWeaponTooltip(ItemTooltipEvent event) {
        if(event.getItemStack().getItem() instanceof MinionWeapon) {
            Player player = event.getEntity();
            if(player != null) {
                String path = BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem()).getPath();
                int count = MinionWeapon.getSummonCount(player, path);
                if(count <= 0) {
                    event.getToolTip().add(
                        Component.translatable("tooltip.lzxnoneterraria.minion_weapon_minion_count", count)
                            .withStyle(ChatFormatting.RED)
                    );
                }else {
                    event.getToolTip().add(
                        Component.translatable("tooltip.lzxnoneterraria.minion_weapon_minion_count", count)
                            .withStyle(ChatFormatting.GREEN)
                    );
                }
            }
        }
    }

    private static void addMagicWeaponTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if(stack.getItem() instanceof MagicWeapon magicWeapon) {
            Player player = event.getEntity();
            if(player != null) {
                float damage = magicWeapon.getTooltipDamage(stack, player);
                if(damage > 0.0F) {
                    event.getToolTip().add(
                        Component.translatable(
                            "tooltip.lzxnoneterraria.magic_weapon_damage",
                            damage == Math.rint(damage) ? String.valueOf((int) damage) : String.format("%.2f", damage)
                        ).withStyle(ChatFormatting.RED)
                    );
                }
            }
        }
    }

    private static void addMeleeWeaponTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if(stack.getItem() instanceof MeleeWeapon meleeWeapon) {
            Player player = event.getEntity();
            if(player != null) {
                float damage = meleeWeapon.getTooltipDamage(stack, player);
                if(damage > 0.0F) {
                    event.getToolTip().add(
                        Component.translatable(
                            "tooltip.lzxnoneterraria.melee_weapon_damage",
                            damage == Math.rint(damage) ? String.valueOf((int) damage) : String.format("%.2f", damage)
                        ).withStyle(ChatFormatting.RED)
                    );
                }
            }
        }
    }

    private static void addRangedWeaponTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if(stack.getItem() instanceof RangedWeapon rangedWeapon) {
            Player player = event.getEntity();
            if(player != null) {
                float damage = rangedWeapon.getTooltipDamage(stack, player);
                if(damage > 0.0F) {
                    event.getToolTip().add(
                        Component.translatable(
                            "tooltip.lzxnoneterraria.ranged_weapon_damage",
                            damage == Math.rint(damage) ? String.valueOf((int) damage) : String.format("%.2f", damage)
                        ).withStyle(ChatFormatting.RED)
                    );
                }
            }
        }
    }

    private static void addSummonWeaponTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if(stack.getItem() instanceof SummonWeapon summonWeapon) {
            Player player = event.getEntity();
            if(player != null) {
                float damage = summonWeapon.getTooltipDamage(stack, player);
                if(damage > 0.0F) {
                    event.getToolTip().add(
                        Component.translatable(
                            "tooltip.lzxnoneterraria.summon_weapon_damage",
                            damage == Math.rint(damage) ? String.valueOf((int) damage) : String.format("%.2f", damage)
                        ).withStyle(ChatFormatting.RED)
                    );
                }
            }
        }
    }
}
