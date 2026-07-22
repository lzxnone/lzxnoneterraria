package com.lzxnone.terraria.ui.config;

import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Scroller;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Switch;
import com.lowdragmc.lowdraglib2.gui.ui.elements.TextField;
import net.minecraft.network.chat.Component;

public class ConfigFactory {
    private static final String RESET_BUTTON_TRANSLATION_KEY = "lzxnoneterraria.configuration.reset_button";
    private static UIElement configContainer;

    public static void bindConfigContainer(UIElement container) {
        configContainer = container;
    }

    public static void clearConfigContainer() {
        if(configContainer != null) {
            configContainer.clearAllChildren();
        }
    }

    public static void addConfigElement(UIElement element) {
        if(configContainer == null) {
            throw new IllegalStateException("Config container has not been bound.");
        }
        configContainer.addChild(element);
    }

    public static void loadIntConfig(String path, String text, int defaultValue, int minValue, int maxValue) {
        loadIntConfig(path, Component.literal(text), null, defaultValue, minValue, maxValue);
    }

    public static void loadIntConfig(String path, Component text, int defaultValue, int minValue, int maxValue) {
        loadIntConfig(path, text, null, defaultValue, minValue, maxValue);
    }

    public static void loadIntConfig(String path, Component text, Component tooltip, int defaultValue, int minValue, int maxValue) {
        if(configContainer == null) {
            throw new IllegalStateException("Config container has not been bound.");
        }

        int value = Math.clamp(ConfigUtil.readInt(path, defaultValue), minValue, maxValue);
        int resetValue = Math.clamp(defaultValue, minValue, maxValue);

        UIElement container = new UIElement();
        container.addClass("config-scroller-container");

        Label label = new Label();
        label.addClass("config-scroller-label");
        label.setText(text);
        applyLabelTooltip(label, tooltip);

        Scroller.Horizontal scroller = new Scroller.Horizontal();
        scroller.addClass("config-scroller");
        disableScrollerWheel(scroller);
        scroller.setRange(minValue, maxValue);
        scroller.setValue((float)value, false);

        TextField valueField = new TextField();
        valueField.addClass("config-scroller-textfield");
        valueField.setNumbersOnlyInt(minValue, maxValue);
        disableTextFieldWheel(valueField);
        valueField.setText(Integer.toString(value), false);

        Button resetButton = new Button();
        resetButton.addClass("reset-button");
        resetButton.setText(Component.translatable(RESET_BUTTON_TRANSLATION_KEY));

        scroller.setOnValueChanged(floatValue -> setIntConfigValue(path, scroller, valueField, Math.round(floatValue), minValue, maxValue));
        valueField.setTextResponder(textValue -> setIntConfigValue(path, scroller, valueField, parseInt(textValue, defaultValue), minValue, maxValue));
        resetButton.setOnClick(event -> setIntConfigValue(path, scroller, valueField, resetValue, minValue, maxValue));

        container.addChildren(label, scroller, valueField, resetButton);
        configContainer.addChild(container);
        configContainer.addChild(new UIElement().addClass("config-divider"));
    }

    public static void loadFloatConfig(String path, String text, float defaultValue, float minValue, float maxValue) {
        loadFloatConfig(path, Component.literal(text), null, defaultValue, minValue, maxValue);
    }

    public static void loadFloatConfig(String path, Component text, float defaultValue, float minValue, float maxValue) {
        loadFloatConfig(path, text, null, defaultValue, minValue, maxValue);
    }

    public static void loadFloatConfig(String path, Component text, Component tooltip, float defaultValue, float minValue, float maxValue) {
        if(configContainer == null) {
            throw new IllegalStateException("Config container has not been bound.");
        }

        float value = Math.clamp(ConfigUtil.readFloat(path, defaultValue), minValue, maxValue);
        float resetValue = Math.clamp(defaultValue, minValue, maxValue);

        UIElement container = new UIElement();
        container.addClass("config-scroller-container");

        Label label = new Label();
        label.addClass("config-scroller-label");
        label.setText(text);
        applyLabelTooltip(label, tooltip);

        Scroller.Horizontal scroller = new Scroller.Horizontal();
        scroller.addClass("config-scroller");
        disableScrollerWheel(scroller);
        scroller.setRange(minValue, maxValue);
        scroller.setValue(value, false);

        TextField valueField = new TextField();
        valueField.addClass("config-scroller-textfield");
        valueField.setNumbersOnlyFloat(minValue, maxValue);
        disableTextFieldWheel(valueField);
        valueField.setText(formatFloat(value), false);

        Button resetButton = new Button();
        resetButton.addClass("reset-button");
        resetButton.setText(Component.translatable(RESET_BUTTON_TRANSLATION_KEY));

        scroller.setOnValueChanged(floatValue -> setFloatConfigValue(path, scroller, valueField, floatValue, minValue, maxValue));
        valueField.setTextResponder(textValue -> setFloatConfigValue(path, scroller, valueField, parseFloat(textValue, defaultValue), minValue, maxValue));
        resetButton.setOnClick(event -> setFloatConfigValue(path, scroller, valueField, resetValue, minValue, maxValue));

        container.addChildren(label, scroller, valueField, resetButton);
        configContainer.addChild(container);
        configContainer.addChild(new UIElement().addClass("config-divider"));
    }

    public static void loadDoubleConfig(String path, String text, double defaultValue, double minValue, double maxValue) {
        loadDoubleConfig(path, Component.literal(text), null, defaultValue, minValue, maxValue);
    }

    public static void loadDoubleConfig(String path, Component text, double defaultValue, double minValue, double maxValue) {
        loadDoubleConfig(path, text, null, defaultValue, minValue, maxValue);
    }

    public static void loadDoubleConfig(String path, Component text, Component tooltip, double defaultValue, double minValue, double maxValue) {
        if(configContainer == null) {
            throw new IllegalStateException("Config container has not been bound.");
        }

        double value = Math.clamp(ConfigUtil.readDouble(path, defaultValue), minValue, maxValue);
        double resetValue = Math.clamp(defaultValue, minValue, maxValue);

        UIElement container = new UIElement();
        container.addClass("config-scroller-container");

        Label label = new Label();
        label.addClass("config-scroller-label");
        label.setText(text);
        applyLabelTooltip(label, tooltip);

        Scroller.Horizontal scroller = new Scroller.Horizontal();
        scroller.addClass("config-scroller");
        disableScrollerWheel(scroller);
        scroller.setRange((float)minValue, (float)maxValue);
        scroller.setValue((float)value, false);

        TextField valueField = new TextField();
        valueField.addClass("config-scroller-textfield");
        valueField.setNumbersOnlyDouble(minValue, maxValue);
        disableTextFieldWheel(valueField);
        valueField.setText(formatDouble(value), false);

        Button resetButton = new Button();
        resetButton.addClass("reset-button");
        resetButton.setText(Component.translatable(RESET_BUTTON_TRANSLATION_KEY));

        scroller.setOnValueChanged(floatValue -> setDoubleConfigValue(path, scroller, valueField, floatValue, minValue, maxValue));
        valueField.setTextResponder(textValue -> setDoubleConfigValue(path, scroller, valueField, parseDouble(textValue, defaultValue), minValue, maxValue));
        resetButton.setOnClick(event -> setDoubleConfigValue(path, scroller, valueField, resetValue, minValue, maxValue));

        container.addChildren(label, scroller, valueField, resetButton);
        configContainer.addChild(container);
        configContainer.addChild(new UIElement().addClass("config-divider"));
    }

    public static void loadStringConfig(String path, String text, String defaultValue) {
        loadStringConfig(path, Component.literal(text), null, defaultValue);
    }

    public static void loadStringConfig(String path, Component text, String defaultValue) {
        loadStringConfig(path, text, null, defaultValue);
    }

    public static void loadStringConfig(String path, Component text, Component tooltip, String defaultValue) {
        if(configContainer == null) {
            throw new IllegalStateException("Config container has not been bound.");
        }

        String value = ConfigUtil.readString(path, defaultValue);

        UIElement container = new UIElement();
        container.addClass("config-textfield-container");

        Label label = new Label();
        label.addClass("config-textfield-label");
        label.setText(text);
        applyLabelTooltip(label, tooltip);

        TextField textField = new TextField();
        textField.addClass("config-textfield");
        textField.setText(value, false);

        Button resetButton = new Button();
        resetButton.addClass("reset-button");
        resetButton.setText(Component.translatable(RESET_BUTTON_TRANSLATION_KEY));

        textField.setTextResponder(textValue -> ConfigUtil.setString(path, textValue));
        resetButton.setOnClick(event -> setStringConfigValue(path, textField, defaultValue));

        container.addChildren(label, textField, resetButton);
        configContainer.addChild(container);
        configContainer.addChild(new UIElement().addClass("config-divider"));
    }

    public static void loadBooleanConfig(String path, String text, boolean defaultValue) {
        loadBooleanConfig(path, Component.literal(text), null, defaultValue);
    }

    public static void loadBooleanConfig(String path, Component text, boolean defaultValue) {
        loadBooleanConfig(path, text, null, defaultValue);
    }

    public static void loadBooleanConfig(String path, Component text, Component tooltip, boolean defaultValue) {
        if(configContainer == null) {
            throw new IllegalStateException("Config container has not been bound.");
        }

        boolean value = ConfigUtil.readBoolean(path, defaultValue);

        UIElement container = new UIElement();
        container.addClass("config-switch-container");

        Label label = new Label();
        label.addClass("config-switch-label");
        label.setText(text);
        applyLabelTooltip(label, tooltip);

        Switch configSwitch = new Switch();
        configSwitch.addClass("config-switch");
        configSwitch.setOn(value, false);

        Button resetButton = new Button();
        resetButton.addClass("reset-button");
        resetButton.setText(Component.translatable(RESET_BUTTON_TRANSLATION_KEY));

        configSwitch.setOnSwitchChanged(switchValue -> ConfigUtil.setBoolean(path, switchValue));
        resetButton.setOnClick(event -> setBooleanConfigValue(path, configSwitch, defaultValue));

        container.addChildren(label, configSwitch, resetButton);
        configContainer.addChild(container);
        configContainer.addChild(new UIElement().addClass("config-divider"));
    }

    private static void setIntConfigValue(String path, Scroller scroller, TextField valueField, int value, int minValue, int maxValue) {
        int intValue = Math.clamp(value, minValue, maxValue);
        if(scroller.getValue() != intValue) {
            scroller.setValue((float)intValue, false);
        }
        valueField.setText(Integer.toString(intValue), false);
        ConfigUtil.setInt(path, intValue);
    }

    private static void setFloatConfigValue(String path, Scroller scroller, TextField valueField, float value, float minValue, float maxValue) {
        float floatValue = Math.clamp(value, minValue, maxValue);
        if(scroller.getValue() != floatValue) {
            scroller.setValue(floatValue, false);
        }
        valueField.setText(formatFloat(floatValue), false);
        ConfigUtil.setFloat(path, floatValue);
    }

    private static void setDoubleConfigValue(String path, Scroller scroller, TextField valueField, double value, double minValue, double maxValue) {
        double doubleValue = Math.clamp(value, minValue, maxValue);
        if(scroller.getValue() != (float)doubleValue) {
            scroller.setValue((float)doubleValue, false);
        }
        valueField.setText(formatDouble(doubleValue), false);
        ConfigUtil.setDouble(path, doubleValue);
    }

    private static void setStringConfigValue(String path, TextField textField, String value) {
        textField.setText(value, false);
        ConfigUtil.setString(path, value);
    }

    private static void setBooleanConfigValue(String path, Switch configSwitch, boolean value) {
        configSwitch.setOn(value, false);
        ConfigUtil.setBoolean(path, value);
    }

    private static void disableScrollerWheel(Scroller scroller) {
        scroller.scrollerStyle(style -> style.scrollDelta(0));
    }

    private static void disableTextFieldWheel(TextField textField) {
        textField.setWheelDur(0);
    }

    private static void applyLabelTooltip(Label label, Component tooltip) {
        if(tooltip != null) {
            label.style(style -> style.tooltips(tooltip));
        }
    }

    private static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch(NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private static float parseFloat(String value, float defaultValue) {
        try {
            return Float.parseFloat(value);
        } catch(NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private static double parseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch(NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private static String formatFloat(float value) {
        return Float.toString(value);
    }

    private static String formatDouble(double value) {
        return Double.toString(value);
    }
}
