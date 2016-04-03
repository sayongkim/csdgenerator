package kr.pe.maun.csdgenerator.model;

import java.io.Serializable;

public class TemplateItem implements Serializable {

	private static final long serialVersionUID = -6471835687825091474L;

	private String templateName;
	private String templatePath;

	public String getTemplateName() {
		return templateName;
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

}
