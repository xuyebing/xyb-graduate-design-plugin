package buaa.sei.xyb.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

import buaa.sei.xyb.actions.ShowRelatedDocLDAAction;
import buaa.sei.xyb.actions.ShowRelatedDocLDAAction;
import buaa.sei.xyb.analyse.document.DocInfo;
import buaa.sei.xyb.analyse.document.DocumentAccess;
import buaa.sei.xyb.common.Constant;
import buaa.sei.xyb.lda.jgibblda.Pair;

public class TreeTableLDA extends ViewPart {

	public static IViewPart view = null;
    private IEditorPart editor;
    public static Tree tree; 
    public static TreeItem ti0;
//    Button button1;

    public static Display display;
    public HashMap<TreeItem,String> map;
    public void createPartControl(Composite parent) { 
    	   
    	    map=new HashMap<TreeItem,String>();
    	    display=parent.getDisplay();
    	    
    	    Composite composite = new Composite(parent,SWT.NONE);
    	    composite.setLayout(new GridLayout(1,false));
    	    
            tree = new Tree(composite,SWT.CHECK | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
            tree.setLayoutData(new GridData(GridData.FILL_BOTH));
            tree.setHeaderVisible(true); 
            tree.setLinesVisible(true); 
            
            tree.addListener(SWT.MouseDoubleClick, new Listener()
            {

				@Override
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
						   Point point = new Point(event.x, event.y);
						   TreeItem item = tree.getItem(point);
						   if(item != null)
						   { 
						        String docName=item.getText(0);
							    docName = docName.substring(docName.indexOf("=")+1, docName.indexOf("."));
							    // 使用docName在DocumentAccess的docLocationMap中进行查找，获得文档段的绝对路径.
							    DocInfo di = DocumentAccess.docLocationMap.get(docName);
							    try {
							    	IWorkbenchPage page = PlatformUI.getWorkbench()
							    			.getActiveWorkbenchWindow().getActivePage();
							    	IPath path = new Path(di.absPath); 
									IFileStore fileStore = EFS.getLocalFileSystem().getStore(path);
									//fileStore = fileStore.getChild(path);
									if (!fileStore.fetchInfo().isDirectory() && fileStore.fetchInfo().exists()) {
									    try {
									        IDE.openEditorOnFileStore(page, fileStore);
									      
									    } catch (PartInitException e) {
									    	e.printStackTrace();
									    }
									}
							    	////////////////
									// 本科毕设中的实现
									//IFile ifile = getModelFileFromPath(di.absPath.replace("\\", "/"));
									//IFile ifile = getModelFileFromPath("C:/link-test/Ch14/SRS/Course.java");
									//IDE.openEditor(page, ifile);
									//editor = getSite().getPage().getActiveEditor();
								} catch (Exception e) {
									e.printStackTrace();
									MessageDialog.openError(null, "错误提示", "1：请将待分析文档放在项目路径下；\n 2：请将结果存放路径设置到项目路径下。");
								}
						   }
						   /* try {
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(("sei.buaa.linktracer.ShowHierarchyTree"));
						    	//PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(("sei.buaa.linktracer.RelatedDocsResultView2"));
							} catch (PartInitException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}*/
							/////////////
						   
						   /**可以保证每次选择一个新的文档时，会自动弹出新的对应的文档层次树
							try {
								if(view==null)
									view=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(("sei.buaa.linktracer.ShowHierarchyTree"));
								else
									{
									     //view.dispose();
									     PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(view);//hideView("sei.buaa.linktracer.RelatedDocsResultView");
									     view=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(("sei.buaa.linktracer.ShowHierarchyTree"));//就是调用Hierarchy.java
									}
							} catch (PartInitException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}**/
				}   	
            });
          
            addColumns(); 
            try {
				addItems();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

    } 
    private void addItems() throws IOException {
    	if (Constant.getToolPath() != null) {
	    	String iconsLoc=Constant.getToolPath().substring(0, Constant.getToolPath().lastIndexOf("tool"));
	    	ti0 = new TreeItem(tree, SWT.NONE); 
	        ti0.setText(new String[] { "class="+ShowRelatedDocLDAAction.javaFile.substring(ShowRelatedDocLDAAction.javaFile.lastIndexOf("\\")+1),"相关度值"}); 
	        ti0.setImage(new Image(display,iconsLoc+"icons/btn_files.gif"));
	        ti0.setData("level", "0");
	        //MessageDialog.openInformation(null, "Inf", SingleProcess.getResultDirString());
	        File[] resultFiles=new File(Constant.workingFolder).listFiles(new ResultFileFilter(".log"));
	        int j=1;
			for(int i=0;i<resultFiles.length;i++)
			{
				if(resultFiles[i].getName().toString().matches("^" + Constant.LDA_TOPIC_RESULT_OUTPUT_FILE_PREFIX + ".*")){//需要得到lsiresult_开头的文件
					FileReader fr=new FileReader(resultFiles[i]);
					BufferedReader br=new BufferedReader(fr);
					String line=null;
					List<Pair> resultList = new ArrayList<Pair>();
					while((line=br.readLine())!=null)
					{
						String[] r=line.split("\t");
						
						String javaFileName = ShowRelatedDocLDAAction.javaFile.substring(ShowRelatedDocLDAAction.javaFile.lastIndexOf("\\")+1, 
								ShowRelatedDocLDAAction.javaFile.lastIndexOf("."));
						if(r[1].trim().matches("^" + javaFileName + "\\.wds"))
						{
							// 将与该代码段相关的记录加入到resultList中
							resultList.add(new Pair(r[0], Double.valueOf(r[2])));
							
//				    		String f=r[1];
//							TreeItem  ti00=new TreeItem(ti0,SWT.NONE);
//			    			ti00.setText(new String[]{"doc"+(j++)+"="+r[0].substring(r[0].lastIndexOf("\\")+1),r[2]});
//			    		    ti00.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE));
//			    		    map.put(ti00, r[0]);
						}
						
					}
					// 对resultList排序
					Collections.sort(resultList);
					for (Iterator<Pair> iterator = resultList.iterator(); iterator.hasNext(); ) {
						Pair oneResult = iterator.next();
						String r0 = oneResult.first.toString();
						String relateValue = oneResult.second.toString();
						
						TreeItem ti00 = new TreeItem(ti0, SWT.NONE);
						ti00.setText(new String[]{"doc"+(j++)+"="+r0.substring(r0.lastIndexOf("\\")+1), relateValue});
						ti00.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE));
						map.put(ti00, r0);
					}
				}
				
			}
    	}
    } 


    private void addColumns() { 
            TreeColumn name = new TreeColumn(tree, SWT.LEFT); 
            name.setResizable(true); 
            name.setText("文件名称"); 
            name.setWidth(300); 

            TreeColumn value = new TreeColumn(tree, SWT.LEFT); 
            value.setResizable(true); 
            value.setText("LDA主题相关度值"); 
            value.setWidth(200); 
    } 

	private IFile getModelFileFromPath(String sf) {
		// TODO Auto-generated method stub
		String sourceFileFullPath = sf;
		IFile file = null;
		IPath path = new Path(sourceFileFullPath);
		if (sourceFileFullPath != null) {
			file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(
					path);
		}
		return file;
	}
    public void setFocus() { 
            tree.setFocus(); 
    } 
}