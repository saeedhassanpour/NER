
package edu.stanford.rad.ner.stats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import edu.stanford.rad.ner.util.KWAnnotation;
import edu.stanford.rad.ner.util.ReadKWAnnotationsForSummary;

public class CorpusStatistics  {

	public static void main(String[] args) throws FileNotFoundException,IOException {
		long startTime = System.currentTimeMillis();
		
		Map<String, LinkedHashSet<String>> AnatomyMap = new TreeMap<String, LinkedHashSet<String>>();
		Map<String, LinkedHashSet<String>> ObservationMap = new TreeMap<String, LinkedHashSet<String>>();
		Map<String, LinkedHashSet<String>> AnatomyModifierMap = new TreeMap<String, LinkedHashSet<String>>();
		Map<String, LinkedHashSet<String>> ModalityMap = new TreeMap<String, LinkedHashSet<String>>();
		Map<String, LinkedHashSet<String>> ObservationModifierMap = new TreeMap<String, LinkedHashSet<String>>();
		Map<String, LinkedHashSet<String>> UncertaintyMap = new TreeMap<String, LinkedHashSet<String>>();
		Map<String, LinkedHashSet<String>> zImageLocationMap = new TreeMap<String, LinkedHashSet<String>>();
		Map<String, LinkedHashSet<String>> zRecommendationStrengthMap = new TreeMap<String, LinkedHashSet<String>>();
		Map<String, LinkedHashSet<String>> zSpatialMap = new TreeMap<String, LinkedHashSet<String>>();
		Map<String, LinkedHashSet<String>> zTechniqueMap = new TreeMap<String, LinkedHashSet<String>>();
		Map<String, LinkedHashSet<String>> zTemporalMap = new TreeMap<String, LinkedHashSet<String>>();

		
		final File folder = new File("files/corpus");
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory()) {
				String fileName = fileEntry.getName();
				String filepath = fileEntry.getPath();
				String XmlfileName = "files/annotation/" + fileName + ".knowtator.xml";
				File Xmlfile = new File(XmlfileName);
				if (!Xmlfile.exists()) {
					System.out.println(XmlfileName + " not found! " + fileName + " is skipped.");
					continue;
				}
				System.out.println("Working on " + fileName + "...");
				Scanner scanner = new Scanner(new File(filepath), "UTF-8");
				String text = scanner.useDelimiter("\\Z").next();
				scanner.close();
				text = text.replaceAll("\r\n", "\n");
				String sfile = fileName.split("_")[0];

				ReadKWAnnotationsForSummary readKWAnnotations = new ReadKWAnnotationsForSummary();
				Map<String, KWAnnotation> kwAnnotationMap = readKWAnnotations.read(XmlfileName);
				List<KWAnnotation> kwAnnotationList = new ArrayList<KWAnnotation>(kwAnnotationMap.values());
				Collections.sort(kwAnnotationList);
				
				List<Integer> bounds = new ArrayList<Integer>();
				int ref = 0;
				while (ref != -1)
				{
					ref = text.indexOf("********************************************" , ref+1);
					if(ref != -1)
						bounds.add(ref);
				}				

				for (KWAnnotation kw : kwAnnotationList) {
					String word = kw.spannedText.toLowerCase();;
					String mentionClass = kw.mentionClass;
					int end = kw.end;
					int counter = 1;
					
					for (int bound : bounds){
						if (bound < end)
						{
							++counter;
						}
					}
					LinkedHashSet<String> reportList;
					
					if (mentionClass.equals("Anatomy")) {
						if (AnatomyMap.containsKey(word)) {
							reportList = AnatomyMap.get(word);
						} else {
							reportList = new LinkedHashSet<String>();
						}
						reportList.add(sfile + "_" + counter);
						AnatomyMap.put(word, reportList);
					} else if (mentionClass.equals("Observation")) {
						if (ObservationMap.containsKey(word)) {
							reportList = ObservationMap.get(word);
						} else {
							reportList = new LinkedHashSet<String>();
						}
						reportList.add(sfile + "_" + counter);
						ObservationMap.put(word, reportList);
					} else if (mentionClass.equals("Anatomy Modifier")) {
						if (AnatomyModifierMap.containsKey(word)) {
							reportList = AnatomyModifierMap.get(word);
						} else {
							reportList = new LinkedHashSet<String>();
						}
						reportList.add(sfile + "_" + counter);
						AnatomyModifierMap.put(word, reportList);
					} else if (mentionClass.equals("Modality")) {
						if (ModalityMap.containsKey(word)) {
							reportList = ModalityMap.get(word);
						} else {
							reportList = new LinkedHashSet<String>();
						}
						reportList.add(sfile + "_" + counter);
						ModalityMap.put(word, reportList);
					} else if (mentionClass.equals("Observation Modifier")) {
						if (ObservationModifierMap.containsKey(word)) {
							reportList = ObservationModifierMap.get(word);
						} else {
							reportList = new LinkedHashSet<String>();
						}
						reportList.add(sfile + "_" + counter);
						ObservationModifierMap.put(word, reportList);
					} else if (mentionClass.equals("Uncertainty")) {
						if (UncertaintyMap.containsKey(word)) {
							reportList = UncertaintyMap.get(word);
						} else {
							reportList = new LinkedHashSet<String>();
						}
						reportList.add(sfile + "_" + counter);
						UncertaintyMap.put(word, reportList);
					} else if (mentionClass.equals("zImage Location")) {
						if (zImageLocationMap.containsKey(word)) {
							reportList = zImageLocationMap.get(word);
						} else {
							reportList = new LinkedHashSet<String>();
						}
						reportList.add(sfile + "_" + counter);
						zImageLocationMap.put(word, reportList);
					} else if (mentionClass
							.equals("zRecommendation Strength")) {
						if (zRecommendationStrengthMap.containsKey(word)) {
							reportList = zRecommendationStrengthMap
									.get(word);
						} else {
							reportList = new LinkedHashSet<String>();
						}
						reportList.add(sfile + "_" + counter);
						zRecommendationStrengthMap.put(word, reportList);
					} else if (mentionClass.equals("zSpatial")) {
						if (zSpatialMap.containsKey(word)) {
							reportList = zSpatialMap.get(word);
						} else {
							reportList = new LinkedHashSet<String>();
						}
						reportList.add(sfile + "_" + counter);
						zSpatialMap.put(word, reportList);
					} else if (mentionClass.equals("zTechnique")) {
						if (zTechniqueMap.containsKey(word)) {
							reportList = zTechniqueMap.get(word);
						} else {
							reportList = new LinkedHashSet<String>();
						}
						reportList.add(sfile + "_" + counter);
						zTechniqueMap.put(word, reportList);
					} else if (mentionClass.equals("zTemporal")) {
						if (zTemporalMap.containsKey(word)) {
							reportList = zTemporalMap.get(word);
						} else {
							reportList = new LinkedHashSet<String>();
						}
						reportList.add(sfile + "_" + counter);
						zTemporalMap.put(word, reportList);
					}
					//System.out.println(word + "\t" + mentionClass + "\t" + counter + "\n");
				}
			}
		}
		
		
		//System.out.println(anatomyMap);
		PrintWriter pw = new PrintWriter("files/stat/AnatomyMap.tsv", "UTF-8");
		for (String key : AnatomyMap.keySet()) {
			pw.print(key + "\t" + AnatomyMap.get(key) + "\n");
		}
		pw.close();
		pw = new PrintWriter("files/stat/ObservationMap.tsv", "UTF-8");
		for (String key : ObservationMap.keySet()) {
			pw.print(key + "\t" + ObservationMap.get(key) + "\n");
		}
		pw.close();
		pw = new PrintWriter("files/stat/AnatomyModifierMap.tsv", "UTF-8");
		for (String key : AnatomyModifierMap.keySet()) {
			pw.print(key + "\t" + AnatomyModifierMap.get(key) + "\n");
		}
		pw.close();
		pw = new PrintWriter("files/stat/ModalityMap.tsv", "UTF-8");
		for (String key : ModalityMap.keySet()) {
			pw.print(key + "\t" + ModalityMap.get(key) + "\n");
		}
		pw.close();
		pw = new PrintWriter("files/stat/ObservationModifierMap.tsv", "UTF-8");
		for (String key : ObservationModifierMap.keySet()) {
			pw.print(key + "\t" + ObservationModifierMap.get(key) + "\n");
		}
		pw.close();
		pw = new PrintWriter("files/stat/UncertaintyMap.tsv", "UTF-8");
		for (String key : UncertaintyMap.keySet()) {
			pw.print(key + "\t" + UncertaintyMap.get(key) + "\n");
		}
		pw.close();
		pw = new PrintWriter("files/stat/zImageLocationMap.tsv", "UTF-8");
		for (String key : zImageLocationMap.keySet()) {
			pw.print(key + "\t" + zImageLocationMap.get(key) + "\n");
		}
		pw.close();
		pw = new PrintWriter("files/stat/zRecommendationStrengthMap.tsv", "UTF-8");
		for (String key : zRecommendationStrengthMap.keySet()) {
			pw.print(key + "\t" + zRecommendationStrengthMap.get(key) + "\n");
		}
		pw.close();
		pw = new PrintWriter("files/stat/zSpatialMap.tsv", "UTF-8");
		for (String key : zSpatialMap.keySet()) {
			pw.print(key + "\t" + zSpatialMap.get(key) + "\n");
		}
		pw.close();
		pw = new PrintWriter("files/stat/zTechniqueMap.tsv", "UTF-8");
		for (String key : zTechniqueMap.keySet()) {
			pw.print(key + "\t" + zTechniqueMap.get(key) + "\n");
		}
		pw.close();
		pw = new PrintWriter("files/stat/zTemporalMap.tsv", "UTF-8");
		for (String key : zTemporalMap.keySet()) {
			pw.print(key + "\t" + zTemporalMap.get(key) + "\n");
		}
		pw.close();
				
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Finshed in " + totalTime/1000.0 + " seconds");
	}
	
}
