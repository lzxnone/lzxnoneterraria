package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.BeeSummon;
import com.lzxnone.terraria.item.IItemWaveBehavior;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.MathUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

public class BeeKeeper extends SwordItem {
    public BeeKeeper() {
        super(Tiers.IRON, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 5, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -2.4, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ));
    }

    public static final IItemWaveBehavior ITEM_WAVE_BEHAVIOR = new IItemWaveBehavior() {
        @Override
        public void onAttackEntity(AttackEntityEvent event) {
            Player player = event.getEntity();
            if(!player.level().isClientSide()) {
                ItemStack itemStack = player.getMainHandItem();
                if(itemStack.isEmpty()) return;
                Item item = itemStack.getItem();
                if(item instanceof BeeKeeper && !player.getCooldowns().isOnCooldown(item)) {
                    Entity target = event.getTarget();
                    if(target instanceof LivingEntity livingTarget && FilterUtil.createLivingTargetFilter(player).test(livingTarget)) {
                        livingTarget.addEffect(new MobEffectInstance(
                            ModEffects.CONFUSED,
                            40,
                            0
                        ));
                    }
                    int count = player.level().random.nextInt(3) + 1;
                    while(count-- > 0) {
                        BeeSummon bee = ModEntities.BEE_SUMMON.get().create(player.level());
                        if(bee != null) {
                            bee.owner = player;
                            bee.setPos(new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ()));
                            player.level().addFreshEntity(bee);
                        }
                    }
                    player.getCooldowns().addCooldown(item, 40);
                }
            }
        }
    };
}
