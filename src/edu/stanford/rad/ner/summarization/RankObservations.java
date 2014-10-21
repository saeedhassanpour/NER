package edu.stanford.rad.ner.summarization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import edu.stanford.rad.ner.util.ValueComparator;

public class RankObservations {

	public static void main(String[] args) throws NumberFormatException,IOException {
		String idfFileName = "idfAll";
		Map<String,Double> idfs = idfFiletoMap(idfFileName);
		Map<String, Double> observations = new HashMap<String, Double>();
		
		int k = 0;
		String model = "CRF"; //CRF,CMM,Dictionary
		final File folder = new File("files/test/test" + model);
		for (final File fileEntry : folder.listFiles()) 
		{
			if (!fileEntry.isDirectory() && fileEntry.getName().startsWith("test"))
				k++;
		}
		System.out.println("number of folds, k = " + k + "\n");

		for (int i = 0; i < k; i++) {
			String testFile = "files/test/test" + model +"/test_" + model+ "_" + i + ".tsv";
			BufferedReader bReader = new BufferedReader(new FileReader(testFile));
			String line;
			while ((line = bReader.readLine()) != null)
			{
				if (line.isEmpty())
					continue;
				String datavalue[] = line.split("\t");
				if (datavalue.length != 3) {
					System.out.println("Incorrect test format: " + line);
					continue;
				}
				String token = datavalue[0];
				String label = datavalue[2];
				
				if(label.equals("Observation"))
				{
					double idf = 0;
					String LowerCasetoken = token.toLowerCase();
					if(idfs.containsKey(LowerCasetoken))
					{
						idf = idfs.get(LowerCasetoken);
					}
					observations.put(token, idf);
				}
			}
			bReader.close();
		}
		
		//Once (or each report)
		ValueComparator bvc = new ValueComparator(observations);
		TreeMap<String, Double> sortedObservations = new TreeMap<String, Double>(bvc);
		sortedObservations.putAll(observations);
		PrintWriter pw = new PrintWriter("files/idf/test_" + model + "_" + idfFileName + ".tsv", "UTF-8");
		for (Map.Entry<String, Double> entry : sortedObservations.entrySet()) {
			String word = entry.getKey();
			double idf = entry.getValue();
			pw.printf("%s\t%.4f\n", word, idf);
		}
		pw.close();
	}
	
	
	public static Map<String, Double> idfFiletoMap(String fileName) throws IOException
	{
		Map<String, Double> idfs = new HashMap<String, Double>();
		String idfFile = "files/idf/" + fileName + ".tsv" ;
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
			//System.out.println(datavalue[0] + "\t" + datavalue[1]);
			idfs.put(datavalue[0], Double.parseDouble(datavalue[1]));
		}
		bReader.close();
		return idfs;
	}
	
	
}