package buaa.sei.xyb.common;
/**
 * @author Xu Yebing
 * Constant 包含analyse.document包中所有类所使用的常量定义
 */
public class Constant {

  public static final String FILE_SEPARATOR = System.getProperty("file.separator");
  public static final String SEGMENT_DIR = "je"; // 保存每个文档段的分词结果的文件夹
  public static final String FILTERED_DIR = "filtered"; // 保存过滤后的文本段词语集合的目录
  public static final String CODE_DIR = "code"; // 保存代码段提取出的词汇的文件夹名称
  public static final String MATRIX_DIR = "matrix"; // LDA模型输出结果
  public static int globalCategoryID = 0; // 用于生成每个文档所属的类别
  public static String srcCodeProjectName = null; // 用于保存待分析的源代码项目名
  public static String softwareDocFolder = null; // 用于保存待分析的软件文档
  public static String tempFolder = null; // 用于保存中间文件（如划分后的文档段）的文件夹路径
  public static String toolPath = null; 
  public static String workingFolder = null; // 用于保存相似度计算结果的文件夹路径
  public static boolean doExpansion = true;
  public static String dataDictPath = null; // 保存数据词典的绝对路径
  public static String cnStopWordsFilePath = null; //
  
  // 2012-08-06 添加
  public static String codeAnalysisLog = null; // 保存代码段分析时的日志文件，用于分析提高翻译精度
  public static final String LOG_NAME = "codeAnalysis.log"; // log文件的固定名称
  // 2012-08-22 添加
  public static final String CODE_TRANSLATE_DIR = "translate"; // 保存代码文件英文词汇翻译情况的文件夹名称
  
  // 2012-11-20 添加
  public static boolean notSplitDoc = false; // false：需要进行文档的自动分割； true：不需要自动分割文档。
  
  // LDA Est Arguments constant variant
  public static double estAlpha = 0.5;
  public static double estBeta = 0.1;
  public static int estNtopics = 50;
  public static int estNiters = 500;
  public static int estSavestep = 100;
  public static int estTwords = 20;
  // LSI 模型相关参数
  public static final String LSI_RESULT_OUTPUT_FILE_PREFIX = "lsiresult_";
  public static final String LSI_OUTPUT_MATRIX_FILENAME = "LSI-output.txt";
  // LDA 分析结果 使用LSI模型进行处理时的相关参数
  public static final String LDA_LSI_RESULT_OUTPUT_FILE_PREFIX = "lda_lsi_result_";
  public static final String LDA_LSI_OUTPUT_MATRIX_FILENAME = "LDA_LSI-output.txt";
  // LDA 使用 “文档-主题”分布值进行相似度计算
  public static final String LDA_TOPIC_RESULT_OUTPUT_FILE_PREFIX = "lda_topic_result_";
  public static final String LDA_TOPIC_MATRIX_FILENAME = "model-final.theta";
  
  public static String getDictPath() {
	  if (toolPath == null)
		  return null;
	  if (!toolPath.endsWith(FILE_SEPARATOR))
		  toolPath = toolPath + FILE_SEPARATOR;
	  return toolPath + "dict" + FILE_SEPARATOR;
  }
  public static String getToolPath() {
	  if (toolPath == null)
		  return null;
	  if (!toolPath.endsWith(FILE_SEPARATOR))
		  toolPath = toolPath + FILE_SEPARATOR;
	  return toolPath;
  }
}