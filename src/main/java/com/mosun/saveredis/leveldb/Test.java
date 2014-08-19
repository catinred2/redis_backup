/**
 * 
 */
package com.mosun.saveredis.leveldb;

/**
 * @Description: TODO
 * @author ming
 * @date 2014年8月18日 下午4:20:29
 */

import java.io.Serializable;
import java.util.Arrays;

import redis.clients.util.SafeEncoder;

public class Test implements  Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3638625558815799861L;
	private String element;
	private Double score;

	public Test(String element, Double score) {
		super();
		this.setElement((element));
		this.score = score;
	}

	public Test(byte[] element, Double score) {
		super();
		this.setElement(new String(element));
		this.score = score;
	}

	/**
	 * @return the element
	 */
	public String getElement() {
		return this.element;
	}

	/**
	 * @param element the element to set
	 */
	
	public void setElement(String element) {
		this.element = (element);
	}
}