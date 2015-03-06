package com.mosun.saveredis;

import java.awt.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.codehaus.jackson.type.TypeReference;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.ReadOptions;

import com.google.common.base.Strings;
import com.mosun.saveredis.util.JsonUtil;
import com.mosun.saveredis.util.Tuple;
import com.mosun.saveredis.util.ZsetItem;

import redis.clients.jedis.Jedis;

public class Restore {
	private static final int MAX_LENGTH=2000;
	private static final Logger logger = Logger.getLogger(Restore.class);
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		String path = Restore.class.getClassLoader().getResource("").getPath();
        path = path + "conf/log4j.xml";
        System.out.println( "configuring log4j.xml at " + path );
        DOMConfigurator.configure(path);
		Jedis j = new Jedis(RedisConfig.getHost(), RedisConfig.getPort());
		try{
			j.connect();
		}catch(Exception e){
			e.printStackTrace();
			j.close();
			return;
		}
		
		DBIterator it = MainProc.DATABASE.iterator(new ReadOptions());
		while(it.hasNext()){
			Entry<byte[],byte[]> entry = it.next();
			
			String key = new String(entry.getKey());
			String v = new String(entry.getValue());
			logger.debug("k:" + key +" v:" + v);
			if (v.isEmpty()){
				v="S";
			}
			String type = v.substring(0,1);
			String value = v.substring(1);
			logger.debug("type:"+type+"["+key+"]=[" + value+"]");
			
			switch(type){
			case "S"://string
				j.del(key);
				j.set(key, value);
				break;
			case "H"://hashmap
				j.del(key);
				Map<String, String> map = (Map<String, String>)JsonUtil.getInstance().readValue(value, Map.class);
				j.hmset(key, map);
				break;
			case "L"://list
				j.del(key);
				ArrayList<String> list = (ArrayList<String>)JsonUtil.getInstance().readValue(value, ArrayList.class);
				int len = list.size();
				if (len <= 0){
					break;
				}
				for(String s : list){
					j.rpush(key, s);
				}
				break;
			case "Z"://zset
				j.del(key);
				ArrayList<ZsetItem> zset = (ArrayList)JsonUtil.getInstance().readValue(value, new TypeReference<ArrayList<ZsetItem>>() {
				});
				
				//Set<Tuple> zset = (Set<Tuple>)JsonUtil.getInstance().readValue(value, Set.class);
				for(ZsetItem t : zset){
					j.zadd(key, t.getScore(), t.getElement());
				}
				break;
			case "T"://set
				j.del(key);
				Set<String> sets = (Set<String>)JsonUtil.getInstance().readValue(value, Set.class);
				for(String string :sets){
					j.sadd(key, string);
				}
				break;
			default:
				break;
			}
		}
		j.close();
	}

}
