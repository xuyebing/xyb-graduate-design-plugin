package buaa.sei.xyb.experiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * ���ڼ����ȫ�ʺͲ�׼��
 * @author Xu Yebing
 */
public class CallPrecision {
	private HashSet<String> correctLinkSet = null; // ����������ȷ�Ĺ�����
	private HashSet<String> codeNameSet = null; // �����ע�Ĵ����ļ�������
	private HashSet<String> docNameSet = null; // �����ע���ĵ��ε�����
	private HashMap<String, ArrayList<HeapNode>> actualLinkMap = null; // ����ʵ�ʵĹ�����,key=codeName, value=��codeName��صĽ������еĹ�����
	private int maxCutPoint = 6; // Cut-Point�������ֵ
	private int[] correctLinkNumS = null; // ����ÿ��Cut-Point��ֵ���ҵ�����ȷ������
	private int[] sumNumS = null; // ����ÿ��Cut-Point��ֵ���ҵ�����������
	private int sum_cor_num = 0; // ȫ���Ĺ���������
	
	public CallPrecision (int maxCutPoint, int sumCorNum) {
		correctLinkSet = new HashSet<String>();
		codeNameSet = new HashSet<String>();
		docNameSet = new HashSet<String>();
		actualLinkMap = new HashMap<String, ArrayList<HeapNode>>();
		this.maxCutPoint = maxCutPoint;
		correctLinkNumS = new int[maxCutPoint];
		sumNumS = new int[maxCutPoint];
		sum_cor_num = sumCorNum;
	}

	// ��ȡ������ȷ�Ĺ��������ڴ���
	private void readCorrectLinkSet(String fileName) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] values = line.trim().split("\\s+");
				assert(values.length == 2);
				correctLinkSet.add(values[0] + "\t" + values[1]);
				codeNameSet.add(values[0]);
				docNameSet.add(values[1]);
			}
			br.close();
			System.out.println(">>> �������Ŀ: " + codeNameSet.size());
			System.out.println(">>> �ĵ������룺" + docNameSet.size());
			BufferedWriter bw = new BufferedWriter(new FileWriter("D:\\exp2\\7_7.txt"));
			for (String sDocName : docNameSet) {
				bw.write(sDocName + "\n");
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// ��inputDir�ж�ȡʵ�ʵĹ���������ȡ��codeNameSet�а����Ĵ����ļ������й�����,������actualLinkMap��
	private void readActualLinkMap(String inputDir) {
		try {
			File inputDirFile = new File(inputDir);
			if (inputDirFile.exists() && inputDirFile.isDirectory()) {
				File[] files = inputDirFile.listFiles();
				for (File file : files) {
					BufferedReader br = new BufferedReader(new FileReader(file));
					String line = "";
					while ((line = br.readLine()) != null) {
						String[] tmp = line.trim().split("\\s+");
						assert (tmp.length == 3);
						if (codeNameSet.contains(tmp[1])) { // codeName������codeNameSet��, �����ǹ�ע�Ĵ����
							HeapNode hn = new HeapNode(tmp[0], tmp[1], Double.valueOf(tmp[2]));
							if (actualLinkMap.containsKey(tmp[1])) {
								ArrayList<HeapNode> value = actualLinkMap.get(tmp[1]);
								value.add(hn);
								actualLinkMap.put(tmp[1], value);
							} else {
								ArrayList<HeapNode> value = new ArrayList<HeapNode> ();
								value.add(hn);
								actualLinkMap.put(tmp[1], value);
							}
						}
					}
					br.close();
				}
				// �����еĹ�������������
				Set<String> keySet = actualLinkMap.keySet();
				for (Iterator<String> it = keySet.iterator(); it.hasNext(); ) {
					String codeName = it.next();
					ArrayList<HeapNode> value = actualLinkMap.get(codeName);
					Collections.sort(value);
					actualLinkMap.put(codeName, value);
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	// ����Cut-Pointֵ������ͳ��ÿһ���ҵ�����ȷ��������ȫ��������
	private void countNumber() {
		Set<String> keySet = actualLinkMap.keySet();
		HashMap<String, Integer> linkLength = new HashMap<String, Integer>(); // ����ÿ��key�Ĺ������ĳ��ȣ����ȡCut-Pointֵ��
		HashMap<String, int[]> correctArray2CodeName = new HashMap<String, int[]>(); // ����ÿ������εĸ����������Ƿ�����ȷ�ģ�int�����б��棬��ȷ=1������=0��
		                                          									 // int[]�ĳ���ΪlinkLength�еĳ��ȣ�
		for (Iterator<String> it = keySet.iterator(); it.hasNext(); ) {
			String codeName = it.next();
			ArrayList<HeapNode> value = actualLinkMap.get(codeName);
			int vLen = value.size();
			int fLen = vLen > this.maxCutPoint ? this.maxCutPoint : vLen;
			linkLength.put(codeName, fLen); // ��֤���Ȳ�����maxCutPoint
			int[] correctArray = new int[fLen];
			
			for (int i = 0; i < fLen; i++) {
				HeapNode hn = value.get(i);
				if (correctLinkSet.contains(hn.codeName + "\t" + hn.subDocName)) { // �ж�codeName����εĵ�i���������Ƿ�����ȷ��
					correctArray[i] = 1;
				} else {
					correctArray[i] = 0;
				}
			}
			correctArray2CodeName.put(codeName, correctArray);
		}
		
		for (int i = 0; i < this.maxCutPoint; i++) {
			// ͳ��ÿ��Cut-Point��ֵ���ҵ�����������
			Set<String> ks1 = linkLength.keySet();
			int tn = 0; // ÿ��Cut-Point��������������
			int tc = 0; // ÿ��Cut-Point��������ȷ������
			for(Iterator<String> it = ks1.iterator(); it.hasNext(); ) {
				String k1 = it.next();
				int len = linkLength.get(k1);
				int[] corArray = correctArray2CodeName.get(k1);
				if (len >= i+1) {
					tn++;
					if (corArray[i] == 1)
						tc++;
				}
			}
			if (i == 0) {
				sumNumS[i] = tn;
				correctLinkNumS[i] = tc;
			} else {
				sumNumS[i] = sumNumS[i-1] + tn;
				correctLinkNumS[i] = correctLinkNumS[i-1] + tc;
			}
		}
	}
	
	public void computeCallPrec(String correctLinkFile, String inputDir) {
		// 1. ��ȡ������ȷ�Ĺ��������ڴ��С�
		readCorrectLinkSet(correctLinkFile);
		// 2. ��inputDir�ж�ȡʵ�ʵĹ���������ȡ��codeNameSet�а����Ĵ����ļ������й�����,������actualLinkMap��
		readActualLinkMap(inputDir);
		// 3. ����Cut-Pointֵ������ͳ��ÿһ���ҵ�����ȷ��������ȫ��������
		countNumber();
		// ������
		for (int i = 0; i < maxCutPoint; i++) {
			System.out.print("C: " + (i+1) + " , ��ȷ������=" + correctLinkNumS[i] + " , ʶ���������=" + sumNumS[i] + "\t");
			double callValue = (double)correctLinkNumS[i]/(double)sum_cor_num;
			double precisionValue = (double)correctLinkNumS[i]/(double)sumNumS[i];
			System.out.printf("call = %.4f",callValue);
			System.out.printf("\t precision = %.4f\n" ,precisionValue);
//			System.out.println(precisionValue + "\t" + callValue);
		}
		
	}
	
	public static void main(String[] args) {
		String correctLinkFile = "D:\\exp2\\correctLinks.txt"; // ��ȷ�������ļ�
		String inputDir = "D:\\exp2\\inputCP"; // �������Ĺ�����ϵ�ļ���
		
		int cut_point = 6;
		int sum_correct_num = 123;
		CallPrecision cp = new CallPrecision(cut_point, sum_correct_num);
		cp.computeCallPrec(correctLinkFile, inputDir);
	}
}
