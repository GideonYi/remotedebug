package com.gideon.tools.debug.action;

import com.gideon.tools.debug.util.ToolWindowUtil;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.compiler.CompilerPaths;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

public class RemoteDebugAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        String title = "Remote Debug Info";
        //获取当前在操作的工程上下文
        Project project = e.getData(PlatformDataKeys.PROJECT);

        //获取当前操作的类文件
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        //获取当前类文件的路径
        VirtualFile virtualFile = psiFile.getVirtualFile();
        String srcClassPath = virtualFile.getPath();
        if (!srcClassPath.endsWith(".java")) {
            Messages.showMessageDialog(project, srcClassPath, title, Messages.getInformationIcon());
            return;
        }
        ToolWindowUtil consoleUtil = ToolWindowUtil.getInstance();
        ConsoleView consoleView = consoleUtil.showConsoleToolWindow(project, virtualFile.getName());
        consoleView.print("213\n", ConsoleViewContentType.SYSTEM_OUTPUT);
        consoleView.print(String.valueOf("abc\n"), ConsoleViewContentType.SYSTEM_OUTPUT);

        //显示对话框
        Module module = e.getData(LangDataKeys.MODULE);
        String moduleFilePath = module.getModuleFilePath();
        String moduleOutputPath = CompilerPaths.getModuleOutputPath(module, false);

        String msg = String.format("%s\n%s\n%s", srcClassPath, moduleFilePath, moduleOutputPath);
        Messages.showMessageDialog(project, msg, title, Messages.getInformationIcon());

    }
}
