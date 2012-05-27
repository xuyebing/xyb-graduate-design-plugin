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
        		//如果翻译的词长不大，太大有误差。如果是英文  把翻译加上  //原词也加上
        		if(Dict.dict.get(zes[i]).length()<=5) // 只保留翻译后的长度不超过5个汉字的词汇 
        			Ch+=Dict.dict.get(zes[i])+" ";//+" "+zhs[i]+" ";
        	}
        }
        Ch=analyzer.segment(Ch, " ");
        return Ch;
	}
}
