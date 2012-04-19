package buaa.sei.xyb.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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

import buaa.sei.xyb.actions.ShowRelatedDocVSMAction;
import buaa.sei.xyb.common.Constant;

public class TreeTableVSM extends ViewPart {

	
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
    	    
//            button1 = new Button(composite,SWT.PUSH);
//            button1.setLayoutData(new GridData(GridData.FILL));
//            button1.setText("����");
            
            /*tree = new Tree(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL 
                            | SWT.H_SCROLL); 
                            */
            tree = new Tree(composite,SWT.CHECK | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
            tree.setLayoutData(new GridData(GridData.FILL_BOTH));
            tree.setHeaderVisible(true); 
            tree.setLinesVisible(true); 
            
//            button1.addSelectionListener(new SelectionAdapter(){
//	        	public void widgetSelected(SelectionEvent e)
//	        	{
//	        		//MessageDialog.openInformation(null,"hurry! ","��ť������!");
//	        		int tiNum = ti0.getItemCount();
//	        		int flag = 0;//��������Ƿ���ѡ�е��ļ����Ӷ���ʼ����
//	        		Vector <String> docsName = new Vector <String>();//����ѡ�е��ļ��������ڷ���
//	        		Vector <String> nonReDocsName = new Vector <String>();//����û��ѡ�е��ļ��������ڷ���
//	        		//String treeRoot = ti0.getText().substring(ti0.getText().indexOf('=')+1);//�õ���ѯ�������Ӷ����з���
//	        		String treeRoot = ShowRelatedDocVSMAction.javaFile;//�õ���ѯ����������·���������Ӷ����з���
//	        		MessageDialog.openInformation(null,"treeItemNum","treeItemNumber =" + tiNum +"\n treeRoot =" + treeRoot);
//	        		if(tiNum > 0){
//	        			for(int i = 0; i < tiNum ; i++){
//	        				TreeItem tri = ti0.getItem(i);
//	        				if(tri.getChecked())
//	        				{
//	        					//String filename = tri.getText().substring(tri.getText().indexOf('=')+1);
//	        					String filename = map.get(tri);//�õ�����·����
//	        					System.out.println("tr_" + i +" = " + filename);
//	        					docsName.add(filename);
//	        					flag = 1;
//	        				}
//	        				else{
//	        					String nfilename = map.get(tri);
//	        					nonReDocsName.add(nfilename);
//	        				}
//	        			}
////	        			if(flag == 0)
////	        				MessageDialog.openInformation(null,"warning!","û��ѡ�е��ļ����޷���ʼ����");
////	        			else{
////	        				System.out.println(">>> ��ʼ����  <<<");
////	        				SingleProcess.treeRoot = treeRoot;
////	        				SingleProcess.docsName = docsName;
////	        				SingleProcess.nonReDocsName = nonReDocsName;
////	        				SingleProcess.isFeedBack = true;//���÷�����ǣ��Ա����������н����ж�
////	        				                                //new idea! :����Ӧ�÷����ͣ���LSI �� VSM�зֱ���������ť������Ӧ�÷�����
////	        				SingleProcess.VSMfeedBack = 1;//��ʾ�����VSM�ķ���
////	        				try {
////								SingleProcess.theLastPartProcess();//���������̣����¼������ƶ�
////							} catch (Exception e1) {
////								// TODO Auto-generated catch block
////								e1.printStackTrace();
////							}
////	        			}
//	        		}
//	        		else  //tiNum == 0
//	        			MessageDialog.openInformation(null,"warning��","û�����ڵ�");
//	        			
//	        	}
//	        });
            
            tree.addListener(SWT.MouseDoubleClick, new Listener()
            {

				@Override
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
						   Point point = new Point(event.x, event.y);
						   TreeItem item = tree.getItem(point);
						   if(item != null)
						   { 
							   String sf=item.getText(2);
							   String fs;//=item.getText(0).substring(item.getText(0).indexOf("=")+1);
							   fs=map.get(item);
							   //@xuyeb
							   String numIndex = fs.substring(fs.lastIndexOf('_')+1,fs.lastIndexOf('.'));//�õ����ĵ������
							   System.out.println("numIndex = " + numIndex);
							   if (sf != null) {
								   
									try {
										//MessageDialog.openInformation(null, "���ĵ�",sf);
										//System.out.println(sf.replace("\\", "/"));
										IWorkbenchPage page = PlatformUI.getWorkbench()
												.getActiveWorkbenchWindow().getActivePage();
										File f = new File(sf);
                                        File f2=new File(fs);
										IFile ifile = getModelFileFromPath(sf.replace("\\", "/"));
										IFile ifile2=getModelFileFromPath(fs.replace("\\", "/"));
										//IFile ifile = getModelFileFromPath("C:/link-test/Ch14/SRS/Course.java");
										IDE.openEditor(page, ifile);
										IDE.openEditor(page, ifile2);
										//@ author xuyeb
										
										editor = getSite().getPage().getActiveEditor();
										
										

									} catch (Exception e) {
										e.printStackTrace();
										MessageDialog.openError(null, "������ʾ", "1���뽫�������ĵ�������Ŀ·���£�\n 2���뽫������·�����õ���Ŀ·���¡�");
									}
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
							try {  //���Ա�֤ÿ��ѡ��һ���µ��ĵ�ʱ�����Զ������µĶ�Ӧ���ĵ������
								if(view==null)
									view=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(("sei.buaa.linktracer.ShowHierarchyTree"));
								else
									{
									     //view.dispose();
									     PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(view);//hideView("sei.buaa.linktracer.RelatedDocsResultView");
									     view=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(("sei.buaa.linktracer.ShowHierarchyTree"));//���ǵ���Hierarchy.java
									}
							} catch (PartInitException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
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
    	String iconsLoc=Constant.getToolPath().substring(0, Constant.getToolPath().lastIndexOf("tool"));
    	ti0 = new TreeItem(tree, SWT.NONE); 
        ti0.setText(new String[] { "class="+ShowRelatedDocVSMAction.javaFile.substring(ShowRelatedDocVSMAction.javaFile.lastIndexOf("\\")+1),"��ض�ֵ"}); 
        ti0.setImage(new Image(display,iconsLoc+"icons/btn_files.gif"));
        ti0.setData("level", "0");
        //MessageDialog.openInformation(null, "Inf", SingleProcess.getResultDirString());
        File[] resultFiles=new File(Constant.workingFolder).listFiles(new ResultFileFilter(".log"));
        int j=1;
		for(int i=0;i<resultFiles.length;i++)
		{
			if(resultFiles[i].toString().contains("result_")){//��Ҫ�õ�result_��ͷ���ļ�
				FileReader fr=new FileReader(resultFiles[i]);
				BufferedReader br=new BufferedReader(fr);
				String line=null;
				while((line=br.readLine())!=null)
				{
					String[] r=line.split("\t");
					
					String javaFileName = ShowRelatedDocVSMAction.javaFile.substring(ShowRelatedDocVSMAction.javaFile.lastIndexOf("\\")+1, 
							ShowRelatedDocVSMAction.javaFile.lastIndexOf("."));
					if(r[1].contains(javaFileName))
					{
						
			    		//String f=r[1];
						TreeItem  ti00=new TreeItem(ti0,SWT.NONE);
		    			ti00.setText(new String[]{"doc"+(j++)+"="+r[0].substring(r[0].lastIndexOf("\\")+1),r[2]});
		    		    ti00.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE));
		    		    map.put(ti00, r[0]);
					}
					
				}
			}
			
		}
        
    } 


    private void addColumns() { 
            TreeColumn name = new TreeColumn(tree, SWT.LEFT); 
            name.setResizable(true); 
            name.setText("�ļ�����"); 
            name.setWidth(300); 

            TreeColumn value = new TreeColumn(tree, SWT.LEFT); 
            value.setResizable(true); 
            value.setText("VSM��ض�ֵ"); 
            value.setWidth(100); 
            
//            TreeColumn parent = new TreeColumn(tree, SWT.CENTER); 
//            parent.setResizable(true); 
//            parent.setText("�����ĵ�"); 
//            parent.setWidth(500); 
//
//            TreeColumn offset = new TreeColumn(tree, SWT.LEFT); 
//            offset.setResizable(true); 
//            offset.setText("�ĵ�Ƭ��ƫ����"); 
//            offset.setWidth(100); 
//
//            TreeColumn length = new TreeColumn(tree, SWT.LEFT); 
//            length.setResizable(true); 
//            length.setText("�ĵ��γ���"); 
//            length.setWidth(100); 
           
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
