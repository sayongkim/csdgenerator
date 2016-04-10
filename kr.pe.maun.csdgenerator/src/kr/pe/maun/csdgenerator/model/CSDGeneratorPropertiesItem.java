package kr.pe.maun.csdgenerator.model;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.IPreferenceStore;

import kr.pe.maun.csdgenerator.CSDGeneratorPlugin;
import kr.pe.maun.csdgenerator.preferences.PreferenceConstants;

public class CSDGeneratorPropertiesItem implements ICSDGeneratorPropertiesItem {

	public static final QualifiedName PROJECT_SPECIFIC_SETTINGS = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_PROJECT_SPECIFIC_SETTINGS);

	public static final QualifiedName TYPE = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_TYPE);

	public static final QualifiedName COMPANY = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_COMPANY);
	public static final QualifiedName AUTHOR = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_AUTHOR);

	public static final QualifiedName DATABASE_CONNECTION_PROFILE_NAME = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_DATABASE_CONNECTION_PROFILE_NAME);

	public static final QualifiedName CREATE_CONTROLLER_FOLDER = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_CREATE_CONTROLLER_FOLDER);
	public static final QualifiedName ADD_PREFIX_CONTROLLER_FOLDER = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_ADD_PREFIX_CONTROLLER_FOLDER);
	public static final QualifiedName CREATE_CONTROLLER_SUB_FOLDER = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_CREATE_CONTROLLER_SUB_FOLDER);

	public static final QualifiedName CREATE_SERVICE_FOLDER = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_CREATE_SERVICE_FOLDER);
	public static final QualifiedName ADD_PREFIX_SERVICE_FOLDER = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_ADD_PREFIX_SERVICE_FOLDER);
	public static final QualifiedName CREATE_SERVICE_SUB_FOLDER = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_CREATE_SERVICE_SUB_FOLDER);

	public static final QualifiedName CREATE_SERVICEIMPL = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_CREATE_SERVICEIMPL);
	public static final QualifiedName CREATE_SERVICEIMPL_FOLDER = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_CREATE_SERVICEIMPL_FOLDER);

	public static final QualifiedName CREATE_DAO_FOLDER = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_CREATE_DAO_FOLDER);
	public static final QualifiedName ADD_PREFIX_DAO_FOLDER = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_ADD_PREFIX_DAO_FOLDER);
	public static final QualifiedName CREATE_DAO_SUB_FOLDER = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_CREATE_DAO_SUB_FOLDER);

	public static final QualifiedName CREATE_MAPPER = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_CREATE_MAPPER);
	public static final QualifiedName MAPPER_PATH = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_MAPPER_PATH);

	public static final QualifiedName CREATE_VO = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_CREATE_VO);
	public static final QualifiedName CREATE_SEARCH_VO = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_CREATE_SEARCH_VO);
	public static final QualifiedName VO_PATH = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_VO_PATH);
	public static final QualifiedName MYBATIS_SETTING_FILE = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_MYBATIS_SETTING_FILE);

	public static final QualifiedName CREATE_JSP = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_CREATE_JSP);
	public static final QualifiedName JSP_PATH = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_JSP_PATH);
	public static final QualifiedName JSP_TEMPLATE_FILE = new QualifiedName(CSDGeneratorPlugin.PLUGIN_ID, PreferenceConstants.CSDGENERATOR_JSP_TEMPLATE_FILE);

	String projectSpecificSettings;

	private String type;

	private String company;
	private String author;

	private String databaseConnectionProfileName;

	private boolean createControllerFolder;
	private boolean addPrefixControllerFolder;
	private boolean createControllerSubFolder;

	private boolean createServiceFolder;
	private boolean addPrefixServiceFolder;
	private boolean createServiceSubFolder;

	private boolean createServiceImpl;
	private boolean createServiceImplFolder;

	private boolean createDaoFolder;
	private boolean addPrefixDaoFolder;
	private boolean createDaoSubFolder;

	private boolean createMapper;
	private String mapperPath;

	private boolean createVo;
	private boolean createSearchVo;
	private String voPath;
	private String myBatisSettingFile;

	private boolean createJsp;
	private String jspPath;

	public CSDGeneratorPropertiesItem(IResource resource) {
		super();

		String projectSpecificSettings = "true";
		IPreferenceStore preferenceStore = CSDGeneratorPlugin.getDefault().getPreferenceStore();
/*
		try {
			projectSpecificSettings = resource.getPersistentProperty(CSDGeneratorPropertiesItem.PROJECT_SPECIFIC_SETTINGS);
		} catch (CoreException e) {
			e.printStackTrace();
		}
*/
		if("true".equals(projectSpecificSettings)) {

			try {

				type = resource.getPersistentProperty(CSDGeneratorPropertiesItem.TYPE);

				company = resource.getPersistentProperty(CSDGeneratorPropertiesItem.COMPANY);
				author = resource.getPersistentProperty(CSDGeneratorPropertiesItem.AUTHOR);

				databaseConnectionProfileName = resource.getPersistentProperty(CSDGeneratorPropertiesItem.DATABASE_CONNECTION_PROFILE_NAME);

				createControllerFolder = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_CONTROLLER_FOLDER));
				addPrefixControllerFolder = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.ADD_PREFIX_CONTROLLER_FOLDER));
				createControllerSubFolder = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_CONTROLLER_SUB_FOLDER));

				createServiceFolder = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SERVICE_FOLDER));
				addPrefixServiceFolder = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.ADD_PREFIX_SERVICE_FOLDER));
				createServiceSubFolder = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SERVICE_SUB_FOLDER));

				createServiceImpl = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SERVICEIMPL));
				createServiceImplFolder = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SERVICEIMPL_FOLDER));

				createDaoFolder = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_DAO_FOLDER));
				addPrefixDaoFolder = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.ADD_PREFIX_DAO_FOLDER));
				createDaoSubFolder = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_DAO_SUB_FOLDER));

				createMapper = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_MAPPER));
				mapperPath = resource.getPersistentProperty(CSDGeneratorPropertiesItem.MAPPER_PATH);

				createVo = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_VO));
				createSearchVo = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SEARCH_VO));
				voPath = resource.getPersistentProperty(CSDGeneratorPropertiesItem.VO_PATH);
				myBatisSettingFile = resource.getPersistentProperty(CSDGeneratorPropertiesItem.MYBATIS_SETTING_FILE);

				createJsp = "true".equals(resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_JSP));
				jspPath = resource.getPersistentProperty(CSDGeneratorPropertiesItem.JSP_PATH);

			} catch (CoreException e) {
				e.printStackTrace();
			}

		} else {


			type = preferenceStore.getString(PreferenceConstants.CSDGENERATOR_TYPE);

			company = preferenceStore.getString(PreferenceConstants.CSDGENERATOR_COMPANY);
			author = preferenceStore.getString(PreferenceConstants.CSDGENERATOR_AUTHOR);

			createControllerFolder = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_CONTROLLER_FOLDER);
			addPrefixControllerFolder = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_ADD_PREFIX_CONTROLLER_FOLDER);
			createControllerSubFolder = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_CONTROLLER_SUB_FOLDER);

			createServiceFolder = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_SERVICE_FOLDER);
			addPrefixServiceFolder = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_ADD_PREFIX_SERVICE_FOLDER);
			createServiceSubFolder = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_SERVICE_SUB_FOLDER);

			createServiceImpl = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_SERVICEIMPL);
			createServiceImplFolder = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_SERVICEIMPL_FOLDER);

			createDaoFolder = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_DAO_FOLDER);
			addPrefixDaoFolder = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_ADD_PREFIX_DAO_FOLDER);
			createDaoSubFolder = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_DAO_SUB_FOLDER);

			createMapper = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_MAPPER);
			mapperPath = preferenceStore.getString(PreferenceConstants.CSDGENERATOR_MAPPER_PATH);

			createJsp = preferenceStore.getBoolean(PreferenceConstants.CSDGENERATOR_CREATE_JSP);
			jspPath = preferenceStore.getString(PreferenceConstants.CSDGENERATOR_JSP_PATH);
		}
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getCompany() {
		return this.company;
	}

	@Override
	public void setCompany(String company) {
		this.company = company;
	}

	@Override
	public String getAuthor() {
		return this.author;
	}

	@Override
	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public String getDatabaseConnectionProfileName() {
		return databaseConnectionProfileName;
	}

	@Override
	public void setDatabaseConnectionProfileName(String databaseConnectionProfileName) {
		this.databaseConnectionProfileName = databaseConnectionProfileName;
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
	public boolean getCreateControllerSubFolder() {
		return this.createControllerSubFolder;
	}

	@Override
	public void setCreateControllerSubFolder(boolean createControllerSubFolder) {
		this.createControllerSubFolder = createControllerSubFolder;
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
	public boolean getCreateServiceSubFolder() {
		return this.createServiceSubFolder;
	}

	@Override
	public void setCreateServiceSubFolder(boolean createServiceSubFolder) {
		this.createServiceSubFolder = createServiceSubFolder;
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
	public boolean getCreateDaoSubFolder() {
		return this.createDaoSubFolder;
	}

	@Override
	public void setCreateDaoSubFolder(boolean createDaoSubFolder) {
		this.createDaoSubFolder = createDaoSubFolder;
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
	public boolean getCreateVo() {
		return createVo;
	}

	@Override
	public void setCreateVo(boolean createVo) {
		this.createVo = createVo;
	}

	@Override
	public boolean getCreateSearchVo() {
		return createSearchVo;
	}

	@Override
	public void setCreateSearchVo(boolean createSearchVo) {
		this.createSearchVo = createSearchVo;
	}

	@Override
	public String getVoPath() {
		return voPath;
	}

	@Override
	public void setVoPath(String voPath) {
		this.voPath = voPath;
	}

	@Override
	public String getMyBatisSettingFile() {
		return myBatisSettingFile;
	}

	@Override
	public void setMyBatisSettingFile(String myBatisSettingFilev) {
		this.myBatisSettingFile = myBatisSettingFile;
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

}
