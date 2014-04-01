import shared.DataSet;
import shared.Instance;
import shared.DataHandler;
import shared.filt.*;
import util.linalg.DenseVector;

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
public class LDA {

	private static PrintWriter out;
	public static void main(String[] args) throws Exception {
		int numInstances = parseInt(args[1]);
		int numAttributes = parseInt(args[2]);

		BufferedReader in = new BufferedReader(new FileReader(args[0]));
		StringTokenizer st;

		Instance[] instances = new Instance[numInstances];

		for(int i = 0; i < instances.length; i++) {
			st = new StringTokenizer(in.readLine(), "[, ]");

			double[] attributes = new double[numAttributes];
			for(int j = 0; j < numAttributes; j++) {
				attributes[j] = Double.parseDouble(st.nextToken());
			}

			instances[i] = new Instance(new DenseVector(attributes), new Instance(Double.parseDouble(st.nextToken())));
		}

		DataSet set = new DataSet(instances);
		System.out.println(set.get(1) );

		out = new PrintWriter(args[3] + "/original.txt");
		out.println(set);
		out.flush();
		out.close();
		System.out.println(set.get(0));

		LinearDiscriminantAnalysis filter = new LinearDiscriminantAnalysis(set);

		System.out.println(set.get(3));
		filter.filter(set);

		out = new PrintWriter(args[3] + "/filterProjection.txt");
		out.println(filter.getProjection());
		out.flush();
		out.close();

		out = new PrintWriter(args[3] + "/afterLDA.txt");
		out.println(set);
		out.flush();
		out.close();

		filter.reverse(set);
		out = new PrintWriter(args[3] + "/newData.txt");
		out.println(set);
		out.flush();
		out.close();

		System.out.println("number of instances : " + set.size());
		System.out.println("number of attributes : " + set.get(0).size());
	}
}
