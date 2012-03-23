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
import buaa.sei.xyb.analyse.document.DocumentAccess;
import buaa.sei.xyb.common.Constant;
import buaa.sei.xyb.common.DocumentDescriptor;
import buaa.sei.xyb.common.GlobalVariant;

public class BuildModel {

	public static int matrixIndex = 0; // ���ڼ�¼��������ʱ��ÿ���ĵ�����Ӧ�ľ�����к�
	public static final String matrixFileName = "inputMatrix.txt"; // �����ļ���
	public static String matrixFilePath = ""; // ��������ļ���·��(�������ļ���)
	private static int docNums = 0;  // ���ڼ���inputMatrix���ж����У�LDA�������ļ���һ�е�������������
	private static int codeNums = 0;
	private String folderSet; // �������д���������ĵ����ļ��о���·��
	private String projectName; // ����������Ŀ����
	
	public BuildModel(String folderSet, String projectName) {
		this.folderSet = folderSet;
		this.projectName = projectName;
	}
	
	
	public void build() throws JavaModelException {
		// 1. �ĵ��δ���
		// String folderSet = "D:\\��������"; // �������д���������ĵ����ļ��о���·��
		DocumentAccess.docProcess(folderSet);
		// 2. ����δ���
		CodeAccess.codeProcess(projectName);
		// 3. �������ĵ�-���ʡ����󣬸þ����һ�ж�Ӧһ���ĵ���ÿ�е������Ǵ��ĵ�����ȡ�Ĵ��Ｏ��
		matrixFilePath = DocumentAccess.resultPath + Constant.FILE_SEPARATOR + Constant.MATRIX_DIR; // !!��Ҫ�޸�!!
		File matrixDirFile = new File(matrixFilePath);
		if (!matrixDirFile.exists()) {
			matrixDirFile.mkdirs();
		}
		File matrixFile = new File(matrixFilePath + Constant.FILE_SEPARATOR + matrixFileName);
		if (matrixFile.exists() && !matrixFile.isDirectory()) {
			matrixFile.delete(); // ���ԭ��matrixFile�ļ��Ѵ��ڣ�����ɾ��ԭ�е��ļ�
		}
		docNums = GlobalVariant.docDescriptorList.size();
		// codeNums = GlobalVariant.codeDescriptorList.size() //������ȫ
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
		// 3.1 �ȼ����ĵ�����
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
					content = content.replaceAll("\\s", " "); // ������ת��Ϊһ���Կո�����Ĵ���
					content = content.trim();
					content += "\r\n";
					// write into the matrixFile
					BufferedWriter bw = new BufferedWriter(new FileWriter(matrixFile, true)); // FileWriter�ĵڶ�������(booleanֵ)Ϊtrue
					                                                                          // ��ʾ�� ׷�ӵķ�ʽд��
					bw.write(content);
					bw.flush();
					bw.close();
					// �����ĵ���������matrixIndex��ֵ
					dd.setMatrixIndex(matrixIndex++);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
}
