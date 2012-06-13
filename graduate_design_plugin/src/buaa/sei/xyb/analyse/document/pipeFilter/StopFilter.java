package buaa.sei.xyb.analyse.document.pipeFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
/**
 * 
 * @author Xu Yebing
 * StopFilter�࣬���ڶ����ķִʽ������ͣ�ôʹ��ˣ�ȥ������ͣ�õĴʻ�
 *
 */
public class StopFilter {
  private Set<String> stopWordSet = null; // ͣ�ôʼ���
  /**
   * initStopWordSet ��ʼ��ͣ�ôʼ���,
   * @param stopWordFilePath ����ͣ�ôʵ��ļ��ľ���·��
   * @return ��ʼ���ɹ�����true,ʧ�ܷ���false
   * @throws IOException 
   */
  public boolean initStopWordSet(String stopWordFilePath) throws IOException {
    File stopWordFile = new File(stopWordFilePath);
    if (!stopWordFile.exists() || stopWordFile.isDirectory() || stopWordFile.isHidden())
      return false;
    // �ļ���һ����һ��ͣ�ôʣ�Ϊ�˱�֤����չ�ԣ�����������ʽ"\\s"���л��֣�
    BufferedReader br = new BufferedReader(new FileReader(stopWordFile));
    String lineContent;
    while ((lineContent = br.readLine()) != null) {
      String[] lineWords = lineContent.split("\\s");
      if (lineWords.length > 0) {
        if (stopWordSet == null)
          stopWordSet = new HashSet<String>();
        stopWordSet.addAll(Arrays.asList(lineWords)); // ��String����lineWords�е�Ԫ��ȫ����ӵ�stopWordSet��
      }
    }
    br.close();
    return true;
  }
  /**
   * filterStopWord ��ÿ���ĵ��εķִʽ������ȥͣ�ôʴ���
   * @param docWordsContent �ĵ��ηִʺ������
   * @return �ִʺ�Ľ��,���غ󽫱�д���ļ���
   */
  public String filterStopWord(String docWordsContent) {
    if (stopWordSet == null) { // û��ͣ�ô�
      System.out.println("======>> û��ͣ�ô� <<=======");
      return docWordsContent;
    }
    // ��docWordsContent���л��֣�ȡ��ÿ������жϸô����Ƿ񱻰�����ͣ�ôʼ����У�����ǣ���ɾ��
    String filteredContent = ""; // ������˽�������ڷ���
    String[] docWords = docWordsContent.split("\\s");
    for (String word : docWords) {
      // ���������ֺͷ�����ɵĴ�(NumWithPunctuate)������ͣ�ôʵĴ������filteredContent��
      if (!NumberFilter.isNumWithPunctuate(word) && !stopWordSet.contains(word)) {
    	  // �Ƴ�word�е����֣����磺"1.1.1��", �����:"��"
    	  if (word.matches("^[0-9\\.]+.*"))
    		  word = word.replaceAll("[0-9\\.]+", "");
    	  filteredContent += word + " ";
      }
    }
    return filteredContent.trim();
  }
  /**
   * ����CodeAccess���У��Է����Ľ������"ȥͣ�ô�"�Ĳ���
   * @param words
   * @return ȥ��ͣ�ôʺ�ļ���
   */
  public HashMap<String, Integer> filterStopWord(HashMap<String, Integer> words) {
	  Iterator<String> iterator = words.keySet().iterator();
	  while (iterator.hasNext()) {
		  String key = iterator.next();
		// ���������ֺͷ�����ɵĴ�(NumWithPunctuate)������ͣ�ôʵĴ������filteredContent��
	      if (NumberFilter.isNumWithPunctuate(key) || stopWordSet.contains(key)) { // ��ͣ�ô�ȥ��
	    	  iterator.remove();
	    	  words.remove(key);
	      }
	  }
	  return words;
  }
}