package buaa.sei.xyb.process.irmodel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import buaa.sei.xyb.analyse.modelcontrol.BuildModel;
import buaa.sei.xyb.common.Constant;
import buaa.sei.xyb.common.DocumentDescriptor;
import buaa.sei.xyb.common.GlobalVariant;
import buaa.sei.xyb.lda.jgibblda.Pair;

/**
 * 自己动手实现VSM模型，直接计算夹角余弦
 */
public class VSMProcess {
	
	private static String resultFolder = Constant.workingFolder;
	/**
	 * categoryMatrixIds  保存形式如下：<文件类别序号, 该类所有文本段在矩阵中的序号>
	 * 例如: <代码3, <矩阵中第9行, 矩阵中第10行, 矩阵中第11行>>
	 */
	private HashMap<Integer, ArrayList<Integer>> categoryMatrixIds = null; // 保存每个类
	
	private String inputFileName = BuildModel.matrixShannonInfo; // 输入矩阵的文件名,默认值="shannonInfo.txt"
	private String outputFileNamePrefix = "result_"; // 输出结果文件的前缀，如："result_"(默认值), "lsiresult_"
	
	public VSMProcess () {
	}
	public VSMProcess (String inputFileName, String outputFileNamePrefix) {
		this.inputFileName = inputFileName;
		this.outputFileNamePrefix = outputFileNamePrefix;
	}
	
	// 直接使用shannonInfo.txt文件，它已经计算夹角余弦所需的矩阵
	// 1. 遍历文档+代码的全体文件描述符
	public void init() {
		// 检查保存相似度计算结果的文件夹是否存在，不存在则创建
		if (resultFolder == null)
			return;
		File resultDir = new File(resultFolder);
		if (!resultDir.exists() || !resultDir.isDirectory()) {
			resultDir.mkdirs();
		}
		if (!resultFolder.endsWith(Constant.FILE_SEPARATOR))
			resultFolder = resultFolder + Constant.FILE_SEPARATOR;
		//
		categoryMatrixIds = new HashMap<Integer, ArrayList<Integer>>();
		for(Iterator<DocumentDescriptor> iterator =  GlobalVariant.docDescriptorList.iterator(); 
				iterator.hasNext(); ) {
			DocumentDescriptor dd = iterator.next();
			int category = dd.getCategoryID();
			int matrixIndex = dd.getMatrixIndex();
			if (categoryMatrixIds.containsKey(category)) {
				ArrayList<Integer> matrixIds = categoryMatrixIds.get(category);
				matrixIds.add(matrixIndex);
				categoryMatrixIds.put(category, matrixIds);
			} else {
				ArrayList<Integer> matrixIds = new ArrayList<Integer>();
				matrixIds.add(matrixIndex);
				categoryMatrixIds.put(category, matrixIds);
			}
		}
	}
	// 计算各类文档段之间的相似度
	public void compute() {
		int categoryNo = categoryMatrixIds.size();
		for (int i = 1; i <= categoryNo-1; i++) {
			ArrayList<Integer> docA = categoryMatrixIds.get(i);
			int lenA = docA.size();
			for (int j = i+1; j <= categoryNo; j++) {
				// 计算i,j两类文档段之间的相似度
				ArrayList<Integer> docB = categoryMatrixIds.get(j);
				// 创建保存输出结果的文件，文件名格式如下:result_i-j.log
				String resFile = this.outputFileNamePrefix+i+"-"+j+".log";
				String outputPath = resultFolder + resFile;
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath));
					
					int lenB = docB.size();
					for (int k = 0; k < lenA; k++) {
						int matrixIdA = docA.get(k);
						double[] vecA = getVectorWithMatrixId(matrixIdA);
						List<Pair> outputList = new ArrayList<Pair>();
						for (int m = 0; m < lenB; m++) {
							int matrixIdB = docB.get(m);
							double[] vecB = getVectorWithMatrixId(matrixIdB);
							try {
								double retCosine = calculateCosine(vecA, vecB);
								String outputLine = getDocNameWithMatrixId(matrixIdA) + "\t" 
										+ getDocNameWithMatrixId(matrixIdB) + "\t" + retCosine;
								outputList.add(new Pair(outputLine, retCosine));
//								bw.write(outputLine + "\n");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						// 将结果排序后写入result文件
						System.out.println("\"%%%%%%%  开始排序 " + resFile + " 文件  %%%%%%%\"");
						Collections.sort(outputList);
						System.out.println("\"%%%%%%%  " + resFile + " 文件 排序结束  %%%%%%%\"");
						for (int h = 0; h < outputList.size(); h++) {
							bw.write(outputList.get(h).first.toString() + "\n");
							System.out.println("\"------> 写入 " + outputList.get(h).first.toString() + " \"");
						}
					}
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 根据文档段在矩阵中的行号matrixId，获得shannonInfo.txt中保存的该文档的向量
	 * @param matrixId
	 * @return 文档段向量(每一维的值为香农信息值)
	 */
	private double[] getVectorWithMatrixId(int matrixId) {
		int readId = -1;
		String shannonInfoFile = BuildModel.matrixFilePath + Constant.FILE_SEPARATOR + this.inputFileName;
		try {
			BufferedReader br = new BufferedReader(new FileReader(shannonInfoFile));
			String line = "";
			do {
				readId++;
				line = br.readLine();
			} while(readId < matrixId && line != null);
			if (line == null)
				return null;
			else {
				String[] valueStrs = line.split("\\s+");
				int len = valueStrs.length;
				double[] value = new double[len];
				for (int i = 0; i < len; i++) {
					value[i] = Double.valueOf(valueStrs[i]);
				}
				return value;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 计算两个向量的夹角余弦
	 * @param x 向量x
	 * @param y 向量y
	 * @return 夹角余弦值
	 * @throws Exception 两个向量长度不等
	 */
	private double calculateCosine(double[] x, double[] y) throws Exception {
		double sumX2 = 0, sumY2 = 0, sumXY = 0;
		double denominator = 0; // 分母
		
		if (x.length != y.length)
			throw new Exception("ERROR: VSM相似度计算中，两向量长度不等!");
		int len = x.length;
		for (int i = 0; i < len; i++) {
			sumX2 += Math.pow(x[i], 2);
			sumY2 += Math.pow(y[i], 2);
			sumXY += x[i] * y[i];
		}
		denominator = Math.sqrt(sumX2 * sumY2);
		if (denominator > 0)
			return (sumXY / denominator);
		else
			return 0;
	}
	/**
	 * 获得矩阵行号所对应的文档段的名称
	 * @param matrixId
	 * @return
	 */
	private String getDocNameWithMatrixId(int matrixId) throws Exception {
		String docName = "";
		if (matrixId > GlobalVariant.docDescriptorList.size())
			throw new Exception("ERROR: 矩阵行号错误, 无法获得对应的文档段的名称!");
		for (Iterator<DocumentDescriptor> iterator = GlobalVariant.docDescriptorList.iterator(); 
				iterator.hasNext(); ) {
			DocumentDescriptor dd = iterator.next();
			if (dd.getMatrixIndex() == matrixId) {
				docName = dd.getName();
				break;
			}
		}
		return docName;
	}
}
