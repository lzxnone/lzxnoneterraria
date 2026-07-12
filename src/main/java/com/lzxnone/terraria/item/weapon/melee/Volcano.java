package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.Config;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.item.IItemWaveBehavior;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.ParticleUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.List;

public class Volcano extends SwordItem {
    public Volcano() {
        super(Tiers.IRON, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 6, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -2.4, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ));
    }

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.075f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(1.0F, 0.8F, 0.5F),
            new Vector3f(1.0F, 0.5F, 0.0F),
            new Vector3f(1.0F, 0.9F, 0.0F),
        }
    );

    public static final IItemWaveBehavior ITEM_WAVE_BEHAVIOR = new IItemWaveBehavior() {
        @Override
        public void onLeftClickAir(PlayerInteractEvent.LeftClickEmpty event) {
            Player player = event.getEntity();
            if(player.level().isClientSide()) {
                ItemStack itemStack = player.getMainHandItem();
                if(itemStack.isEmpty()) return;
                Item item = itemStack.getItem();
                if(item instanceof Volcano) {
                    ParticleUtil.addParticles(
                        player.level(), PARTICLE,
                        player.getBoundingBox().getCenter(), 0.2,
                        new Vec3(0, 0, 0), 0.2,
                        10
                    );
                }
            }
        }

        @Override
        public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
            Player player = event.getEntity();
            if(player.level().isClientSide()) {
                ItemStack itemStack = player.getMainHandItem();
                if(itemStack.isEmpty()) return;
                Item item = itemStack.getItem();
                if(item instanceof Volcano) {
                    ParticleUtil.addParticles(
                        player.level(), PARTICLE,
                        player.getBoundingBox().getCenter(), 0.2,
                        new Vec3(0, 0, 0), 0.2,
                        10
                    );
                }
            }
        }

        @Override
        public void onAttackEntity(AttackEntityEvent event) {
            Player player = event.getEntity();
            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.isEmpty()) return;
            Item item = itemStack.getItem();
            if(item instanceof Volcano) {
                if(!player.level().isClientSide()) {
                    Entity target = event.getTarget();
                    if(target instanceof LivingEntity livingTarget && FilterUtil.createLivingTargetFilter(player).test(livingTarget)) {
                        if(!player.getCooldowns().isOnCooldown(item)) {
                            List<LivingEntity> targets = player.level().getEntitiesOfClass(
                                LivingEntity.class,
                                AABB.ofSize(player.getBoundingBox().getCenter(), Config.volcanoExplosionRange * 2, Config.volcanoExplosionRange * 2, Config.volcanoExplosionRange * 2),
                                FilterUtil.createLivingTargetFilter(player)
                            );
                            targets.sort(Comparator.comparingDouble(e -> e.distanceToSqr(target.position())));
                            int hitCount = 0;
                            for(LivingEntity livingEntity : targets) {
                                if(livingEntity.getUUID() == target.getUUID()) continue;
                                if(hitCount >= Config.volcanoExplosionMaxHitCount) break;
                                if(DamageUtil.attack(player, livingEntity, (float) Config.volcanoExplosionDamage)) {
                                    livingTarget.igniteForSeconds(Config.volcanoIgniteSeconds);
                                    hitCount++;
                                }
                            }
                            int randomSound = player.getRandom().nextInt(3);
                            if(randomSound == 0) {
                                SoundUtil.playServerSound(target.level(), ModSounds.EXPLOSIVE_TRAP_EXPLODE0.get(), target.position());
                            }else if(randomSound == 1) {
                                SoundUtil.playServerSound(target.level(), ModSounds.EXPLOSIVE_TRAP_EXPLODE1.get(), target.position());
                            }else {
                                SoundUtil.playServerSound(target.level(), ModSounds.EXPLOSIVE_TRAP_EXPLODE2.get(), target.position());
                            }
                            ParticleUtil.addParticles(
                                (ServerLevel) target.level(), ModParticles.SOLAR_EXPLOSION_PARTICLE.get(),
                                new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2.0, target.getZ()), new Vec3(0, 0, 0),
                                0, 1
                            );
                            ParticleUtil.addParticles(
                                (ServerLevel) target.level(), ModParticles.EXPLODE_PARTICLE.get(),
                                new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2.0, target.getZ()), new Vec3(0, 0, 0),
                                0.2, (int) (4 + Math.random() * 4)
                            );
                            ParticleUtil.addParticles(
                                (ServerLevel) target.level(), PARTICLE,
                                new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2.0, target.getZ()), new Vec3(0, 0, 0),
                                0.4, 50
                            );
                            player.getCooldowns().addCooldown(item, 20);
                        }
                        if(player.getRandom().nextInt(2) == 0) livingTarget.igniteForSeconds(Config.volcanoIgniteSeconds);
                    }
                }else {
                    ParticleUtil.addParticles(
                        player.level(), PARTICLE,
                        player.getBoundingBox().getCenter(), 0.2,
                        new Vec3(0, 0, 0), 0.2,
                        10
                    );
                }
            }
        }
    };
}
