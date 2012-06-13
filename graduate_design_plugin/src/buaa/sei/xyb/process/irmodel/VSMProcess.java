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
 * �Լ�����ʵ��VSMģ�ͣ�ֱ�Ӽ���н�����
 */
public class VSMProcess {
	
	private static String resultFolder = Constant.workingFolder;
	/**
	 * categoryMatrixIds  ������ʽ���£�<�ļ�������, ���������ı����ھ����е����>
	 * ����: <����3, <�����е�9��, �����е�10��, �����е�11��>>
	 */
	private HashMap<Integer, ArrayList<Integer>> categoryMatrixIds = null; // ����ÿ����
	
	private String inputFileName = BuildModel.matrixShannonInfo; // ���������ļ���,Ĭ��ֵ="shannonInfo.txt"
	private String outputFileNamePrefix = "result_"; // �������ļ���ǰ׺���磺"result_"(Ĭ��ֵ), "lsiresult_"
	
	public VSMProcess () {
	}
	public VSMProcess (String inputFileName, String outputFileNamePrefix) {
		this.inputFileName = inputFileName;
		this.outputFileNamePrefix = outputFileNamePrefix;
	}
	
	// ֱ��ʹ��shannonInfo.txt�ļ������Ѿ�����н���������ľ���
	// 1. �����ĵ�+�����ȫ���ļ�������
	public void init() {
		// ��鱣�����ƶȼ��������ļ����Ƿ���ڣ��������򴴽�
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
	// ��������ĵ���֮������ƶ�
	public void compute() {
		int categoryNo = categoryMatrixIds.size();
		for (int i = 1; i <= categoryNo-1; i++) {
			ArrayList<Integer> docA = categoryMatrixIds.get(i);
			int lenA = docA.size();
			for (int j = i+1; j <= categoryNo; j++) {
				// ����i,j�����ĵ���֮������ƶ�
				ArrayList<Integer> docB = categoryMatrixIds.get(j);
				// �����������������ļ����ļ�����ʽ����:result_i-j.log
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
						// ����������д��result�ļ�
						System.out.println("\"%%%%%%%  ��ʼ���� " + resFile + " �ļ�  %%%%%%%\"");
						Collections.sort(outputList);
						System.out.println("\"%%%%%%%  " + resFile + " �ļ� �������  %%%%%%%\"");
						for (int h = 0; h < outputList.size(); h++) {
							bw.write(outputList.get(h).first.toString() + "\n");
							System.out.println("\"------> д�� " + outputList.get(h).first.toString() + " \"");
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
	 * �����ĵ����ھ����е��к�matrixId�����shannonInfo.txt�б���ĸ��ĵ�������
	 * @param matrixId
	 * @return �ĵ�������(ÿһά��ֵΪ��ũ��Ϣֵ)
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
	 * �������������ļн�����
	 * @param x ����x
	 * @param y ����y
	 * @return �н�����ֵ
	 * @throws Exception �����������Ȳ���
	 */
	private double calculateCosine(double[] x, double[] y) throws Exception {
		double sumX2 = 0, sumY2 = 0, sumXY = 0;
		double denominator = 0; // ��ĸ
		
		if (x.length != y.length)
			throw new Exception("ERROR: VSM���ƶȼ����У����������Ȳ���!");
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
	 * ��þ����к�����Ӧ���ĵ��ε�����
	 * @param matrixId
	 * @return
	 */
	private String getDocNameWithMatrixId(int matrixId) throws Exception {
		String docName = "";
		if (matrixId > GlobalVariant.docDescriptorList.size())
			throw new Exception("ERROR: �����кŴ���, �޷���ö�Ӧ���ĵ��ε�����!");
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
