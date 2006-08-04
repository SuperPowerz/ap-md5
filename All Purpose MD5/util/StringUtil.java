package util;

public class StringUtil {

	public static boolean equalIgnoreCase(String s1, String s2){
		boolean isEqual = true;
		
		if(s1 == null){
			if(s2 != null){
				isEqual = false;
			}
		} else {
			isEqual = s1.equalsIgnoreCase(s2);
		}
		
		return isEqual;
	}
}

