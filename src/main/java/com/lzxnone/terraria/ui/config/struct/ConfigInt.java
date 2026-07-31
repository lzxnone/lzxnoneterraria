package com.lzxnone.terraria.ui.config.struct;

import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigUtil;

public class ConfigInt extends ConfigValue<Integer> {
    public ConfigInt(String path, int defaultValue, int minValue, int maxValue) {
        super(path, defaultValue, minValue, maxValue);
    }

    public ConfigInt(String path, String translationKey, int defaultValue, int minValue, int maxValue) {
        super(path, translationKey, defaultValue, minValue, maxValue);
    }

    @Override
    public Integer get() {
        return Math.clamp(ConfigUtil.readInt(path, defaultValue), minValue, maxValue);
    }

    @Override
    public void load() {
        ConfigFactory.loadIntConfig(path, configText(), configTooltip(), defaultValue, minValue, maxValue);
    }
}
