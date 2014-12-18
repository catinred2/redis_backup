/**
 * 
 */
package com.mosun.saveredis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/**
 * @Description: TODO
 * @author ming
 * @date 2014年8月13日 下午5:27:41
 */
public class RedisConfig {
	
	private volatile static RedisConfig instance=null;
	private Properties appSettings;
	public static RedisConfig getInstance(){
		if (instance==null){
			synchronized (RedisConfig.class) {
				if (instance==null){
					instance = new RedisConfig();
				}
			}
		}
		return instance;
	}
	private RedisConfig(){
		if (this.appSettings==null){
			this.appSettings = new Properties();
			try {
				//this.appSettings.load(getClass().getResourceAsStream("/config.properties"));
				InputStream in =this.getClass().getClassLoader().getResourceAsStream("conf/config.properties");
                this.appSettings.load(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private static String getConfigProperty(String key){
		return RedisConfig.getInstance().appSettings.getProperty(key);
	}
	public static String getHost(){
		return getConfigProperty("redis.host");
	}
	public static int getPort(){
		return Integer.parseInt(getConfigProperty("redis.port"));
	}
	public static String getLevelDBFileName(){
		return getConfigProperty("leveldb.filename");
	}
	public static int getSystemPort(){
		return Integer.parseInt(getConfigProperty("system.port"));
	}
}
