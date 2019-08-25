package com.gideon.tools.debug.util;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.impl.ContentImpl;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yixianhai
 * @date 2019-08-13
 */
public class ToolWindowUtil {

    private static ToolWindowUtil instance = new ToolWindowUtil();

    private Map<Project, ToolWindow> toolWindowMap = new HashMap<>();

    private Map<Project, Map<String, ConsoleView>> consoleViewMap = new HashMap<>();

    private static final String REMOTE_DEBUG_WINDOW = "RemoteDebug";

    private ToolWindowUtil() {
    }

    public static ToolWindowUtil getInstance() {
        return instance;
    }

    public ConsoleView showConsoleToolWindow(final Project project, String displayName) {
        Map<String, ConsoleView> map = consoleViewMap.get(project);
        if (map == null) {
            map = new HashMap<>();
            consoleViewMap.put(project, map);
        }
        ConsoleView view = map.get(displayName);
        if (view != null) {
            return view;
        }
        ToolWindow toolWindow = toolWindowMap.get(project);
        if (toolWindow == null) {
            toolWindow = createNewToolWindow(project, REMOTE_DEBUG_WINDOW);
            toolWindowMap.put(project, toolWindow);
        }
        Content content = toolWindow.getContentManager().findContent(displayName);
        if (content == null) {
            ConsoleView consoleView = new ConsoleViewImpl(project, false);
            content = createConsoleContent(consoleView, displayName);
            toolWindow.getContentManager().addContent(content);
            toolWindow.getContentManager().setSelectedContent(content);
            map.put(displayName, consoleView);
        }
        toolWindow.getContentManager().setSelectedContent(content);
        return map.get(displayName);
    }

    private ToolWindow createNewToolWindow(final Project project, String title) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(title);
        if (toolWindow == null) {
            toolWindow = ToolWindowManager.getInstance(project).registerToolWindow(
                    title,
                    true,
                    ToolWindowAnchor.BOTTOM
            );
        }
        return toolWindow;
    }

    private Content createConsoleContent(final ConsoleView console, final String title) {
        final Content content = new ContentImpl(createConsolePanel(console), title, true);
        content.setTabName(title);
        content.setDisplayName(title);
        return content;
    }

    private JComponent createConsolePanel(ConsoleView view) {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(view.getComponent(), BorderLayout.CENTER);
        return panel;
    }

}
