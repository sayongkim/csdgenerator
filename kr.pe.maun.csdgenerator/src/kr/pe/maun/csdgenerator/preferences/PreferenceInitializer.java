package kr.pe.maun.csdgenerator.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import kr.pe.maun.csdgenerator.CSDGeneratorPlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = CSDGeneratorPlugin.getDefault().getPreferenceStore();

		store.setDefault(PreferenceConstants.CSDGENERATOR_COMPANY, "");
		store.setDefault(PreferenceConstants.CSDGENERATOR_AUTHOR, "");

		store.setDefault(PreferenceConstants.CSDGENERATOR_CREATE_CONTROLLER_FOLDER, false);
		store.setDefault(PreferenceConstants.CSDGENERATOR_ADD_PREFIX_CONTROLLER_FOLDER, false);
		store.setDefault(PreferenceConstants.CSDGENERATOR_CONTROLLER_TEMPLATE_FILE, "");

		store.setDefault(PreferenceConstants.CSDGENERATOR_CREATE_SERVICE_FOLDER, true);
		store.setDefault(PreferenceConstants.CSDGENERATOR_ADD_PREFIX_SERVICE_FOLDER, true);
		store.setDefault(PreferenceConstants.CSDGENERATOR_SERVICE_TEMPLATE_FILE, "");

		store.setDefault(PreferenceConstants.CSDGENERATOR_CREATE_SERVICEIMPL, false);
		store.setDefault(PreferenceConstants.CSDGENERATOR_CREATE_SERVICEIMPL_FOLDER, false);
		store.setDefault(PreferenceConstants.CSDGENERATOR_SERVICEIMPL_TEMPLATE_FILE, "");

		store.setDefault(PreferenceConstants.CSDGENERATOR_CREATE_DAO_FOLDER, true);
		store.setDefault(PreferenceConstants.CSDGENERATOR_ADD_PREFIX_DAO_FOLDER, true);
		store.setDefault(PreferenceConstants.CSDGENERATOR_DAO_TEMPLATE_FILE, "");

		store.setDefault(PreferenceConstants.CSDGENERATOR_CREATE_MAPPER, false);
		store.setDefault(PreferenceConstants.CSDGENERATOR_MAPPER_PATH, "");
		store.setDefault(PreferenceConstants.CSDGENERATOR_MAPPER_TEMPLATE_FILE, "");
	}

}
