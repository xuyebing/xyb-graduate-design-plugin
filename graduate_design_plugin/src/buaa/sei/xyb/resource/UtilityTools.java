package buaa.sei.xyb.resource;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * UtilityTools 提供常用工具的类
 * @author Xu Yebing
 *
 */
public class UtilityTools {

	// 弹出包含指定信息的警告窗口
	public static void warning(Shell shell, String content) {
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING);
		messageBox.setMessage(content);
		messageBox.open();
	}
}
