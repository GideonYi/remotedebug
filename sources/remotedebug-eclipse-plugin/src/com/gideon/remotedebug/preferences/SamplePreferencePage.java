package com.gideon.remotedebug.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.gideon.remotedebug.Activator;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class SamplePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	private BooleanFieldEditor flagEditor;
	private FieldEditor pathEditor;

	public SamplePreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("A demonstration of a preference page implementation");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		BooleanFieldEditor booleanEditor = new BooleanFieldEditor(PreferenceConstants.P_BOOLEAN, "&Use the customize path of the class", getFieldEditorParent());
		this.flagEditor=booleanEditor;
		addField(booleanEditor);
		FileFieldEditor editor = new FileFieldEditor(PreferenceConstants.P_PATH, "Choose the class to remote debug", getFieldEditorParent());
		this.pathEditor=editor;
		addField(editor);
		addField(new BooleanFieldEditor(PreferenceConstants.P_DEBUG_FLAG, "Flag to show the debug message ", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_DEBUG_PATH, "Set the remote debug URL", getFieldEditorParent()));
	}
	
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		if(this.flagEditor==event.getSource()){
			boolean enabled=(boolean)event.getNewValue();
			this.pathEditor.setEnabled(enabled, getFieldEditorParent());
		}
	};

	@Override
	protected void initialize() {
		super.initialize();
		this.pathEditor.setEnabled(flagEditor.getBooleanValue(), getFieldEditorParent());
	};
	
	@Override
	public void init(IWorkbench workbench) {
		
	}

}