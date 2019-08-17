// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.gideon.tools.debug.state;

import com.gideon.tools.debug.configuration.RemoteDebugConfiguration;
import com.gideon.tools.debug.util.HttpUtils;
import com.gideon.tools.debug.util.ToolWindowUtil;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.compiler.CompilerPaths;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author lex
 */
public class RemoteStateState implements RunProfileState {
    private final Project myProject;

    private final RemoteDebugConfiguration configuration;

    private final ToolWindowUtil toolWindowUtil = ToolWindowUtil.getInstance();


    public RemoteStateState(Project project, RemoteDebugConfiguration configuration) {
        myProject = project;
        this.configuration = configuration;
    }

    @Override
    public ExecutionResult execute(final Executor executor, @NotNull final ProgramRunner runner) throws ExecutionException {

        String serverUrl = configuration.getServerUrl();
        String classPath = configuration.getClassPath();
        boolean debug = configuration.isDebug();
        JavaPsiFacade facade = JavaPsiFacade.getInstance(myProject);
        PsiClass aClass = facade.findClass(classPath, GlobalSearchScope.projectScope(myProject));
        if (aClass == null) {
            String msg = String.format("class: %s not found!", classPath);
            Messages.showMessageDialog(myProject, msg, "Error", Messages.getInformationIcon());
            return null;
        }
        Module module = ModuleUtil.findModuleForPsiElement(aClass);
        String moduleOutputPath = CompilerPaths.getModuleOutputPath(module, false);
        String filePath = classPath.replace(".", "/");
        String classFilePath = String.format("%s/%s.class", moduleOutputPath, filePath);
        File file = FileUtils.getFile(classFilePath);
        if (!file.exists()) {
            String msg = String.format("class file path: {} not exists!", classFilePath);
            Messages.showMessageDialog(myProject, msg, "Error Message", Messages.getInformationIcon());
        }
        ConsoleView consoleView = toolWindowUtil.showConsoleToolWindow(myProject, configuration.getName());
        String debugMsg = String.format("server url:%s\nclass path: %s\nclass file path: %s\n", serverUrl, classPath, classFilePath);
        consoleView.print(debugMsg, ConsoleViewContentType.SYSTEM_OUTPUT);
        consoleView.print("=========== remote output ===========\n", ConsoleViewContentType.SYSTEM_OUTPUT);

        InputStream is = null;
        try {
            is = new FileInputStream(classFilePath);
            byte[] b = new byte[is.available()];
            is.read(b);
            is.close();
            String classValue = new String(b, "iso-8859-1");
            String result = HttpUtils.postRequest(serverUrl, classValue);
            consoleView.print(result, ConsoleViewContentType.SYSTEM_OUTPUT);
            consoleView.print("\n", ConsoleViewContentType.SYSTEM_OUTPUT);
        } catch (Exception e) {
            consoleView.print(e.getMessage(), ConsoleViewContentType.SYSTEM_OUTPUT);
            consoleView.print("\n", ConsoleViewContentType.SYSTEM_OUTPUT);
        }

        return null;
    }

}
