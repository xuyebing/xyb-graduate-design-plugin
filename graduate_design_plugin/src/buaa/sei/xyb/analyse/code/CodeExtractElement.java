package buaa.sei.xyb.analyse.code;

import java.util.LinkedList;

public class CodeExtractElement {

	private LinkedList<String> fieldsName; // 属性名集合
	private LinkedList<String> methodsName; // 方法名集合
	
	public void setFieldsName(LinkedList<String> fieldsName) {
		this.fieldsName = fieldsName;
	}
	public void setMethodsName(LinkedList<String> methodsName) {
		this.methodsName = methodsName;
	}
}
