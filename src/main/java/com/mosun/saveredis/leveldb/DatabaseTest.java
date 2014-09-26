/**
 * 
 */
package com.mosun.saveredis.leveldb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map.Entry;

import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.ReadOptions;
import org.iq80.leveldb.util.DbIterator;

import com.mosun.saveredis.MainProc;
import com.mosun.saveredis.RedisConfig;
import com.mosun.saveredis.util.JsonUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

/**
 * @Description: TODO
 * @author ming
 * @date 2014年8月14日 下午6:28:29
 */
public class DatabaseTest {
	public static void showDatabase() {
		ReadOptions ro = new ReadOptions();
		int count = 0;
		DBIterator it = MainProc.DATABASE.iterator(ro);
		while (it.hasNext()) {
			count++;
			Entry<byte[], byte[]> entry = it.next();
			byte[] byteKey = entry.getKey();
			byte[] byteValue = entry.getValue();
			System.out.println("key=" + new String(byteKey, StandardCharsets.UTF_8) + "\r\nvalue="
					+ new String(byteValue, StandardCharsets.UTF_8));
		}
		MainProc.DATABASE.close();
		System.out.println("total=" + count);

	}

	public static void main(String[] args) {
		// keepInserting();
		// showDatabase();
		// query();
//		Test tp = new Test("1", (double) 2);
//		String value = JsonUtil.getInstance().writeValue(tp);
//		System.out.println(value);
		
	}

	public static void keepInserting() {
		Jedis j = new Jedis(RedisConfig.getHost(), RedisConfig.getPort());
		for (int i = 0; i < 10000; i++) {
			String key = String.valueOf(i);
			j.set(key, key);

		}
		j.close();
	}
}
