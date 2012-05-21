package buaa.sei.xyb.analyse.document;

/**
 * DocInfo类 用于记录文档段相关的信息，如：文档段所在的绝对路径，文档段的长度，偏移量，父文档名等等
 * 该类的用途：
 *   （1）用于在显示与某个代码相关的文档段列表的view中，双击某个文档段名称时，能够在editor中根据绝对路径absPath打开对应的文档段
 * @author Xu Yebing
 *
 */
public class DocInfo {

	public String parentName; // 文档段所属的父文档名
	public int offset; // 文档段在父文档中的偏移量
	public int length; // 文档段的长度
	public String absPath; // 文档段所在的绝对路径
}
