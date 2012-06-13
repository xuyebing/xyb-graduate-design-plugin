package buaa.sei.xyb.common;

import java.util.ArrayList;
import java.util.HashMap;

public class GlobalVariant {

	// docDescriptorList 保存所有文档段(包括代码、软件文档)的描述信息
	public static ArrayList<DocumentDescriptor> docDescriptorList = null;
	// 保存java程序项目名
	//public static String sourceCodeProjectName = null;
	// 保存待分析的软件文档集合
	//public static String softwareDocFolder = null;
	// tablePathMap保存当前分析的文档中全部的表格所对应的word文档的路径信息
	// 例： tablePathMap.add("概要设计_100", "D:\\abc\\设计文档_table_1.doc")
	public static HashMap<String, String> tablePathMap = null;
}
