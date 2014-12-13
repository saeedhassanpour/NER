package edu.stanford.rad.ner.summarization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;

public class PresentResults {
	public static void main(String[] args) throws Exception {
		
		StringBuilder results = new StringBuilder();
		final File Datafolder = new File("files/corpus-segmented");
		for (final File inputFile : Datafolder.listFiles()) {
			if (!inputFile.isDirectory() && !inputFile.getName().startsWith(".")) {
				String inputFileName = inputFile.getName();
				String inputFilepath = inputFile.getPath();
				File summarizationFile = new File("files/summarizationOutput/sum_" + inputFileName.split("\\.")[0] + "_idfAllFiltered_sub.tsv");
				if (!summarizationFile.exists()) {
					System.out.println(summarizationFile.getName() + " not found! " + inputFileName + " is skipped.");
					continue;
				}
				System.out.println("Working on " + inputFileName + "...");

				results.append("REPORT " + inputFileName + ":\r\n");
				Scanner scanner = new Scanner(new File(inputFilepath), "UTF-8");
				String text = scanner.useDelimiter("\\Z").next();
				scanner.close();
				results.append(text);
				
				results.append("\r\n\r\nSUMMARY:\r\n");
				String line;
				BufferedReader bReader = new BufferedReader(new FileReader(summarizationFile));
				while ((line = bReader.readLine()) != null) {
					if (line.isEmpty())
						continue;
					results.append(line + "\r\n");
				}
				bReader.close();
				
				results.append("\r\n\r\n\r\n");
				results.append("********************************************");
				results.append("\r\n\r\n\r\n");
			}
		}
		
		String output = results.toString();
		output = output.replaceAll("\r\n", "\n");
		output = output.replaceAll("\n", "\r\n");
		//System.out.println(results);
		PrintWriter pw = new PrintWriter("files/summarizationResults/summarizationResults.txt", "UTF-8");
		pw.print(output);
		pw.close();
	}

}
