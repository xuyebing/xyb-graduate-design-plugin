package buaa.sei.xyb.resource;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * UtilityTools �ṩ���ù��ߵ���
 * @author Xu Yebing
 *
 */
public class UtilityTools {

	// ��������ָ����Ϣ�ľ��洰��
	public static void warning(Shell shell, String content) {
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING);
		messageBox.setMessage(content);
		messageBox.open();
	}
}
