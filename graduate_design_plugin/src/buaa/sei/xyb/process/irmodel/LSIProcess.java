package buaa.sei.xyb.process.irmodel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import sei.buaa.IR.matrix.BasicMatrix;
import sei.buaa.IR.matrix.IMatrix;
import sei.buaa.IR.matrix.exception.IncorrectDimensionException;
import sei.buaa.IR.matrix.exception.InvalidIndexException;
import Jama.Matrix;
import Jama.SingularValueDecomposition;
import buaa.sei.xyb.analyse.modelcontrol.BuildModel;
import buaa.sei.xyb.common.Constant;

/**
 * LSIProcess 从师兄实现版本中截取出的LSI处理过程，用于对LDA的输出进行LSI分析
 * @author Xu Yebing
 *
 */
public class LSIProcess {
	/* 在plugin.xml 的 Dependencies 页面的 Required Plug-ins下加入了对sei.buaa.linktracer.IR的引用*/

	private double[][] matrixArray = null;
	private Matrix matrix = null;
	public static double threshold = 0.99; // 默认的LSI 降维阈值
	private String matrixLSI = BuildModel.matrixFilePath + Constant.FILE_SEPARATOR + BuildModel.matrixLSI; // 默认值
	private String outputName = BuildModel.matrixFilePath + Constant.FILE_SEPARATOR + Constant.LSI_OUTPUT_MATRIX_FILENAME;
	private boolean transposeFlag = false; // 表明是否需要对矩阵进行转置（当输入矩阵的row数 < col数时，需要转置，才能进行SVD运算）
	
	public LSIProcess() { // 使用matrixLSI的默认值
	}
	
	public LSIProcess(String matrixLSIFileName, String outputName) {
		this.matrixLSI = BuildModel.matrixFilePath + Constant.FILE_SEPARATOR + matrixLSIFileName;
		this.outputName =  BuildModel.matrixFilePath + Constant.FILE_SEPARATOR + outputName;
	}
	/**
	 */
	public void initMatrix() {
		int row1 = 0;
		int col1 = 0;
		try {
			// 先不用rows, cols 而是先扫描一遍文件，获得行数和列数
			BufferedReader br = new BufferedReader(new FileReader(matrixLSI));
			int row = 0;
			String line = "";
			while ((line = br.readLine()) != null) {
				if (col1 == 0) {
					String[] values = line.split("\\s+");
					col1 = values.length;
				}
				row++;
			}
			row1 = row;
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (matrixArray == null) {
//			matrixArray = new double[rows][cols];
			matrixArray = new double[row1][col1];
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(matrixLSI));
			int row = 0;
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] values = line.split("\\s+");
				int colSum = values.length;
				for (int col = 0; col < colSum; col++) {
					matrixArray[row][col] = Double.valueOf(values[col]);
				}
				row++;
			}
			matrix = new Matrix(this.matrixArray); // 构造矩阵
			if (row1 < col1) { //  Jama中SingularValueDecomposition的使用条件是:行数  >= 列数
				// 当行数小于列数时，先转置
				this.transposeFlag = true;
				matrix = matrix.transpose();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("restriction")
	public IMatrix beginSVD() {
		SingularValueDecomposition svd = new SingularValueDecomposition(this.matrix);
		int reducedSpaceDimension = svd.rank();
		double[] s = svd.getSingularValues();
		int rank = getReducedSpaceDimension(s, this.threshold);
		
		if (rank < reducedSpaceDimension) {
			reducedSpaceDimension = rank;
		}
		
		IMatrix reducedMatrix = null;
		
		try {
			// conceptMatrix 由矩阵的奇异值组成的矩阵（对角线矩阵）
			IMatrix conceptMatrix = new BasicMatrix(svd.getS().getRowDimension(), svd.getS().getColumnDimension(), svd.getS().getArrayCopy());
			IMatrix reducedConceptMatrix = conceptMatrix.subMatrix(reducedSpaceDimension, reducedSpaceDimension);
			
//			// 左奇异矩阵
//			IMatrix leftMatrix = new BasicMatrix(svd.getU().getRowDimension(), svd.getU().getColumnDimension(), svd.getU().getArrayCopy());
//			IMatrix reducedLeftMatrix = leftMatrix.subMatrix(leftMatrix.getRowCount(), reducedSpaceDimension);
			
			// 右奇异矩阵
			IMatrix rightMatrix = new BasicMatrix(svd.getV().getRowDimension(), svd.getV().getColumnDimension(), svd.getV().getArrayCopy());
			IMatrix reducedRightMatrix = rightMatrix.subMatrix(rightMatrix.getRowCount(), reducedSpaceDimension);
			
//			reducedMatrix = reducedLeftMatrix.mul(reducedConceptMatrix);
//			reducedMatrix = reducedMatrix.mul(reducedRightMatrix.transpose());
			reducedMatrix = reducedRightMatrix.mul(reducedConceptMatrix).transpose();
		} catch (InvalidIndexException e) {
			e.printStackTrace();
		} catch (IncorrectDimensionException e) {
			e.printStackTrace();
		}
		if (transposeFlag)
			reducedMatrix = reducedMatrix.transpose();
		
		return reducedMatrix;
	}
	private int getReducedSpaceDimension(double[] s, double threshold) {
		double sum = 0.0, sum2 = 0.0;
		int k = 0;
		int len = s.length;
		for (int i = 0; i < len; i++) {
			sum += s[i];
		}
		for (int i = 0; i < len; i++) {
			sum2 += s[i];
			if (sum2/sum > threshold) {
				k = i;
				break;
			}
		}
		return k;
	}
	
	// 供外部启动LSI分析过程
	@SuppressWarnings("restriction")
	public void triggerLSIAnalysis(){
		IMatrix matrix = this.beginSVD();
		
		// outputName = “matrix/LSI-output.txt”
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputName));
			int row = matrix.getRowCount();
			int col = matrix.getColumnCount();
			double[][] value = matrix.toArray();
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < col; j++) {
					if (j == col-1)
						bw.write(String.valueOf(value[i][j]));
					else
						bw.write(value[i][j] + " ");
				}
				bw.write("\r\n");
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("======>> LSI 分析结束，输出文件 = " + outputName);
		System.out.println(" 矩阵 行数 = " + matrix.getRowCount() + " , 列数 = " + matrix.getColumnCount());
	}
	
	@SuppressWarnings("restriction")
	public static void main(String[] args) {
//		String fileName = "D:\\LSI_matrix.txt";
		String fileName = "D:\\ttmp_1\\matrix\\shannonInfo.txt";
		String outputName = "D:\\ttmp_1\\LSI-output.txt";
		LSIProcess lsiProc = new LSIProcess(fileName, outputName);
		lsiProc.initMatrix();
		
		IMatrix matrix = lsiProc.beginSVD();
//		double[][] values = new double[][]{
//				{0.4971, -0.033, 0.0232, 0.4867, -0.0069},
//				{0.6003, 0.0094, 0.9933, 0.3858, 0.7091},
//				{0.4971, -0.03, 0.0232, 0.4867, -0.0069},
//				{-0.0436, 0.9866, 0.0094, 0.4402, 0.7043},
//				{0.1801, 0.074, -0.0522, 0.232, 0.0155},
//				{0.1801, 0.074, -0.0522, 0.232, 0.0155}};
//		for(int i = 0; i < values.length; ++i) {
//			for(int j = 0; j < values[i].length; ++j) {
//				try {
//					assertEquals("(i, j) :" + "(" + (i+1) + "," + (j+1) + ")",
//							values[i][j], matrix.elementAt(i+1, j+1), 0.02);
//				} catch (InvalidIndexException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputName));
			int row = matrix.getRowCount();
			int col = matrix.getColumnCount();
			double[][] value = matrix.toArray();
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < col; j++) {
					if (j == col-1)
						bw.write(String.valueOf(value[i][j]));
					else
						bw.write(value[i][j] + " ");
				}
				bw.write("\r\n");
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(" 矩阵 行数 = " + matrix.getRowCount() + " , 列数 = " + matrix.getColumnCount());
	}
}
