package buaa.sei.xyb.views;

import java.io.File;
import java.io.FileFilter;

public class ResultFileFilter implements FileFilter{

	private String suffix; // ��׺
	
	public ResultFileFilter(String suffix) {
		this.suffix = suffix;
	}

	@Override
	public boolean accept(File file) {
		// TODO Auto-generated method stub
		// ������ļ��У��ͷ���false
		if(file.isDirectory()) {
			return false;
		}
		String name = file.getName();
		return name.endsWith(this.suffix);
	}
	
	
}
