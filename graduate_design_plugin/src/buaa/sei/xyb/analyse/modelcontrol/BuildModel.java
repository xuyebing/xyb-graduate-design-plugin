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
import java.text.DateFormat;
import java.util.Date;
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

	public static int matrixIndex = 0; // 用于记录创建矩阵时，每个文档所对应的矩阵的行号,从0开始计数
	public static final String matrixFileName = "inputMatrix.txt"; // 矩阵文件名
	public static final String matrixWordoc = "model-final.wordoc"; // LDA模型计算后得到的"词-文档"概率分布，用于计算"香农信息"
	public static final String matrixShannonInfo = "shannonInfo.txt"; // 保存香农信息的文件名
	public static final String matrixLSI = "LSI-Matrix.txt"; // 保存“文档-词”矩阵，其中矩阵中的每一项A[i][j]表示j词在i文档中的词频(该矩阵用于LSI模型)
	public static final String matrixShannonWords = "shannonWords.txt"; // 按香农信息值降序保存每个文档中的主题单词
	public static final String matrixWordMap = "wordmap.txt";
	public static String matrixFilePath = ""; // 保存矩阵文件的路径(不包含文件名)
	private static int docNums = 0;  // 用于计算inputMatrix共有多少行（LDA的输入文件第一行的内容是行数）
	private String folderSet; // 包含所有待分析软件文档的文件夹绝对路径
	private String projectName; // 待分析的项目名称
	
	public BuildModel() {
	}
	
	public BuildModel(String folderSet, String projectName) {
		this.folderSet = folderSet;
		this.projectName = projectName;
	}
	
	/**
	 * 创建数据库中的英文翻译表
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
		// 打印开始时间
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
		Date startDate = new Date();
		String startTime = df.format(startDate);
		
		// 1. 文档段处理
		createDataBase();
		// String folderSet = "D:\\毕设用例"; // 包含所有待分析软件文档的文件夹绝对路径
//		File tempDir = new File(Constant.tempFolder);
//		if (tempDir.exists() && tempDir.isDirectory()) {  // 清空tempDir目录中的临时word文件
//			File[] files = tempDir.listFiles();
//			for (File file : files)
//				file.delete();
//			tempDir.delete();
//		}
		DocumentAccess.docProcess(folderSet);
		// 2. 代码段处理
		CodeAccess.codeProcess(projectName);
		// 3. 建立“文档-单词”矩阵，该矩阵的一行对应一个文档，每行的内容是从文档中提取的词语集合
		matrixFilePath = DocumentAccess.resultPath + Constant.FILE_SEPARATOR + Constant.MATRIX_DIR; // !!需要修改!!
		File matrixDirFile = new File(matrixFilePath);
		if (!matrixDirFile.exists()) {
			matrixDirFile.mkdirs();
		}
		File matrixFile = new File(matrixFilePath + Constant.FILE_SEPARATOR + matrixFileName);
		if (matrixFile.exists() && !matrixFile.isDirectory()) {
			matrixFile.delete(); // 如果原来matrixFile文件已存在，则先删除原有的文件
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
		// 遍历文档+代码的全体文件描述符
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
							System.out.println(">>>> Error: Read wds file fail! keyValue.length = " + keyValue.length);
							System.out.println("\t line = " + line);
							return;
						}
						int value = Integer.valueOf(keyValue[1]);
						for (int i = 0; i < value; i++) {
							content += keyValue[0] + " ";
						}
					}
					///////////////
					content = content.replaceAll("\\s+", " "); // 把内容转换为一行以空格隔开的词语
					content = content.trim();
					content += "\r\n";
					// write into the matrixFile
//					BufferedWriter bw = new BufferedWriter(new FileWriter(matrixFile, true)); // FileWriter的第二个参数(boolean值)为true
//					                                                                          // 表示以 追加的方式写入
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(matrixFile, true), "UTF-8"));
					bw.write(content);
					bw.flush();
					bw.close();
					// 设置文档描述符中matrixIndex的值
					dd.setMatrixIndex(matrixIndex++);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
//		System.out.println(">>>>>>>>>>>>>>>  inputMatrix 生成完毕，可以开始实验一!");
//		2012-4-6 写inputMatrix.txt成功，继续工作，将其加入到LDA模型中
		String argStr = "-est -alpha " + Constant.estAlpha +
				            " -beta " + Constant.estBeta +
				            " -ntopics " + Constant.estNtopics +
				            " -niters " + Constant.estNiters +
				            " -savestep " + Constant.estSavestep +
				            " -twords " + Constant.estTwords +
                            " -dir " + this.matrixFilePath +
                            " -dfile " + this.matrixFileName;
		String[] args = argStr.split(" ");
		System.out.println("============>> Begin LDA Process <<=============");
		LDA.main(args);
		System.out.println("============>> LDA 模型执行完毕 <<================");
		// 继续， 计算每个词汇在每个文档段中的香农信息值
		computeShannonInfo();
		// 构建“文档-词”矩阵，矩阵中保存的值为词在文档中出现的词频
		buildMatrixForLSI();
		
		System.out.println("============>> ShannonInfo 计算完毕 <<================");
//		继续 2012-04-09, 得到文档段-单词-香农值文件
		createShannonWordsFile();
		System.out.println("============>> \"文档段-单词-香农值文件\"生成完毕 <<================");
		
//		/******** 使用VSM模型计算相关性 *******/
//		VSMProcess vsmProcess = new VSMProcess();
//		System.out.println("============>> \"开始LDA-VSM计算\"生成完毕 <<================");
//		vsmProcess.init();
//		System.out.println("============>> LDA-VSM 初始化完毕 <<================");
//		// 计算相似度
//		vsmProcess.compute();
//		System.out.println("============>> !LDA-VSM 计算完毕! <<================");
		
		/**************  LDA 使用“文档-主题”分布计算相似度  *******************/
//		VSMProcess vsmProcessTopic = new VSMProcess(Constant.LDA_TOPIC_MATRIX_FILENAME, Constant.LDA_TOPIC_RESULT_OUTPUT_FILE_PREFIX);
//		System.out.println("============>> \"开始LDA-Topic -> VSM计算\"生成完毕 <<================");
//		vsmProcessTopic.init();
//		System.out.println("============>> LDA-Topic -> VSM 初始化完毕 <<================");
//		// 计算相似度
//		vsmProcessTopic.compute();
//		System.out.println("============>> !LDA-Topic -> VSM 计算完毕! <<================");
		
		/***********  LDA-LSI计算  *************/
		String matrixLdaLsi = BuildModel.matrixShannonInfo; // 和上述VSM模型的输入文件相同
		String ldaLsiOutputName = Constant.LDA_LSI_OUTPUT_MATRIX_FILENAME;
		LSIProcess ldaLsiProc = new LSIProcess(matrixLdaLsi, ldaLsiOutputName);
		ldaLsiProc.initMatrix();
		ldaLsiProc.triggerLSIAnalysis();
		
		VSMProcess vsmProcess1 = new VSMProcess(Constant.LDA_LSI_OUTPUT_MATRIX_FILENAME, Constant.LDA_LSI_RESULT_OUTPUT_FILE_PREFIX);
		System.out.println("============>> \"开始LDA-LSI -> VSM计算\"生成完毕 <<================");
		vsmProcess1.init();
		System.out.println("============>> LDA-LSI -> VSM 初始化完毕 <<================");
		// 计算相似度
		vsmProcess1.compute();
		System.out.println("============>> !LDA-LSI -> VSM 计算完毕! <<================");
		
		/** *** LSI 模型计算 *** **/
		// 启动LSI分析过程
		LSIProcess lsiProc = new LSIProcess();
		lsiProc.initMatrix();
		lsiProc.triggerLSIAnalysis();
		
		VSMProcess vsmProcess2 = new VSMProcess(Constant.LSI_OUTPUT_MATRIX_FILENAME, Constant.LSI_RESULT_OUTPUT_FILE_PREFIX);
		System.out.println("============>> \"开始LSI -> VSM计算\"生成完毕 <<================");
		vsmProcess2.init();
		System.out.println("============>> LSI -> VSM 初始化完毕 <<================");
		// 计算相似度
		vsmProcess2.compute();
		System.out.println("============>> !LSI -> VSM 计算完毕! 整个工具分析完毕! <<================");
		
		// 打印结束时间
		Date endDate = new Date();
		String endTime = df.format(endDate);
		System.out.println("[ Start Time : " + startTime + " ]");
		System.out.println("[ End   Time : " + endTime + " ]");
	}
	
	class SwPair implements Comparable {
		private String word;
		private double shannonValue;
		
		public SwPair (String word, double shannonValue) {
			this.word = word;
			this.shannonValue = shannonValue;
		}

		/**
		 * compareTo用来比较，比较结果是逆序的（大的元素排前面）
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
		// 读入wordmap.txt
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
				bw.write(dd.getName() + " "); // 写入文档段名称
				String[] splits = line.split("\\s+");
				int len = splits.length;
				Set<SwPair> shannonWordSet = new TreeSet<SwPair>();
				for (int i = 0; i < len; i++) {
					double value = Double.valueOf(splits[i]);
					if (value == 0.0)
						continue;
					String word = wordMap.get(i); // 获得对应的单词
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
		// 读入wordmap.txt
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
					//// 记录每个文档段对应的词和相应的香农信息值
					
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
	 * buildMatrixForLSI 构建词频矩阵A:一行对应一个文档，一列对应一个词;
	 *                   A[i][j] = 第j个词在第i个文档中出现的次数
	 * @return
	 */
	private void buildMatrixForLSI() {
		// 读入wordmap.txt
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
	 * 获得每个文档段包含的词集（词+词的出现频数）
	 * @param matrixRowNo
	 * @return 文档段包含的词集（词  + 词频）
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
	 * 根据矩阵中的行号获得对应的DocumentDescriptor对象
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
	 * @param wordId: 单词在wordmap.txt中的序号
	 * @param wordMap: 依据wordmap.txt建立的“序号-词”的 哈希映射
	 * @param ddWordMap: 每个文档段包含的词集（词+词的出现频数）
	 * @return
	 */
	private int getFrequency(int wordId, HashMap<Integer, String> wordMap, HashMap<String, Integer> ddWordMap) {
		// 根据wordId在wordmap.txt中找到对应的单词
		if (wordMap.containsKey(wordId)) {
			String word = wordMap.get(wordId); // 找到对应的单词word
			// 去每个文档中查找该单词出现的频数
			if (ddWordMap.containsKey(word)) {
				return ddWordMap.get(word);
			} else {
				return 0;
			}
		}
		return 0;
	}
	
	// main 方法用来将lucene in action 书籍的word文档分割为txt文档，方便进行实验。
	// 对于已经分割好的txt文档，不再进行文档的自动分段过程，直接进行后续的操作。
	public static void main(String[] args) {
		
		String docFolderPath = "D:\\LuceneInActionDoc";
		
		BuildModel bm = new BuildModel();
		bm.createDataBase();
		DocumentAccess.docProcess(docFolderPath);
		System.out.println(">>> Word文档分割成功!");
	}
}
