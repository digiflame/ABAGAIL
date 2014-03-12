package shared;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import shared.DataSet;
import shared.*;
import shared.DataSetDescription;
import shared.reader.ArffDataSetReader;
import shared.reader.DataSetReader;
import shared.filt.ContinuousToDiscreteFilter;
import shared.filt.LabelSplitFilter;
import shared.reader.DataSetLabelBinarySeperator;

/**
 * Reads a data set
 *
 * @author joker23
 * @version 1.0
 */
public class DataHandler {

	private static double inf = Double.MAX_VALUE;

	/**
	 * reads a file and returns a dataset
	 *
	 * @param datafile file that has the data
	 * @param instanceNum number of instances in the dataset
	 */

	public static DataSet getDataSet(String data, int instanceNum) {
		//finds the extension
		String extension = data.substring(data.lastIndexOf(".") + 1, data.length());
		if (extension.equals("txt")){
			return makeDataSet(new File(data), instanceNum);
		} else {
			//TODO
			System.err.println("please enter in a .txt file");
			return null;
		}
	}

	private static DataSet makeDataSet(File file, int numInstances) {
		double[][][] attr = new double[numInstances][][];
		boolean[] missing = null;
		try{
			BufferedReader in = new BufferedReader(new FileReader(file));
			String[] st;

			for(int i = 0; i < numInstances; i++){
				st = in.readLine().split("[,]");

				if(missing == null){
					missing = new boolean[st.length - 1];
				}

				attr[i] = new double[2][];
				attr[i][0] = new double[st.length - 1];
				attr[i][1] = new double[1];

				for(int j = 0; j < st.length - 1; j++){
					String tmp = st[j];
					if(tmp.equals("?")){
						missing[j] = true;
						attr[i][0][j] = inf;
					} else {
						attr[i][0][j] = Double.parseDouble(st[j]);
					}
				}

				attr[i][1][0] = Double.parseDouble(st[st.length - 1]);
			}

			//handle unknowns by assinging them the average
			for (int k = 0; k < missing.length; k++) {
				if (missing[k]) {
					double avg = 0;
					int n = 0;
					for (int j = 0; j < numInstances; j++){
						if (attr[j][0][k] < inf){
							avg += attr[j][0][k];
							n++;
						}
					}
					avg /= n;

					for (int j = 0; j < numInstances; j++) {
						if(attr[j][0][k] == inf){
							attr[j][0][k] = avg;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		Instance[] instances = new Instance[numInstances];

		for (int i = 0; i < numInstances; i++) {
			instances[i] = new Instance(attr[i][0]);
			//TODO make a bundle of splitting functions
			instances[i].setLabel(new Instance(attr[i][1][0] > 8 ? 1 : 0));
		}

		return new DataSet(instances);
	}

	/**
	 * helper function to handle different kind of data set
	 *
	 * @param datafile 	file that has the data
	 * @param reader 	DataSetReader that allows us to parse the dataset
	 */
	private static DataSet getDataSet(String file, DataSetReader reader){
		//TODO
		return null;
	}
}
