package buaa.sei.xyb.process.irmodel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import buaa.sei.xyb.analyse.modelcontrol.BuildModel;
import buaa.sei.xyb.common.Constant;

public class InitialVectorSpace {
	
	// 构造向量矩阵（将统计所有的词汇，获得每个文档段的向量表示）
	// LDA模型中已经统计了所有的词汇，可以直接分析wordmap.txt获得
	// 矩阵中词的顺序按照wordmap.txt中给定的序号排列
	
	// 1. 分析wordmap.txt，获得所有的词汇
//	public HashMap<Integer, String> getWordMap() {
//		HashMap<Integer, String> wordMap = new HashMap<Integer, String>();
//		String wordMapPath = BuildModel.matrixFilePath + Constant.FILE_SEPARATOR + BuildModel.matrixWordMap;
//		try {
//			BufferedReader br = new BufferedReader(new InputStreamReader(
//					new FileInputStream(wordMapPath), "UTF-8"));
//			String line = "";
//			while ((line = br.readLine()) != null) {
//				String[] res = line.split("\\s+");
//				if (res.length == 2) {
//					wordMap.put(Integer.valueOf(res[1]), res[0]);
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (NumberFormatException e) {
//			e.printStackTrace();
//		}
//		return wordMap;
//	}
	/**
	 * 直接使用shannonInfo.txt文件进行
	 */
}
