package buaa.sei.xyb.experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * ʵ��2����
 * @author Xu Yebing
 */
public class SecondExp {

	// �ҵ�LDA-LSI�Ľ��log�ļ��е�ǰ500��������ϵ������д��ָ�����ļ��С�
	public static void main(String[] args) {
		String inputDirPath = "D:\\exp2\\input";
		String outputFile = "D:\\exp2\\output.txt";
		
		MinHeap mh = new MinHeap(500); // ȡǰ500��������ϵ
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
