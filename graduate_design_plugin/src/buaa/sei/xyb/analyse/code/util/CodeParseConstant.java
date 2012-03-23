package buaa.sei.xyb.analyse.code.util;

public final class CodeParseConstant {
	
	// AT Abbreviation type
	public static final int AT_OTHER		= 0;	// only for "misses"
	public static final int AT_ACRONYM		= 1;
	public static final int AT_PREFIX		= 2;
	public static final int AT_PREFIX_PL	= 3;
	public static final int AT_DROPPED		= 4;
	public static final int AT_MULTI		= 5;
	
	// LFL location where the longform found
	/**
	 * the long form was found in the JavaDoc
	 */
	public static final String LFL_JAVADOC	 	= "JavaDoc";
	/**
	 * the long form is JavaWord
	 */
	public static final String LFL_JAVA_WORD	= "JavaWord";
	/**
	 * the long form was found in the Statement
	 */
	public static final String LFL_STMT 	 	= "Statement";
	/**
	 * the long form was found in the StringLiteral
	 */
	public static final String LFL_STRING 	 	= "StringLiteral";
	/**
	 * the long form was found in the MethodID
	 */
	public static final String LFL_METHOD_ID 	= "MethodID";
	/**
	 * the long form was found in the MethodName
	 */
	public static final String LFL_METHOD_NAME 	= "MethodName";
	/**
	 * the long form was found in the  Method
	 */
	public static final String LFL_METHOD 	 	= "Method";
	/**
	 * the long form was found in its Type
	 */
	public static final String LFL_TYPE 	 	= "Type";

	public static final String LFL_KNOWN 	 	= "Known";
	public static final String LFL_PROPER_NOUN 	= "ProperNoun";
	public static final String LFL_CONTRACTION 	= "Contraction";
	/**
	 * the long form was found in the LeadingComment
	 */
	public static final String LFL_LCOMMENT 	= "LeadingComment";
	/**
	 * the long form was found in Comment
	 */
	public static final String LFL_COMMENT 	 	= "Comment";
	public static final String LFL_ICOMMENT 	= "InternalComment";
	/**
	 * the long form was found in the ClassComment
	 */
	public static final String LFL_CCOMMENT 	= "ClassComment";
	/**
	 * the long form cannot be found.
	 */
	public static final String LFL_MISSED 	 	= "Unknown";
	/**
	 * the long form was found in the Dictionary
	 */
	public static final String LFL_DICTIONARY	= "Dictionary";
	
	// LF Default long form (where to look next)
	public static final String LF_MISSED = "MISSED";
	/*public static final String LF_METHOD = "Method:";
	public static final String LF_FIELD  = "Field:";
	public static final String LF_TYPE   = "Type:";
	public static final String LF_DECL   = "DECL";*/
	
	public static String getAcronymType(int at) {
		switch (at) {
		case AT_OTHER:
			return "OO";
		case AT_ACRONYM:
			return "AC";
		case AT_PREFIX:
			return "PR";
		case AT_PREFIX_PL:
			return "PP";
		case AT_DROPPED:
			return "DL";
		case AT_MULTI:
			return "MW";
		default:
			return "OO";
		}
	}

}

