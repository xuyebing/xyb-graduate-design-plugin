package buaa.sei.xyb.process.irmodel;

import sei.buaa.IR.documentSpace.BasicTermByDocumentMatrix;
import sei.buaa.IR.documentSpace.ITermByDocumentMatrix;

/**
 * LSIProcess ��ʦ��ʵ�ְ汾�н�ȡ����LSI������̣����ڶ�LDA���������LSI����
 * @author Xu Yebing
 *
 */
public class LSIProcess {
	/* ��plugin.xml �� Dependencies ҳ��� Required Plug-ins�¼����˶�sei.buaa.linktracer.IR������*/
	private static ITermByDocumentMatrix<String, String> termByDocumentMatrix;
	private static String resultDirString;

//	private static void initMatrix() {
//		termByDocumentMatrix = new BasicTermByDocumentMatrix(
//				new LogEntropyWeigher(), new FileDocumentSpaceStorage<String, String>(resultDirString));
//	}
}
