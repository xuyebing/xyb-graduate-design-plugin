package buaa.sei.xyb.analyse.document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import buaa.sei.xyb.common.dict.SegTran;

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
    	if (SegTran.analyzer == null)
			SegTran.init();
      String workDirPath = ".";
      System.out.println(">>>> Begin word segmentation");
      // 使用与代码翻译类似的处理方法
      String splitWords = SegTran.ChiEng2Chi(docParagraph);
      
      splitWords = cleanSplitWords(splitWords);
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
   * 针对中文文档中的形如：crates_component和component.java的形式，
   * 考虑利用"_"分词，以及去除".java"
   */
  private String cleanSplitWords (String splitWords) {
	  StringBuilder retStr = new StringBuilder("");
	  String[] words = splitWords.split("\\s+");
	  for (String word : words) {
		  if (word.matches("^[0-9\\.]+.*")) {
			  word = word.replaceAll("[0-9\\.]", "");
		  }
		  if (word.matches("^[\\w\\.]+$")) { // 英文串
			  word = word.replace(".*\\.java$", "");
			  if (!word.equals("")) {
				  String[] tmpWs = word.split("[_\\.]");
				  for (String tmpW : tmpWs) {
					  retStr.append(tmpW + " ");
				  }
			  }
		  } else {
			  retStr.append(word + " ");
		  }
	  }
	  String retS = retStr.toString();
	  retS = retS.replaceAll("\\s+", " ");
	  retS = retS.trim();
	  return retS;
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
          wordsFrequencyMap.put(word, frequency+1);
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
//    String docPara = "6/";
    String docPara = "类图中的(Dispatch)类和ActiveXComponent类就是该Jar包中的类";
    String[] enStrs = docPara.split("[\\u4E00-\\u9FA5\\s]+");
    int id = 0;
    for (String str : enStrs) {
    	if (!str.isEmpty())
    		System.out.println(id++ + " str = " + str);
    }
    
    String aStr = "WordSegment.java等于_WordSegment._java等于word.java.java等于_.等于_abc.等于_abc.a";
    String[] enWords = aStr.split("[\\u4E00-\\u9FA5\\s]+");
    for (String word : enWords) {
    	if (word.matches("[\\w.]+"))
    		System.out.println(word);
    }
    
//    ws.segmentWord(docPara);
  }
}