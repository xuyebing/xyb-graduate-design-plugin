package buaa.sei.xyb.common;

/**
 * @author Xu Yebing
 * �ĵ��������࣬������һ���ĵ������ĵ��λ����Σ���������Ϣ���磺���ID���ļ���������ֲ���Ϣ���ִʺ�Ĵ��Ｏ�ϵȣ�
 */
public class DocumentDescriptor {

	// Ψһȷ��һ���ĵ���������categoryID + ID
//	private int ID = -1; // �ĵ��������е����
	private int matrixIndex = -1; // �ĵ�����������-�ĵ�����������ţ�Ҳ����˵���кţ���������LDA������Ϻ��һص���Ӧ���ĵ�
								  // matrixIndex�����ɾ���ʱ�ű���ֵ
	private int categoryID = -1; // �ĵ�����������磺������ơ�����ȣ���
	                             // ��������ĵ���˵��һ��ԭʼ�ļ���Ӧһ�������ţ��������������˵�����д����ļ�������һ�����
	private String name; // �ĵ�����
	private String path; // �ĵ�·��(�����ĵ���name)
	
	public DocumentDescriptor () {
	}
	public DocumentDescriptor (int categoryID, String name, String path) {
		this.categoryID = categoryID;
		this.name = name;
		this.path = path;
	}
	
	public void setMatrixIndex (int matrixIndex) {
		this.matrixIndex = matrixIndex;
	}
	public void setCategoryID (int categoryID) {
		this.categoryID = categoryID;
	}
	public void setName (String name) {
		this.name = name;
	}
	public void setPath (String path) {
		this.path = path;
	}
	public int getMatrixIndex () {
		return this.matrixIndex;
	}
	public int getCategoryID () {
		return this.categoryID;
	}
	public String getName () {
		return this.name;
	}
	public String getPath () {
		return this.path;
	}
}
