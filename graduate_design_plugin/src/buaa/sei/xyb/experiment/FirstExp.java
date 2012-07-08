package buaa.sei.xyb.experiment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import buaa.sei.xyb.common.Constant;
import buaa.sei.xyb.lda.jgibblda.LDA;

/**
 * 第一个实验，用于得到最优的主题数目
 * @author Xu Yebing
 */
public class FirstExp {
	public static final String matrixFileName = "inputMatrix.txt"; // 矩阵文件名
	public static final String matrixFilePath = "D:\\exp1";
	public static final String bMatrixFileName = "model-final.phi"; // 主题-词汇分布矩阵
	private static Vector<double[]> bMatrix = new Vector<double[]>();
	private static double[] arrayMo = null; // 保存bMatrix中每个行向量的模长（各分量平方、求和、开根号）
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
		int rowNo = bMatrix.size(); // 矩阵的行数
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
	// 计算两个向量的夹角余弦
	// 其中，根据rowA和rowB获得vecA和vecB两向量的模长
	private static double computeConsine(int rowA, double[] vecA, int rowB, double[] vecB) {
		double cosine = 0;
		double moA = arrayMo[rowA]; // vecA的模长
		double moB = arrayMo[rowB]; // vecB的模长
		int len = vecA.length;
		assert(vecA.length == vecB.length);
		
		double fenZi = 0; // 分子
		for (int i = 0; i < len; i++) {
			fenZi += vecA[i] * vecB[i];
		}
		double fenMu = moA * moB; // 分母
		assert(fenMu > 0);
		cosine = fenZi / fenMu;
		return cosine;
	}
	// 计算矩阵bMatrix中每一个行向量的模长
	private static void computeMo(int rowNo) { // rowNo是bMatrix的size
		if (arrayMo == null)
			arrayMo = new double[rowNo];
		for (int j = 0; j < rowNo; j++) {
			double[] vecOne = bMatrix.get(j); // 得到一个行向量
			double sum = 0;
			int len = vecOne.length;
			for (int i = 0; i < len; i++) {
				sum += vecOne[i]*vecOne[i];
			}
			arrayMo[j] = Math.sqrt(sum);
		}
	}
	
	// 从文件中构件矩阵
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
		System.out.println("主题数 = " + FirstExp.K + ", avg_cosine = " + avg_cosine);
	}
}
