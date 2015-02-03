package edu.stanford.rad.ner.stride;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class ExtractReportsNegativeEctopicPregnancy {

	public static void main(String[] args) throws FileNotFoundException,IOException {
		long startTime = System.currentTimeMillis();
		final File folder = new File("/Users/saeedhp/Dropbox/Stanford/Data/STRIDE/Reports");
		
		List<String> ourProcedures = Arrays.asList("US R/O ECTOPIC PREGNANCY", "US PELVIS NON OB WO TRANSVAG", "US PELVIS NON OB W TRANSVAG");
		Random rand = new Random(123);
		
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory() && !fileEntry.getName().startsWith(".")) {
				String fileName = fileEntry.getName();
				String filepath = fileEntry.getPath();
				System.out.println("Working on " + fileName + "...");
				
				Scanner scanner = new Scanner(new File(filepath), "UTF-8");
				String text = scanner.useDelimiter("\\Z").next();
				scanner.close();
				
				String records[] = text.split("\n(?=\\d{9}\t)");
				
				for (String record : records) {
					
					String fields[] = record.split("\t\\s*");
					String procedureDesc = fields[3].trim();
					
					if(!ourProcedures.contains(procedureDesc)){
						continue;
					}

					boolean OurDisease = false;
					for (int i = 5; i < fields.length; ++i) {
						String stringCode = fields[i].trim();
						double code = -1;
						if (stringCode.matches("-?\\d+(\\.\\d+)?")) {
							code = Double.parseDouble(stringCode);
						}
						if ((int)Math.floor(code) == 633) { //Code
							OurDisease = true;
							break;
						}
					}
					
					if(!OurDisease)
					{
//						System.out.println(procedureDesc);
						int patID = Integer.parseInt(fields[1]);
						String report = fields[4].trim();
						
//						int trimIndex = report.indexOf(" I have personally reviewed the images for this examination and agree");
//						if (trimIndex != -1){
//							report = report.substring(0, trimIndex).trim();
//						}
						
						if(fields.length < 15 && rand.nextDouble() < 0.157371837){
							PrintWriter pw = new PrintWriter("files/stride/ectopicPregnancy/corpus/negative/" + patID +".txt", "UTF-8");
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
