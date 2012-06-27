package buaa.sei.xyb.experiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * ʵ��2����Ҫ����С�����ݽṹ��ʵ��
 * @author Xu Yebing
 */
public class MinHeap {

	private HeapNode[] heapArray; // ������
	private int maxSize; // �ѵ�����С
	private int currentSize; // �ѵ�ǰ�Ĵ�С
	
	public MinHeap(int maxSize) {
		this.maxSize = maxSize;
		heapArray = new HeapNode[this.maxSize];
		currentSize = 0;
	}
	
	/*
	 * ���϶��µĵ���
	 */
	public void filterDown(int start, int endOfHeap) {
		int i = start;
		int j = 2 * i + 1; // jΪ i������
		HeapNode temp = heapArray[i];
		
		while (j <= endOfHeap) {
			if (j < endOfHeap && heapArray[j].rValue > heapArray[j+1].rValue) { // ��jָ��������Ů�е�С��
				j++;
			}
			if (temp.rValue <= heapArray[j].rValue) { // ���ڵ��������Ů�еĽ�С�߻�С������С�ѵ������
				break;
			} else { // ���򣬸��ڵ����ӽڵ��С�߻�����Ȼ������ж���������Ƿ�������С������
				heapArray[i] = heapArray[j];
				i = j;
				j = 2 * i + 1;
			}
		}
		heapArray[i] = temp; // iΪ����Ԫ�����յ�λ��
	}
	/*
	 * ���¶��ϵĵ��������ڳ�ʼ����ʱ���ڶ�����������Ԫ�غ󣬵�������С��
	 */
	public void filterUp(int start) {
		int j = start;
		int i = (j-1)/2;
		HeapNode temp = heapArray[j];
		
		while (j > 0) {
			if (heapArray[i].rValue <= temp.rValue) { // ˫�׽ڵ�ֵС�����õ���
				break;
			} else { // ˫�׽ڵ�ֵ����Ҫ����
				heapArray[j] = heapArray[i];
				j = i;
				i = (j-1)/2;
			}
		}
		heapArray[j] = temp;
	}
	/*
	 * ����в�����
	 */
	public void insert(HeapNode iNode) {
		if (isFull()) { // �����������²���Ԫ����Ѷ�Ԫ�ؽ��бȽϣ�iNode�ȶѶ�Ԫ�ش�����iNode�滻�Ѷ�Ԫ�أ�Ȼ�����϶��µ�����С��
			if (iNode.rValue > heapArray[0].rValue) {
				heapArray[0] = iNode;
				filterDown(0, maxSize-1);
			}
		} else { // ���²���Ԫ�ؼ���heap��������棬Ȼ�����¶��ϵ�����
			heapArray[currentSize] = iNode;
			filterUp(currentSize);
			currentSize++;
		}
	}
	// �ж϶��Ƿ�����
	public boolean isFull() {
		return currentSize == maxSize;
	}
	// �����С�ѵ���Ϣ��ָ���ļ���
	// ����: filePath = �ļ��ľ���·��
	public void writeHeapToFile(String filePath) {
		File file = new File(filePath);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (int i = 0; i < currentSize; i++) {
				StringBuilder sb = new StringBuilder(heapArray[i].codeName + "\t");
				sb.append(heapArray[i].subDocName + "\t");
				sb.append(heapArray[i].rValue + "\r\n");
				bw.write(sb.toString());
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/*
	 * writeOrderedHeapToFile: ����С���е����ݰ���codeName��������������ʵ�����
	 */
	public void writeOrderedHeapToFile(String filePath) {
		HashMap<String, ArrayList<HeapNode>> orderedHeapMap = new HashMap<String, ArrayList<HeapNode>>();
		for (int i = 0; i < currentSize; i++) {
			if (orderedHeapMap.containsKey(heapArray[i].codeName)) {
				ArrayList<HeapNode> values = orderedHeapMap.get(heapArray[i].codeName);
				values.add(heapArray[i]);
				orderedHeapMap.put(heapArray[i].codeName, values);
			} else {
				ArrayList<HeapNode> values= new ArrayList<HeapNode>();
				values.add(heapArray[i]);
				orderedHeapMap.put(heapArray[i].codeName, values);
			}
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
			// ��ÿ��codeName��Ӧ��ֵ���ϰ������ƶȴ�С��������
			Set<String> keySet = orderedHeapMap.keySet();
			int setSize = keySet.size();
			System.out.println(">>> �������� " + setSize + " ����");
			for (Iterator<String> it = keySet.iterator(); it.hasNext(); ) {
				String codeName = it.next();
				ArrayList<HeapNode> values = orderedHeapMap.get(codeName);
				Collections.sort(values);
				// �����������ļ���
				for (Iterator<HeapNode> it2 = values.iterator(); it2.hasNext(); ) {
					HeapNode thn = it2.next();
					StringBuilder sb = new StringBuilder(thn.codeName + "\t");
					sb.append(thn.subDocName + "\t");
					sb.append(thn.rValue + "\r\n");
					bw.write(sb.toString());
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
