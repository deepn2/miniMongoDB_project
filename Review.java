package io.github.test;

import java.util.*;

public class Review {
	
	//instance fields
	private String id;
	private String review;
	private HashMap<String, Double> tf;
	private HashMap<String, Double> tfidf;
	
	/**
	 * Constructor
	 */
	public Review(String id, String review) {
		this.id = id;
		this.review = review;
		tf = new HashMap<String, Double>();
		tfidf = new HashMap<String, Double>();
	}
	
	public String getID() {
		return id;
	}
	
	public String getReview() {
		return review;
	}
	
	public HashMap<String, Double> getTF() {
		return tf;
	}
	
	public HashMap<String, Double> getTFIDF() {
		return tfidf;
	}
	
	/**
	 * Updates a review's term-frequency values
	 */
	public void updateTFs() {
		String[] review_text = review.toLowerCase().split("\\W+");
		for (int i = 1; i < review_text.length; i++) {
			if (!tf.containsKey(review_text[i])) {
				tf.put(review_text[i], 1.0);
			}
			else {
				tf.put(review_text[i], 1.0 + tf.get(review_text[i]));
			}
		}
		for (String word : tf.keySet()) {
			tf.put(word, 1.0 + Math.log10(tf.get(word)));
		}
	}
	
	/**
	 * Returns Review in json format
	 */
	public String toString() {
		return "{id: " + id + ", review: " + review + "}";
	}
}
