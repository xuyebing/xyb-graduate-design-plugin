package buaa.sei.xyb.views;

import java.io.File;
import java.io.FileFilter;

public class ResultFileFilter implements FileFilter{

	private String suffix; // 后缀
	
	public ResultFileFilter(String suffix) {
		this.suffix = suffix;
	}

	@Override
	public boolean accept(File file) {
		// TODO Auto-generated method stub
		// 如果是文件夹，就返回false
		if(file.isDirectory()) {
			return false;
		}
		String name = file.getName();
		return name.endsWith(this.suffix);
	}
	
	
}
