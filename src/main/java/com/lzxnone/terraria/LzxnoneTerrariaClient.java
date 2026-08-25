package com.lzxnone.terraria;

import com.lzxnone.terraria.ui.config.ConfigScreen;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.client.entity.beam.SwordBeamRenderer;
import com.lzxnone.terraria.client.entity.projectile.StaticProjectileRenderer;
import com.lzxnone.terraria.client.entity.summon.StaticSummonRenderer;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.particle.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = LzxnoneTerraria.MODID, dist = Dist.CLIENT)
public class LzxnoneTerrariaClient {
    public LzxnoneTerrariaClient(ModContainer container, IEventBus modEventBus) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) -> ConfigScreen.create(parent));

        //使用渲染
        modEventBus.addListener(FMLClientSetupEvent.class, event -> event.enqueueWork(() -> ItemProperties.register(
            ModItems.NEBULA_ARCANUM.get(),
            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "using"),
            (stack, level, entity, seed) -> {
                if(entity instanceof Player player && player.isUsingItem() && player.getUseItem().is(stack.getItem())) {
                    return 1.0F;
                }
                return 0.0F;
            }
        )));

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
            event.registerSpriteSet(ModParticles.IRON_SPARK_PARTICLE.get(), IronSparkParticle.Provider::new);
        });
        modEventBus.addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpriteSet(ModParticles.CIRCLE_PARTICLE.get(), CircleParticle.Provider::new);
        });
        modEventBus.addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpecial(ModParticles.STARLIGHT_PARTICLE.get(), new StarlightParticle.Provider());
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
            event.registerSpecial(ModParticles.TERRA_BEAM_HIT_PARTICLE.get(), new TerraBeamHitParticle.Provider());
        });
        modEventBus.addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpecial(ModParticles.EXCALIBUR_HIT_PARTICLE.get(), new ExcaliburHitParticle.Provider());
        });
        modEventBus.addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpecial(ModParticles.TRUE_EXCALIBUR_HIT_PARTICLE.get(), new TrueExcaliburHitParticle.Provider());
        });
        modEventBus.addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpecial(ModParticles.NIGHTS_EDGE_HIT_PARTICLE.get(), new NightsEdgeHitParticle.Provider());
        });
        modEventBus.addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpecial(ModParticles.TRUE_NIGHTS_EDGE_HIT_PARTICLE.get(), new TrueNightsEdgeHitParticle.Provider());
        });
        modEventBus.addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpriteSet(ModParticles.BLOOD_BUTCHERED_PARTICLE.get(), BloodButcheredParticle.Provider::new);
        });
        modEventBus.addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpriteSet(ModParticles.LEAF_PARTICLE.get(), LeafParticle.Provider::new);
        });
        modEventBus.addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpriteSet(ModParticles.SOLAR_EXPLOSION_PARTICLE.get(), SolarExplosionParticle.Provider::new);
        });
        modEventBus.addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpriteSet(ModParticles.LUNAR_FLARE_EXPLOSION_PARTICLE.get(), LunarFlareExplosionParticle.Provider::new);
        });
        modEventBus.addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpriteSet(ModParticles.CRYSTAL_FRAGMENT_PARTICLE.get(), CrystalFragmentParticle.Provider::new);
        });
        modEventBus.addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpriteSet(ModParticles.DEVILS_DEVASTATION_RUNE_PARTICLE.get(), DevilsDevastationRuneParticle.Provider::new);
        });
        modEventBus.addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpriteSet(ModParticles.DEVILS_DEVASTATION_RUNE_PARTICLE2.get(), DevilsDevastationRuneParticle2.Provider::new);
        });
        modEventBus.addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpriteSet(ModParticles.BLACK_DUST_PARTICLE.get(), BlackDustParticle.Provider::new);
        });
        modEventBus.addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpriteSet(ModParticles.PARTY_PARTICLE.get(), PartyParticle.Provider::new);
        });
    }
}
