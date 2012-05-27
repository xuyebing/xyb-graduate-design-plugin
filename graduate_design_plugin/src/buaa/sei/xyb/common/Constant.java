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