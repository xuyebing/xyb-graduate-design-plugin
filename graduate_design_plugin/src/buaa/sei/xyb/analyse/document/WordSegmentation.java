package buaa.sei.xyb.analyse.document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import jeasy.analysis.MMAnalyzer;

/**
 * @author Xu Yebing
 *
 *  WordSegmentation 用于进行中文分词的类，其主要方法用于将输入的中文文档段分割成对应的中文词组
 */
public class WordSegmentation {
  /**
   * segmentWord 将输入的文档段落进行分词
   * @param docParagraph : 待分词的文档段落
   */
  public String segmentWord(String docParagraph) {
    try {
      MMAnalyzer analyzer = new MMAnalyzer();
      String workDirPath = ".";
      System.out.println(">>>> Begin word segmentation");
      // 不考虑导入用户词典，直接根据系统词典进行分词 （看看效果）
      String splitWords = analyzer.segment(docParagraph, " ");
      System.out.println("---------- < 分词结果 > : \n" + splitWords);
      // 计算词频
      wordFrequency(splitWords);
      System.out.println("---------- < key word > :");
      
      return splitWords;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  /**
   * wordFrequency 统计分词结果中每个词出现的频率
   * 
   */
  public void wordFrequency(String splitWords) {
    String[] words = splitWords.split(" ");
    final HashMap<String, Integer> wordsFrequencyMap = new HashMap<String, Integer>();
    for (String word : words) {
      if (wordsFrequencyMap.containsKey(word)) {
        Integer frequency = wordsFrequencyMap.get(word);
        if (frequency == null)
          wordsFrequencyMap.put(word, 1);
        else
          wordsFrequencyMap.put(word, frequency);
      } else {
        wordsFrequencyMap.put(word, 1);
      }
    }
    // 把HashMap中的条目按照value值来排序，即按照词频大小降序排列每个word
    ArrayList<String> keys = new ArrayList<String>(wordsFrequencyMap.keySet());
    Collections.sort(keys, new Comparator<Object>() {
      @Override
      public int compare(Object o1, Object o2) {
        // TODO Auto-generated method stub
        // 按照value的大小降序排列
        if (Integer.parseInt(wordsFrequencyMap.get(o1).toString()) < 
            Integer.parseInt(wordsFrequencyMap.get(o2).toString())) {
          return 1;  // 如果要升序排列，将小于号换成大于号
        } else if (Integer.parseInt(wordsFrequencyMap.get(o1).toString()) == 
            Integer.parseInt(wordsFrequencyMap.get(o2).toString())) {
          return 0;
        } else
          return -1;
      }
    });
    // 输出排序后的word
    Iterator<String> keysIterator = keys.iterator();
    int i = 1;
    while (keysIterator.hasNext()) {
      String key = keysIterator.next();
      System.out.println(i + ": " + "key = " + key + "\t value = " + wordsFrequencyMap.get(key));
    }
  }
  
  public static void main(String args[]) {
    WordSegmentation ws = new WordSegmentation();
//    String docPara = "据报道，狄克森州立大学位于美国中北部北达科他州的狄克森市，是一所公立综合性大学，与中国不少学校签署过合作协议。2003年起，共有816名海外学生参与了该校的国际课程，然而经调查发现，其中743名学生的档案文件存在问题；被授予联合学位的410名学生中，只有10位完成了所有的必修课程；120名在读学生中，仅39名学生修完所有课程并有资格获取学位；student名学生之前已获得过其他证书或学位。";
//    String docPara = "牡丹园地铁";
//    String docPara = "据路透社报道，印度尼西亚社会事务部一官员星期二(29日)表示，"  
//                 "日惹市附近当地时间27日晨5时53分发生的里氏6.2级地震已经造成至少5427人死亡，"  
//                 "20000余人受伤，近20万人无家可归。";
    String docPara = "6/";
    ws.segmentWord(docPara);
  }
}