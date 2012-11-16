package buaa.sei.xyb.experiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Diff2TraceLinkFile: 比较新、旧两个Top500关联关系文件的异同，节省判断关联关系正确性的操作。
 *   旧的前500个关联关系的正确性已经进行了判断，对于新的前500个关联关系，只需要判断原来没判断过的、新增的关联关系的正确性即可。
 * @author Xu Yebing
 *
 */
public class Diff2TraceLinkFile {

	private String oldFile; // 旧的Top500关联关系文件
	private String newFile; // 新的Top500关联关系文件
	private Set<String> oldSet; // 保存旧文件中包含的关联关系的集合，每个元素形如:"Directory.wds\t概要设计_104.wds"
	private Set<String> newSet; // 保存新文件中包含的关联关系的集合
	private String outputFile = "D:\\exp2\\diff2TraceLinkSet.txt"; // 保存diff结果的文件路径
	
	public Diff2TraceLinkFile (String oldFile, String newFile) {
		this.oldFile = oldFile;
		this.newFile = newFile;
	}
	private void readTraceLink() {
		// 从新、旧文件中读取关联关系，并将其保存在对应的oldSet或newSet集合中
		try {
			BufferedReader br = new BufferedReader(new FileReader(this.oldFile));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] fields = line.split("\t");
				assert(fields.length == 3);
				// 将fields的前两个字段用“\t”拼接成关联关系条目，将其保存在对应集合中
				if (oldSet == null)
					oldSet = new HashSet<String>();
				oldSet.add(fields[0] + "\t" + fields[1]);
			}
			br.close();
			br = new BufferedReader(new FileReader(this.newFile));
			line = "";
			while ((line = br.readLine()) != null) {
				String[] fields = line.split("\t");
				assert(fields.length == 3);
				if (newSet == null)
					newSet = new HashSet<String>();
				newSet.add(fields[0] + "\t" + fields[1]);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void diff () {
		readTraceLink(); // 初始化新、旧两个关联关系集合
		// 以新集合为基础，对两个集合求差集，保留新集合中不在旧集合中的元素
		Set<String> tmpNewSet = new HashSet<String>(newSet);
		tmpNewSet.removeAll(oldSet);
		List<String> resultList = new ArrayList<String>(tmpNewSet);
		// 排序,便于统计分析
		Collections.sort(resultList);
		// 输出求差后的集合中的元素
		try {
			File outputFile = new File(this.outputFile);
			if (outputFile.exists())
				outputFile.delete();
			BufferedWriter bw = new BufferedWriter(new FileWriter(this.outputFile));
			for (String traceLink : resultList) {
				bw.write(traceLink+"\n");
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		String oldFile = "D:\\exp2\\output-old.txt";
		String newFile = "D:\\exp2\\output.txt";
		
		Diff2TraceLinkFile d2tf = new Diff2TraceLinkFile(oldFile, newFile);
		d2tf.diff();
		System.out.println("Diff Finished!");
	}
}
