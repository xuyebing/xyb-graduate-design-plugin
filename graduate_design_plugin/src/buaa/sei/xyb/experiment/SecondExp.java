package buaa.sei.xyb.experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 实验2步骤：
 * 1.Input: 在exp2/input文件夹中保存形如lda_lsi_result_i-j.log的文件，用于提取前N个关联关系作为后续实验分析的样本空间
 *   Output: D:\\exp2\\output.txt (抽取的前N个关联关系)
 * 2. 人工分析output.txt中关联关系的正确性，将所有正确的关联关系保存到correctLinks.txt文件中，格式形如：“Directory.wds\t概要设计_104.wds”
 * 3. Input Dir: exp2/inputCP, 该文件夹保存待分析的关联关系结果文件，形如：lda_lsi_result_i-j.log
 * 	  Input file： exp2/correctLinks.txt, 作为正确关联关系的标准
 * 	  Output : 直接在终端输出查全率和查准率的计算结果
 */
/**
 * 实验2代码
 * @author Xu Yebing
 */
public class SecondExp {

	// 找到LDA-LSI的结果log文件中的前500个关联关系，将其写到指定的文件中。
	public static void main(String[] args) {
		String inputDirPath = "D:\\exp4\\高层文档与代码的相关性\\LDA方法\\input";
		String outputFile = "D:\\exp4\\高层文档与代码的相关性\\LDA方法\\output.txt";
		
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
