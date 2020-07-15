package com.api.utilities;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class ResourceHelper {
	public static String getResourcePath(String resource) {
		System.out.println(getBaseResourcePath() + resource);
		return getBaseResourcePath() + resource;
	}
	
	public static String getBaseResourcePath() {
		return System.getProperty("user.dir");
	}
	
	public static FileInputStream getBaseResourceFilePath( String resource) throws FileNotFoundException {
		return new FileInputStream(getResourcePath(resource));
	}

}
