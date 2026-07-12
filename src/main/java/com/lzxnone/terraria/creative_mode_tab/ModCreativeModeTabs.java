package com.lzxnone.terraria.creative_mode_tab;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LzxnoneTerraria.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WEAPON_TAB = CREATIVE_MODE_TABS.register("weapon_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.lzxnoneterraria.weapon"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.TERRA_BLADE.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.COPPER_SHORTSWORD.get());
                output.accept(ModItems.ENCHANTED_SWORD.get());
                output.accept(ModItems.BEE_KEEPER.get());
                output.accept(ModItems.STARFURY.get());
                output.accept(ModItems.SEEDLER.get());
                output.accept(ModItems.THE_HORSEMANS_BLADE.get());
                output.accept(ModItems.INFLUX_WAVER.get());
                output.accept(ModItems.STAR_WRATH.get());
                output.accept(ModItems.MEOWMERE.get());
                output.accept(ModItems.TERRA_BLADE.get());
                output.accept(ModItems.ZENITH.get());
                output.accept(ModItems.TRUE_COPPER_SHORTSWORD.get());
                output.accept(ModItems.EXCALIBUR.get());
                output.accept(ModItems.NIGHTS_EDGE.get());
                output.accept(ModItems.TRUE_EXCALIBUR.get());
                output.accept(ModItems.TRUE_NIGHTS_EDGE.get());
                output.accept(ModItems.LIGHTS_BANE.get());
                output.accept(ModItems.BLOOD_BUTCHERER.get());
                output.accept(ModItems.MURAMASA.get());
                output.accept(ModItems.BLADE_OF_GRASS.get());
                output.accept(ModItems.VOLCANO.get());
                output.accept(ModItems.TERRAGRIM.get());
            }).build());
}
