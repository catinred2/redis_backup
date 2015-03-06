package com.mosun.saveredis.util;

public class ZsetItem {
	private String element;
	private Double score;
	/**
	 * @return the element
	 */
	public String getElement() {
		return element;
	}
	/**
	 * @param element the element to set
	 */
	public void setElement(String element) {
		this.element = element;
	}
	/**
	 * @return the score
	 */
	public Double getScore() {
		return score;
	}
	/**
	 * @param score the score to set
	 */
	public void setScore(Double score) {
		this.score = score;
	}
}
