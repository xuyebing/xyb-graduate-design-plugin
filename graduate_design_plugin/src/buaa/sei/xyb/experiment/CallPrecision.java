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
 * 用于计算查全率和查准率
 * @author Xu Yebing
 */
public class CallPrecision {
	private HashSet<String> correctLinkSet = null; // 保存所有正确的关联链
	private HashSet<String> codeNameSet = null; // 保存关注的代码文件的名称
	private HashSet<String> docNameSet = null; // 保存关注的文档段的名称
	private HashMap<String, ArrayList<HeapNode>> actualLinkMap = null; // 保存实际的关联链,key=codeName, value=与codeName相关的降序排列的关联链
	private int maxCutPoint = 6; // Cut-Point的最大数值
	private int[] correctLinkNumS = null; // 保存每个Cut-Point数值下找到的正确链接数
	private int[] sumNumS = null; // 保存每个Cut-Point数值下找到的链接总数
	private int sum_cor_num = 0; // 全部的关联链接数
	
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

	// 读取所有正确的关联链到内存中
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
			System.out.println(">>> 代码段数目: " + codeNameSet.size());
			System.out.println(">>> 文档段数码：" + docNameSet.size());
			BufferedWriter bw = new BufferedWriter(new FileWriter("D:\\exp2\\7_7.txt"));
			for (String sDocName : docNameSet) {
				bw.write(sDocName + "\n");
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// 从inputDir中读取实际的关联链，提取出codeNameSet中包含的代码文件的所有关联链,保存在actualLinkMap中
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
						if (codeNameSet.contains(tmp[1])) { // codeName包含在codeNameSet中, 是我们关注的代码段
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
				// 将所有的关联链降序排列
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
	// 按照Cut-Point值遍历，统计每一轮找到的正确链接数、全部链接数
	private void countNumber() {
		Set<String> keySet = actualLinkMap.keySet();
		HashMap<String, Integer> linkLength = new HashMap<String, Integer>(); // 保存每个key的关联链的长度（最大取Cut-Point值）
		HashMap<String, int[]> correctArray2CodeName = new HashMap<String, int[]>(); // 保存每个代码段的各个关联链是否是正确的（int数组中保存，正确=1，错误=0）
		                                          									 // int[]的长度为linkLength中的长度，
		for (Iterator<String> it = keySet.iterator(); it.hasNext(); ) {
			String codeName = it.next();
			ArrayList<HeapNode> value = actualLinkMap.get(codeName);
			int vLen = value.size();
			int fLen = vLen > this.maxCutPoint ? this.maxCutPoint : vLen;
			linkLength.put(codeName, fLen); // 保证长度不超过maxCutPoint
			int[] correctArray = new int[fLen];
			
			for (int i = 0; i < fLen; i++) {
				HeapNode hn = value.get(i);
				if (correctLinkSet.contains(hn.codeName + "\t" + hn.subDocName)) { // 判断codeName代码段的第i个关联链是否是正确的
					correctArray[i] = 1;
				} else {
					correctArray[i] = 0;
				}
			}
			correctArray2CodeName.put(codeName, correctArray);
		}
		
		for (int i = 0; i < this.maxCutPoint; i++) {
			// 统计每个Cut-Point数值下找到的链接总数
			Set<String> ks1 = linkLength.keySet();
			int tn = 0; // 每个Cut-Point新增的总链接数
			int tc = 0; // 每个Cut-Point新增的正确链接数
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
		// 1. 读取所有正确的关联链到内存中。
		readCorrectLinkSet(correctLinkFile);
		// 2. 从inputDir中读取实际的关联链，提取出codeNameSet中包含的代码文件的所有关联链,保存在actualLinkMap中
		readActualLinkMap(inputDir);
		// 3. 按照Cut-Point值遍历，统计每一轮找到的正确链接数、全部链接数
		countNumber();
		// 输出结果
		for (int i = 0; i < maxCutPoint; i++) {
			System.out.print("C: " + (i+1) + " , 正确链接数=" + correctLinkNumS[i] + " , 识别的链接数=" + sumNumS[i] + "\t");
			double callValue = (double)correctLinkNumS[i]/(double)sum_cor_num;
			double precisionValue = (double)correctLinkNumS[i]/(double)sumNumS[i];
			System.out.printf("call = %.4f",callValue);
			System.out.printf("\t precision = %.4f\n" ,precisionValue);
//			System.out.println(precisionValue + "\t" + callValue);
		}
		
	}
	
	public static void main(String[] args) {
		String correctLinkFile = "D:\\exp2\\correctLinks.txt"; // 正确关联链文件
		String inputDir = "D:\\exp2\\inputCP"; // 待分析的关联关系文件夹
		
		int cut_point = 6;
		int sum_correct_num = 123;
		CallPrecision cp = new CallPrecision(cut_point, sum_correct_num);
		cp.computeCallPrec(correctLinkFile, inputDir);
	}
}
