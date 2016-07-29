package edu.uncc.sis.aside.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.PluginConstants;
import edu.uncc.sis.aside.AsidePlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Plugin.getDefault().getPreferenceStore();

		store.setDefault(PluginConstants.ASIDE_TB_PREFERENCE, true);
		store.setDefault(PluginConstants.ASIDE_VR_PREFERENCE, true);
		store.setDefault(PluginConstants.PROJECT_TB_PREFERENCE, false);
		store.setDefault(PluginConstants.PROJECT_VR_PREFERENCE, false);
		store.setDefault(PluginConstants.EXTERNAL_TB_PREFERENCE, false);
		store.setDefault(PluginConstants.EXTERNAL_VR_PREFERENCE, false);
		store.setDefault(PluginConstants.EXTERNAL_TB_PATH_PREFERENCE,
				PluginConstants.EXTERNAL_TB_PATH_PREFERENCE_DEFAULT);
		store.setDefault(PluginConstants.EXTERNAL_VR_PATH_PREFERENCE,
				PluginConstants.EXTERNAL_VR_PATH_PREFERENCE_DEFAULT);

	}

}
