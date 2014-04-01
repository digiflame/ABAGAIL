import java.util.*;
import java.io.*;
import java.text.*;

public class CalculateKurtosis {

	private static BufferedReader in;
	private static PrintWriter out;
	private static StringTokenizer st;

	private static int attr;
	private static int instances;
	private static String path;
	private static double[][] data;

	/**
	 * args => {
	 * 		[String] file path
	 * 		[int] 	number of attributes
	 * 		[int] 	number of instances
	 * }
	 */

	private static DecimalFormat df = new DecimalFormat("0.000");

	public static void main(String[] args) throws Exception {

		try {
			path = args[0];
			attr = Integer.parseInt(args[1]);
			instances = Integer.parseInt(args[2]);

		} catch (Exception e) {
			System.err.print("incorrect arguments");
		}

		in = new BufferedReader(new FileReader(path));
		out = new PrintWriter(System.out);

		data = new double[instances][attr];
		for(int i=0; i<instances; i++) {
			st = new StringTokenizer(in.readLine(), ",");
			for(int j=0; j<attr; j++) {
				data[i][j] = Double.parseDouble(st.nextToken());
			}
		}

		for(int i=0; i< attr; i++) {
			double avg = 0;
			for(int j=0; j<instances; j++) {
				avg += data[j][i];
			}

			avg /= instances;

			double p = 0, q = 0;
			for(int k=0; k<instances; k++) {
				p += Math.pow(data[k][i] - avg, 4);
				q += Math.pow(data[k][i] - avg, 2);
			}

			p /= instances;
			q /= instances;
			p *= p;

			out.println("Kurtosis for attribute " + i + ": " + df.format(p/q));
		}

		out.flush();
		out.close();
	}
}
