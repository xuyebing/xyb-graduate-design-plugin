package buaa.sei.xyb.common;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Ϊ�ı��������ṩ���õĹ���
 * @author lai
 *
 */
public class CommonTool {
	private static Log log = LogFactory.getLog(CommonTool.class);
	
	/* 
	 * �ж�pCharacter�Ƿ���ĸ
	 * @param pCharacter ���жϵ��ַ�
	 * @return <code>true</code> ���pCharacter����ĸ, <code>false</code>
	 * 		   pCharacter������ĸ.
	 */
	public static boolean isLetter(char pCharacter) {
		boolean capital, lowercase;
		capital = (pCharacter >= 'A') && (pCharacter <= 'Z');
		lowercase = (pCharacter >= 'a') && (pCharacter <= 'z');

		return (capital || lowercase);
	}
	
	/*
	 * Determines if a string is a number
	 * @param pString 
	 * 		  Stringa letta
	 * @return <code>true</code> If pString is a number, <code>false</code> 
	 * 		   otherwise
	 */
	public static boolean isNumber(String pString) {
		boolean digit;
		for(int i = 0; i < pString.length(); ++i) {
			// The function considers a number a word that begins with a figure
			char firstChar = pString.charAt(i);
			digit = (firstChar >= '0') && (firstChar <= '9');
			if(!digit)
				return false;
		}
		return true;
	}
	
	/*
	 * �ж��Ƿ��´�.
	 * @param pCharacter char���ַ�
	 * @return <code>true</code> ���pCharacter�ǻس����ո񡢵����š��»��߻����ţ�
	 * 		   <code>false</code> �����ַ�.
	 */
	public static boolean newWord(char pCharacter) {
		char[] token = {' ', (char) 10, (char) 13, '\n', '\t', '\'', '"', '_', 
		'<', '>', '(', ')', '[', ']', '{', '}', '@', '?', '!', '#', '%', '\\', 
		'/', '-', '+', '*', ':', '&', '$', '`', '~', '|', ',', '.', ';'};
		
		for(int i = 0; i < token.length; ++i)
			if(pCharacter == token[i])
				return true;
		return false;
	}
	
	/* ��CamelCaseIdentifier��ʽ�ı�ʶ�����зָ�磺splitCamelCaseIdentifier���ָ�Ϊ��
	 * split, camel, case �� identifier.
	 * @param pIdentifier 
	 * 		  ��ʶ��������
	 * @return A string containing several words that make up pIdentifier.
	 */
	public static ArrayList<String> splitCamelCaseIdentifier(String pIdentifier) {
		ArrayList<String> wordList = new ArrayList<String>();
		
		String regex = "[A-Z]";
		String regexStart = "(^[a-z]+)";
		String regexWord1 = "([A-Z][a-z]+)";
		String regexWord2 = "([A-Z]+[A-Z]$)|([A-Z]+)[A-Z]";
		Matcher matcher;
		
		// if the source string dosen't contain the Capitalization, do nothing.
		matcher = Pattern.compile(regex).matcher(pIdentifier);
		if(!matcher.find()) {
			wordList.add(pIdentifier);
			return wordList;
		}
		
		matcher = Pattern.compile(regexStart).matcher(pIdentifier);
		if(matcher.find()) {
//			log.debug("find: " + matcher.group(1));
			wordList.add(matcher.group(1));
		}
		
		matcher = Pattern.compile(regexWord1).matcher(pIdentifier);
		while(matcher.find()) {
//			log.debug("find:" + matcher.group(1));
			wordList.add(matcher.group(1));
		}
		
		matcher = Pattern.compile(regexWord2).matcher(pIdentifier);
		while(matcher.find()) {
			String result;
			if(matcher.group(1) != null)
				result = matcher.group(1);
			else
				result = matcher.group(2);
//			log.debug("fine: " + result);
			if(result.length() > 1)
				wordList.add(result);
		}
		
		return wordList;
	}
	public static void main(String[] args)
	{
		System.out.println(splitCamelCaseIdentifier("AAA"));
		System.out.println(splitCamelCaseIdentifier("AaAaa"));
		System.out.println(splitCamelCaseIdentifier("AAAaaaAAddSdfg"));
		System.out.println(splitCamelCaseIdentifier("aAA"));
		System.out.println(splitCamelCaseIdentifier("StudentName"));
	}
}


