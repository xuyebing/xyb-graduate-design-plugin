package buaa.sei.xyb.analyse.document;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class WordDocParser {

	public static String tempDir = "tempDir";
	private static String documentDir = ""; // 包含所有待分析的文档的目录
	public static String copyName; // 保存读入文件复制后的绝对路径，
								   // 从而保证分离表格的处理不影响原文件。
	public static Vector<Integer> pOutlineLevel = new Vector<Integer>();
	public static Vector<Integer> tableAtDoc = new Vector<Integer>();// 保存每个表格在文档中的段落号
	
	public void analyze(String docFileName, String resultPath) throws IOException {
		splitToSmallFiles(docFileName, resultPath);
	}
	/**
	 *  将文件分割成合适的大小，并且将分割后的小文件存放到指定的目录中
	 * 分割后的文件的命名为"原文件名_index.txt"，其中的index表示次序
	 * @param docFile 待分割的文件
	 * @param outPutPath 存放分割后的小文件的目录
	 * @throws IOException 
	 */
	private void splitToSmallFiles(String docFile, String outPutPath) throws IOException {
		File outDir = new File(outPutPath + Constant.FILE_SEPARATOR + Constant.globalCategoryID );
		if (!outDir.exists()) {
			outDir.mkdirs();
		}
		File segOutDir = new File(outPutPath + Constant.FILE_SEPARATOR + Constant.SEGMENT_DIR + Constant.FILE_SEPARATOR + Constant.globalCategoryID);
		if (!segOutDir.exists()) {
			segOutDir.mkdirs();
		}
		int filePointer = 0;
		File doc = new File(docFile);
		
		String docName = doc.getName();
		System.out.println("^^^^^^ docName = " + docName + " ^^^^^");
		docName = docName.substring(0, docName.lastIndexOf("."));
		
		ArrayList<String> parts = splitFile(docFile);
		Iterator<String> subPartsIt = parts.iterator();
		WordSegmentation wordSegmentation = new WordSegmentation();
		int offset = 0;
		BufferedWriter writer = null;
		while(subPartsIt.hasNext()) {
			String outPutName = outPutPath + Constant.FILE_SEPARATOR + Constant.globalCategoryID + Constant.FILE_SEPARATOR + docName + "_" + filePointer + ".txt"; 
			writer = new BufferedWriter(new FileWriter(outPutName));
			String str=subPartsIt.next();
			writer.write(str);
			writer.close();
			System.out.println(outPutName + "; fatherFile = " + docFile + "; offset =  " + offset + "; docPart.length = " + str.length());
			offset += str.length();
			// 调用分词类进行分词
			String words = wordSegmentation.segmentWord(str);
			words = words.replaceAll("\\s+", " ");
			// ictclasOutPutName 保存分词结果的文件的绝对路径
			String ictclasOutPutName = outPutPath + Constant.FILE_SEPARATOR + Constant.SEGMENT_DIR + Constant.FILE_SEPARATOR + Constant.globalCategoryID + Constant.FILE_SEPARATOR + docName + "_" + filePointer + ".txt";
			writer = new BufferedWriter(new FileWriter(ictclasOutPutName));
			writer.write(words);
			writer.close();
			
			filePointer++;
		}
		System.out.println(" >>>> one file splited successful!");
	}
	/**
	 * spiltFile 将文档分割成若干个片段
	 * @param docFile 待分割的文档
	 * @return 分割好的文档片段的列表
	 */
	private ArrayList<String> splitFile(String docFile) {
		File doc = new File(docFile);
		// 将原文档的文本和表格进行分离
		String[] savePath =apartTableFromDoc(docFile);
		String[] contents = readDocByParagraph(copyName); // contents中包含了切分好的段落
		                                                  //，一个元素是一段
	    // 现在需要根据docFile中得到的大纲级别进行划块，标记出每块的开始段号和结束段号，
		// 再对每一块进行divide（）处理。
		// 划块函数：makeBlockInPara()
		Vector<int []> block = makeBlockInPara(pOutlineLevel);
		System.out.println("Vector<int []>block.size = "+block.size());
		
		ArrayList<String> divideResult =new ArrayList<String>();//divideResult保存分割好的文档片段
	    Vector<int[]> vtemp = new Vector<int[]>();//vtemp保存一个原文档的每个子文档的起止段落号
	    for(Iterator<int []> it = block.iterator(); it.hasNext();)
	    {
	    	int [] temp = it.next();//temp保存了当前块的开始和结束段落号
	    	ArrayList<String> aTemp = new ArrayList<String>();
	    	//divideResult.addAll(DocumentPartitioner.divideWord(contents,temp[0],temp[1]));//分块进行子文本划分  	!!!!!!!!!2010.3.8待debug
	    	aTemp = DocumentPartitioner.divideWord(contents, temp[0],temp[1]);
	    	divideResult.addAll(aTemp);
	    	//记录切分后的文本在原文档中的段落，不包括表格
	    	Vector<int[]> vvtem = DocumentPartitioner.array;//保存一个block 的子文档的起止段落号
	    	vtemp.addAll(vvtem);
	    }
    	DocumentAccess.paraBEatDocs.add(vtemp);
    	DocumentAccess.tableStartIndex.add(divideResult.size());//divideResult.size()的值为第一个表格的序号。
	    //接着处理保存表格的文档
	    ArrayList<String> bTemp = new ArrayList<String>();
	    bTemp = changeTableToString(savePath);
	    divideResult.addAll(bTemp);
		
	    return divideResult;
	}
	/**
	 * @author Xu Yebing
	 * apartTableFromDoc()将原文档的文本和表格进行分离
	 * @para String docFile 传入的原文档的绝对路径
	 * @return String[] 分割后的保存表格的文档路径名（原文档的文本内容还保留在docFile中，所以不用返回）
	 */
	private String[] apartTableFromDoc(String docFile){
		  ActiveXComponent app = new ActiveXComponent("Word.Application");
		  String[] savePath = new String[]{};// 保存分割后的保存表格的文档的路径名称
		  // 得到保存分离后表格的绝对路径
		  String tempStr = docFile;
		  int index = tempStr.lastIndexOf('\\');
		  int indexD = tempStr.lastIndexOf('.');
		  String fName = tempStr.substring(index + 1,indexD);//从绝对路径中获得文档的名称
		  String subTemp = tempStr.substring(0,index);
		  index = subTemp.lastIndexOf('\\');
		  if (index > 0)
			  subTemp = subTemp.substring(0,index);
		  subTemp = subTemp + Constant.FILE_SEPARATOR + tempDir;
		  File tempFile = new File(subTemp);
		  if(!tempFile.exists())
			  tempFile.mkdirs();
		  subTemp = subTemp + "\\" + fName;
		  String subDocFile = subTemp;

		  try{
			  app.setProperty("Visible",new Variant(false));	//设置word不可见
			  Dispatch docs = app.getProperty("Documents").toDispatch();
				Dispatch doc = Dispatch.invoke(docs,"Open",Dispatch.Method,
						new Object[]{docFile,new Variant(false),new Variant(false)},
						new int[1]).toDispatch();
		      ///先将doc另存为，备份用
			      //String subDocFile = docFile.substring(0,docFile.lastIndexOf('.'));
			  copyName = null;//每次先将其清空
			  copyName = subDocFile + "_copy.doc";//得到备份文件的名称	  
			  Dispatch.call(doc,"SaveAs",new Variant(copyName));
			  Dispatch.call(doc,"Close",new Variant(false));
			  ///
			  doc = Dispatch.invoke(docs,"Open",Dispatch.Method,
					   new Object[]{copyName,new Variant(false),new Variant(false)},
					   new int[1]).toDispatch();
			  Dispatch activeDocument = app.getProperty("ActiveDocument").toDispatch();
			  Dispatch selection = app.getProperty("Selection").toDispatch();
			  Dispatch tables = Dispatch.get(activeDocument,"Tables").toDispatch();
			  int tablesCount = Dispatch.get(tables,"Count").toInt();
			  System.out.println("the tables number in the doc is: " + tablesCount);
			  if(tablesCount == 0)
				  System.out.println("Doc has no tables");
			  else{
				  Dispatch table = null;
				  Dispatch range = null;//table 的范围
				  savePath = new String[tablesCount];
				  for(int i = 1; i <= tablesCount; i++)
				  {
					  // 将每一个表格单独存放到一个doc文件中。
					  // 注意：cut一个表格后，原文档的表格数减一，此时只有每次读取当前的第一个表格才是正确的。
					  table = Dispatch.call(tables,"Item",new Variant(1)).toDispatch();
					  range = Dispatch.get(table,"Range").toDispatch();
					  // 插入特殊字符串"$..$"，定位表格所在位置。
					  // Dispatch.call(range,"InsertAfter","$*VBASelection,there is a table*$");
					  // InsertAfter后，新插入的文本会扩展到range域中，所以要重新得到只含表格的range。
                      // range = Dispatch.get(table,"Range").toDispatch();
					  Dispatch.call(range,"Cut");
					  Dispatch document = Dispatch.call(docs,"Add").toDispatch();//新建一个doc文件
					  Dispatch activeDocument2 = app.getProperty("ActiveDocument").toDispatch();
					  Dispatch selection2 = app.getProperty("Selection").toDispatch();
					  Dispatch textRange = Dispatch.get(selection2,"Range").toDispatch();
					  Dispatch.call(textRange,"Paste");		
					  savePath[i-1] = subDocFile + "_table_";//新建的保存表格的word文档的名称。
					  savePath[i-1] = savePath[i-1] + i + ".doc";
					  Dispatch.call(document,"SaveAs",new Variant(savePath[i-1]));
					  // 插入特殊字符串"$..$"，定位表格所在位置。@@@@@   2010.3.21
					  // Dispatch.call(range,"InsertAfter","$*VBASelection,there is a table*$\r");
					  Dispatch.call(range,"InsertAfter","$**$\r");			  
				  }
				  //最后应该将修改后的文档再次进行保存
				  //Dispatch.call(doc,"SaveAs",new Variant(docFile));
				  Dispatch.call(doc,"SaveAs",new Variant(copyName));
			  }	
		  }catch(Exception e){
			  e.printStackTrace();
		  }finally{
			  app.invoke("Quit",new Variant[]{});
			  app.safeRelease();
		  }
		  return savePath;	
	}
	/**
	 * readDocByParagraph 对去除表格的文档进行文本读取，得到每一段，并返回
	 * @param doc 待分析的文档的绝对路径名
	 * @return 文档的段落集合
	 */
	private String[] readDocByParagraph(String doc) {
		if (doc == null) {
			System.out.println("待分析的文件名为NULL!");
			return null;
		}
		String[] c = achieveParaAndOLLevel(doc); // 获得文档中的段落及每段的大纲级别
		return c;
	}
	/**
	 * achieveParaAndOLLevel 获得文档中的段落及每段的大纲级别
	 * @param filename 待分析的文档绝对路径名
	 * @return 文档的段落集合
	 */
	private String[] achieveParaAndOLLevel(String filename) {
		ActiveXComponent app = new ActiveXComponent("Word.Application");
		String[] para = null;//保存文档中的每个段落
		pOutlineLevel.removeAllElements();//清空pOutlineLevel防止前一个文件的影响。
		tableAtDoc.removeAllElements();//同上
		try{
			app.setProperty("Visible",new Variant(false));//设置word不可见
			Dispatch docs = app.getProperty("Documents").toDispatch();
			Dispatch document = Dispatch.invoke(docs,"Open",Dispatch.Method,
					new Object[]{filename,new Variant(false),new Variant(false)},
					new int[1]).toDispatch();
			Dispatch activeDocument = app.getProperty("ActiveDocument").toDispatch();
			Dispatch paragraphs = Dispatch.get(activeDocument,"Paragraphs").toDispatch();
			int paragraphCount = Dispatch.get(paragraphs,"Count").toInt();
			if(paragraphCount == 0)
				System.out.println("Doc have no paragraphs!");
			else{
			  para = new String[paragraphCount];
              Dispatch paragraph = null;
              for(int i = 1; i <= paragraphCount; i++)
			  {
			     paragraph = Dispatch.call(paragraphs,"Item",new Variant(i)).toDispatch();
			    // 获得大纲级别
			     pOutlineLevel.addElement(new Integer(Dispatch.get(paragraph,"OutlineLevel").toInt()));
			     Dispatch range = Dispatch.get(paragraph,"Range").toDispatch();
			     para[i-1]= Dispatch.get(range,"Text").toString();
			     /*if(para[i-1].contains("$*VBASelection,there is a table*$"))
			     {
			    	 tableAtDoc.add(i-1);// 记录该表格所在的段落号
			     }*/
			     if(para[i-1].contains("$**$"))
			     {
			    	 tableAtDoc.add(i-1);// 记录该表格所在的段落号
			     }
			     System.out.println("第 " + i +"段的内容为:");
			     System.out.println("\t"+ para[i-1]);
			     if(para[i-1].equals("\r"))///!!!!!
			         System.out.println("this para 为空");
			  }
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			app.invoke("Quit",new Variant[]{});
			app.safeRelease();
		}
		Vector<Integer> ptemp = new Vector<Integer>();
		ptemp.addAll(pOutlineLevel);
		//SingleProcess.pOutlineLevels.add(pOutlineLevel);//将读到的一个文档的大纲级别保存到总的集合中去。
		DocumentAccess.pOutlineLevels.add(ptemp);// 将读到的一个文档的大纲级别保存到总的集合中去。
		Vector<Integer> ttemp = new Vector<Integer>();
		ttemp.addAll(tableAtDoc);
		DocumentAccess.tableAtDocs.add(ttemp);// 将一个文档中的所有表格对应的段落号保存到总的集合中去
		System.out.println("pLevel.length =" + DocumentAccess.pOutlineLevels.size());
		for(int i = 0 ; i < DocumentAccess.pOutlineLevels.size(); i++)
		{
			Vector<Integer> temp = new Vector<Integer>();
			temp = DocumentAccess.pOutlineLevels.elementAt(i);
			System.out.println("Doc_" + i + "'s length = " + temp.size());
		}
		// 将para的内容复制到contexts一个元素中，contexts在word定位显示的时候使用。
		DocumentAccess.contexts.add(para);
		return para;
	}
	/**
	 * makeBlockInPara 根据文档段落的大纲级别，对文档进行划块(在块内再进行文档段划分)
	 * @param outlineLevel 保存当前文档每个段落的大纲级别
	 * @return int[].length = 2, 每个块的开始段号(int[0])和结束段号(int[1])
	 */
	public static Vector<int []> makeBlockInPara(Vector<Integer> outlineLevel){
		int oSize = outlineLevel.size();
		Vector<int []> block = new Vector<int []>();//用于保存块的开始和结束段落号
		int [] blockBeginAndEnd = new int [2];
		int preLevel = outlineLevel.get(0);//preLevel保存前一个段落的级别。
		blockBeginAndEnd[0] = blockBeginAndEnd[1] = 1;
		int curLevel = 0;//curLevel保存当前段落的级别。
		for(int i = 1;i < oSize; i++){
			 curLevel = outlineLevel.get(i);
			 if(curLevel >= preLevel){
				 if(i == oSize -1)//i已经是最后一段了，则直接产生最后一块
				 {
				    blockBeginAndEnd[1] = oSize;
				    block.addElement(new int[]{blockBeginAndEnd[0],blockBeginAndEnd[1]});
				 }
				 preLevel = curLevel;
				 continue;
			 }
			 else{
			     blockBeginAndEnd[1] = i ;//i是上一段的段号
			     block.addElement(new int []{blockBeginAndEnd[0],blockBeginAndEnd[1]});
			     blockBeginAndEnd[0] = blockBeginAndEnd[1] = i + 1;
			     preLevel = curLevel;
			 }
		}
		return block;
	}	
	/**
	 * @author Xu Yebing
	 * changeTableToString()将表格的每个单元格内容合并成一个String
	 * @param tableSavePath 保存表格的文档的绝对路径
	 * @return 将文档的所有表格生成的String统一返回
	 */
	public ArrayList<String> changeTableToString(String[] tableSavePath){
		ArrayList<String> tableString = new ArrayList<String>();
		ActiveXComponent app = new ActiveXComponent("Word.Application");
		for(int i = 0;i < tableSavePath.length; i++) {
		    try {
			    app.setProperty("Visible",new Variant(false));	//设置word不可见
				Dispatch docs = app.getProperty("Documents").toDispatch();
				Dispatch doc = Dispatch.invoke(docs,"Open",Dispatch.Method,
					new Object[]{tableSavePath[i],new Variant(false),new Variant(false)},
					new int[1]).toDispatch();
				Dispatch activeDocument = app.getProperty("ActiveDocument").toDispatch();
				Dispatch paragraphs = Dispatch.get(activeDocument,"Paragraphs").toDispatch();
			    int paragraphCount = Dispatch.get(paragraphs,"Count").toInt();
			    if(paragraphCount == 0)
				    System.out.println("Doc have no paragraphs!");
	            Dispatch paragraph = null;
	            String str = "";//保存一个表格得到的String
	            for(int j = 1; j <= paragraphCount; j++)
				{
				   paragraph = Dispatch.call(paragraphs,"Item",new Variant(j)).toDispatch();
				   Dispatch range = Dispatch.get(paragraph,"Range").toDispatch();
				   str = str + Dispatch.get(range,"Text").toString();
				}
				str = str.trim();//去除string首尾的空格
				//重新保存表格文档
				Dispatch.call(doc,"SaveAs",new Variant(tableSavePath[i]));
				tableString.add(str);		  
			} catch(Exception e) {
				  e.printStackTrace();
		    }
		}
		app.invoke("Quit",new Variant[]{});
		app.safeRelease();
		return tableString;
	}
}
