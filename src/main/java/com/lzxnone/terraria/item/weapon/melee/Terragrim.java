package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.Config;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class Terragrim extends SwordItem {
    public Terragrim() {
        super(Tiers.DIAMOND, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 3, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -0.5, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ));
    }

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
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            Entity owner = summon.getOwner();
            if(owner == null) return;

            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            if(!customData.contains("idx")) return;

            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES[Math.min(customData.getInt("idx"), RES.length - 1)]));
            Vector3f[] dirs = MathUtil.computeCoordinateSystem(owner);
            int rotate = summon.getEntityData().get(StaticSummon.RZP);

            Quaternionf rotation = new Quaternionf()
                .fromAxisAngleRad(dirs[0], (float) Math.toRadians(rotate));
            poseStack.mulPose(rotation);

            float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));

            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));

            vertexConsumer.addVertex(poseStack.last().pose(), -HALF_WIDTH * SCALE, -HALF_HEIGHT * SCALE, 0)
                    .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            vertexConsumer.addVertex(poseStack.last().pose(), HALF_WIDTH * SCALE, -HALF_HEIGHT * SCALE, 0)
                    .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(1.0f, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            vertexConsumer.addVertex(poseStack.last().pose(), HALF_WIDTH * SCALE, HALF_HEIGHT * SCALE, 0)
                    .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(1.0f, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            vertexConsumer.addVertex(poseStack.last().pose(), -HALF_WIDTH * SCALE, HALF_HEIGHT * SCALE, 0)
                    .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
        }

        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            if(summon.getOwner() != null) {
                summon.setPos(summon.getOwner().getX(), summon.getOwner().getY() + summon.getOwner().getBbHeight() * 0.5, summon.getOwner().getZ());
            }
            summon.setBoundingBox(new AABB(
                summon.getX() - Config.terragrimHitRange, summon.getY() - Config.terragrimHitRange, summon.getZ() - Config.terragrimHitRange,
                summon.getX() + Config.terragrimHitRange, summon.getY() + Config.terragrimHitRange, summon.getZ() + Config.terragrimHitRange
            ));
            if(!summon.level().isClientSide()) {
                if(summon.getOwner() instanceof Player player) {
                    List<LivingEntity> targets = summon.level().getEntitiesOfClass(
                        LivingEntity.class,
                        summon.getBoundingBox(),
                        FilterUtil.createLivingTargetFilter(summon, player)
                    );
                    for(LivingEntity target : targets) {
                        AttributeInstance knockbackResist = null;
                        double originalResist = 0.0;
                        knockbackResist = target.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
                        if(knockbackResist != null) {
                            originalResist = knockbackResist.getBaseValue();
                            knockbackResist.setBaseValue(Math.max(0.9, originalResist));
                        }
                        if(DamageUtil.attack(player, target, (float) Config.terragrimDamage)) {
                            target.invulnerableTime = 0;
                        }
                        if(knockbackResist != null) {
                            knockbackResist.setBaseValue(originalResist);
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
            Vec3 pos = new Vec3(player.getX(), player.getY() + player.getBbHeight() * 0.5, player.getZ());
            summon.setPos(pos);
            summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.TERRAGRIM_BEAM);
            summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
            summon.getEntityData().set(StaticSummon.RZP, (int) ((Math.random() * 2 - 1) * Config.terragrimRotateRange));
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
}
