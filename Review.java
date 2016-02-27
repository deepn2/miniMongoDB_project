//package io.github.test;
//
//import java.util.HashMap;
//
//public class Review {
//	
//	private String id;
//	private String review;
//	private HashMap<String, Double> tf;
//	private HashMap<String, Double> tfidf;
//	
//	public Review (String id, String review) {
//			this.id = id;
//			this.review = review;
//			tf = new HashMap<String, Double> ();
//			tfidf = new HashMap<String, Double>();
//	
//}
//
//	public void calcTf()
//	{
//		String[] rawview = review.toLowerCase().split("\\W+");
//		for(int i = 1; i < rawview.length; i++)
//			if (!tf.containsKey(rawview[i])){
//				tf.put(rawview[i], 1.0);
//			}
//			else{
//				tf.put(rawview[i], 1.0 + tf.get(rawview[i]));
//			}
//	
//	for (String term: tf.keySet()){
//			tf.put(term, 1 + Math.log10(tf.get(term)));
//	}
//	}
//	
//	public void calcTFIDF(HashMap<String, Double>idf)
//	{
//		for(String term: tf.keySet()){
//				tfidf.put(term,  tf.get(term)*idf.get(term));
//		}}
//	
//	public void printTfs(){
//		for (String term: tf.keySet()){
//				System.out.println(term + ":" + tf.get(term));
//				}
//	}
//	public void printTFIDF(){
//		for(String term : tfidf.keySet()){
//			System.out.println(term + ":" + tf.get(term));
//		}
//	}
//	public String toJSON() {
//		return "{id: " +id + ", review: " + review + "}";
//	}
//	public String retReview() {
//		return review;
//	}
//	public HashMap<String, Double> getTf() {
//		return tf;
//	}
//	public HashMap<String, Double> getTfidf() {
//		return tfidf;
//	}
//		}

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