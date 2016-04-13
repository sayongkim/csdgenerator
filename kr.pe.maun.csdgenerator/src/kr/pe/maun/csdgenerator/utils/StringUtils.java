package kr.pe.maun.csdgenerator.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import kr.pe.maun.csdgenerator.model.CSDGeneratorPropertiesItem;

public class StringUtils {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy. MM. dd");

	public static String toCamelCase(String source) {
	    StringBuffer result = new StringBuffer();
	    String[] sourceArray = source.split("_");
	    if(sourceArray.length > 1) {
		    result.append(sourceArray[0].toLowerCase());
		    if(sourceArray.length > 1) {
			    for (int i = 1; i < sourceArray.length; i++) {
			    	String s = sourceArray[i];
			        result.append(Character.toUpperCase(s.charAt(0)));
			        if (s.length() > 1) {
			            result.append(s.substring(1, s.length()).toLowerCase());
			        }
			    }
		    }
	    } else {
	    	result.append(source);
		}
	    return result.toString();
	}

	public static String appedFirstAndEndNewLine(String source) {
		String result = "";
		if(!source.startsWith("\n\n")) {
			result += "\n\n";
		}
		result += source;
		if(!source.endsWith("\n\n")) {
			source += "\n\n";
		}
		return result;
	}

	public static String replaceReservedWord(CSDGeneratorPropertiesItem propertiesItem, String prefix, String source) {

		String date = dateFormat.format(new Date());

		String company = propertiesItem.getCompany() != null ? propertiesItem.getCompany() : "";
		String author = propertiesItem.getAuthor() != null ? propertiesItem.getAuthor() : System.getProperty("user.name");

		String capitalizePrefix = prefix.substring(0, 1).toUpperCase() + prefix.substring(1);

		source = source.replaceAll("\\[prefix\\]", prefix);
		source = source.replaceAll("\\[capitalizePrefix\\]", capitalizePrefix);
		source = source.replaceAll("\\[company\\]", company);
		source = source.replaceAll("\\[author\\]", author);
		source = source.replaceAll("\\[date\\]", date);

		return source;
	}

	public static String replaceParameter(String parameterType, String source) {
		if(parameterType.toLowerCase().indexOf("hashmap") > -1) {
			source = source.replaceAll("\\[searchParamType\\]", parameterType);
			source = source.replaceAll("\\[searchParamName\\]", "searchMap");
			source = source.replaceAll("\\[paramType\\]", parameterType);
			source = source.replaceAll("\\[paramName\\]", "requestMap");
		} else {
			source = source.replaceAll("\\[searchParamType\\]", parameterType);
			source = source.replaceAll("\\[searchParamName\\]", parameterType.toLowerCase().charAt(0) + parameterType.substring(1));
			source = source.replaceAll("\\[paramType\\]", parameterType);
			source = source.replaceAll("\\[paramName\\]", parameterType.toLowerCase().charAt(0) + parameterType.substring(1));
		}
		return source;
	}

	public static String replaceReturn(String returnType, String source) {
		source = source.replaceAll("\\[returnType\\]", returnType);
		return source;
	}

}
