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

    public final ModelPart skirt;

    public RobeModel(ModelPart root) {
        super(root);
        this.skirt = this.body.getChild("skirt");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidArmorModel.createMesh(new CubeDeformation(0.5F), 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 头部与头盔（长袍无头部模型）
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

        // 躯干 (长袍身体)
        PartDefinition body = partdefinition.addOrReplaceChild("body",
            CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F))
                .texOffs(26, 0).addBox(-4.0F, -1.5F, 1.5F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(46, 0).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(76, 0).addBox(-3.8F, 0.5F, -2.8F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(76, 0).mirror().addBox(1.8F, 0.5F, -2.8F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(60, 0).addBox(-1.0F, 2.0F, -3.2F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(68, 0).addBox(-1.0F, 6.0F, -2.5F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.ZERO
        );

        // 腰带与中间飘带 (Skirt，保留腰带与前后垂带挂载于身体)
        body.addOrReplaceChild("skirt",
            CubeListBuilder.create()
                .texOffs(0, 36).addBox(-4.5F, -1.5F, -2.5F, 9.0F, 2.0F, 5.0F, new CubeDeformation(0.1F)),
            PartPose.offset(0.0F, 12.0F, 0.0F)
        );

        // 右臂 (长袍宽袖)
        partdefinition.addOrReplaceChild("right_arm",
            CubeListBuilder.create()
                .texOffs(0, 20).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
        );

        // 左臂 (长袍宽袖)
        partdefinition.addOrReplaceChild("left_arm",
            CubeListBuilder.create()
                .texOffs(0, 20).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false),
            PartPose.offset(5.0F, 2.0F, 0.0F)
        );

        // 右腿 (长袍内衬 + 右半侧裙摆，随右腿前后摆动)
        partdefinition.addOrReplaceChild("right_leg",
            CubeListBuilder.create()
                .texOffs(32, 36).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(64, 36).addBox(-3.0F, 6.0F, -3.0F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(10, 52).addBox(-2.4F, 0.0F, -2.9F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 52).addBox(-3.3F, 6.0F, -1.5F, 1.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-2.0F, 12.0F, 0.0F)
        );

        // 左腿 (长袍内衬 + 左半侧裙摆，随左腿前后摆动)
        partdefinition.addOrReplaceChild("left_leg",
            CubeListBuilder.create()
                .texOffs(32, 36).mirror().addBox(-2.5F, 0.0F, -2.5F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(64, 36).mirror().addBox(-2.0F, 6.0F, -3.0F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(10, 52).mirror().addBox(0.4F, 0.0F, -2.9F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(30, 52).mirror().addBox(2.3F, 6.0F, -1.5F, 1.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offset(2.0F, 12.0F, 0.0F)
        );

        return LayerDefinition.create(meshdefinition, 128, 128);
    }
}
