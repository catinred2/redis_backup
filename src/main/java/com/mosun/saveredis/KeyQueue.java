/**
 * 
 */
package com.mosun.saveredis;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Description: TODO
 * @author ming
 * @date 2014年8月14日 下午1:44:08
 */
public class KeyQueue {
	private static LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
	public final static String MAGIC_WORD = "__NEED_QUIT";
	public final static String HOTCOPY = "__HOT_COPY";
	public static void Put(String str) {
		try {
			queue.put(str);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String Take() {
		try {
			return queue.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
