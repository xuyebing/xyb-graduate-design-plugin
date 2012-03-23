package buaa.sei.xyb.dictionary;

import java.util.AbstractCollection;
import java.util.Vector;

public interface Dictionary {

	public boolean contains(String s);
	public AbstractCollection<String> getCandidates(String s);
	public Vector<String> getNonDictionaryWords(Vector<String> v);
}
