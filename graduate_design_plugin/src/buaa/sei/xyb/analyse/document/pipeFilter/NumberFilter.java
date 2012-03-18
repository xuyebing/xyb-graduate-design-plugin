package buaa.sei.xyb.analyse.document.pipeFilter;
import java.util.regex.Pattern;
/**
 * 
 * @author Xu Yebing
 * NumberFilter类 用于过滤分词结果中的数字和"."(包括"1.1.1"这样的形式)
 */
public class NumberFilter {
  /**
   * isNumWithPunctuate 判断参数word是否仅由"数字" , "." , "-", "_"
   * @return
   */
  public static boolean isNumWithPunctuate(String word) {
//    String regex = "[0-9.\\-_]+";
	  // \\u4E00-\\u9FA5 表示中文
	  String regex = "[^A-Za-z\\u4E00-\\u9FA5]+"; // 表示除英文和中文以外的全部字符
	  return Pattern.matches(regex, word);
  }
}