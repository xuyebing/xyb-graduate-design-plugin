package graduate_design_plugin.ui;

import graduate_design_plugin.Activator;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import buaa.sei.xyb.common.Constant;

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
	
	// Temp Folder 保存程序运行过程中的中间文件及数据
	private Text textTempFolder;
	private Label labelTempFolder;
	private Button buttonTempFolder;
	
	private Text textToolFolder;
	private Label labelToolFolder;
	private Button buttonToolFolder;
	private Text textDataDict;
	private Label labelDataDict;
	private Button buttonDataDict;
	// 中文停用词
	private Text textCnStopWords;
	private Label labelCnStopWords;
	private Button buttonCnStopWords;
	// new group
	private Group nonSplitGroup;
	private Button nonDocSplit; // 选中表示不需要进行文档的自动分割（lucene实验使用）。
	private Button doDocSplit; // 选中表示进行文档的自动分割（lucene实验使用）。
	
	private PreferenceValue preferenceValue;
	// New Tab "Model-Setting"
	private TabItem tabModelSetting;
	private Composite modSetComposite;
	private Group modSetGroup;
	
	private Text alphaText;
	private Text betaText;
	private Text ntopicsText;
	private Text nitersText;
	private Text savestepText;
	private Text twordsText;
	
	public PreferenceValue getPreferenceValue() {
		return this.preferenceValue;
	}
	
	public String getSourceCodeProjectName() {
		return this.sourceCodeProjectName.getText();
	}
	public String getWorkingFolder() {
		return this.textWorkingFolder.getText();
	}
	public String getTempFolder() {
		return this.textTempFolder.getText();
	}
	public String getSoftDoc() {
		return this.textSoftDoc.getText();
	}
	public String getToolFolder() {
		return this.textToolFolder.getText();
	}
	public String getDataDict() {
		return this.textDataDict.getText();
	}
	public String getCnStopWords() {
		return this.textCnStopWords.getText();
	}
	
	// LDA Est Arguments
	public String getLDAEstArgumentsAlpha() {
		return this.alphaText.getText();
	}
	public String getLDAEstArgumentsBeta() {
		return this.betaText.getText();
	}
	public String getLDAEstArgumentsNtopics() {
		return this.ntopicsText.getText();
	}
	public String getLDAEstArgumentsNiters() {
		return this.nitersText.getText();
	}
	public String getLDAEstArgumentsSavestep() {
		return this.savestepText.getText();
	}
	public String getLDAEstArgumentsTwords() {
		return this.twordsText.getText();
	}
	
	public TraceableFormulaPreferencesComposite(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
		this.preferenceValue = new PreferenceValue();
		this.initGUI();
		
		// 初始化以保存的preference值
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		this.initStoredPrefs(preferenceStore);
		
	}
	private void initGUI() {
		FillLayout thisLayout = new FillLayout(SWT.HORIZONTAL);
		this.setLayout(thisLayout);
		this.setSize(542, 479);
		{  // a tabFolder
			this.tabFolderTraceableFormula = new TabFolder(this, SWT.NONE);
			// The page of "Configuration"
			{ // sub page in tabFolder
				this.tabConfiguration = new TabItem(this.tabFolderTraceableFormula, SWT.NONE);
				this.tabConfiguration.setText("Configuration");
				{
					this.confComposite = new Composite(this.tabFolderTraceableFormula, SWT.NONE);
					this.tabConfiguration.setControl(this.confComposite); // set tab page's composite
					{
						this.artefactConfGroup = new Group(this.confComposite, SWT.NONE);
						this.artefactConfGroup.setBounds(0, 0, 492, 250);
						this.artefactConfGroup.setText("Artefact Configuration");
						{
							// Label
							this.labelSrcCodeProjectName = new Label(
									this.artefactConfGroup, SWT.NONE);
							this.labelSrcCodeProjectName.setBounds(5, 35, 102, 20);
							this.labelSrcCodeProjectName.setText("Source Code Dir");
							// Text
							this.sourceCodeProjectName = new Text(
									this.artefactConfGroup, SWT.BORDER);
							this.sourceCodeProjectName.setBounds(120, 35, 306, 20);
							// Button
//							this.buttonChangesrcCodeProjectName = new Button(
//									this.artefactConfmodSetGroup, SWT.NONE);
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
							this.textWorkingFolder.setBounds(120, 65, 306, 20);
							this.textWorkingFolder.setEditable(false);
							// Label
							this.labelWorkingFolder = new Label(
									this.artefactConfGroup, SWT.NONE);
							this.labelWorkingFolder.setBounds(5, 67, 102, 20);
							this.labelWorkingFolder.setText("Working Folder");
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
							this.buttonBuildSpace.setBounds(432, 65, 53, 20);
							this.buttonBuildSpace.setText("scan");
						}
						{ // 软件文档路径
							// Text
							this.textSoftDoc = new Text(
									this.artefactConfGroup, SWT.BORDER);
							this.textSoftDoc.setBounds(120, 95, 306, 20);
							this.textSoftDoc.setEditable(false);
							// Label
							this.labelSoftDoc = new Label(
									this.artefactConfGroup, SWT.NONE);
							this.labelSoftDoc.setBounds(5, 95, 102, 20);
							this.labelSoftDoc.setText("Document Dir");
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
							this.buttonSoftDoc.setBounds(432, 95, 53, 20);
							this.buttonSoftDoc.setText("scan");
						}
						{ // 保存程序运行中的中间文件及数据
							// Text
							this.textTempFolder = new Text(
									this.artefactConfGroup, SWT.BORDER);
							this.textTempFolder.setBounds(120, 125, 306, 20);
							this.textTempFolder.setEditable(false);
							// String toolFolderPath = this.getClass().get;
							// this.textToolFolder.setText(toolFolderPath);
							// Label
							this.labelTempFolder = new Label(
									this.artefactConfGroup, SWT.NONE);
							this.labelTempFolder.setBounds(5, 125, 102, 20);
							this.labelTempFolder.setText("Temp Folder");
							// Button
							this.buttonTempFolder = new Button(
									this.artefactConfGroup, SWT.NONE);
							this.buttonTempFolder
							.addSelectionListener(new SelectionAdapter() {
								public void widgetSelected(SelectionEvent e) {
									super.widgetSelected(e);
										onNewDDFolder(textTempFolder);
								}
							});
							this.buttonTempFolder.setBounds(432, 125, 53, 20);
							this.buttonTempFolder.setText("scan");
						}
						{ // 处理代码所需工具，师兄实现版本使用
							// Text
							this.textToolFolder = new Text(
									this.artefactConfGroup, SWT.BORDER);
							this.textToolFolder.setBounds(120, 155, 306, 20);
							this.textToolFolder.setEditable(false);
							// String toolFolderPath = this.getClass().get;
							// this.textToolFolder.setText(toolFolderPath);
							// Label
							this.labelToolFolder = new Label(
									this.artefactConfGroup, SWT.NONE);
							this.labelToolFolder.setBounds(5, 155, 102, 20);
							this.labelToolFolder.setText("Tool Folder");
							// Button
							this.buttonToolFolder = new Button(
									this.artefactConfGroup, SWT.NONE);
							this.buttonToolFolder
							.addSelectionListener(new SelectionAdapter() {
								public void widgetSelected(SelectionEvent e) {
									super.widgetSelected(e);
										onNewDDFolder(textToolFolder);
								}
							});
							this.buttonToolFolder.setBounds(432, 155, 53, 20);
							this.buttonToolFolder.setText("scan");
						}
						{ // 英文翻译所需的词典，师兄实现版本使用
							// Text
							this.textDataDict = new Text(
									this.artefactConfGroup, SWT.BORDER);
							this.textDataDict.setBounds(120, 185, 306, 20);
							this.textDataDict.setEditable(false);
							// String toolFolderPath = this.getClass().get;
							// this.textToolFolder.setText(toolFolderPath);
							// Label
							this.labelDataDict = new Label(
									this.artefactConfGroup, SWT.NONE);
							this.labelDataDict.setBounds(5, 185, 102, 20);
							this.labelDataDict.setText("Data Dict");
							// Button
							this.buttonDataDict = new Button(
									this.artefactConfGroup, SWT.NONE);
							this.buttonDataDict
							.addSelectionListener(new SelectionAdapter() {
								public void widgetSelected(SelectionEvent e) {
									super.widgetSelected(e);
										onNewFileFolder(textDataDict);
								}
							});
							this.buttonDataDict.setBounds(432, 185, 53, 20);
							this.buttonDataDict.setText("scan");
						}
						{ // "中文停用词文件路径"
							// Text
							this.textCnStopWords = new Text(
									this.artefactConfGroup, SWT.BORDER);
							this.textCnStopWords.setBounds(120, 215, 306, 20);
							this.textCnStopWords.setEditable(false);
							// Label
							this.labelCnStopWords = new Label(
									this.artefactConfGroup, SWT.NONE);
							this.labelCnStopWords.setBounds(5, 215, 102, 20);
							this.labelCnStopWords.setText("Cn Stop Words");
							// Button
							this.buttonCnStopWords = new Button(
									this.artefactConfGroup, SWT.NONE);
							this.buttonCnStopWords
							.addSelectionListener(new SelectionAdapter() {
								public void widgetSelected(SelectionEvent e) {
									super.widgetSelected(e);
									onNewFileFolder(textCnStopWords);
								}
							});
							this.buttonCnStopWords.setBounds(432, 215, 53, 20);
							this.buttonCnStopWords.setText("scan");
						}
					}
					//new group
					this.nonSplitGroup = new Group(this.confComposite, SWT.NONE);
					this.nonSplitGroup.setBounds(0, 260, 492, 45);
//					this.nonSplitGroup.setText("Artefact Configuration");
					{
						this.nonDocSplit = new Button(
								this.nonSplitGroup, SWT.RADIO
								| SWT.LEFT);
						this.nonDocSplit.setBounds(3,15,243,26);
						this.nonDocSplit.setText("不进行文档自动分割");
						this.nonDocSplit
						.setSelection(false);
						Constant.notSplitDoc = false;
						this.nonDocSplit.addSelectionListener(new SelectionAdapter() {
							public void widgetSelected(SelectionEvent e) {
								Constant.notSplitDoc = true;
							}
						});
						
						this.doDocSplit = new Button(
								this.nonSplitGroup, SWT.RADIO
								| SWT.LEFT);
						this.doDocSplit.setBounds(250,15,237,26);
						this.doDocSplit
						.setText("文档自动分割");
						this.doDocSplit
						.setSelection(true);
						
						this.doDocSplit.addSelectionListener(new SelectionAdapter() {
							public void widgetSelected(SelectionEvent e) {
								Constant.notSplitDoc = false;
							}
						});
					}
				}
			}
			// The page of "Model-Setting"
			{
				this.tabModelSetting = new TabItem(this.tabFolderTraceableFormula,
						SWT.NONE);
				this.tabModelSetting.setText("Model-Setting");
				{
					this.modSetComposite = new Composite(this.tabFolderTraceableFormula, SWT.NONE);
					this.tabModelSetting.setControl(this.modSetComposite);
					{
						this.modSetGroup = new Group(this.modSetComposite, SWT.NONE);
						this.modSetGroup.setBounds(0, 0, 533, 111);
						this.modSetGroup.setText("LDA Est Arguments");
						
						Label lblAlpha = new Label(modSetGroup, SWT.NONE);
						lblAlpha.setBounds(18, 33, 33, 17);
						lblAlpha.setText("alpha");
						
						alphaText = new Text(modSetGroup, SWT.BORDER);
						alphaText.setBounds(57, 30, 61, 23);
						
						Label lblBeta = new Label(modSetGroup, SWT.NONE);
						lblBeta.setText("beta");
						lblBeta.setBounds(141, 33, 33, 17);
						
						betaText = new Text(modSetGroup, SWT.BORDER);
						betaText.setBounds(180, 30, 61, 23);
						
						Label lblNewLabel = new Label(modSetGroup, SWT.NONE);
						lblNewLabel.setBounds(10, 72, 43, 17);
						lblNewLabel.setText("ntopics");
						
						ntopicsText = new Text(modSetGroup, SWT.BORDER);
						ntopicsText.setBounds(57, 69, 61, 23);
						
						Label lblIter = new Label(modSetGroup, SWT.NONE);
						lblIter.setBounds(141, 72, 33, 17);
						lblIter.setText("niters");
						
						nitersText = new Text(modSetGroup, SWT.BORDER);
						nitersText.setBounds(180, 69, 61, 23);
						
						Label lblTopicwords = new Label(modSetGroup, SWT.NONE);
						lblTopicwords.setText("savestep");
						lblTopicwords.setBounds(263, 72, 51, 17);
						
						savestepText = new Text(modSetGroup, SWT.BORDER);
						savestepText.setBounds(320, 69, 61, 23);
						
						Label label = new Label(modSetGroup, SWT.NONE);
						label.setText("twords");
						label.setBounds(397, 72, 43, 17);
						
						twordsText = new Text(modSetGroup, SWT.BORDER);
						twordsText.setBounds(446, 69, 61, 23);
						
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
	private void onNewFileFolder(Text pathText) {
		FileDialog fileDialog = new FileDialog(this.getShell());
		fileDialog.setText("请选择对应的文件");
		String filePath = fileDialog.open();
		if (filePath != null)
			pathText.setText(filePath);
	}
	
	/**
	 * 保存preference value 的类
	 */
	public class PreferenceValue {
		
		// Artefact Configuration
		public String getSrcCodeProjectName() {
			return TraceableFormulaPreferencesComposite.this.sourceCodeProjectName.getText();
		}
		public String getWorkingFolder() {
			return TraceableFormulaPreferencesComposite.this.textWorkingFolder.getText();
		}
		public String getSoftDoc() {
			return TraceableFormulaPreferencesComposite.this.textSoftDoc.getText();
		}
		public String getTempFolder() {
			return TraceableFormulaPreferencesComposite.this.textTempFolder.getText();
		}
		public String getToolFolder() {
			return TraceableFormulaPreferencesComposite.this.textToolFolder.getText();
		}
		public String getDataDict() {
			return TraceableFormulaPreferencesComposite.this.textDataDict.getText();
		}
		public String getCnStopWords() {
			return TraceableFormulaPreferencesComposite.this.textCnStopWords.getText();
		}
		// LDA Est Arguments
		public String getLDAEstArgumentsAlpha() {
			return TraceableFormulaPreferencesComposite.this.alphaText.getText();
		}
		public String getLDAEstArgumentsBeta() {
			return TraceableFormulaPreferencesComposite.this.betaText.getText();
		}
		public String getLDAEstArgumentsNtopics() {
			return TraceableFormulaPreferencesComposite.this.ntopicsText.getText();
		}
		public String getLDAEstArgumentsNiters() {
			return TraceableFormulaPreferencesComposite.this.nitersText.getText();
		}
		public String getLDAEstArgumentsSavestep() {
			return TraceableFormulaPreferencesComposite.this.savestepText.getText();
		}
		public String getLDAEstArgumentsTwords() {
			return TraceableFormulaPreferencesComposite.this.twordsText.getText();
		}
	}
	/**
	 * Init the preference : 设置为上次执行时输入的值
	 */
	private void initStoredPrefs(IPreferenceStore pPreferenceStore) {
		// set Artefact Configuration
		this.sourceCodeProjectName.setText(pPreferenceStore.getString(PreferenceConstant.SRC_CODE_PROJ_NAME));
		this.textWorkingFolder.setText(pPreferenceStore.getString(PreferenceConstant.WORKING_FOLDER));
		this.textSoftDoc.setText(pPreferenceStore.getString(PreferenceConstant.SOFTWARE_DOCUMENT));
		this.textTempFolder.setText(pPreferenceStore.getString(PreferenceConstant.TEMP_FOLDER));
		this.textToolFolder.setText(pPreferenceStore.getString(PreferenceConstant.TOOL_FOLDER));
		this.textDataDict.setText(pPreferenceStore.getString(PreferenceConstant.DATA_DICT));
		this.textCnStopWords.setText(pPreferenceStore.getString(PreferenceConstant.CN_STOP_WORDS));
		// set LDA Est Arguments
		this.alphaText.setText(pPreferenceStore.getString(PreferenceConstant.LDA_EST_ARG_ALPHA));
		this.betaText.setText(pPreferenceStore.getString(PreferenceConstant.LDA_EST_ARG_BETA));
		this.ntopicsText.setText(pPreferenceStore.getString(PreferenceConstant.LDA_EST_ARG_NTOPICS));
		this.nitersText.setText(pPreferenceStore.getString(PreferenceConstant.LDA_EST_ARG_NITERS));
		this.savestepText.setText(pPreferenceStore.getString(PreferenceConstant.LDA_EST_ARG_SAVESTEP));
		this.twordsText.setText(pPreferenceStore.getString(PreferenceConstant.LDA_EST_ARG_TWORDS));
	}
}

