package buaa.sei.xyb.analyse.document;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import buaa.sei.xyb.analyse.document.pipeFilter.StopFilter;
import buaa.sei.xyb.common.DocumentDescriptor;
import buaa.sei.xyb.common.GlobalVariant;


/**
 * 
 * @author Xu Yebing
 * DocumentAccess �����ĵ����������࣬�����ĵ������Ĺ���
 */
public class DocumentAccess {

	// pOutlineLevels��ÿ��Ԫ�ر���һ���ĵ��Ĵ�ټ��� 
	public static Vector<Vector<Integer>> pOutlineLevels = new Vector<Vector<Integer>>();
	// tableAtDocs�����ĵ��б������ڵĶ����,��tableStartIndex���ʹ�á�
	public static Vector<Vector<Integer>> tableAtDocs = new Vector<Vector<Integer>>();
	// contexts�е�ÿ��Ԫ�ر���һ���ĵ������ж��䡣
	public static Vector<String []> contexts = new Vector<String[]>();
	// paraBEatDocs ���±���pOutlineLevels�Լ�contexts���±걣�ֶ�Ӧ
	public static Vector<Vector<int[]>> paraBEatDocs = new Vector<Vector<int[]>>();
	// tableStartIndex ����ÿ���ĵ��ı����ʼ����ţ���WordDocParser��divideResult�У�
	public static Vector<Integer> tableStartIndex = new Vector<Integer>();
	// foldersToAnalyze �������ĵ���Ŀ¼���ϣ�����˳�����Ƚ��ȳ�
	public static List<String> foldersToAnalyze;
	
	public static String resultPath = "D:\\ttmp_1"; // ������������·��
	public static String toolPath = "D:\\tool"; // �������õ��ļ����ʵ䡢���ݴʵ䡢ͣ�ô��ļ��ȣ����ļ��У�����·����
	
	public static String stopWordFilePath = "D:\\˶ʿ����\\����ͣ�ô�\\stopword1.txt"; // ��ʱʹ��
	/**
	 * executeStopFilter ִ��ȥͣ�ôʵĲ���
	 * @return ִ�гɹ�����true�����򷵻�false
	 * @throws IOException
	 */
	public static boolean executeStopFilter() throws IOException {
		StopFilter stopFilter = new StopFilter();
		stopFilter.initStopWordSet(stopWordFilePath); // ��ʼ��ͣ�ôʼ���
		// ��������ִʽ�����ļ���
		String filteredFolderPath = resultPath + Constant.FILE_SEPARATOR + Constant.FILTERED_DIR + Constant.FILE_SEPARATOR + Constant.globalCategoryID;
		File filteredFolder = new File(filteredFolderPath);
		if (!filteredFolder.exists() || !filteredFolder.isDirectory()) {
			if (!filteredFolder.mkdirs()) {
				System.out.println("=====>>Error: filteredFolder û�д����ɹ� <<=====");
				return false; // Ŀ¼û�д����ɹ����򷵻�
			}
		}
		// ����je�ļ����µ�globalCategoryID��ִ��ļ�����
		String docWordsFolderPath = resultPath + Constant.FILE_SEPARATOR + Constant.SEGMENT_DIR + Constant.FILE_SEPARATOR + Constant.globalCategoryID;
		File docWordsFolder = new File(docWordsFolderPath);
		if (docWordsFolder.exists() && docWordsFolder.isDirectory()) {
			// �����ļ��У�������µ�����txt�ļ������������ļ��к��������͵��ļ���
			File[] docWordsFiles = docWordsFolder.listFiles();
			for (File docWordsFile : docWordsFiles) {
				if (docWordsFile.getName().endsWith(".txt")) {
					BufferedReader br = new BufferedReader(new FileReader(docWordsFile));
					String docWordsContent = "";
					String lineContent;
					while ((lineContent = br.readLine()) != null) {
						docWordsContent += lineContent + "\n"; //����ĵ���ȫ������
					}
					String filteredContent = stopFilter.filterStopWord(docWordsContent);
					// �����˺������filteredContentд���µ��ļ��б���
					// ���ļ��ľ���·��filteredFilePath
					String filteredFilePath = filteredFolderPath + Constant.FILE_SEPARATOR + docWordsFile.getName();
					File filteredFile = new File(filteredFilePath);
					BufferedWriter bw = new BufferedWriter(new FileWriter(filteredFile));
					bw.write(filteredContent);
					bw.flush();
					bw.close();
					// �����ĵ��ζ�Ӧ���ĵ�������
					createDocumentDescriptor(Constant.globalCategoryID, docWordsFile.getName(),
							                 filteredFilePath);
				}
			}
		}
		return true;
	}
	/**
	 * ����ÿ���ĵ��ε��ĵ�������
	 */
	private static void createDocumentDescriptor (int categoryID, String name, String path) {
		DocumentDescriptor dd = new DocumentDescriptor(categoryID, name, path);
		if (GlobalVariant.docDescriptorList == null)
			GlobalVariant.docDescriptorList = new ArrayList<DocumentDescriptor>();
		GlobalVariant.docDescriptorList.add(dd);
	}
	/**
	 * docProcess �ݹ�ط���ÿ������ĵ��ļ����µ������ļ�
	 * @param docPath
	 */
	public static void docProcess(String docPath) {
		File docFile = new File(docPath);
		if (!docFile.exists())
			return;
		if (docFile.isDirectory()) {
			// ����������Ϊ${WordDocParser.tempDir}���ļ���
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
				// 1. �ĵ��ηָͬʱ��ÿ���ĵ��ν��зִʲ��� [���׶ε����������1.���ֺõ��ĵ��μ��ϡ� 2.ÿ���ĵ��ζ�Ӧ�Ĵ��Ｏ��]
				// Ŀǰ��һ�׶������
				Constant.globalCategoryID ++; // ÿ����һ������ĵ���ȫ�����������1.
				
				wdp.analyze(docPath, resultPath);
				
				// 2. �Էִʽ�����в�ͬ����Ĵ���������ȥ��ͣ�ôʡ��������ݴʵ��ʵ佫�ĵ��е�Ӣ�Ĵʷ�������Ĵʵȣ�
				// ����ʹ������lucene ��׼�������ġ��ܵ����������ṹ��ʹ�÷���������������ȷ
				// TODO �ı��������
				boolean stopFilterFlag = DocumentAccess.executeStopFilter();
				if (stopFilterFlag)
					System.out.println("=====>> ��ȡ����! <<=====");
				// TODO ��û���Ǵ����ĵ��е�Ӣ�ĵ��ʣ�����Ҫ���Ǹ����ֵ䣨�����ݴʵ䣩���з��� 
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
//	public static void main(String args[]) {
//		String folderSet = "D:\\˶ʿ����\\5.31�հ�v1.0\\����������\\0203\\doc"; // �������д���������ĵ����ļ��о���·��
//		DocumentAccess.docProcess(folderSet);
//	}
}
