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
 *  WordSegmentation ���ڽ������ķִʵ��࣬����Ҫ�������ڽ�����������ĵ��ηָ�ɶ�Ӧ�����Ĵ���
 */
public class WordSegmentation {
  /**
   * segmentWord ��������ĵ�������зִ�
   * @param docParagraph : ���ִʵ��ĵ�����
   */
  public String segmentWord(String docParagraph) {
    try {
    	if (SegTran.analyzer == null)
			SegTran.init();
      String workDirPath = ".";
      System.out.println(">>>> Begin word segmentation");
      // ʹ������뷭�����ƵĴ�����
      String splitWords = SegTran.ChiEng2Chi(docParagraph);
      
      splitWords = cleanSplitWords(splitWords);
      System.out.println("---------- < �ִʽ�� > : \n" + splitWords);
      // �����Ƶ
      wordFrequency(splitWords);
      System.out.println("---------- < key word > :");
      
      return splitWords;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  /**
   * ��������ĵ��е����磺crates_component��component.java����ʽ��
   * ��������"_"�ִʣ��Լ�ȥ��".java"
   */
  private String cleanSplitWords (String splitWords) {
	  StringBuilder retStr = new StringBuilder("");
	  String[] words = splitWords.split("\\s+");
	  for (String word : words) {
		  if (word.matches("^[0-9\\.]+.*")) {
			  word = word.replaceAll("[0-9\\.]", "");
		  }
		  if (word.matches("^[\\w\\.]+$")) { // Ӣ�Ĵ�
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
   * wordFrequency ͳ�Ʒִʽ����ÿ���ʳ��ֵ�Ƶ��
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
    // ��HashMap�е���Ŀ����valueֵ�����򣬼����մ�Ƶ��С��������ÿ��word
    ArrayList<String> keys = new ArrayList<String>(wordsFrequencyMap.keySet());
    Collections.sort(keys, new Comparator<Object>() {
      @Override
      public int compare(Object o1, Object o2) {
        // TODO Auto-generated method stub
        // ����value�Ĵ�С��������
        if (Integer.parseInt(wordsFrequencyMap.get(o1).toString()) < 
            Integer.parseInt(wordsFrequencyMap.get(o2).toString())) {
          return 1;  // ���Ҫ�������У���С�ںŻ��ɴ��ں�
        } else if (Integer.parseInt(wordsFrequencyMap.get(o1).toString()) == 
            Integer.parseInt(wordsFrequencyMap.get(o2).toString())) {
          return 0;
        } else
          return -1;
      }
    });
    // ���������word
    Iterator<String> keysIterator = keys.iterator();
    int i = 1;
    while (keysIterator.hasNext()) {
      String key = keysIterator.next();
      System.out.println(i + ": " + "key = " + key + "\t value = " + wordsFrequencyMap.get(key));
    }
  }
  
  public static void main(String args[]) {
    WordSegmentation ws = new WordSegmentation();
//    String docPara = "�ݱ������ҿ�ɭ������ѧλ�������б�����������ݵĵҿ�ɭ�У���һ�������ۺ��Դ�ѧ�����й�����ѧУǩ�������Э�顣2003���𣬹���816������ѧ�������˸�У�Ĺ��ʿγ̣�Ȼ�������鷢�֣�����743��ѧ���ĵ����ļ��������⣻����������ѧλ��410��ѧ���У�ֻ��10λ��������еı��޿γ̣�120���ڶ�ѧ���У���39��ѧ���������пγ̲����ʸ��ȡѧλ��student��ѧ��֮ǰ�ѻ�ù�����֤���ѧλ��";
//    String docPara = "ĵ��԰����";
//    String docPara = "��·͸�籨����ӡ���������������һ��Ա���ڶ�(29��)��ʾ��"  
//                 "�����и�������ʱ��27�ճ�5ʱ53�ַ���������6.2�������Ѿ��������5427��������"  
//                 "20000�������ˣ���20�����޼ҿɹ顣";
//    String docPara = "6/";
    String docPara = "��ͼ�е�(Dispatch)���ActiveXComponent����Ǹ�Jar���е���";
    String[] enStrs = docPara.split("[\\u4E00-\\u9FA5\\s]+");
    int id = 0;
    for (String str : enStrs) {
    	if (!str.isEmpty())
    		System.out.println(id++ + " str = " + str);
    }
    
    String aStr = "WordSegment.java����_WordSegment._java����word.java.java����_.����_abc.����_abc.a";
    String[] enWords = aStr.split("[\\u4E00-\\u9FA5\\s]+");
    for (String word : enWords) {
    	if (word.matches("[\\w.]+"))
    		System.out.println(word);
    }
    
//    ws.segmentWord(docPara);
  }
}