package buaa.sei.xyb.analyse.code;

import java.io.IOException;
import java.util.LinkedList;

import buaa.sei.xyb.common.dict.SegTran;

public class CodeExtractElement {

	private LinkedList<String> fieldsName; // ����������
	private LinkedList<String> methodsName; // ����������
	private String otherComments = "";
	private String classComments = "";
	private String methodComments = "";
	private String body = ""; // ������˫�����е�����
	
	public void setFieldsName(LinkedList<String> fieldsName) {
		this.fieldsName = fieldsName;
	}
	public void setMethodsName(LinkedList<String> methodsName) {
		this.methodsName = methodsName;
	}
	
	public LinkedList<String> getFieldsName() {
		return this.fieldsName;
	}
	public LinkedList<String> getMethodsName() {
		return this.methodsName;
	}
	public void setOtherComments(String otherComments) {
		this.otherComments = otherComments;
	}
	public String getOtherComments() { 
		return this.otherComments;
	}
	public void setClassComments(String classComments) {
		this.classComments = classComments;
	}
	public String getClassComments() { 
		return this.classComments;
	}
	public void setMethodComments(String methodComments) {
		this.methodComments = methodComments;
	}
	public String getMethodComments() { 
		return this.methodComments;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getBody() { 
		return this.body;
	}
}
