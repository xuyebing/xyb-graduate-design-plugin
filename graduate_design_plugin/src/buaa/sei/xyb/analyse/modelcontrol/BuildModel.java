package buaa.sei.xyb.analyse.modelcontrol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.jdt.core.JavaModelException;

import buaa.sei.xyb.analyse.code.CodeAccess;
import buaa.sei.xyb.analyse.document.DocumentAccess;
import buaa.sei.xyb.common.Constant;
import buaa.sei.xyb.common.DocumentDescriptor;
import buaa.sei.xyb.common.GlobalVariant;
import buaa.sei.xyb.database.DataBaseOperation;
import buaa.sei.xyb.lda.jgibblda.LDA;
import buaa.sei.xyb.process.irmodel.LSIProcess;
import buaa.sei.xyb.process.irmodel.VSMProcess;

public class BuildModel {

	public static int matrixIndex = 0; // ���ڼ�¼��������ʱ��ÿ���ĵ�����Ӧ�ľ�����к�,��0��ʼ����
	public static final String matrixFileName = "inputMatrix.txt"; // �����ļ���
	public static final String matrixWordoc = "model-final.wordoc"; // LDAģ�ͼ����õ���"��-�ĵ�"���ʷֲ������ڼ���"��ũ��Ϣ"
	public static final String matrixShannonInfo = "shannonInfo.txt"; // ������ũ��Ϣ���ļ���
	public static final String matrixLSI = "LSI-Matrix.txt"; // ���桰�ĵ�-�ʡ��������о����е�ÿһ��A[i][j]��ʾj����i�ĵ��еĴ�Ƶ(�þ�������LSIģ��)
	public static final String matrixShannonWords = "shannonWords.txt"; // ����ũ��Ϣֵ���򱣴�ÿ���ĵ��е����ⵥ��
	public static final String matrixWordMap = "wordmap.txt";
	public static String matrixFilePath = ""; // ��������ļ���·��(�������ļ���)
	private static int docNums = 0;  // ���ڼ���inputMatrix���ж����У�LDA�������ļ���һ�е�������������
	private String folderSet; // �������д���������ĵ����ļ��о���·��
	private String projectName; // ����������Ŀ����
	
	public BuildModel(String folderSet, String projectName) {
		this.folderSet = folderSet;
		this.projectName = projectName;
	}
	
	/**
	 * �������ݿ��е�Ӣ�ķ����
	 */
	public void createDataBase() {
		DataBaseOperation.createDataBase(DataBaseOperation.db_name);
		StringBuilder tableFields = new StringBuilder(DataBaseOperation.keyForTable + " VARCHAR(200) NOT NULL,");
		tableFields.append("cn_words TEXT,");
		tableFields.append("in_parenthesis TINYINT default \'0\',");
		tableFields.append("previous_cn_word TEXT,");
		tableFields.append(" PRIMARY KEY (" + DataBaseOperation.keyForTable + ")");
		
		DataBaseOperation.createTable(DataBaseOperation.db_name, DataBaseOperation.translate_table_name, tableFields.toString());
	}
	
	public void build() throws JavaModelException {
		// 1. �ĵ��δ���
		createDataBase();
		// String folderSet = "D:\\��������"; // �������д���������ĵ����ļ��о���·��
//		File tempDir = new File(Constant.tempFolder);
//		if (tempDir.exists() && tempDir.isDirectory()) {  // ���tempDirĿ¼�е���ʱword�ļ�
//			File[] files = tempDir.listFiles();
//			for (File file : files)
//				file.delete();
//			tempDir.delete();
//		}
		DocumentAccess.docProcess(folderSet);
		// 2. ����δ���
		CodeAccess.codeProcess(projectName);
		// 3. �������ĵ�-���ʡ����󣬸þ����һ�ж�Ӧһ���ĵ���ÿ�е������Ǵ��ĵ�����ȡ�Ĵ��Ｏ��
		matrixFilePath = DocumentAccess.resultPath + Constant.FILE_SEPARATOR + Constant.MATRIX_DIR; // !!��Ҫ�޸�!!
		File matrixDirFile = new File(matrixFilePath);
		if (!matrixDirFile.exists()) {
			matrixDirFile.mkdirs();
		}
		File matrixFile = new File(matrixFilePath + Constant.FILE_SEPARATOR + matrixFileName);
		if (matrixFile.exists() && !matrixFile.isDirectory()) {
			matrixFile.delete(); // ���ԭ��matrixFile�ļ��Ѵ��ڣ�����ɾ��ԭ�е��ļ�
		}
		docNums = GlobalVariant.docDescriptorList.size();
		int sumNum = docNums;
		try {
			BufferedWriter ibw = new BufferedWriter(new FileWriter(matrixFile, true));
			String firstLine = sumNum + "\r\n";
			ibw.write(firstLine);
			ibw.flush();
			ibw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// �����ĵ�+�����ȫ���ļ�������
		for(Iterator<DocumentDescriptor> iterator = GlobalVariant.docDescriptorList.iterator();
			iterator.hasNext(); ) {
			DocumentDescriptor dd = iterator.next();
			String filePath = dd.getPath();
			File file = new File(filePath);
			if (file.exists() && file.getName().endsWith(".wds")) {
				try {
					String content = "";
					BufferedReader br = new BufferedReader(new FileReader(file));
					String line = "";
					while ((line = br.readLine()) != null) {
						String[] keyValue = line.split("=");
						if (keyValue.length != 2) {
							System.out.println(">>>> Error: Read wds file fail!");
							return;
						}
						int value = Integer.valueOf(keyValue[1]);
						for (int i = 0; i < value; i++) {
							content += keyValue[0] + " ";
						}
					}
					///////////////
					content = content.replaceAll("\\s+", " "); // ������ת��Ϊһ���Կո�����Ĵ���
					content = content.trim();
					content += "\r\n";
					// write into the matrixFile
//					BufferedWriter bw = new BufferedWriter(new FileWriter(matrixFile, true)); // FileWriter�ĵڶ�������(booleanֵ)Ϊtrue
//					                                                                          // ��ʾ�� ׷�ӵķ�ʽд��
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(matrixFile, true), "UTF-8"));
					bw.write(content);
					bw.flush();
					bw.close();
					// �����ĵ���������matrixIndex��ֵ
					dd.setMatrixIndex(matrixIndex++);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
//		2012-4-6 дinputMatrix.txt�ɹ�������������������뵽LDAģ����
		String argStr = "-est -alpha " + Constant.estAlpha +
				            " -beta " + Constant.estBeta +
				            " -ntopics " + Constant.estNtopics +
				            " -niters " + Constant.estNiters +
				            " -savestep " + Constant.estSavestep +
				            " -twords " + Constant.estTwords + " " +
                            "-dir " + this.matrixFilePath +
                            " -dfile " + this.matrixFileName;
		String[] args = argStr.split(" ");
		LDA.main(args);
		System.out.println("============>> LDA ģ��ִ����� <<================");
		// ������ ����ÿ���ʻ���ÿ���ĵ����е���ũ��Ϣֵ
		computeShannonInfo();
		// �������ĵ�-�ʡ����󣬾����б����ֵΪ�����ĵ��г��ֵĴ�Ƶ
		buildMatrixForLSI();
		
		System.out.println("============>> ShannonInfo ������� <<================");
//		���� 2012-04-09, �õ��ĵ���-����-��ũֵ�ļ�
		createShannonWordsFile();
		System.out.println("============>> \"�ĵ���-����-��ũֵ�ļ�\"������� <<================");
		
//		/******** ʹ��VSMģ�ͼ�������� *******/
//		VSMProcess vsmProcess = new VSMProcess();
//		System.out.println("============>> \"��ʼLDA-VSM����\"������� <<================");
//		vsmProcess.init();
//		System.out.println("============>> LDA-VSM ��ʼ����� <<================");
//		// �������ƶ�
//		vsmProcess.compute();
//		System.out.println("============>> !LDA-VSM �������! <<================");
		
		/**************  LDA ʹ�á��ĵ�-���⡱�ֲ��������ƶ�  *******************/
		VSMProcess vsmProcessTopic = new VSMProcess(Constant.LDA_TOPIC_MATRIX_FILENAME, Constant.LDA_TOPIC_RESULT_OUTPUT_FILE_PREFIX);
		System.out.println("============>> \"��ʼLDA-Topic -> VSM����\"������� <<================");
		vsmProcessTopic.init();
		System.out.println("============>> LDA-Topic -> VSM ��ʼ����� <<================");
		// �������ƶ�
		vsmProcessTopic.compute();
		System.out.println("============>> !LDA-Topic -> VSM �������! <<================");
		
		/***********  LDA-LSI����  *************/
		String matrixLdaLsi = BuildModel.matrixShannonInfo; // ������VSMģ�͵������ļ���ͬ
		String ldaLsiOutputName = Constant.LDA_LSI_OUTPUT_MATRIX_FILENAME;
		LSIProcess ldaLsiProc = new LSIProcess(matrixLdaLsi, ldaLsiOutputName);
		ldaLsiProc.initMatrix();
		ldaLsiProc.triggerLSIAnalysis();
		
		VSMProcess vsmProcess1 = new VSMProcess(Constant.LDA_LSI_OUTPUT_MATRIX_FILENAME, Constant.LDA_LSI_RESULT_OUTPUT_FILE_PREFIX);
		System.out.println("============>> \"��ʼLDA-LSI -> VSM����\"������� <<================");
		vsmProcess1.init();
		System.out.println("============>> LDA-LSI -> VSM ��ʼ����� <<================");
		// �������ƶ�
		vsmProcess1.compute();
		System.out.println("============>> !LDA-LSI -> VSM �������! <<================");
		
		/** *** LSI ģ�ͼ��� *** **/
		// ����LSI��������
		LSIProcess lsiProc = new LSIProcess();
		lsiProc.initMatrix();
		lsiProc.triggerLSIAnalysis();
		
		VSMProcess vsmProcess2 = new VSMProcess(Constant.LSI_OUTPUT_MATRIX_FILENAME, Constant.LSI_RESULT_OUTPUT_FILE_PREFIX);
		System.out.println("============>> \"��ʼLSI -> VSM����\"������� <<================");
		vsmProcess2.init();
		System.out.println("============>> LSI -> VSM ��ʼ����� <<================");
		// �������ƶ�
		vsmProcess2.compute();
		System.out.println("============>> !LSI -> VSM �������! �������߷������! <<================");
	}
	
	class SwPair implements Comparable {
		private String word;
		private double shannonValue;
		
		public SwPair (String word, double shannonValue) {
			this.word = word;
			this.shannonValue = shannonValue;
		}

		/**
		 * compareTo�����Ƚϣ��ȽϽ��������ģ����Ԫ����ǰ�棩
		 */
		@Override
		public int compareTo(Object arg0) {
			// TODO Auto-generated method stub
			if (arg0 instanceof SwPair) {
				SwPair b = (SwPair)arg0;
				if (this.shannonValue > b.shannonValue)
					return  -1;
				else if (this.shannonValue == b.shannonValue)
					return 0;
				else
					return 1;
			}
			return 0;
		}
	}
	
	private void createShannonWordsFile() {
		// ����wordmap.txt
		HashMap<Integer, String> wordMap = getWordMap();
		String shannonInfoFile = this.matrixFilePath + Constant.FILE_SEPARATOR + this.matrixShannonInfo;
		String shannonWordsFile = this.matrixFilePath + Constant.FILE_SEPARATOR + this.matrixShannonWords;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(shannonWordsFile));
			BufferedReader br = new BufferedReader(new FileReader(shannonInfoFile));
			String line = "";
			int matrixNo = 0;
			while ((line = br.readLine()) != null) {
				DocumentDescriptor dd = getDD(matrixNo);
				bw.write(dd.getName() + " "); // д���ĵ�������
				String[] splits = line.split("\\s+");
				int len = splits.length;
				Set<SwPair> shannonWordSet = new TreeSet<SwPair>();
				for (int i = 0; i < len; i++) {
					double value = Double.valueOf(splits[i]);
					if (value == 0.0)
						continue;
					String word = wordMap.get(i); // ��ö�Ӧ�ĵ���
					shannonWordSet.add(new SwPair(word, value));
				}
				for (Iterator<SwPair> it = shannonWordSet.iterator(); 
						it.hasNext(); ) {
					SwPair swPair = it.next();
					bw.write(swPair.word + ":" + swPair.shannonValue + " ");
				}
				bw.write("\n");
				matrixNo++;
			}
			br.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void computeShannonInfo() {
		// ����wordmap.txt
		HashMap<Integer, String> wordMap = getWordMap();
		// read the "model-final.wordoc"
		String finalWordoc = this.matrixFilePath + Constant.FILE_SEPARATOR + this.matrixWordoc;
		String shannonInfoFile = this.matrixFilePath + Constant.FILE_SEPARATOR + this.matrixShannonInfo;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(shannonInfoFile));
			//////////////////
			BufferedReader br = new BufferedReader(new FileReader(finalWordoc));
			String line = "";
			int matrixNo = 0;
			while ((line = br.readLine()) != null) {
				HashMap<String, Integer> ddWordMap = getDDWordMap(matrixNo);
				String[] splits = line.split("\\s+");
				int len = splits.length;
				Map<Integer, String> shannonWordTreeMap = new TreeMap<Integer, String>();
				for (int i = 0; i < len; i++) {
					double value = Double.valueOf(splits[i]);
					int freq = getFrequency(i, wordMap, ddWordMap);
					// shannon information expression
					double I = freq * Math.log(value) * (-1);
					// write I into the shannon info file
					bw.write(String.valueOf(I) + " ");
					//// ��¼ÿ���ĵ��ζ�Ӧ�Ĵʺ���Ӧ����ũ��Ϣֵ
					
				}
				bw.write("\n");
				matrixNo++;
			}
			br.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * buildMatrixForLSI ������Ƶ����A:һ�ж�Ӧһ���ĵ���һ�ж�Ӧһ����;
	 *                   A[i][j] = ��j�����ڵ�i���ĵ��г��ֵĴ���
	 * @return
	 */
	private void buildMatrixForLSI() {
		// ����wordmap.txt
		HashMap<Integer, String> wordMap = getWordMap();
		// read the "model-final.wordoc"
		String finalWordoc = this.matrixFilePath + Constant.FILE_SEPARATOR + this.matrixWordoc;
		String matrixLSI = this.matrixFilePath + Constant.FILE_SEPARATOR + this.matrixLSI;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(matrixLSI));
			//////////////////
			BufferedReader br = new BufferedReader(new FileReader(finalWordoc));
			String line = "";
			int matrixNo = 0;
			while ((line = br.readLine()) != null) {
				HashMap<String, Integer> ddWordMap = getDDWordMap(matrixNo);
				String[] splits = line.split("\\s+");
				int len = splits.length;
				Map<Integer, String> shannonWordTreeMap = new TreeMap<Integer, String>();
				for (int i = 0; i < len; i++) {
					double value = Double.valueOf(splits[i]);
					int freq = getFrequency(i, wordMap, ddWordMap);
					// write freq into the LSI Matrix file
					bw.write(String.valueOf(freq) + " ");
				}
				bw.write("\n");
				matrixNo++;
			}
			br.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private HashMap<Integer, String> getWordMap() {
		HashMap<Integer, String> wordMap = new HashMap<Integer, String>();
		String wordMapPath = this.matrixFilePath + Constant.FILE_SEPARATOR + this.matrixWordMap;
		try {
//			BufferedReader br = new BufferedReader(new FileReader(wordMapPath));
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(wordMapPath), "UTF-8"));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] res = line.split("\\s+");
				if (res.length == 2) {
					wordMap.put(Integer.valueOf(res[1]), res[0]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return wordMap;
	}
	/**
	 * ���ÿ���ĵ��ΰ����Ĵʼ�����+�ʵĳ���Ƶ����
	 * @param matrixRowNo
	 * @return �ĵ��ΰ����Ĵʼ�����  + ��Ƶ��
	 */
	private HashMap<String, Integer> getDDWordMap(int matrixRowNo) {
		HashMap<String, Integer> ddWordMap = new HashMap<String, Integer>();
		DocumentDescriptor dd = getDD(matrixRowNo);
		if (dd != null) {
			String file = dd.getPath();
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
	//					BufferedReader br = new BufferedReader(new InputStreamReader(
	//							new FileInputStream(file), "UTF-8"));
				String line = "";
				while ((line = br.readLine()) != null) {
					String[] res = line.split("=");
					if (res.length == 2) {
						ddWordMap.put(res[0], Integer.valueOf(res[1]));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ddWordMap;
	}
	/**
	 * ���ݾ����е��кŻ�ö�Ӧ��DocumentDescriptor����
	 */
	private DocumentDescriptor getDD(int matrixRowNo) {
		DocumentDescriptor retDd = null;
		for (Iterator<DocumentDescriptor> iterator = GlobalVariant.docDescriptorList.iterator();
				iterator.hasNext(); ) {
			DocumentDescriptor dd = iterator.next();
			if (dd.getMatrixIndex() == matrixRowNo) {
				retDd = dd;
			}
		}
		return retDd;
	}
	/**
	 * @param wordId: ������wordmap.txt�е����
	 * @param wordMap: ����wordmap.txt�����ġ����-�ʡ��� ��ϣӳ��
	 * @param matrixRowNo: �����е��кţ������������ҵ���Ӧ���ĵ��Σ���ø��ĵ��ΰ����Ĵʼ�
	 * @return
	 */
	private int getFrequency(int wordId, HashMap<Integer, String> wordMap, HashMap<String, Integer> ddWordMap) {
		// ����wordId��wordmap.txt���ҵ���Ӧ�ĵ���
		if (wordMap.containsKey(wordId)) {
			String word = wordMap.get(wordId); // �ҵ���Ӧ�ĵ���word
			// ȥÿ���ĵ��в��Ҹõ��ʳ��ֵ�Ƶ��
			if (ddWordMap.containsKey(word)) {
				return ddWordMap.get(word);
			} else {
				return 0;
			}
		}
		return 0;
	}
}
