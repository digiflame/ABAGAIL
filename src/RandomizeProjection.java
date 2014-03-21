import shared.DataSet;
import shared.Instance;
import shared.DataHandler;
import shared.filt.PrincipalComponentAnalysis;
import shared.filt.RandomizedProjectionFilter;
import util.linalg.Matrix;

import java.util.*;
import java.io.*;

public class RandomizeProjection {

	/**
	 * args = {
	 *		[String] path to data file
	 *		[int] number of Instances
	 *		[int] number of Attributes
	 *		[int] number of attributes to project to
	 *		[String] output file path
	 * }
	 */
    public static void main(String[] args) throws Exception{
        int numInstances = Integer.parseInt(args[1]);
		int numAttributes = Integer.parseInt(args[2]);
		int newNumAttributes = Integer.parseInt(args[3]);
		PrintWriter out;

		DataSet set = DataHandler.getDataSet(args[0], numInstances);

		out = new PrintWriter(args[4] + "/original.txt");
		out.println(set);
		out.flush();
		out.close();

        RandomizedProjectionFilter filter = new RandomizedProjectionFilter(newNumAttributes, numAttributes);

		out = new PrintWriter(args[4] + "/trasposedProjection.txt");
        out.println(filter.getProjection().transpose());
		out.flush();
		out.close();

        filter.filter(set);

		out = new PrintWriter(args[4] + "/afterRA.txt");
        out.println(set);
		out.flush();
		out.close();

        Matrix reverse = filter.getProjection().transpose();

		out = new PrintWriter(args[4] + "/afterReconstruction.txt");
        for (int i = 0; i < set.size(); i++) {
            Instance instance = set.get(i);
            instance.setData(reverse.times(instance.getData()));
        }
        out.println(set);
		out.flush();
		out.close();
    }
}
