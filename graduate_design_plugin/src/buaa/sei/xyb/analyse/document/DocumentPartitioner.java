package buaa.sei.xyb.analyse.document;
import java.util.ArrayList;
import java.util.Vector;
public class DocumentPartitioner {
  // 允许的最小字节数
  private static int minChar = 1000;//在sei.buaa.linktracer.views.ui的LinkTracerViewComposite.java中修改
  //private static int minChar = 400;//在sei.buaa.linktracer.views.ui的LinkTracerViewComposite.java中修改
  // 允许的最大字节数
  private static int maxChar = 5000;
  //private static int maxChar = 3000;
  private static final int DIVIDE = 1;
  private static final int COMBINE = 2;
  private static final int DONOTHING = 0;
  // 用来保存每个子文档在原文档中的起始和结束段落号
  public static Vector<int []> array = new Vector<int []>();
  /**
   * 根据设置的最小和最大字节数，将文档分割成若干个片段
   * @param contents 待分割的文档内容; begin 一个文本块的开始段落号；end 一个文本块的结束段落号
   * @return 分割好的文档片段
   */
  public static ArrayList<String> divideWord(String[] contents,int begin,int end) {
    ArrayList<String> result = new ArrayList<String>();
    String segment = "";
    array.removeAllElements();//清空所有元素
    int [] tempt = new int[]{begin-1,0};
    for(int i = begin - 1; i <= end - 1; ++i) {//begin - 1:因为段数从1开始，而contents从0开始，end - 1同理
      segment += contents[i] + " ";
      switch(chooseAction(segment.length())) {
      case DONOTHING:
        result.add(new String(segment));
        tempt[1] = i;
        int [] tt = tempt;
        array.add(tt);
        if(i < end -1)
            tempt[0] = i+1;//
        segment = "";
        break;
     case COMBINE:
        if(i == end - 1){
          result.add(new String(segment));
          tempt[1] = i;
          int [] tt1 = tempt;
         array.add(tt1);
        }
        break;
      case DIVIDE:
        //result.addAll(divideString(segment));
        result.add(new String(segment));//不切分最后加入segment的段落，直接将超过maxChar的segment添加到result上。
        tempt[1] = i;
        int [] tt2 = tempt;
        array.add(tt2);
        if(i < end -1)
            tempt[0] = i+1;//
        segment = "";
        break;
      default:
        break;
      }
    }
    return result;
  }
  private static int chooseAction(int length) {
    if(length > maxChar)
      return DIVIDE;
    if(length < minChar)
      return COMBINE;
    return DONOTHING;
  }
}
