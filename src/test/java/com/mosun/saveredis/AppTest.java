package com.mosun.saveredis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map.Entry;

import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.ReadOptions;

import com.mosun.saveredis.leveldb.Database;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
       // MainProc.main(null);
        //ask:e02526f693f143ffa2ca3f13a3478fab:content
    	
    	Path path = Paths.get("");
    	String stringPath = path.toAbsolutePath().getParent().toString();
    	String dbFile = stringPath + "/leveldb.db";
    	String logFile = stringPath +"/dump";
    	File f = new File(logFile);
    	OutputStreamWriter osw = null;
    	try {
			osw = new OutputStreamWriter(new FileOutputStream(f));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	Database db = new Database(dbFile, null);
    	try {
			db.init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	int count =0;
    	StringBuilder sb = new StringBuilder();
    	DBIterator it = db.iterator(new ReadOptions());
    	while(it.hasNext()){
    		count++;
    		Entry<byte[], byte[]> entry = it.next();
			byte[] byteKey = entry.getKey();
			byte[] byteValue = entry.getValue();
			sb.append(new String(byteKey));
			sb.append("=");
			sb.append(new String(byteValue));
			try {
				osw.write(sb.toString());
				osw.write("\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sb.setLength(0);
    	}
    	System.out.println("total:" + count);
    	db.close();
    	try {
			osw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
