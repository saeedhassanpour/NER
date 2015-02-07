package edu.stanford.rad.ner.fullsummarization;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ie.ner.CMMClassifier;


public class FS4_KFoldCrossValidation {

	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		String model = "CRF"; //CRF,CMM
		
		String prop = 
				  "map = word=0,answer=1,tag=2,lemma=3,cat=4,pcat=5,idf=6,mark=7  \n"


				
//				+ "printFeatures = print \n"
//				+ "printFeaturesUpto = 10 \n"
//				+ "printClassifier = AllWeights \n" //AllWeights
//				+ "#HighWeight,HighMagnitude, AllWeights, WeightHistogram \n"
//				+ "printClassifierParam = 10 \n"
//				+ "#justify = true \n"
//				+ "#suppressTestDebug = false \n"
//				+ "\n"
//				
				+ "cleanGazette=true \n"
				+ "#sloppyGazette = true \n"
				+ "gazette = files/gazette/Anatomy.txt,files/gazette/Modifier.txt,files/gazette/Observation.txt,files/gazette/Uncertainty.txt,files/gazette/Modality.txt \n"
				+ "\n"
				
				+ "useTags = true \n"
				+ "useLemmas = true \n"
				+ "useClassFeature = true \n"
				+ "usePcatFeature = true \n"
				+ "useIdfFeature = true \n"
				+ "useMarkFeature= true \n"
				+ "\n"
				
//				+ "#useObservedSequencesOnly = true \n"
//				+ "useGenericFeatures = true \n"
//				+ "\n"
//
//				+ "useWord = true \n"
				+ "usePrev = true \n"
				+ "useNext = true \n"
//				+ "#useBinnedLength = 3,6,10 \n"
//				+ "useWordPairs = true \n"
//				+ "useReverse = true \n"
//				+ "usePosition = true \n"
//				+ "useBeginSent = true \n"
//				+ "useLastRealWord = true \n"
//				+ "useNextRealWord = true \n"
//				+ "useTitle = true \n"
//				+ "useOccurrencePatterns = true \n"
//				+ "\n"
//				
//				
				+ "useNGrams = true \n"
				+ "noMidNGrams = true \n"
				+ "maxNGramLeng = 5 \n"
				+ "lowercaseNGrams = true \n"
				+ "dehyphenateNGrams = true \n"
//				+ "conjoinShapeNGrams = true \n"
//				+ "useNeighborNGrams = true \n"
//				+ "cacheNGrams = ture \n"
//				+ "\n"
//				
//				+ "useWordTag = true \n"
//				+ "#useSymTags = true \n"
//				+ "#useSymWordPairs = true \n"
//				+ "\n"
//				
//				+ "usePrevNextLemmas = true \n"
//				+ "useLemmaAsWord = true \n"
//				+ "normalizeTerms = true \n"
//				+ "normalizeTimex = true \n"
//				+ "\n"
//				
				+ "wordShape = digits  \n"   //digits,chris1,chris2useLC
//				dan1,dan2,dan2useLC,dan2bio,dan2bioUseLC,jenny1,jenny1useLC,
//				chris1,chris2,chris2useLC,chris3,chris3useLC,chris4,digits,chinese
//				+ "wordShape = WordShapeClassifier.WORDSHAPEDAN2USELC \n"
//				+ "useShapeConjunctions = true \n"
//				+ "\n"
//				
//				+ "maxLeft = 2 \n"
//				+ "maxRight = 2 \n" 				
				+ "useSequences = true \n"
//				+ "usePrevSequences = true \n" 		
//				+ "useNextSequences = true \n"
//				+ "useLongSequences = true \n" 		
//				+ "#useBoundarySequences = true \n"
				+ "useTaggySequences = true \n" 	
//				+ "useExtraTaggySequences = true \n"
//				+ "useTaggySequencesShapeInteraction = true \n"
//				+ "dontExtendTaggy = true \n"
//				+ "\n"
//
//				+ "useTypeSeqs = true \n"
				+ "useTypeSeqs2 = true \n"
				+ "useTypeSeqs3 = true \n"
//				+ "useTypeySequences = true \n"
//				+ "\n"
//				
				+ "useDisjunctive = true \n"
				+ "disjunctionWidth = 5 \n"
				+ "useDisjShape = true \n"
//				+ "\n"
//				
//				+ "intern = true \n"
//				+ "intern2 = true \n"
//				+ "\n"
//				
//				+ "#normalize = true \n"
//				+ "useHuber = true \n"
//				+ "epsilon = 0.005 \n"
//				+ "useQuartic = true \n"
//				+ "sigma = 20.0 \n"
//				+ "beamSize = 100 \n"
//				+ "adaptSigma = 20.0 \n"
//				+ "useQN = true \n"
//				+ "QNsize = 15 \n"
//				+ "\n"
//				
//				//+ "#trainFileList = files/rad/CT Abdomen.txt.output.tsv,files/rad/Ankle Report.txt.output.tsv \n"
//				//+ "trainFile = files/rad/CT Abdomen.txt.output.tsv  \n"
//				//+ "testFile = files/rad/CT Head.txt.output.tsv \n"
//				//+ "serializeTo = files/rad/rad1-ner-model.ser.gz \n"
				;
		
		
		int k = 0;
		final File folder = new File("sum/completePartitions");
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory() && fileEntry.getName().startsWith("partition_"))
				k++;
		}
		
		if(k<2)
		{
			System.err.println("There should be at least 2 partions...");
			return;
		}
		
		System.err.println("number of folds, k = " + k);
		PrintStream defaultErr = System.err;
		
		for(int i=0; i<k; i++) //<k,1
		{
			File file = new File("sum/test/test" +model+ "/test_" + model + "_" + i + ".tsv");  
			FileOutputStream fis = new FileOutputStream(file.getPath());  
			PrintStream out = new PrintStream(fis);  
			System.setOut(out);
			
			file = new File("sum/err/err" +model+ "/err_" + model + "_" + i + ".tsv");  
			fis = new FileOutputStream(file.getPath());  
			out = new PrintStream(fis);  
			System.setErr(out); //
			
			System.err.println("fold " + i + " ...");
			String partProp = prop;
			String testFile = "testFile = sum/completePartitions/partition_" + i + "_Complete.tsv \n";
			String serializeTo = "serializeTo = sum/model/model" + model + "/ner_" + model + "_" + i + ".ser.gz \n";
			String trainFileList = "trainFileList = ";
			for(int j=0; j<k; j++)
			{
				if(j != i){
					trainFileList += "sum/completePartitions/partition_" + j + "_Complete.tsv,";
				}
			}
			trainFileList += "\n";
			trainFileList = trainFileList.replaceAll(",\n", "\n");
			partProp += trainFileList + testFile + serializeTo;
			
	    	PrintWriter pw = new PrintWriter("sum/prop/prop" + model + "/prop_" + model + "_"+ i + ".prop", "UTF-8");
	    	pw.print(partProp);
			pw.close();
			
			String[] crfArgs = {"-prop", "sum/prop/prop" + model + "/prop_" + model + "_"+ i + ".prop"};
			
			if(model.equals("CRF"))
				CRFClassifier.main(crfArgs);
			else if(model.equals("CMM"))
				CMMClassifier.main(crfArgs);
		}
		
		System.setErr(defaultErr);
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.err.println("Finshed in " + totalTime/1000.0 + " seconds");
	}

}

