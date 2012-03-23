package buaa.sei.xyb.analyse.code.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import buaa.sei.xyb.common.Constant;
import buaa.sei.xyb.dictionary.CommonAbbreviation;
import buaa.sei.xyb.dictionary.ImprovedDictionary;

public class LongFormUtils {
	private static final ImprovedDictionary dicWords = 
		new ImprovedDictionary(Constant.getDictPath() + "words.dict");
	
	private static final ImprovedDictionary proper = 
		new ImprovedDictionary(Constant.getDictPath() + "proper4UP.dict");
	
	private static final ImprovedDictionary contractions = 
		new ImprovedDictionary(Constant.getDictPath() + "contractions.dict");
	
	private static ImprovedDictionary stopWords = 
		new ImprovedDictionary(Constant.getDictPath() + "EnglishWord.stop");
	
	private static final ImprovedDictionary javaWords = 
		new ImprovedDictionary(Constant.getDictPath() + "java.stop");

	private static HashMap<String, CommonAbbreviation> commonAbbrs = NLUtils.readAbbrevs(
			Constant.getDictPath() + "common.abbrev");
	
	public static void main(String[] args) {
		System.out.println(LongFormUtils.isStem("note","notes"));
		System.out.println(LongFormUtils.isStem("add","adding"));
		
		System.out.println(LongFormUtils.isStem("definition","define"));
		System.out.println(LongFormUtils.isStem("default","defaults"));
		System.out.println(LongFormUtils.isStem("gibson guitar","gibson guitars"));
		System.out.println(LongFormUtils.isStem("round robin databases","round robin database"));
		System.out.println(LongFormUtils.isStem("round databases","robin database"));
		System.out.println(LongFormUtils.isJavaWord("String"));
	}
	
	public static void setStopWordDictionary(String stopWordFile) {
		stopWords = new ImprovedDictionary(stopWordFile);
	}
	
	public static boolean isProperNoun(String s) {
		return ( proper.contains(s) );
	}
	
	public static boolean isContraction(String s) {
		return ( contractions.contains(s) );
	}	
	
	public static boolean isStopWord(String s) {
		return ( stopWords.contains(s) );
	}	

	public static boolean isJavaWord(String s) {
		return ( javaWords.contains(s) );
	}

	/** make sure "." have spaces around! */
	public static String removeStopWords(String in) {
		String temp = "";
		for (String s : in.split("\\s+")) {
			if ( (!isStopWord(s)) && (!isContraction(s)) )
				temp = temp + "  " + s;
		}
		return temp;
	}
	
	public static boolean isCommonAbbreviation(String s) {
		return commonAbbrs.containsKey(s);
	}
	
	public static CommonAbbreviation getCommonAbbreviation(String s) {
		CommonAbbreviation ca = null;
		if (commonAbbrs.containsKey(s))
			return commonAbbrs.get(s);
		return ca;
	}
	
	/**
	 * returns true if all the words in the string are either
	 * dictionary, proper nouns, or contractions.
	 */
	public static boolean isDictionaryString(String lf) {
		for (String s : lf.split("\\s+"))
			if (!(isDictionaryWord(s)||isProperNoun(s)||isContraction(s)))
				return false;
		return true;
	}
	
	/**
	 * Returns true if s is a word, proper noun, or contraction
	 */
	public static boolean isDictionaryExpansion(String s) {
		return (isDictionaryWord(s)||isProperNoun(s)||isContraction(s));
	}
	
	/*public static boolean isNonDictionaryCandidate(String lf, HashMap<String, Boolean> nonDictWords) {
		for (String s : lf.split("\\s+"))
			if (nonDictWords.keySet().contains(s))
				return true;
		return false;
	}*/
	
	/**
	 * Contained in word dictionary
	 */
	public static boolean isDictionaryWord(String s) {
		return dicWords.contains(s);
	}
	
	/*
	 * NOTE: isNonDictionaryWord is NOT the opposite of isDictionary word, 
	 * since it takes into account length
	 */
	public static boolean isNonDictionaryWord(String s) {
		//return ( (!id.contains(s)) && (s.length()>1) );
		return !dicWords.contains(s);
	}
	
	public static HashMap<String, Boolean> getNonDictionaryWordsSet(String v)
	{
		HashMap<String, Boolean> hashCopy = new HashMap<String, Boolean>();
		for(String s:v.split("\\s+"))
		{
			if(isNonDictionaryWord(s)) hashCopy.put(s, false);
		}
		return hashCopy;
	}
	
	/**
	 * Get the non dictionary words from the string v.
	 * @param v the string being handled with. We split the words by spaces.
	 * @param minLength the minLength of the word
	 * @param maxLength the maxLength of the word
	 * @return a HashMap contains the non dictionary words. key--the word, value--flase.
	 */
	public static HashMap<String, Boolean> getNonDictionaryWordsSet(String v ,
			int minLength, int maxLength)
	{
		HashMap<String, Boolean> hashCopy = new HashMap<String, Boolean>();
		for(String s:v.split("\\s+"))
		{
			if(isNonDictionaryWord(s) && 
					s.length() <= maxLength && 
					s.length() >= minLength)
				hashCopy.put(s, false);
		}
		return hashCopy;
	}
	
	public static HashSet<String> getNonDictionaryWords(Vector<String> v)
	{
		HashSet<String> hashCopy = new HashSet<String>();
		for(String s:v)
		{
			if(isNonDictionaryWord(s)) hashCopy.add(s);
		}
		return hashCopy;
	}
	
	/**
	 * Assume space delimited set of words
	 */
	public static String getDictionaryWords(String src) {
		String str = "";
		for(String s: src.split("\\s+"))
		{
			if(isDictionaryWord(s))
				str = str + "  " + s;
		}	
		return " " + str + " ";
	}

	/**
	 * Annotate dictionary words with " --"
	 */
	public static String annotateDictionaryWords(String statementsClean) {
		String str = "";
		for(String s: statementsClean.split("\\s+"))
		{
			// put two spaces in between and annotate dictionary words
			if (isStopWord(s) || isContraction(s))
				str = str;
			else if(isDictionaryWord(s))
				str = str + " --" + s + "--";
			else
				str = str + "  " + s;
		}	
		return "  " + str + "  ";
	}
	

	/**
	 * Annotate dictionary words with " --" and remove non dictionary words
	 * longer than maxLength to make sure the statement dictionary search halts.
	 */
	public static String annotateDictionaryWords(String statementsClean, int maxLength) {
		String str = "";
		for(String s: statementsClean.split("\\s+"))
		{
			// put two spaces in between and annotate dictionary words
			if(isDictionaryWord(s))
				str = str + " --" + s + "--";
			else if (s.length() < maxLength)
				str = str + "  " + s;
		}	
		return "  " + str + "  ";
	}

	/**
	 * Don't want to match multi words across nd words in statements, but
	 * greedy expression can't handle multiple matches. removes stop
	 * words.
	 */
	public static String dotNonDictionaryWords(String statementsClean) {
		String str = "";
		for(String s: statementsClean.split("\\s+"))
		{
			if (isStopWord(s) || isContraction(s))
				str = str;
			else if(isDictionaryWord(s))
				str = str + "  " + s + "  ";
			else
				str = str + ". ";
		}	
		return str;
	}
	
	public static String getDictionary() {
		return dicWords.toString();
	}
	
	public static boolean isStem(String s1, String s2) {
		return getStem(s1).matches(getStem(s2));
	}
	
	private static final HashMap<String,String> stemmer = new HashMap<String,String>();
	
	private static String getStem(String s) {
		if (!stemmer.containsKey(s))
			stemmer.put(s, Stemmer.getStem(s));
		return stemmer.get(s);
	}
	

}

