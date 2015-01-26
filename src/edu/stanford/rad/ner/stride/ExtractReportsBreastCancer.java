package edu.stanford.rad.ner.stride;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ExtractReportsBreastCancer {

	public static void main(String[] args) throws FileNotFoundException,IOException {
		long startTime = System.currentTimeMillis();
		
		Map<Integer, ArrayList<String>> patientProcedureDesc = new HashMap<Integer,ArrayList<String>>();
		Map<Integer, ArrayList<String>> patientReports = new HashMap<Integer,ArrayList<String>>();

		
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
						if ((int)Math.floor(code) == 174) { //Code
							//System.out.println(code);
							OurDisease = true;
							break;
						}
					}
					
					if(OurDisease)
					{
//						int pocID = Integer.parseInt(fields[0]);
						int patID = Integer.parseInt(fields[1]);
//						String procedure = fields[2].trim();
//						String procedureDesc = fields[3].trim();
						String report = fields[4].trim();
//						String ICD9Codes = "";
//						for (int i = 5; i < fields.length; ++i) {
//							ICD9Codes += fields[i].trim() + " ";
//						}
//						ICD9Codes.trim();
						//String entry = pocID + "\n" + patID + "\n" + procedure + "\n" + procedureDesc + "\n" + report + "\n" + ICD9Codes;

						
						//trim report
						int trimIndex = report.indexOf(" I have personally reviewed the images for this examination and agree");
						if (trimIndex != -1){
							report = report.substring(0, trimIndex).trim();
						}
						// add procedure report
						ArrayList<String> reportList = new ArrayList<String>();
						if(patientReports.containsKey(patID)){
							reportList.addAll(patientReports.get(patID));
						}
						reportList.add(report);
						//Collections.sort(reportList); // It should be sorted based on date
						patientReports.put(patID, reportList);
						
						// add procedure description
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
		
		List<String> pattern1 = Arrays.asList("MRI BREAST UNILATERAL");
		List<String> pattern2 = Arrays.asList("MRI BREAST UNILATERAL", "MRI BREAST UNILATERAL");
		
		for (int pid : patientProcedureDesc.keySet()) {
			ArrayList<String> pattern = patientProcedureDesc.get(pid);
			
			if (pattern.equals(pattern1)) { //pattern 1
				PrintWriter pw = new PrintWriter("files/stride/breastCancer/corpus/pattern1/"+ pid +".txt", "UTF-8");
				pw.println(patientReports.get(pid).get(0));
				pw.close();
			} 
			else if (pattern.equals(pattern2)) { //pattern 2
				PrintWriter pw = new PrintWriter("files/stride/breastCancer/corpus/pattern2/"+ pid +".txt", "UTF-8");
				pw.println(patientReports.get(pid).get(0));
				pw.close();
			}
			
			//explore
			if (pattern.equals(pattern2)) {
				PrintWriter pw = new PrintWriter("files/stride/breastCancer/corpus/explore/"+ pid +".txt", "UTF-8");
				pw.println(patientReports.get(pid).get(0));
				pw.println("******************");
				pw.println(patientReports.get(pid).get(1));
				pw.close();
			}
		}

		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Finshed in " + totalTime/1000.0 + " seconds");
	}
}
