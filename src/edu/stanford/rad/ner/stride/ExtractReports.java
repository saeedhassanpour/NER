package edu.stanford.rad.ner.stride;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class ExtractReports {

	public static void main(String[] args) throws FileNotFoundException,IOException {
		long startTime = System.currentTimeMillis();
		
		Set<String> reportSet = new HashSet<String>();
		
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
				System.out.println("Number of records: " + records.length);
				
				for (String record : records) {
					String fields[] = record.split("\t\\s*");
					String procedureDesc = fields[3].trim();
					
					if (procedureDesc.toLowerCase().startsWith("ct thorax")) {
						int pocID = Integer.parseInt(fields[0]);
						int patID = Integer.parseInt(fields[1]);
						String procedure = fields[2].trim();
						String report = fields[4].trim();
						String ICD9Codes = "";
						for (int i = 5; i < fields.length; ++i) {
							ICD9Codes += fields[i].trim() + " ";
						}
						ICD9Codes.trim();
						
						String entry = pocID + "\n" + patID + "\n" + procedure + "\n" + procedureDesc + "\n" + report + "\n" + ICD9Codes;

						reportSet.add(entry);
					}

				}
				
			}
		}
		
		List<String> reportList = new ArrayList<String>(reportSet);
		StringBuilder output = new StringBuilder();
		Random randomGenerator = new Random();
		Set<Integer> ids = new HashSet<Integer>();
		int n = 25, i=0;
		while (i<n)
		{
			int index = randomGenerator.nextInt(reportList.size());
			if(!ids.contains(index))
			{
				
				String chosenReport = reportList.get(index);
				chosenReport = chosenReport.replaceAll("\n", "\r\n");
				output.append(chosenReport);
				output.append("\r\n\r\n\r\n");
				output.append("********************************************");
				output.append("\r\n\r\n\r\n");
				ids.add(index);
				i++;
			}
		}
		
		PrintWriter pw = new PrintWriter("files/stride/stride_" + n + "_Chest_CT_reports.txt","UTF-8");
		pw.print(output);
		pw.close();
		//System.out.println(output);
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Finshed in " + totalTime/1000.0 + " seconds");
	}
}
