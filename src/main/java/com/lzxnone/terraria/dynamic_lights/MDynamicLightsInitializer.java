package com.lzxnone.terraria.dynamic_lights;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.entity.ModEntities;
import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import dev.lambdaurora.lambdynlights.api.entity.luminance.ItemDerivedEntityLuminance;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class MDynamicLightsInitializer implements DynamicLightsInitializer {
    public static final EntityLuminance.Type CONSTANT = EntityLuminance.Type.registerSimple(
		ResourceLocation.fromNamespaceAndPath("lzxnoneterraria", "custom"),
		MEntityLuminance.INSTANCE
	);

    @Override
	public void onInitializeDynamicLights(@NonNull DynamicLightsContext context) {
		context.entityLightSourceManager().onRegisterEvent().register(reg -> {
			reg.register(ModEntities.STATIC_PROJECTILE.get(), MEntityLuminance.INSTANCE);
			reg.register(ModEntities.STATIC_SUMMON.get(), MEntityLuminance.INSTANCE);
			reg.register(ModEntities.SWORD_BEAM.get(), MEntityLuminance.INSTANCE);
		});
	}

	@Override
    @Deprecated
    public void onInitializeDynamicLights(@NonNull ItemLightSourceManager itemLightSourceManager) {}
}
