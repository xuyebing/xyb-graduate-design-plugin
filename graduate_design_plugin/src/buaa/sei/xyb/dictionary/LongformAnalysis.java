package buaa.sei.xyb.dictionary;

import java.util.AbstractCollection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import buaa.sei.xyb.common.Constant;

public class LongformAnalysis {
	
	// AT Abbreviation type
	public static final int AT_OTHER	= 0;
	public static final int AT_ACRONYM	= 1;
	public static final int AT_PREFIX	= 2;
	public static final int AT_DROPPED	= 3;
	
	// LFL location where the longform found
	public static final String LFL_METHOD_ID = "MethodID";
	public static final String LFL_METHOD 	 = "Method";
	public static final String LFL_TYPE 	 = "Type";
	public static final String LFL_KNOWN 	 = "Known";
	public static final String LFL_COMMENT 	 = "Comment";
	public static final String LFL_STMT 	 = "Statement";
	public static final String LFL_MISSED 	 = "Unknown";
	
	// LF Default long form (where to look next)
	public static final String LF_MISSED = "MISSED";
	public static final String LF_METHOD = "Method:";
	public static final String LF_FIELD  = "Field:";
	public static final String LF_TYPE   = "Type:";
	public static final String LF_DECL   = "DECL";
	
	
	private static final ImprovedDictionary id = 
		new ImprovedDictionary(Constant.getDictPath() + "words.dict");
	
	public static int checkAbbrAgainstLongform(String abbr, String longform)
	{
		/*if(isAcronym(abbr, longform))
			return 1;*/
		if(isPrefix(abbr, longform))
			return 2;
		if(isDroppedLetters(abbr, longform))
			return 3;
		
		return 0;
	}


	public static boolean isAcronym(String abbr, String longform) {
		//assuming abbr.length()>1
		
		// Try type name first
		if (isTypeAcronym(abbr, longform))
			return true;
		
		// otherwise, grab first letters
		else if (isMultiWordAcronym(abbr, longform)) 
			return true;
		
		return false;
	}


	public static boolean isMultiWordAcronym(String abbr, String longform) {
		return multiword2acronym(longform).matches(".*" + abbr.toLowerCase() + ".*");
	}

	// Assume camel-cased id as input
	/*public static boolean isIDAcronym(String abbr, String longform) {
		longform = longform.replaceAll("([A-Z])", " $1");
		String lf = multiword2acronym(longform);
		if(lf.matches(".*" + abbr.toLowerCase() + ".*")) {
			return true;
			//TODO eventually return the chopped longform
		}
		return false;
	}*/
	
	// Returns null if false, lf otherwise
	// TODO We still don't check to see if long forms are in dictionary!!!
	public static String getLongForm4IDAcronym(String sf, String lf) {
		// Make sure the first letter is capitalized
		if (lf.length() > 1) {
			String firstLetter = lf.substring(0, 1);
			lf = lf.substring(1);
			lf = firstLetter.toUpperCase().concat(lf);
			sf = sf.toUpperCase();
			char[] sfSplit = sf.toCharArray();
			String expanded = new String();

			//add each letter to string with regex in between
			for (char c:sfSplit) 
			{
				expanded = expanded.concat(c + "[a-z]*");
			}
			expanded = ".*(" + expanded + ").*";

			// TODO: only want to do once
			Pattern p = Pattern.compile(expanded);
			Matcher m = p.matcher(lf);

			//System.out.println(sf+ " " + lf + " " + expanded + " " + m.matches());

			// long form
			if (m.matches())
				return m.group(1);
		}
		
		return null;
	}

	// Assume camel-cased id as input
	public static boolean isTypeAcronym(String abbr, String longform) {
		return typename2acronym(longform).matches(".*" + abbr.toLowerCase() + ".*");
	}

	public static String multiword2acronym(String longform) {
		String temp = "";
		String[] words = longform.split("\\s");
		
		for(String w : words) {
			if(w.length()>0)
				temp = temp.concat(w.substring(0, 1));
		}
		return temp.toLowerCase();
	}

	
	public static String typename2acronym(String longform) {
		return longform.replaceAll("[^A-Z]", "").toLowerCase().replaceAll("[^a-z]", "");
	}
	
	public static boolean isPrefix(String sf, String lf) {
		String expanded = new String();
		if(sf.endsWith("s"))
		{
			//check both s removed and whole thing
			expanded = sf.replaceAll("s$", "[a-zA-Z]*s");
			AbstractCollection<String> cands = id.getCandidates(expanded);
			if(cands.contains(lf))
				return true;
			
			expanded = sf.concat("[a-zA-Z]*");
			cands = id.getCandidates(expanded);
			if(cands.contains(lf))
				return true;
		}
		else
		{
			expanded = sf.concat("[a-zA-Z]*");
			AbstractCollection<String> cands = id.getCandidates(expanded);
			if(cands.contains(lf))
				return true;
		}
		return false;
	}
	
	public static boolean isDroppedLetters(String sf, String lf) {
		char[] sfSplit = sf.toCharArray();
		String expanded = new String();
		//add each letter to string with regex in between
		for (char c:sfSplit) 
		{
			expanded = expanded.concat(c + "[a-zA-Z]*");
		}
		
		AbstractCollection<String> cands = id.getCandidates(expanded);
		if(cands.contains(lf))
			return true;
		
		return false;
	}
	
	//for dropped letters
	public static int calculateEditDistance(String abbrev, String word) {
		int wordPenalty = 0;
		for (int j=0; j<abbrev.length(); j++) {
			int start = word.indexOf(abbrev.charAt(j));
			String part = new String();
			if (j == (abbrev.length()-1)) {
				part = word.substring(start);
			} else {
				int stop = word.indexOf(abbrev.charAt(j+1), start);
				part = word.substring(start, stop);
			}
			for (int k=1; k<part.length(); k++) {
				wordPenalty += getPenalty(part.charAt(k));
			}
		}
		return wordPenalty;
	}
	
	private static int getPenalty(char letter) {
		if (letter == 'a' || 
			letter == 'e' ||
			letter == 'i' ||
			letter == 'o' ||
			letter == 'u') {
			return 1;
		} else {
			return 2;
		}
	}
	
	// NOTE: isNonDictionaryWord is NOT the opposite of isDictionary word,
	// since it takes into account length
	public static boolean isNonDictionaryWord(String s) {
		return ( (!id.contains(s)) && (s.length()>1) );
	}
	
	public static HashMap<String, Boolean> getNonDictionaryWordsSet(Vector<String> v)
	{
		HashMap<String, Boolean> hashCopy = new HashMap<String, Boolean>();
		for(String s:v)
		{
			if(isNonDictionaryWord(s)) hashCopy.put(s, false);
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
	
	public static HashSet<String> getDictionaryWords(Vector<String> v)
	{
		HashSet<String> hashCopy = new HashSet<String>();
		for(String s:v)
		{
			if(isDictionaryWord(s)) hashCopy.add(s);
		}
		return hashCopy;
	}


	public static boolean isDictionaryWord(String s) {
		return id.contains(s);
	}

	public static String getAcronymType(int at) {
		switch (at) {
		case AT_OTHER:
			return "OO";
		case AT_ACRONYM:
			return "AC";
		case AT_PREFIX:
			return "PR";
		case AT_DROPPED:
			return "DL";
		default:
			return "OO";
		}
	}
		
}

