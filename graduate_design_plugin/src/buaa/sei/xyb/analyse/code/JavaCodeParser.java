package buaa.sei.xyb.analyse.code;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jeasy.analysis.MMAnalyzer;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import buaa.sei.xyb.analyse.code.util.LongFormUtils;
import buaa.sei.xyb.analyse.code.util.SourceProcessor;
import buaa.sei.xyb.common.CommonTool;
import buaa.sei.xyb.common.DocumentDescriptor;

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
	
	public void setProjectDir(String projectDir) {
		this.projectDir = projectDir;
	}
	public DocumentDescriptor analyze(IType element) throws JavaModelException {
		int index = element.getPath().toOSString().indexOf("\\", 1);
		String location = element.getPath().toOSString().substring(index);
		String javaFileName = projectDir + location;
		className = element.getElementName();
		System.out.println("==>> Parse the source code: " + javaFileName + ", class name: " + className);
		
//		docDescriptor = new DocumentDescriptor(Constant.globalCategoryID, className, javaFileName);
		parseClass(element);
		
		
		return new DocumentDescriptor();
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
		System.out.println("====>> otherComments : " + otherComments);
		System.out.println("====>> classComments : " + classComments);
		System.out.println("====>> methodComments : " + methodComments);
		System.out.println("====>> body : " + body);
		codeExtractElement.setOtherComments(otherComments.trim());
		codeExtractElement.setClassComments(classComments.trim());
		codeExtractElement.setMethodComments(methodComments.trim());
		codeExtractElement.setBody(body);
	}
	
	private void getJavaChildren(IType javaClass) throws JavaModelException {
		LinkedList<String> fieldList = new LinkedList<String>();
		LinkedList<String> methodList = new LinkedList<String>();
		IField[] fields = javaClass.getFields();
		IMethod[] methods = javaClass.getMethods();
		System.out.println("--------------> Fields:");
		for (IField field : fields) {
			fieldList.add(field.getElementName()); // 获得所有的属性名
			System.out.println("\t\t" + field);
		}
		System.out.println("--------------> Methods:");
		for (IMethod method : methods) {
			methodList.add(method.getElementName()); // 获得所有的方法名
			System.out.println("\t\t" + method);
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

		//otherComments += methodParser.getInternalCommentsClean();
		//otherComments += methodParser.getStringLiteralsCleanNoStop();
		otherComments += methodParser.getInternalComments();
		otherComments += methodParser.getStringLiterals();

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
		fieldBodyClean += fieldBody.replaceAll(regex, " ");

		// 匹配以"//"形式编写的注释
		regex = "//.*";
		matcher = Pattern.compile(regex).matcher(fieldBody);
		while(matcher.find()) {
			comment += matcher.group() + " ";
		}

		// 匹配双引号里面的内容
		fieldBodyClean = fieldBodyClean.replaceAll(regex, " ");

		body += splitCamelCaseIdentifier(fieldBodyClean) + " ";
		
		otherComments += splitCamelCaseIdentifier(comment) + " ";
        body += fieldBodyClean + " ";
		
		otherComments += comment + " ";
	}
	
	private String splitCamelCaseIdentifier(String str)  {
		//added by han 添加提取中文操作，因为原来的没有处理中文的功能，把中文过滤掉了。
	    String result = "";
	    if (str.contains("[\\u4E00-\\u9FA5]")) { // 判断是包含了中文
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
