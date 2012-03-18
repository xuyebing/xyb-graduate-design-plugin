package buaa.sei.xyb.analyse.code;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import buaa.sei.xyb.analyse.document.Constant;

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
		Constant.globalCategoryID++; // 所有的代码段都对应到同一个类ID，而不是一个代码段对应一个类ID
		while (iter.hasNext()) {
			IType element = iter.next();
			JavaCodeParser parser = new JavaCodeParser();
			parser.setProjectDir(projectDir);
			
			parser.analyze(element);
		}
		
//		继续改SingleProcess中的parseSrcFiles方法
	}
}
