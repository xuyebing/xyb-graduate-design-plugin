package buaa.sei.xyb.analyse.code;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

import buaa.sei.xyb.analyse.code.util.CodeParseConstant;
import buaa.sei.xyb.analyse.code.util.LongFormUtils;
import buaa.sei.xyb.analyse.code.util.SourceProcessor;
import buaa.sei.xyb.common.Constant;
import buaa.sei.xyb.dictionary.CommonAbbreviation;
import buaa.sei.xyb.dictionary.LongformAnalysis;

/**
 * 提取缩写词的关键类，实现了论文中提到的主要算法
 * 
 * @author lai
 *
 */
public class AMAPParser {

	private static Log log = LogFactory.getLog(AMAPParser.class);

	// The file to store the result of the abbreviation expansions.
	private String fname;
	private PrintStream emit = null;
	private FileOutputStream output = null;
	private HashMap<String,Boolean> nonDictWords;
	protected static final int maxLength = 10; // no longer an abbrev!
	protected static final int minLength = 1; // leave single letters for later

	private HashMap<String,Integer> matches; // keep track of all matches per at/sf

	private SourceProcessor sp;

	public AMAPParser(SourceProcessor sp) {
		this.sp = sp;
	}

	/**
	 * 目前只对方法体的缩写词进行扩展，不对方法注释里的缩写词进行扩展
	 * 
	 */
	protected String getAbbrExpandBody() {
		StringBuffer body = new StringBuffer();
		// get abbreviation candidates from the method body
		for(String sf : sp.getMethodBodyCleanNoStop().split("\\s+"))
		{
			if(LongFormUtils.isJavaWord(sf))
				continue;
			if(!Constant.doExpansion || LongFormUtils.isDictionaryWord(sf))
				body.append(sf + " ");
			else if (sf.length() <= maxLength && sf.length() >= minLength) {
				body.append(findLongForm(sf) + " ");
			}
		}

		return body.toString();
//		printMissed(sp.getMethodName());
	}

	/**
	 * find the most possible long form of the short form
	 * @param sf the short form
	 * @return the most possible long form
	 */
	private String findLongForm(String sf) {
		String lf = sf;
		if(sf.length() == 1) 
			lf = findPrefixes(sf);
		else
			lf = getOne(sf, sf.toCharArray());
		if((lf == null) || (lf.equals(sf)))
			lf = checkCommonAbbr(sf);
		return lf;
	}

	/**
	 * Get the first possible expansion been found.
	 * The order is acronyms, prefixes, dropped letters, combination multi-word and other contraction.
	 * @param sf: short form
	 * @param sfSplit: the split form of the short form
	 * @return if found, return the first possible expansion, otherwise, return null.
	 */
	private String getOne(String sf, char[] sfSplit) {

		String lf = null;

		// Check for acronyms of types, methodIDs, method name, and comments

		// Check for methodID, method name, leading and internal comment acronyms
		if ((lf = checkAcronyms(sf, sfSplit)) == null)
			if ((lf = findPrefixes(sf)) == null)
				if ((lf = checkDroppedLetters(sf, sfSplit)) == null)
					if ((lf = checkCombinationWord(sf, sfSplit)) == null)
						lf = checkOtherContraction(sf, sp.getMethodName());
		return lf;
	}

	/**
	 * 检查是否常用缩写词。常用缩写词存放在dict\\commom.abbrev文件中
	 * @param sf short form
	 * @return 如果是常用缩写词则返回其扩展词，负责返回缩写词本身
	 */
	private String checkCommonAbbr(String sf) {
		String lf = sf;
		CommonAbbreviation ca = LongFormUtils.getCommonAbbreviation(sf);
		if (ca != null) {
			lf = ca.getLongform();
		}

		return lf;
	}

	/*
	 * print the missed abbreviation
	 */
	private void printMissed(String methodName) {
		// print missed
		for (String sf : nonDictWords.keySet())
			if (!nonDictWords.get(sf))
				printAbbr(sf, CodeParseConstant.LF_MISSED, CodeParseConstant.AT_OTHER,
						0, LongformAnalysis.LFL_MISSED,
						methodName);
	}

	/**
	 * check if the short form is other contraction.
	 * @param sf short form
	 * @param mname method name
	 * @return true if the sf is other contraction, false otherwise.
	 */
	private String checkOtherContraction(String sf, String mname) {
		String match = null;
		if (LongFormUtils.isContraction(sf)) {
//			printAbbr(sf, sf, CodeParseConstant.AT_OTHER, 0, CodeParseConstant.LFL_CONTRACTION, mname);
			match = sf;
		}
		return match;
	}

	/**
	 * Prefix short forms are formed by dropping the latter part of a long form,
	 * retaining only the few beginning letters. 
	 * Examples include 'attr'(attribute), 'obj'(objcet);
	 * 
	 * The prefix pattern is thus the short form followed by the regular expression
	 * "[a-z]+": "sf[a-z]+".
	 * 
	 * @param sf short forms
	 * @return if matches the short form, return the long form. Otherwise return null.
	 */
	private String findPrefixes(String sf) {
		matches = new HashMap<String,Integer>();
		String lf = null;
		String pattern = "";

		pattern = " (" + sf+"[a-zA-Z]*" + ") ";
		lf = createSWPatterns(sf, pattern, CodeParseConstant.AT_PREFIX);

		/*if(sf.endsWith("s"))
		{
			pattern = " (" + sf.replaceAll("s$", "[a-zA-Z]*s") + ") ";
			match|= matchPattern2MethodCommentStmt(sf, pattern, CodeParseConstant.AT_PREFIX_PL, sp);
		}	*/		

		return lf;
	}

	/**
	 * Dropped letter shout forms can have any letters but the first letter removed
	 * from the long term. Examples include 'evt'(event), 'msg'(message).
	 * 
	 * The dropped letter pattern is constructed by inserting the expression "[a-z]" 
	 * after every letter in the short form. 
	 * Let sf=c0,c1,...,cn, where n is the length of the short form.
	 * Then the dropped letter pattern is c0[a-z]*c1[a-z]*...cn[a-z]*.
	 * 
	 * Need to make sure length of abbr's checked, otherwise could try to expand
	 * pppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp
	 * this with wildcard in between!
	 */
	private String checkDroppedLetters(String sf, char[] sfSplit) {
		matches = new HashMap<String,Integer>();
		// Dropped letters are weird with vowels
		/* if it has a vowel OTHER than a leading vowel, 
		 * only expand if > 3 long
		 * Basically, if it has two vowels and it's less than 4 long, 
		 * don't expand (cvv, vcv, vvc, ccv)
		 */
		// TODO: what about eg ig? DL match??
		if (sf.matches("[a-z][^aeiou]+") || sf.length() > 3) {
			String pattern = " (";

			for (char c:sfSplit) 
			{
				pattern = pattern + c + "[a-z]*";
			}
			pattern = pattern +") ";

			//System.out.println(pattern);

			return createSWPatterns(sf, pattern, CodeParseConstant.AT_DROPPED);
		}
		return null;
	}

	/**
	 * Combination multi-word may combine single-word abbreviations, acronyms 
	 * or dictionary words. Examples include 'doctype'(document type), 'println'(print line).
	 * 
	 * The pattern to search for combination word long forms is constructed by 
	 * appending the expression “[a–z]*?[ ]*?” to every letter of the short form. 
	 * Let sf = c0, c1, ..., cn, where n is the length of the short form. 
	 * Then the combination word pattern is c0[a–z]*?[ ]*?c1[a–z]*?[ ]*?...[a–z]*?[ ]*?cn.
	 * The pattern is constructed such that only letters occurring in the short form can begin a word. 
	 * This keeps the pattern from expanding short forms like ‘ada’ with ‘adding machine’.
	 * We use a less greedy wild card to favor shorter long forms with fewer spaces, 
	 * such as ‘period defined’ for ‘pdef’, rather than ‘period defined first’.
	 * 
	 * @param sf: short form
	 * @param sfSplit: the splits of the shot form
	 * @return true if find, otherwise false.
	 */
	private String checkCombinationWord(String sf, char[] sfSplit) {
		matches = new HashMap<String,Integer>();
		// only check multi words > 3
		if ( sf.length() > 3) {
			String pattern = "";

			//add each letter to string with regex in between
			for (int i = 0; i < sfSplit.length; i++) 
			{
				// don't be greedy, we want shortest matches possible?
				if (i == sfSplit.length - 1)
					pattern = pattern + sfSplit[i] + "[a-z]*?"; 
				else
					pattern = pattern + sfSplit[i] + "[a-z]*?[ ]*?";
			}
			pattern = pattern +")";

			/*if (sf.matches("println"))
				System.out.println(pattern);*/

			return createMWPatterns(sf, pattern, CodeParseConstant.AT_MULTI);
		}
		return null;

	}

	/**
	 * Acronyms consist of the first letters of the words in the long form.
	 * A common naming scheme is to use the type's abbreviation.
	 * Examples include 'sb'(StringBuffer).
	 * 
	 * The regular expression pattern used to search for acronym
	 * long forms is simply constructed by inserting the expression
	 * “[a–z]+[ ]+” after every letter in the short form. Let
	 * sf = c0, c1, ..., cn, where n is the length of the short form.
	 * Then the acronym pattern is c0[a–z]+[ ]+c1[a–z]+[ ]+...[a–z]+[ ]+cn.
	 * As with prefixes, the letter ‘x’ is a special case.
	 * When forming the acronym pattern, any occurrence of ‘x’in the 
	 * short form is replaced with the expression “e?x.”This enables 
	 * our technique to find long forms for acronyms such as ‘xml’ (extensible markup language).
	 * 
	 * @param sf short form
	 * @param sfSplit the splits of the short form
	 * @return true if find, otherwise false.
	 */
	protected String checkAcronyms(String sf, char[] sfSplit) {
		matches = new HashMap<String,Integer>();
		String pattern = "";

		//add each letter to string with regex in between
		for (int i = 0; i < sfSplit.length; i++) 
		{
			if (sfSplit[i] == 'x') // add optional e (just for acronyms?)
				pattern = pattern + "e?" + sfSplit[i] + "[a-z]+";
			else
				pattern = pattern + sfSplit[i] + "[a-z]+";

			if (i == sfSplit.length - 1)
				pattern = pattern + ")";
			else
				pattern = pattern + "\\s+";
		}

		return createMWPatterns(sf, pattern, CodeParseConstant.AT_ACRONYM);
	}

	/**
	 * For prefixes and dropped letters.
	 * 
	 * @param sf: short forms
	 * @param patternStr: regular expression to match long form
	 * @param at: abbreviation type
	 * @return null if cannot expand the short form, otherwise return the long form
	 */
	private String createSWPatterns(String sf, String patternStr, int at) {
		String match = null;

		/* Don't match 2+ vowels with optional leading consonant
		 * with SW -- save for MW  (c?vv+)
		 * For dropped vowels only, don't match vv+c either
		 * (vv+c could be a legitimate prefix, such as auction)
		 */
		if (! (sf.matches("[a-z]?[aeiou][aeiou]+") /*||
				(at == CodeParseConstant.AT_DROPPED && 
						sf.matches("[aeiou][aeiou]+[a-z]"))*/ ) ) {

			Pattern p = Pattern.compile(patternStr);

			// First, search the JavaDoc comments for "@param sf pattern".
			Pattern pattern = Pattern.compile(sf + "\\s*--[^\\.]*" + patternStr);
			match = findSWAbbr(sf, pattern, sp.getJavaDocComments(),
					CodeParseConstant.LFL_JAVADOC, at, sp.getMethodName());

			if (match != null) return match;
			// Match statement for sf and lf if sf < 10
			// (otherwise too expensive for long string literals with gibberish)

			// Secondly, search the TypeNames and corresponding declared variable names for "pattern sf"
			pattern = Pattern.compile(patternStr + "[^\\.=]*==\\s*" + sf + "[\\s\\.]");
			match = findSWAbbr(sf, pattern, sp.getTypesSeparated(),
					CodeParseConstant.LFL_TYPE, at, sp.getMethodName());

			if (match != null) return match;

			// Thirdly, search MethodName for "pattern"
			match = findSWAbbr(sf, p, sp.getMethodNameCleanNoStop(), 
					CodeParseConstant.LFL_METHOD_NAME, at, sp.getMethodName());

			if (match != null) return match;

			// Remove leading and trailing space first
			patternStr = patternStr.replaceAll("^\\s+", "").replaceAll("\\s+$", "");

			// Fourthly, search Statements for "sf pattern" and "pattern sf"
			// NOTE: This pattern assumes sf left of lf
			Pattern stp = Pattern.compile("[\\.\\s]" + sf + " " + "[^\\.]*--" + patternStr + "--");
			match = findSWAbbr(sf, stp, sp.getStatementsMarkedDictionary(), 
					CodeParseConstant.LFL_STMT, at, sp.getMethodName());

			if (match != null) return match;

			boolean last = false;
			if (sf.length() == 2)
				last = true;
			// NOTE: This pattern assumes sf right of lf
			stp = Pattern.compile( "--" + patternStr + "--[^\\.]*" + " " + sf + "[\\.\\s]");
			match = findSWAbbr(sf, stp, sp.getStatementsMarkedDictionary(), 
					CodeParseConstant.LFL_STMT, at, sp.getMethodName(), last);

			if (match != null) return match;

			// Match string literals
			/*match |= findSWAbbr(sf, p, sp.getStringLiteralsDictionary(), 
				CodeParseConstant.LFL_STRING, at, sp.getMethodName());*/

			// !sf.matches("[a-z]?[aeiou][aeiou]+")
			// matches legit prefixes too -- jsut skip for DL

			// length 2 seems to be finicky...
			if (sf.length() != 2) {
				// Search method words for "pattern"
				match = findSWAbbr(sf, p, sp.getMethodDictionary(), 
						CodeParseConstant.LFL_METHOD, at, sp.getMethodName());

				if (match != null) return match;

				if ((sf.length() == 1) || (at == CodeParseConstant.AT_DROPPED))
					last = true;
				match = findSWAbbr(sf, p, sp.getCommentDictionary(), 
						CodeParseConstant.LFL_COMMENT, at, sp.getMethodName(), last);

				if (match != null) return match;

				// Don't check beyond local context for single letters
				if (sf.length() > 1 && at == CodeParseConstant.AT_PREFIX)
					match = findSWAbbr(sf, p, sp.getClassCommentDictionary(), 
							CodeParseConstant.LFL_CCOMMENT, at, sp.getMethodName(), true);
			}
		}
		return match;
	}

	/**
	 * Create muti-word patterns.
	 * 
	 * @param sf
	 * @param pattern
	 * @param at_level
	 * @return
	 */
	private String createMWPatterns(String sf, String pattern, int at_level) {
		String match = null;
		String startp = "[\\s\\.](";
		String endp = "[\\s\\.]"; // used for everybody but type
		String endType = "\\s+[^\\.=]*==\\s*" + sf + endp;

		// 1. search the JavaDoc comments for "@param sf pattern"
		match = findMWAbbr(sf, sf + "\\s*--[^\\.]*" + startp + pattern + endp, sp.getJavaDocComments(), at_level,
				CodeParseConstant.LFL_JAVADOC, sp.getMethodName());

		if (match != null) return match;

		// 2. search Type name and corresponding declared variale names for "pattern sf"
		match = findMWAbbr(sf, startp + pattern + endType, sp.getTypesSeparated(), at_level,
				CodeParseConstant.LFL_TYPE, sp.getMethodName());

		if (match != null) return match;

		// 3. search Method name better than body for "pattern"
		match = findMWAbbr(sf, startp + pattern + endp, sp.getMethodNameCleanNoStop(), at_level,
				CodeParseConstant.LFL_METHOD_NAME, sp.getMethodName());

		if (match != null) return match;

		// Statements require presence of sf -- will only match first
		// Can't do MW stmt because won't halt
		/*match |= findMWAbbr(sf, "[\\s\\.]" + sf + " [^\\.]* (" + pattern + endp, sp.getStatementsClean(), at_level,
				CodeParseConstant.LFL_STMT, sp.getMethodName());

		if (one && match) return match;

		match |= findMWAbbr(sf, startp + pattern + " [^\\.]* " + sf + "[\\s\\.]", sp.getStatementsClean(), at_level,
				CodeParseConstant.LFL_STMT, sp.getMethodName());

		if (one && match) return match;*/

		// 4. search all identifiers in the method for "pattern" including type names
		match = findMWAbbr(sf, startp + pattern + endp, sp.getMethodIDsCleanNoStop(), at_level,
				CodeParseConstant.LFL_METHOD_ID, sp.getMethodName());

		if (match != null) return match;

		// 5. search string literals for "pattern" 
		// (At this point we have searched all the possible phrases in the method body)
		match = findMWAbbr(sf, startp + pattern + endp, sp.getStringLiteralsCleanNoStop(), at_level, 
				CodeParseConstant.LFL_STRING, sp.getMethodName());

		if (match != null) return match;

		// leading comment split on camel case
		/*match |= findMWAbbr(sf, pattern + endp, sp.getLeadingCommentsCleanSplit(), at_level,
				CodeParseConstant.LFL_LCOMMENT, sp.getMethodName());

		if (match) return match;

		// internal comment split on camel case
		match |= findMWAbbr(sf, pattern + endp, sp.getInternalCommentsCleanSplit(), at_level,
				CodeParseConstant.LFL_ICOMMENT, sp.getMethodName());*/
		boolean last = false;
		if (at_level == CodeParseConstant.AT_MULTI)
			last = true;
		match = findMWAbbr(sf, startp + pattern + endp, sp.getCommentsCleanSplit(), at_level,
				CodeParseConstant.LFL_COMMENT, sp.getMethodName(), last);

		if (match != null) return match;

		if (at_level == CodeParseConstant.AT_ACRONYM)
			match = findMWAbbr(sf, startp + pattern + endp, sp.getClassCommentsCleanSplit(), at_level,
					CodeParseConstant.LFL_CCOMMENT, sp.getMethodName(), true);

		return match;
	}

	/**
	 * find muti-word abbreviation. Default is not last.
	 * 
	 * @param sf
	 * @param pattern
	 * @param toSearch
	 * @param at_level
	 * @param lfl
	 * @param mname
	 * @return
	 */
	private String findMWAbbr(String sf, String pattern, String toSearch,
			int at_level, String lfl, String mname) {
		return findMWAbbr(sf, pattern, toSearch, at_level, lfl, mname, false);
	}

	/**
	 * For acronyms and multiword abbrevia.
	 * 
	 */
	private String findMWAbbr(String sf, String pattern, String toSearch,
			int at_level, String lfl, String mname, boolean last) {
		String lf = null;
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(toSearch);

		//HashMap<String,Integer> matches = new HashMap<String,Integer>();

		while (m.find()) {
			lf = m.group(1).replaceAll("\\s+", " ").replaceAll("\\s+$", "");
			if (LongFormUtils.isDictionaryString(lf) && lf.contains(" ")) {
				if (matches.containsKey(lf))
					matches.put(lf, matches.get(lf)+1);
				else
					matches.put(lf, 1);
				lf = lf;
			}
		}

		// TODO increase freq depending on length? 2 letters match 1?
		// o/w match 0?
//		if (lf != null)
//			printMatches(sf, lfl, at_level, mname, matches, last);
		return lf;
	}

	/**
	 * Find the single-word abbreviations 
	 * Default is not last 
	 * 
	 * @param sf shot forms
	 * @param p pattern
	 * @param toSearch the content to be searched
	 * @param lfl location where the longform found
	 * @param at abbreviation type
	 * @param mname method name
	 * @see #findSWAbbr(String, Pattern, String, String, int, String, boolean)
	 */
	private String findSWAbbr(String sf, Pattern p, String toSearch, String lfl, 
			int at,	String mname) {
		return findSWAbbr(sf, p, toSearch, lfl, at, mname, false);
	}

	/**
	 * Find the single-word abbreviations
	 * @param sf shot forms
	 * @param p pattern
	 * @param toSearch the content to be searched
	 * @param lfl location where the longform found
	 * @param at abbreviation type
	 * @param mname method name
	 * @param last if continue search or not
	 */
	private String findSWAbbr(String sf, Pattern p, String toSearch, String lfl, 
			int at,	String mname, boolean last) {
		String lf = null;
		Matcher m = p.matcher(toSearch);

		//HashMap<String,Integer> matches = new HashMap<String,Integer>();

		while (m.find()) {
			lf = m.group(1);
			if (LongFormUtils.isDictionaryExpansion(lf)) {
				if (matches.containsKey(lf))
					matches.put(lf, matches.get(lf)+1);
				else
					matches.put(lf, 1);
			}
		}
//		if (lf != null)
//			printMatches(sf, lfl, at, mname, matches, last);
		return lf;
	}

	private boolean printMatches(String sf, String lfl, int at, String mname, HashMap<String, Integer> matches, boolean last) {
		// if there's at least one match ...
		if (!matches.isEmpty()) {
			if (matches.keySet().size() == 1) {
				for (String lf : matches.keySet()) {
					printAbbr(sf, lf, at, matches.get(lf), lfl, mname);
					return true; // only print one no matter what!
				}
			} // otherwise, try stems!
			Vector<String> vs = new Vector<String>(matches.keySet());
			for (int i = 0; i < vs.size(); i++) {
				for (int j = i + 1; j < vs.size(); j++) {
					// if they're stems
					if (LongFormUtils.isStem(vs.get(i),vs.get(j))) {
						// go with the shorter one and increment by freq
						int shorter = i;
						int longer = j;
						if (vs.get(i).length() > vs.get(j).length()) {
							shorter = j;
							longer = i;
						}
						// add longer's frequency to shorter
						matches.put( vs.get(shorter), matches.get(vs.get(shorter)) +
								matches.get(vs.get(longer)) );
						// and set longer to 0 (effectively removing)
						matches.put( vs.get(longer), 0 );
					}
				}						
			}
			// Return max
			String smax = "";
			int count = 0;
			int imax = 0;
			HashMap<String, Integer> vmax = new HashMap<String, Integer>();
			for (String lf : matches.keySet()) {
				if (matches.get(lf) > imax) {
					smax = lf;
					imax = matches.get(lf);
					count = 1;
					vmax = new HashMap<String, Integer>();
					vmax.put(lf, imax);
				} else if (matches.get(lf) == imax) {
					count++;
					vmax.put(lf, imax);
				}
			}
			// If 2 have same count, try another level until we win
			if (count == 1) {
				printAbbr(sf, smax, at, imax, lfl, mname);
				return true;
			} else { // count > 1
				// if this is the last chance on this AT
				if (last) {

					//System.out.println("Deciding "+ sf + " " + at + " " + lfl + " " + mname);
					// let freq decide
					printAbbr(sf, vmax.toString().replaceAll(", ", ":"),
							at, 0, lfl, mname);
					return true; // this stops PR going to DL and MW
				}
				//System.out.println("... "+ sf + " " + at + " " + lfl + " " + mname);
				// o/w, keep going up levels, voting on lfs
				// until last level
				return false;
			}
		}
		return false;

	}

	protected void visit(IField field) {
		// TODO Auto-generated method stub

	}

	/**
	 * Init the FileOutputStream and the PrintStream
	 * @throws IOException
	 */
	public void getFW() throws IOException {
		try {
			output = new FileOutputStream(fname);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		emit = new PrintStream(output);
	}

	/**
	 * Close the Steam
	 *
	 */
	public void closeFW() {
		emit.flush();
		emit.close();
		try {
			if (output != null) {
				output.flush();
				output.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Output the result
	 * @param abbr: the short form
	 * @param lf: the long form
	 * @param at_level: abbreviation type level
	 * @param freq: the frequence of the abbreviation
	 * @param lfl_level: location where the longform found
	 * @param document: location where the abbreviation is
	 */
	private void printAbbr(String abbr, String lf, 
			int at_level, int freq, String lfl_level, String document) {
		nonDictWords.put(abbr, true);
		emit.println(abbr+"," + lf + "," + 
				CodeParseConstant.getAcronymType(at_level) + "," + 
				freq + "," + lfl_level + ","+document);

		/*if (abbr.matches(lf))
			System.out.println(abbr+" "+lf+" ("+CodeParseConstant.getAcronymType(at_level)
					+ ", " + lfl_level +") in "+document);*/
	}
}
