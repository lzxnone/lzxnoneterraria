package com.lzxnone.terraria.mixin;

import com.lzxnone.terraria.client.config.RenderConfigs;
import com.lzxnone.terraria.entity.beam.SwordBeam;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.weapon.RangedWeapon;
import com.lzxnone.terraria.item.weapon.ranged.bow.Bow;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> {
    @Shadow @Final public ModelPart rightArm;
    @Shadow @Final public ModelPart leftArm;

    private static String behaviorOf(ItemStack stack) {
        return stack.isEmpty() ? "" : BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
    }

    @Inject(method = "setupAnim", at = @At("RETURN"))
    private void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                           float netHeadYaw, float headPitch, CallbackInfo ci) {
        if(!RenderConfigs.ENABLE_SETUP_ANIM.get()) return;
        if(!(entity instanceof Player player)) return;

        SwordBeam beam = SwordBeam.getActiveBeam(player, behaviorOf(player.getMainHandItem()));
        HumanoidArm beamArm = player.getMainArm();
        if(beam == null) {
            beam = SwordBeam.getActiveBeam(player, behaviorOf(player.getOffhandItem()));
            if(beam != null) beamArm = player.getMainArm().getOpposite();
        }
        if(beam != null) {
            float partialTick = ageInTicks - (float) entity.tickCount;
            int age = beam.getEntityData().get(SwordBeam.AGE);
            int lifetime = Math.max(1, beam.getEntityData().get(SwordBeam.LIFETIME));
            float progress = (age + partialTick) / (float) lifetime;
            if(progress > 1.0f) progress = 1.0f;
            if(beam.getEntityData().get(SwordBeam.RIGHT)) progress = 1.0f - progress;

            float angleDeg = (0.5f - progress) * 180.0f;
            if(beamArm == player.getMainArm()) angleDeg = -angleDeg;
            ModelPart arm = beamArm == HumanoidArm.RIGHT ? rightArm : leftArm;
            if(arm == leftArm) angleDeg = -angleDeg;
            if(player.getMainArm() == HumanoidArm.LEFT) angleDeg = -angleDeg;
            arm.xRot += (float) Math.toRadians(-75);
            arm.zRot += (float) Math.toRadians(-beam.getEntityData().get(SwordBeam.ROTATE));
            arm.yRot += (float) Math.toRadians(angleDeg);
        }

        if(player.getMainHandItem().is(ModItems.NIGHTGLOW.get())) {
            ModelPart arm = player.getMainArm() == HumanoidArm.RIGHT ? this.rightArm : this.leftArm;
            arm.xRot = (float) Math.toRadians(-90.0F);
            arm.yRot = 0.0F;
            arm.zRot = 0.0F;
        }
        if(player.getOffhandItem().is(ModItems.NIGHTGLOW.get())) {
            ModelPart arm = player.getMainArm() == HumanoidArm.RIGHT ? this.leftArm : this.rightArm;
            arm.xRot = (float) Math.toRadians(-90.0F);
            arm.yRot = 0.0F;
            arm.zRot = 0.0F;
        }

        if(!player.isUsingItem()) return;

        HumanoidArm usedArm = player.getUsedItemHand() == InteractionHand.MAIN_HAND
                ? player.getMainArm()
                : player.getMainArm().getOpposite();

        if(player.getUseItem().getItem() instanceof RangedWeapon) {
            if(!(player.getUseItem().getItem() instanceof Bow)) {
                if(usedArm == HumanoidArm.RIGHT) {
                    this.rightArm.xRot = (float) Math.toRadians(player.getXRot() - 90);
                }else {
                    this.leftArm.xRot = (float) Math.toRadians(player.getXRot() - 90);
                }
            }
        }
    }
}
