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
		
		// 保存首选项中的值
		preferenceStore.setValue(PreferenceConstant.SRC_CODE_PROJ_NAME, 
				preferenceValue.getSrcCodeProjectName());
		preferenceStore.setValue(PreferenceConstant.WORKING_FOLDER,
				preferenceValue.getWorkingFolder());
		preferenceStore.setValue(PreferenceConstant.SOFTWARE_DOCUMENT,
				preferenceValue.getSoftDoc());
		preferenceStore.setValue(PreferenceConstant.TOOL_FOLDER,
				preferenceValue.getToolFolder());
		preferenceStore.setValue(PreferenceConstant.DATA_DICT,
				preferenceValue.getDataDict());
		// 进行preference更新
		Activator.getDefault().preferencesUpdate();
		
		// GlobalVariant.sourceCodeProjectName = this.traceableFormulaPreferencesComposite.getsourceCodeProjectName();
		// GlobalVariant.softwareDocFolder = this.traceableFormulaPreferencesComposite.getSoftDoc();
		String srcCodeProjectName = this.traceableFormulaPreferencesComposite.getSourceCodeProjectName();
		String softwareDocFolder = this.traceableFormulaPreferencesComposite.getSoftDoc();
		Constant.toolPath = this.traceableFormulaPreferencesComposite.getToolFolder();
		
		BuildModel buildModel = new BuildModel(softwareDocFolder, srcCodeProjectName);
		try {
			buildModel.build();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
