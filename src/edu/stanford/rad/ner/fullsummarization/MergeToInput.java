package edu.stanford.rad.ner.fullsummarization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class MergeToInput {

	static Map<String, Double> idfs;
	static Map<String, String> tags = new HashMap<String, String>();

	public static void main(String[] args) throws NumberFormatException, IOException {
		String idfFileName = "idfAllFiltered"; // idfAll,idfFiltered
		idfs = idfFiletoMap(idfFileName);

		final File Datafolder = new File("sum/PartitionOutput");
		for (final File inputFile : Datafolder.listFiles()) {
			if (!inputFile.isDirectory() && !inputFile.getName().startsWith(".")) {
				String inputFileName = inputFile.getName();
				File tagFile = new File("sum/inputTags/tagged_" + inputFileName);
				if (!tagFile.exists()) {
					System.out.println(tagFile.getName() + " not found! " + inputFileName + " is skipped.");
					continue;
				}
				System.out.println("Working on " + inputFileName + "...");

				String line;
				tags.clear();

				BufferedReader bReader = new BufferedReader(new FileReader(tagFile));
				while ((line = bReader.readLine()) != null) {
					if (line.isEmpty())
						continue;
					String datavalue[] = line.split("\t");
					if (datavalue.length != 3) {
						System.out.println("Incorrect tag format: " + line);
						continue;
					}
					tags.put(datavalue[0], datavalue[2]);
				}
				bReader.close();

				PrintWriter pw = new PrintWriter("sum/completePartitions/"+ inputFileName.split("\\.")[0] + "_Complete.tsv", "UTF-8");
				bReader = new BufferedReader(new FileReader(inputFile));
				while ((line = bReader.readLine()) != null) {
					if (line.isEmpty())
						continue;
					String datavalue[] = line.split("\t");
					if (datavalue.length != 6) {
						System.out.println("Incorrect input format: " + line);
						continue;
					}
					String word = datavalue[0];
					double idf = 0;
					if (idfs.containsKey(word)) {
						idf = idfs.get(word);
					}
					String tag = "NaN";
					if (tags.containsKey(word)) {
						tag = tags.get(word);
					} else {
						System.out.println("Mismach in input: " + line);
						continue;
					}

					pw.printf("%s\t%.4f\t%s\n", line, idf, tag);

				}
				bReader.close();
				pw.close();

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