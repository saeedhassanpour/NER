package edu.stanford.rad.ner.kfold;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KFoldEvaluation {

	static double precision;
	static double recall;
	static double f1;

	public static void main(String[] args) throws NumberFormatException,IOException {
		String model = "Dictionary"; //CRF,CMM,Dictionary
		int k = 0;
		final File folder = new File("files/test/test" + model);
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory()
					&& fileEntry.getName().startsWith("test"))
				k++;
		}
		System.out.println("number of folds, k = " + k);
		System.out.println();
		Set<String> entitySet = new HashSet<String>();

		for (int i = 0; i < k; i++) {
			String testFile = "files/test/test" + model +"/test_" + model+ "_" + i + ".tsv";
			
			BufferedReader bReader = new BufferedReader(
					new FileReader(testFile));
			String line;
			while ((line = bReader.readLine()) != null) {
				if (line.isEmpty())
					continue;
				String datavalue[] = line.split("\t");
				if (datavalue.length != 3) {
					System.out.println("Incorrect test format: " + line);
					continue;
				}
				String e = datavalue[2];
				entitySet.add(e);
			}
			bReader.close();
		}

		boolean printedHeader = false;
		entitySet.remove("O");
		List<String> entityList = new ArrayList<String>();
		entityList.addAll(entitySet);
		Collections.sort(entityList);
		int n = entityList.size();
		double[][] tp = new double[k][n];
		double[][] fp = new double[k][n];
		double[][] fn = new double[k][n];
		double[] ntp = new double[n];
		double[] nfp = new double[n];
		double[] nfn = new double[n];
		double[] nprecision = new double[n];
		double[] nrecall = new double[n];
		double[] nf1 = new double[n];
		double totaltp = 0, totalfp = 0, totalfn = 0;
		double totalPrecision = 0, totalRecall = 0, totalF1 = 0;

		for (int i = 0; i < k; i++) {
			String testFile = "files/test/test" + model +"/test_" + model+ "_" + i + ".tsv";
			BufferedReader bReader = new BufferedReader(
					new FileReader(testFile));
			String line;
			while ((line = bReader.readLine()) != null) {
				if (line.isEmpty())
					continue;
				String datavalue[] = line.split("\t");
				if (datavalue.length != 3) {
					System.out.println("Incorrect test format: " + line);
					continue;
				}
				String g = datavalue[1];
				String l = datavalue[2];

				if (entityList.contains(l)) {
					int index = entityList.indexOf(l);
					if (l.equalsIgnoreCase(g)) {
						tp[i][index]++;
					} else {
						fp[i][index]++;
					}
				}
				if (entityList.contains(g)) {
					int index = entityList.indexOf(g);
					if (g.equalsIgnoreCase(l)) {
						// tp is already counted
					} else {
						fn[i][index]++;
					}
				}
			}
			bReader.close();
		}

		for (int i = 0; i < k; ++i) {
			System.out.println("Fold :" + i);
			for (int j = 0; j < n; j++) {
				printedHeader = printPRLine(entityList.get(j) + " Fold" + i,
						tp[i][j], fp[i][j], fn[i][j], printedHeader);
				nprecision[j] += precision;
				nrecall[j] += recall;
				nf1[j] += f1;
			}
			printedHeader = false;
			System.out.println();
		}

		System.out.println("                   Entity\tP\tR\tF1");
		for (int i = 0; i < n; ++i) {
			double macroPrecision = nprecision[i] / k;
			double macroRecall = nrecall[i] / k;
			double macroF1 = nf1[i] / k;
			System.out.format("%25s\t%.4f\t%.4f\t%.4f\n", entityList.get(i)
					+ " Macro-average", macroPrecision, macroRecall, macroF1);

			totalPrecision += macroPrecision;
			totalRecall += macroRecall;
			totalF1 += macroF1;
		}
		System.out.format("%25s\t%.4f\t%.4f\t%.4f\n", "Total Macro-average",
				totalPrecision / n, totalRecall / n, totalF1 / n);
		System.out.println();

		for (int j = 0; j < n; ++j) {
			for (int i = 0; i < k; ++i) {
				ntp[j] += tp[i][j];
				nfp[j] += fp[i][j];
				nfn[j] += fn[i][j];
			}
		}

		for (int i = 0; i < n; i++) {
			printedHeader = printPRLine(entityList.get(i) + " Micro-average",
					ntp[i], nfp[i], nfn[i], printedHeader);

			totaltp += ntp[i];
			totalfp += nfp[i];
			totalfn += nfn[i];
		}

		printPRLine("Total Micro-average", totaltp, totalfp, totalfn,
				printedHeader);
	}

	private static boolean printPRLine(String entity, double tp, double fp,
			double fn, boolean printedHeader) {
		if (tp == 0.0 && (fp == 0.0 || fn == 0.0))
			return printedHeader;
		precision = tp / (tp + fp);
		recall = tp / (tp + fn);
		f1 = ((precision == 0.0 || recall == 0.0) ? 0.0
				: 2.0 / (1.0 / precision + 1.0 / recall));
		if (!printedHeader) {
			System.out
					.println("                   Entity\tP\tR\tF1\tTP\tFP\tFN");
			printedHeader = true;
		}
		System.out.format("%25s\t%.4f\t%.4f\t%.4f\t%.0f\t%.0f\t%.0f\n", entity,
				precision, recall, f1, tp, fp, fn);
		return printedHeader;
	}
}
