package buaa.sei.xyb.process.irmodel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import buaa.sei.xyb.analyse.modelcontrol.BuildModel;
import buaa.sei.xyb.common.Constant;

public class InitialVectorSpace {
	
	// �����������󣨽�ͳ�����еĴʻ㣬���ÿ���ĵ��ε�������ʾ��
	// LDAģ�����Ѿ�ͳ�������еĴʻ㣬����ֱ�ӷ���wordmap.txt���
	// �����дʵ�˳����wordmap.txt�и������������
	
	// 1. ����wordmap.txt��������еĴʻ�
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
	 * ֱ��ʹ��shannonInfo.txt�ļ�����
	 */
}
