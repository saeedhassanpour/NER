package edu.stanford.rad.ner.kfold;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.rad.ner.util.GenNegEx;
import edu.stanford.rad.ner.util.KWAnnotation;
import edu.stanford.rad.ner.util.ReadKWAnnotations;
import edu.stanford.rad.ner.util.Stemmer;

public class IE1_KFoldPartitioning {

	public static void main(String[] args) throws FileNotFoundException,IOException {
		long startTime = System.currentTimeMillis();
		List<String> allReports = new ArrayList<String>();
		GenNegEx g = new GenNegEx(true);
		Stemmer stemmer = new Stemmer();
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

				ReadKWAnnotations readKWAnnotations = new ReadKWAnnotations();
				Map<String, KWAnnotation> kwAnnotationMap = readKWAnnotations.read(XmlfileName);
				List<KWAnnotation> kwAnnotationList = new ArrayList<KWAnnotation>(kwAnnotationMap.values());
				Collections.sort(kwAnnotationList);
//				 for(KWAnnotation kwAnnotation : kwAnnotationList)
//				 {
//				 System.out.println(kwAnnotation);
//				 }
				ListIterator<KWAnnotation> it = kwAnnotationList.listIterator();
				int aEnd = -1;
				KWAnnotation kw = null;

				Properties props = new Properties();
				props.put("annotators", "tokenize, ssplit, pos, lemma"); // ,ner,parse,dcoref
				props.put("ssplit.newlineIsSentenceBreak", "always");
				StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
				Annotation document = new Annotation(text);
				pipeline.annotate(document);

				List<CoreMap> sentences = document.get(SentencesAnnotation.class);
				String report = new String();

				for (CoreMap sentence : sentences) {
					String scope = g.negScope(cleans(sentence.toString()));
					int nStart = -1, nEnd = -1, counter = 0;
				    if (!scope.equals("-1") && !scope.equals("-2"))
				    {
				    	String[] number = scope.split("\\s+");
				    	nStart = Integer.valueOf(number[0]);
				    	nEnd = Integer.valueOf(number[2]);
				    	//System.out.println(sentence);
				    	//System.out.println(cleans(sentence.toString()));
				    	//System.out.println(nStart + "......" + nEnd);
				    }


					for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
						String word = token.get(TextAnnotation.class);
						if(word.equals("********************************************"))
						{
							//System.out.println(report);
							//System.out.println("-------");
							allReports.add(report);
							report = new String();
							continue;
						}
						int start = token.beginPosition();
						int end = token.endPosition();
						String pos = token.get(PartOfSpeechAnnotation.class);
						
						String Stanfordlemma = token.get(LemmaAnnotation.class);
						char[] wordCharArray = Stanfordlemma.toLowerCase().toCharArray();
						stemmer.add(wordCharArray, wordCharArray.length);
						stemmer.stem();
						String lemma = stemmer.toString();

						while (aEnd < start && it.hasNext()) {
							kw = it.next();
							aEnd = kw.end;
						}

						String mentionClass = "O";
						if (kw != null && kw.start <= start && kw.end >= end) {
							mentionClass = kw.mentionClass;
						}
						mentionClass = mentionClass.replaceAll(" ", "_");
						
						String negex = "P";
						if(nStart <= counter && counter <= nEnd){
							//System.out.println("N=====>  " + word);
							negex = "N";
						}
						if(isClean(word))
							counter++;
						report += word + "\t" + mentionClass + "\t" + pos+ "\t" + lemma + "\t" + negex + "\n";
					}
				}
			}
			System.out.println("size:" + allReports.size());
		}
		
		int k = 1;
		int n = allReports.size();
		int d = n/k;
		System.out.println("n = "+n+", k = " + k);
		if(n<k)
		{
			System.out.println("k should be <= " + n);
			return;
		}
		
//        List<String> indices = new ArrayList<String>();
//        for (int i = 0; i < n; i++)
//        {
//        	indices.add(i + "");
//        }
//        Collections.shuffle(indices);
//        System.out.println(indices);
        
		Collections.shuffle(allReports);
        List<List<String>> parts = new ArrayList<List<String>>();
        for (int i = 0; i < k; i++) {
            parts.add(new ArrayList<String>(allReports.subList(i*d, Math.min(n, (i+1)*d))));
        }
        
        for(int i=k*d; i<n; ++i)
        {
        	parts.get(i%k).add(allReports.get(i));
        }
        
        
        for(int i=0; i<parts.size(); i++)
        {
        	PrintWriter pw = new PrintWriter("files/partitionOutput/partition_" + i+ ".tsv", "UTF-8");
        	for(String rep: parts.get(i))
        	{
        		pw.print(rep);
        	}
			pw.close();
        }
				
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Finshed in " + totalTime/1000.0 + " seconds");
	}
	
    // post: removes punctuations
    private static String cleans(String line) {
	line = line.toLowerCase();
	if (line.contains("\""))
	    line = line.replaceAll("\"", "");
	if (line.contains(","))
	    line = line.replaceAll(",", "");  
	if (line.contains("."))
	    line = line.replaceAll("\\.", "");
	if (line.contains(";"))
	    line = line.replaceAll(";", "");
	if (line.contains(":"))
	    line = line.replaceAll(":", "");
	return line;
    }
    
    private static boolean isClean(String token) {
		if (token.equals("\"") || token.equals(",") || token.equals(".") || token.equals(";") || token.equals(":")){
			return false;
		}
		else{
			return true;
		}
    }
}
