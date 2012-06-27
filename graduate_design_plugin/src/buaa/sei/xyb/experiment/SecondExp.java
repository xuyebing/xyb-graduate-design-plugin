package buaa.sei.xyb.experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 实验2代码
 * @author Xu Yebing
 */
public class SecondExp {

	// 找到LDA-LSI的结果log文件中的前500个关联关系，将其写到指定的文件中。
	public static void main(String[] args) {
		String inputDirPath = "D:\\exp2\\input";
		String outputFile = "D:\\exp2\\output.txt";
		
		MinHeap mh = new MinHeap(500); // 取前500个关联关系
		try {
			File inputDir = new File(inputDirPath);
			if (inputDir.exists() && inputDir.isDirectory()) {
				File[] inputFiles = inputDir.listFiles();
				for (File inputFile : inputFiles) {
					BufferedReader br = new BufferedReader(new FileReader(inputFile));
					String line = "";
					while ((line = br.readLine()) != null) {
						String[] tmp = line.split("\\s+");
						assert(tmp.length == 3);
						HeapNode hn = new HeapNode(tmp[0], tmp[1], Double.valueOf(tmp[2]));
						mh.insert(hn);
					}
					br.close();
				}
//				mh.writeHeapToFile(outputFile);
				mh.writeOrderedHeapToFile(outputFile);
			}
			System.out.println(">>> 2nd Exp Finish!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
