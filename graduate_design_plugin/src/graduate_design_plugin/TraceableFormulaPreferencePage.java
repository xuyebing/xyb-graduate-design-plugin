package graduate_design_plugin;

import graduate_design_plugin.ui.TraceableFormulaPreferencesComposite;

import org.eclipse.jdt.core.JavaModelException;
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

	考虑保存代码分析的结果到文件中，从而建立矩阵
}
