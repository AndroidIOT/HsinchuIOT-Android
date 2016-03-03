package org.slstudio.hsinchuiot.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slstudio.hsinchuiot.Constants;

public class ConfigurationService {

	public static final String CFG_FILENAME = "hsinchu.properties";

	private Properties properties = null;
	
	public ConfigurationService() {
		try {
			String path = Constants.CONFIG_FILE_PATH;
			File f = new File(path);
			if (!f.exists()) {
				f.mkdirs();
			}
			String configFilename = path + CFG_FILENAME;
			File configFile = new File(configFilename);
			if (!configFile.exists()) {
				configFile.createNewFile();

			}

			FileInputStream fis = new FileInputStream(configFile);
			properties = new Properties();
			properties.load(fis);
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getConfig(String configName){
		return properties.getProperty(configName);
	}
	
	public void saveConfig(String configName, String configValue){
		properties.setProperty(configName, configValue);
		try {
			String path = Constants.CONFIG_FILE_PATH;
			File f = new File(path);
			if (!f.exists()) {
				f.mkdirs();
			}
			String configFilename = path + CFG_FILENAME;
			File configFile = new File(configFilename);
			if (!configFile.exists()) {
				configFile.createNewFile();

			}

			FileOutputStream fos = new FileOutputStream(configFile);
			properties.store(fos, null);
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
