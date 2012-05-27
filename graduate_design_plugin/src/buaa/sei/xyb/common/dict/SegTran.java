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
		// ��ʼ���ֵ�
		try {
			Dict.insertItemsIntoDict();
			if (Constant.dataDictPath != null) { // �û�ѡ�������ݴʵ�
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
        	if(zes[i].matches("[\\u4E00-\\u9FA5]+")) // ƥ�������ַ�
			{
				Ch+=zes[i]+" ";
				continue;
			}
        	
        	if(Dict.dataDict.containsKey(zes[i])) // �����ǰ��һ��Ӣ�Ĵ�����鿴���ݴʵ����Ƿ�����Ӧ�Ķ��գ�
        	{                                     // ����У��򽫸�Ӣ�Ĵ�ת��Ϊ��Ӧ�����Ĵ�
        		//������ݴʵ�����Ӣ�Ķ���
        		Ch+=Dict.dataDict.get(zes[i])+" ";//+" "+zhs[i]+" ";
        	} else if (Dict.dict.containsKey(zes[i])) {
        		//�������Ĵʳ�����̫�����������Ӣ��  �ѷ������  //ԭ��Ҳ����
        		if(Dict.dict.get(zes[i]).length()<=5) // ֻ���������ĳ��Ȳ�����5�����ֵĴʻ� 
        			Ch+=Dict.dict.get(zes[i])+" ";//+" "+zhs[i]+" ";
        	}
        }
        Ch=analyzer.segment(Ch, " ");
        return Ch;
	}
}
