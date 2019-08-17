package com.gideon.tools.debug.configuration;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.icons.AllIcons;
import com.intellij.icons.AllIcons.Actions;
import com.intellij.icons.AllIcons.General;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author yixianhai
 * @date 2019-08-12
 */
public class RemoteDebugConfigurationType implements ConfigurationType {
    @Override
    public String getDisplayName() {
        return "RemoteDebug";
    }

    @Override
    public String getConfigurationTypeDescription() {
        return "Remote Debug Configuration Type";
    }

    @Override
    public Icon getIcon() {
        return Actions.StartDebugger;
    }

    @NotNull
    @Override
    public String getId() {
        return "REMOTE_DEBUG_CONFIGURATION";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{new RemoteDebugConfigurationFactory(this)};
    }
}
