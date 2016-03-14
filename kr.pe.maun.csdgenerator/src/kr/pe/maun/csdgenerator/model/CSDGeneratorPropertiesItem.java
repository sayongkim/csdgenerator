package kr.pe.maun.csdgenerator.model;

import kr.pe.maun.csdgenerator.CSDGeneratorPlugin;
import kr.pe.maun.csdgenerator.preferences.PreferenceConstants;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.IPreferenceStore;

public class CSDGeneratorPropertiesItem implements ICSDGeneratorPropertiesItem {

	public static final QualifiedName PROJECT_SPECIFIC_SETTINGS = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_PROJECT_SPECIFIC_SETTINGS);

	public static final QualifiedName CREATE_CONTROLLER_FOLDER = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_CREATE_CONTROLLER_FOLDER);
	public static final QualifiedName ADD_PREFIX_CONTROLLER_FOLDER = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_ADD_PREFIX_CONTROLLER_FOLDER);
	public static final QualifiedName CONTROLLER_TEMPLATE_FILE = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_CONTROLLER_TEMPLATE_FILE);

	public static final QualifiedName CREATE_SERVICE_FOLDER = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_CREATE_SERVICE_FOLDER);
	public static final QualifiedName ADD_PREFIX_SERVICE_FOLDER = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_ADD_PREFIX_SERVICE_FOLDER);
	public static final QualifiedName SERVICE_TEMPLATE_FILE = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_SERVICE_TEMPLATE_FILE);

	public static final QualifiedName CREATE_SERVICEIMPL = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_CREATE_SERVICEIMPL);
	public static final QualifiedName CREATE_SERVICEIMPL_FOLDER = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_CREATE_SERVICEIMPL_FOLDER);
	public static final QualifiedName SERVICEIMPL_TEMPLATE_FILE = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_SERVICEIMPL_TEMPLATE_FILE);

	public static final QualifiedName CREATE_DAO_FOLDER = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_CREATE_DAO_FOLDER);
	public static final QualifiedName ADD_PREFIX_DAO_FOLDER = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_ADD_PREFIX_DAO_FOLDER);
	public static final QualifiedName DAO_TEMPLATE_FILE = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_DAO_TEMPLATE_FILE);

	public static final QualifiedName CREATE_MAPPER = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_CREATE_MAPPER);
	public static final QualifiedName MAPPER_PATH = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_MAPPER_PATH);
	public static final QualifiedName MAPPER_TEMPLATE_FILE = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_MAPPER_TEMPLATE_FILE);

	public static final QualifiedName CREATE_JSP = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_CREATE_JSP);
	public static final QualifiedName JSP_PATH = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_JSP_PATH);
	public static final QualifiedName JSP_TEMPLATE_FILE = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_JSP_TEMPLATE_FILE);

	String projectSpecificSettings;

	private boolean createControllerFolder;
	private boolean addPrefixControllerFolder;
	private String controllerTemplateFile;

	private boolean createServiceFolder;
	private boolean addPrefixServiceFolder;
	private String serviceTemplateFile;

	private boolean createServiceImpl;
	private boolean createServiceImplFolder;
	private String serviceImplTemplateFile;

	private boolean createDaoFolder;
	private boolean addPrefixDaoFolder;
	private String daoTemplateFile;

	private boolean createMapper;
	private String mapperPath;
	private String mapperTemplateFile;

	private boolean createJsp;
	private String jspPath;
	private String jspTemplateFile;

	public CSDGeneratorPropertiesItem(IResource resource) {
		super();

		String projectSpecificSettings = "";
		IPreferenceStore preferenceStore = CSDGeneratorPlugin.getDefault().getPreferenceStore();

		try {
			projectSpecificSettings = resource.getPersistentProperty(CSDGeneratorPropertiesItem.PROJECT_SPECIFIC_SETTINGS);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		if("true".equals(projectSpecificSettings)) {

			try {
				createControllerFolder = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_CONTROLLER_FOLDER));
				addPrefixControllerFolder = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.ADD_PREFIX_CONTROLLER_FOLDER));

				createServiceFolder = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SERVICE_FOLDER));
				addPrefixServiceFolder = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.ADD_PREFIX_SERVICE_FOLDER));
				createServiceImpl = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SERVICEIMPL));
				createServiceImplFolder = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SERVICEIMPL_FOLDER));

				createDaoFolder = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_DAO_FOLDER));
				addPrefixDaoFolder = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.ADD_PREFIX_DAO_FOLDER));

				controllerTemplateFile = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CONTROLLER_TEMPLATE_FILE);
				serviceTemplateFile = resource.getPersistentProperty(CSDGeneratorPropertiesItem.SERVICE_TEMPLATE_FILE);
				serviceImplTemplateFile = resource.getPersistentProperty(CSDGeneratorPropertiesItem.SERVICEIMPL_TEMPLATE_FILE);
				daoTemplateFile = resource.getPersistentProperty(CSDGeneratorPropertiesItem.DAO_TEMPLATE_FILE);

				createMapper = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_MAPPER));
				mapperPath = resource.getPersistentProperty(CSDGeneratorPropertiesItem.MAPPER_PATH);
				mapperTemplateFile = resource.getPersistentProperty(CSDGeneratorPropertiesItem.MAPPER_TEMPLATE_FILE);

				createJsp = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_JSP));
				jspPath = resource.getPersistentProperty(CSDGeneratorPropertiesItem.JSP_PATH);
				jspTemplateFile = resource.getPersistentProperty(CSDGeneratorPropertiesItem.JSP_TEMPLATE_FILE);

			} catch (CoreException e) {
				e.printStackTrace();
			}

		} else {
			createControllerFolder = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_CONTROLLER_FOLDER);
			addPrefixControllerFolder = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_ADD_PREFIX_CONTROLLER_FOLDER);

			createServiceFolder = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_SERVICE_FOLDER);
			addPrefixServiceFolder = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_ADD_PREFIX_SERVICE_FOLDER);
			createServiceImpl = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_SERVICEIMPL);
			createServiceImplFolder = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_SERVICEIMPL_FOLDER);

			createDaoFolder = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_DAO_FOLDER);
			addPrefixDaoFolder = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_ADD_PREFIX_DAO_FOLDER);

			controllerTemplateFile = preferenceStore.getString(PreferenceConstants.CSDGENERATOR_CONTROLLER_TEMPLATE_FILE);
			serviceTemplateFile = preferenceStore.getString(PreferenceConstants.CSDGENERATOR_SERVICE_TEMPLATE_FILE);
			serviceImplTemplateFile = preferenceStore.getString(PreferenceConstants.CSDGENERATOR_SERVICEIMPL_TEMPLATE_FILE);
			daoTemplateFile = preferenceStore.getString(PreferenceConstants.CSDGENERATOR_DAO_TEMPLATE_FILE);

			createMapper = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_MAPPER);
			mapperPath = preferenceStore.getString(PreferenceConstants.CSDGENERATOR_MAPPER_PATH);
			mapperTemplateFile = preferenceStore.getString(PreferenceConstants.CSDGENERATOR_MAPPER_TEMPLATE_FILE);

			createJsp = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_JSP);
			jspPath = preferenceStore.getString(PreferenceConstants.CSDGENERATOR_JSP_PATH);
			jspTemplateFile = preferenceStore.getString(PreferenceConstants.CSDGENERATOR_JSP_TEMPLATE_FILE);
		}
	}

	@Override
	public boolean getCreateControllerFolder() {
		return this.createControllerFolder;
	}

	@Override
	public void setCreateControllerFolder(boolean createControllerFolder) {
		this.createControllerFolder = createControllerFolder;
	}

	@Override
	public boolean getAddPrefixControllerFolder() {
		return this.addPrefixControllerFolder;
	}

	@Override
	public void setAddPrefixControllerFolder(boolean addPrefixControllerFolder) {
		this.addPrefixControllerFolder = addPrefixControllerFolder;
	}

	@Override
	public String getControllerTemplateFile() {
		return this.controllerTemplateFile;
	}

	@Override
	public void setControllerTemplateFile(String controllerTemplateFile) {
		this.controllerTemplateFile = controllerTemplateFile;
	}

	@Override
	public boolean getCreateServiceFolder() {
		return this.createServiceFolder;
	}

	@Override
	public void setCreateServiceFolder(boolean createServiceFolder) {
		this.createServiceFolder = createServiceFolder;
	}

	@Override
	public boolean getAddPrefixServiceFolder() {
		return this.addPrefixServiceFolder;
	}

	@Override
	public void setAddPrefixServiceFolder(boolean addPrefixServiceFolder) {
		this.addPrefixServiceFolder = addPrefixServiceFolder;
	}

	@Override
	public String getServiceTemplateFile() {
		return this.serviceTemplateFile;
	}

	@Override
	public void setServiceTemplateFile(String serviceTemplateFile) {
		this.serviceTemplateFile = serviceTemplateFile;
	}



	@Override
	public boolean getCreateServiceImpl() {
		return this.createServiceImpl;
	}

	@Override
	public void setCreateServiceImpl(boolean createServiceImpl) {
		this.createServiceImpl = createServiceImpl;
	}



	@Override
	public String getServiceImplTemplateFile() {
		return this.serviceImplTemplateFile;
	}

	@Override
	public void setServiceImplTemplateFile(String serviceImplTemplateFile) {
		this.serviceImplTemplateFile = serviceImplTemplateFile;
	}

	@Override
	public boolean getCreateServiceImplFolder() {
		return this.createServiceImplFolder;
	}

	@Override
	public void setCreateServiceImplFolder(boolean createServiceImplFolder) {
		this.createServiceImplFolder = createServiceImplFolder;
	}

	@Override
	public boolean getCreateDaoFolder() {
		return this.createDaoFolder;
	}

	@Override
	public void setCreateDaoFolder(boolean createDaoFolder) {
		this.createDaoFolder = createDaoFolder;
	}

	@Override
	public boolean getAddPrefixDaoFolder() {
		return this.addPrefixDaoFolder;
	}

	@Override
	public void setAddPrefixDaoFolder(boolean addPrefixDaoFolder) {
		this.addPrefixDaoFolder = addPrefixDaoFolder;
	}

	@Override
	public String getDaoTemplateFile() {
		return this.daoTemplateFile;
	}

	@Override
	public void setDaoTemplateFile(String daoTemplateFile) {
		this.daoTemplateFile = daoTemplateFile;
	}

	@Override
	public boolean getCreateMapper() {
		return createMapper;
	}

	@Override
	public void setCreateMapper(boolean createMapper) {
		this.createMapper = createMapper;
	}

	@Override
	public String getMapperPath() {
		return mapperPath;
	}

	@Override
	public void setMapperPath(String mapperPath) {
		this.mapperPath = mapperPath;
	}

	@Override
	public String getMapperTemplateFile() {
		return mapperTemplateFile;
	}

	@Override
	public void setMapperTemplateFile(String mapperTemplateFile) {
		this.mapperTemplateFile = mapperTemplateFile;
	}

	@Override
	public boolean getCreateJsp() {
		return createJsp;
	}

	@Override
	public void setCreateJsp(boolean createJsp) {
		this.createJsp = createJsp;
	}

	@Override
	public String getJspPath() {
		return jspPath;
	}

	@Override
	public void setJspPath(String jspPath) {
		this.jspPath = jspPath;
	}

	@Override
	public String getJspTemplateFile() {
		return jspTemplateFile;
	}

	@Override
	public void setJspTemplateFile(String jspTemplateFile) {
		this.jspTemplateFile = jspTemplateFile;
	}
}
