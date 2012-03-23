package buaa.sei.xyb.dictionary;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.JButton;


public class ImprovedDictionary implements Dictionary {
	private ArrayElement[]dictionary;
	
	private static int A = 0;
	private static int B = 1;
	private static int C = 2;
	private static int D = 3;
	private static int E = 4;
	private static int F = 5;
	private static int G = 6;
	private static int H = 7;
	private static int I = 8;
	private static int J = 9;
	private static int K = 10;
	private static int L = 11;
	private static int M = 12;
	private static int N = 13;
	private static int O = 14;
	private static int P = 15;
	private static int Q = 16;
	private static int R = 17;
	private static int S = 18;
	private static int T = 19;
	private static int U = 20;
	private static int V = 21;
	private static int W = 22;
	private static int X = 23;
	private static int Y = 24;
	private static int Z = 25;
	
	public static void main(String argv[]) {
		ImprovedDictionary id = new ImprovedDictionary("/home/fry/workspace/Abbreviations/dict/wordnet.txt");
		Vector<String> vi = new Vector<String>();
		vi.add("new");
		vi.add("lgi");
		vi.add("apple");
		vi.add("db");
		vi.add("mnb");
		vi.add("banana");
		vi.add("lri");
		vi.add("str");
		vi.add("cat");
		vi.add("gif");
		vi.add("dog");
		Vector<String> v = id.getNonDictionaryWords(vi);
		
		for(String s:v){
			System.out.println(s);
		}
		
	}
	
	public ImprovedDictionary(String fileName) {
		dictionary = new ArrayElement[26];
		for (int i=0; i<26; i++) {
			dictionary[i] = new ArrayElement();
		}
		loadDictionary(fileName);
	}
	
	public ArrayElement getArrayElement(int letter) {
		return dictionary[letter];
	}
	
	public ArrayElement getArrayElement(char letter) {
		switch(letter) {
		case 'a':
			return dictionary[A];
		case 'b':
			return dictionary[B];
		case 'c':
			return dictionary[C];
		case 'd':
			return dictionary[D];
		case 'e':
			return dictionary[E];
		case 'f':
			return dictionary[F];
		case 'g':
			return dictionary[G];
		case 'h':
			return dictionary[H];
		case 'i':
			return dictionary[I];
		case 'j':
			return dictionary[J];
		case 'k':
			return dictionary[K];
		case 'l':
			return dictionary[L];
		case 'm':
			return dictionary[M];
		case 'n':
			return dictionary[N];
		case 'o':
			return dictionary[O];
		case 'p':
			return dictionary[P];
		case 'q':
			return dictionary[Q];
		case 'r':
			return dictionary[R];
		case 's':
			return dictionary[S];
		case 't':
			return dictionary[T];
		case 'u':
			return dictionary[U];
		case 'v':
			return dictionary[V];
		case 'w':
			return dictionary[W];
		case 'x':
			return dictionary[X];
		case 'y':
			return dictionary[Y];
		case 'z':
			return dictionary[Z];
			default:
				return new ArrayElement();		
		}
		
	}
	
	public ImprovedDictionary(AbstractCollection list) {
		loadDictionary(list);
	}
	
//	from file
	public void loadDictionary(String fileName) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String word;
			while ((word = in.readLine()) != null) {
				word = word.toLowerCase(); // make sure all lower case!
				char letter = word.charAt(0);
				getArrayElement(letter).getSet().add(word);
				//dictionary.get(letter).add(word);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//from list
	public void loadDictionary(AbstractCollection list) {
		Iterator it = list.iterator();
		String word;
		while(it.hasNext()) {
			word = (String)it.next();
			char letter = word.charAt(0);
			getArrayElement(letter).getSet().add(word);
			//dictionary.get(letter).add(word);
		}
	}
	
	public AbstractCollection<String> getCandidates(String abbrev) {
		AbstractCollection<String> list = new LinkedList<String>();
		char letter = abbrev.charAt(0);
		Iterator it = getArrayElement(letter).getSet().iterator();
		while (it.hasNext()) {
			String word = (String)it.next();
			boolean res = Pattern.matches(abbrev, word);
			if (res) 
			{
				list.add(word);
			}
		}
		return list;
	}

	
	public boolean contains(String word) {
		if (word.length() > 0) {
			word = word.toLowerCase();
			char letter = word.charAt(0);
			if (getArrayElement(letter).getSet().contains(word))
				return true;
		}
		return false;
	}
	
	public Vector<String> getNonDictionaryWords(Vector<String> v)
	{
		Vector<String> vectorCopy = new Vector<String>(v);
		for(String s:v)
		{
			if(contains(s)) vectorCopy.removeElement(s);
		}
		return vectorCopy;
	}
	
	public String toString() {
		String dict = "";
		ArrayElement ae;
		for(int i=0; i<26; i++) {
			ae = getArrayElement(i);
			for(String s:ae.getSet()) {
				dict.concat(s + "  ");
			}
		}
		return dict;
	}
}
