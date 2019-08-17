package com.gideon.tools.debug.configuration;

import com.intellij.openapi.options.ConfigurationException;
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
    private JCheckBox isDebug;
    private JTextField classPath;
    private LabeledComponent<ComponentWithBrowseButton> myMainClass;

    @Override
    protected void resetEditorFrom(RemoteDebugConfiguration configuration) {
        debugServerUrl.setText(configuration.getServerUrl());
        classPath.setText(configuration.getClassPath());
        isDebug.setSelected(configuration.isDebug);
    }

    @Override
    protected void applyEditorTo(RemoteDebugConfiguration configuration) throws ConfigurationException {
        configuration.serverUrl = debugServerUrl.getText();
        configuration.isDebug = isDebug.isSelected();
        configuration.classPath = classPath.getText();
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return myPanel;
    }

    private void createUIComponents() {
        myMainClass = new LabeledComponent<ComponentWithBrowseButton>();
        myMainClass.setComponent(new TextFieldWithBrowseButton());
    }
}

