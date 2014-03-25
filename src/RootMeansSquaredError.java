import static java.lang.Math.*;
import static java.lang.Integer.*;
import static java.lang.Double.*;

import java.util.*;
import java.io.*;

public class RootMeansSquaredError {

	private static BufferedReader buf1;
	private static BufferedReader buf2;

	private static StringTokenizer st1;
	private static StringTokenizer st2;
	/**
	 *	args => {
	 *		[string] file path 1
	 *		[string] file path 2
	 *		[int] number of instances
	 *	}
	 */
	public static void main (String[] args) {

		int n = parseInt(args[3]);
		try {
			buf1 = new BufferedReader(new FileReader(args[0]));
			buf2 = new BufferedReader(new FileReader(args[1]));

			double sum = 0.0;
			double div = 0;

			for(int i=0; i<n; i++) {
				st1 = new StringTokenizer(buf1.readLine(), ",");
				st2 = new StringTokenizer(buf2.readLine(), ",");

				while(st1.hasMoreTokens() && st2.hasMoreTokens()) {
					double a = parseDouble(st1.nextToken());
					double b = parseDouble(st2.nextToken());

					sum += pow(abs(a - b), 2);
					div ++;
				}
			}

			sum /= div;

			System.out.println(sum);

		} catch (Exception e) {
			System.out.println("Failed to read the buffer");
		}
	}
}
