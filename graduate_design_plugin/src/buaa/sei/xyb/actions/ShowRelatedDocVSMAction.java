package buaa.sei.xyb.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ShowRelatedDocVSMAction implements IObjectActionDelegate {
	
	private IWorkbenchPart targetPart;
	private IProject project ;
	private ISelection selection;
	public static String javaFile=null;
	public static IViewPart view=null;

	public ShowRelatedDocVSMAction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		Object node;
		selection= this.targetPart.getSite().getSelectionProvider()
		    .getSelection();
        if (selection == null)
        	return;
		if (selection instanceof IStructuredSelection) {
			//getProject(selection);
			IStructuredSelection s = (IStructuredSelection) selection;
			node = s.getFirstElement();
			IJavaElement je=(IJavaElement)node;
			IJavaProject pro=(IJavaProject) je.getJavaProject();
			String proPath=pro.getProject().getLocation().toString();
			String filePath=((JavaElement) node).getPath().toString();
			System.out.println( filePath);
			String loc=filePath.substring(filePath.indexOf("/", 1));
			String proName = pro.getElementName();
			javaFile=(proPath+loc).replace("/", "\\");
			System.out.println(javaFile);

			try {
				if(view==null)
				{
					view=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(("buaa.sei.xyb.RelatedDocsResultViewVSM"));			    
				}
				else
					{
					     //view.dispose();
					     PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(view);//hideView("sei.buaa.linktracer.RelatedDocsResultView");
					     view=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(("buaa.sei.xyb.RelatedDocsResultViewVSM"));//就是调用TreeTableVSM.java
					}
			} catch (PartInitException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		this.targetPart = targetPart;
	}

}
