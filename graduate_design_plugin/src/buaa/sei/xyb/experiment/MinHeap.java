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
 * 实验2所需要的最小堆数据结构的实现
 * @author Xu Yebing
 */
public class MinHeap {

	private HeapNode[] heapArray; // 堆容器
	private int maxSize; // 堆的最大大小
	private int currentSize; // 堆当前的大小
	
	public MinHeap(int maxSize) {
		this.maxSize = maxSize;
		heapArray = new HeapNode[this.maxSize];
		currentSize = 0;
	}
	
	/*
	 * 自上而下的调整
	 */
	public void filterDown(int start, int endOfHeap) {
		int i = start;
		int j = 2 * i + 1; // j为 i的左孩子
		HeapNode temp = heapArray[i];
		
		while (j <= endOfHeap) {
			if (j < endOfHeap && heapArray[j].rValue > heapArray[j+1].rValue) { // 令j指向两个子女中的小者
				j++;
			}
			if (temp.rValue <= heapArray[j].rValue) { // 父节点比两个子女中的较小者还小，则最小堆调整完毕
				break;
			} else { // 否则，父节点与子节点较小者互换，然后继续判断深层子树是否满足最小堆条件
				heapArray[i] = heapArray[j];
				i = j;
				j = 2 * i + 1;
			}
		}
		heapArray[i] = temp; // i为插入元素最终的位置
	}
	/*
	 * 自下而上的调整，用于初始建堆时，在堆数组后面加入元素后，调整到最小堆
	 */
	public void filterUp(int start) {
		int j = start;
		int i = (j-1)/2;
		HeapNode temp = heapArray[j];
		
		while (j > 0) {
			if (heapArray[i].rValue <= temp.rValue) { // 双亲节点值小，不用调整
				break;
			} else { // 双亲节点值大，需要调整
				heapArray[j] = heapArray[i];
				j = i;
				i = (j-1)/2;
			}
		}
		heapArray[j] = temp;
	}
	/*
	 * 向堆中插入结点
	 */
	public void insert(HeapNode iNode) {
		if (isFull()) { // 堆已满，将新插入元素与堆顶元素进行比较，iNode比堆顶元素大，则用iNode替换堆顶元素，然后自上而下调整最小堆
			if (iNode.rValue > heapArray[0].rValue) {
				heapArray[0] = iNode;
				filterDown(0, maxSize-1);
			}
		} else { // 将新插入元素加入heap数组最后面，然后自下而上调整。
			heapArray[currentSize] = iNode;
			filterUp(currentSize);
			currentSize++;
		}
	}
	// 判断堆是否已满
	public boolean isFull() {
		return currentSize == maxSize;
	}
	// 输出最小堆的信息到指定文件中
	// 参数: filePath = 文件的绝对路径
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
	 * writeOrderedHeapToFile: 将最小堆中的内容按照codeName归类后输出，便于实验分析
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
			// 对每个codeName对应的值集合按照相似度大小降序排列
			Set<String> keySet = orderedHeapMap.keySet();
			int setSize = keySet.size();
			System.out.println(">>> 共包含了 " + setSize + " 个类");
			for (Iterator<String> it = keySet.iterator(); it.hasNext(); ) {
				String codeName = it.next();
				ArrayList<HeapNode> values = orderedHeapMap.get(codeName);
				Collections.sort(values);
				// 排序后输出到文件中
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
