package kr.pe.maun.csdgenerator.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import kr.pe.maun.csdgenerator.CSDGeneratorPlugin;

public class CSDGeneratorPreferencePage	extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public CSDGeneratorPreferencePage() {
		super(GRID);
		setPreferenceStore(CSDGeneratorPlugin.getDefault().getPreferenceStore());
	}

	private StringFieldEditor company;
	private StringFieldEditor author;

	private BooleanFieldEditor createControllerFolder;
	private BooleanFieldEditor addPrefixControllerFolder;

	private BooleanFieldEditor createServiceFolder;
	private BooleanFieldEditor addPrefixServiceFolder;

	private BooleanFieldEditor createServiceImplFile;
	private BooleanFieldEditor createServiceImplFolder;
	private FileFieldEditor serviceimplTemplateFile;

	private BooleanFieldEditor createDaoFolder;
	private BooleanFieldEditor addPrefixDaoFolder;

	private BooleanFieldEditor createMapper;
	private DirectoryFieldEditor mapperPath;
	private FileFieldEditor mapperTemplateFile;

	private BooleanFieldEditor createJsp;
	private DirectoryFieldEditor jspPath;
	private FileFieldEditor jspTemplateFile;

	public void createFieldEditors() {

		IPreferenceStore store = getPreferenceStore();

		company = new StringFieldEditor(PreferenceConstants.CSDGENERATOR_COMPANY, "Company:", getFieldEditorParent());
		addField(company);

		author = new StringFieldEditor(PreferenceConstants.CSDGENERATOR_AUTHOR, "Author:", getFieldEditorParent());
		addField(author);

		createControllerFolder = new BooleanFieldEditor(PreferenceConstants.CSDGENERATOR_CREATE_CONTROLLER_FOLDER, "Create controller folder", getFieldEditorParent());
		addField(createControllerFolder);

		addPrefixControllerFolder = new BooleanFieldEditor(PreferenceConstants.CSDGENERATOR_ADD_PREFIX_CONTROLLER_FOLDER, "Add prefix controller folder name", getFieldEditorParent());
		if(!store.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_CONTROLLER_FOLDER))
			addPrefixControllerFolder.setEnabled(false, getFieldEditorParent());
		addField(addPrefixControllerFolder);

		createServiceFolder = new BooleanFieldEditor(PreferenceConstants.CSDGENERATOR_CREATE_SERVICE_FOLDER, "Create service folder", getFieldEditorParent());
		addField(createServiceFolder);

		addPrefixServiceFolder = new BooleanFieldEditor(PreferenceConstants.CSDGENERATOR_ADD_PREFIX_SERVICE_FOLDER, "Add prefix service folder name", getFieldEditorParent());
		addField(addPrefixServiceFolder);
		if(!store.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_SERVICE_FOLDER))
			addPrefixServiceFolder.setEnabled(false, getFieldEditorParent());

		createServiceImplFile = new BooleanFieldEditor(PreferenceConstants.CSDGENERATOR_CREATE_SERVICEIMPL, "Create ServiceImpl", getFieldEditorParent());
		addField(createServiceImplFile);

		createServiceImplFolder = new BooleanFieldEditor(PreferenceConstants.CSDGENERATOR_CREATE_SERVICEIMPL_FOLDER, "Create ServiceImpl folder", getFieldEditorParent());
		addField(createServiceImplFolder);
		if(!store.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_SERVICEIMPL))
			createServiceImplFolder.setEnabled(false, getFieldEditorParent());

		addField(serviceimplTemplateFile);
		if(!store.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_SERVICEIMPL))
			serviceimplTemplateFile.setEnabled(false, getFieldEditorParent());

		createDaoFolder = new BooleanFieldEditor(PreferenceConstants.CSDGENERATOR_CREATE_DAO_FOLDER, "Create dao folder", getFieldEditorParent());
		addField(createDaoFolder);

		addPrefixDaoFolder = new BooleanFieldEditor(PreferenceConstants.CSDGENERATOR_ADD_PREFIX_DAO_FOLDER, "Add prefix dao folder name", getFieldEditorParent());
		addField(addPrefixDaoFolder);
		if(!store.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_DAO_FOLDER))
			addPrefixDaoFolder.setEnabled(false, getFieldEditorParent());

		createMapper = new BooleanFieldEditor(PreferenceConstants.CSDGENERATOR_CREATE_MAPPER, "Create Mapper", getFieldEditorParent());
		addField(createMapper);

		mapperPath = new DirectoryFieldEditor(PreferenceConstants.CSDGENERATOR_MAPPER_PATH, "Mapper Path:", getFieldEditorParent());
		addField(mapperPath);
		if(!store.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_MAPPER))
			mapperPath.setEnabled(false, getFieldEditorParent());

		addField(mapperTemplateFile);
		if(!store.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_MAPPER))
			mapperTemplateFile.setEnabled(false, getFieldEditorParent());

		createJsp = new BooleanFieldEditor(PreferenceConstants.CSDGENERATOR_CREATE_JSP, "Create Jsp", getFieldEditorParent());
		addField(createJsp);

		jspPath = new DirectoryFieldEditor(PreferenceConstants.CSDGENERATOR_JSP_PATH, "Jsp Path:", getFieldEditorParent());
		addField(jspPath);
		if(!store.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_JSP))
			jspPath.setEnabled(false, getFieldEditorParent());

		jspTemplateFile = new FileFieldEditor(PreferenceConstants.CSDGENERATOR_JSP_TEMPLATE_FILE, "Jsp Template File:", getFieldEditorParent());
		addField(jspTemplateFile);
		if(!store.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_JSP))
			jspTemplateFile.setEnabled(false, getFieldEditorParent());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);

		if(event.getProperty().equals(FieldEditor.VALUE)) {
			if(event.getSource() == createControllerFolder) {
				if(createControllerFolder.getBooleanValue()) {
					addPrefixControllerFolder.setEnabled(true, getFieldEditorParent());
				} else {
					addPrefixControllerFolder.setEnabled(false, getFieldEditorParent());
				}
			} else if(event.getSource() == createServiceFolder) {
				if(createServiceFolder.getBooleanValue()) {
					addPrefixServiceFolder.setEnabled(true, getFieldEditorParent());
				} else {
					addPrefixServiceFolder.setEnabled(false, getFieldEditorParent());
				}
			} else if(event.getSource() == createServiceImplFile) {
				if(createServiceImplFile.getBooleanValue()) {
					createServiceImplFolder.setEnabled(true, getFieldEditorParent());
					serviceimplTemplateFile.setEnabled(true, getFieldEditorParent());
				} else {
					createServiceImplFolder.setEnabled(false, getFieldEditorParent());
					serviceimplTemplateFile.setEnabled(false, getFieldEditorParent());
				}
			} else if(event.getSource() == createDaoFolder) {
				if(createDaoFolder.getBooleanValue()) {
					addPrefixDaoFolder.setEnabled(true, getFieldEditorParent());
				} else {
					addPrefixDaoFolder.setEnabled(false, getFieldEditorParent());
				}
			} else if(event.getSource() == createMapper) {
				if(createMapper.getBooleanValue()) {
					mapperPath.setEnabled(true, getFieldEditorParent());
					mapperTemplateFile.setEnabled(true, getFieldEditorParent());
				} else {
					mapperPath.setEnabled(false, getFieldEditorParent());
					mapperTemplateFile.setEnabled(false, getFieldEditorParent());
				}
			} else if(event.getSource() == createJsp) {
				if(createJsp.getBooleanValue()) {
					jspPath.setEnabled(true, getFieldEditorParent());
					jspTemplateFile.setEnabled(true, getFieldEditorParent());
				} else {
					jspPath.setEnabled(false, getFieldEditorParent());
					jspTemplateFile.setEnabled(false, getFieldEditorParent());
				}
			}
		}
	}


}