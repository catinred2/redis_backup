/**
 * 
 */
package com.mosun.saveredis.leveldb;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.ReadOptions;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.iq80.leveldb.impl.Level;
import org.iq80.leveldb.util.DbIterator;
import org.iq80.leveldb.util.FileUtils;

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
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
	public Database(String _filename,Options _options) {
		this.filename = _filename;
		options = _options;
	}
	public boolean hotCopy(String target){
		
			return FileUtils.copyDirectoryContents(Paths.get(this.filename).toFile(), Paths.get(target).toFile());
			
	}
	public static String GenBackupName(){
		return sdf.format(Calendar.getInstance().getTime());
	}
	public void init() throws IOException{
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
