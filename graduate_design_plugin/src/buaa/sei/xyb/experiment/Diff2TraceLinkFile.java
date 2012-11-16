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
 * Diff2TraceLinkFile: �Ƚ��¡�������Top500������ϵ�ļ�����ͬ����ʡ�жϹ�����ϵ��ȷ�ԵĲ�����
 *   �ɵ�ǰ500��������ϵ����ȷ���Ѿ��������жϣ������µ�ǰ500��������ϵ��ֻ��Ҫ�ж�ԭ��û�жϹ��ġ������Ĺ�����ϵ����ȷ�Լ��ɡ�
 * @author Xu Yebing
 *
 */
public class Diff2TraceLinkFile {

	private String oldFile; // �ɵ�Top500������ϵ�ļ�
	private String newFile; // �µ�Top500������ϵ�ļ�
	private Set<String> oldSet; // ������ļ��а����Ĺ�����ϵ�ļ��ϣ�ÿ��Ԫ������:"Directory.wds\t��Ҫ���_104.wds"
	private Set<String> newSet; // �������ļ��а����Ĺ�����ϵ�ļ���
	private String outputFile = "D:\\exp2\\diff2TraceLinkSet.txt"; // ����diff������ļ�·��
	
	public Diff2TraceLinkFile (String oldFile, String newFile) {
		this.oldFile = oldFile;
		this.newFile = newFile;
	}
	private void readTraceLink() {
		// ���¡����ļ��ж�ȡ������ϵ�������䱣���ڶ�Ӧ��oldSet��newSet������
		try {
			BufferedReader br = new BufferedReader(new FileReader(this.oldFile));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] fields = line.split("\t");
				assert(fields.length == 3);
				// ��fields��ǰ�����ֶ��á�\t��ƴ�ӳɹ�����ϵ��Ŀ�����䱣���ڶ�Ӧ������
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
		readTraceLink(); // ��ʼ���¡�������������ϵ����
		// ���¼���Ϊ�������������������������¼����в��ھɼ����е�Ԫ��
		Set<String> tmpNewSet = new HashSet<String>(newSet);
		tmpNewSet.removeAll(oldSet);
		List<String> resultList = new ArrayList<String>(tmpNewSet);
		// ����,����ͳ�Ʒ���
		Collections.sort(resultList);
		// �������ļ����е�Ԫ��
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
