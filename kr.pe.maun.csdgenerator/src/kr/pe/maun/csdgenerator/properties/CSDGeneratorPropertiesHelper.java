package kr.pe.maun.csdgenerator.properties;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class CSDGeneratorPropertiesHelper {

	Preferences preferences;

	private static final String GENERAL_TEMPLATE_GOUP_NAME = "general.template.group";
	private static final String GENERAL_TEMPLATE_GOUP_CONTROLLER_NAME = "general.template.group.controller";
	private static final String GENERAL_TEMPLATE_GOUP_SERVICE_NAME = "general.template.group.service";
	private static final String GENERAL_TEMPLATE_GOUP_DAO_NAME = "general.template.group.dao";
	private static final String GENERAL_TEMPLATE_GOUP_MAPPER_NAME = "general.template.group.mapper";
	private static final String GENERAL_TEMPLATE_GOUP_JSP_NAME = "general.template.group.jsp";

	private static final String CONTROLLER_TEMPLATE_NAME = "controller.template";
	private static final String CONTROLLER_TEMPLATE_PATH = "controller.template.path";


	private static final String SERVICE_TEMPLATE_NAME = "service.template";
	private static final String SERVICE_TEMPLATE_PATH = "service.template.path";

	private static final String DAO_TEMPLATE_NAME = "dao.template";
	private static final String DAO_TEMPLATE_PATH = "dao.template.path";

	private static final String MAPPER_TEMPLATE_NAME = "mapper.template";
	private static final String MAPPER_TEMPLATE_PATH = "mapper.template.path";

	private static final String JSP_TEMPLATE_NAME = "jsp.template";
	private static final String JSP_TEMPLATE_LIST_PATH = "jsp.template.list";
	private static final String JSP_TEMPLATE_REG_PATH = "jsp.template.reg";
	private static final String JSP_TEMPLATE_VIEW_PATH = "jsp.template.view";

	private static final String VO_DATA_TYPE_MAPPING_PATH_NAME = "vo.data.type.mapping";

	public CSDGeneratorPropertiesHelper(Preferences preferences){
		this.preferences = preferences;
	}

	public String[] getGeneralTemplateGoupName(){
		try {
			Preferences node = preferences.node(GENERAL_TEMPLATE_GOUP_NAME);
			return node.keys();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return new String[]{};
	}

	public String[] getGeneralTemplateGroupNames(){
		try {
			Preferences node = preferences.node(GENERAL_TEMPLATE_GOUP_NAME);
			return node.keys();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return new String[]{};
	}

	public String getGeneralTemplateController(String key) {
		return preferences.node(GENERAL_TEMPLATE_GOUP_CONTROLLER_NAME).get(key, null);
	}

	public String getGeneralTemplateService(String key) {
		return preferences.node(GENERAL_TEMPLATE_GOUP_SERVICE_NAME).get(key, null);
	}

	public String getGeneralTemplateDao(String key) {
		return preferences.node(GENERAL_TEMPLATE_GOUP_DAO_NAME).get(key, null);
	}

	public String getGeneralTemplateMapper(String key) {
		return preferences.node(GENERAL_TEMPLATE_GOUP_MAPPER_NAME).get(key, null);
	}

	public String getGeneralTemplateJSP(String key) {
		return preferences.node(GENERAL_TEMPLATE_GOUP_JSP_NAME).get(key, null);
	}

	public void generalPropertiesFlush(Tree templateTree){
		try {
			preferences.node(GENERAL_TEMPLATE_GOUP_NAME).clear();
			preferences.node(GENERAL_TEMPLATE_GOUP_CONTROLLER_NAME).clear();
			preferences.node(GENERAL_TEMPLATE_GOUP_SERVICE_NAME).clear();
			preferences.node(GENERAL_TEMPLATE_GOUP_DAO_NAME).clear();
			preferences.node(GENERAL_TEMPLATE_GOUP_MAPPER_NAME).clear();
			preferences.node(GENERAL_TEMPLATE_GOUP_JSP_NAME).clear();
			TreeItem[] templateItems = templateTree.getItems();
			for(TreeItem template : templateItems) {
				preferences.node(GENERAL_TEMPLATE_GOUP_NAME).put(template.getText(), template.getText());
				preferences.node(GENERAL_TEMPLATE_GOUP_CONTROLLER_NAME).put(template.getText(), template.getItems()[0].getText());
				preferences.node(GENERAL_TEMPLATE_GOUP_SERVICE_NAME).put(template.getText(), template.getItems()[1].getText());
				preferences.node(GENERAL_TEMPLATE_GOUP_DAO_NAME).put(template.getText(), template.getItems()[2].getText());
				preferences.node(GENERAL_TEMPLATE_GOUP_MAPPER_NAME).put(template.getText(), template.getItems()[3].getText());
				preferences.node(GENERAL_TEMPLATE_GOUP_JSP_NAME).put(template.getText(), template.getItems()[4].getText());
			}
			preferences.node(GENERAL_TEMPLATE_GOUP_NAME).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String[] getControllerTemplateNames(){
		try {
			Preferences node = preferences.node(CONTROLLER_TEMPLATE_NAME);
			return node.keys();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return new String[]{};
	}

	public String getControllerTemplatePath(String key) {
		return preferences.node(CONTROLLER_TEMPLATE_PATH).get(key, null);
	}

	public void controllerPropertiesFlush(Tree templateTree){
		try {
			preferences.node(CONTROLLER_TEMPLATE_NAME).clear();
			preferences.node(CONTROLLER_TEMPLATE_PATH).clear();
			TreeItem[] templateItems = templateTree.getItems();
			for(TreeItem template : templateItems) {
				TreeItem templatePathItem = template.getItems()[0];
				preferences.node(CONTROLLER_TEMPLATE_NAME).put(template.getText(), template.getText());
				preferences.node(CONTROLLER_TEMPLATE_PATH).put(template.getText(), templatePathItem.getText());
			}
			preferences.node(CONTROLLER_TEMPLATE_NAME).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String[] getServiceTemplateName(){
		try {
			Preferences node = preferences.node(SERVICE_TEMPLATE_NAME);
			return node.keys();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return new String[]{};
	}

	public String[] getServiceTemplateNames(){
		try {
			Preferences node = preferences.node(SERVICE_TEMPLATE_NAME);
			return node.keys();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return new String[]{};
	}

	public String getServiceTemplatePath(String key) {
		return preferences.node(SERVICE_TEMPLATE_PATH).get(key, null);
	}

	public void servicePropertiesFlush(Tree templateTree){
		try {
			preferences.node(SERVICE_TEMPLATE_NAME).clear();
			preferences.node(SERVICE_TEMPLATE_PATH).clear();
			TreeItem[] templateItems = templateTree.getItems();
			for(TreeItem template : templateItems) {
				TreeItem templatePathItem = template.getItems()[0];
				preferences.node(SERVICE_TEMPLATE_NAME).put(template.getText(), template.getText());
				preferences.node(SERVICE_TEMPLATE_PATH).put(template.getText(), templatePathItem.getText());
			}
			preferences.node(SERVICE_TEMPLATE_NAME).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String[] getDaoTemplateName(){
		try {
			Preferences node = preferences.node(DAO_TEMPLATE_NAME);
			return node.keys();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return new String[]{};
	}

	public String[] getDaoTemplateNames(){
		try {
			Preferences node = preferences.node(DAO_TEMPLATE_NAME);
			return node.keys();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return new String[]{};
	}

	public String getDaoTemplatePath(String key) {
		return preferences.node(DAO_TEMPLATE_PATH).get(key, null);
	}

	public void daoPropertiesFlush(Tree templateTree){
		try {
			preferences.node(DAO_TEMPLATE_NAME).clear();
			preferences.node(DAO_TEMPLATE_PATH).clear();
			TreeItem[] templateItems = templateTree.getItems();
			for(TreeItem template : templateItems) {
				TreeItem templatePathItem = template.getItems()[0];
				preferences.node(DAO_TEMPLATE_NAME).put(template.getText(), template.getText());
				preferences.node(DAO_TEMPLATE_PATH).put(template.getText(), templatePathItem.getText());
			}
			preferences.node(DAO_TEMPLATE_NAME).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String[] getMapperTemplateName(){
		try {
			Preferences node = preferences.node(MAPPER_TEMPLATE_NAME);
			return node.keys();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return new String[]{};
	}

	public String[] getMapperTemplateNames(){
		try {
			Preferences node = preferences.node(MAPPER_TEMPLATE_NAME);
			return node.keys();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return new String[]{};
	}

	public String getMapperTemplatePath(String key) {
		return preferences.node(MAPPER_TEMPLATE_PATH).get(key, null);
	}

	public void mapperPropertiesFlush(Tree templateTree){
		try {
			preferences.node(MAPPER_TEMPLATE_NAME).clear();
			preferences.node(MAPPER_TEMPLATE_PATH).clear();
			TreeItem[] templateItems = templateTree.getItems();
			for(TreeItem template : templateItems) {
				TreeItem templatePathItem = template.getItems()[0];
				preferences.node(MAPPER_TEMPLATE_NAME).put(template.getText(), template.getText());
				preferences.node(MAPPER_TEMPLATE_PATH).put(template.getText(), templatePathItem.getText());
			}
			preferences.node(MAPPER_TEMPLATE_NAME).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String[] getJSPTemplateName(){
		try {
			Preferences node = preferences.node(JSP_TEMPLATE_NAME);
			return node.keys();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return new String[]{};
	}

}
