package com.lzxnone.terraria.dynamic_lights;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.entity.beam.SwordBeam;
import com.lzxnone.terraria.entity.beam.SwordBeamBehaviors;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NonNull;

public class MEntityLuminance implements EntityLuminance {
    public static final MEntityLuminance INSTANCE = new MEntityLuminance();

	private MEntityLuminance() {}

    @Override
	public @NonNull Type type() {
		return MDynamicLightsInitializer.CONSTANT;
	}

	@Override
	public @Range(from = 0, to = 15) int getLuminance(@NonNull ItemLightSourceManager itemLightSourceManager, @NonNull Entity entity) {
		if(entity instanceof StaticProjectile projectile) {
			switch(projectile.getEntityData().get(StaticProjectile.BEHAVIOR)) {
				case StaticProjectileBehaviors.ENCHANTED_SWORD_BEAM,
					 StaticProjectileBehaviors.STARFURY_STAR,
					 StaticProjectileBehaviors.STAR_WRATH_STAR,
					 StaticProjectileBehaviors.ZENITH_PROJECTILE,
					 StaticProjectileBehaviors.MURAMASA_PROJECTILE,
					 StaticProjectileBehaviors.DEVILS_DEVASTATION_PROJECTILE,
					 StaticProjectileBehaviors.DEVILS_DEVASTATION_PROJECTILE2,
					 StaticProjectileBehaviors.DEVILS_DEVASTATION_PROJECTILE3,
					 StaticProjectileBehaviors.POSSESSION_PROJECTILE -> {
					return 15;
				}
				default -> {
					return 0;
				}
			}
		}else if(entity instanceof StaticSummon summon) {
			switch(summon.getEntityData().get(StaticSummon.BEHAVIOR)) {
				case StaticSummonBehaviors.THE_HORSEMANS_BLADE_JACK,
					 StaticSummonBehaviors.INFLUX_WAVER_BEAM,
					 StaticSummonBehaviors.MEOWMERE_PROJECTILE,
					 StaticSummonBehaviors.TERRA_BLADE_BEAM,
					 StaticSummonBehaviors.TRUE_NIGHTS_EDGE_BEAM,
					 StaticSummonBehaviors.NIGHTS_EDGE_BEAM,
					 StaticSummonBehaviors.LIGHTS_BANE_SLASH,
					 StaticSummonBehaviors.VOLCANO_LIGHT,
					 StaticSummonBehaviors.TERRAGRIM_BEAM,
					 StaticSummonBehaviors.FIRST_FRACTAL_PROJECTILE,
					 StaticSummonBehaviors.DEVILS_DEVASTATION_STUCK_PROJECTILE,
					 StaticSummonBehaviors.DEVILS_DEVASTATION_LIGHTNING,
					 StaticSummonBehaviors.DEVILS_DEVASTATION_MARK,
					 StaticSummonBehaviors.DEVILS_DEVASTATION_MARK_LIGHTNING,
					 StaticSummonBehaviors.LAST_PRISM_TRI,
					 StaticSummonBehaviors.LAST_PRISM_BEAM,
					 StaticSummonBehaviors.TERRAPRISMA,
					 StaticSummonBehaviors.FLAMETHROWER_FLAME,
					 StaticSummonBehaviors.ELF_MELTER_FLAME,
					 StaticSummonBehaviors.STARDUST_DRAGON,
					 StaticSummonBehaviors.FALLEN_STAR,
					 StaticSummonBehaviors.FALLEN_STAR_SUPER,
					 StaticSummonBehaviors.DROP_FALLEN_STAR -> {
					return 15;
				}
				case StaticSummonBehaviors.CHLOROPHYTE_BULLET,
					 StaticSummonBehaviors.ICHOR_BULLET,
					 StaticSummonBehaviors.NANO_BULLET,
					 StaticSummonBehaviors.LUMINITE_BULLET -> {
					return 5;
				}
				default -> {
					return 0;
				}
			}
		}else if(entity instanceof SwordBeam beam) {
			switch(beam.getEntityData().get(SwordBeam.BEHAVIOR)) {
				case SwordBeamBehaviors.THE_HORSEMANS_BLADE, SwordBeamBehaviors.TERRA_BLADE, SwordBeamBehaviors.TRUE_EXCALIBUR, SwordBeamBehaviors.TRUE_NIGHTS_EDGE, SwordBeamBehaviors.EXCALIBUR, SwordBeamBehaviors.NIGHTS_EDGE -> {
					return 15;
				}
				default -> {
					return 0;
				}
			}
		}
		return 0;
	}
}
