package buaa.sei.xyb.analyse.code;

import java.util.LinkedList;

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
	
	/**
	 * ��String����ȡterm
	 * @param content ��������String
	 */
	1. �޸�UI �е� Data Dict����������ѡ���ļ����������ļ���
	2. �����������ݴʵ�ʹʵ����Ӣ�ĵ����ĵķ���
//	private void getTermsFromString(String content) {
//		if(!content.equals("")) {
//			try {
//				content=SegTran.ChiEng2Chi(content);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		if(!content.equals(""))
//		{
//		    String[] words = content.split(" ");
//			for(int i = 0; i < words.length; ++i) {
//				//if(words[i].length() > 2) {
//					if(termMap.containsKey(words[i])) {
//						Term term = termMap.get(words[i]);
//						if(term.getType() < type) {
//							log.info("word: " + term.getTermName() + ". type: " + type);
//							term.setType(type);
//						}
//						term.setLocalFreq(term.getLocalFreq() + 1);
//					} else {
//						Term term = new Term(words[i]);
//						term.setType(type);
//						termMap.put(words[i], term);
//					}
//				//}
//			}
//		}
//	}
}
