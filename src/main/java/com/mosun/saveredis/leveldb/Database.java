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
	public Database(String _filename) {
		this.filename = _filename;
		
	}
	public void init() throws IOException{
		Options options = new Options();
		options.createIfMissing(true);
		options.writeBufferSize(256<<20);
		db = factory.open(new File(filename), options);
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
	}
	public DBIterator iterator(ReadOptions ro){
		if (!initialized){
			return null;
		}
		return db.iterator(ro);
	}
}
