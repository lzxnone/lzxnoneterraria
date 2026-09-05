package com.lzxnone.terraria.client;

import com.lzxnone.terraria.client.model.armor.GemRobeModel;
import com.lzxnone.terraria.client.model.armor.RobeModel;
import com.lzxnone.terraria.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public class ClientArmorRegistration {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ClientArmorRegistration::onRegisterLayerDefinitions);
        modEventBus.addListener(ClientArmorRegistration::onRegisterClientExtensions);
    }

    // 1. 注册 3D 护甲模型的骨骼网格定义
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(RobeModel.LAYER_LOCATION, RobeModel::createBodyLayer);
        event.registerLayerDefinition(GemRobeModel.LAYER_LOCATION, GemRobeModel::createBodyLayer);
    }

    // 2. 注册物品穿戴时的 3D 模型拦截与替换
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        // 普通长袍 3D 模型
        event.registerItem(new IClientItemExtensions() {
            private RobeModel model;

            @Override
            @SuppressWarnings({"rawtypes", "unchecked"})
            public Model getGenericArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.model == null) {
                    this.model = new RobeModel(Minecraft.getInstance().getEntityModels().bakeLayer(RobeModel.LAYER_LOCATION));
                }
                original.copyPropertiesTo((HumanoidModel) this.model);
                this.model.setAllVisible(false);
                this.model.body.visible = true;
                this.model.rightArm.visible = true;
                this.model.leftArm.visible = true;
                this.model.rightLeg.visible = true;
                this.model.leftLeg.visible = true;
                return this.model;
            }
        },
            ModItems.ROBE.get()
        );

        // 7 种宝石长袍 3D 模型
        event.registerItem(new IClientItemExtensions() {
            private GemRobeModel model;

            @Override
            @SuppressWarnings({"rawtypes", "unchecked"})
            public Model getGenericArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.model == null) {
                    this.model = new GemRobeModel(Minecraft.getInstance().getEntityModels().bakeLayer(GemRobeModel.LAYER_LOCATION));
                }
                original.copyPropertiesTo((HumanoidModel) this.model);
                this.model.setAllVisible(false);
                this.model.body.visible = true;
                this.model.rightArm.visible = true;
                this.model.leftArm.visible = true;
                this.model.rightLeg.visible = true;
                this.model.leftLeg.visible = true;
                return this.model;
            }
        },
            ModItems.AMETHYST_ROBE.get(),
            ModItems.TOPAZ_ROBE.get(),
            ModItems.SAPPHIRE_ROBE.get(),
            ModItems.EMERALD_ROBE.get(),
            ModItems.RUBY_ROBE.get(),
            ModItems.AMBER_ROBE.get(),
            ModItems.DIAMOND_ROBE.get()
        );
    }
}
