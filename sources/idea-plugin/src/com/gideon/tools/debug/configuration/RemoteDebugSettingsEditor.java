package com.gideon.tools.debug.configuration;

import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Anna Bulenkova
 */

/**
 * @author Anna Bulenkova
 */
public class RemoteDebugSettingsEditor extends SettingsEditor<RemoteDebugConfiguration> {
    private JPanel myPanel;
    private JTextField debugServerUrl;
    private JTextField classPath;
    private JCheckBox isDebug;
    private JCheckBox isTest;
    private LabeledComponent<ComponentWithBrowseButton> myMainClass;

    @Override
    protected void resetEditorFrom(RemoteDebugConfiguration configuration) {
        debugServerUrl.setText(configuration.getServerUrl());
        classPath.setText(configuration.getClassPath());
        isDebug.setSelected(configuration.isDebug);
    }

    @Override
    protected void applyEditorTo(RemoteDebugConfiguration configuration) {
        configuration.serverUrl = debugServerUrl.getText();
        configuration.classPath = classPath.getText();
        configuration.isDebug = isDebug.isSelected();
        configuration.isTest = isTest.isSelected();
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return myPanel;
    }

    private void createUIComponents() {
        myMainClass = new LabeledComponent<>();
        myMainClass.setComponent(new TextFieldWithBrowseButton());
    }
}

