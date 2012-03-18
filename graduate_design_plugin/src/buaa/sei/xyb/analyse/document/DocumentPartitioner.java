package buaa.sei.xyb.analyse.document;
import java.util.ArrayList;
import java.util.Vector;
public class DocumentPartitioner {
  // �������С�ֽ���
  private static int minChar = 1000;//��sei.buaa.linktracer.views.ui��LinkTracerViewComposite.java���޸�
  //private static int minChar = 400;//��sei.buaa.linktracer.views.ui��LinkTracerViewComposite.java���޸�
  // ���������ֽ���
  private static int maxChar = 5000;
  //private static int maxChar = 3000;
  private static final int DIVIDE = 1;
  private static final int COMBINE = 2;
  private static final int DONOTHING = 0;
  // ��������ÿ�����ĵ���ԭ�ĵ��е���ʼ�ͽ��������
  public static Vector<int []> array = new Vector<int []>();
  /**
   * �������õ���С������ֽ��������ĵ��ָ�����ɸ�Ƭ��
   * @param contents ���ָ���ĵ�����; begin һ���ı���Ŀ�ʼ����ţ�end һ���ı���Ľ��������
   * @return �ָ�õ��ĵ�Ƭ��
   */
  public static ArrayList<String> divideWord(String[] contents,int begin,int end) {
    ArrayList<String> result = new ArrayList<String>();
    String segment = "";
    array.removeAllElements();//�������Ԫ��
    int [] tempt = new int[]{begin-1,0};
    for(int i = begin - 1; i <= end - 1; ++i) {//begin - 1:��Ϊ������1��ʼ����contents��0��ʼ��end - 1ͬ��
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
        result.add(new String(segment));//���з�������segment�Ķ��䣬ֱ�ӽ�����maxChar��segment��ӵ�result�ϡ�
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
