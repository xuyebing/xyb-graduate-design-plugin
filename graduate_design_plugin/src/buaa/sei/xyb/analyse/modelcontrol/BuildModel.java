package buaa.sei.xyb.analyse.modelcontrol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.eclipse.jdt.core.JavaModelException;

import buaa.sei.xyb.analyse.code.CodeAccess;
import buaa.sei.xyb.analyse.document.Constant;
import buaa.sei.xyb.analyse.document.DocumentAccess;
import buaa.sei.xyb.common.DocumentDescriptor;
import buaa.sei.xyb.common.GlobalVariant;

public class BuildModel {

	public static int matrixIndex = 0; // 用于记录创建矩阵时，每个文档所对应的矩阵的行号
	public static final String matrixFileName = "inputMatrix.txt"; // 矩阵文件名
	public static String matrixFilePath = ""; // 保存矩阵文件的路径(不包含文件名)
	private static int docNums = 0;  // 用于计算inputMatrix共有多少行（LDA的输入文件第一行的内容是行数）
	private static int codeNums = 0;
	
	public static void main(String[] args) {
		// 1. 文档段处理
		String folderSet = "D:\\毕设用例"; // 包含所有待分析软件文档的文件夹绝对路径
		DocumentAccess.docProcess(folderSet);
		// 2. 代码处理
		String projectName = "UseAST";
		try {
			CodeAccess.codeProcess(projectName);
		} catch (JavaModelException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		// 3. 建立“文档-单词”矩阵，该矩阵的一行对应一个文档，每行的内容是从文档中提取的词语集合
		matrixFilePath = DocumentAccess.resultPath + Constant.FILE_SEPARATOR + Constant.MATRIX_DIR; // !!需要修改!!
		File matrixDirFile = new File(matrixFilePath);
		if (!matrixDirFile.exists()) {
			matrixDirFile.mkdirs();
		}
		File matrixFile = new File(matrixFilePath + Constant.FILE_SEPARATOR + matrixFileName);
		if (matrixFile.exists() && !matrixFile.isDirectory()) {
			matrixFile.delete(); // 如果原来matrixFile文件已存在，则先删除原有的文件
		}
		docNums = GlobalVariant.docDescriptorList.size();
		// codeNums = GlobalVariant.codeDescriptorList.size() //留待补全
		int sumNum = docNums + codeNums;
		try {
			BufferedWriter ibw = new BufferedWriter(new FileWriter(matrixFile, true));
			String firstLine = sumNum + "\r\n";
			ibw.write(firstLine);
			ibw.flush();
			ibw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// 3.1 先加入文档集合
		for(Iterator<DocumentDescriptor> iterator = GlobalVariant.docDescriptorList.iterator();
			iterator.hasNext(); ) {
			DocumentDescriptor dd = iterator.next();
			String filePath = dd.getPath();
			File file = new File(filePath);
			if (file.exists()) {
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					String line;
					String content = "";
					while ((line = br.readLine()) != null) {
						content += line + "\n";
					}
					br.close();
					content = content.replaceAll("\\s", " "); // 把内容转换为一行以空格隔开的词语
					content = content.trim();
					content += "\r\n";
					// write into the matrixFile
					BufferedWriter bw = new BufferedWriter(new FileWriter(matrixFile, true)); // FileWriter的第二个参数(boolean值)为true
					                                                                          // 表示以 追加的方式写入
					bw.write(content);
					bw.flush();
					bw.close();
					// 设置文档描述符中matrixIndex的值
					dd.setMatrixIndex(matrixIndex++);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
}
