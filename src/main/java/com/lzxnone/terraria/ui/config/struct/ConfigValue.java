package com.lzxnone.terraria.ui.config.struct;

public abstract class ConfigValue<T extends Number> extends ConfigStruct<T> {
    public final T defaultValue;
    public final T minValue;
    public final T maxValue;

    protected ConfigValue(String path, T defaultValue, T minValue, T maxValue) {
        super(path);
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    protected ConfigValue(String path, String translationKey, T defaultValue, T minValue, T maxValue) {
        super(path, translationKey);
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
}
