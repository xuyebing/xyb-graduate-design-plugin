package graduate_design_plugin;

import graduate_design_plugin.ui.PreferenceConstant;
import graduate_design_plugin.ui.TraceableFormulaPreferencesComposite;
import graduate_design_plugin.ui.TraceableFormulaPreferencesComposite.PreferenceValue;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import buaa.sei.xyb.analyse.modelcontrol.BuildModel;
import buaa.sei.xyb.common.Constant;


public class TraceableFormulaPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private TraceableFormulaPreferencesComposite traceableFormulaPreferencesComposite;
	
	public TraceableFormulaPreferencePage() {
		// TODO Auto-generated constructor stub
		super();
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Set the TraceableFormula preferences below");
	}

	public TraceableFormulaPreferencePage(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public TraceableFormulaPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Control createContents(Composite parent) {
		// TODO Auto-generated method stub
		this.traceableFormulaPreferencesComposite = new TraceableFormulaPreferencesComposite(parent, SWT.NULL);
		
		return this.traceableFormulaPreferencesComposite;
	}
	
	public boolean performOk() {
		IPreferenceStore preferenceStore = getPreferenceStore();
		PreferenceValue preferenceValue = this.traceableFormulaPreferencesComposite.getPreferenceValue();
		
		// 保存Artefact Configuration首选项中的值
		preferenceStore.setValue(PreferenceConstant.SRC_CODE_PROJ_NAME, 
				preferenceValue.getSrcCodeProjectName());
		preferenceStore.setValue(PreferenceConstant.WORKING_FOLDER,
				preferenceValue.getWorkingFolder());
		preferenceStore.setValue(PreferenceConstant.SOFTWARE_DOCUMENT,
				preferenceValue.getSoftDoc());
		preferenceStore.setValue(PreferenceConstant.TEMP_FOLDER, 
				preferenceValue.getTempFolder());
		preferenceStore.setValue(PreferenceConstant.TOOL_FOLDER,
				preferenceValue.getToolFolder());
		preferenceStore.setValue(PreferenceConstant.DATA_DICT,
				preferenceValue.getDataDict());
		preferenceStore.setValue(PreferenceConstant.CN_STOP_WORDS,
				preferenceValue.getCnStopWords());
		
		// 保存LDA est arguments组中的值
		preferenceStore.setValue(PreferenceConstant.LDA_EST_ARG_ALPHA,
				preferenceValue.getLDAEstArgumentsAlpha());
		preferenceStore.setValue(PreferenceConstant.LDA_EST_ARG_BETA,
				preferenceValue.getLDAEstArgumentsBeta());
		preferenceStore.setValue(PreferenceConstant.LDA_EST_ARG_NTOPICS,
				preferenceValue.getLDAEstArgumentsNtopics());
		preferenceStore.setValue(PreferenceConstant.LDA_EST_ARG_NITERS,
				preferenceValue.getLDAEstArgumentsNiters());
		preferenceStore.setValue(PreferenceConstant.LDA_EST_ARG_SAVESTEP,
				preferenceValue.getLDAEstArgumentsSavestep());
		preferenceStore.setValue(PreferenceConstant.LDA_EST_ARG_TWORDS,
				preferenceValue.getLDAEstArgumentsTwords());
		
		
		// GlobalVariant.sourceCodeProjectName = this.traceableFormulaPreferencesComposite.getsourceCodeProjectName();
		// GlobalVariant.softwareDocFolder = this.traceableFormulaPreferencesComposite.getSoftDoc();
		
		// 保存Artefact Configuration首选项中的值 到 Constant
		Constant.srcCodeProjectName = this.traceableFormulaPreferencesComposite.getSourceCodeProjectName();
		Constant.softwareDocFolder = this.traceableFormulaPreferencesComposite.getSoftDoc();
		Constant.tempFolder = this.traceableFormulaPreferencesComposite.getTempFolder();
		Constant.toolPath = this.traceableFormulaPreferencesComposite.getToolFolder();
		Constant.workingFolder = this.traceableFormulaPreferencesComposite.getWorkingFolder();
		Constant.dataDictPath = this.traceableFormulaPreferencesComposite.getDataDict();
		Constant.cnStopWordsFilePath = this.traceableFormulaPreferencesComposite.getCnStopWords();
		
		// 保存LDA est arguments组中的值 到 Constant
		Constant.estAlpha = Double.valueOf(this.traceableFormulaPreferencesComposite.getLDAEstArgumentsAlpha());
		Constant.estBeta = Double.valueOf(this.traceableFormulaPreferencesComposite.getLDAEstArgumentsBeta());
		Constant.estNtopics = Integer.valueOf(this.traceableFormulaPreferencesComposite.getLDAEstArgumentsNtopics());
		Constant.estNiters = Integer.valueOf(this.traceableFormulaPreferencesComposite.getLDAEstArgumentsNiters());
		Constant.estSavestep = Integer.valueOf(this.traceableFormulaPreferencesComposite.getLDAEstArgumentsSavestep());
		Constant.estTwords = Integer.valueOf(this.traceableFormulaPreferencesComposite.getLDAEstArgumentsTwords());
		
		// 进行preference更新
		Activator.getDefault().preferencesUpdate();
		
//		// 以下部分需要移动到view中，从view中启动工具分析
//		BuildModel buildModel = new BuildModel(Constant.softwareDocFolder, Constant.srcCodeProjectName);
//		try {
//			buildModel.build();
//		} catch (JavaModelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return true;
	}

}
