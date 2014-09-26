/**
 * 
 */
package com.mosun.saveredis;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import redis.clients.jedis.exceptions.JedisConnectionException;

import com.mosun.saveredis.util.*;



/**
 * @Description: TODO
 * @author ming
 * @date 2014年8月13日 下午5:53:19
 */
public class Monitor implements Runnable{
	private static final Logger logger = Logger.getLogger(Monitor.class);
    
	private RedisInputStream ris;
	public Monitor(RedisInputStream in){
		ris = in;
	}
	

	@Override
	public void run() {
		
		System.out.println("monitor thread start...");
		while(true){
			//String msg = ris.readLine();
			//System.out.println(msg);
			List<Object> obj = null;
			obj = (List<Object>)Protocol.read(ris);
			
			if (obj==null){
				continue;
			}
			if (obj.size()==3){
				logger.debug("subscribe ok");
			}
			else{
				List<String> response = BuilderFactory.STRING_LIST.build(obj);
				String key = response.get(3);
				if (key.startsWith("NetworkStatics")){
					continue;
				}
				try {
					//logger.debug("put queue:" + System.currentTimeMillis());
					KeyQueue.Put(response.get(3));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
			}
		}
		
		
	}
	
}
