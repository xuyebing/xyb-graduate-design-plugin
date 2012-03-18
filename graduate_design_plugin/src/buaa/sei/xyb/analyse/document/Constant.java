package buaa.sei.xyb.analyse.document;
/**
 * @author Xu Yebing
 * Constant 包含analyse.document包中所有类所使用的常量定义
 */
public class Constant {

  public static final String FILE_SEPARATOR = System.getProperty("file.separator");
  public static final String SEGMENT_DIR = "je"; // 保存每个文档段的分词结果的文件夹
  public static final String FILTERED_DIR = "filtered"; // 保存过滤后的文本段词语集合的目录
  public static final String MATRIX_DIR = "matrix";
  public static int globalCategoryID = 0; // 用于生成每个文档所属的类别
  
}