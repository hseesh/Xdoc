package com.yatoufang.config;


import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @Auther: hse
 * @Date: 2021/2/3 0003
 */
public class AppSettingsConfigurable implements Configurable {

    private AppSettingsComponent mySettingsComponent;

    public AppSettingsConfigurable() {

    }

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Custom Template";
    }


    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new AppSettingsComponent();
        AppSettingsState settings = AppSettingsState.getInstance();
        mySettingsComponent.setRequestExampleState(settings.requestExample);
        mySettingsComponent.setResponseFieldsState(settings.responseFields);
        mySettingsComponent.setResponseExampleState(settings.responseExample);
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettingsState settings = AppSettingsState.getInstance();
        boolean requestExampleSate = mySettingsComponent.getRequestExampleSate();
        boolean responseFieldsState = mySettingsComponent.getResponseFieldsState();
        boolean responseExampleState = mySettingsComponent.getResponseExampleState();
        return !requestExampleSate == settings.requestExample || !responseFieldsState == settings.responseFields || !settings.responseExample == responseExampleState;
    }

    @Override
    public void apply() {
        AppSettingsState settings = AppSettingsState.getInstance();
        settings.requestExample = mySettingsComponent.getRequestExampleSate();
        settings.responseFields = mySettingsComponent.getResponseFieldsState();
        settings.responseExample = mySettingsComponent.getResponseExampleState();
    }


    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }


}

