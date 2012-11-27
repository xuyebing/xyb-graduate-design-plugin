package buaa.sei.xyb.experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * ʵ��2���裺
 * 1.Input: ��exp2/input�ļ����б�������lda_lsi_result_i-j.log���ļ���������ȡǰN��������ϵ��Ϊ����ʵ������������ռ�
 *   Output: D:\\exp2\\output.txt (��ȡ��ǰN��������ϵ)
 * 2. �˹�����output.txt�й�����ϵ����ȷ�ԣ���������ȷ�Ĺ�����ϵ���浽correctLinks.txt�ļ��У���ʽ���磺��Directory.wds\t��Ҫ���_104.wds��
 * 3. Input Dir: exp2/inputCP, ���ļ��б���������Ĺ�����ϵ����ļ������磺lda_lsi_result_i-j.log
 * 	  Input file�� exp2/correctLinks.txt, ��Ϊ��ȷ������ϵ�ı�׼
 * 	  Output : ֱ�����ն������ȫ�ʺͲ�׼�ʵļ�����
 */
/**
 * ʵ��2����
 * @author Xu Yebing
 */
public class SecondExp {

	// �ҵ�LDA-LSI�Ľ��log�ļ��е�ǰ500��������ϵ������д��ָ�����ļ��С�
	public static void main(String[] args) {
		String inputDirPath = "D:\\exp4\\�߲��ĵ������������\\LDA����\\input";
		String outputFile = "D:\\exp4\\�߲��ĵ������������\\LDA����\\output.txt";
		
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
