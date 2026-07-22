package com.lzxnone.terraria.ui.config;

public interface IConfigData {
    default void onConfigLoad() {}
    default void onConfigSave() {}
}
