package com.lzxnone.terraria;

import com.lzxnone.terraria.block.ModBlocks;
import com.lzxnone.terraria.creative_mode_tab.ModCreativeModeTabs;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.BeeSummon;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.particle.ModParticles;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;

@Mod(LzxnoneTerraria.MODID)
public class LzxnoneTerraria {
    public static final String MODID = "lzxnoneterraria";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LzxnoneTerraria(IEventBus modEventBus, ModContainer modContainer) {
        //注册
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModEffects.MOB_EFFECTS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModParticles.PARTICLE_TYPES.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModCreativeModeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        //实体属性创建
        modEventBus.addListener(this::onEntityAttributeCreation);

        //配置
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.BEE_SUMMON.get(), BeeSummon.createAttributes().build());
    }
}
