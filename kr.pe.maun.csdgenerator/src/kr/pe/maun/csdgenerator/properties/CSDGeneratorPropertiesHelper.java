package kr.pe.maun.csdgenerator.properties;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class CSDGeneratorPropertiesHelper {

	Preferences preferences;

	private static final String FUNCTION_TEMPLATE_KEY = "template";

	private static final String GENERAL_TEMPLATE_GOUP_NAME = "general.template.group";
	private static final String GENERAL_TEMPLATE_GOUP_CONTROLLER_NAME = "general.template.group.controller";
	private static final String GENERAL_TEMPLATE_GOUP_SERVICE_NAME = "general.template.group.service";
	private static final String GENERAL_TEMPLATE_GOUP_DAO_NAME = "general.template.group.dao";
	private static final String GENERAL_TEMPLATE_GOUP_MAPPER_NAME = "general.template.group.mapper";
	private static final String GENERAL_TEMPLATE_GOUP_JSP_NAME = "general.template.group.jsp";

	private static final String CONTROLLER_TEMPLATE_NAME = "controller.template";
	private static final String CONTROLLER_TEMPLATE_FILE = "controller.template.file";

	private static final String SERVICE_TEMPLATE_NAME = "service.template";
	private static final String SERVICE_TEMPLATE_FILE = "service.template.file";

	private static final String SERVICE_SELECT_COUNT_TEMPLATE = "service.function.select.count.template";
	private static final String SERVICE_SELECT_LIST_TEMPLATE = "service.function.select.list.template";
	private static final String SERVICE_SELECT_ONE_TEMPLATE = "service.function.select.one.template";
	private static final String SERVICE_INSERT_TEMPLATE = "service.function.select.insert.template";
	private static final String SERVICE_UPDATE_TEMPLATE = "service.function.select.update.template";
	private static final String SERVICE_DELETE_TEMPLATE = "service.function.select.delete.template";

	private static final String DAO_TEMPLATE_NAME = "dao.template";
	private static final String DAO_TEMPLATE_FILE = "dao.template.file";

	private static final String DAO_SELECT_COUNT_TEMPLATE = "dao.function.select.count.template";
	private static final String DAO_SELECT_LIST_TEMPLATE = "dao.function.select.list.template";
	private static final String DAO_SELECT_ONE_TEMPLATE = "dao.function.select.one.template";
	private static final String DAO_INSERT_TEMPLATE = "dao.function.select.insert.template";
	private static final String DAO_UPDATE_TEMPLATE = "dao.function.select.update.template";
	private static final String DAO_DELETE_TEMPLATE = "dao.function.select.delete.template";

	private static final String MAPPER_TEMPLATE_NAME = "mapper.template";
	private static final String MAPPER_TEMPLATE_FILE = "mapper.template.file";

	private static final String MAPPER_SELECT_COUNT_TEMPLATE = "mapper.function.select.count.template";
	private static final String MAPPER_SELECT_LIST_TEMPLATE = "mapper.function.select.list.template";
	private static final String MAPPER_SELECT_ONE_TEMPLATE = "mapper.function.select.one.template";
	private static final String MAPPER_INSERT_TEMPLATE = "mapper.function.select.insert.template";
	private static final String MAPPER_UPDATE_TEMPLATE = "mapper.function.select.update.template";
	private static final String MAPPER_DELETE_TEMPLATE = "mapper.function.select.delete.template";

	private static final String JSP_TEMPLATE_NAME = "jsp.template";
	private static final String JSP_TEMPLATE_LIST_FILE = "jsp.template.list.file";
	private static final String JSP_TEMPLATE_POST_FILE = "jsp.template.post.file";
	private static final String JSP_TEMPLATE_VIEW_FILE = "jsp.template.view.file";

	private static final String VO_DATA_TYPE = "vo.data.type";

	public CSDGeneratorPropertiesHelper(Preferences preferences){
		this.preferences = preferences;
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

	public String getGeneralTemplateJsp(String key) {
		return preferences.node(GENERAL_TEMPLATE_GOUP_JSP_NAME).get(key, null);
	}

	public String[] generalPropertiesFlush(Tree templateTree){
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
				preferences.node(GENERAL_TEMPLATE_GOUP_CONTROLLER_NAME).put(template.getText(), (String) template.getItems()[0].getData());
				preferences.node(GENERAL_TEMPLATE_GOUP_SERVICE_NAME).put(template.getText(), (String) template.getItems()[1].getData());
				preferences.node(GENERAL_TEMPLATE_GOUP_DAO_NAME).put(template.getText(), (String) template.getItems()[2].getData());
				preferences.node(GENERAL_TEMPLATE_GOUP_MAPPER_NAME).put(template.getText(), (String) template.getItems()[3].getData());
				preferences.node(GENERAL_TEMPLATE_GOUP_JSP_NAME).put(template.getText(), (String) template.getItems()[4].getData());
			}
			preferences.node(GENERAL_TEMPLATE_GOUP_NAME).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

		return getGeneralTemplateGroupNames();
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

	public String getControllerTemplateFile(String key) {
		return preferences.node(CONTROLLER_TEMPLATE_FILE).get(key, null);
	}

	public String[] controllerPropertiesFlush(Tree templateTree){
		try {
			preferences.node(CONTROLLER_TEMPLATE_NAME).clear();
			preferences.node(CONTROLLER_TEMPLATE_FILE).clear();
			TreeItem[] templateItems = templateTree.getItems();
			for(TreeItem template : templateItems) {
				TreeItem templatePathItem = template.getItems()[0];
				preferences.node(CONTROLLER_TEMPLATE_NAME).put(template.getText(), template.getText());
				preferences.node(CONTROLLER_TEMPLATE_FILE).put(template.getText(), templatePathItem.getText());
			}
			preferences.node(CONTROLLER_TEMPLATE_NAME).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return getControllerTemplateNames();
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

	public String getServiceTemplateFile(String key) {
		return preferences.node(SERVICE_TEMPLATE_FILE).get(key, null);
	}

	public String[] servicePropertiesFlush(Tree templateTree){
		try {
			preferences.node(SERVICE_TEMPLATE_NAME).clear();
			preferences.node(SERVICE_TEMPLATE_FILE).clear();
			TreeItem[] templateItems = templateTree.getItems();
			for(TreeItem template : templateItems) {
				TreeItem templatePathItem = template.getItems()[0];
				preferences.node(SERVICE_TEMPLATE_NAME).put(template.getText(), template.getText());
				preferences.node(SERVICE_TEMPLATE_FILE).put(template.getText(), templatePathItem.getText());
			}
			preferences.node(SERVICE_TEMPLATE_NAME).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

		return getServiceTemplateName();
	}

	public String getServiceFunctionSelectCountTemplate() {
		return preferences.node(SERVICE_SELECT_COUNT_TEMPLATE).get(FUNCTION_TEMPLATE_KEY, "\t/**\n\t * 총 건수 조회\n\t * @Method : select[capitalizePrefix]TotalCnt\n\t * @Company : [company]\n\t * @Author : [author]\n\t * @Date : [date]\n\t * @param [searchParamName]\n\t * @return\n\t */\n\t@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)\n\tpublic long select[capitalizePrefix]TotalCnt([searchParamType] [searchParamName]) {\n\t\treturn [lowerPrefix]Dao.select[capitalizePrefix]TotalCnt([searchParamName]);\n\t}");
	}

	public void setServiceFunctionSelectCountTemplate(String template) {
		try {
			preferences.node(SERVICE_SELECT_COUNT_TEMPLATE).put(FUNCTION_TEMPLATE_KEY, template);
			preferences.node(SERVICE_SELECT_COUNT_TEMPLATE).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String getServiceFunctionSelectListTemplate() {
		return preferences.node(SERVICE_SELECT_LIST_TEMPLATE).get(FUNCTION_TEMPLATE_KEY, "\t/**\n\t * 목록 조회\n\t * @Method : select[lowerPrefix]List\n\t * @Company : [company]\n\t * @Author : [author]\n\t * @Date : [date]\n\t * @param [searchParamName]\n\t * @return\n\t */\n\t@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)\n\tpublic List<[returnType]> select[capitalizePrefix]List([searchParamType] [searchParamName]) {\n\t\treturn [lowerPrefix]Dao.select[capitalizePrefix]List([searchParamName]);\n\t}");
	}

	public void setServiceFunctionSelectListTemplate(String template) {
		try {
			preferences.node(SERVICE_SELECT_LIST_TEMPLATE).put(FUNCTION_TEMPLATE_KEY, template);
			preferences.node(SERVICE_SELECT_LIST_TEMPLATE).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String getServiceFunctionSelectOneTemplate() {
		return preferences.node(SERVICE_SELECT_ONE_TEMPLATE).get(FUNCTION_TEMPLATE_KEY, "\t/**\n\t * 조회\n\t * @Method : select[lowerPrefix]\n\t * @Company : [company]\n\t * @Author : [author]\n\t * @Date : [date]\n\t * @param [searchParamName]\n\t * @return\n\t */\n\t@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)\n\tpublic [returnType] select[capitalizePrefix](HashMap<String, Object> [searchParamName]) {\n\t\treturn [lowerPrefix]Dao.select[capitalizePrefix]([searchParamName]);\n\t}");
	}

	public void setServiceFunctionSelectOneTemplate(String template) {
		try {
			preferences.node(SERVICE_SELECT_ONE_TEMPLATE).put(FUNCTION_TEMPLATE_KEY, template);
			preferences.node(SERVICE_SELECT_ONE_TEMPLATE).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String getServiceFunctionInsertTemplate() {
		return preferences.node(SERVICE_INSERT_TEMPLATE).get(FUNCTION_TEMPLATE_KEY, "\t/**\n\t * 등록\n\t * @Method : insert[lowerPrefix]\n\t * @Company : [company]\n\t * @Author : [author]\n\t * @Date : [date]\n\t * @param [paramName]\n\t * @return\n\t */\n\t@Transactional(propagation = Propagation.REQUIRED, isolation=Isolation.DEFAULT)\n\tpublic int insert[capitalizePrefix]([paramType] [paramName]) {\n\t\treturn [lowerPrefix]Dao.insert[capitalizePrefix](requestMap);\n\t}");
	}

	public void setServiceFunctionInsertTemplate(String template) {
		try {
			preferences.node(SERVICE_INSERT_TEMPLATE).put(FUNCTION_TEMPLATE_KEY, template);
			preferences.node(SERVICE_INSERT_TEMPLATE).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String getServiceFunctionUpdateTemplate() {
		return preferences.node(SERVICE_UPDATE_TEMPLATE).get(FUNCTION_TEMPLATE_KEY, "\t/**\n\t * 수정\n\t * @Method : update[lowerPrefix]\n\t * @Company : [company]\n\t * @Author : [author]\n\t * @Date : [date]\n\t * @param [paramName]\n\t * @return\n\t */\n\t@Transactional(propagation = Propagation.REQUIRED, isolation=Isolation.DEFAULT)\n\tpublic int update[capitalizePrefix]([paramType] [paramName]) {\n\t\treturn [lowerPrefix]Dao.update[capitalizePrefix]([paramName]);\n\t}");
	}

	public void setServiceFunctionUpdateTemplate(String template) {
		try {
			preferences.node(SERVICE_UPDATE_TEMPLATE).put(FUNCTION_TEMPLATE_KEY, template);
			preferences.node(SERVICE_UPDATE_TEMPLATE).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String getServiceFunctionDeleteTemplate() {
		return preferences.node(SERVICE_DELETE_TEMPLATE).get(FUNCTION_TEMPLATE_KEY, "\t/**\n\t * 삭제\n\t * @Method : delete[lowerPrefix]\n\t * @Company : [company]\n\t * @Author : [author]\n\t * @Date : [date]\n\t * @param [paramName]\n\t * @return\n\t */\n\t@Transactional(propagation = Propagation.REQUIRED, isolation=Isolation.DEFAULT)\n\tpublic int delete[capitalizePrefix]([paramType] [paramName]) {\n\t\treturn [lowerPrefix]Dao.update[capitalizePrefix]([paramName]);\n\t}");
	}

	public void setServiceFunctionDeleteTemplate(String template) {
		try {
			preferences.node(SERVICE_DELETE_TEMPLATE).put(FUNCTION_TEMPLATE_KEY, template);
			preferences.node(SERVICE_DELETE_TEMPLATE).flush();
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

	public String getDaoTemplateFile(String key) {
		return preferences.node(DAO_TEMPLATE_FILE).get(key, null);
	}

	public String[] daoPropertiesFlush(Tree templateTree){
		try {
			preferences.node(DAO_TEMPLATE_NAME).clear();
			preferences.node(DAO_TEMPLATE_FILE).clear();
			TreeItem[] templateItems = templateTree.getItems();
			for(TreeItem template : templateItems) {
				TreeItem templatePathItem = template.getItems()[0];
				preferences.node(DAO_TEMPLATE_NAME).put(template.getText(), template.getText());
				preferences.node(DAO_TEMPLATE_FILE).put(template.getText(), templatePathItem.getText());
			}
			preferences.node(DAO_TEMPLATE_NAME).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return getDaoTemplateName();
	}

	public String getDaoFunctionSelectCountTemplate() {
		return preferences.node(DAO_SELECT_COUNT_TEMPLATE).get(FUNCTION_TEMPLATE_KEY, "\t/**\n\t * 총 건수 조회\n\t * @Method : select[capitalizePrefix]TotalCnt\n\t * @Company : [company]\n\t * @Author : [author]\n\t * @Date : [date]\n\t * @param [searchParamName]\n\t * @return\n\t */\n\tpublic long select[capitalizePrefix]TotalCnt([searchParamType] [searchParamName]) {\n\t\treturn sqlSession.selectOne(\"[lowerPrefix]Mapper.select[capitalizePrefix]TotalCnt\", [searchParamName]);\n\t}");
	}

	public void setDaoFunctionSelectCountTemplate(String template) {
		try {
			preferences.node(DAO_SELECT_COUNT_TEMPLATE).put(FUNCTION_TEMPLATE_KEY, template);
			preferences.node(DAO_SELECT_COUNT_TEMPLATE).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String getDaoFunctionSelectListTemplate() {
		return preferences.node(DAO_SELECT_LIST_TEMPLATE).get(FUNCTION_TEMPLATE_KEY, "\t/**\n\t * 목록 조회\n\t * @Method : select[capitalizePrefix]List\n\t * @Company : [company]\n\t * @Author : [author]\n\t * @Date : [date]\n\t * @param [searchParamName]\n\t * @return\n\t */\n\tpublic List<[returnType]> select[capitalizePrefix]List([searchParamType] [searchParamName]) {\n\t\treturn sqlSession.selectList(\"[lowerPrefix]Mapper.select[capitalizePrefix]List\", [searchParamName]);\n\t}");
	}

	public void setDaoFunctionSelectListTemplate(String template) {
		try {
			preferences.node(DAO_SELECT_LIST_TEMPLATE).put(FUNCTION_TEMPLATE_KEY, template);
			preferences.node(DAO_SELECT_LIST_TEMPLATE).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String getDaoFunctionSelectOneTemplate() {
		return preferences.node(DAO_SELECT_ONE_TEMPLATE).get(FUNCTION_TEMPLATE_KEY, "\t/**\n\t * 조회\n\t * @Method : select[capitalizePrefix]\n\t * @Company : [company]\n\t * @Author : [author]\n\t * @Date : [date]\n\t * @param [searchParamName]\n\t * @return\n\t */\n\tpublic [returnType] select[capitalizePrefix]([searchParamType] [searchParamName]) {\n\t\treturn sqlSession.selectOne(\"[lowerPrefix]Mapper.select[capitalizePrefix]\", [searchParamName]);\n\t}");
	}

	public void setDaoFunctionSelectOneTemplate(String template) {
		try {
			preferences.node(DAO_SELECT_ONE_TEMPLATE).put(FUNCTION_TEMPLATE_KEY, template);
			preferences.node(DAO_SELECT_ONE_TEMPLATE).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String getDaoFunctionInsertTemplate() {
		return preferences.node(DAO_INSERT_TEMPLATE).get(FUNCTION_TEMPLATE_KEY, "\t/**\n\t * 등록\n\t * @Method : insert[capitalizePrefix]\n\t * @Company : [company]\n\t * @Author : [author]\n\t * @Date : [date]\n\t * @param [paramName]\n\t * @return\n\t */\n\tpublic int insert[capitalizePrefix]([paramType] [paramName]) {\n\t\treturn sqlSession.insert(\"[lowerPrefix]Mapper.insert[capitalizePrefix]\", [paramName]);\n\t}");
	}

	public void setDaoFunctionInsertTemplate(String template) {
		try {
			preferences.node(DAO_INSERT_TEMPLATE).put(FUNCTION_TEMPLATE_KEY, template);
			preferences.node(DAO_INSERT_TEMPLATE).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String getDaoFunctionUpdateTemplate() {
		return preferences.node(DAO_UPDATE_TEMPLATE).get(FUNCTION_TEMPLATE_KEY, "\t/**\n\t * 수정\n\t * @Method : update[capitalizePrefix]\n\t * @Company : [company]\n\t * @Author : [author]\n\t * @Date : [date]\n\t * @param [paramName]\n\t * @return\n\t */\n\tpublic int update[capitalizePrefix]([paramType] [paramName] requestMap) {\n\t\treturn sqlSession.update(\"[lowerPrefix]Mapper.update[capitalizePrefix]\", [paramName]);\n\t}");
	}

	public void setDaoFunctionUpdateTemplate(String template) {
		try {
			preferences.node(DAO_UPDATE_TEMPLATE).put(FUNCTION_TEMPLATE_KEY, template);
			preferences.node(DAO_UPDATE_TEMPLATE).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String getDaoFunctionDeleteTemplate() {
		return preferences.node(DAO_DELETE_TEMPLATE).get(FUNCTION_TEMPLATE_KEY, "\t/**\n\t * 삭제\n\t * @Method : delete[capitalizePrefix]\n\t * @Company : [company]\n\t * @Author : [author]\n\t * @Date : [date]\n\t * @param [paramName]\n\t * @return\n\t */\n\tpublic int delete[capitalizePrefix]([paramType] [paramName] requestMap) {\n\t\treturn sqlSession.delete(\"[lowerPrefix]Mapper.delete[capitalizePrefix]\", [paramName]);\n\t}");
	}

	public void setDaoFunctionDeleteTemplate(String template) {
		try {
			preferences.node(DAO_DELETE_TEMPLATE).put(FUNCTION_TEMPLATE_KEY, template);
			preferences.node(DAO_DELETE_TEMPLATE).flush();
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

	public String getMapperTemplateFile(String key) {
		return preferences.node(MAPPER_TEMPLATE_FILE).get(key, null);
	}

	public String[] mapperPropertiesFlush(Tree templateTree){
		try {
			preferences.node(MAPPER_TEMPLATE_NAME).clear();
			preferences.node(MAPPER_TEMPLATE_FILE).clear();
			TreeItem[] templateItems = templateTree.getItems();
			for(TreeItem template : templateItems) {
				TreeItem templatePathItem = template.getItems()[0];
				preferences.node(MAPPER_TEMPLATE_NAME).put(template.getText(), template.getText());
				preferences.node(MAPPER_TEMPLATE_FILE).put(template.getText(), templatePathItem.getText());
			}
			preferences.node(MAPPER_TEMPLATE_NAME).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return getMapperTemplateName();
	}

	public String getMapperFunctionSelectCountTemplate() {
		return preferences.node(MAPPER_SELECT_COUNT_TEMPLATE).get(FUNCTION_TEMPLATE_KEY, "\t<!--\n\tDesc : 총 건수 조회\n\tTABLE : [table]\n\t-->\n\t<select id=\"select[capitalizePrefix]TotalCnt\" parameterType=\"[paramName]\" resultType=\"long\">\n\t\tSELECT\n\t\t\tCOUNT(NO_SEQ) AS cnt\n\t\tFROM [table]\n\t\tWHERE\n\t\t\t&lt;include refid=\"sqlSearch[capitalizePrefix]\" /&gt;\n\t</select>");
	}

	public void setMapperFunctionSelectCountTemplate(String template) {
		try {
			preferences.node(MAPPER_SELECT_COUNT_TEMPLATE).put(FUNCTION_TEMPLATE_KEY, template);
			preferences.node(MAPPER_SELECT_COUNT_TEMPLATE).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String getMapperFunctionSelectListTemplate() {
		return preferences.node(MAPPER_SELECT_LIST_TEMPLATE).get(FUNCTION_TEMPLATE_KEY, "\t<!--\n\tDesc : 목록 조회\n\tTABLE : [table]\n\t-->\n\t<select id=\"select[capitalizePrefix]List\" parameterType=\"[paramName]\" resultType=\"[paramName]\">\n\t\tSELECT\n\t\t\t[columns]\n\t\tFROM [table]\n\t\tWHERE DEL_YN = 'N'\n\t\t\t&lt;include refid=\"sqlSearch[capitalizePrefix]\" /&gt;\n\t\tORDER BY NO_SEQ DESC\n\t\t&lt;if test=' startNum != null and startNum != \"\" and  endNum != null and endNum != \"\" '&gt;\n\t\tLIMIT #{startNum}, #{endNum}\n\t\t&lt;/if&gt;\n\t</select>");
	}

	public void setMapperFunctionSelectListTemplate(String template) {
		try {
			preferences.node(MAPPER_SELECT_LIST_TEMPLATE).put(FUNCTION_TEMPLATE_KEY, template);
			preferences.node(MAPPER_SELECT_LIST_TEMPLATE).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String getMapperFunctionSelectOneTemplate() {
		return preferences.node(MAPPER_SELECT_ONE_TEMPLATE).get(FUNCTION_TEMPLATE_KEY, "\t<!--\n\tDesc : 조회\n\tTABLE : [table]\n\t-->\n\t<select id=\"select[capitalizePrefix]\" parameterType=\"[paramName]\" resultType=\"[paramName]\">\n\t\tSELECT\n\t\t\t[columns]\n\t\tFROM [table]\n\t\tWHERE 1=1\n\t\t\t[indexColumns]\n\t</select>");
	}

	public void setMapperFunctionSelectOneTemplate(String template) {
		try {
			preferences.node(MAPPER_SELECT_ONE_TEMPLATE).put(FUNCTION_TEMPLATE_KEY, template);
			preferences.node(MAPPER_SELECT_ONE_TEMPLATE).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String getMapperFunctionInsertTemplate() {
		return preferences.node(MAPPER_INSERT_TEMPLATE).get(FUNCTION_TEMPLATE_KEY, "\t<!--\n\tDesc : 등록\n\tTABLE : [table]\n\t-->\n\t<insert id=\"insert[capitalizePrefix]\" parameterType=\"[paramName]\">\n\t\t&lt;selectKey resultType=\"string\" keyProperty=\"no_seq\" order=\"BEFORE\"&gt;\n\t\t&lt;/selectKey&gt;\n\t\tINSERT INTO [table] (\n\t\t\t[columns]\n\t\t) VALUES (\n\t\t\t[values]\n\t\t)\n\t</insert>");
	}

	public void setMapperFunctionInsertTemplate(String template) {
		try {
			preferences.node(MAPPER_INSERT_TEMPLATE).put(FUNCTION_TEMPLATE_KEY, template);
			preferences.node(MAPPER_INSERT_TEMPLATE).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String getMapperFunctionUpdateTemplate() {
		return preferences.node(MAPPER_UPDATE_TEMPLATE).get(FUNCTION_TEMPLATE_KEY, "\t<!--\n\tDesc : 수정\n\tTABLE : [table]\n\t-->\n\t<update id=\"update[capitalizePrefix]\" parameterType=\"[paramName]\">\n\t\tUPDATE [table] SET\n\t\t\t[columns]\n\t\tWHERE 1=1\n\t\t\t[indexColumns]\n\t</update>");
	}

	public void setMapperFunctionUpdateTemplate(String template) {
		try {
			preferences.node(MAPPER_UPDATE_TEMPLATE).put(FUNCTION_TEMPLATE_KEY, template);
			preferences.node(MAPPER_UPDATE_TEMPLATE).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String getMapperFunctionDeleteTemplate() {
		return preferences.node(MAPPER_DELETE_TEMPLATE).get(FUNCTION_TEMPLATE_KEY, "\t<!--\n\tDesc : 삭제\n\tTABLE : [table]\n\t-->\n\t<delete id=\"delete[capitalizePrefix]\" parameterType=\"[paramName]\">\n\t\tDELETE FROM [table] WHERE 1=1 [indexColumns]\n\t</delete>");
	}

	public void setMapperFunctionDeleteTemplate(String template) {
		try {
			preferences.node(MAPPER_DELETE_TEMPLATE).put(FUNCTION_TEMPLATE_KEY, template);
			preferences.node(MAPPER_DELETE_TEMPLATE).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String[] getJspTemplateNames(){
		try {
			Preferences node = preferences.node(JSP_TEMPLATE_NAME);
			return node.keys();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return new String[]{};
	}

	public String getJspTemplateListFile(String key) {
		return preferences.node(JSP_TEMPLATE_LIST_FILE).get(key, null);
	}

	public String getJspTemplatePostFile(String key) {
		return preferences.node(JSP_TEMPLATE_POST_FILE).get(key, null);
	}

	public String getJspTemplateViewFile(String key) {
		return preferences.node(JSP_TEMPLATE_VIEW_FILE).get(key, null);
	}

	public String[] jspPropertiesFlush(Tree templateTree){
		try {
			preferences.node(JSP_TEMPLATE_NAME).clear();
			preferences.node(JSP_TEMPLATE_LIST_FILE).clear();
			preferences.node(JSP_TEMPLATE_POST_FILE).clear();
			preferences.node(JSP_TEMPLATE_VIEW_FILE).clear();
			TreeItem[] templateItems = templateTree.getItems();
			for(TreeItem template : templateItems) {
				preferences.node(JSP_TEMPLATE_NAME).put(template.getText(), template.getText());
				preferences.node(JSP_TEMPLATE_LIST_FILE).put(template.getText(), (String) template.getItems()[0].getData());
				preferences.node(JSP_TEMPLATE_POST_FILE).put(template.getText(), (String) template.getItems()[1].getData());
				preferences.node(JSP_TEMPLATE_VIEW_FILE).put(template.getText(), (String) template.getItems()[2].getData());
			}
			preferences.node(JSP_TEMPLATE_NAME).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

		return getJspTemplateNames();
	}

	public String[] getDataTypes(){
		try {
			Preferences node = preferences.node(VO_DATA_TYPE);
			return node.keys();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return new String[]{};
	}

	public String getJavaObject(String key) {
		return preferences.node(VO_DATA_TYPE).get(key, null);
	}

	public String[] dataTypeFlush(Table mappingTable){
		try {
			preferences.node(VO_DATA_TYPE).clear();
			TableItem[] tableItems = mappingTable.getItems();
			for(TableItem tableItem : tableItems) {
				preferences.node(VO_DATA_TYPE).put(tableItem.getText(0), tableItem.getText(1));
			}
			preferences.node(JSP_TEMPLATE_NAME).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

		return getDataTypes();
	}
}
