package buaa.sei.xyb.common;
/**
 * @author Xu Yebing
 * Constant ����analyse.document������������ʹ�õĳ�������
 */
public class Constant {

  public static final String FILE_SEPARATOR = System.getProperty("file.separator");
  public static final String SEGMENT_DIR = "je"; // ����ÿ���ĵ��εķִʽ�����ļ���
  public static final String FILTERED_DIR = "filtered"; // ������˺���ı��δ��Ｏ�ϵ�Ŀ¼
  public static final String MATRIX_DIR = "matrix";
  public static int globalCategoryID = 0; // ��������ÿ���ĵ����������
  public static String toolPath = null; 
  public static boolean doExpansion = true;
  
  public static String getDictPath() {
	  if (!toolPath.endsWith(FILE_SEPARATOR))
		  toolPath = toolPath + FILE_SEPARATOR;
	  return toolPath + "dict" + FILE_SEPARATOR;
  }
  public static String getToolPath() {
	  if (!toolPath.endsWith(FILE_SEPARATOR))
		  toolPath = toolPath + FILE_SEPARATOR;
	  return toolPath;
  }
}