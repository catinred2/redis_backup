package com.mosun.saveredis;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/*
 *配置�?
 *@author benlin
 *@created 2010-5-19
 *返回为字符串
 *Sample:
 *ConfigProperties.getInstance().appSettings.getProperty("DBAcessServer.host")
 *@modify 2010-6-12
 *增加getConfigProperty()方法
 *Sample�?
 *ConfigProperties.getConfigProperty("genius.spiritpower.dodge");
 * */
public class ConfigProperties {
	
	private static ConfigProperties instance;
	
	private Properties appSettings;
	public void setAppSettings(Properties appSettings) {
		this.appSettings = appSettings;
	}

	public Properties getAppSettings() {
		return appSettings;
	}
	
	public static String getConfigProperty(String key){
		return ConfigProperties.getInstance().appSettings.getProperty(key);
	}
	
	public static ConfigProperties getInstance()
	{
		if(instance==null)
		{
			synchronized (ConfigProperties.class) {
				if(instance==null){
					instance=new ConfigProperties();
				}
			}
			
		}
		return instance;
	}
	
	private ConfigProperties()
	{
		if(this.appSettings==null)
		{
			//是否保存在缓存中
			//如果没有缓存
			this.appSettings = new Properties();
			try {
				InputStream in =this.getClass().getClassLoader().getResourceAsStream("conf/config.properties");
                this.appSettings.load(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		 
		System.out.println(ConfigProperties.getConfigProperty("redis.port"));
	}
}
