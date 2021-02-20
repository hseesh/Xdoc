package com.yatoufang.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @Auther: hse
 * @Date: 2021/2/3
 */
@State(
        name = "com.yatoufang.config.AppSettingsState",
        storages = {@Storage("DocumentGeneratorSettings.xml")}
)
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {

    public boolean requestExample;
    public boolean responseFields;
    public boolean responseExample;

    public static AppSettingsState getInstance() {
        AppSettingsState service = ServiceManager.getService(AppSettingsState.class);
        if(service == null) return new AppSettingsState();
        return service;
    }

    @Nullable
    @Override
    public AppSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AppSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}
