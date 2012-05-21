package buaa.sei.xyb.analyse.document;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import buaa.sei.xyb.common.Constant;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class WordDocParser {

	public static String tempDir = "tempDir";
	private static String documentDir = ""; // �������д��������ĵ���Ŀ¼
	public static String copyName; // ��������ļ����ƺ�ľ���·����
								   // �Ӷ���֤������Ĵ���Ӱ��ԭ�ļ���
	public static Vector<Integer> pOutlineLevel = new Vector<Integer>();
	public static Vector<Integer> tableAtDoc = new Vector<Integer>();// ����ÿ��������ĵ��еĶ����
	
	public void analyze(String docFileName, String resultPath) throws IOException {
		splitToSmallFiles(docFileName, resultPath);
	}
	/**
	 *  ���ļ��ָ�ɺ��ʵĴ�С�����ҽ��ָ���С�ļ���ŵ�ָ����Ŀ¼��
	 * �ָ����ļ�������Ϊ"ԭ�ļ���_index.txt"�����е�index��ʾ����
	 * @param docFile ���ָ���ļ�
	 * @param outPutPath ��ŷָ���С�ļ���Ŀ¼
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
			
			DocInfo di = new DocInfo();
			di.absPath = outPutName;
			di.offset = offset;
			di.length = str.length();
			di.parentName = docFile;
			
			DocumentAccess.docLocationMap.put(docName + "_" + filePointer, di); // ���ĵ�����Ϣ��ӵ�docLocationMap�У�����˫���鿴ʱ(show related docs)ʹ��
			
			System.out.println(outPutName + "; fatherFile = " + docFile + "; offset =  " + offset + "; docPart.length = " + str.length());
			offset += str.length();
			// ���÷ִ�����зִ�
			String words = wordSegmentation.segmentWord(str);
			words = words.replaceAll("\\s+", " ");
			// ictclasOutPutName ����ִʽ�����ļ��ľ���·��
			String ictclasOutPutName = outPutPath + Constant.FILE_SEPARATOR + Constant.SEGMENT_DIR + Constant.FILE_SEPARATOR + Constant.globalCategoryID + Constant.FILE_SEPARATOR + docName + "_" + filePointer + ".txt";
			writer = new BufferedWriter(new FileWriter(ictclasOutPutName));
			writer.write(words);
			writer.close();
			
			filePointer++;
		}
		System.out.println(" >>>> one file splited successful!");
	}
	/**
	 * spiltFile ���ĵ��ָ�����ɸ�Ƭ��
	 * @param docFile ���ָ���ĵ�
	 * @return �ָ�õ��ĵ�Ƭ�ε��б�
	 */
	private ArrayList<String> splitFile(String docFile) {
		File doc = new File(docFile);
		// ��ԭ�ĵ����ı��ͱ����з���
		String[] savePath =apartTableFromDoc(docFile);
		String[] contents = readDocByParagraph(copyName); // contents�а������зֺõĶ���
		                                                  //��һ��Ԫ����һ��
	    // ������Ҫ����docFile�еõ��Ĵ�ټ�����л��飬��ǳ�ÿ��Ŀ�ʼ�κźͽ����κţ�
		// �ٶ�ÿһ�����divide��������
		// ���麯����makeBlockInPara()
		Vector<int []> block = makeBlockInPara(pOutlineLevel);
		System.out.println("Vector<int []>block.size = "+block.size());
		
		ArrayList<String> divideResult =new ArrayList<String>();//divideResult����ָ�õ��ĵ�Ƭ��
	    Vector<int[]> vtemp = new Vector<int[]>();//vtemp����һ��ԭ�ĵ���ÿ�����ĵ�����ֹ�����
	    for(Iterator<int []> it = block.iterator(); it.hasNext();)
	    {
	    	int [] temp = it.next();//temp�����˵�ǰ��Ŀ�ʼ�ͽ��������
	    	ArrayList<String> aTemp = new ArrayList<String>();
	    	//divideResult.addAll(DocumentPartitioner.divideWord(contents,temp[0],temp[1]));//�ֿ�������ı�����  	!!!!!!!!!2010.3.8��debug
	    	aTemp = DocumentPartitioner.divideWord(contents, temp[0],temp[1]);
	    	divideResult.addAll(aTemp);
	    	//��¼�зֺ���ı���ԭ�ĵ��еĶ��䣬���������
	    	Vector<int[]> vvtem = DocumentPartitioner.array;//����һ��block �����ĵ�����ֹ�����
	    	vtemp.addAll(vvtem);
	    }
    	DocumentAccess.paraBEatDocs.add(vtemp);
    	DocumentAccess.tableStartIndex.add(divideResult.size());//divideResult.size()��ֵΪ��һ��������š�
	    //���Ŵ���������ĵ�
	    ArrayList<String> bTemp = new ArrayList<String>();
	    bTemp = changeTableToString(savePath);
	    divideResult.addAll(bTemp);
		
	    return divideResult;
	}
	/**
	 * @author Xu Yebing
	 * apartTableFromDoc()��ԭ�ĵ����ı��ͱ����з���
	 * @para String docFile �����ԭ�ĵ��ľ���·��
	 * @return String[] �ָ��ı�������ĵ�·������ԭ�ĵ����ı����ݻ�������docFile�У����Բ��÷��أ�
	 */
	private String[] apartTableFromDoc(String docFile){
		  ActiveXComponent app = new ActiveXComponent("Word.Application");
		  String[] savePath = new String[]{};// ����ָ��ı�������ĵ���·������
		  // �õ�����������ľ���·��
		  String tempStr = docFile;
		  int index = tempStr.lastIndexOf('\\');
		  int indexD = tempStr.lastIndexOf('.');
		  String fName = tempStr.substring(index + 1,indexD);//�Ӿ���·���л���ĵ�������
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
			  app.setProperty("Visible",new Variant(false));	//����word���ɼ�
			  Dispatch docs = app.getProperty("Documents").toDispatch();
				Dispatch doc = Dispatch.invoke(docs,"Open",Dispatch.Method,
						new Object[]{docFile,new Variant(false),new Variant(false)},
						new int[1]).toDispatch();
		      ///�Ƚ�doc���Ϊ��������
			      //String subDocFile = docFile.substring(0,docFile.lastIndexOf('.'));
			  copyName = null;//ÿ���Ƚ������
			  copyName = subDocFile + "_copy.doc";//�õ������ļ�������	  
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
				  Dispatch range = null;//table �ķ�Χ
				  savePath = new String[tablesCount];
				  for(int i = 1; i <= tablesCount; i++)
				  {
					  // ��ÿһ����񵥶���ŵ�һ��doc�ļ��С�
					  // ע�⣺cutһ������ԭ�ĵ��ı������һ����ʱֻ��ÿ�ζ�ȡ��ǰ�ĵ�һ����������ȷ�ġ�
					  table = Dispatch.call(tables,"Item",new Variant(1)).toDispatch();
					  range = Dispatch.get(table,"Range").toDispatch();
					  // ���������ַ���"$..$"����λ�������λ�á�
					  // Dispatch.call(range,"InsertAfter","$*VBASelection,there is a table*$");
					  // InsertAfter���²�����ı�����չ��range���У�����Ҫ���µõ�ֻ������range��
                      // range = Dispatch.get(table,"Range").toDispatch();
					  Dispatch.call(range,"Cut");
					  Dispatch document = Dispatch.call(docs,"Add").toDispatch();//�½�һ��doc�ļ�
					  Dispatch activeDocument2 = app.getProperty("ActiveDocument").toDispatch();
					  Dispatch selection2 = app.getProperty("Selection").toDispatch();
					  Dispatch textRange = Dispatch.get(selection2,"Range").toDispatch();
					  Dispatch.call(textRange,"Paste");		
					  savePath[i-1] = subDocFile + "_table_";//�½��ı������word�ĵ������ơ�
					  savePath[i-1] = savePath[i-1] + i + ".doc";
					  Dispatch.call(document,"SaveAs",new Variant(savePath[i-1]));
					  // ���������ַ���"$..$"����λ�������λ�á�@@@@@   2010.3.21
					  // Dispatch.call(range,"InsertAfter","$*VBASelection,there is a table*$\r");
					  Dispatch.call(range,"InsertAfter","$**$\r");			  
				  }
				  //���Ӧ�ý��޸ĺ���ĵ��ٴν��б���
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
	 * readDocByParagraph ��ȥ�������ĵ������ı���ȡ���õ�ÿһ�Σ�������
	 * @param doc ���������ĵ��ľ���·����
	 * @return �ĵ��Ķ��伯��
	 */
	private String[] readDocByParagraph(String doc) {
		if (doc == null) {
			System.out.println("���������ļ���ΪNULL!");
			return null;
		}
		String[] c = achieveParaAndOLLevel(doc); // ����ĵ��еĶ��估ÿ�εĴ�ټ���
		return c;
	}
	/**
	 * achieveParaAndOLLevel ����ĵ��еĶ��估ÿ�εĴ�ټ���
	 * @param filename ���������ĵ�����·����
	 * @return �ĵ��Ķ��伯��
	 */
	private String[] achieveParaAndOLLevel(String filename) {
		ActiveXComponent app = new ActiveXComponent("Word.Application");
		String[] para = null;//�����ĵ��е�ÿ������
		pOutlineLevel.removeAllElements();//���pOutlineLevel��ֹǰһ���ļ���Ӱ�졣
		tableAtDoc.removeAllElements();//ͬ��
		try{
			app.setProperty("Visible",new Variant(false));//����word���ɼ�
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
			    // ��ô�ټ���
			     pOutlineLevel.addElement(new Integer(Dispatch.get(paragraph,"OutlineLevel").toInt()));
			     Dispatch range = Dispatch.get(paragraph,"Range").toDispatch();
			     para[i-1]= Dispatch.get(range,"Text").toString();
			     /*if(para[i-1].contains("$*VBASelection,there is a table*$"))
			     {
			    	 tableAtDoc.add(i-1);// ��¼�ñ�����ڵĶ����
			     }*/
			     if(para[i-1].contains("$**$"))
			     {
			    	 tableAtDoc.add(i-1);// ��¼�ñ�����ڵĶ����
			     }
			     System.out.println("�� " + i +"�ε�����Ϊ:");
			     System.out.println("\t"+ para[i-1]);
			     if(para[i-1].equals("\r"))///!!!!!
			         System.out.println("this para Ϊ��");
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
		//SingleProcess.pOutlineLevels.add(pOutlineLevel);//��������һ���ĵ��Ĵ�ټ��𱣴浽�ܵļ�����ȥ��
		DocumentAccess.pOutlineLevels.add(ptemp);// ��������һ���ĵ��Ĵ�ټ��𱣴浽�ܵļ�����ȥ��
		Vector<Integer> ttemp = new Vector<Integer>();
		ttemp.addAll(tableAtDoc);
		DocumentAccess.tableAtDocs.add(ttemp);// ��һ���ĵ��е����б���Ӧ�Ķ���ű��浽�ܵļ�����ȥ
		System.out.println("pLevel.length =" + DocumentAccess.pOutlineLevels.size());
		for(int i = 0 ; i < DocumentAccess.pOutlineLevels.size(); i++)
		{
			Vector<Integer> temp = new Vector<Integer>();
			temp = DocumentAccess.pOutlineLevels.elementAt(i);
			System.out.println("Doc_" + i + "'s length = " + temp.size());
		}
		// ��para�����ݸ��Ƶ�contextsһ��Ԫ���У�contexts��word��λ��ʾ��ʱ��ʹ�á�
		DocumentAccess.contexts.add(para);
		return para;
	}
	/**
	 * makeBlockInPara �����ĵ�����Ĵ�ټ��𣬶��ĵ����л���(�ڿ����ٽ����ĵ��λ���)
	 * @param outlineLevel ���浱ǰ�ĵ�ÿ������Ĵ�ټ���
	 * @return int[].length = 2, ÿ����Ŀ�ʼ�κ�(int[0])�ͽ����κ�(int[1])
	 */
	public static Vector<int []> makeBlockInPara(Vector<Integer> outlineLevel){
		int oSize = outlineLevel.size();
		Vector<int []> block = new Vector<int []>();//���ڱ����Ŀ�ʼ�ͽ��������
		int [] blockBeginAndEnd = new int [2];
		int preLevel = outlineLevel.get(0);//preLevel����ǰһ������ļ���
		blockBeginAndEnd[0] = blockBeginAndEnd[1] = 1;
		int curLevel = 0;//curLevel���浱ǰ����ļ���
		for(int i = 1;i < oSize; i++){
			 curLevel = outlineLevel.get(i);
			 if(curLevel >= preLevel){
				 if(i == oSize -1)//i�Ѿ������һ���ˣ���ֱ�Ӳ������һ��
				 {
				    blockBeginAndEnd[1] = oSize;
				    block.addElement(new int[]{blockBeginAndEnd[0],blockBeginAndEnd[1]});
				 }
				 preLevel = curLevel;
				 continue;
			 }
			 else{
			     blockBeginAndEnd[1] = i ;//i����һ�εĶκ�
			     block.addElement(new int []{blockBeginAndEnd[0],blockBeginAndEnd[1]});
			     blockBeginAndEnd[0] = blockBeginAndEnd[1] = i + 1;
			     preLevel = curLevel;
			 }
		}
		return block;
	}	
	/**
	 * @author Xu Yebing
	 * changeTableToString()������ÿ����Ԫ�����ݺϲ���һ��String
	 * @param tableSavePath ��������ĵ��ľ���·��
	 * @return ���ĵ������б�����ɵ�Stringͳһ����
	 */
	public ArrayList<String> changeTableToString(String[] tableSavePath){
		ArrayList<String> tableString = new ArrayList<String>();
		ActiveXComponent app = new ActiveXComponent("Word.Application");
		for(int i = 0;i < tableSavePath.length; i++) {
		    try {
			    app.setProperty("Visible",new Variant(false));	//����word���ɼ�
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
	            String str = "";//����һ�����õ���String
	            for(int j = 1; j <= paragraphCount; j++)
				{
				   paragraph = Dispatch.call(paragraphs,"Item",new Variant(j)).toDispatch();
				   Dispatch range = Dispatch.get(paragraph,"Range").toDispatch();
				   str = str + Dispatch.get(range,"Text").toString();
				}
				str = str.trim();//ȥ��string��β�Ŀո�
				//���±������ĵ�
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
