package buaa.sei.xyb.process.irmodel;

import sei.buaa.IR.documentSpace.BasicTermByDocumentMatrix;
import sei.buaa.IR.documentSpace.ITermByDocumentMatrix;

/**
 * LSIProcess 从师兄实现版本中截取出的LSI处理过程，用于对LDA的输出进行LSI分析
 * @author Xu Yebing
 *
 */
public class LSIProcess {
	/* 在plugin.xml 的 Dependencies 页面的 Required Plug-ins下加入了对sei.buaa.linktracer.IR的引用*/
	private static ITermByDocumentMatrix<String, String> termByDocumentMatrix;
	private static String resultDirString;

//	private static void initMatrix() {
//		termByDocumentMatrix = new BasicTermByDocumentMatrix(
//				new LogEntropyWeigher(), new FileDocumentSpaceStorage<String, String>(resultDirString));
//	}
}
