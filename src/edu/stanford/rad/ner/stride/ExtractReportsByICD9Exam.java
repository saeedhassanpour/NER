package edu.stanford.rad.ner.stride;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

public class ExtractReportsByICD9Exam {

	public static void main(String[] args) throws FileNotFoundException,IOException {
		long startTime = System.currentTimeMillis();
		Random rand = new Random(123);
		
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
						if ((int)Math.floor(code) == 633) { //Code
							//System.out.println(code);
							OurDisease = true;
							break;
						}
					}
					
					String procedureDesc = fields[3].trim();
					//if(!OurDisease && procedureDesc.equals("US PELVIS NON OB W TRANSVAG"))
					if(!OurDisease && procedureDesc.equals("US R/O ECTOPIC PREGNANCY"))
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
						
						if(fields.length<15 && rand.nextDouble() < 0.065387675){ //0.067314384 //0.08186341 //0.150761345 //0.226142017
							PrintWriter pw = new PrintWriter("files/stride/ectopicPregnancy/corpus/negative/USR"+ patID +".txt", "UTF-8");
							pw.println(report);
							pw.close();
						}
					}
				}	
			}
		}

		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Finshed in " + totalTime/1000.0 + " seconds");
	}
}
