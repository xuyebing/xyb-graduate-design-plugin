package buaa.sei.xyb.analyse.code;

import java.util.ArrayList;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaElement;

/**
 * 
 * @author Xu Yebing
 * JavaProjectVisitor 用于获取java项目中的.java文件
 */
public class JavaProjectVisitor {

	private ArrayList<IType> filesElmList = new ArrayList<IType>();
	private IJavaProject project;
	
	public JavaProjectVisitor(IJavaProject project) {
		this.project = project;
	}
	public ArrayList<IType> getJavaFiles() throws JavaModelException {
		getJavaFilesFromProject(project);
		return filesElmList;
	}
	private void getJavaFilesFromProject(IJavaElement element) throws JavaModelException {
		if (element instanceof IType) {
			if (((IType)element).isBinary() == false) {
				filesElmList.add((IType) element);
			}
		} else if (element instanceof JavaElement) {
			JavaElement je = (JavaElement)element;
			IJavaElement[] children = je.getChildren();
			for (IJavaElement child : children) {
				getJavaFilesFromProject(child);
			}
		}
	}
}
