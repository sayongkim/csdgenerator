package kr.pe.maun.csdgenerator.properties;

import org.eclipse.osgi.util.NLS;

public class CSDGeneratorMessages extends NLS {

	private static final String BUNDLE_NAME = "kr.pe.maun.csdgenerator.properties.messages";

	public static String FUNCTION_TEMPLATE_SERVICE_SELECT_CNT;
	public static String FUNCTION_TEMPLATE_SERVICE_SELECT_LIST;
	public static String FUNCTION_TEMPLATE_SERVICE_SELECT_ONE;
	public static String FUNCTION_TEMPLATE_SERVICE_INSERT;
	public static String FUNCTION_TEMPLATE_SERVICE_UPDATE;
	public static String FUNCTION_TEMPLATE_SERVICE_DELETE;

	public static String FUNCTION_TEMPLATE_DAO_SELECT_CNT;
	public static String FUNCTION_TEMPLATE_DAO_SELECT_LIST;
	public static String FUNCTION_TEMPLATE_DAO_SELECT_ONE;
	public static String FUNCTION_TEMPLATE_DAO_INSERT;
	public static String FUNCTION_TEMPLATE_DAO_UPDATE;
	public static String FUNCTION_TEMPLATE_DAO_DELETE;

	public static String FUNCTION_TEMPLATE_MAPPER_SELECT_CNT;
	public static String FUNCTION_TEMPLATE_MAPPER_SELECT_LIST;
	public static String FUNCTION_TEMPLATE_MAPPER_SELECT_ONE;
	public static String FUNCTION_TEMPLATE_MAPPER_INSERT;
	public static String FUNCTION_TEMPLATE_MAPPER_UPDATE;
	public static String FUNCTION_TEMPLATE_MAPPER_DELETE;

	static {
		NLS.initializeMessages(BUNDLE_NAME, CSDGeneratorMessages.class);
	}

	private CSDGeneratorMessages() {
	}
}
