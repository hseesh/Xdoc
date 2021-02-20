package com.yatoufang.config;

import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

/**
 * @Auther: hse
 * @Date: 2021/2/3
 */
public class AppSettingsComponent {

    private final JPanel myMainPanel;
    private final JCheckBox requestExample = new JCheckBox("Remove Request-Example");
    private final JCheckBox responseFields = new JCheckBox("Remove Response-Fields");
    private final JCheckBox responseExample = new JCheckBox("Remove Response-Example");

    public AppSettingsComponent() {

        myMainPanel = FormBuilder.createFormBuilder()
                .addComponent(new JBTextField("Custom Your Document Template"), 1)
                .addComponent(requestExample, 1)
                .addComponent(responseFields, 1)
                .addComponent(responseExample, 1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }


    public boolean getRequestExampleSate() {
        return requestExample.isSelected();
    }

    public boolean getResponseFieldsState() {
        return responseFields.isSelected();
    }

    public boolean getResponseExampleState() {
        return responseExample.isSelected();
    }

    public void setRequestExampleState(boolean flag){
        requestExample.setSelected(flag);
    }

    public void setResponseFieldsState(boolean flag){
        responseFields.setSelected(flag);
    }

    public void setResponseExampleState(boolean flag){
        responseExample.setSelected(flag);
    }


}
