package buaa.sei.xyb.analyse.tool;

import java.io.File;

import buaa.sei.xyb.common.dict.Dict;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * BuildDataDict ��������doc�ļ���������ݴʵ�
 * @author Xu Yebing
 */
public class BuildDataDict {

	public static void createDataDict(String dataDictPath) {
		File dataDictFile = new File(dataDictPath);
		if (dataDictFile.exists() && 
			dataDictFile.isFile() && 
			dataDictFile.getName().endsWith("doc")) {
			ActiveXComponent word = new ActiveXComponent("Word.Application");
			word.setProperty("Visible", new Variant(false));
			Dispatch documents = word.getProperty("Documents").toDispatch();
			// open word document
			Dispatch wordDoc = Dispatch.call(documents, "Open", dataDictPath).toDispatch();
			Dispatch tables = Dispatch.get(wordDoc, "Tables").toDispatch();
			Dispatch table = Dispatch.call(tables, "Item", new Variant(1)).toDispatch();
			Dispatch rows = Dispatch.get(table, "Rows").toDispatch();
			Dispatch columns = Dispatch.get(table, "Columns").toDispatch();
			
			int rowNum = Dispatch.get(rows, "Count").getInt();
			int colNum = Dispatch.get(columns, "Count").getInt();
			if (colNum >= 2) { // �涨�����ݴʵ�����Ҫ��2�У����е�1�������Ĵʣ���2���Ǹ����Ĵʶ�Ӧ��Ӣ�Ĵʡ��ʵ��е����������û��Լ�����
				for (int i = 2; i <= rowNum; i++) { // i��2��ʼ��������ĵ�һ�У�������
					Dispatch engCell = Dispatch.call(table, "Cell", i, 2).toDispatch();
					Dispatch engRange = Dispatch.get(engCell, "Range").toDispatch();
					String engWord = Dispatch.get(engRange, "Text").getString();
					engWord = engWord.trim().replaceAll("\\s+", "");
					if (engWord.equals("")) { // ��Ӣ�Ĵ�Ϊ�գ�����������
						continue;
					}
					Dispatch cnCell = Dispatch.call(table, "Cell", i, 1).toDispatch();
					Dispatch cnRange = Dispatch.get(cnCell, "Range").toDispatch();
					String cnWord = Dispatch.get(cnRange, "Text").getString();
					cnWord = cnWord.trim().replaceAll("\\s+", "");
					
					Dict.dataDict.put(engWord, cnWord); // ��Ӣ�Ĵ���Ϊkey�����Ĵ���Ϊvalue
				}
			}
		}
	}
	
	public static void main(String[] args) {
		String dataDictPath = "D:\\˶ʿ����\\ʦ������\\������\\���Լ�\\���ݴʵ�\\SRS���ݴʵ�.doc";
		BuildDataDict.createDataDict(dataDictPath);
	}
}
