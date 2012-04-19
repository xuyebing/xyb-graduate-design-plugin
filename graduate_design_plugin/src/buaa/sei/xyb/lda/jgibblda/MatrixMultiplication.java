package buaa.sei.xyb.lda.jgibblda;

/**
 * ���������˵���
 * @author Xu Yebing
 *
 */
public class MatrixMultiplication {

	/**
	 * @param matrixA : rowLen �� innerLen �ľ���
	 * @param matrixB : innerLen �� culLen �ľ���
	 * @param rowLen
	 * @param culLen
	 * @param innerLen
	 * @return rowLen �� culLen �ľ���
	 */
	public static double[][] matrixMultiple(double[][] matrixA, double[][] matrixB,
			                                int rowLen, int culLen, int innerLen) {
		double[][] mulMatrix = new double[rowLen][culLen];
		for (int i = 0; i < rowLen; i++) {
			for (int k = 0; k < culLen; k++) {
				mulMatrix[i][k] = 0.0;
				for (int j = 0; j < innerLen; j++) {
					mulMatrix[i][k] += matrixA[i][j] * matrixB[j][k];
				}
			}
		}
		return mulMatrix;
	}
	
//	public static void main(String[] args) {
//		double[][] a = new double[][] {
//				{1, 2},
//				{3, 4}
//		};
//		double[][] b = new double[][] {
//				{5, 6},
//				{7, 8}
//		};
//		double[][] result = MatrixMultiplication.matrixMultiple(a, b, 2, 2, 2);
//		for (int i = 0; i < 2; i++) {
//			for (int j = 0; j < 2; j++) {
//				System.out.print(result[i][j] + "\t");
//			}
//			System.out.println();
//		}
//	}
}
