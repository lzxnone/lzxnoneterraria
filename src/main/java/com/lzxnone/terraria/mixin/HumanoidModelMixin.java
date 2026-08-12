package com.lzxnone.terraria.mixin;

import com.lzxnone.terraria.client.config.RenderConfigs;
import com.lzxnone.terraria.item.weapon.RangedWeapon;
import com.lzxnone.terraria.item.weapon.ranged.bow.Bow;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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

    @Inject(method = "setupAnim", at = @At("RETURN"))
    private void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                           float netHeadYaw, float headPitch, CallbackInfo ci) {
        if(!RenderConfigs.ENABLE_SETUP_ANIM.get()) return;
        if(!(entity instanceof Player player)) return;
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
