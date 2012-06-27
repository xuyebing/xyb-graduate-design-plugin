package buaa.sei.xyb.experiment;

/**
 * 最小堆的结点类型
 * @author Xu Yebing
 *
 */
public class HeapNode implements Comparable<HeapNode> {

	public String subDocName = null; // 文档段名称
	public String codeName = null; // 代码段名称
	public double rValue = 0.0; // 相关度
	
	public HeapNode(String docName, String codeName, double value) {
		this.subDocName = docName;
		this.codeName = codeName;
		this.rValue = value;
	}

	@Override
	public int compareTo(HeapNode hn1) {
		// TODO Auto-generated method stub
		// 降序排列
		if (this.rValue > hn1.rValue)
			return -1;
		else if (this.rValue == hn1.rValue)
			return 0;
		else
			return 1;
	}
}
