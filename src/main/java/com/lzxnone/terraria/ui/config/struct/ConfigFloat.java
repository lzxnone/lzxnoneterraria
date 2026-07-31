package com.lzxnone.terraria.ui.config.struct;

import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigUtil;

public class ConfigFloat extends ConfigValue<Float> {
    public ConfigFloat(String path, float defaultValue, float minValue, float maxValue) {
        super(path, defaultValue, minValue, maxValue);
    }

    public ConfigFloat(String path, String translationKey, float defaultValue, float minValue, float maxValue) {
        super(path, translationKey, defaultValue, minValue, maxValue);
    }

    @Override
    public Float get() {
        return Math.clamp(ConfigUtil.readFloat(path, defaultValue), minValue, maxValue);
    }

    @Override
    public void load() {
        ConfigFactory.loadFloatConfig(path, configText(), configTooltip(), defaultValue, minValue, maxValue);
    }
}
