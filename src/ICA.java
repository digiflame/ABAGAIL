import shared.DataSet;
import shared.Instance;
import shared.DataHandler;
import shared.filt.IndependentComponentAnalysis;

import static java.lang.Integer.*;
import java.util.*;
import java.io.*;

/**
 *	args => {
 *		[String] path to data file
 *		[int] number of instances
 *		[int] number of Attributes
 *		[int] dimensionality
 *		[String] output file path
 *	}
 */
public class ICA {

	private static PrintWriter out;
    public static void main(String[] args) throws Exception {
		int numInstances = parseInt(args[1]);
		int numAttributes = parseInt(args[2]);
		int targetDimensionality = parseInt(args[3]);

		DataSet set = DataHandler.getDataSet(args[0], numInstances);

		out = new PrintWriter(args[4] + "/original.txt");
		out.println(set);
		out.flush();
		out.close();

        IndependentComponentAnalysis filter = new IndependentComponentAnalysis(set, targetDimensionality);
        filter.filter(set);

		out = new PrintWriter(args[4] + "/afterICA.txt");
		System.out.println("number of instances " + set.size());
		System.out.println("number of attributes " + set.get(0).size());
		out.println(set);
		out.flush();
		out.close();
    }
}
