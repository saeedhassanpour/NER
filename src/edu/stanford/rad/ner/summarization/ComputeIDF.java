package edu.stanford.rad.ner.summarization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.rad.ner.util.ValueComparator;


public class ComputeIDF {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		long startTime = System.currentTimeMillis();
		int N = 0;
		Map<String, Integer> docFreq = new HashMap<String, Integer>();
		Map<String, Double> idfs = new HashMap<String, Double>();
		ValueComparator bvc = new ValueComparator(idfs);
		TreeMap<String, Double> sortedidfs = new TreeMap<String, Double>(bvc);

		Set<String> report = new HashSet<String>();
		String numericRegex = "-?\\d+(\\.\\d+)?";
		String dateRegex = "(\\d*)(/|-)(\\d*)((/|-)(\\d*))?";
		String dateRegex1 = "^.*(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec).*$";
		String hourRegex = "(\\d*)[:,-\\.](\\d*)";



		final File folder = new File("files/corpus-full");
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory()) {
				String fileName = fileEntry.getName();
				String filepath = fileEntry.getPath();
				System.out.println("Working on " + fileName + "...");
				Scanner scanner = new Scanner(new File(filepath), "UTF-8");
				String text = scanner.useDelimiter("\\Z").next();
				scanner.close();
				text = text.replaceAll("\r\n", "\n");
				text = text.replaceAll("‑", "-");

				Properties props = new Properties();
				props.put("annotators", "tokenize, ssplit");
				props.put("ssplit.newlineIsSentenceBreak", "always");
				StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
				Annotation document = new Annotation(text);
				pipeline.annotate(document);

				List<CoreMap> sentences = document.get(SentencesAnnotation.class);

				for (CoreMap sentence : sentences) {
					for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
						String word = token.get(TextAnnotation.class);
						word = word.toLowerCase();
						if(word.matches(numericRegex) || word.matches(dateRegex) || word.matches(dateRegex1) || word.matches(hourRegex))
						{
							continue;
						}
						if (word.equals("********************************************")) {
							N++;
							for (String w : report) {
								int f = 0;
								if (docFreq.containsKey(w)) {
									f = docFreq.get(w);
								}
								docFreq.put(w, f + 1);
							}
							report.clear();
							continue;
						}
						//System.out.println(">>>" + word + "<<<");
						report.add(word);
					}
				}
			}
		}

		System.out.println("N = " + N);

		for (String word : docFreq.keySet()) {
			double idf = Math.log10((1.0 * N) / docFreq.get(word));
			idfs.put(word, idf);
		}

		sortedidfs.putAll(idfs);
		
		PrintWriter pw = new PrintWriter("files/idf/idfAll.tsv", "UTF-8");
		for (Map.Entry<String, Double> entry : sortedidfs.entrySet()) {
			String word = entry.getKey();
			double idf = entry.getValue();

			pw.printf("%s\t%.4f\n", word, idf);
		}
		pw.close();

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Finshed in " + totalTime / 1000.0 + " seconds");
	}

}
