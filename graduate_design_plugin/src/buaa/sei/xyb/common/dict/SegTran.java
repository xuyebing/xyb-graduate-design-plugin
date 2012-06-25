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
		String tCE = new String(CE); // ����������ַ���
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
        	String[] tceStrs = tCE.split("(?i)"+zes[i]); // ����Ӣ�Ĵ��ָ�ԭ�ַ���
        	int id1 = tceStrs[0].length(); // ��õ�һ�����ַ����ĳ��ȣ�����Ӣ�Ĵʴ���ԭ�ַ����е���ʼλ��
        	String oldEnStr = tCE.substring(id1, id1+zes[i].length()); // oldEnStr: �����˴�Сд��Ϣ��Ӣ���ַ���
        	String[] shorterEnStr = oldEnStr.split("_|\\.");
        	ArrayList<String> enWds = new ArrayList<String> (); // �������еĵ���
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
        	
        	if(Dict.dataDict.containsKey(zes[i].toLowerCase())) // �����ǰ��һ��Ӣ�Ĵ�����鿴���ݴʵ����Ƿ�����Ӧ�Ķ��գ�
        	{                                     // ����У��򽫸�Ӣ�Ĵ�ת��Ϊ��Ӧ�����Ĵ�
        		//������ݴʵ�����Ӣ�Ķ���
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
		        		if (enWd.length() <= 3) { // �����ų��޹شʵĸ��ţ� �����볤��С�ڵ���3��Ӣ�Ĵ�
		        			System.out.println("-*-*-*-*- enWord : " + enWd + " length <= 3 , ������Ӣ�Ĵʵ����ķ���");
		        			continue;
		        		}
		        		//�������Ĵʳ�����̫�����������Ӣ��  �ѷ������  //ԭ��Ҳ����
		//        		if(Dict.dict.get(zes[i]).length()<=5) // ֻ���������ĳ��Ȳ�����5�����ֵĴʻ� 
		//        			Ch+=Dict.dict.get(zes[i])+" ";//+" "+zhs[i]+" ";
		        		String cnTrans = Dict.dict.get(enWd).trim();
		        		/** �������� "declaration = n.����(˵��,����)  ����, ����, ���� ˵��" ���ַ��� **/
		        		if (cnTrans.matches("^[a-zA-Z\\.]+.*")) {
		        			cnTrans = cnTrans.replaceAll("[a-zA-Z\\.]+", "");
		        			int pid = cnTrans.indexOf('('); // ��Ӣ��������'('��λ��
		        			if (pid > 0) {
		        				cnTrans = cnTrans.substring(0, pid);
		        			} else {
		        				pid = cnTrans.indexOf('��'); // û�ҵ�Ӣ�������ŵĻ�������������������'��'
		        				if (pid > 0) {
		        					cnTrans = cnTrans.substring(0, pid);
		        				}
		        			}
		        			Ch+=cnTrans + " ";
		        		}
		        		/** ��������"simple = �򵥵�" ���ַ��� **/
		        		else if (cnTrans.matches("[\\u4E00-\\u9FA5]+")) {
		        			Ch+=cnTrans + " ";
		        		}
		        	}
        		}
        	}
//        	else { // ��Ŀǰ���ܴӡ����ݴʵ䡱�͡�Ӣ�����մʵ䡱�з��������Ӣ�Ĵ�������ҹ���
//        		Ch += zes[i] + " ";
//        	}
        }
        System.out.println("@@@@@@@@@@@@@@@@@@@@----***** Ch �ִ�ǰ = " + Ch);
        Ch=analyzer.segment(Ch, " ");
        System.out.println("@@@@@@@@@@@@@@@@@@@@----***** Ch �ִʺ� = " + Ch);
        return Ch;
	}
	
	/**
	 * ChiEng2Chi_SynonymDict ʹ�á�ͬ��ʡ��ʵ䣬���з���
	 */
	public static String ChiEng2Chi_SynonymDict(String CE) throws IOException
	{
		String tCE = new String(CE); // ����������ַ���
		String Ch="";
        //MMAnalyzer.clear();
        
        CE=analyzer.segment(CE, " ");
        
        String []zes=CE.split(" ");
        for(int i=0;i<zes.length;i++)
        {
        	if(zes[i].matches("[\\u4E00-\\u9FA5]+")) // ƥ�������ַ�
			{
				Ch+=zes[i]+" ";
				continue;
			}
        	String[] tceStrs = tCE.split("(?i)"+zes[i]); // ����Ӣ�Ĵ��ָ�ԭ�ַ���
        	int id1 = tceStrs[0].length(); // ��õ�һ�����ַ����ĳ��ȣ�����Ӣ�Ĵʴ���ԭ�ַ����е���ʼλ��
        	String oldEnStr = tCE.substring(id1, id1+zes[i].length()); // oldEnStr: �����˴�Сд��Ϣ��Ӣ���ַ���
        	String[] shorterEnStr = oldEnStr.split("_|\\.");
        	ArrayList<String> enWds = new ArrayList<String> (); // �������еĵ���
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
        	
        	if(Dict.dataDict.containsKey(zes[i].toLowerCase())) // �����ǰ��һ��Ӣ�Ĵ�����鿴���ݴʵ����Ƿ�����Ӧ�Ķ��գ�
        	{                                     // ����У��򽫸�Ӣ�Ĵ�ת��Ϊ��Ӧ�����Ĵ�
        		//������ݴʵ�����Ӣ�Ķ���
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
		        		if (enWd.length() <= 3) { // �����ų��޹شʵĸ��ţ� �����볤��С�ڵ���3��Ӣ�Ĵ�
		        			System.out.println("-*-*-*-*- enWord : " + enWd + " length <= 3 , ������Ӣ�Ĵʵ����ķ���");
		        			continue;
		        		}
		        		//�������Ĵʳ�����̫�����������Ӣ��  �ѷ������  //ԭ��Ҳ����
		//        		if(Dict.dict.get(zes[i]).length()<=5) // ֻ���������ĳ��Ȳ�����5�����ֵĴʻ� 
		//        			Ch+=Dict.dict.get(zes[i])+" ";//+" "+zhs[i]+" ";
		        		String cnTrans = Dict.dict.get(enWd).trim();
		        		//1�� ��cnTrans,�÷����ĴʶԸ��ַ������зִʣ��õ�Ӣ�Ĵ����ֵ�������Ӧ���������Ĵ�
		        		String[] allCnWords = cnTrans.split("[^\\u4E00-\\u9FA5]+");
		        		HashSet<String> allCnSet = new HashSet<String>(); // �ų��ֵ����ظ������Ĵʻ�
		        		for (String cnWord : allCnWords) {
		        			if (!cnWord.equals(""))
		        				allCnSet.add(cnWord);
		        		}
		        		//2�� �����ݿ��е�translatetable�����enWd�����ڱ��ĳ����Ŀ�������У���Ը���Ŀ�е����Ĵ����зִʲ������������ͬ��ʿ�����Ƿ���ͬ��ʵ��жϣ�
		        		//   ���enWd���������κ�һ����Ŀ�������У���ʹ��ԭ�еķ��뷽ʽ��
		        		String cnStrDB = DataBaseOperation.getEnWdContext(DataBaseOperation.translate_table_name, enWd);
		        		if (cnStrDB != null && !cnStrDB.equals("")) {
		        			// ��cnStrDB���зִ�
		        			String[] allDBCnWords = cnStrDB.split("[^\\u4E00-\\u9FA5]+");
		        			HashSet<String> allDBCnSet = new HashSet<String>();
		        			for (String dbCnWord : allDBCnWords) {
		        				if (!dbCnWord.equals(""))
		        					allDBCnSet.add(dbCnWord);
		        			}
		        			// ��allCnSet���Ϻ�allDBCnSet�����󽻣����������Ϊ�գ��򷵻ص�һ��Ԫ�أ������а������Ԫ�ص����������Ҫ��һ��������
		        			// �������Ϊ�գ���ʹ��ͬ��ʿ��еı�ţ�ͬ��ʿ���ÿ���ʶ�Ӧһ����ţ�������������е�����Ԫ�أ��ж������������Ƿ�����ͬ��ŵ�Ԫ�أ��У���enWd����Ϊ��Ԫ�أ�
		        			// ����ʹ��enWd�����ж�Ӧ�ĵ�һ�����Ĵʻ���Ϊ�䷭��
		        			HashSet<String> Jset = new HashSet<String> ();
		        			Jset.addAll(allCnSet);
		        			Jset.retainAll(allDBCnSet);
		        			if (Jset.size() > 0) { // ������Ϊ��
		        				Iterator<String> it = Jset.iterator();
		        				Ch += it.next() + " ";
		        			} else { // ����Ϊ��
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
		        				// �ж�����Map���Ƿ������ͬ��key
		        				Set<Integer> cnMapSet = allCnMap.keySet();
		        				Set<Integer> cnDBMapSet = allDBCnMap.keySet();
		        				Set<Integer> JJSet = new HashSet<Integer>();
		        				JJSet.addAll(cnMapSet);
		        				JJSet.retainAll(cnDBMapSet);
		        				if (JJSet.size() > 0) { // ����Map�к�����ͬ��key
		        					Iterator<Integer> it = JJSet.iterator();
		        					Integer lineNo = it.next();
		        					// ���ֵ��е����ķ���ת��Ϊ�ı��У����������ݿ��У�����ͬ�ࣨlineNo��ͬ���Ĵʻ�
		        					Vector<String> vec = allDBCnMap.get(lineNo);
		        					assert(vec.size() > 0);
		        					String cnToTrans = vec.get(0); // ʹ��lineNo�еĵ�һ���ʻ㣨�����о�����
		        					Ch+=cnToTrans + " ";
		        				} else { // ����Map�в�������ͬ��key�����ֵ䷭���еĵ�һ������ΪӢ�Ĵʵķ���
		        					Ch+=allCnWords[0] + " ";
		        				}
		        			}
		        			
		        		} else {
			        		/** �������� "declaration = n.����(˵��,����)  ����, ����, ���� ˵��" ���ַ��� **/
			        		if (cnTrans.matches("^[a-zA-Z\\.]+.*")) {
			        			cnTrans = cnTrans.replaceAll("[a-zA-Z\\.]+", "");
			        			int pid = cnTrans.indexOf('('); // ��Ӣ��������'('��λ��
			        			if (pid > 0) {
			        				cnTrans = cnTrans.substring(0, pid);
			        			} else {
			        				pid = cnTrans.indexOf('��'); // û�ҵ�Ӣ�������ŵĻ�������������������'��'
			        				if (pid > 0) {
			        					cnTrans = cnTrans.substring(0, pid);
			        				}
			        			}
			        			Ch+=cnTrans + " ";
			        		}
			        		/** ��������"simple = �򵥵�" ���ַ��� **/
			        		else if (cnTrans.matches("[\\u4E00-\\u9FA5]+")) {
			        			Ch+=cnTrans + " ";
			        		}
		        		}
		        	}
        		}
        	}
//        	else { // ��Ŀǰ���ܴӡ����ݴʵ䡱�͡�Ӣ�����մʵ䡱�з��������Ӣ�Ĵ�������ҹ���
//        		Ch += zes[i] + " ";
//        	}
        }
        System.out.println("@@@@@@@@@@@@@@@@@@@@----***** Ch �ִ�ǰ = " + Ch);
        Ch=analyzer.segment(Ch, " ");
        System.out.println("@@@@@@@@@@@@@@@@@@@@----***** Ch �ִʺ� = " + Ch);
        return Ch;
	}
	
	public static void main(String[] args) {
		String t = "��������������ӿ�/ CRATES_TreeElement";
		String dataDictPath = "D:\\ttmp_1\\CRATES���ݴʵ�.doc";
		BuildDataDict.createDataDict(dataDictPath);
		String t1 = "CRATES_TreeElement";
		String[] tceStrs = t.split("(?i)"+t1); // ����Ӣ�Ĵ��ָ�ԭ�ַ���
    	int id1 = tceStrs[0].length(); // ��õ�һ�����ַ����ĳ��ȣ�����Ӣ�Ĵʴ���ԭ�ַ����е���ʼλ��
    	String oldEnStr = t.substring(id1, id1+t1.length());
    	String cnStrs = "n.����(˵��,����)  ����, ����, ���� ˵��";
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
