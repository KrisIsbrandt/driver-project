package pl.coderslab.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

	//Folder location where files are stored
	@Value("${storage.location}")
	private String location;

	@Value("${storage.allowedFormats}")
	private static String[] allowedFormats;

	public static boolean allowedFormat(String fileFormat) {
		for (String allowedFormat : allowedFormats) {
			if (allowedFormat.equals(fileFormat)) {
				return true;
			}
		}
		return false;
	}

	//Getters & Setters
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String[] getAllowedFormats() {
		return allowedFormats;
	}

	public void setAllowedFormats(String[] allowedFormats) {
		this.allowedFormats = allowedFormats;
	}
}
