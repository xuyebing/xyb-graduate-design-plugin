package graduate_design_plugin.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

/**
 * TraceableFormula preferences page 
 * "Window"->"Preference"->"TraceableFormula" 界面
 * @author Xu Yebing
 *
 */
public class TraceableFormulaPreferencesComposite extends Composite {

	private TabFolder tabFolderTraceableFormula;
	private TabItem tabConfiguration;
	private Composite confComposite;
	private Group artefactConfGroup;
	private Label labelSrcCodeProjectName;
	private Text sourceCodeProjectName;
	private Button buttonChangesrcCodeProjectName;
	private Button buttonBuildSpace;
	private Label labelWorkingFolder;
	private Text textWorkingFolder;
	private Text textSoftDoc;
	private Label labelSoftDoc;
	private Button buttonSoftDoc;
	
	public String getSourceCodeProjectName() {
		return this.sourceCodeProjectName.getText();
	}
	public String getWorkingFolder() {
		return this.textWorkingFolder.getText();
	}
	public String getSoftDoc() {
		return this.textSoftDoc.getText();
	}
	
	public TraceableFormulaPreferencesComposite(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
		this.initGUI();
		
	}
	private void initGUI() {
		FillLayout thisLayout = new FillLayout(SWT.HORIZONTAL);
		this.setLayout(thisLayout);
		this.setSize(542, 479);
		{  // a tabFolder
			this.tabFolderTraceableFormula = new TabFolder(this, SWT.NONE);
			{ // sub page in tabFolder
				this.tabConfiguration = new TabItem(this.tabFolderTraceableFormula, SWT.NONE);
				this.tabConfiguration.setText("Configuration");
				{
					this.confComposite = new Composite(this.tabFolderTraceableFormula, SWT.NONE);
					this.tabConfiguration.setControl(this.confComposite); // set tab page's composite
					{
						this.artefactConfGroup = new Group(this.confComposite, SWT.NONE);
						this.artefactConfGroup.setBounds(0, 0, 492, 150);
						this.artefactConfGroup.setText("Artefact Configuration");
						{
							// Label
							this.labelSrcCodeProjectName = new Label(
									this.artefactConfGroup, SWT.NONE);
							this.labelSrcCodeProjectName.setBounds(5, 67, 102, 13);
							this.labelSrcCodeProjectName.setText("Source code dir");
							// Text
							this.sourceCodeProjectName = new Text(
									this.artefactConfGroup, SWT.BORDER);
							this.sourceCodeProjectName.setBounds(120, 65, 306, 20);
							// Button
//							this.buttonChangesrcCodeProjectName = new Button(
//									this.artefactConfGroup, SWT.NONE);
//							this.buttonChangesrcCodeProjectName
//							.addSelectionListener(new SelectionAdapter() {
//								public void widgetSelected(SelectionEvent e) {
//									super.widgetSelected(e);
//									onSelectStopwordFile();
//								}
//							});
//							this.buttonChangesrcCodeProjectName.setBounds(432,
//									65, 53, 20);
//							this.buttonChangesrcCodeProjectName.setText("Change");
//							this.buttonChangesrcCodeProjectName.setEnabled(false);
						}
						{ // "Working Folder"
							// Text
							this.textWorkingFolder = new Text(
									this.artefactConfGroup, SWT.BORDER);
							this.textWorkingFolder.setBounds(120, 95, 306, 20);
							this.textWorkingFolder.setEditable(false);
							// Label
							this.labelWorkingFolder = new Label(
									this.artefactConfGroup, SWT.NONE);
							this.labelWorkingFolder.setBounds(5, 97, 102, 20);
							this.labelWorkingFolder.setText("Working folder");
							// Button
							this.buttonBuildSpace = new Button(
									this.artefactConfGroup, SWT.NONE);
							this.buttonBuildSpace
							.addSelectionListener(new SelectionAdapter() {
								public void widgetSelected(SelectionEvent e) {
									super.widgetSelected(e);
									onNewDDFolder(textWorkingFolder);
								}
							});
							this.buttonBuildSpace.setBounds(432, 95, 53, 20);
							this.buttonBuildSpace.setText("scan");
						}
						{ // 软件文档路径
							// Text
							this.textSoftDoc = new Text(
									this.artefactConfGroup, SWT.BORDER);
							this.textSoftDoc.setBounds(120, 125, 306, 20);
							this.textSoftDoc.setEditable(false);
							// Label
							this.labelSoftDoc = new Label(
									this.artefactConfGroup, SWT.NONE);
							this.labelSoftDoc
							.setBounds(5, 127, 72, 13);
							this.labelSoftDoc
							.setText("Document dir");
							// Button
							this.buttonSoftDoc = new Button(
									this.artefactConfGroup, SWT.NONE);
							this.buttonSoftDoc
							.addSelectionListener(new SelectionAdapter() {
								public void widgetSelected(SelectionEvent e) {
									super.widgetSelected(e);
										onNewDDFolder(textSoftDoc);
								}
							});
							this.buttonSoftDoc.setBounds(432, 125, 53, 20);
							this.buttonSoftDoc.setText("scan");
						}
					}
				}
			}
		}
		this.layout();
	}
	private void onNewDDFolder(Text pathText) {
		DirectoryDialog directoryDialog = new DirectoryDialog(this.getShell());
		directoryDialog.setText("请选择对应的文件夹");
		String dirPath = directoryDialog.open();
		if (dirPath != null)
			pathText.setText(dirPath);
	}
	
}
