package kr.pe.maun.csdgenerator.model;


public interface ICSDGeneratorPropertiesItem {

	boolean getCreateControllerFolder();
	void setCreateControllerFolder(boolean createControllerFolder);

	boolean getAddPrefixControllerFolder();
	void setAddPrefixControllerFolder(boolean addPrefixControllerFolder);

	String getControllerTemplateFile();
	void setControllerTemplateFile(String controllerTemplateFile);

	boolean getCreateServiceFolder();
	void setCreateServiceFolder(boolean createServiceFolder);

	boolean getAddPrefixServiceFolder();
	void setAddPrefixServiceFolder(boolean addPrefixServiceFolder);

	String getServiceTemplateFile();
	void setServiceTemplateFile(String serviceTemplateFile);

	boolean getCreateServiceImpl();
	void setCreateServiceImpl(boolean createServiceImpl);

	boolean getCreateServiceImplFolder();
	void setCreateServiceImplFolder(boolean createServiceImplFolder);

	String getServiceImplTemplateFile();
	void setServiceImplTemplateFile(String serviceImplTemplateFile);

	boolean getCreateDaoFolder();
	void setCreateDaoFolder(boolean createDaoFolder);

	boolean getAddPrefixDaoFolder();
	void setAddPrefixDaoFolder(boolean addPrefixDaoFolder);

	String getDaoTemplateFile();
	void setDaoTemplateFile(String daoTemplateFile);

	boolean getCreateMapper();
	void setCreateMapper(boolean createMapper);

	String getMapperPath();
	void setMapperPath(String mapperPath);

	String getMapperTemplateFile();
	void setMapperTemplateFile(String mapperTemplateFile);

	boolean getCreateJsp();
	void setCreateJsp(boolean createJsp);

	String getJspPath();
	void setJspPath(String jspPath);

	String getJspTemplateFile();
	void setJspTemplateFile(String jspTemplateFile);
}
