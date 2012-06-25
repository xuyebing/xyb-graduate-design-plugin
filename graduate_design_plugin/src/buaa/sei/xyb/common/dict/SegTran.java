package buaa.sei.xyb.common.dict;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import jeasy.analysis.MMAnalyzer;
import buaa.sei.xyb.analyse.tool.BuildDataDict;
import buaa.sei.xyb.common.Constant;
import buaa.sei.xyb.database.CreateSynonymDict;
import buaa.sei.xyb.database.DataBaseOperation;

public class SegTran {

	public static MMAnalyzer analyzer = null;
	public static void init() {
		analyzer = new MMAnalyzer();
		// 初始化字典
		try {
			Dict.insertItemsIntoDict();
			if (Constant.dataDictPath != null) { // 用户选择了数据词典
				BuildDataDict.createDataDict(Constant.dataDictPath);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String ChiEng2Chi(String CE) throws IOException
	{
		String tCE = new String(CE); // 保留输入的字符串
		String Ch="";
        //MMAnalyzer.clear();
        
        CE=analyzer.segment(CE, " ");
        
      //  System.out.println(Zh);
        /*
        HashMap<String , String > map=new HashMap();
        FileReader reader=new FileReader("c:\\zh-eng.txt");
        BufferedReader buf=new BufferedReader(reader);
        String temp=buf.readLine();
        while(temp!=null)
        {
        	String [] arr=temp.split("\\=");
        //	System.out.print("split:");
        //	System.out.println(arr.length);
        	map.put(arr[0], arr[1]);
        	temp=buf.readLine();
        }
        */
        String []zes=CE.split(" ");
        for(int i=0;i<zes.length;i++)
        {
        	if(zes[i].matches("[\\u4E00-\\u9FA5]+")) // 匹配中文字符
			{
				Ch+=zes[i]+" ";
				continue;
			}
        	String[] tceStrs = tCE.split("(?i)"+zes[i]); // 利用英文串分割原字符串
        	int id1 = tceStrs[0].length(); // 获得第一个子字符串的长度，即该英文词串在原字符串中的起始位置
        	String oldEnStr = tCE.substring(id1, id1+zes[i].length()); // oldEnStr: 保留了大小写信息的英文字符串
        	String[] shorterEnStr = oldEnStr.split("_|\\.");
        	ArrayList<String> enWds = new ArrayList<String> (); // 保存所有的单词
        	for (String shorterEn : shorterEnStr) {
        		int lens = shorterEn.length();
        		int st = 0;
        		for (int k = 1 ; k < lens; k++) {
        			if ((shorterEn.charAt(k) >= 'A' && shorterEn.charAt(k) <= 'Z')
        					&& (shorterEn.charAt(k-1) < 'A' && shorterEn.charAt(k-1) > 'Z')) {
        				String oneEn = shorterEn.substring(st, k-1);
        				enWds.add(oneEn);
        				st = k;
        			}
        		}
        		enWds.add(shorterEn);
        	}
        	
        	if(Dict.dataDict.containsKey(zes[i].toLowerCase())) // 如果当前是一个英文串，则查看数据词典中是否有相应的对照，
        	{                                     // 如果有，则将该英文串转换为对应的中文串
        		//如果数据词典有中英文对照
        		Ch+=Dict.dataDict.get(zes[i])+" ";//+" "+zhs[i]+" ";
        		continue;
        	} else {
        		for (String enWd : enWds) {
        			if (Dict.dataDict.containsKey(enWd)) {
        				Ch+=Dict.dataDict.get(zes[i])+" ";
        				continue;
        			} else if (Dict.dict.containsKey(enWd)) {
		        		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
		        		System.out.println("------------ enWord = " + enWd + " <=> cnWord = " + Dict.dict.get(enWd));
		        		if (enWd.length() <= 3) { // 尝试排除无关词的干扰： 不翻译长度小于等于3的英文词
		        			System.out.println("-*-*-*-*- enWord : " + enWd + " length <= 3 , 跳过该英文词的中文翻译");
		        			continue;
		        		}
		        		//如果翻译的词长不大，太大有误差。如果是英文  把翻译加上  //原词也加上
		//        		if(Dict.dict.get(zes[i]).length()<=5) // 只保留翻译后的长度不超过5个汉字的词汇 
		//        			Ch+=Dict.dict.get(zes[i])+" ";//+" "+zhs[i]+" ";
		        		String cnTrans = Dict.dict.get(enWd).trim();
		        		/** 分析形如 "declaration = n.宣言(说明,声明)  宣布, 宣言, 声明 说明" 的字符串 **/
		        		if (cnTrans.matches("^[a-zA-Z\\.]+.*")) {
		        			cnTrans = cnTrans.replaceAll("[a-zA-Z\\.]+", "");
		        			int pid = cnTrans.indexOf('('); // 找英文左括号'('的位置
		        			if (pid > 0) {
		        				cnTrans = cnTrans.substring(0, pid);
		        			} else {
		        				pid = cnTrans.indexOf('（'); // 没找到英文左括号的话，尝试找中文左括号'（'
		        				if (pid > 0) {
		        					cnTrans = cnTrans.substring(0, pid);
		        				}
		        			}
		        			Ch+=cnTrans + " ";
		        		}
		        		/** 分析形如"simple = 简单的" 的字符串 **/
		        		else if (cnTrans.matches("[\\u4E00-\\u9FA5]+")) {
		        			Ch+=cnTrans + " ";
		        		}
		        	}
        		}
        	}
//        	else { // 把目前不能从“数据词典”和“英汉对照词典”中翻译出来的英文词输出，找规律
//        		Ch += zes[i] + " ";
//        	}
        }
        System.out.println("@@@@@@@@@@@@@@@@@@@@----***** Ch 分词前 = " + Ch);
        Ch=analyzer.segment(Ch, " ");
        System.out.println("@@@@@@@@@@@@@@@@@@@@----***** Ch 分词后 = " + Ch);
        return Ch;
	}
	
	/**
	 * ChiEng2Chi_SynonymDict 使用“同义词”词典，进行翻译
	 */
	public static String ChiEng2Chi_SynonymDict(String CE) throws IOException
	{
		String tCE = new String(CE); // 保留输入的字符串
		String Ch="";
        //MMAnalyzer.clear();
        
        CE=analyzer.segment(CE, " ");
        
        String []zes=CE.split(" ");
        for(int i=0;i<zes.length;i++)
        {
        	if(zes[i].matches("[\\u4E00-\\u9FA5]+")) // 匹配中文字符
			{
				Ch+=zes[i]+" ";
				continue;
			}
        	String[] tceStrs = tCE.split("(?i)"+zes[i]); // 利用英文串分割原字符串
        	int id1 = tceStrs[0].length(); // 获得第一个子字符串的长度，即该英文词串在原字符串中的起始位置
        	String oldEnStr = tCE.substring(id1, id1+zes[i].length()); // oldEnStr: 保留了大小写信息的英文字符串
        	String[] shorterEnStr = oldEnStr.split("_|\\.");
        	ArrayList<String> enWds = new ArrayList<String> (); // 保存所有的单词
        	for (String shorterEn : shorterEnStr) {
        		int lens = shorterEn.length();
        		int st = 0;
        		for (int k = 1 ; k < lens; k++) {
        			if ((shorterEn.charAt(k) >= 'A' && shorterEn.charAt(k) <= 'Z')
        					&& (shorterEn.charAt(k-1) < 'A' && shorterEn.charAt(k-1) > 'Z')) {
        				String oneEn = shorterEn.substring(st, k-1);
        				enWds.add(oneEn);
        				st = k;
        			}
        		}
        		enWds.add(shorterEn);
        	}
        	
        	if(Dict.dataDict.containsKey(zes[i].toLowerCase())) // 如果当前是一个英文串，则查看数据词典中是否有相应的对照，
        	{                                     // 如果有，则将该英文串转换为对应的中文串
        		//如果数据词典有中英文对照
        		Ch+=Dict.dataDict.get(zes[i])+" ";//+" "+zhs[i]+" ";
        		continue;
        	} else {
        		for (String enWd : enWds) {
        			if (Dict.dataDict.containsKey(enWd)) {
        				Ch+=Dict.dataDict.get(zes[i])+" ";
        				continue;
        			} else if (Dict.dict.containsKey(enWd)) {
		        		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
		        		System.out.println("------------ enWord = " + enWd + " <=> cnWord = " + Dict.dict.get(enWd));
		        		if (enWd.length() <= 3) { // 尝试排除无关词的干扰： 不翻译长度小于等于3的英文词
		        			System.out.println("-*-*-*-*- enWord : " + enWd + " length <= 3 , 跳过该英文词的中文翻译");
		        			continue;
		        		}
		        		//如果翻译的词长不大，太大有误差。如果是英文  把翻译加上  //原词也加上
		//        		if(Dict.dict.get(zes[i]).length()<=5) // 只保留翻译后的长度不超过5个汉字的词汇 
		//        			Ch+=Dict.dict.get(zes[i])+" ";//+" "+zhs[i]+" ";
		        		String cnTrans = Dict.dict.get(enWd).trim();
		        		//1： 对cnTrans,用非中文词对该字符串进行分词，得到英文词在字典中所对应的所有中文词
		        		String[] allCnWords = cnTrans.split("[^\\u4E00-\\u9FA5]+");
		        		HashSet<String> allCnSet = new HashSet<String>(); // 排除字典中重复的中文词汇
		        		for (String cnWord : allCnWords) {
		        			if (!cnWord.equals(""))
		        				allCnSet.add(cnWord);
		        		}
		        		//2： 查数据库中的translatetable表，如果enWd包含在表的某个条目的主键中，则对该条目中的中文串进行分词操作，随后，依据同义词库进行是否是同义词的判断；
		        		//   如果enWd不包含在任何一个条目的主键中，则使用原有的翻译方式。
		        		String cnStrDB = DataBaseOperation.getEnWdContext(DataBaseOperation.translate_table_name, enWd);
		        		if (cnStrDB != null && !cnStrDB.equals("")) {
		        			// 对cnStrDB进行分词
		        			String[] allDBCnWords = cnStrDB.split("[^\\u4E00-\\u9FA5]+");
		        			HashSet<String> allDBCnSet = new HashSet<String>();
		        			for (String dbCnWord : allDBCnWords) {
		        				if (!dbCnWord.equals(""))
		        					allDBCnSet.add(dbCnWord);
		        			}
		        			// 对allCnSet集合和allDBCnSet集合求交，如果交集不为空，则返回第一个元素（交集中包含多个元素的情况，还需要进一步处理）；
		        			// 如果交集为空，则使用同义词库中的标号（同义词库中每个词对应一个标号）标记两个集合中的所有元素，判断两个集合中是否有相同标号的元素，有，则将enWd翻译为该元素；
		        			// 否则，使用enWd翻译中对应的第一个中文词汇作为其翻译
		        			HashSet<String> Jset = new HashSet<String> ();
		        			Jset.addAll(allCnSet);
		        			Jset.retainAll(allDBCnSet);
		        			if (Jset.size() > 0) { // 交集不为空
		        				Iterator<String> it = Jset.iterator();
		        				Ch += it.next() + " ";
		        			} else { // 交集为空
		        				HashMap<Integer, Vector<String>> allCnMap = new HashMap<Integer, Vector<String>>();
		        				HashMap<Integer, Vector<String>> allDBCnMap = new HashMap<Integer, Vector<String>>();
		        				for(Iterator<String> it = allCnSet.iterator(); it.hasNext(); ) {
		        					String cnWd = it.next();
		        					String lines = CreateSynonymDict.getLineNo(cnWd);
		        					if (lines != null && !lines.equals("")) {
		        						String[] lineNos = lines.split(",");
		        						for (String lineNo : lineNos) {
		        							if (!lineNo.equals("")) {
		        								Integer lineInt = Integer.valueOf(lineNo);
		        								if (allCnMap.containsKey(lineInt)) {
		        									Vector<String> valueVec = allCnMap.get(lineInt);
		        									valueVec.add(cnWd);
		        									allCnMap.put(lineInt, valueVec);
		        								} else {
		        									Vector<String> valueVec = new Vector<String>();
		        									valueVec.add(cnWd);
		        									allCnMap.put(lineInt, valueVec);
		        								}
		        							}
		        						}
		        					}
		        				}
		        				for(Iterator<String> it = allDBCnSet.iterator(); it.hasNext(); ) {
		        					String cnWd = it.next();
		        					String lines = CreateSynonymDict.getLineNo(cnWd);
		        					if (lines != null && !lines.equals("")) {
		        						String[] lineNos = lines.split(",");
		        						for (String lineNo : lineNos) {
		        							if (!lineNo.equals("")) {
		        								Integer lineInt = Integer.valueOf(lineNo);
		        								if (allDBCnMap.containsKey(lineInt)) {
		        									Vector<String> valueVec = allDBCnMap.get(lineInt);
		        									valueVec.add(cnWd);
		        									allDBCnMap.put(lineInt, valueVec);
		        								} else {
		        									Vector<String> valueVec = new Vector<String>();
		        									valueVec.add(cnWd);
		        									allDBCnMap.put(lineInt, valueVec);
		        								}
		        							}
		        						}
		        					}
		        				}
		        				// 判断两个Map中是否包含相同的key
		        				Set<Integer> cnMapSet = allCnMap.keySet();
		        				Set<Integer> cnDBMapSet = allDBCnMap.keySet();
		        				Set<Integer> JJSet = new HashSet<Integer>();
		        				JJSet.addAll(cnMapSet);
		        				JJSet.retainAll(cnDBMapSet);
		        				if (JJSet.size() > 0) { // 两个Map中含有相同的key
		        					Iterator<Integer> it = JJSet.iterator();
		        					Integer lineNo = it.next();
		        					// 将字典中的中文翻译转换为文本中（保存在数据库中）的相同类（lineNo相同）的词汇
		        					Vector<String> vec = allDBCnMap.get(lineNo);
		        					assert(vec.size() > 0);
		        					String cnToTrans = vec.get(0); // 使用lineNo中的第一个词汇（！待研究！）
		        					Ch+=cnToTrans + " ";
		        				} else { // 两个Map中不包含相同的key，将字典翻译中的第一个词作为英文词的翻译
		        					Ch+=allCnWords[0] + " ";
		        				}
		        			}
		        			
		        		} else {
			        		/** 分析形如 "declaration = n.宣言(说明,声明)  宣布, 宣言, 声明 说明" 的字符串 **/
			        		if (cnTrans.matches("^[a-zA-Z\\.]+.*")) {
			        			cnTrans = cnTrans.replaceAll("[a-zA-Z\\.]+", "");
			        			int pid = cnTrans.indexOf('('); // 找英文左括号'('的位置
			        			if (pid > 0) {
			        				cnTrans = cnTrans.substring(0, pid);
			        			} else {
			        				pid = cnTrans.indexOf('（'); // 没找到英文左括号的话，尝试找中文左括号'（'
			        				if (pid > 0) {
			        					cnTrans = cnTrans.substring(0, pid);
			        				}
			        			}
			        			Ch+=cnTrans + " ";
			        		}
			        		/** 分析形如"simple = 简单的" 的字符串 **/
			        		else if (cnTrans.matches("[\\u4E00-\\u9FA5]+")) {
			        			Ch+=cnTrans + " ";
			        		}
		        		}
		        	}
        		}
        	}
//        	else { // 把目前不能从“数据词典”和“英汉对照词典”中翻译出来的英文词输出，找规律
//        		Ch += zes[i] + " ";
//        	}
        }
        System.out.println("@@@@@@@@@@@@@@@@@@@@----***** Ch 分词前 = " + Ch);
        Ch=analyzer.segment(Ch, " ");
        System.out.println("@@@@@@@@@@@@@@@@@@@@----***** Ch 分词后 = " + Ch);
        return Ch;
	}
	
	public static void main(String[] args) {
		String t = "检索树与结果构造接口/ CRATES_TreeElement";
		String dataDictPath = "D:\\ttmp_1\\CRATES数据词典.doc";
		BuildDataDict.createDataDict(dataDictPath);
		String t1 = "CRATES_TreeElement";
		String[] tceStrs = t.split("(?i)"+t1); // 利用英文串分割原字符串
    	int id1 = tceStrs[0].length(); // 获得第一个子字符串的长度，即该英文词串在原字符串中的起始位置
    	String oldEnStr = t.substring(id1, id1+t1.length());
    	String cnStrs = "n.宣言(说明,声明)  宣布, 宣言, 声明 说明";
    	String[] allCN = cnStrs.split("[^\\u4E00-\\u9FA5]+");
    	System.out.println("---------*****************-----------");
    	for (String cnWd : allCN) {
    		if (!cnWd.equals(""))
    			System.out.println("\t"+cnWd);
    	}
    	System.out.println("---------*****************-----------");

		try {
			MMAnalyzer ma = new MMAnalyzer();
			t = ma.segment(t, " ");
			System.out.println("t = " + t);
			System.out.println("oldEnStr = " + oldEnStr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
