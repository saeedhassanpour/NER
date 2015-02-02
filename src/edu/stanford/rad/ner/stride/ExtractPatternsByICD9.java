package edu.stanford.rad.ner.stride;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import edu.stanford.rad.ner.util.ValueComparatorComplex;

public class ExtractPatternsByICD9 {

	public static void main(String[] args) throws FileNotFoundException,IOException {
		long startTime = System.currentTimeMillis();
		
		int totalRecordfs = 0;
		int ourDiseaseCounter = 0;
		//Set<String> reportSet = new HashSet<String>();
		Map<Integer, ArrayList<String>> patientProcedureDesc = new HashMap<Integer,ArrayList<String>>();
		
		final File folder = new File("/Users/saeedhp/Dropbox/Stanford/Data/STRIDE/Reports");
		
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory() && !fileEntry.getName().startsWith(".")) {
				String fileName = fileEntry.getName();
				String filepath = fileEntry.getPath();
				System.out.println("Working on " + fileName + "...");
				
				Scanner scanner = new Scanner(new File(filepath), "UTF-8");
				String text = scanner.useDelimiter("\\Z").next();
				scanner.close();
				
				String records[] = text.split("\n(?=\\d{9}\t)");
				totalRecordfs += records.length;
				//System.out.println("Number of records in " + fileName + ": " + records.length);
				
				for (String record : records) {
					boolean OurDisease = false;
					String fields[] = record.split("\t\\s*");

					for (int i = 5; i < fields.length; ++i) {
						String stringCode = fields[i].trim();
						double code = -1;
						if (stringCode.matches("-?\\d+(\\.\\d+)?")) {
							code = Double.parseDouble(stringCode);
						}
						if ((int)Math.floor(code) == 633) { //*************Code*********//
							//System.out.println(code);
							OurDisease = true;
							break;
						}
					}
					
					if(OurDisease)
					{
						++ourDiseaseCounter;
						int pocID = Integer.parseInt(fields[0]);
						int patID = Integer.parseInt(fields[1]);
						String procedure = fields[2].trim();
						String procedureDesc = fields[3].trim();
						String report = fields[4].trim();
						String ICD9Codes = "";
						for (int i = 5; i < fields.length; ++i) {
							ICD9Codes += fields[i].trim() + " ";
						}
						ICD9Codes.trim();
						
						//String entry = pocID + "\n" + patID + "\n" + procedure + "\n" + procedureDesc + "\n" + report + "\n" + ICD9Codes;
						//reportSet.add(entry);
						
						// add procedure Description
						ArrayList<String> procedureDescList = new ArrayList<String>();
						if(patientProcedureDesc.containsKey(patID)){
							procedureDescList.addAll(patientProcedureDesc.get(patID));
						}
						procedureDescList.add(fields[3].trim());
						Collections.sort(procedureDescList);
						patientProcedureDesc.put(patID, procedureDescList);
					}
				}	
			}
		}
		
		Map<ArrayList<String>, Integer> patterns = new HashMap<ArrayList<String>, Integer>();
		ValueComparatorComplex bvc = new ValueComparatorComplex(patterns);
		TreeMap<ArrayList<String>, Integer> sortedPatterns = new TreeMap<ArrayList<String>, Integer>(bvc);

		for (int pid : patientProcedureDesc.keySet()) {
			ArrayList<String> pattern = patientProcedureDesc.get(pid);
			// System.out.println(pid + ":" + pattern);
			int counter = 0;
			if (patterns.containsKey(pattern)) {
				counter = patterns.get(pattern);
			}
			++counter;
			patterns.put(pattern, counter);
		}

		sortedPatterns.putAll(patterns);

		PrintWriter pw = new PrintWriter("files/stride/633-Ectopic Pregnancy-Patterns.txt", "UTF-8");
		pw.println("Patterns:" + patterns.keySet().size());
		pw.println("Patients with our disease:" + patientProcedureDesc.keySet().size());
		pw.println("Our disease records: " + ourDiseaseCounter);
		pw.println("Total number of records: " + totalRecordfs);
		pw.println();
		
		for (Map.Entry<ArrayList<String>, Integer> entry : sortedPatterns.entrySet()) {
			ArrayList<String> pattern = entry.getKey();
			int freq = entry.getValue();
			pw.printf("%s\t%d\n", pattern, freq);
		}
		pw.close();
		
		System.out.println("Patterns:" + patterns.keySet().size());
		System.out.println("Patients with our disease:" + patientProcedureDesc.keySet().size());
		System.out.println("Our disease records: " + ourDiseaseCounter);
		System.out.println("Total number of records: " + totalRecordfs);
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Finshed in " + totalTime/1000.0 + " seconds");
	}
}
