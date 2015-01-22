package edu.stanford.rad.ner.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class ValueComparatorComplex implements Comparator<ArrayList<String>> {

	Map<ArrayList<String>, Integer> base;

	public ValueComparatorComplex(Map<ArrayList<String>, Integer> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with
	// equals.
	public int compare(ArrayList<String> a, ArrayList<String> b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}