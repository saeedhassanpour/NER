package edu.stanford.rad.ner.stride;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;

public class ReadInput {

	public static void main(String[] args) throws FileNotFoundException,IOException {
		long startTime = System.currentTimeMillis();
		
		Map<Integer, LinkedHashSet<String>> patientProcedure = new HashMap<Integer,LinkedHashSet<String>>();
		Map<Integer, LinkedHashSet<String>> patientProcedureDesc = new HashMap<Integer,LinkedHashSet<String>>();
		Map<Integer, LinkedHashSet<String>> patientReport = new HashMap<Integer,LinkedHashSet<String>>();
		Map<Integer, LinkedHashSet<String>> patientICD9 = new HashMap<Integer,LinkedHashSet<String>>();
		
		
		final File folder = new File("/Users/saeedhp/Dropbox/Stanford/Data/STRIDE/Reports1");
		
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory() && !fileEntry.getName().startsWith(".")) {
				String fileName = fileEntry.getName();
				String filepath = fileEntry.getPath();
				System.out.println("Working on " + fileName + "...");
				
				Scanner scanner = new Scanner(new File(filepath), "UTF-8");
				String text = scanner.useDelimiter("\\Z").next();
				scanner.close();
				
//				int eCounter = 0;
				String records[] = text.split("\n(?=\\d{9}\t)");
				//System.out.println("Number of records: " + records.length);
				
				for (String record : records) {
//					System.out.println(record);
//					System.out.println("-----------------");
					
					String fields[] = record.split("\t\\s*");
					
//					if (fields.length < 6) {
//						//System.out.println("Error in formatting. Size: " + fields.length + " Record: " + record);
//						++eCounter;
//						continue;
//					}
					
					int patID = Integer.parseInt(fields[1]);
					
					// add procedure
					LinkedHashSet<String> procedureSet = new LinkedHashSet<String>();
					if(patientProcedure.containsKey(patID)){
						procedureSet.addAll(patientProcedure.get(patID));
					}
					procedureSet.add(fields[2].trim());
					patientProcedure.put(patID, procedureSet);
					
					// add procedure Description
					LinkedHashSet<String> procedureDescSet = new LinkedHashSet<String>();
					if(patientProcedureDesc.containsKey(patID)){
						procedureDescSet.addAll(patientProcedureDesc.get(patID));
					}
					procedureDescSet.add(fields[3].trim());
					patientProcedureDesc.put(patID, procedureDescSet);
					
					// add report
					LinkedHashSet<String> reportSet = new LinkedHashSet<String>();
					if(patientReport.containsKey(patID)){
						reportSet.addAll(patientReport.get(patID));
					}
					reportSet.add(fields[4].trim());
					patientReport.put(patID, reportSet);
					
					// add ICD9 codes
					LinkedHashSet<String> ICD9Set = new LinkedHashSet<String>();
					if (patientICD9.containsKey(patID)) {
						ICD9Set.addAll(patientICD9.get(patID));
					}

					for (int i = 5; i < fields.length; ++i) {
						ICD9Set.add(fields[i].trim());
					}
					patientICD9.put(patID, ICD9Set);
					
					//System.out.println(fields[0] + "\t" + fields[5] + "\t");
				}
				
//				System.out.println("Error Counter: " + eCounter);
			}
		}
		
		//System.out.println(patientICD9.get(50734));
		System.out.println("Number of patients: " + patientProcedure.keySet().size());
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Finshed in " + totalTime/1000.0 + " seconds");
	}
}
