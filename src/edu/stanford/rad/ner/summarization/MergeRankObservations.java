package edu.stanford.rad.ner.summarization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.stanford.rad.ner.util.ValueComparator;

public class MergeRankObservations {

	static Map<String, Double> idfs;
	static Map<String, Double> observations = new HashMap<String, Double>();

	static List<String> words = new ArrayList<String>();
	static List<String> boundaries = new ArrayList<String>();
	static List<String> tags = new ArrayList<String>();
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		String idfFileName = "idfAllFiltered"; //idfAll,idfFiltered
		idfs = idfFiletoMap(idfFileName);
		boolean sub = true;

		final File Datafolder = new File("files/summarizatonInput");
		for (final File inputFile : Datafolder.listFiles()) {
			if (!inputFile.isDirectory() && !inputFile.getName().startsWith(".")) {
				String inputFileName = inputFile.getName();
				File tagFile = new File("files/summarizatonTags/tagged_" + inputFileName);
				if (!tagFile.exists()) {
					System.out.println(tagFile.getName() + " not found! " + inputFileName + " is skipped.");
					continue;
				}
				System.out.println("Working on " + inputFileName + "...");
				String line;

				words.clear();
				boundaries.clear();
				BufferedReader bReader = new BufferedReader(new FileReader(inputFile));
				while ((line = bReader.readLine()) != null) {
					if (line.isEmpty())
						continue;
					String datavalue[] = line.split("\t");
					if (datavalue.length != 6) {
						System.out.println("Incorrect input format: " + line);
						continue;
					}
					words.add(datavalue[0]);
					boundaries.add(datavalue[5]);
				}
				bReader.close();

				tags.clear();
				bReader = new BufferedReader(new FileReader(tagFile));
				while ((line = bReader.readLine()) != null) {
					if (line.isEmpty())
						continue;
					String datavalue[] = line.split("\t");
					if (datavalue.length != 3) {
						System.out.println("Incorrect tag format: " + line);
						continue;
					}
					tags.add(datavalue[2]);
				}
				bReader.close();
				
				if (words.size() != boundaries.size() && boundaries.size() != tags.size()) {
					System.out.println("Error is reading inputs");
					System.out.println("words size: " + words.size());
					System.out.println("boundaries size: " + boundaries.size());
					System.out.println("tags size: " + tags.size());
				}
				
				observations.clear();
				boolean searching = false;
				int start = 0;
				for (int i = 0; i < boundaries.size(); ++i) {
					if (boundaries.get(i).equals("B")) {
						if (searching) {
							tryAddObservations(start, i - 1, sub);
						} else {
							searching = true;
						}
						start = i;
					} else if (boundaries.get(i).equals("O")) {
						if (searching) {
							tryAddObservations(start, i - 1, sub);
							searching = false;
						}
					} else if (boundaries.get(i).equals("E")) {
						if(!searching)
						{
							System.out.println("Start did not show up for " + i);
							return;
						}
						else
						{
							tryAddObservations(start,i, sub);
							searching = false;
						}
					}
				}
				
				 ValueComparator bvc = new ValueComparator(observations);
				 TreeMap<String, Double> sortedObservations = new TreeMap<String, Double>(bvc);
				 sortedObservations.putAll(observations);
				 String ifname = idfFileName;
				 if(sub)
				 {
					 ifname = ifname + "_sub";
				 }
				 PrintWriter pw = new PrintWriter("files/summarizationOutput/" + inputFileName.split("\\.")[0] + "_" +ifname + ".tsv", "UTF-8");
				for (Map.Entry<String, Double> entry : sortedObservations.entrySet()) {
					String word = entry.getKey();
					double idf = entry.getValue();
					pw.printf("%s\t%.4f\n", word, idf);
				}
				pw.close();
			}
		}
	}

	public static void tryAddObservations(int start, int end, boolean sub) {
		boolean obs = false;
		for (int j = start; j <= end; ++j) {
			if (tags.get(j).equals("Observation")) {
				obs = true;
				break;
			}
		}
		if (obs) {
			if (words.get(end).equals(".")) {
				--end;
			}
			StringBuilder obsPhrase = new StringBuilder();
			double totalIdf = 0;
			for (int j = start; j <= end; ++j) {
				String word = words.get(j);
				String lwWord = word.toLowerCase();
				double idf = 0;
				if (idfs.containsKey(lwWord)) {
					idf = idfs.get(lwWord);
				}
				obsPhrase.append(word + " ");
				if (sub) {
					if (tags.get(j).equals("Observation") || tags.get(j).equals("Observation_Modifier"))
					{
						totalIdf += idf;
					}
				} else {
					totalIdf += idf;
				}
			}
			
			String obsPhraseString = obsPhrase.toString().trim();
			if (!obsPhraseString.isEmpty()) {
				if(!sub){
					totalIdf = totalIdf / (end - start + 1);
				}
				observations.put(obsPhraseString, totalIdf);
			}else{
				System.out.println("Phrase is empty between " + start + " " + end);
			}
		}
	}
	
	public static Map<String, Double> idfFiletoMap(String fileName)
			throws IOException {
		Map<String, Double> idfs = new HashMap<String, Double>();
		String idfFile = "files/idf/" + fileName + ".tsv";
		BufferedReader bReader = new BufferedReader(new FileReader(idfFile));
		String line;
		while ((line = bReader.readLine()) != null) {
			if (line.isEmpty())
				continue;
			String datavalue[] = line.split("\t");
			if (datavalue.length != 2) {
				System.out.println("Incorrect test format: " + line);
				continue;
			}
			// System.out.println(datavalue[0] + "\t" + datavalue[1]);
			idfs.put(datavalue[0], Double.parseDouble(datavalue[1]));
		}
		bReader.close();
		return idfs;
	}

}