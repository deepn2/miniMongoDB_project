package io.github.test;

import io.github.sqlconnection.BaseConnection;
import java.util.*;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;



public class MongoConnection {
	
	public static void main(String[] args){
		BaseConnection bc = new BaseConnection();
		bc.connect();
		bc.showDBs();
		
		bc.setDBAndCollection("cs336","unlabel_review");
		DBCursor unsplit = bc.showRecords();
		ArrayList<Review> listofReviews = new ArrayList<Review> ();
		HashMap<String, Double> idf = new HashMap<String, Double>();
		
		//int rand; //use with randInt later
		
		while(unsplit.hasNext()){
			DBObject unsplitColl = unsplit.next();
			Review review = new Review ((String) unsplitColl.get("id"), (String) unsplitColl.get("review"));
			review.updateTFs();
			listofReviews.add(review);
		}
		
		calcIdf(listofReviews, idf);
		
		//Forming R, a list of 6 random reviews and storing their TF information
		ArrayList<Review> setofReviews = new ArrayList<Review>();
		int count = 0;
		while (count < 6) {
			int rand = randNum(0, listofReviews.size() - 1);
			Review rndReview = listofReviews.get(rand);
			if (!setofReviews.contains(rndReview)) {
				setofReviews.add(rndReview);
				count++;
			}
		}
		
		for (Review review : setofReviews) {
			updateTFIDFs(review, idf);
		}
		calcIdf(listofReviews,idf);
		 
		/* Pick one R* (review) from the set listOperating randomly */
		
		 int randm = randNum (0, setofReviews.size()  - 1 );
		 Review rStar = setofReviews.get(randm);
		 String [] rviwWords = rStar.getReview().toLowerCase().split("\\W+");
		
		 /*creating the query*/
		
		 int word1 = randNum (0, rviwWords.length-1), word2 = randNum(0, rviwWords.length-1);
		 if (word1 == word2) {
			 word2 = randNum(0,rviwWords.length -1);
	 }
	Review  Q = new Review("1992", "\"" + rviwWords[word1] + " " + rviwWords[word2] + "\"");
		 
			//Calculate N
	
			HashMap<String, Integer> CF =  new HashMap<String,Integer> ();
		
			HashMap<String,Integer> DF = new HashMap<String,Integer> ();		
			String[] queryWords = Q.getReview().split("\\W+"); //assign query
			for(int k = 1; k < queryWords.length; k++){
				int z = 0;
				int c = 0;
				
				for (Review review: listofReviews ) { 
					
					String [] reviewWords = review.getReview().toLowerCase().split("\\W+");
					
						for( int i = 1; i< reviewWords.length; i++)
					{  
						 if(reviewWords[i].equals(queryWords[k]))
						   z++;
						
					}
					for( int i = 1; i< reviewWords.length; i++)
					{  
						 if(reviewWords[i].equals(queryWords[k]))
						   c++;
						  if(reviewWords[i].equals(queryWords[k]))
							  break;
					}}
				 
				 CF.put(queryWords[k], z);
				 DF.put(queryWords[k], c);
				
				 
			}
				
		for (String term : DF.keySet()) {
		
		System.out.println("Number of occurences of - " + term + " - in the collection (the collection frequency) is : " + CF.get(term));
			
		System.out.println("Number of reviews containing -" + term + "- : " + DF.get(term));
		}
		System.out.println("Number of unique words in the list of Reviews: " + calcV(setofReviews));
		System.out.println();
		
		// Calculates cosine similarity for each review to Q
		Q.updateTFs();
		updateTFIDFs(Q,idf);
		System.out.println("TF values for query:");
		printHashMap(Q.getTF());
		System.out.println();
		System.out.println("TF-IDF values for query:");
		printHashMap(Q.getTFIDF());
		System.out.println();
		cosineSim(Q, setofReviews);
		
		System.out.println();
		
		System.out.println("Cosine similarity of randomly selected review R* to the set of 5 reviews now excluding R*");
		setofReviews.remove(rStar);
		cosineSim(rStar, setofReviews);
		
		bc.close();
			
	}		
	
	public static void printHashMap(HashMap<String, Double> map) {
		for (String word : map.keySet()) {
			System.out.println("(" + word + ", " + map.get(word) + ")");
		}
	}
	public static void calcIdf(ArrayList <Review> listofAllReviews, HashMap <String, Double> idf) {
		for(Review nxtReview: listofAllReviews ) {
			String[] reviewWords = nxtReview.getReview().toLowerCase().split("\\W+");
			for ( int n = 1; n< reviewWords.length; n++){
				if (!idf.containsKey(reviewWords[n])) {
					idf.put(reviewWords[n], 1.0);
				}
				else {
					idf.put(reviewWords[n], 1.0 + idf.get(reviewWords[n])); //add to it
				}
			}
		}
		
		for(String term : idf.keySet()) {
			idf.put(term, Math.log10(listofAllReviews.size()/idf.get(term)));
		}
	}
	public static int calcV(ArrayList<Review> reviews_list) {
		ArrayList<String> unique_words = new ArrayList<String>();
		for (Review r : reviews_list) {
			String[] transcript_array = r.getReview().toLowerCase().split("\\W+");
			for (int i = 1; i < transcript_array.length; i++) {
				if (!unique_words.contains(transcript_array[i])) {
					unique_words.add(transcript_array[i]);
				}
			}
		}
		
		return unique_words.size();
	}
	public static void cosineSim(Review ran, ArrayList<Review> listofAllReviews)
	{
		double cosine;
		for (Review rev: listofAllReviews) {
			double randomReviewTotal = 0, reviewTotal = 0, bothTotal = 0;
			
			HashSet<String> union = new HashSet<String>();
			union.addAll(rev.getTFIDF().keySet());
			union.addAll(ran.getTFIDF().keySet());
		
			for (String word : union) {
				double reviewTfidf = 0, randomTfidf = 0;
				
				if (ran.getTFIDF().containsKey(word)) {
					randomTfidf = ran.getTFIDF().get(word);
				}
				if (rev.getTFIDF().containsKey(word)) {
					reviewTfidf = rev.getTFIDF().get(word);
				}
				bothTotal += (reviewTfidf * randomTfidf);
				randomReviewTotal += Math.pow(randomTfidf, 2);
				reviewTotal += Math.pow(reviewTfidf, 2);
			}
			randomReviewTotal = Math.sqrt(randomReviewTotal);
			reviewTotal = Math.sqrt(reviewTotal);
			cosine = bothTotal / (randomReviewTotal * reviewTotal);
			
			//Print out relevant info
			System.out.println(ran.toString());
			System.out.println(rev.toString());
			System.out.println("Cosine value: " + cosine + "\n");
		}
	}
	public static HashMap<String, Integer> calcN(Review Q, ArrayList<Review> listofReviews2)
	{
		HashMap<String,Integer> nDocs = new HashMap<String,Integer> ();
		String[] queryWords = Q.getReview().split("\\W+"); //return query
		for(int k = 0; k < queryWords.length; k++){
			int z = 0;
			for (Review r1: listofReviews2 ) {   
				String [] reviewWords = r1.getReview().toLowerCase().split("\\W+");
		

				for( int i =0; i< reviewWords.length; i++)
				{  
				 if (reviewWords[i].equals(queryWords))
				{
					z++;
				}
				}}
				nDocs.put((queryWords[k]),z);
		}
		return nDocs;
	}
	public static int randNum(int min, int max) {
	//order to get a specific range of values first, you need to multiply by the magnitude of the range of values you want covered.
	//we add the 1, because it does not include it -- [0,1) is the range
		return min + (int)(Math.random() * ((max - min) + 1));
	}
	public static void updateTFIDFs(Review r, HashMap<String, Double> idf) {
		for (String word : r.getTF().keySet()) {
			r.getTFIDF().put(word, r.getTF().get(word) * idf.get(word));
		}
	}
}
