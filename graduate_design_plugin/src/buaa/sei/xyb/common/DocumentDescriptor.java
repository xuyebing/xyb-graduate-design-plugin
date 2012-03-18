package buaa.sei.xyb.common;

/**
 * @author Xu Yebing
 * 文档描述符类，包含了一个文档对象（文档段或代码段）的所有信息（如：类别、ID、文件名、主题分布信息、分词后的词语集合等）
 */
public class DocumentDescriptor {

	// 唯一确定一个文档的索引：categoryID + ID
//	private int ID = -1; // 文档在其类中的序号
	private int matrixIndex = -1; // 文档在整个“词-文档”矩阵中序号（也可以说成行号），用于在LDA计算完毕后找回到对应的文档
								  // matrixIndex在生成矩阵时才被赋值
	private int categoryID = -1; // 文档所属的类别（如：需求、设计、代码等）。
	                             // 对于软件文档来说，一个原始文件对应一个类别序号；对于软件代码来说，所有代码文件都属于一个类别
	private String name; // 文档名称
	private String path; // 文档路径(包含文档名name)
	
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
