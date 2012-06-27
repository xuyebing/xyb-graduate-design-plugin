package buaa.sei.xyb.experiment;

/**
 * ��С�ѵĽ������
 * @author Xu Yebing
 *
 */
public class HeapNode implements Comparable<HeapNode> {

	public String subDocName = null; // �ĵ�������
	public String codeName = null; // ���������
	public double rValue = 0.0; // ��ض�
	
	public HeapNode(String docName, String codeName, double value) {
		this.subDocName = docName;
		this.codeName = codeName;
		this.rValue = value;
	}

	@Override
	public int compareTo(HeapNode hn1) {
		// TODO Auto-generated method stub
		// ��������
		if (this.rValue > hn1.rValue)
			return -1;
		else if (this.rValue == hn1.rValue)
			return 0;
		else
			return 1;
	}
}
