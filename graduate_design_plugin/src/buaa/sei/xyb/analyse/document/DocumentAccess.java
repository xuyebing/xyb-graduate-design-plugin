package buaa.sei.xyb.analyse.document;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import buaa.sei.xyb.analyse.document.pipeFilter.StopFilter;
import buaa.sei.xyb.common.Constant;
import buaa.sei.xyb.common.DocumentDescriptor;
import buaa.sei.xyb.common.GlobalVariant;


/**
 * 
 * @author Xu Yebing
 * DocumentAccess 进行文档分析的主类，控制文档分析的过程
 */
public class DocumentAccess {

	// pOutlineLevels的每个元素保存一个文档的大纲级别。 
	public static Vector<Vector<Integer>> pOutlineLevels = new Vector<Vector<Integer>>();
	// tableAtDocs保存文档中表格的所在的段落号,与tableStartIndex配合使用。
	public static Vector<Vector<Integer>> tableAtDocs = new Vector<Vector<Integer>>();
	// contexts中的每个元素保存一个文档的所有段落。
	public static Vector<String []> contexts = new Vector<String[]>();
	// paraBEatDocs 其下标与pOutlineLevels以及contexts的下标保持对应
	public static Vector<Vector<int[]>> paraBEatDocs = new Vector<Vector<int[]>>();
	// tableStartIndex 保存每个文档的表格起始的序号（在WordDocParser的divideResult中）
	public static Vector<Integer> tableStartIndex = new Vector<Integer>();
	// foldersToAnalyze 待分析文档的目录集合，分析顺序是先进先出
	public static List<String> foldersToAnalyze;
	// docLocationMap 用于保存文档段及其对应的信息（文档段的位置、大小、偏移、父文档名）
	public static HashMap<String, DocInfo> docLocationMap = new HashMap<String, DocInfo>();
	
	public static String resultPath = Constant.tempFolder; // 保存分析结果的路径
	public static String toolPath = Constant.toolPath; // 包含有用的文件（词典、数据词典、停用词文件等）的文件夹（绝对路径）
	
	public static String stopWordFilePath = "D:\\硕士开题\\中文停用词\\stopword1.txt"; // 临时使用
	/**
	 * executeStopFilter 执行去停用词的操作
	 * @return 执行成功返回true，否则返回false
	 * @throws IOException
	 */
	public static boolean executeStopFilter() throws IOException {
		StopFilter stopFilter = new StopFilter();
		stopFilter.initStopWordSet(stopWordFilePath); // 初始化停用词集合
		// 创建保存分词结果的文件夹
		String filteredFolderPath = resultPath + Constant.FILE_SEPARATOR + Constant.FILTERED_DIR + Constant.FILE_SEPARATOR + Constant.globalCategoryID;
		File filteredFolder = new File(filteredFolderPath);
		if (!filteredFolder.exists() || !filteredFolder.isDirectory()) {
			if (!filteredFolder.mkdirs()) {
				System.out.println("=====>>Error: filteredFolder 没有创建成功 <<=====");
				return false; // 目录没有创建成功，则返回
			}
		}
		// 分析je文件夹下第globalCategoryID类分词文件集合
		String docWordsFolderPath = resultPath + Constant.FILE_SEPARATOR + Constant.SEGMENT_DIR + Constant.FILE_SEPARATOR + Constant.globalCategoryID;
		File docWordsFolder = new File(docWordsFolderPath);
		if (docWordsFolder.exists() && docWordsFolder.isDirectory()) {
			// 遍历文件夹，获得其下的所有txt文件（不考虑子文件夹和其他类型的文件）
			File[] docWordsFiles = docWordsFolder.listFiles();
			for (File docWordsFile : docWordsFiles) {
				if (docWordsFile.getName().endsWith(".txt")) {
					BufferedReader br = new BufferedReader(new FileReader(docWordsFile));
					String docWordsContent = "";
					String lineContent;
					while ((lineContent = br.readLine()) != null) {
						docWordsContent += lineContent + "\n"; //获得文档的全部内容
					}
					String filteredContent = stopFilter.filterStopWord(docWordsContent);
//					// 将过滤后的内容filteredContent写入新的文件中保存
//					// 新文件的绝对路径filteredFilePath
//					String filteredFilePath = filteredFolderPath + Constant.FILE_SEPARATOR + docWordsFile.getName();
//					File filteredFile = new File(filteredFilePath);
//					BufferedWriter bw = new BufferedWriter(new FileWriter(filteredFile));
//					bw.write(filteredContent);
//					bw.flush();
//					bw.close();
					
					// 添加wds类型的输入文档，其中每一行形式为：词语=出现频数；以该类文档作为构建LDA模型的输入
					String fn = docWordsFile.getName();
					int eindex = fn.lastIndexOf(".");
					if (eindex > 0)
						fn = fn.substring(0, eindex);
					fn += ".wds"; // 将文件名从"abc.xxx"改为了"abc.wds"
					HashMap<String, Integer> wordsMap = new HashMap<String, Integer>();
					String[] words = filteredContent.split("\\s");
					for (String word : words) {
						if (wordsMap.containsKey(word)) {
							wordsMap.put(word, wordsMap.get(word)+1);
						} else {
							wordsMap.put(word, 1);
						}
					}
					String wdsContent = "";
					for (Entry<String, Integer> entry : wordsMap.entrySet()) {
						wdsContent += entry.getKey() + "=" + entry.getValue() + "\r\n";
					}
					String wdsFilePath = filteredFolderPath + Constant.FILE_SEPARATOR + fn;
					File wdsFile = new File(wdsFilePath);
					BufferedWriter bw2 = new BufferedWriter(new FileWriter(wdsFile));
					bw2.write(wdsContent);
					bw2.flush();
					bw2.close();
					// 产生文档段对应的文档描述符
//					createDocumentDescriptor(Constant.globalCategoryID, docWordsFile.getName(),
//							                 filteredFilePath);
					createDocumentDescriptor(Constant.globalCategoryID, fn,
							wdsFilePath);
				}
			}
		}
		return true;
	}
	/**
	 * 产生每个文档段的文档描述符
	 */
	private static void createDocumentDescriptor (int categoryID, String name, String path) {
		DocumentDescriptor dd = new DocumentDescriptor(categoryID, name, path);
		if (GlobalVariant.docDescriptorList == null)
			GlobalVariant.docDescriptorList = new ArrayList<DocumentDescriptor>();
		GlobalVariant.docDescriptorList.add(dd);
	}
	/**
	 * docProcess 递归地分析每个软件文档文件夹下的所有文件
	 * @param docPath
	 */
	public static void docProcess(String docPath) {
		File docFile = new File(docPath);
		if (!docFile.exists())
			return;
		if (docFile.isDirectory()) {
			// 不分析名称为${WordDocParser.tempDir}的文件夹
			if (docFile.getName().contains(WordDocParser.tempDir))
				return;
			else {
				File[] files  = docFile.listFiles();
				for (File file : files) {
					docProcess(file.getAbsolutePath());
				}
			}
		} else {
			WordDocParser wdp = new WordDocParser(); 
			try {
				// 1. 文档段分割，同时对每个文档段进行分词操作 [本阶段的输出包括：1.划分好的文档段集合。 2.每个文档段对应的词语集合]
				// 目前第一阶段已完成
				Constant.globalCategoryID ++; // 每分析一个软件文档，全局类别索引加1.
				
				wdp.analyze(docPath, resultPath);
				
				// 2. 对分词结果进行不同步骤的处理（包括：去掉停用词、根据数据词典或词典将文档中的英文词翻译成中文词等）
				// 考虑使用类似lucene 标准分析器的“管道过滤器”结构，使得分析过程清晰、明确
				// TODO 文本处理过程
				boolean stopFilterFlag = DocumentAccess.executeStopFilter();
				if (stopFilterFlag)
					System.out.println("=====>> 提取结束! <<=====");
				// TODO 还没考虑处理文档中的英文单词，这需要考虑根据字典（或数据词典）进行翻译 
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
//	public static void main(String args[]) {
//		String folderSet = "D:\\硕士开题\\5.31终版v1.0\\测试用例集\\0203\\doc"; // 包含所有待分析软件文档的文件夹绝对路径
//		DocumentAccess.docProcess(folderSet);
//	}
}
