package com.gideon.remotedebug.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;

import com.gideon.remotedebug.Activator;
import com.gideon.remotedebug.preferences.PreferenceConstants;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SampleHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IPreferenceStore page = Activator.getDefault().getPreferenceStore();
		boolean usingCustomize = page.getBoolean(PreferenceConstants.P_BOOLEAN);
		boolean logFlag = page.getBoolean(PreferenceConstants.P_DEBUG_FLAG);
		String debugURL = page.getString(PreferenceConstants.P_DEBUG_PATH);
		String classPath = "";
		if (usingCustomize) {
			classPath = page.getString(PreferenceConstants.P_PATH);
		}
		else {
			IEditorInput currentEditor = HandlerUtil.getActiveEditorInput(event);
			// get current JavaElement
			IJavaElement javaElement = JavaUI.getEditorInputJavaElement(currentEditor);
			// get relative java src path
			String outputPath = javaElement.getPath().makeRelative().toFile().getPath();
			// get the project
			IJavaProject javaProject = javaElement.getJavaProject();
			String projectPath = javaProject.getProject().getLocation().makeAbsolute().toFile().getParent();
			String javaFilePath = projectPath + File.separator + outputPath;
			if(logFlag){
				Activator.getStream().println("project relative src path is: "+javaFilePath);
			}
			try {
				IPath projectOutput = javaProject.getOutputLocation();
				IClasspathEntry[] classpathEntries = javaProject.getRawClasspath();
				for (IClasspathEntry iClasspathEntry : classpathEntries) {
					if (IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind()) {
						String source = iClasspathEntry.getPath().makeRelative().toFile().getPath();
						IPath iPath = iClasspathEntry.getOutputLocation();
						if (iPath == null) {
							iPath = projectOutput;
						}
						String output = iPath.makeRelative().toFile().getPath();
						if(logFlag){
							Activator.getStream().println("source file is: " + source);
						}
						if (javaFilePath.contains(source)) {
							classPath = javaFilePath.replace(source, output).replace(".java", ".class");
							break;
						}
					}
				}
			}
			catch (JavaModelException e) {
				// TODO Auto-generated catch block
				Activator.getStream().println(e.getMessage());
				e.printStackTrace();
			}

		}
		try {
			if(logFlag){
				Activator.getStream().println("debug class path is:"+classPath);
			}
			InputStream is = new FileInputStream(classPath);
			byte[] b = new byte[is.available()];
			is.read(b);
			is.close();
			String classValue = new String(b, "iso-8859-1");
			String result = HttpUtils.postRequest(debugURL, classValue);
			Activator.getStream().println(result);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			Activator.getStream().println(e.getMessage());
			e.printStackTrace();
		}

		return null;
	}
}
