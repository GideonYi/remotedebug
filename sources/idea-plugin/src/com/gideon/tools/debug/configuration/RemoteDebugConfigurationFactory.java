package com.gideon.tools.debug.configuration;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;

/**
 * @author yixianhai
 * @date 2019-08-12
 */
public class RemoteDebugConfigurationFactory extends ConfigurationFactory {
    private static final String FACTORY_NAME = "RemoteDebug configuration factory";

    protected RemoteDebugConfigurationFactory(ConfigurationType type) {
        super(type);
    }

    @Override
    public RunConfiguration createTemplateConfiguration(Project project) {
        return new RemoteDebugConfiguration(project, this, "RemoteDebug");
    }

    @Override
    public String getName() {
        return FACTORY_NAME;
    }
}
