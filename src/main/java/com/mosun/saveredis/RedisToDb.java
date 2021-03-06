/**
 * 
 */
package com.mosun.saveredis;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
import com.mosun.saveredis.util.ZsetItem;
import com.mosun.saveredis.util.Protocol.Command;

/**
 * @Description: TODO
 * @author ming
 * @date 2014年8月14日 下午2:50:43
 */
public class RedisToDb implements Runnable {
	private boolean flag = true;
	private static final Logger logger = Logger.getLogger(RedisToDb.class);
	Socket socket;
	RedisInputStream ris;
	RedisOutputStream ros;

	public RedisToDb() {

	}
	public static void backup(){
		logger.debug("starting backup");
		MainProc.DATABASE.close();
		String name = Database.GenBackupName();
		if (MainProc.DATABASE.hotCopy(name)){
			logger.debug("backup to "+name+" done");
		}
		else{
			logger.debug("backup to "+name+" fails");
		}
		try {
			MainProc.DATABASE.init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		Jedis j = new Jedis(RedisConfig.getHost(), RedisConfig.getPort());
		String value = null;
		long take_time, finish_time, total_time = 0;
		int count = 0;

		while (flag) {
			String key;
			String valueTypeString = "";
			key = KeyQueue.Take();
			if (key==null){
				continue;
			}
			if (KeyQueue.MAGIC_WORD.equals(key)) {
				break;
			}
			if (KeyQueue.HOTCOPY.equals(key)) {
				backup();
				continue;
			}
			String type = j.type(key);
			switch (type) {
			case "string":
				value = j.get(key);
				valueTypeString = "S";
				break;
			case "hash":
				Map<String, String> hashMap = j.hgetAll(key);
				value = JsonUtil.getInstance().writeValue(hashMap);
				valueTypeString = "H";
				break;
			case "list":
				List<String> list = j.lrange(key, 0, -1);
				value = JsonUtil.getInstance().writeValue(list);
				valueTypeString = "L";
				break;
			case "zset":
				Set<Tuple> zset = j.zrangeWithScores(key, 0, -1);
				int size = zset.size();
				if (size<=0){
					value = null;
					break;
				}
				ArrayList<ZsetItem> arrayList = new ArrayList<ZsetItem>(size);
				for(Tuple t:zset){
					ZsetItem item = new ZsetItem();
					item.setElement(t.getElement());
					item.setScore(t.getScore());
					arrayList.add(item);
				}
				value = JsonUtil.getInstance().writeValue(arrayList);
				valueTypeString = "Z";
				break;
			case "set":
				Set<String> set = j.smembers(key);
				value = JsonUtil.getInstance().writeValue(set);
				valueTypeString = "T";
				break;
			default:
				value = null;
				break;
			}
			if (value != null && !value.isEmpty()) {
				// save to db
				// count++;
				// logger.debug(String.format("key=%s type=%s\r\nvalue=%s",key,type,
				// value));
				// take_time = System.currentTimeMillis();
				MainProc.DATABASE.write(key, valueTypeString + value);
				// finish_time = System.currentTimeMillis();
				// total_time = total_time + finish_time - take_time;
				// if (count==10000){
				// logger.debug("=============time=" + total_time);
				// }
			}
			else {
				// delete from db??
				if (type.equals("none")) {
					MainProc.DATABASE.del(key);
				}
			}

		}
		logger.debug("ok,quit now.");
		MainProc.DATABASE.close();

		j.close();
		j = null;
	}

	public void run1() {
		try {
			socket = new Socket(RedisConfig.getHost(), RedisConfig.getPort());
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
		long take_time, finish_time;
		while (true) {
			try {
				String key = KeyQueue.Take();
				ros.write(SafeEncoder.encode("type " + key));
				ros.writeCrLf();
				ros.flush();
				String type = BuilderFactory.STRING.build(Protocol.read(ris));
				switch (type) {
				case "string":
					// Protocol.sendCommand(ros, Command.GET,
					// SafeEncoder.encode("get " + key));
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
				logger.debug("read from redis:value=" + value);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
