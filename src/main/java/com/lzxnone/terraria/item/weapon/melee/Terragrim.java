package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.ConfigUtil;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import com.mojang.math.Axis;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class Terragrim extends MeleeWeapon {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    public static final String HIT_RANGE_PATH = "weapon.terragrim.hit_range";
    public static final double HIT_RANGE_DEFAULT = 2.5;
    public static final double HIT_RANGE_MIN = 0.5;
    public static final double HIT_RANGE_MAX = 10.0;

    public static final String DAMAGE_PATH = "weapon.terragrim.damage";
    public static final float DAMAGE_DEFAULT = 0.25f;
    public static final float DAMAGE_MIN = 0.0f;
    public static final float DAMAGE_MAX = 8388600.0f;

    public static final String ROTATE_RANGE_PATH = "weapon.terragrim.rotate_range";
    public static final int ROTATE_RANGE_DEFAULT = 45;
    public static final int ROTATE_RANGE_MIN = 0;
    public static final int ROTATE_RANGE_MAX = 90;

    public Terragrim() {
        super(Tiers.DIAMOND, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 3, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -0.5, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ).rarity(Rarity.RARE));
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigFactory.loadDoubleConfig(HIT_RANGE_PATH, configText("terragrim_hit_range"), configTooltip("terragrim_hit_range"), HIT_RANGE_DEFAULT, HIT_RANGE_MIN, HIT_RANGE_MAX);
            ConfigFactory.loadFloatConfig(DAMAGE_PATH, configText("terragrim_damage"), configTooltip("terragrim_damage"), DAMAGE_DEFAULT, DAMAGE_MIN, DAMAGE_MAX);
            ConfigFactory.loadIntConfig(ROTATE_RANGE_PATH, configText("terragrim_rotate_range"), configTooltip("terragrim_rotate_range"), ROTATE_RANGE_DEFAULT, ROTATE_RANGE_MIN, ROTATE_RANGE_MAX);
        }
    };

    private static Component configText(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key);
    }

    private static Component configTooltip(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key + ".tooltip");
    }

    public static double getHitRange() {
        return Math.clamp(ConfigUtil.readDouble(HIT_RANGE_PATH, HIT_RANGE_DEFAULT), HIT_RANGE_MIN, HIT_RANGE_MAX);
    }

    public static float getDamage() {
        return Math.clamp(ConfigUtil.readFloat(DAMAGE_PATH, DAMAGE_DEFAULT), DAMAGE_MIN, DAMAGE_MAX);
    }

    public static int getRotateRange() {
        return Math.clamp(ConfigUtil.readInt(ROTATE_RANGE_PATH, ROTATE_RANGE_DEFAULT), ROTATE_RANGE_MIN, ROTATE_RANGE_MAX);
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "terragrim",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/terragrim.png"),
        Component.translatable("item.lzxnoneterraria.terragrim"),
        CONFIG_DATA
    );

    public static final ResourceLocation[] RES = {
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam0.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam1.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam2.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam3.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam4.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam5.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam6.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam7.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam8.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam9.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam10.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam11.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam12.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam13.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam14.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam15.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam16.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam17.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam18.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam19.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam20.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam21.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam22.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam23.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam24.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam25.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam26.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/terragrim_beam27.png")
    };

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        public static final float HALF_WIDTH = 32.0f;
        public static final float HALF_HEIGHT = 30.0f;
        public static final float SCALE = 0.05f;


        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            if(summon.getOwner() != null) {
                summon.setPos(summon.getOwner().getX(), summon.getOwner().getY() + summon.getOwner().getBbHeight() * 0.5, summon.getOwner().getZ());
            }
            double hitRange = getHitRange();
            summon.setBoundingBox(new AABB(
                summon.getX() - hitRange, summon.getY() - hitRange, summon.getZ() - hitRange,
                summon.getX() + hitRange, summon.getY() + hitRange, summon.getZ() + hitRange
            ));
            if(!summon.level().isClientSide()) {
                if(summon.getOwner() instanceof Player player) {
                    List<Entity> targets = summon.level().getEntitiesOfClass(
                        Entity.class,
                        summon.getBoundingBox(),
                        FilterUtil.createTargetFilter(summon, player)
                    );
                    for(Entity target : targets) {
                        if(target instanceof LivingEntity livingEntity) {
                            if(DamageUtil.meleeAttack(summon, target, summon.getEntityData().get(StaticSummon.STACK_SOURCE), (float) getDamage(), 0.05f)) target.invulnerableTime = 0;
                        }
                    }
                }
            }
        }
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        CustomData.update(DataComponents.CUSTOM_DATA, stack,
                tag -> tag.putInt("idx", 0));
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int count) {
        if(!(livingEntity instanceof Player player)) return;
        int idx = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag().getInt("idx");
        if(!level.isClientSide()) {
            StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), level);
            summon.setOwner(player);
            summon.getEntityData().set(StaticSummon.STACK_SOURCE, player.getWeaponItem().copy());
            Vec3 pos = new Vec3(player.getX(), player.getY() + player.getBbHeight() * 0.5, player.getZ());
            summon.setPos(pos);
            summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.TERRAGRIM_BEAM);
            summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
            summon.getEntityData().set(StaticSummon.RZP, (int) ((Math.random() * 2 - 1) * getRotateRange()));
            summon.getEntityData().set(StaticSummon.LIFETIME, 1);
            summon.getEntityData().set(StaticSummon.GLOW, true);

            CompoundTag customData = new CompoundTag();
            customData.putInt("idx", idx);
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

            summon.setNoGravity(true);
            summon.noPhysics = true;

            Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
            float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
            summon.setXRot(xyRot[0]);
            summon.setYRot(xyRot[1]);

            level.addFreshEntity(summon);
        }else {
            SoundUtil.playClientSound(player, ModSounds.WAVE.get());
        }
        CustomData.update(DataComponents.CUSTOM_DATA, stack,
                tag -> tag.putInt("idx", (idx + 1) % RES.length));
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public @NonNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }
}

