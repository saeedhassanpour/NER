package edu.stanford.rad.ner.summarization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class SegmentCorpus {

	public static void main(String[] args) throws FileNotFoundException,IOException {
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
				
				String reports[] = text.split("\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*");
				for(int i=0; i<reports.length; ++i) {
					String report = reports[i].trim();
					if(!report.isEmpty()){
						int fileNumber = i+1;
						PrintWriter pw = new PrintWriter("files/corpus-segmented/" + fileName.split("\\.")[0] + "_" + fileNumber +".txt", "UTF-8");
						pw.print(report);
						pw.close();
					}
				}
			}
		}
	}
}
				
