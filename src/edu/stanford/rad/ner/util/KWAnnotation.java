package edu.stanford.rad.ner.util;

public class KWAnnotation implements Comparable<KWAnnotation>{
	String mention;
	String annotator;
	public int start;
	public int end;
	public String spannedText;
	String creationDate;
	public String mentionClass;
	
	KWAnnotation(String mention, String annotator, int start, int end, String spannedText, String creationDate){
		this.mention = mention;
		this.annotator = annotator;
		this.start = start;
		this.end = end;
		this.spannedText = spannedText;
		this.creationDate = creationDate;
	}

	@Override
	public int compareTo(KWAnnotation kw) {
		return this.start - kw.start;
	}
	
	@Override 
	public String toString() {
		return spannedText + " " +  start + " - " + end + " : " + mentionClass;
	}

}
