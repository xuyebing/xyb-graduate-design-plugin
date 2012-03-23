package buaa.sei.xyb.dictionary;

import java.util.HashSet;

public class ArrayElement {

	private HashSet<String> letterSet;
	
	public ArrayElement() {
		letterSet = new HashSet<String>();
	}
	
	public HashSet<String> getSet() {
		return this.letterSet;
	}
}
