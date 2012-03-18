package buaa.sei.xyb.analyse.code;

import java.util.LinkedList;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import buaa.sei.xyb.common.DocumentDescriptor;

public class JavaCodeParser {

	private String projectDir;
	private String className;
	private DocumentDescriptor docDescriptor;
	private CodeExtractElement codeExtractElement;
	
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
	}
	
	private void getJavaChildren(IType javaClass) throws JavaModelException {
		LinkedList<String> fieldList = new LinkedList<String>();
		LinkedList<String> methodList = new LinkedList<String>();
		IField[] fields = javaClass.getFields();
		IMethod[] methods = javaClass.getMethods();
		for (IField field : fields) {
			fieldList.add(field.getElementName()); // 获得所有的属性名
		}
		for (IMethod method : methods) {
			methodList.add(method.getElementName()); // 获得所有的方法名
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
		}
	}
}
