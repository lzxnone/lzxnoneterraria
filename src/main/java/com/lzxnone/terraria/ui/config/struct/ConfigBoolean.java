package com.lzxnone.terraria.ui.config.struct;

import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigUtil;

public class ConfigBoolean extends ConfigStruct<Boolean> {
    public final boolean defaultValue;

    public ConfigBoolean(String path, boolean defaultValue) {
        super(path);
        this.defaultValue = defaultValue;
    }

    public ConfigBoolean(String path, String translationKey, boolean defaultValue) {
        super(path, translationKey);
        this.defaultValue = defaultValue;
    }

    @Override
    public Boolean get() {
        return ConfigUtil.readBoolean(path, defaultValue);
    }

    @Override
    public void load() {
        ConfigFactory.loadBooleanConfig(path, configText(), configTooltip(), defaultValue);
    }
}
