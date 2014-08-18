/**
 * 
 */
package com.mosun.saveredis;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import com.mosun.saveredis.leveldb.Database;
import com.mosun.saveredis.util.BuilderFactory;
import com.mosun.saveredis.util.JsonUtil;
import com.mosun.saveredis.util.Protocol;
import com.mosun.saveredis.util.RedisInputStream;
import com.mosun.saveredis.util.RedisOutputStream;
import com.mosun.saveredis.util.SafeEncoder;
import com.mosun.saveredis.util.Protocol.Command;

/**
 * @Description: TODO
 * @author ming
 * @date 2014年8月14日 下午2:50:43
 */
public class RedisToDb implements Runnable{
	private boolean flag = true;
	private static final Logger logger=Logger.getLogger(RedisToDb.class);
	Socket socket;
	RedisInputStream ris;
	RedisOutputStream ros;
	public RedisToDb(){
		
	}
	@Override
	public void run(){
		Jedis j = new Jedis(RedisConfig.getHost(),RedisConfig.getPort());
		String value = null;
		long take_time,finish_time,total_time=0;
		int count=0;
		Database db = new Database("./test.db");
		try {
			db.init();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		while(flag){
			String key;
			try {
				key = KeyQueue.Take();
				if (KeyQueue.MAGIC_WORD.equals(key)){
					break;
				}
				String type = j.type(key);
				switch (type){
				case "string":
					value = j.get(key);
					break;
				case "hashmap":
					Map<String, String> hashMap = j.hgetAll(key);
					value = JsonUtil.getInstance().writeValue(hashMap);
					break;
				case "list":
					List<String> list = j.lrange(key, 0, -1);
					value = JsonUtil.getInstance().writeValue(list);
					break;
				case "zset":
					Set<Tuple> zset = j.zrangeWithScores(key, 0, -1);
					value = JsonUtil.getInstance().writeValue(zset);
					break;
				case "set":
					Set<String> set = j.smembers(key);
					value = JsonUtil.getInstance().writeValue(set);
					break;
				default:
					break;
				}
				if (value!=null && !value.isEmpty()){
					// save to db
					//count++;
					logger.debug(String.format("key=%s type=%s\r\nvalue=%s",key,type, value));
					//take_time = System.currentTimeMillis();
					db.write(key, value);
//					finish_time = System.currentTimeMillis();
//					total_time = total_time + finish_time - take_time;
//					if (count==10000){
//						logger.debug("=============time=" + total_time);
//					}
				}else{
					//delete from db??
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			
		}
		logger.debug("ok,quit now.");
		db.close();
		db = null;
		j.close();
		j=null;
	}
	
	public void run1() {
		try {
			socket = new Socket(RedisConfig.getHost(),RedisConfig.getPort());
			ris = new RedisInputStream(socket.getInputStream());
			ros = new RedisOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		String value = null;
		long take_time,finish_time;
		while(true){
			try {
				String key = KeyQueue.Take();
				ros.write(SafeEncoder.encode("type " + key));
				ros.writeCrLf();
				ros.flush();
				String type = BuilderFactory.STRING.build(Protocol.read(ris));
				switch (type){
				case "string":
					//Protocol.sendCommand(ros, Command.GET, SafeEncoder.encode("get " + key));
					ros.write(SafeEncoder.encode("get " + key));
					ros.writeCrLf();
					ros.flush();
					value = BuilderFactory.STRING.build(Protocol.read(ris));
					break;
				case "hashmap":
					ros.write(SafeEncoder.encode("hgetall " + key));
					ros.writeCrLf();
					ros.flush();
					
					break;
				case "list":
					break;
				case "zset":
					break;
				case "set":
					break;
				default:
					break;
				}
				logger.debug("read from redis:value="+value);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
