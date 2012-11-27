package buaa.sei.xyb.common;
/**
 * @author Xu Yebing
 * Constant ����analyse.document������������ʹ�õĳ�������
 */
public class Constant {

  public static final String FILE_SEPARATOR = System.getProperty("file.separator");
  public static final String SEGMENT_DIR = "je"; // ����ÿ���ĵ��εķִʽ�����ļ���
  public static final String FILTERED_DIR = "filtered"; // ������˺���ı��δ��Ｏ�ϵ�Ŀ¼
  public static final String CODE_DIR = "code"; // ����������ȡ���Ĵʻ���ļ�������
  public static final String MATRIX_DIR = "matrix"; // LDAģ��������
  public static int globalCategoryID = 0; // ��������ÿ���ĵ����������
  public static String srcCodeProjectName = null; // ���ڱ����������Դ������Ŀ��
  public static String softwareDocFolder = null; // ���ڱ��������������ĵ�
  public static String tempFolder = null; // ���ڱ����м��ļ����绮�ֺ���ĵ��Σ����ļ���·��
  public static String toolPath = null; 
  public static String workingFolder = null; // ���ڱ������ƶȼ��������ļ���·��
  public static boolean doExpansion = true;
  public static String dataDictPath = null; // �������ݴʵ�ľ���·��
  public static String cnStopWordsFilePath = null; //
  
  // 2012-08-06 ���
  public static String codeAnalysisLog = null; // �������η���ʱ����־�ļ������ڷ�����߷��뾫��
  public static final String LOG_NAME = "codeAnalysis.log"; // log�ļ��Ĺ̶�����
  // 2012-08-22 ���
  public static final String CODE_TRANSLATE_DIR = "translate"; // ��������ļ�Ӣ�Ĵʻ㷭��������ļ�������
  
  // 2012-11-20 ���
  public static boolean notSplitDoc = false; // false����Ҫ�����ĵ����Զ��ָ true������Ҫ�Զ��ָ��ĵ���
  
  // LDA Est Arguments constant variant
  public static double estAlpha = 0.5;
  public static double estBeta = 0.1;
  public static int estNtopics = 50;
  public static int estNiters = 500;
  public static int estSavestep = 100;
  public static int estTwords = 20;
  // LSI ģ����ز���
  public static final String LSI_RESULT_OUTPUT_FILE_PREFIX = "lsiresult_";
  public static final String LSI_OUTPUT_MATRIX_FILENAME = "LSI-output.txt";
  // LDA ������� ʹ��LSIģ�ͽ��д���ʱ����ز���
  public static final String LDA_LSI_RESULT_OUTPUT_FILE_PREFIX = "lda_lsi_result_";
  public static final String LDA_LSI_OUTPUT_MATRIX_FILENAME = "LDA_LSI-output.txt";
  // LDA ʹ�� ���ĵ�-���⡱�ֲ�ֵ�������ƶȼ���
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