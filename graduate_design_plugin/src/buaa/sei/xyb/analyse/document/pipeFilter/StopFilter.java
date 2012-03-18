package buaa.sei.xyb.analyse.document.pipeFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
/**
 * 
 * @author Xu Yebing
 * StopFilter类，用于对中文分词结果进行停用词过滤，去除其中停用的词汇
 *
 */
public class StopFilter {
  private Set<String> stopWordSet = null; // 停用词集合
  /**
   * initStopWordSet 初始化停用词集合,
   * @param stopWordFilePath 包含停用词的文件的绝对路径
   * @return 初始化成功返回true,失败返回false
   * @throws IOException 
   */
  public boolean initStopWordSet(String stopWordFilePath) throws IOException {
    File stopWordFile = new File(stopWordFilePath);
    if (!stopWordFile.exists() || stopWordFile.isDirectory() || stopWordFile.isHidden())
      return false;
    // 文件中一行是一个停用词（为了保证可扩展性，采用正则表达式"\\s"进行划分）
    BufferedReader br = new BufferedReader(new FileReader(stopWordFile));
    String lineContent;
    while ((lineContent = br.readLine()) != null) {
      String[] lineWords = lineContent.split("\\s");
      if (lineWords.length > 0) {
        if (stopWordSet == null)
          stopWordSet = new HashSet<String>();
        stopWordSet.addAll(Arrays.asList(lineWords)); // 将String数组lineWords中的元素全部添加到stopWordSet中
      }
    }
    br.close();
    return true;
  }
  /**
   * filterStopWord 对每个文档段的分词结果进行去停用词处理
   * @param docWordsContent 文档段分词后的内容
   * @return 分词后的结果,返回后将被写入文件中
   */
  public String filterStopWord(String docWordsContent) {
    if (stopWordSet == null) { // 没有停用词
      System.out.println("======>> 没有停用词 <<=======");
      return docWordsContent;
    }
    // 对docWordsContent进行划分，取得每个词语，判断该词语是否被包含在停用词集合中，如果是，则删除
    String filteredContent = ""; // 保存过滤结果，用于返回
    String[] docWords = docWordsContent.split("\\s");
    for (String word : docWords) {
      // 将不是数字和符号组成的串(NumWithPunctuate)、不是停用词的词语加入filteredContent中
      if (!NumberFilter.isNumWithPunctuate(word) && !stopWordSet.contains(word)) 
        filteredContent += word + " ";
    }
    return filteredContent.trim();
  }
}