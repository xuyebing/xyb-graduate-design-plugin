package buaa.sei.xyb.analyse.code.util;

import java.io.PrintStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;


public class SourceProcessor {

	private static Log log = LogFactory.getLog(SourceProcessor.class);
	
	private IMethod im;
	private String src = "";
	private String methodName = "";
	// The comments just befor the method. It means that the closet up comments
	private String leadingComments = "";
	// The internal comments in a method when dealing with the method, 
	// if not, it belongs to the internalComments.
	private String internalComments = "";
	// Other class comments except the leadingComments and internalComments
	private String classComments = "";
	// The method body without the internalComments.
	private String methodBody = "";
	// The methodBody removing of the stop words and so on.
	private String methodBodyClean = "";
	private String methodNameClean = "";
	private String statementsClean = "";
	private String types = "";
	private String methodIDsClean = "";
	private String methodDictionary = "";
	private String leadingCommentDictionary = "";
	private String statementsDictionary = "";
	private String typesSeparated = "";
	private String leadingCommentsClean = "";
	private String internalCommentsClean = "";
	private String classCommentsClean = "";
	private String stringLiterals = "";
	private String stringLiteralsClean = "";
	private String stringLiteralsDictionary = "";
	private String leadingCommentsCleanSplit = "";
	private String internalCommentsCleanSplit = "";
	private String classCommentsCleanSplit = "";
	private String internalCommentDictionary = "";
	private String commentDictionary = "";
	private String classCommentDictionary = "";
	private String commentsCleanSplit = "";
	private String statementsMarkedDictionary ="";
	private String javaDocComments = "";
	
	private String methodNameCleanNoStop = "";
	private String methodIDsCleanNoStop = "";
	private String stringLiteralsCleanNoStop = "";
	private String methodBodyCleanNoStop = "";
	
	public SourceProcessor(IMethod i) {
//		log.info(">>>Parse the method: " + i.getElementName());
		im = i;
		methodName = getSignature();
		
		try {
			src = im.getSource();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		processComments();
		//changed by hxd
//		createCleanDictionaries2();
		createCleanDictionaries();
	}
	
	private void createCleanDictionaries2() {
		// TODO Auto-generated method stub		
		// remove loan 'int' surronded by spaces or (); and replace
		// with type to match 'i' and other 'int'
		methodBodyClean = 
			methodBody.replaceAll(
					"(([,\\(]\\s*)|(\\s+))int((\\s+)|(\\s*[,\\)]))",
					"$1 integer $4");
	
		methodBodyClean  = " " + 
				NLUtils.cleanString(NLUtils.splitCamel(
				methodBodyClean)) + " ";
		
		methodBodyCleanNoStop  = " " + LongFormUtils.removeStopWords(
				methodBodyClean) + " ";
		
		// can't remove stop words here, have to remove later!!
		/*statementsClean = " " + LongFormUtils.removeStopWords(
				NLUtils.splitCamel(
			methodBody.replaceAll("[^A-Za-z;\\{}]+", " ")
			.replaceAll("[;\\{}]", " \\.") ).toLowerCase()) + " ";*/
		
		
		leadingCommentsClean = " " + LongFormUtils.removeStopWords(
				NLUtils.cleanStringLeaveDot(
				leadingComments)) + " ";
		internalCommentsClean = " " + LongFormUtils.removeStopWords(
				NLUtils.cleanStringLeaveDot(
				internalComments)) + " ";
		classCommentsClean = " " + LongFormUtils.removeStopWords(
				NLUtils.cleanStringLeaveDot(
				classComments)) + " ";
		
		leadingCommentsCleanSplit = " " + LongFormUtils.removeStopWords(
				NLUtils.cleanStringLeaveDot(
				NLUtils.splitCamel(leadingComments))) + " ";
		internalCommentsCleanSplit = " " + LongFormUtils.removeStopWords(
				NLUtils.cleanStringLeaveDot(
				NLUtils.splitCamel(internalComments))) + " ";
		
	    Pattern p;
	    Matcher m;
		// String literals
		p = Pattern.compile("\"(.*?)\"");
		m = p.matcher(methodBody);
		while (m.find()) {
			stringLiterals += m.group(1) + ". ";
		}
		/*
		stringLiteralsClean = " " + NLUtils.cleanStringLeaveDot(
				NLUtils.splitCamel(stringLiterals)) + " ";
		System.out.println("stringLiteralsCleanNoStop:"+stringLiteralsCleanNoStop);
		stringLiteralsCleanNoStop = " " + 
		LongFormUtils.removeStopWords(stringLiteralsClean) + " ";
		
	*/
	}

	/**
	 * Get the full method name
	 * @return the name of the method
	 */
	private String getSignature() {
		String sig;
		String cl = im.getDeclaringType().getFullyQualifiedName();

		if (cl.length() < 1)
			sig = "";
		else
			sig = cl + ".";

		sig += im.getElementName();

		//If the method has parameters
		if (im.getNumberOfParameters() > 0) {		
			String[] types = null;
			sig += "(";

			// Get types and names
			types = im.getParameterTypes();

			// Pretty print in a string
			for (int i = 0; i < types.length; i++) {
				sig += Signature.getSignatureSimpleName(types[i]);
				if (i < types.length - 1)
					sig += "_";
			}
			sig += ")";
		} else { // Otherwise
			sig += "()";
		}
		log.debug("Signature: " + sig);
		return sig;
	}
	
	private String getFormals() {
		String sig = "";

		//If the method has parameters
		if (im.getNumberOfParameters() > 0) {		
			String[] names = null;
			String[] types = null;
			sig += "(";

			// Get types and names
			try {
				names = im.getParameterNames();
				types = im.getParameterTypes();
			} catch (JavaModelException e) {
				e.printStackTrace();
			}

			// Pretty print in a string
			for (int i = 0; i < types.length; i++) {
				sig += Signature.getSignatureSimpleName(types[i]);
				sig += " " + names[i];

				if (i < types.length - 1) {
					sig += ",";
					sig += " ";
				}
			}
			sig += ")";
		} else { // Otherwise
			sig += "()";
		}

		return sig;
	}

	
	private String getClassName() {
		String sig;
		String cl = im.getDeclaringType().getFullyQualifiedName();
		if (cl.length() < 1)
			sig = "";
		else
			sig = cl + ".";
		return sig;
	}

	private String getSignatureNoClass() {
		String sig = im.getElementName();

		//If the method has parameters
		if (im.getNumberOfParameters() > 0) {		
			String[] types = null;
			sig += "(";

			// Get types and names
			types = im.getParameterTypes();

			// Pretty print in a string
			for (int i = 0; i < types.length; i++) {
				sig += Signature.getSignatureSimpleName(types[i]);
				if (i < types.length - 1)
					sig += "_";
			}
			sig += ")";
		} else { // Otherwise
			sig += "()";
		}
		return sig;
	}


	public String getInternalComments() {
		return internalComments;
	}


	public String getLeadingComments() {
		return leadingComments;
	}


	public String getMethodBody() {
		return methodBody;
	}

	/**
	 * Get the full method name.
	 * @return Example: use this method in "java.util.String.indexAt(int)"
	 * returns "java.util.String.indexAt(int)"
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * 
	 * @return the source of we parse, including the method body and its leading comments
	 */
	public String getSrc() {
		return src;
	}
	
	/**
	 * Get the comments from the source, including the leadingComments,
	 * classComments, javaDocComments and the methodBody.
	 *
	 */
	private void processComments() {
		try {
			ICompilationUnit icu = im.getCompilationUnit();
			int start = im.getSourceRange().getOffset();
			int length = im.getSourceRange().getLength();
			//int commentOffset = start;
			//int commentLength = length;
			
			ASTParser lParser;

			// Create a parser that can get comments
			lParser = ASTParser.newParser(AST.JLS3); 
			lParser.setKind(ASTParser.K_COMPILATION_UNIT);
			lParser.setSource(icu); // set source
			lParser.setResolveBindings(false); // we need bindings later on

			try {
				CompilationUnit cu = (CompilationUnit) lParser.createAST(null);

				List<Comment> l = cu.getCommentList();

				int lastEnd = start;
				methodBody = "";
				// Only save the comments in the method source range
				for (Comment node : l) {
					// get leading comment
					if (node.getStartPosition() == start) {
						leadingComments = icu.getBuffer().getText( 
								node.getStartPosition(), node.getLength());
						/*commentOffset = 
							node.getStartPosition() + node.getLength();
						commentLength = length - node.getLength();*/
						
						// Ok, this means we've hit the start of the method
						// start getting body
						lastEnd = node.getStartPosition() + node.getLength();
						log.debug("leading comments: " + leadingComments);
					}
					// get the rest of the comments
					// TODO Test if internal dot makes sense
					else if (node.getStartPosition() > start &&
								node.getStartPosition() <= start+length) {
						internalComments = internalComments + ".\n" +
								icu.getBuffer().getText( 
								node.getStartPosition(), node.getLength());
						// body goes from end of last comment to beggining
						// of current one
						log.debug("internal comments: " + internalComments);
						methodBody += icu.getBuffer().getText(lastEnd, 
								node.getStartPosition() - lastEnd);
						lastEnd = node.getStartPosition() + node.getLength();
					} else {
						classComments = classComments + ".\n" + 
							icu.getBuffer().getText( 
							node.getStartPosition(), node.getLength());
						log.debug("class comments: " + classComments);
					}
				}
				
				// Get the last bit of comment
				methodBody += icu.getBuffer().getText(lastEnd, 
						(start + length) - lastEnd);
					
				// TODO: Remove nonDict keywords like int

				// Strip off leading method name
				int strip = methodBody.indexOf('{');
				
				if (strip < 0)
					strip = methodBody.indexOf(';');
				
				if (strip > 0)
					methodBody = methodBody.substring(strip);
				
				methodBody = "  " + im.getElementName() + "  " + getFormals() + 
							 "\n" + methodBody;
				log.debug("method body: " + methodBody);
				
				// Remove the comments signature
				leadingComments = 
					leadingComments.replaceAll("[/\\*\\\\]", "");
				internalComments = 
					internalComments.replaceAll("[/\\*\\\\]", "");
				
				classComments = 
					classComments.replaceAll("[/\\*\\\\]", "");
				// Need to process javaDoc before stop words because 
				// won't preserve spaces
				getJavaDoc();
				
			} catch (Exception e) {
				System.err.println(im.getElementName() + ": source problem");
				//System.err.println(start + " " + length + " " + commentOffset);
				e.printStackTrace();
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get java doc from comments. Since imperfect matching on leading
	 * comments that aren't /** or /*, need to check internal too.
	 * @return @param* matches
	 * 'pos -- Position (index in the value table) of the new datapoint.'
	 * Grabs until newline
	 */
	private void getJavaDoc() {
		String toProcess = leadingComments + "\n.\n" + internalComments + "\n.\n";
		javaDocComments = "";
		
		Pattern p = Pattern.compile("@param\\S*\\s+(\\w+)\\s+([^\\n]+)\\n");
		Matcher m = p.matcher(toProcess);
		while (m.find()) {
			javaDocComments = javaDocComments + 
					NLUtils.cleanString(NLUtils.splitCamel(m.group(1))) + 
					" -- " + 
					LongFormUtils.removeStopWords(
							NLUtils.cleanString(NLUtils.splitCamel(
									m.group(2)))) +
					". \n";
		}
		
		// will remove short forms too!
		/*javaDocComments = " " + 
		LongFormUtils.removeStopWords(javaDocComments) + " ";*/
		
		/*System.out.println("************************************");
		System.out.println(methodName + "(" + methodNameClean + ")");
		System.out.println("______________\n" + toProcess);
		System.out.println("______________\n" + javaDocComments);*/
	}


	private void createCleanDictionaries() {
				
		methodNameClean = " " + NLUtils.cleanString(NLUtils.splitCamel(
				im.getElementName()));
		
		methodNameCleanNoStop = " " + 
		LongFormUtils.removeStopWords(methodNameClean) + " ";
		
		// remove loan 'int' surronded by spaces or (); and replace
		// with type to match 'i' and other 'int'
		methodBodyClean = 
			methodBody.replaceAll(
					"(([,\\(]\\s*)|(\\s+))int((\\s+)|(\\s*[,\\)]))",
					"$1 integer $4");
	
		methodBodyClean  = " " + 
				NLUtils.cleanString(NLUtils.splitCamel(
				methodBodyClean)) + " ";
		
		methodBodyCleanNoStop  = " " + LongFormUtils.removeStopWords(
				methodBodyClean) + " ";
		
		// can't remove stop words here, have to remove later!!
		/*statementsClean = " " + LongFormUtils.removeStopWords(
				NLUtils.splitCamel(
			methodBody.replaceAll("[^A-Za-z;\\{}]+", " ")
			.replaceAll("[;\\{}]", " \\.") ).toLowerCase()) + " ";*/
		
		statementsClean = " " + 
				NLUtils.splitCamel(
			methodBody.replaceAll("[^A-Za-z;\\{}]+", " ")
			.replaceAll("[;\\{}]", " \\.") ).toLowerCase() + " ";
		
		methodIDsClean = " " + NLUtils.splitCamel(
				methodBody.replaceAll("\\W", " . "))
				.replaceAll("[0-9_]", " ").toLowerCase() + " ";
		
		methodIDsCleanNoStop = " " + 
		LongFormUtils.removeStopWords(methodIDsClean) + " ";
		
		
		leadingCommentsClean = " " + LongFormUtils.removeStopWords(
				NLUtils.cleanStringLeaveDot(
				leadingComments)) + " ";
		internalCommentsClean = " " + LongFormUtils.removeStopWords(
				NLUtils.cleanStringLeaveDot(
				internalComments)) + " ";
		classCommentsClean = " " + LongFormUtils.removeStopWords(
				NLUtils.cleanStringLeaveDot(
				classComments)) + " ";
		
		leadingCommentsCleanSplit = " " + LongFormUtils.removeStopWords(
				NLUtils.cleanStringLeaveDot(
				NLUtils.splitCamel(leadingComments))) + " ";
		internalCommentsCleanSplit = " " + LongFormUtils.removeStopWords(
				NLUtils.cleanStringLeaveDot(
				NLUtils.splitCamel(internalComments))) + " ";
		
		commentsCleanSplit = " " + LongFormUtils.removeStopWords(
				NLUtils.cleanStringLeaveDot(
				NLUtils.splitCamel(leadingComments + 
						" . " + internalComments))) + " ";
		classCommentsCleanSplit = " " + LongFormUtils.removeStopWords(
				NLUtils.cleanStringLeaveDot(
				NLUtils.splitCamel(classComments))) + " ";
				
		statementsDictionary  = " " + 
			LongFormUtils.dotNonDictionaryWords(statementsClean) + " ";
		
		statementsMarkedDictionary  = " " + 
			LongFormUtils.annotateDictionaryWords(statementsClean) + " ";
		
		// TODO: Remove stop words??
		methodDictionary = " " + 
			LongFormUtils.getDictionaryWords(methodBodyCleanNoStop) +" ";
		
		stringLiteralsDictionary = " " + 
			LongFormUtils.getDictionaryWords(stringLiteralsClean) + " ";
		
		leadingCommentDictionary = " " + 
			LongFormUtils.getDictionaryWords(leadingCommentsCleanSplit) + " ";
		
		commentDictionary = " " + 
		LongFormUtils.getDictionaryWords(commentsCleanSplit) + " ";
		
		classCommentDictionary = " " + 
		LongFormUtils.getDictionaryWords(classCommentsCleanSplit) + " ";

		
		internalCommentDictionary  = " " + 
			LongFormUtils.getDictionaryWords(internalCommentsCleanSplit) + " ";
		
		// TYPES
		// variable declarations
		Pattern p = Pattern.compile("\\s+(([\\.\\w<>\\[\\]]+)\\s+(\\w+))\\s*[=;]");
		Matcher m = p.matcher(methodBody);
		while (m.find()) {
			String t = m.group(2);
			if (! (t.matches("return") || t.matches("new") )) {
				String lf = NLUtils.cleanString(NLUtils.splitCamel(m.group(2)));
				lf = LongFormUtils.getDictionaryWords(lf);
				if (!lf.matches("\\s*")) {
					types = types + ". " +  m.group(1);
					typesSeparated = typesSeparated + ". " + lf   + 
					" == " + NLUtils.cleanString(NLUtils.splitCamel(m.group(3)));
				}
			}
		}
		typesSeparated = typesSeparated + ". ";

		// exceptions
		p = Pattern.compile("catch\\s*\\(\\s*((\\S+)\\s+(\\w+))\\s*\\)");
		m = p.matcher(methodBody);
		while (m.find()) {
			String lf = NLUtils.cleanString(NLUtils.splitCamel(m.group(2)));
			lf = LongFormUtils.getDictionaryWords(lf);
			if (!lf.matches("\\s*")) {
				types = types + ". " +  m.group(1);
				typesSeparated = typesSeparated + ". " + lf
				+ " == " + NLUtils.cleanString(NLUtils.splitCamel(m.group(3)));
			}
		}
		typesSeparated = typesSeparated + ". ";
		
		// formals
		p = Pattern.compile("\\s*(([\\.\\w<>\\[\\]]+)\\s+(\\w+))\\s*[,\\)]");
		m = p.matcher(getFormals());
		while (m.find()) {
			String lf = NLUtils.cleanString(NLUtils.splitCamel(m.group(2)));
			lf = LongFormUtils.getDictionaryWords(lf);
			if (!lf.matches("\\s*")) {
				types = types + ". " +  m.group(1);
				typesSeparated = typesSeparated + ". " + lf
				+ " == " + NLUtils.cleanString(NLUtils.splitCamel(m.group(3)));
			}
		}
		typesSeparated = typesSeparated + ". ";
		
		// TODO verify this is ok!!!
		/*typesSeparated = 
			LongFormUtils.getDictionaryWords(typesSeparated);*/
		
		// String literals
		p = Pattern.compile("\"(.*?)\"");
		m = p.matcher(methodBody);
		while (m.find()) {
			stringLiterals += m.group(1) + ". ";
		}
		stringLiteralsClean = " " + NLUtils.cleanStringLeaveDot(
				NLUtils.splitCamel(stringLiterals)) + " ";
		System.out.println("stringLiteralsCleanNoStop:"+stringLiteralsCleanNoStop);
		stringLiteralsCleanNoStop = " " + 
		LongFormUtils.removeStopWords(stringLiteralsClean) + " ";
		
	}

	public String getLeadingCommentDictionary() {
		return leadingCommentDictionary;
	}


	public IMethod getIm() {
		return im;
	}


	/**
	 * Returns stuff of the form
	 * " this guitar g  super doody     hello  type ht   this y   hash  set string  hss  "
	 * @return
	 */
	public String getMethodBodyClean() {
		return methodBodyClean;
	}


	public String getMethodDictionary() {
		return methodDictionary;
	}


	public String getMethodIDsClean() {
		return methodIDsClean;
	}

	/**
	 * Get the method name just.
	 * 
	 * @return Example: method "java.util.String.indexAt(int)", 
	 * this method return "indexAt"
	 */
	public String getMethodNameClean() {
		return methodNameClean;
	}


	public String getStatementsClean() {
		return statementsClean;
	}

	public String getTypes() {
		return types;
	}


	public String getStatementsDictionary() {
		return statementsDictionary;
	}


	/**
	 * Returns stuff of the form
	 * ".   hello  type == ht.   hash  set   string  == hss. int == computer."
	 * @return
	 */
	public String getTypesSeparated() {
		return typesSeparated;
	}


	public String getInternalCommentsClean() {
		return internalCommentsClean;
	}


	public String getLeadingCommentsClean() {
		return leadingCommentsClean;
	}


	public String getInternalCommentsCleanSplit() {
		return internalCommentsCleanSplit;
	}


	public String getLeadingCommentsCleanSplit() {
		return leadingCommentsCleanSplit;
	}


	public String getInternalCommentDictionary() {
		return internalCommentDictionary;
	}

	/**
	 * Returns stuff of the form
	 * "  int  i  .  system --  out --  println  sample --  hello --  i  "
	 * @return
	 */
	public String getStatementsMarkedDictionary() {
		return statementsMarkedDictionary;
	}

	/**
	 * 
	 * @return 在leadingComments中以@开头的注释
	 */
	public String getJavaDocComments() {
		return javaDocComments;
	}

	/**
	 * 
	 * @return method body 中的双引号里的内容。
	 */
	public String getStringLiterals() {
		return stringLiterals;
	}
	
	/**
	 * 
	 * @return method body 中的双引号里的内容，已经去掉停用词。
	 */	
	public String getStringLiteralsClean() {
		return stringLiteralsClean;
	}

	/**
	 * 
	 * @return method body 中的双引号里且在词典中的词。
	 */
	public String getStringLiteralsDictionary() {
		return stringLiteralsDictionary;
	}


	public String getMethodNameCleanNoStop() {
		return methodNameCleanNoStop;
	}

	/**
	 * 
	 * @return method body 中的双引号里的内容，已经去掉停用词。
	 */	
	public String getStringLiteralsCleanNoStop() {
		return stringLiteralsCleanNoStop;
	}

	/**
	 * 
	 * @return identifiers in the method without the stop words.
	 */
	public String getMethodIDsCleanNoStop() {
		return methodIDsCleanNoStop;
	}

	public String getCommentDictionary() {
		return commentDictionary;
	}

	public String getCommentsCleanSplit() {
		return commentsCleanSplit;
	}

	public String getClassCommentDictionary() {
		return classCommentDictionary;
	}

	public String getClassComments() {
		return classComments;
	}

	public String getClassCommentsClean() {
		return classCommentsClean;
	}

	public String getClassCommentsCleanSplit() {
		return classCommentsCleanSplit;
	}

	public String getMethodBodyCleanNoStop() {
		return methodBodyCleanNoStop;
	}

	public void printAll(PrintStream out) {
		out.println("************************************");
		out.println(methodName + "(" + methodNameClean + ")");
		out.println(statementsClean);
		out.println(methodBodyClean);
		out.println(methodBody);
		out.println(methodNameClean);
		out.println("______________\nmethodNameCleanNoStop:" + methodNameCleanNoStop);
		out.println("______________\ntypes:" + types);
		out.println("______________\ntypesSeparated:" + typesSeparated);
		out.println("______________\nmethodIDsClean:" + methodIDsClean);
		out.println("______________\nstatementsClean:" + statementsClean);
		out.println("______________\nleadingComments:" + leadingComments);
		out.println("______________\ninternalComments:" + internalComments);
		
		out.println("______________\nleadingCommentsCleanSplit:" + leadingCommentsCleanSplit);
		out.println("______________\ninternalCommentsCleanSplit:" + internalCommentsCleanSplit);
		
		out.println("______________\nmethodDictionary:" + methodDictionary);
		out.println("______________\nleadingCommentDictionary:" + leadingCommentDictionary);
		out.println("______________\ninternalCommentDictionary:" + internalCommentDictionary);
		out.println("______________\nstatementsClean:" + statementsClean);
		out.println("______________\nstatementsDictionary:" + statementsDictionary);
		out.println("______________\njavaDocComments:" + javaDocComments);
		out.println("______________\nstringLiterals:" + stringLiterals);
		out.println("______________\nstringLiteralsCleanNoStop:" + stringLiteralsCleanNoStop);
		out.println("______________\nmethodIDsCleanNoStop:" + methodIDsCleanNoStop);
		
		
		
		out.println("\n");
	}

}

