package buaa.sei.xyb.analyse.code;

import java.util.LinkedList;

public class CodeExtractElement {

	private LinkedList<String> fieldsName; // ����������
	private LinkedList<String> methodsName; // ����������
	
	public void setFieldsName(LinkedList<String> fieldsName) {
		this.fieldsName = fieldsName;
	}
	public void setMethodsName(LinkedList<String> methodsName) {
		this.methodsName = methodsName;
	}
}
