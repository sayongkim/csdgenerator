package kr.pe.maun.csdgenerator.model;

import java.io.Serializable;

public class TemplateGroupItem implements Serializable {

	private static final long serialVersionUID = 1612030390072922221L;

	private String templateGroupId;
	private String templateGroupName;
	private String controllerTemplateId;
	private String serviceTemplateId;
	private String daoTemplateId;
	private String mapperTemplateId;
	private String jspTemplateId;

	public String getTemplateGroupId() {
		return templateGroupId;
	}

	public String getTemplateGroupName() {
		return templateGroupName;
	}

	public String getControllerTemplateId() {
		return controllerTemplateId;
	}

	public String getServiceTemplateId() {
		return serviceTemplateId;
	}

	public String getDaoTemplateId() {
		return daoTemplateId;
	}

	public String getMapperTemplateId() {
		return mapperTemplateId;
	}

	public String getJspTemplateId() {
		return jspTemplateId;
	}

	public void setTemplateGroupId(String templateGroupId) {
		this.templateGroupId = templateGroupId;
	}

	public void setTemplateGroupName(String templateGroupName) {
		this.templateGroupName = templateGroupName;
	}

	public void setControllerTemplateId(String controllerTemplateId) {
		this.controllerTemplateId = controllerTemplateId;
	}

	public void setServiceTemplateId(String serviceTemplateId) {
		this.serviceTemplateId = serviceTemplateId;
	}

	public void setDaoTemplateId(String daoTemplateId) {
		this.daoTemplateId = daoTemplateId;
	}

	public void setMapperTemplateId(String mapperTemplateId) {
		this.mapperTemplateId = mapperTemplateId;
	}

	public void setJspTemplateId(String jspTemplateId) {
		this.jspTemplateId = jspTemplateId;
	}

}
