package buaa.sei.xyb.common.dict;

import java.io.IOException;
import java.sql.SQLException;

import buaa.sei.xyb.analyse.tool.BuildDataDict;
import buaa.sei.xyb.common.Constant;

import jeasy.analysis.MMAnalyzer;

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
        	
        	if(Dict.dataDict.containsKey(zes[i])) // 如果当前是一个英文串，则查看数据词典中是否有相应的对照，
        	{                                     // 如果有，则将该英文串转换为对应的中文串
        		//如果数据词典有中英文对照
        		Ch+=Dict.dataDict.get(zes[i])+" ";//+" "+zhs[i]+" ";
        	} else if (Dict.dict.containsKey(zes[i])) {
        		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
        		System.out.println("------------ enWord = " + zes[i] + " <=> cnWord = " + Dict.dict.get(zes[i]));
        		if (zes[i].length() <= 3) { // 尝试排除无关词的干扰： 不翻译长度小于等于3的英文词
        			System.out.println("-*-*-*-*- enWord : " + zes[i] + " length <= 3 , 跳过该英文词的中文翻译");
        			continue;
        		}
        		//如果翻译的词长不大，太大有误差。如果是英文  把翻译加上  //原词也加上
//        		if(Dict.dict.get(zes[i]).length()<=5) // 只保留翻译后的长度不超过5个汉字的词汇 
//        			Ch+=Dict.dict.get(zes[i])+" ";//+" "+zhs[i]+" ";
        		String cnTrans = Dict.dict.get(zes[i]).trim();
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
//        	else { // 把目前不能从“数据词典”和“英汉对照词典”中翻译出来的英文词输出，找规律
//        		Ch += zes[i] + " ";
//        	}
        }
        System.out.println("@@@@@@@@@@@@@@@@@@@@----***** Ch 分词前 = " + Ch);
        Ch=analyzer.segment(Ch, " ");
        System.out.println("@@@@@@@@@@@@@@@@@@@@----***** Ch 分词后 = " + Ch);
        return Ch;
	}
}
