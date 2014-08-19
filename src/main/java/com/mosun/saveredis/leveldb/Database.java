/**
 * 
 */
package com.mosun.saveredis.leveldb;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.ReadOptions;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.iq80.leveldb.impl.Level;
import org.iq80.leveldb.util.DbIterator;

import com.mosun.saveredis.RedisConfig;

/**
 * @Description: TODO
 * @author ming
 * @date 2014年8月14日 下午6:07:39
 */
public class Database {
	private DBFactory factory = new Iq80DBFactory();
	private DB db = null;
	private String filename = "";
	private boolean initialized = false;
	private Options options;
	private static Database instance = null;
	public static Database getInstance(){
		if (instance==null){
			synchronized (Database.class) {
				if (instance==null){
					instance = new Database(RedisConfig.getLevelDBFileName(), null);
					try {
						instance.init();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return instance;
	}
	private Database(String _filename,Options _options) {
		this.filename = _filename;
		options = _options;
	}
	private void init() throws IOException{
		if (this.options==null){
			this.options = new Options();
			this.options.createIfMissing(true);
			this.options.writeBufferSize(256<<20);
		}
		
		db = factory.open(new File(filename), this.options);
		initialized = true;
		
	}
	public void write(String key,String value){
		if (!initialized){
			return;
		}
		if (key==null || key.isEmpty()){
			return;
		}
		byte[] byteKey = key.getBytes(StandardCharsets.UTF_8);
		byte[] byteValue = value.getBytes(StandardCharsets.UTF_8);
		db.put(byteKey, byteValue);
		
	}
	public String read(String key){
		if (!initialized){
			return null;
		}
		if (key==null || key.isEmpty()){
			return null;
		}
		byte[] byteKey = key.getBytes(StandardCharsets.UTF_8);
		byte[] byteValue = db.get(byteKey);
		if (byteValue==null) return null;
		return new String(byteValue,StandardCharsets.UTF_8);
		
	}
	public void del(String key){
		if (!initialized){
			return;
		}
		if (key==null || key.isEmpty()){
			return;
		}
		byte[] byteKey = key.getBytes(StandardCharsets.UTF_8);
		db.delete(byteKey);
	}
	public void close(){
		synchronized (Database.class) {
			try {
				if (db!=null){
					db.close();
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				db = null;
			}
			instance = null;
		}
		
	}
	public DBIterator iterator(ReadOptions ro){
		if (!initialized){
			return null;
		}
		return db.iterator(ro);
	}
}
