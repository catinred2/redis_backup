/**
 * 
 */
package com.mosun.saveredis;

import java.io.IOException;
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
				this.appSettings.load(getClass().getResourceAsStream("/config.properties"));
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
}
