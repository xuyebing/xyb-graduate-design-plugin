package buaa.sei.xyb.analyse.code;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jeasy.analysis.MMAnalyzer;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import buaa.sei.xyb.analyse.code.util.LongFormUtils;
import buaa.sei.xyb.analyse.code.util.SourceProcessor;
import buaa.sei.xyb.common.CommonTool;
import buaa.sei.xyb.common.DocumentDescriptor;
import buaa.sei.xyb.common.dict.SegTran;

public class JavaCodeParser {

	private String projectDir;
	private String className;
	private DocumentDescriptor docDescriptor;
	private CodeExtractElement codeExtractElement;
	private String otherComments = "";
	private String classComments = "";
	private String methodComments = "";
	private String body = "";
	private String partition = "\\W|_";
	private HashMap<String, Integer> termMap = new HashMap<String, Integer>(); //termMap保存了一个词（String）以及该词出现的次数（Integer）
	
	public void setProjectDir(String projectDir) {
		this.projectDir = projectDir;
	}
	public HashMap<String, Integer> analyze(IType element) throws JavaModelException {
		int index = element.getPath().toOSString().indexOf("\\", 1);
		String location = element.getPath().toOSString().substring(index);
		String javaFileName = projectDir + location;
		className = element.getElementName();
		System.out.println("==>> Parse the source code: " + javaFileName + ", class name: " + className);
		
		body = className.toLowerCase();
		
//		docDescriptor = new DocumentDescriptor(Constant.globalCategoryID, className, javaFileName);
		parseClass(element);
		// 将代码中的标识符和注释进行分词和翻译，保存分词结果到文件中
		createTerms();
		Set<String> terms = termMap.keySet();
		Iterator<String> t_it = terms.iterator();
		System.out.println("%%%%%%%%%%%%% 输出代码中的词  %%%%%%%%%%%");
		while(t_it.hasNext()) {
			String term = t_it.next();
			System.out.println("\t\t term = " + term + " ; frequency = " + termMap.get(term));
		}
		return termMap;
	}
	/**
	 * 
	 *
	 */
	private void createTerms() {
		/* 由于body中已经包含了方法名和属性名，所以不需要单独去处理它们 */
//		// 将代码中的属性名和方法名分词处理
//		LinkedList<String> fieldsName = codeExtractElement.getFieldsName();
//		Iterator<String> f_it = fieldsName.iterator();
//		String fn_content = ""; // 保存所有fieldName的分词结果
//		while(f_it.hasNext()) {
//			String fieldName = f_it.next();
//			fn_content += splitCamelCaseIdentifier(fieldName) + " ";
//		}
//		LinkedList<String> methodsName = codeExtractElement.getMethodsName();
//		Iterator<String> m_it = methodsName.iterator();
//		String mn_content = ""; // 保存所有methodName的分词结果
//		while(m_it.hasNext()) {
//			String methodName = m_it.next();
//			mn_content += splitCamelCaseIdentifier(methodName) + " ";
//		}
//		// fields
//		getTermsFromString(fn_content);
//		// methods
//		getTermsFromString(mn_content);
		// otherComments
		getTermsFromString(codeExtractElement.getOtherComments());
		// classComments
		getTermsFromString(codeExtractElement.getClassComments());
		// methodComments
		getTermsFromString(codeExtractElement.getMethodComments());
		// body
		getTermsFromString(codeExtractElement.getBody());
	}
	/**
	 * 从String中提取term
	 * @param content 待分析的String
	 */
//	1. 修改UI 中的 Data Dict，让它可以选择文件、而不是文件夹
//	2. 继续利用数据词典和词典进行英文到中文的翻译
	private void getTermsFromString(String content) {
		if(!content.equals("")) {
			try {
				if (SegTran.analyzer == null)
					SegTran.init();
				System.out.println("&&&&&&&  翻译前  &&&&&&");
				System.out.println(" content = " + content);
				content=SegTran.ChiEng2Chi(content);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(!content.equals(""))
		{
		    String[] words = content.split(" ");
			for(int i = 0; i < words.length; ++i) {
				//if(words[i].length() > 2) {
					if(termMap.containsKey(words[i])) {
						int wordNum = termMap.get(words[i]);
						termMap.put(words[i], wordNum + 1);
					} else {
						termMap.put(words[i], 1);
					}
				//}
			}
		}
	}
	
	private void parseClass(IType javaClass) throws JavaModelException {
		getJavaChildren(javaClass);
		
		distillClassComments(javaClass);
		IJavaElement[] children = javaClass.getChildren();
		for (IJavaElement child : children) {
			if (child instanceof IMethod) {
				parseMethod((IMethod)child);
			} else if (child instanceof IField) {
				String fieldBody = ((IField)child).getSource();
				parseField(fieldBody);
			}
		}
		// 打印所有分析的结果
		otherComments = cleanComments(otherComments);
		classComments = cleanComments(classComments);
		methodComments = cleanComments(methodComments);
		System.out.println("====>> otherComments : " + otherComments);
		System.out.println("====>> classComments : " + classComments);
		System.out.println("====>> methodComments : " + methodComments);
		System.out.println("====>> body : " + body);
		codeExtractElement.setOtherComments(otherComments.trim());
		codeExtractElement.setClassComments(classComments.trim());
		codeExtractElement.setMethodComments(methodComments.trim());
		codeExtractElement.setBody(body);
	}
	private String cleanComments (String comments) {
		// 去除eclipse自动产生的注释:"// TODO Auto-generated method stub && catch block"
		comments = comments.replaceAll("\\b(TODO Auto-generated method stub|" +
				                           "TODO Auto-generated catch block|" +
				                           "TODO add action|" +
				                           "TODO Auto-generated constructor stub" +
				                       ")\\b", "");
		return comments;
	}
	
	private void getJavaChildren(IType javaClass) throws JavaModelException {
		LinkedList<String> fieldList = new LinkedList<String>();
		LinkedList<String> methodList = new LinkedList<String>();
		IField[] fields = javaClass.getFields();
		IMethod[] methods = javaClass.getMethods();
		System.out.println("--------------> Fields:");
		for (IField field : fields) {
			fieldList.add(field.getElementName()); // 获得所有的属性名
			System.out.println("\t\t" + field.getElementName());
		}
		System.out.println("--------------> Methods:");
		for (IMethod method : methods) {
			methodList.add(method.getElementName()); // 获得所有的方法名
			System.out.println("\t\t" + method.getElementName());
		}
		if (codeExtractElement == null)
			codeExtractElement = new CodeExtractElement();
		codeExtractElement.setFieldsName(fieldList);
		codeExtractElement.setMethodsName(methodList);
	}
	private void distillClassComments(IType javaClass) throws JavaModelException {
		String comment = "";
		ISourceRange commentRange = javaClass.getJavadocRange();
		if (commentRange != null) {
			comment = javaClass.getCompilationUnit().getBuffer().getText(commentRange.getOffset(), commentRange.getLength());
			System.out.println("comment = " + comment);
			comment = splitCamelCaseIdentifier(comment);
		}
		classComments += comment + " ";
	}
	
	/**
	 * 分析类中的方法
	 * @param method : 待分析的方法
	 */
//	参见师兄毕设 JavaCodeParser.java
	private void parseMethod(IMethod method) {
		SourceProcessor methodParser = new SourceProcessor(method);

		otherComments += methodParser.getInternalCommentsClean();
		otherComments += methodParser.getStringLiteralsCleanNoStop();
//		otherComments += methodParser.getInternalComments();
//		otherComments += methodParser.getStringLiterals();

		if(method.getElementName().equals(className)) // 说明是构造方法
			//classComments += methodParser.getLeadingCommentsCleanSplit();
		    classComments += methodParser.getLeadingComments();
		else
			//methodComments += methodParser.getLeadingCommentsCleanSplit();
		    methodComments += methodParser.getLeadingComments();

		AMAPParser amap = new AMAPParser(methodParser);
		String tmp = amap.getAbbrExpandBody();
		System.out.println("before body: " + methodParser.getMethodBodyCleanNoStop());
		System.out.println("after body: " + tmp);
		body  += tmp + " ";
//		// 加入方法名，及方法的参数名
//		body += method.getElementName() + " ";
//		try {
//			String[] params = method.getParameterNames();
//			for (String paramName : params) {
//				body += paramName + " ";
//			}
//		} catch (JavaModelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
       }

	/**
	 * 分析类变量
	 */
	private void parseField(String fieldBody) throws JavaModelException {
		String comment = "";
		String fieldBodyClean = "";
		fieldBody = fieldBody.replaceAll("\r\n|\n", " ");

		// 使用字符串匹配
		String regex = "/\\*+?.+?\\*/";
		Matcher matcher = Pattern.compile(regex, Pattern.DOTALL).matcher(fieldBody);
		while(matcher.find()) {
			comment += matcher.group() + " ";
		}
		fieldBodyClean += fieldBody.replaceAll(regex, " "); // 去除所有的/**/注释

		// 匹配以"//"形式编写的注释
		regex = "//.*";
		matcher = Pattern.compile(regex).matcher(fieldBody);
		while(matcher.find()) {
			comment += matcher.group() + " ";
		}

		// 匹配双引号里面的内容
		fieldBodyClean = fieldBodyClean.replaceAll(regex, " "); // 去除所有的//注释

		body += splitCamelCaseIdentifier(fieldBodyClean) + " "; // body对应属性字段中提取的信息，如:String a = "内容",则body将包含"内容"
		
		otherComments += splitCamelCaseIdentifier(comment) + " ";
//        body += fieldBodyClean + " ";
//		otherComments += comment + " ";
	}
	
	private String splitCamelCaseIdentifier(String str)  {
		//added by han 添加提取中文操作，因为原来的没有处理中文的功能，把中文过滤掉了。
	    String result = "";
	    String regEx = "[\\u4E00-\\u9FA5]";
	    Pattern pattern = Pattern.compile(regEx);
	    Matcher matcher = pattern.matcher(str);
	    if (matcher.find()) { // 判断是包含了中文
			try {
				MMAnalyzer cnAnalyzer = new MMAnalyzer();
				str = cnAnalyzer.segment(str, " ");
			} catch (IOException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
			String[] CEs=str.split(" ");
			for(int i = 0; i < CEs.length; ++i) {
		    	if(CEs[i].matches("[\\u4E00-\\u9FA5]+"))
		    	{
			    	result+=CEs[i]+" ";
		    	    continue;
		        }
			    String[] words = CEs[i].split(partition);
			    for(int i1 = 0; i1 < words.length; ++i1){
				   ArrayList<String> splitWords = CommonTool.splitCamelCaseIdentifier(words[i1]);
				   Iterator<String> it = splitWords.iterator();
				   while(it.hasNext()) {
					   String word = it.next().toLowerCase();
					   if(!LongFormUtils.isJavaWord(word))
						   result += word + " ";
				   }
			   }
			}
	    } else {
	    	String[] words = str.split(partition);
	    	for (String word : words) {
	    		if (word.equals(""))
	    			continue;
	    		ArrayList<String> splitWords = CommonTool.splitCamelCaseIdentifier(word);
	    		Iterator<String> it = splitWords.iterator();
	    		while(it.hasNext()) {
	    			String iWord = it.next().toLowerCase();
	    			if (!LongFormUtils.isJavaWord(iWord))
	    				result += iWord + " ";
	    		}
	    	}
	    }
		return LongFormUtils.removeStopWords(result);
	}
	
	public CodeExtractElement getCodeExtractorElement() {
		return this.codeExtractElement;
	}
	
	
}
