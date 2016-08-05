package kr.pe.maun.csdgenerator.utils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import kr.pe.maun.csdgenerator.model.CSDGeneratorPropertiesItem;
import kr.pe.maun.csdgenerator.model.ColumnItem;

public class StringUtils {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy. MM. dd");

	public static String toCamelCase(String source) {
	    StringBuffer result = new StringBuffer();
		try {
		    if(source.indexOf("_") != -1) {
		    	String[] sourceArray = source.toLowerCase().split("_");
			    result.append(sourceArray[0]);
			    for (int i = 1; i < sourceArray.length; i++) {
			    	String s = sourceArray[i];
			        result.append(Character.toUpperCase(s.charAt(0)));
			        if (s.length() > 1) {
			            result.append(s.substring(1, s.length()));
			        }
			    }
		    } else {
		    	int upperCaseCount = 0;
		    	byte[] sourceBytes = source.getBytes("UTF-8");
		    	for(Byte sourceByte : sourceBytes) {
		    		if(0x40 < sourceByte && sourceByte < 0x5B) {
		    			upperCaseCount++;
		    		}
		    	}
		    	if(source.length() == upperCaseCount) {
		    		result.append(source.toLowerCase());
		    	} else {
		    		result.append(source);
		    	}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
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


	public static String replaceMapperSelecColumn(List<ColumnItem> columnItems) {
		StringBuffer result = new StringBuffer();
		if(columnItems.size() > 0) {
			for (int i = 0; i < columnItems.size(); i++) {
				ColumnItem columnItem = columnItems.get(i);
				if(i > 0) result.append("\t\t\t,");
				result.append(columnItem.getColumnName().toUpperCase());
				result.append(" AS ");
				result.append(StringUtils.toCamelCase(columnItem.getColumnName()));
				if(i < (columnItems.size() - 1)) result.append("\n");
			}
		}
		return result.toString();
	}

	public static String replaceMapperInsertColumn(List<ColumnItem> columnItems) {
		StringBuffer result = new StringBuffer();
		if(columnItems.size() > 0) {
			for (int i = 0; i < columnItems.size(); i++) {
				ColumnItem columnItem = columnItems.get(i);
				if(i > 0) result.append("\t\t\t,");
				result.append(columnItem.getColumnName().toUpperCase());
				if(i < (columnItems.size() - 1)) result.append("\n");
			}
		}
		return result.toString();
	}

	public static String replaceMapperInsertValue(List<ColumnItem> columnItems) {
		StringBuffer result = new StringBuffer();
		if(columnItems.size() > 0) {
			for (int i = 0; i < columnItems.size(); i++) {
				ColumnItem columnItem = columnItems.get(i);
				if(i > 0) result.append("\t\t\t,");
				result.append("#{");
				result.append(StringUtils.toCamelCase(columnItem.getColumnName()));
				result.append("}");
				if(i < (columnItems.size() - 1)) result.append("\n");
			}
		}
		return result.toString();
	}

	public static String replaceMapperUpdateColumn(List<ColumnItem> columnItems) {
		StringBuffer result = new StringBuffer();
		int size = columnItems.size();
		if(size > 0) {
			for (int i = 0; i < size; i++) {
				ColumnItem columnItem = columnItems.get(i);
				if(i > 0) result.append("\t\t\t,");
				result.append(columnItem.getColumnName().toUpperCase());
				result.append(" = #{");
				result.append(StringUtils.toCamelCase(columnItem.getColumnName()));
				result.append("}");
				if(i < (size - 1)) result.append("\n");
			}
		}
		return result.toString();
	}

	public static String replaceMapperIndexColumn(List<String> indexColumns) {
		StringBuffer result = new StringBuffer();
		int size = indexColumns.size();
		if(size > 0) {
			for (int i = 0; i < size; i++) {
				String column = indexColumns.get(i);
				if(i > 0) result.append("\t\t\t");
				result.append("AND ");
				result.append(column.toUpperCase());
				result.append(" = #{");
				result.append(StringUtils.toCamelCase(column));
				result.append("}");
				if(i < (size - 1)) result.append("\n");
			}
		}
		return result.toString();
	}

	public static String replaceRepeatWord(String content, List<ColumnItem> columns) {
		int size = columns.size();
		if(size > 0 && (content.indexOf("/*r:s*/") > -1 || content.indexOf("<!--r:s-->") > -1)) {
			String[] contentItems = content.split("\n");
			boolean isReservedWord = false;
			String replaceTarget = "";
			String replaceContent = "";
			for(String contentItem : contentItems) {
				if(!isReservedWord && (contentItem.indexOf("/*r:s*/") > -1 || contentItem.indexOf("<!--r:s-->") > -1)) {
					isReservedWord = true;
				} else if(isReservedWord && contentItem.indexOf("/*r:e*/") == -1 && contentItem.indexOf("<!--r:e-->") == -1) {
					replaceTarget += "\n";
					replaceTarget += contentItem;
				} else if(isReservedWord && (contentItem.indexOf("/*r:e*/") > -1 || contentItem.indexOf("<!--r:e-->") > -1)) {
					isReservedWord = false;
					for(ColumnItem columnItem : columns) {
						String tempContent = replaceTarget;
						if(replaceTarget.indexOf("et[column]") > -1) {
							tempContent = tempContent.replaceAll("et\\[column\\]", StringUtils.toCamelCase("et_" + columnItem.getColumnName()));
						} else {
							tempContent = tempContent.replaceAll("\\[column\\]", StringUtils.toCamelCase(columnItem.getColumnName()));
						}
						tempContent = tempContent.replaceAll("\\[comment\\]", columnItem.getComments());
						replaceContent += tempContent;
					}
					content = content.replace(replaceTarget, replaceContent);
					replaceTarget = "";
					replaceContent = "";
				}
			}
		}
		return content;
	}

}
