package buaa.sei.xyb.views;

import graduate_design_plugin.Activator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * TraceableFormulaView 是启动整个分析工具的入口
 * @author Xu Yebing
 */
public class TraceableFormulaView extends ViewPart {

	private TraceableFormulaViewComposite viewer = null;
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		this.viewer = new TraceableFormulaViewComposite(parent, 0, this);
		Activator.getDefault().addPreferenceListener(this.viewer);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		if (this.viewer != null) {
			this.viewer.setFocus();
		}
	}

}
