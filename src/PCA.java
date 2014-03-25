import shared.DataSet;
import shared.Instance;
import shared.DataHandler;
import shared.filt.PrincipalComponentAnalysis;
import util.linalg.Matrix;

import static java.lang.Integer.*;
import java.util.*;
import java.io.*;

/**
 *	args => {
 *		[String] path to data file
 *		[int] number of instances
 *		[int] number of Attributes
 *		[String] output file path
 *	}
 */
public class PCA {

	private static PrintWriter out;
	private static String outPath;
	private static DataSet set;

	public static void main(String[] args) throws Exception {
		int numInstances = parseInt(args[1]);
		int numAttributes = parseInt(args[2]);

		outPath = args[3];

		set = DataHandler.getDataSet(args[0], numInstances);

		print("original");
		PrincipalComponentAnalysis filter = new PrincipalComponentAnalysis(set);
		print("Eigen value", filter.getEigenValues().toString());
		print("Projection", filter.getProjection().transpose().toString()); //linearizes the dataset...

		filter.filter(set);

		print("filtered");
		Matrix reverse = filter.getProjection().transpose();
		for (int i = 0; i < set.size(); i++) {
			Instance instance = set.get(i);
			instance.setData(reverse.times(instance.getData()).plus(filter.getMean()));
		}

		print("res");
	}

	private static void print(String name) throws Exception {
		out = new PrintWriter(outPath + "/" + name + ".txt");
		out.println(set);
		out.flush();
		out.close();
	}

	private static void print(String name, String stream) throws Exception {
		out = new PrintWriter(outPath + "/" + name + ".txt");
		out.println(stream);
		out.flush();
		out.close();
	}
}
