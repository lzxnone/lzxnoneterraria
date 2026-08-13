package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.BeeSummon;
import com.lzxnone.terraria.item.IItemWaveBehavior;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.MathUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

public class BeeKeeper extends MeleeWeapon {
    public static final String ID = "bee_keeper";
    public static final ConfigFloat BASE_MELEE_DAMAGE = createBaseMeleeDamageConfig(ID, 5F);
    public static final ConfigFloat BASE_MELEE_ATTACK_SPEED = createBaseMeleeAttackSpeedConfig(ID, -2.4F);
    public static final ConfigInt MAX_BEES = new ConfigInt(
        "weapon.bee_keeper.max_bees",
        "bee_keeper_max_bees",
        3,
        0,
        10
    );
    public static final ConfigInt CONFUSION_DURATION = new ConfigInt(
        "weapon.bee_keeper.confusion_duration",
        "bee_keeper_confusion_duration",
        40,
        0,
        1200
    );
    public static final ConfigFloat BEE_DAMAGE = new ConfigFloat(
        "weapon.bee_keeper.bee_damage",
        "bee_keeper_bee_damage",
        2.0f,
        0.0f,
        8388600.0f
    );
    public BeeKeeper() {
        super(Tiers.IRON, new Item.Properties());
    }

    @Override
    protected float getBaseMeleeDamage(ItemStack stack) {
        return BASE_MELEE_DAMAGE.get();
    }

    @Override
    protected float getBaseMeleeAttackSpeed(ItemStack stack) {
        return BASE_MELEE_ATTACK_SPEED.get();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                BASE_MELEE_DAMAGE,
                BASE_MELEE_ATTACK_SPEED,
                MAX_BEES,
                CONFUSION_DURATION,
                BEE_DAMAGE
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);

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
                            CONFUSION_DURATION.get(),
                            0
                        ));
                    }

                    int maxBees = MAX_BEES.get();
                    int count = maxBees > 0 ? player.level().random.nextInt(maxBees) + 1 : 0;
                    while(count-- > 0) {
                        BeeSummon bee = ModEntities.BEE_SUMMON.get().create(player.level());
                        if(bee != null) {
                            bee.owner = player;
                            bee.stackSource = itemStack.copy();
                            bee.damage = BEE_DAMAGE.get();
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

