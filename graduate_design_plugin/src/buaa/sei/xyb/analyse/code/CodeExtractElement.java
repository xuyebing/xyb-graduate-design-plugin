package buaa.sei.xyb.analyse.code;

import java.util.LinkedList;

public class CodeExtractElement {

	private LinkedList<String> fieldsName; // ����������
	private LinkedList<String> methodsName; // ����������
	private LinkedList<String> comments; // ע�ͼ���
	
	public void setFieldsName(LinkedList<String> fieldsName) {
		this.fieldsName = fieldsName;
	}
	public void setMethodsName(LinkedList<String> methodsName) {
		this.methodsName = methodsName;
	}
	public void setComments(LinkedList<String> comments) {
		this.comments = comments;
	}
	
	public LinkedList<String> getFieldsName() {
		return this.fieldsName;
	}
	public LinkedList<String> getMethodsName() {
		return this.methodsName;
	}
	public LinkedList<String> getComments() {
		return this.comments;
	}
}
