package com.lzxnone.terraria.ui.config.struct;

import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigUtil;

public class ConfigString extends ConfigStruct<String> {
    public final String defaultValue;

    public ConfigString(String path, String defaultValue) {
        super(path);
        this.defaultValue = defaultValue;
    }

    public ConfigString(String path, String translationKey, String defaultValue) {
        super(path, translationKey);
        this.defaultValue = defaultValue;
    }

    @Override
    public String get() {
        return ConfigUtil.readString(path, defaultValue);
    }

    @Override
    public void load() {
        ConfigFactory.loadStringConfig(path, configText(), configTooltip(), defaultValue);
    }
}
