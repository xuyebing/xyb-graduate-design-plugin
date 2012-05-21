package buaa.sei.xyb.views;

import graduate_design_plugin.Activator;
import graduate_design_plugin.IPreferenceListener;
import graduate_design_plugin.resource.ImageNames;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.part.ViewPart;

import buaa.sei.xyb.analyse.document.WordDocParser;
import buaa.sei.xyb.analyse.modelcontrol.BuildModel;
import buaa.sei.xyb.common.Constant;

import com.cloudgarden.resource.SWTResourceManager;

public class TraceableFormulaViewComposite extends Composite implements
		SelectionListener, IPreferenceListener {

	// Similarity里的相似性比较按钮
	private ToolItem buttonTracing;

	private Button checkBoxWholeWord;

	private TableColumn columnSimilarity;

	private Composite compositeSimilairity;

	private Vector<ProgressBar> disposeBag;

	private List listCandidateIdentifiers;

//		private EclipseLSIProvider providerLSI;

//	private Shell shell;

	private TabFolder tabFolderLinkTracer;

	private TabItem tabItemSimilarity;

	// Similarity的table框
	private Table tableSimilarity;
	
	private Text textBoxFilter;
	
	private ToolBar toolbarSimilarity;
	
	private ViewPart view;
	
	private int order = 1;
	
	private Collection<String> documents = null;
	
	{
		SWTResourceManager.registerResourceUser(this);
	}
	
	public TraceableFormulaViewComposite(Composite parent,
			int style, ViewPart view) {
		super(parent, style);
		this.view = view;
		this.disposeBag = new Vector<ProgressBar>();
		this.initGUI(); // 初始化view界面
		this.loadDocumentsTable(); // 向view界面中加入内容
	}
	
	/**
	 * @see graduate_design_plugin.IPreferenceListener#preferenceChanged()
	 */
	public void preferenceChanged() {
		this.loadDocumentsTable();
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		/** 按下追踪按钮后，开始执行对应的分析 
		 *  这里是整个程序的入口
		 */
//		// 判断是否使能“启动”程序开关
//		this.toggleEnableButton();
		if (e.getSource().equals(this.buttonTracing)) {
			if (this.documents != null && this.documents.size() > 0) {
				/** 整个程序的入口 **/
				BuildModel buildModel = new BuildModel(Constant.softwareDocFolder, Constant.srcCodeProjectName);
				try {
					buildModel.build();
				} catch (JavaModelException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

	}
	
	private void initGUI() {
		try {
			FillLayout thisLayout = new FillLayout(
					org.eclipse.swt.SWT.HORIZONTAL);
			this.setLayout(thisLayout);
			this.setSize(251, 514);
			{
				this.tabFolderLinkTracer = new TabFolder(this, SWT.NONE);
				{
					// 添加Similarity栏
					this.tabItemSimilarity = new TabItem(this.tabFolderLinkTracer,
							SWT.NONE);
					this.tabItemSimilarity.setText("Similarity");
					{
						this.compositeSimilairity = new Composite(
								this.tabFolderLinkTracer, SWT.NONE);
						this.tabItemSimilarity
								.setControl(this.compositeSimilairity);
						{
							this.toolbarSimilarity = new ToolBar(
									this.compositeSimilairity, SWT.NONE);
							this.toolbarSimilarity.setBounds(0, 0, 45, 45);
							{
								// 增添相似性比较的按钮
								this.buttonTracing = new ToolItem(
										this.toolbarSimilarity, SWT.NONE);
								this.buttonTracing.setEnabled(true);
//								this.buttonTracing.setImage(ResourceManager
//										.getPluginImage(LinkTracerPlugin
//												.getDefault(),
//												"icons/traceIcon.gif"));
								this.buttonTracing
										.setToolTipText("Begin software traceability analyze now!");
								this.buttonTracing.setImage(Activator.getDefault().getImageRegistry().get(ImageNames.ICON_START));
//								this.buttonTracing.setImage(SWTResourceManager
//										.getImage("icons/start_button.jpg"));
//								Image icon = new Image(this.getDisplay(), "icons/start_button.jpg");
//								this.buttonTracing.setImage(icon);
								this.buttonTracing.addSelectionListener(this);
							}
						}
						{
							// 添加相似度显示的表格
							this.tableSimilarity = new Table(
									this.compositeSimilairity, // SWT.CHECK |
											 SWT.H_SCROLL | SWT.V_SCROLL
											| SWT.BORDER);
							this.tableSimilarity.setBounds(0, 50, 410, 350);
							this.tableSimilarity.setHeaderVisible(true);
							this.tableSimilarity.setLinesVisible(true);
							this.tableSimilarity.addSelectionListener(this);
							TableColumn columnSelection = new TableColumn(
									this.tableSimilarity, SWT.LEFT);
							columnSelection.setResizable(false);
							columnSelection.setAlignment(SWT.CENTER);
							columnSelection.setWidth(20);
							TableColumn columnArtefact = new TableColumn(
									this.tableSimilarity, SWT.LEFT);
							columnArtefact.setText("Artefact");
							columnArtefact.setWidth(300);
							{
								this.columnSimilarity = new TableColumn(
										this.tableSimilarity, SWT.NONE);
								this.columnSimilarity.setResizable(false);
								this.columnSimilarity.setText("Order");
								this.columnSimilarity.setWidth(80);
							}
						}
					}
				}
			}
			this.layout();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	// 注释掉了相似度显示的部分
	private void loadDocumentsTable() {
		if (this.tableSimilarity!=null && !this.tableSimilarity.isDisposed())
			this.tableSimilarity.removeAll();
		if (Constant.softwareDocFolder != null) {
			File docFile = new File(Constant.softwareDocFolder);
			File[] files = null;
			if (docFile.exists() && docFile.isDirectory()) {
				files = docFile.listFiles();
			}
			this.documents = new Vector<String>();
			if (files != null) {
				for (File file : files) {
					if (file.isDirectory() && !file.getName().contains(WordDocParser.tempDir))
						documents.add(file.getName());
				}
			}
			
			Iterator<String> it = documents.iterator();
			int orderIndex = 1;
			this.clearDisposeBag();
			while (it.hasNext()) {
				String docDir = it.next();
				TableItem item = new TableItem(this.tableSimilarity, 0);
				String[] itemText = new String[3];
				itemText[0] = "";
				itemText[1] = docDir;
				double similarity = -1;
	//				if (item.getChecked())
	//					similarity = documentDescriptor.getSimilarity();
				itemText[2] = String.valueOf(orderIndex++);
	//				itemText[2] = String.format("%1$4.2f", similarity);
				item.setText(itemText);
				item.setData(docDir);
				// 相似度显示
				/*TableEditor tableEditor = new TableEditor(this.tableSimilarity);
				final ProgressBar tmpProgressBar = new ProgressBar(
						this.tableSimilarity, SWT.SMOOTH);
				this.disposeBag.add(tmpProgressBar);
				final double percentageSimilarity = (similarity < 0) ? 0 : similarity * 100;
				int color = SWT.COLOR_GREEN;
				if (percentageSimilarity < 30)
					color = SWT.COLOR_RED;
				else if ((percentageSimilarity > 30) && (percentageSimilarity < 60)) 
					color = SWT.COLOR_YELLOW;
				final int backgroudColor = color;
				tmpProgressBar.setSelection((int) percentageSimilarity);
				tmpProgressBar.addPaintListener(new PaintListener() {
					public void paintControl(PaintEvent e) {
						tmpProgressBar.setForeground(LinkTracerViewComposite.this.shell
											.getDisplay().getSystemColor(backgroudColor));
						String string = String.format("%1$4.2f", (percentageSimilarity
								/ (tmpProgressBar.getMaximum() - tmpProgressBar
										.getMinimum()) * 100));
						string = string + "%";
						Point point = tmpProgressBar.getSize();
						Font font = new Font(LinkTracerViewComposite.this.shell
								.getDisplay(), "Courier", 10, SWT.BOLD);
						e.gc.setFont(font);
						e.gc.setForeground(LinkTracerViewComposite.this.shell
								.getDisplay().getSystemColor(SWT.COLOR_BLACK));
						FontMetrics fontMetrics = e.gc.getFontMetrics();
						int stringWidth = fontMetrics.getAverageCharWidth()
								* string.length();
						int stringHeight = fontMetrics.getHeight();
						e.gc.setForeground(LinkTracerViewComposite.this.shell
								.getDisplay().getSystemColor(SWT.COLOR_BLACK));
						e.gc.drawString(string, (point.x - stringWidth) / 2,
								(point.y - stringHeight) / 2, true);
						font.dispose();
					}
				});
				tableEditor.grabHorizontal = true;
				tableEditor.setEditor(tmpProgressBar, item, 2);*/
			}
		}
	}
	private void clearDisposeBag() {
		if (this.disposeBag.size() > 0) {
			ProgressBar progressBar = this.disposeBag.remove(0);
			progressBar.dispose();
		}
	}
	
	private void toggleEnableButton() {
		if (this.documents != null && this.documents.size() > 0) {
			this.buttonTracing.setEnabled(true);
		}
	}
}
