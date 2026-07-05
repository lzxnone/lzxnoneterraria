package com.lzxnone.terraria;

import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.beam.SwordBeamRenderer;
import com.lzxnone.terraria.entity.projectile.StaticProjectileRenderer;
import com.lzxnone.terraria.entity.summon.StaticSummonRenderer;
import com.lzxnone.terraria.particle.*;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = LzxnoneTerraria.MODID, dist = Dist.CLIENT)
public class LzxnoneTerrariaClient {
    public LzxnoneTerrariaClient(ModContainer container, IEventBus modEventBus) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        //Entity渲染
        modEventBus.addListener(EntityRenderersEvent.RegisterRenderers.class, event -> {
            event.registerEntityRenderer(ModEntities.STATIC_PROJECTILE.get(), StaticProjectileRenderer::new);
        });
        modEventBus.addListener(EntityRenderersEvent.RegisterRenderers.class, event -> {
            event.registerEntityRenderer(ModEntities.STATIC_SUMMON.get(), StaticSummonRenderer::new);
        });
        modEventBus.addListener(EntityRenderersEvent.RegisterRenderers.class, event -> {
            event.registerEntityRenderer(ModEntities.SWORD_BEAM.get(), SwordBeamRenderer::new);
        });
        modEventBus.addListener(EntityRenderersEvent.RegisterRenderers.class, event -> {
            event.registerEntityRenderer(ModEntities.BEE_SUMMON.get(), BeeRenderer::new);
        });

        //粒子渲染
        modEventBus.addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpriteSet(ModParticles.DUST_PARTICLE.get(), DustParticle.Provider::new);
        });
        modEventBus.addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpriteSet(ModParticles.ZENITH_TRAIL_PARTICLE.get(), ZenithTrailParticle.Provider::new);
        });
        modEventBus.addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpriteSet(ModParticles.STAR_PARTICLE.get(), StarParticle.Provider::new);
        });
        modEventBus.addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpriteSet(ModParticles.EXPLODE_PARTICLE.get(), ExplodeParticle.Provider::new);
        });
        modEventBus.addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpriteSet(ModParticles.SEEDLER_THORN_PARTICLE.get(), SeedlerThornParticle.Provider::new);
        });
        modEventBus.addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpriteSet(ModParticles.INFLUX_WAVER_DROP_PARTICLE.get(), InfluxWaverDropParticle.Provider::new);
        });
        modEventBus.addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpriteSet(ModParticles.TERRA_BEAM_HIT_PARTICLE.get(), TerraBeamHitParticle.Provider::new);
        });
    }
}
