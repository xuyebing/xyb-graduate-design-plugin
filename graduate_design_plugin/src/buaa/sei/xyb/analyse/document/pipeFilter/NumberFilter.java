package buaa.sei.xyb.analyse.document.pipeFilter;
import java.util.regex.Pattern;
/**
 * 
 * @author Xu Yebing
 * NumberFilter�� ���ڹ��˷ִʽ���е����ֺ�"."(����"1.1.1"��������ʽ)
 */
public class NumberFilter {
  /**
   * isNumWithPunctuate �жϲ���word�Ƿ����"����" , "." , "-", "_"
   * @return
   */
  public static boolean isNumWithPunctuate(String word) {
//    String regex = "[0-9.\\-_]+";
	  // \\u4E00-\\u9FA5 ��ʾ����
	  String regex = "[^A-Za-z\\u4E00-\\u9FA5]+"; // ��ʾ��Ӣ�ĺ����������ȫ���ַ�
	  return Pattern.matches(regex, word);
  }
}