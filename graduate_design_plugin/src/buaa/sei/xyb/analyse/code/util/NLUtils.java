package buaa.sei.xyb.analyse.code.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import buaa.sei.xyb.dictionary.CommonAbbreviation;

public class NLUtils {
	
	public static String commaSeparate(Vector array) {
		String str = "";
		for (int i = 0; i < array.size(); i++) {
			str += array.elementAt(i);
			if (i < array.size() - 1) {
				str += ", ";
			}
		}
		return str;
	}
	
	public static String commaSeparate(String[] array) {
		String str = "";
		for (int i = 0; i < array.length; i++) {
			str += array[i];
			if (i < array.length - 1) {
				str += ", ";
			}
		}
		return str;
	}
	
	/**
	 * remove ' and non [A-Za-z]
	 * @param str
	 * @return
	 */
	public static String splitPunctuation(String str) {
		str = str.replaceAll("'", "");
		return str.replaceAll("[^A-Za-z]", " ");
	}
	
	/**
	 * clean space-delimited string of terms
	 * @param str
	 */
	public static Vector<String> cleanTerms(String str) {
		Vector<String> terms = new Vector<String>();
		
		
		// Convert to lowercase
		str = str.toLowerCase();
				
		// Remove punctuation
		str = splitPunctuation(str);
				
		for (String w : str.split("\\s")) {
			// Add non-empty terms
			if (!w.matches("^\\s*$"))
				terms.add(w);	
		}
		return terms;
	}
	
	/**
	 * Converts to lower case and removes non alphabetic chars
	 * (including hex)
	 * @param str
	 * @return
	 */
	public static String cleanString(String str) {
		
		// Convert to lowercase
		str = str.toLowerCase();
		
		
		// Remove Lowercase Hex
		str = str.replaceAll("0x[0-9a-f]*", " ");
				
		// Remove punctuation
		str = str.replaceAll("'", "");
		str = str.replaceAll("[^A-Za-z]", " ");
		
		// double space just to be safe
		str = str.replaceAll(" ", "  ");
		
		return str;
	}
	
	public static String cleanStringLeaveDot(String str) {
		
		// Convert to lowercase
		str = str.toLowerCase();
		
		
		// Remove Lowercase Hex
		str = str.replaceAll("0x[0-9a-f]*", " ");
				
		// Remove punctuation
		str = str.replaceAll("'", "");
		
		// make sure method call dots are spaced properly
		str = str.replaceAll("\\.", " . ");
		
		// equal is for statements left in comments for MW
		// same for () and {}
		str = str.replaceAll("[?!,;]", " . ");
		str = str.replaceAll("[=\\(\\)\\{}]", " . ");
		
		// remove anything else
		str = str.replaceAll("[^A-Za-z\\.]", " ");
		
		// double space just to be safe
		str = str.replaceAll(" ", "  ");
		
		return str;
	}
	
	
	
	/**
	 * Clean id-delimited string of terms
	 */
	public static Vector<String> cleanIdentifiers(String id) {
		return cleanTerms(splitCamel(id));
	}
	
	public static String spaceIdentifiers(String id) {
		String str = splitCamel(id);
		
		// Convert to lowercase
		str = str.toLowerCase();
				
		// Remove punctuation
		str = " " + splitPunctuation(str) + " ";
		
		return str;
	}
	
	/**
	 * Converts camel case id into a space delimited string
	 * @param id
	 * @return string
	 */
	public static String splitCamel(String id) {
		//Only strip off last upper case if followed by lower case
		//This will leave all cap constants together
		//if (Preferences.CAMEL_ABBREV) {
			// First get beginning abbrevs
			id = id.replaceAll("([A-Z]+)?([A-Z][a-z])", " $1 $2");
			// Then get trailing
			id = id.replaceAll("([a-z])([A-Z]+)", "$1 $2 ");
		/*} else {
			id = id.replaceAll("([A-Z])", " $1");
		}*/

		return id;
	}
	
	public static HashSet<String> readStopWords(String fname) {
		BufferedReader input = null;
		HashSet<String> stopWords = new HashSet<String>();
		//System.out.println("Reading stop word list from "+fname);
	    try {
	      input = new BufferedReader( new FileReader(fname) );
	      String line = null; //not declared within while loop

	      while (( line = input.readLine()) != null){
	        // Do something with the line
	    	  if (!line.matches("^#.*") && !line.matches("\\s*")) {
	    		  stopWords.add(line);
	    		  System.out.println("Adding -" + line + "-");
	    	  }
	      }
	    }
	    catch (FileNotFoundException ex) {
	      ex.printStackTrace();
	    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }
	    finally {
	      try {
	        if (input!= null) {
	          //flush and close both "input" and its underlying FileReader
	          input.close();
	        }
	      }
	      catch (IOException ex) {
	        ex.printStackTrace();
	      }
	    }
	    
	    return stopWords;
	}
	
	public static HashMap<String,CommonAbbreviation> readAbbrevs(String fname) {
		HashMap<String, CommonAbbreviation> commonAbbrs = 
			new HashMap<String, CommonAbbreviation>();
		
		//load common abbreviation list
		try {
			BufferedReader in = new BufferedReader(new FileReader(fname));
			String l = in.readLine();
			while (l != null) {
				String[] temp = l.split("\\s+");
				String sf = temp[0];
				String t = temp[1];
				String lf = "";
				for(int i=2; i<temp.length; i++) {
					lf = lf.concat(temp[i]);
					
					// as long as there's one more, add a space
					if (i < temp.length - 1) 
						lf = lf + " ";
				}
				CommonAbbreviation ca = new CommonAbbreviation(sf, t, lf);
				commonAbbrs.put(sf, ca);
				l = in.readLine();
			}
		}
		catch (IOException e) {
			System.out.println(e);
		}
		
		return commonAbbrs;
	}
	
	public static void main(String[] args) {
		System.out.println(cleanIdentifiers("complete completion completed"));
	}
	
}
