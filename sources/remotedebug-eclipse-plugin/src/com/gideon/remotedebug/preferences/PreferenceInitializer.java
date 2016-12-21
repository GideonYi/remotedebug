package com.gideon.remotedebug.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.gideon.remotedebug.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_BOOLEAN, false);
		store.setDefault(PreferenceConstants.P_PATH, "E:\\developTools\\eclipse-jee-kepler-SR2-win32-x86_64\\eclipse\\artifacts.xml");
		store.setDefault(PreferenceConstants.P_DEBUG_FLAG, true);
		store.setDefault(PreferenceConstants.P_DEBUG_PATH, "http://localhost:8082/WebDemo/debug.jsp");
	}

}
