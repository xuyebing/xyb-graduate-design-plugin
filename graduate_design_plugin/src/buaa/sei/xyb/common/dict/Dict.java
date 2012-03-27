package buaa.sei.xyb.common.dict;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.internal.layout.Row;

import buaa.sei.xyb.common.Constant;

public class Dict {

	public static HashMap<String,String> dict=new HashMap();
	public static HashMap<String,String> dateDict=new HashMap();
	public static void insertItemsIntoDateDict(String dateDictFile) throws IOException
	{
		FileReader rd=new FileReader(dateDictFile);
		BufferedReader br=new BufferedReader(rd);
		String line=null;
		while((line=br.readLine())!=null)
		{
			String[] item=line.split("=");
			if(dateDict.containsKey(item[0]))
			{
				String v=dateDict.get(item[0]);
				v+=" "+item[1];
				dateDict.put(item[0], v);
			}
			else
			dateDict.put(item[0], item[1]);
		}
	}
	public static void insertItemsIntoDict() throws ClassNotFoundException, SQLException  //向字典或者分词词典中添加条目吧
	{
		 Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
	     Statement stmt=null;
	     ResultSet rs=null;
	   //  String strurl="jdbc:odbc:driver={Microsoft Access Driver (*.mdb)};DBQ=C:\\db1.mdb"; 
	     //String strurl="jdbc:odbc:driver={Microsoft Access Driver (*.mdb)};DBQ=tools/database/db1.mdb"; 
		 String strurl="jdbc:odbc:driver={Microsoft Access Driver (*.mdb)};DBQ="+Constant.getToolPath()+"database\\db1.mdb";
		 System.out.println("database init......");
	     Connection conn=DriverManager.getConnection(strurl) ; 
		 stmt = conn.createStatement();
		 String[] str={"s","2","5"};
		 for(int i=0;i<str.length;i++)
		 {
		 rs=stmt.executeQuery("select * from word"+str[i]);
		 while(rs.next())
		 {
			 String eng=rs.getString("eng");
			 String chi=rs.getString("chi");

			 if(!dict.containsKey(eng))
			      dict.put(eng, chi);
			 else
			 {
				 String temp=dict.get(eng);
				 if(temp.contains(chi))
					 continue;
				 else if(chi.contains(temp))
					 dict.put(eng, chi);
					 else
						 dict.put(eng, chi+dict.get(eng));
			 }
		 }
		 }
	}
	public static void main(String[] args)
	{
		System.out.print(System.getProperty("file.separator"));
		Dict d=new Dict();
		try {
			d.insertItemsIntoDict();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(d.dict.get("English"));
		System.out.println(d.dict.get("China"));
		
	}
//	public static void insertItemsIntoDateDict2(String dataDictString) throws JComException {
//		// TODO Auto-generated method stub
//		MsWordHandler.start();
//		MsWordHandler.Add2TableMap(new File(dataDictString));
//		System.out.println(MsWordHandler.tablesmap.size());
//		System.out.println(MsWordHandler.tablesmap.keySet().toString()+"  "+MsWordHandler.tablesmap.values().toString());
//		
//		String ss=new File(dataDictString).getName();
//		String s2=ss.substring(0,ss.indexOf(".doc"));
//		System.out.println(s2);
//		insertTable2WordAndTransDict(s2,MsWordHandler.tablesmap);
//		//insertTable2TransDict(dataDictString,MsWordHandler.tablesmap);
//		MsWordHandler.quit();
//	}
//	private static void insertTable2TransDict(String dataDictString,
//			HashMap<String, Tables> tablesmap) {
//		// TODO Auto-generated method stub
//		Tables ts=tablesmap.get(dataDictString);
//		
//		try {
//			if(ts.getCount()>0)
//			for(int i=1;i<=ts.getCount();i++)
//			{
//				Table t=ts.Item(i);
//				Rows rs=t.getRows();
//				if(t.getRows().getCount()>0)
//				for(int j=1;j<=t.getRows().getCount();j++)
//				{
//					
//					Row r=rs.Item(j);
//				    Cells cs=r.getCells();
//					if(cs.getCount()>0)
//					{
//						String chi=cs.Item(1).getRange().getText().trim();
//						String eng=cs.Item(2).getRange().getText().trim();
//						//System.out.println(eng+"  "+chi);
//						dateDict.put(eng,chi);
//					}
//				}
//			}
//		} catch (JComException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	private static void insertTable2WordAndTransDict(String dataDictString,
//			HashMap<String, Tables> tablesmap) {
//		// TODO Auto-generated method stub
//		Tables ts=tablesmap.get(dataDictString);
//		try {
//			if(ts.getCount()>0)
//			for(int i=1;i<=ts.getCount();i++)
//			{
//				Table t=ts.Item(i);
//				Rows rs=t.getRows();
//				if(t.getRows().getCount()>0)
//				for(int j=1;j<=t.getRows().getCount();j++)
//				{
//					
//					Row r=rs.Item(j);
//				    Cells cs=r.getCells();
//					if(cs.getCount()>0)
//					{
//						String chi=cs.Item(1).getRange().getText().trim();
//						String eng=cs.Item(2).getRange().getText().trim();
//						System.out.println(eng+"  "+chi);
//						dateDict.put(eng,chi);
//						SegTran.analyzer.addWord(chi);
//					}
//				}
//			}
//		} catch (JComException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
