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
  
  // LDA Est Arguments constant variant
  public static double estAlpha = 0.5;
  public static double estBeta = 0.1;
  public static int estNtopics = 50;
  public static int estNiters = 500;
  public static int estSavestep = 100;
  public static int estTwords = 20;
  //
  
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