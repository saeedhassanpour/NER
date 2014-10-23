package edu.stanford.rad.ner.kfold;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import edu.stanford.rad.ner.util.Stemmer;

public class DictionaryBasedAnnotation {
	public static void main(String[] args) throws NumberFormatException,IOException {

		List<String> entityList = Arrays.asList("Observation", "Modifier", "Anatomy", "Uncertainty");
		List<HashSet<String>> dictionaries = new ArrayList<HashSet<String>>();
		Stemmer stemmer = new Stemmer();
		int max = 0;

		// Read dictionaries
		for (String dicName : entityList) {
			HashSet<String> currDic = new HashSet<String>();
			String dicFile = "files/gazette/" + dicName + ".txt";
			BufferedReader bReader = new BufferedReader(new FileReader(dicFile));
			String line;
			
			while ((line = bReader.readLine()) != null) {
				if (line.isEmpty())
					continue;
				String toks[] = line.split("\\s+");
				if (toks.length < 2) {
					System.out.println("Incorrect dictionary format: " + line);
					continue;
				}

				String entry = "";
				for (int i = 1; i < toks.length; ++i) {
					char[] wordCharArray = toks[i].trim().toLowerCase().toCharArray();
					stemmer.add(wordCharArray, wordCharArray.length);
					stemmer.stem();
					String lemma = stemmer.toString();
					entry += lemma + " ";
				}
				entry = entry.trim();
				if (toks.length - 1 > max) {
					max = toks.length - 1;
				}
				//System.out.println(entry);
				currDic.add(entry);

			}
			dictionaries.add(currDic);
			bReader.close();
		}
		System.out.println("Dictionary entry max length = " + max);

		// determine k
		int k = 0;
		final File folder = new File("files/test/testCRF");
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory() && fileEntry.getName().startsWith("test"))
				k++;
		}
		System.out.println("number of folds, k = " + k);
		System.out.println();

		//annotate dictionary file
		for (int i = 0; i < k; i++) {
			String testFile = "files/test/testCRF/test_CRF_" + i + ".tsv";
			PrintWriter pw = new PrintWriter("files/test/testDictionary/test_dictionary_" + i + ".tsv", "UTF-8");
			Scanner scanner = new Scanner(new File(testFile), "UTF-8");
			String text = scanner.useDelimiter("\\Z").next();
			scanner.close();
			text = text.replaceAll("\r\n", "\n");
			String[] lines = text.split("\n");

			for (int lineNum = 0; lineNum < lines.length;) {
				int SpanEnd = Math.min(lines.length, lineNum + max) - 1;
				boolean found = false;
				String l = "O";
				int span = SpanEnd;

				for (; span >= lineNum; --span) {
					String entry = "";
					for (int j = lineNum; j <= span; ++j) {
						char[] wordCharArray = lines[j].split("\t")[0].trim().toLowerCase().toCharArray();
						stemmer.add(wordCharArray, wordCharArray.length);
						stemmer.stem();
						String lemma = stemmer.toString();
						entry += lemma + " ";
					}
					entry = entry.trim();
					//System.out.println(entry);

					for (int dicIndex = 0; dicIndex < dictionaries.size(); ++dicIndex) {
						if (dictionaries.get(dicIndex).contains(entry)) {
							l = entityList.get(dicIndex);
							found = true;
							break;
						}
					}

					if (found) {
						System.out.println("FOUND: " + entry);
						break;
					}
				}
				
				if (!found) {
					span++;
				}
				for (int j = lineNum; j <= span; ++j) {
					String[] v = lines[j].split("\t");
					pw.printf("%s\t%s\t%s\n", v[0], v[1], l);
				}
				lineNum = span + 1;
			}
			pw.close();
		}
	}
}
