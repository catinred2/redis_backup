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

	public static void Put(String str) throws InterruptedException{
		queue.put(str);
	}
	public static String Take() throws InterruptedException{
		return queue.take();
	}
}
