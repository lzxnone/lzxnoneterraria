package com.lzxnone.terraria.ui.config.struct;

import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigUtil;

public class ConfigDouble extends ConfigValue<Double> {
    public ConfigDouble(String path, double defaultValue, double minValue, double maxValue) {
        super(path, defaultValue, minValue, maxValue);
    }

    public ConfigDouble(String path, String translationKey, double defaultValue, double minValue, double maxValue) {
        super(path, translationKey, defaultValue, minValue, maxValue);
    }

    @Override
    public Double get() {
        return Math.clamp(ConfigUtil.readDouble(path, defaultValue), minValue, maxValue);
    }

    @Override
    public void load() {
        ConfigFactory.loadDoubleConfig(path, configText(), configTooltip(), defaultValue, minValue, maxValue);
    }
}
