package buaa.sei.xyb.analyse.tool;

import java.io.File;

import buaa.sei.xyb.common.dict.Dict;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * BuildDataDict 用来分析doc文件保存的数据词典
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
			if (colNum >= 2) { // 规定：数据词典至少要有2列，其中第1列是中文词，第2列是该中文词对应的英文词。词典中的其他列由用户自己定义
				for (int i = 2; i <= rowNum; i++) { // i从2开始，跳过表的第一行（列名）
					Dispatch engCell = Dispatch.call(table, "Cell", i, 2).toDispatch();
					Dispatch engRange = Dispatch.get(engCell, "Range").toDispatch();
					String engWord = Dispatch.get(engRange, "Text").getString();
					engWord = engWord.trim().replaceAll("\\s+", "");
					if (engWord.equals("")) { // 若英文词为空，则跳过该行
						continue;
					}
					Dispatch cnCell = Dispatch.call(table, "Cell", i, 1).toDispatch();
					Dispatch cnRange = Dispatch.get(cnCell, "Range").toDispatch();
					String cnWord = Dispatch.get(cnRange, "Text").getString();
					cnWord = cnWord.trim().replaceAll("\\s+", "");
					
					Dict.dataDict.put(engWord, cnWord); // 以英文词作为key，中文词作为value
				}
			}
		}
	}
	
	public static void main(String[] args) {
		String dataDictPath = "D:\\硕士开题\\师兄资料\\韩晓东\\测试集\\数据词典\\SRS数据词典.doc";
		BuildDataDict.createDataDict(dataDictPath);
	}
}
