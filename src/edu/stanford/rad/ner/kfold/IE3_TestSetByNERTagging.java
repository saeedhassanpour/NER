package edu.stanford.rad.ner.kfold;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ie.ner.CMMClassifier;

public class IE3_TestSetByNERTagging {

	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		String modelType = "CRF"; // CRF,CMM
		PrintStream defaultOut = System.out;
		String model = "files/model/model" + modelType + "/ner_" + modelType + "_0.ser.gz";
		
		int i=0;
		final File Datafolder = new File("files/testRaw");
		for (final File fileEntry : Datafolder.listFiles()) {
			if (!fileEntry.isDirectory() && !fileEntry.getName().startsWith(".")) {
				String inputFileName = fileEntry.getName();
				String inputFilepath = fileEntry.getPath();
				System.setOut(defaultOut);
				System.out.println("Working on " + inputFileName + "...");
				
				String testFile = "files/test/test" + modelType +"/test_" + modelType+ "_" + i + ".tsv";
				
				File outputFile = new File(testFile);  
				FileOutputStream fis = new FileOutputStream(outputFile.getPath());  
				PrintStream out = new PrintStream(fis);  
				System.setOut(out);

				String[] modelArgs = { "-loadClassifier", model, "-testFile", inputFilepath };

				if (modelType.equals("CRF"))
					CRFClassifier.main(modelArgs);
				else if (modelType.equals("CMM"))
					CMMClassifier.main(modelArgs);
				
				++i;
			}
		}
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.err.println("Finshed in " + totalTime/(60*1000.0) + " Minutes");
	}
}
