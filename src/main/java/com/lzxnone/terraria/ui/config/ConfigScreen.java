package com.lzxnone.terraria.ui.config;

import com.lowdragmc.lowdraglib2.gui.holder.ModularUIScreen;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.utils.XmlUtils;
import com.lzxnone.terraria.LzxnoneTerraria;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

public class ConfigScreen {
    private static final ResourceLocation UI_PATH = ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "ui/config.xml");
    private static final String[] NAV_KEYS = {ConfigList.WEAPON, ConfigList.AMMO, ConfigList.EFFECT, ConfigList.ENCHANTMENT};
    private static String selectedNavKey = ConfigList.WEAPON;
    private static final Map<String, Integer> NAV_SELECTED_INDEXES = new HashMap<>();

    public static Screen create(Screen parent) {
        UI ui = loadUI();

        //返回回调
        findElement(ui, "back-button", Button.class).setOnClick(event -> saveAndReturn(parent));

        //为头部导航按钮设置回调
        findElement(ui, "nav-button-weapon", Button.class).setOnClick(event -> onNavSelected(ui, ConfigList.WEAPON));
        findElement(ui, "nav-button-ammo", Button.class).setOnClick(event -> onNavSelected(ui, ConfigList.AMMO));
        findElement(ui, "nav-button-effect", Button.class).setOnClick(event -> onNavSelected(ui, ConfigList.EFFECT));
        findElement(ui, "nav-button-enchantment", Button.class).setOnClick(event -> onNavSelected(ui, ConfigList.ENCHANTMENT));

        //底部回调
        findElement(ui, "button-cancel", Button.class).setOnClick(event -> discardAndReturn(parent));
        findElement(ui, "button-confirm", Button.class).setOnClick(event -> saveAndReturn(parent));

        ConfigFactory.bindConfigContainer(findElement(ui, "config-container", UIElement.class));

        onNavSelected(ui, getCachedNavKey());

        ModularUI modularUI = ModularUI.of(UI.of(ui.rootElement, ui.stylesheets, screenSize -> screenSize));
        modularUI.shouldCloseOnEsc(true);
        return new ModularUIScreen(modularUI, Component.translatable("lzxnoneterraria.configuration.title")) {
            @Override
            public boolean isPauseScreen() {
                return true;
            }
        };
    }

    //UI文件加载
    private static UI loadUI() {
        Document document = XmlUtils.loadXml(UI_PATH);
        if(document == null) {
            LzxnoneTerraria.LOGGER.error("Failed to load LDLib2 UI template {}", UI_PATH);
            return UI.of(new UIElement());
        }
        return UI.of(document);
    }

    //头部导航按钮回调
    private static void onNavSelected(UI ui, String key) {
        selectedNavKey = getValidNavKey(key);
        for(String navKey : NAV_KEYS) {
            findElement(ui, "nav-item-" + navKey, UIElement.class).removeClass("nav-item--selected");
        }
        findElement(ui, "nav-item-" + selectedNavKey, UIElement.class).addClass("nav-item--selected");
        ConfigFactory.clearConfigContainer();

        ConfigListItem[] items = ConfigList.getItems(selectedNavKey);
        UIElement toggleContainer = findElement(ui, "toggle-container", UIElement.class);
        toggleContainer.clearAllChildren();

        for(int index = 0; index < items.length; index++) {
            ConfigListItem item = items[index];
            int itemIndex = index;
            Button toggleItem = new Button();
            toggleItem.setId(getToggleItemId(selectedNavKey, itemIndex));
            toggleItem.addClass("toggle-item");
            toggleItem.noText();
            toggleItem.setOnClick(event -> loadConfigItem(ui, selectedNavKey, itemIndex, item));

            UIElement itemIcon = new UIElement();
            itemIcon.addClass("toggle-item-img");
            itemIcon.style(style -> style.backgroundTexture(SpriteTexture.of(item.icon)));

            Label itemLabel = new Label();
            itemLabel.addClass("toggle-item-label");
            itemLabel.setText(item.name);

            toggleItem.addChildren(itemIcon, itemLabel);
            toggleContainer.addChild(toggleItem);
        }

        if(items.length > 0) {
            int selectedIndex = Math.clamp(NAV_SELECTED_INDEXES.getOrDefault(selectedNavKey, 0), 0, items.length - 1);
            loadConfigItem(ui, selectedNavKey, selectedIndex, items[selectedIndex]);
        }
    }

    private static String getCachedNavKey() {
        return getValidNavKey(selectedNavKey);
    }

    private static String getValidNavKey(String key) {
        for(String navKey : NAV_KEYS) {
            if(navKey.equals(key)) {
                return key;
            }
        }
        return NAV_KEYS[0];
    }

    private static void loadConfigItem(UI ui, String navKey, int index, ConfigListItem item) {
        NAV_SELECTED_INDEXES.put(navKey, index);
        updateToggleSelected(ui, navKey, index);
        ConfigFactory.clearConfigContainer();
        ConfigFactory.addConfigElement(createConfigTitle(item.name));
        item.onConfigLoad();
    }

    private static Label createConfigTitle(Component name) {
        Label title = new Label();
        title.addClass("config-title");
        title.setText(name);
        return title;
    }

    private static void updateToggleSelected(UI ui, String navKey, int selectedIndex) {
        UIElement toggleContainer = findElement(ui, "toggle-container", UIElement.class);
        for(UIElement child : toggleContainer.getChildren()) {
            child.removeClass("toggle-item--selected");
        }
        ui.selectId(getToggleItemId(navKey, selectedIndex), UIElement.class)
            .findFirst()
            .ifPresent(element -> element.addClass("toggle-item--selected"));
    }

    private static String getToggleItemId(String navKey, int index) {
        return "toggle-item-" + navKey + "-" + index;
    }

    private static void saveAndReturn(Screen parent) {
        ConfigUtil.save();
        Minecraft.getInstance().setScreen(parent);
    }

    private static void discardAndReturn(Screen parent) {
        ConfigUtil.reload();
        Minecraft.getInstance().setScreen(parent);
    }

    private static <T extends UIElement> T findElement(UI ui, String id, Class<T> type) {
        return ui.selectId(id, type).findFirst().orElseThrow(() ->
            new IllegalStateException("Missing " + type.getSimpleName() + " with id '" + id + "' in " + UI_PATH));
    }
}
