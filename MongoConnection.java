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
		
		/*
		for (Review r : reviews) {
			updateTFIDF(r, idf);
		}
		*/
		
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
		
//		for (Review r : listofReviews) {
//			updateTFIDFs(r, idf);
//		}
	//	ArrayList<Review> listOperatingOn = new ArrayList<Review> ();

//		for (int i =0; i<=6 ; i++)
//		{
//			
//		int rand = randNum(0, listofReviews.size() -1 );
//		 Review reviewOne = listofReviews.get(rand);
//		 if(!listOperatingOn.contains(reviewOne)){
//			 listOperatingOn.add(reviewOne);}
//		 else
//		 { --i; //go back one iteration or repeat process
//		}
		 
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
//					if (z>0)
//					{
//					c++; //iterates another variable to account for overcounting if multiple words are found in the same review
//					}
				 
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
//	public static void calcTfidf(Review R, HashMap<String,Double> idf)
//	{ 	for( String word : R.getTf().keySet()) {
//		R.getTfidf().put(word,R.getTf().get(word) * idf.get(word));
//	}
//		
	
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




//
//public class MongoConnection {
//	
//	/**
//	 * Main method containing cosine similarity comparison algorithm
//	 */
//	public static void main(String[] args) {		
//		BaseConnection bc = new BaseConnection();
//		bc.connect();
//		
//		bc.setDBAndCollection("cs336", "unlabel_review");
//		DBCursor no_split = bc.showRecords();		
//		ArrayList<Review> reviews = new ArrayList<Review>(); //ArrayList masterlist of reviews
//		HashMap<String, Double> idfs = new HashMap<String, Double>(); //IDF's HashMap for all terms in all reviews
//		
//		while(no_split.hasNext()){
//			DBObject no_split_dbo = no_split.next();
//			Review review = new Review((String) no_split_dbo.get("id"), (String) no_split_dbo.get("review"));
//			review.updateTFs();
//			reviews.add(review);
//		}
//		updateIDFs(reviews, idfs);
//		
//		/*
//		for (Review r : reviews) {
//			updateTFIDF(r, idfs);
//		}
//		*/
//		
//		//Forming R, a list of 6 random reviews and storing their TF information
//		ArrayList<Review> R = new ArrayList<Review>();
//		int count = 0;
//		while (count < 6) {
//			int rand = randInt(0, reviews.size() - 1);
//			Review rand_review = reviews.get(rand);
//			if (!R.contains(rand_review)) {
//				R.add(rand_review);
//				count++;
//			}
//		}
//		
//		for (Review sample_review : R) {
//			updateTFIDFs(sample_review, idfs);
//		}
//		
//		//Picking r* from R
//		int rand2 = randInt(0, R.size() - 1);
//		Review r_star = R.get(rand2);
//		
//		//Calculates N, number of documents that contain each word in the query
//		Review query = makeQuery(r_star);
//		HashMap<String, Integer> N = new HashMap<String, Integer>();
//		System.out.println("Query: " + query.getReview());
//		String[] query_text = query.getReview().split("\\W+");
//		for (int y = 1; y < query_text.length; y++) {
//			int n = 0;
//			for (Review r : R) {
//				String review_text[] = r.getReview().toLowerCase().split("\\W+");
//				if (containsQuery(review_text, query_text[y])) {
//					n++;
//				}
//			}
//			N.put(query_text[y], n);
//		}
//		for (String word : N.keySet()) {
//			System.out.println("Number of reviews containing " + word + ": " + N.get(word));
//		}
//		System.out.println("Number of unique words in R: " + calcV(R));
//		System.out.println();
//		
//		//Calculates cosine similarity for each review to Q
//		query.updateTFs();
//		updateTFIDFs(query,idfs);
//		System.out.println("TF values for query:");
//		
//		printHashMap(query.getTF());
//		System.out.println();
//		System.out.println("TF-IDF values for query:");
//		printHashMap(query.getTFIDF());
//		System.out.println();
//		cosineSimilarity(query, R);
//				
//		//Cosine similarity for reviews compared to r*
//		/*
//		R.remove(r_star);
//		cosineSimilarity(r_star, R);
//		*/
//		
//		bc.close();
//	}
//	
//	/**
//	 * Returns a random integer between min and max (inclusive)
//	 */
//	public static int randInt(int min, int max) {
//		return min + (int)(Math.random() * ((max - min) + 1));
//	}
//	
//	/**
//	 * Updates the IDF values for each term in all reviews in masterlist
//	 */
//	public static void updateIDFs(ArrayList<Review> masterlist, HashMap<String, Double> idfs) {
//		for (Review m_review : masterlist) {
//			String[] review_words = m_review.getReview().toLowerCase().split("\\W+");
//			for (int j = 1; j < review_words.length; j++) {
//				if (!idfs.containsKey(review_words[j])) {
//					idfs.put(review_words[j], 1.0);
//				}
//				else {
//					idfs.put(review_words[j], 1.0 + idfs.get(review_words[j]));
//				}
//			}
//		}
//		
//		//Updates it with log10-weighted values
//		for (String word : idfs.keySet()) {
//			idfs.put(word, Math.log10(masterlist.size()/idfs.get(word)));
//		}
//	}
//	
//	/**
//	 * Sets the TFIDF values for a review given an idf HashMap
//	 */
//	public static void updateTFIDFs(Review r, HashMap<String, Double> idf) {
//		for (String word : r.getTF().keySet()) {
//			r.getTFIDF().put(word, r.getTF().get(word) * idf.get(word));
//		}
//	}
//	
//	/**
//	 * Returns a query in the form of a review (picks 2 random words from given review r)
//	 */
//	public static Review makeQuery(Review r) {
//		String[] review_words = r.getReview().toLowerCase().split("\\W+");
//		int word_index1 = randInt(0, review_words.length - 1), word_index2 = randInt(0, review_words.length - 1);
//		while (word_index1 == word_index2) {
//			word_index2 = randInt(0, review_words.length - 1);
//		}
//		
//		//Forms review with an arbitrary ID number and a "review" of 2 words in the review
//		Review query = new Review("1992", "\"" + review_words[word_index1] + " " + review_words[word_index2] + "\"");
//		return query;
//	}
//	
//	/**
//	 * Compares a random review to a set of reviews and calculates its cosine similarity value
//	 */
//	public static void cosineSimilarity(Review random, ArrayList<Review> masterlist) {
//		double cosine_value;
//		for (Review review : masterlist) {
//			double random_total = 0.0, review_total = 0.0, rev_ran_total = 0.0;
//			
//			HashSet<String> union = new HashSet<String>();
//			union.addAll(review.getTFIDF().keySet());
//			union.addAll(random.getTFIDF().keySet());
//			
//			for (String word : union) {
//				double tfidf_review = 0.0, tfidf_random = 0.0;
//				
//				if (random.getTFIDF().containsKey(word)) {
//					tfidf_random = random.getTFIDF().get(word);
//				}
//				if (review.getTFIDF().containsKey(word)) {
//					tfidf_review = review.getTFIDF().get(word);
//				}
//				rev_ran_total += (tfidf_review * tfidf_random);
//				random_total += Math.pow(tfidf_random, 2);
//				review_total += Math.pow(tfidf_review, 2);
//			}
//			random_total = Math.sqrt(random_total);
//			review_total = Math.sqrt(review_total);
//			cosine_value = rev_ran_total / (random_total * review_total);
//			
//			//Print out relevant info
//			System.out.println(random.toString());
//			System.out.println(review.toString());
//			System.out.println("Cosine value: " + cosine_value + "\n");
//		}
//	}
//	
//	/**
//	 * Prints HashMap key-value pairs in the form: (key, value)
//	 */
//	public static void printHashMap(HashMap<String, Double> map) {
//		for (String word : map.keySet()) {
//			System.out.println("(" + word + ", " + map.get(word) + ")");
//		}
//	}
//	
//	/**
//	 * Checks if the array form of a review's transcript contains query
//	 * Returns true if it does, false otherwise
//	 */
//	public static boolean containsQuery(String[] text, String query) {
//		for (int i = 1; i < text.length; i++) {
//			if (text[i].equals(query)) {
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	/**
//	 * @param A list of reviews
//	 * @return The number of unique words found in that list of reviews
//	 */
//	public static int calcV(ArrayList<Review> reviews_list) {
//		ArrayList<String> unique_words = new ArrayList<String>();
//		for (Review r : reviews_list) {
//			String[] transcript_array = r.getReview().toLowerCase().split("\\W+");
//			for (int i = 1; i < transcript_array.length; i++) {
//				if (!unique_words.contains(transcript_array[i])) {
//					unique_words.add(transcript_array[i]);
//				}
//			}
//		}
//		
//		return unique_words.size();
//	}
//}