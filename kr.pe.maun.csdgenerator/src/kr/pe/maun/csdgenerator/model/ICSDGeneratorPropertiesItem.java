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

	String getVoPath();
	void setVoPath(String voPath);

	String getMyBatisSettingFile();
	void setMyBatisSettingFile(String myBatisSettingFile);

	boolean getCreateJsp();
	void setCreateJsp(boolean createJsp);

	String getJspPath();
	void setJspPath(String jspPath);
}
