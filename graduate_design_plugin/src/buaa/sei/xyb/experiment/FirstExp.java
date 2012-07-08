package buaa.sei.xyb.experiment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import buaa.sei.xyb.common.Constant;
import buaa.sei.xyb.lda.jgibblda.LDA;

/**
 * ��һ��ʵ�飬���ڵõ����ŵ�������Ŀ
 * @author Xu Yebing
 */
public class FirstExp {
	public static final String matrixFileName = "inputMatrix.txt"; // �����ļ���
	public static final String matrixFilePath = "D:\\exp1";
	public static final String bMatrixFileName = "model-final.phi"; // ����-�ʻ�ֲ�����
	private static Vector<double[]> bMatrix = new Vector<double[]>();
	private static double[] arrayMo = null; // ����bMatrix��ÿ����������ģ����������ƽ������͡������ţ�
	private static int K = 0;
	public static void runSample() {
		double alpha = 50.0/(double)K;
		String argStr = "-est -alpha " + alpha +
	            " -beta " + 0.01 +
	            " -ntopics " + K +
	            " -niters " + 500 +
	            " -savestep " + 100 +
	            " -twords " + 20 +
                " -dir " + matrixFilePath +
                " -dfile " + matrixFileName;
		String[] newArgs = argStr.split(" ");
		LDA.main(newArgs);
	}
	public static double compute() {
		double avg_cosine = 0;
		double sumCosine = 0;
		buildMatrix();
		int rowNo = bMatrix.size(); // ���������
		computeMo(rowNo);
		for (int i = 0; i < rowNo-1; i++) {
			double[] vecA = bMatrix.get(i);
			for (int j = i+1; j < rowNo; j++) {
				double[] vecB = bMatrix.get(j);
				sumCosine += computeConsine(i, vecA, j, vecB);
			}
		}
		double fenMu = K*(K - 1)/2;
		assert(fenMu > 0);
		avg_cosine = sumCosine / fenMu;
		return avg_cosine;
	}
	// �������������ļн�����
	// ���У�����rowA��rowB���vecA��vecB��������ģ��
	private static double computeConsine(int rowA, double[] vecA, int rowB, double[] vecB) {
		double cosine = 0;
		double moA = arrayMo[rowA]; // vecA��ģ��
		double moB = arrayMo[rowB]; // vecB��ģ��
		int len = vecA.length;
		assert(vecA.length == vecB.length);
		
		double fenZi = 0; // ����
		for (int i = 0; i < len; i++) {
			fenZi += vecA[i] * vecB[i];
		}
		double fenMu = moA * moB; // ��ĸ
		assert(fenMu > 0);
		cosine = fenZi / fenMu;
		return cosine;
	}
	// �������bMatrix��ÿһ����������ģ��
	private static void computeMo(int rowNo) { // rowNo��bMatrix��size
		if (arrayMo == null)
			arrayMo = new double[rowNo];
		for (int j = 0; j < rowNo; j++) {
			double[] vecOne = bMatrix.get(j); // �õ�һ��������
			double sum = 0;
			int len = vecOne.length;
			for (int i = 0; i < len; i++) {
				sum += vecOne[i]*vecOne[i];
			}
			arrayMo[j] = Math.sqrt(sum);
		}
	}
	
	// ���ļ��й�������
	private static void buildMatrix() {
		String inputFilePath = matrixFilePath + Constant.FILE_SEPARATOR + bMatrixFileName;
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] valueStr = line.split("\\s+");
				int len = valueStr.length;
				double[] value = new double[len];
				for (int i = 0; i < len; i++) {
					value[i] = Double.valueOf(valueStr[i]);
				}
				bMatrix.add(value);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		FirstExp.K = 250;
		FirstExp.runSample();
		double avg_cosine = FirstExp.compute();
		System.out.println("������ = " + FirstExp.K + ", avg_cosine = " + avg_cosine);
	}
}
