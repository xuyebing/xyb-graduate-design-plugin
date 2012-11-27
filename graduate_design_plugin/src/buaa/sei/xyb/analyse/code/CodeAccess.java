package buaa.sei.xyb.analyse.code;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import buaa.sei.xyb.analyse.document.DocumentAccess;
import buaa.sei.xyb.analyse.document.pipeFilter.StopFilter;
import buaa.sei.xyb.common.Constant;
import buaa.sei.xyb.common.DocumentDescriptor;
import buaa.sei.xyb.common.GlobalVariant;

public class CodeAccess {
	
	private static IJavaProject getJavaProject(String projectName) {
		IProject iproject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		return JavaCore.create(iproject);
	}

	public static void codeProcess(String javaProjectName) throws JavaModelException {
		IJavaProject javaProject = getJavaProject(javaProjectName);
		String projectDir = javaProject.getProject().getLocation().toOSString();
		
		JavaProjectVisitor jpVisitor = new JavaProjectVisitor(javaProject);
		ArrayList<IType> elementList = jpVisitor.getJavaFiles();
		Iterator<IType> iter = elementList.iterator();
		Constant.globalCategoryID++; // ���еĴ���ζ���Ӧ��ͬһ����ID��������һ������ζ�Ӧһ����ID
		while (iter.hasNext()) {
			IType element = iter.next();
			JavaCodeParser parser = new JavaCodeParser();
			parser.setProjectDir(projectDir);
			
			HashMap<String, Integer> termMap = parser.analyze(element);
			// �����Ƿ���Ҫ���������ĵ��Ĵ���(ȥͣ�ô�)
			try {
				StopFilter sf = new StopFilter();
				sf.initStopWordSet(Constant.cnStopWordsFilePath);
				termMap = sf.filterStopWord(termMap);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// ��������ζ�Ӧ���ĵ�������
			String wdsContent = "";
			for (Entry<String, Integer> entry : termMap.entrySet()) {
				wdsContent += entry.getKey() + "=" + entry.getValue() + "\r\n";
			}
			String codeFolderPath = DocumentAccess.resultPath + Constant.FILE_SEPARATOR + Constant.CODE_DIR + Constant.FILE_SEPARATOR + Constant.globalCategoryID;
			File codeFolder = new File(codeFolderPath);
			if (!codeFolder.exists() || !codeFolder.isDirectory()) {
				if (!codeFolder.mkdirs()) {
					System.out.println("=====>>Error: CodeFolder û�д����ɹ� <<=====");
					return ; // Ŀ¼û�д����ɹ����򷵻�
				}
			}
			String fn = element.getElementName();
			int eindex = fn.lastIndexOf(".");
			if (eindex > 0)
				fn = fn.substring(0, eindex);
			fn += ".wds";
			String codeWdsFilePath = codeFolderPath + Constant.FILE_SEPARATOR + fn;
			File wdsFile = new File(codeWdsFilePath);
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(wdsFile));
				bw.write(wdsContent);
				bw.flush();
				bw.close();
				createDocumentDescriptor(Constant.globalCategoryID, fn,
						codeWdsFilePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
//		������SingleProcess�е�parseSrcFiles����
	}
	/**
	 * ����ÿ���ĵ��ε��ĵ�������
	 */
	private static void createDocumentDescriptor (int categoryID, String name, String path) {
		DocumentDescriptor dd = new DocumentDescriptor(categoryID, name, path);
		if (GlobalVariant.docDescriptorList == null)
			GlobalVariant.docDescriptorList = new ArrayList<DocumentDescriptor>();
		GlobalVariant.docDescriptorList.add(dd);
		System.out.println("\t GlobalVariant.docDescriptorList.size = " + GlobalVariant.docDescriptorList.size());
	}
}
