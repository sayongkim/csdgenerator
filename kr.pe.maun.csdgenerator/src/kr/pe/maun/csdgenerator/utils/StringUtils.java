package kr.pe.maun.csdgenerator.utils;

public class StringUtils {

	public static String toCamelCase(String source) {
	    StringBuffer result = new StringBuffer();
	    String[] sourceArray = source.split("_");
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
	    return result.toString();
	}

}
