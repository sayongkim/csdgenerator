package kr.pe.maun.csdgenerator.model;


public interface ICSDGeneratorPropertiesItem {

	String getType();
	void setType(String type);

	String getCompany();
	void setCompany(String company);

	String getAuthor();
	void setAuthor(String author);

	String getDatabaseConnectionProfileName();
	void setDatabaseConnectionProfileName(String databaseConnectionProfileName);

	boolean getCreateTest();
	void setCreateTest(boolean createTest);

	boolean getCreateTestControllerFolder();
	void setCreateTestControllerFolder(boolean createTestControllerFolder);

	boolean getCreateTestServiceFolder();
	void setCreateTestServiceFolder(boolean createTestServiceFolder);

	boolean getCreateTestDaoFolder();
	void setCreateTestDaoFolder(boolean createTestDaoFolder);

	String getTestPath();
	void setTestPath(String testPath);

	boolean getCreateControllerFolder();
	void setCreateControllerFolder(boolean createControllerFolder);

	boolean getAddPrefixControllerFolder();
	void setAddPrefixControllerFolder(boolean addPrefixControllerFolder);

	boolean getCreateControllerSubFolder();
	void setCreateControllerSubFolder(boolean createControllerSubFolder);

	boolean getCreateServiceFolder();
	void setCreateServiceFolder(boolean createServiceFolder);

	boolean getAddPrefixServiceFolder();
	void setAddPrefixServiceFolder(boolean addPrefixServiceFolder);

	boolean getCreateServiceSubFolder();
	void setCreateServiceSubFolder(boolean createServiceSubFolder);

	boolean getCreateServiceImpl();
	void setCreateServiceImpl(boolean createServiceImpl);

	boolean getCreateServiceImplFolder();
	void setCreateServiceImplFolder(boolean createServiceImplFolder);

	boolean getCreateDaoFolder();
	void setCreateDaoFolder(boolean createDaoFolder);

	boolean getCreateDaoSubFolder();
	void setCreateDaoSubFolder(boolean createDaoSubFolder);

	boolean getAddPrefixDaoFolder();
	void setAddPrefixDaoFolder(boolean addPrefixDaoFolder);

	boolean getCreateMapper();
	void setCreateMapper(boolean createMapper);

	String getMapperPath();
	void setMapperPath(String mapperPath);

	boolean getCreateVo();
	void setCreateVo(boolean createVo);

	boolean getCreateSearchVo();
	void setCreateSearchVo(boolean createSearchVo);

	boolean getCreateVoFolder();
	void setCreateVoFolder(boolean createVoFolder);

	String getVoSuperclass();
	void setVoSuperclass(String voSuperclass);

	String getVoFolder();
	void setVoFolderName(String voFolderName);

	String getVoPath();
	void setVoPath(String voPath);

	String getMyBatisSettingFile();
	void setMyBatisSettingFile(String myBatisSettingFile);

	boolean getCreateJsp();
	void setCreateJsp(boolean createJsp);

	String getJspPath();
	void setJspPath(String jspPath);
}
