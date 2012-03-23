package buaa.sei.xyb.dictionary;

public class CommonAbbreviation {
	private String shortform;
	private String type;
	private String longform;
	
	public CommonAbbreviation(String sf, String t, String lf) {
		shortform=sf;
		type=t;
		longform=lf;
	}

	public String getShortform() {
		return shortform;
	}

	public int getType() {
		if(type.equals("AC"))
			return 1;
		else if(type.equals("PR") || type.equals("PP"))
			return 2;
		else if(type.equals("DL"))
			return 3;
		else return 4;
	}

	public String getLongform() {
		return longform;
	}			
	
	public String toString() {
		return shortform + " -> " + longform + " (" + type + ")\n";
	}
}