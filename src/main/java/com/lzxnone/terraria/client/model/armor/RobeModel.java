package com.lzxnone.terraria.client.model.armor;

import com.lzxnone.terraria.LzxnoneTerraria;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class RobeModel extends HumanoidArmorModel<LivingEntity> {
    public static final ModelLayerLocation LAYER_LOCATION =
        new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "robe"), "main");

    public RobeModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidArmorModel.createMesh(new CubeDeformation(0.5F), 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 躯干 (长袍身体)
        partdefinition.addOrReplaceChild("body",
            CubeListBuilder.create()
                .texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
            PartPose.ZERO
        );

        // 右臂
        partdefinition.addOrReplaceChild("right_arm",
            CubeListBuilder.create()
                .texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
            PartPose.ZERO
        );

        // 左臂
        partdefinition.addOrReplaceChild("left_arm",
            CubeListBuilder.create()
                .texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
            PartPose.ZERO
        );

        // 右腿
        partdefinition.addOrReplaceChild("right_leg",
            CubeListBuilder.create()
                .texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
        );

        // 左腿
        partdefinition.addOrReplaceChild("left_leg",
            CubeListBuilder.create()
                .texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
        );

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}
