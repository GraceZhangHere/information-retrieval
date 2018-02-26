package edu.illinois.cs.eval;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

import edu.illinois.cs.index.ResultDoc;
import edu.illinois.cs.index.Runner;
import edu.illinois.cs.index.Searcher;
import org.apache.lucene.util.MathUtil;

class QueryScore{
	String query;
	double tf;
	double bdp;
	double bm;
	double dp;
	double tfbdp;
	double tfbm;
	double bmdp;
}

public class Evaluate {
	/**
	 * Format for judgements.txt is:
	 *
	 * line 0: <query 1 text> line 1: <space-delimited list of relevant URLs>
	 * line 2: <query 2 text> line 3: <space-delimited list of relevant URLs>
	 * ...
	 * Please keep all these constants!
	 */

	private static final String _judgeFile = "npl-judgements.txt";
	final static String _indexPath = "lucene-npl-index";
	static Searcher _searcher = null;

	
//	public static void main(String[] args) throws IOException {
//		String method = "--ok";//specify the ranker you want to test
//
//		_searcher = new Searcher(_indexPath);
//		Runner.setSimilarity(_searcher, method);
//		BufferedReader br = new BufferedReader(new FileReader(_judgeFile));
//		String line = null, judgement = null;
//		int k = 10;
//		double meanAvgPrec = 0.0, p_k = 0.0, mRR = 0.0, nDCG = 0.0;
//		double numQueries = 0.0;
//		while ((line = br.readLine()) != null) {
//			judgement = br.readLine();
//
//			//compute corresponding AP
//			meanAvgPrec += AvgPrec(line, judgement, Integer.MAX_VALUE);
//			//compute corresponding P@K
//			p_k += Prec(line, judgement, k);
//			//compute corresponding MRR
//			mRR += RR(line, judgement, Integer.MAX_VALUE);
//			//compute corresponding NDCG
//			nDCG += NDCG(line, judgement, k);
//
//			++numQueries;
//		}
//		br.close();
//
//		System.out.println("\nMAP: " + meanAvgPrec / numQueries);//this is the final MAP performance of your selected ranker
//		System.out.println("\nP@" + k + ": " + p_k / numQueries);//this is the final P@K performance of your selected ranker
//		System.out.println("\nMRR: " + mRR / numQueries);//this is the final MRR performance of your selected ranker
//		System.out.println("\nNDCG: " + nDCG / numQueries); //this is the final NDCG performance of your selected ranker
//	}
	/*
	 * Compare TF-IDF, BDP, BM25. Get top 10 different scores.
	 */
	public static void main(String[] args) throws IOException {
		//  String method = "--bdp";//specify the ranker you want to test
		//TF-IDF - BDP
		//TF-IDF - BM25
		//BM25 - DP

		String[] methods = new String[]{"--bdp","--tfidf","--ok","--dp"};
		double[][] queryMatrix = new double[93][4];//bdp,tfidf, bm25,dp
		String[] queries = new String[93];
		int order = 0;
		for(String method : methods){
			System.out.println(method);
			_searcher = new Searcher(_indexPath);
			Runner.setSimilarity(_searcher, method);
			BufferedReader br = new BufferedReader(new FileReader(_judgeFile));
			String line = null, judgement = null;
			//   double meanAvgPrec = 0.0;
			int numQueries = 0; // 0-93
			//   int queriesOrder = 0; //0-3
			while ((line = br.readLine()) != null) {
				judgement = br.readLine();

				//compute corresponding AP
				double meanAvgPrec = AvgPrec(line, judgement, Integer.MAX_VALUE);
				if(order == 0){
					queries[numQueries] = line;
				}
				queryMatrix[numQueries][order] = meanAvgPrec;
				//compute corresponding P@K
				//   p_k += Prec(line, judgement, k);
				//   //compute corresponding MRR
				//   mRR += RR(line, judgement);
				//   //compute corresponding NDCG
				//   nDCG += NDCG(line, judgement, k);

				++numQueries;
			}
			System.out.println("array size " + numQueries);
			br.close();
			order++;
		}
		QueryScore[] toSort = new QueryScore[93];
		for(int i = 0; i < 93; i++){
			//   System.out.println(i);
			toSort[i] = new QueryScore();
			toSort[i].query = queries[i];
			toSort[i].tf = (double) queryMatrix[i][1];
			toSort[i].bdp = (double) queryMatrix[i][0];
			toSort[i].bm = (double) queryMatrix[i][2];
			toSort[i].dp = (double) queryMatrix[i][3];
			toSort[i].tfbdp = (double) queryMatrix[i][1] - queryMatrix[i][0];
			toSort[i].tfbm = (double) queryMatrix[i][1] - queryMatrix[i][2];
			toSort[i].bmdp = (double) queryMatrix[i][2] - queryMatrix[i][3];
		}

		Arrays.sort(toSort,new Comparator<QueryScore>(){
			@Override
			public int compare(QueryScore s1, QueryScore s2){
				return s1.tfbdp > s2.tfbdp ? -1
						: s1.tfbdp < s2.tfbdp ? 1
						: 0;
			}
		});
		System.out.println("tfbdp");
		for(int i = 0; i< 10; i++){
			System.out.println(toSort[i].query);
			System.out.println("Score diff: " + toSort[i].tfbdp + " tf " + toSort[i].tf + " bdp " + toSort[i].bdp);
		}

		Arrays.sort(toSort,new Comparator<QueryScore>(){
			@Override
			public int compare(QueryScore s1, QueryScore s2){
				return s1.tfbm > s2.tfbm ? -1
						: s1.tfbm < s2.tfbm ? 1
						: 0;
			}
		});
		System.out.println("");
		System.out.println("");
		System.out.println("tfbm");
		for(int i = 0; i< 10; i++){
			System.out.println(toSort[i].query);
			System.out.println("Score diff: " + toSort[i].tfbm + " tf " + toSort[i].tf + " bm " + toSort[i].bm);

		}

		Arrays.sort(toSort,new Comparator<QueryScore>(){
			@Override
			public int compare(QueryScore s1, QueryScore s2){
				return s1.bmdp > s2.bmdp ? -1
						: s1.bmdp < s2.bmdp ? 1
						: 0;
			}
		});
		System.out.println("");
		System.out.println("");
		System.out.println("bmdp");
		for(int i = 0; i< 10; i++){
			System.out.println(toSort[i].query);
			System.out.println("Score diff: " + toSort[i].bmdp + " bm " + toSort[i].bm + " dp " + toSort[i].dp);

		}


		//  System.out.println("\nMAP: " + meanAvgPrec / numQueries);//this is the final MAP performance of your selected ranker
		//  System.out.println("\nP@" + k + ": " + p_k / numQueries);//this is the final P@K performance of your selected ranker
		//  System.out.println("\nMRR: " + mRR / numQueries);//this is the final MRR performance of your selected ranker
		//  System.out.println("\nNDCG: " + nDCG / numQueries); //this is the final NDCG performance of your selected ranker
	}
	private static double AvgPrec(String query, String docString, Integer NumResult) {
		ArrayList<ResultDoc> results = _searcher.search(query, NumResult).getDocs();
		if (results.size() == 0)
			return 0; // no result returned

		HashSet<String> relDocs = new HashSet<String>(Arrays.asList(docString.split("\\s+")));
		int i = 1;
		double avgp = 0.0;
		double numRel = 0;
		double precSum = 0;
		System.out.println("\nQuery: " + query);
		for (ResultDoc rdoc : results) {
			if (relDocs.contains(rdoc.title())) {
				//how to accumulate average precision (avgp) when we encounter a relevant document
				numRel ++;
				precSum = numRel / i + precSum;
				System.out.print("  ");
			} else {
				//how to accumulate average precision (avgp) when we encounter an irrelevant document
				System.out.print("X ");
			}
			System.out.println(i + ". " + rdoc.title());
			++i;
		}

		//compute average precision here
		if (numRel == 0) {
			return 0;
		}
		avgp = precSum / relDocs.size();
		System.out.println("Average Precision: " + avgp);
		return avgp;
	}

	//precision at K
	private static double Prec(String query, String docString, int k) {
		ArrayList<ResultDoc> results = _searcher.search(query).getDocs();
		if (results.size() == 0)
			return 0; // no result returned

		HashSet<String> relDocs = new HashSet<String>(Arrays.asList(docString.split("\\s+")));
		double p_k = 0;
		double numRel = 0;
		int i = 1;
		
		System.out.println("\nQuery: " + query);
		for (ResultDoc rdoc : results) {
			if (relDocs.contains(rdoc.title())) {
				numRel++;
			} else {
				System.out.print("X ");
			}
			System.out.println(i + ". " + rdoc.title());
			if(i == k) {
				break;
			}
			++i;

		}

		if(numRel == 0) {
			return 0;
		}
		p_k = numRel / k;
		System.out.println("Precision at k: " + p_k);

		return p_k;
	}

	//Reciprocal Rank
	private static double RR(String query, String docString, Integer NumResult) {
		ArrayList<ResultDoc> results = _searcher.search(query, NumResult).getDocs();
		if (results.size() == 0)
			return 0; // no result returned

		HashSet<String> relDocs = new HashSet<String>(Arrays.asList(docString.split("\\s+")));
		double i = 1.0;
		double rr;
		double numRel = 0;
		System.out.println("\nQuery: " + query);
		for (ResultDoc rdoc : results) {
			if (relDocs.contains(rdoc.title())) {
				numRel = 1;
				break;
			} else {
				System.out.print("X ");
			}
			System.out.println(i + ". " + rdoc.title());
			++i;
		}

		if(numRel == 0) {
			return 0;
		}
		rr = 1 / i;
		System.out.println("Reciprocal Rank: " + rr);

		return rr;
	}

	//Normalized Discounted Cumulative Gain
	private static double NDCG(String query, String docString, int k) {
		ArrayList<ResultDoc> results = _searcher.search(query).getDocs();
		if (results.size() == 0)
			return 0; // no result returned

		HashSet<String> relDocs = new HashSet<String>(Arrays.asList(docString.split("\\s+")));
		double ndcg;
		double i = 1;
		double dcg = 0;
		double numRel = 0;
		System.out.println("\nQuery: " + query);
		for (ResultDoc rdoc : results) {
			if (relDocs.contains(rdoc.title())) {
				numRel ++;
				dcg = 1.0 / MathUtil.log(2,(i + 1)) + dcg;
				System.out.print("  ");
			} else {
				System.out.print("X ");
			}
			System.out.println(i + ". " + rdoc.title());
			if(i == k) {
				break;
			}
			++i;
		}
		//your code for computing Normalized Discounted Cumulative Gain here
		double idcg = 0;
		for(double m = 1; m <= Math.min(k, relDocs.size()); m++){
			idcg = 1.0 / MathUtil.log(2,(m + 1)) + idcg;
		}
		if(numRel == 0) {
			return 0;
		}
		ndcg = dcg / idcg;
		System.out.println("Normalized Discounted Cumulative Gain: " + ndcg);
		return ndcg;
	}
}