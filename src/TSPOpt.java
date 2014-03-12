import opt.example.*;
import opt.*;
import opt.ga.*;
import opt.prob.*;
import shared.*;
import util.*;
import dist.*;

import java.util.*;
import java.math.*;
import java.io.*;
import java.text.*;
import static java.lang.Math.*;

public class TSPOpt {

	//variables for solving the proble
	private static double[][] points; 				//the points in our tsp graph
	private static final int N = 100;				//the number of points that we will have
	private static final int NUM_ITERATIONS = 1000; //number of training iterations that we will do
	private static String[] names = {"SA", "GA", "MIMIC"};

	private static Random rand;
	private static OptimizationAlgorithm[] algo;		//optimization alogrithm
	private static EvaluationFunction evalFunction;	//evaluation function

	//pretty stuff
	private static DecimalFormat df = new DecimalFormat("0.000");

	public TSPOpt () {
		rand = new Random();
		points = new double[N][2];

		for(int i=0; i<N; i++){
			points[i][0] = rand.nextDouble();
			points[i][1] = rand.nextDouble();
		}

		evalFunction = new TravelingSalesmanRouteEvaluationFunction(points);

		algo = new OptimizationAlgorithm[3];
		algo[0] = new SimulatedAnnealing(1E12, .85, new GenericHillClimbingProblem(evalFunction, new DiscretePermutationDistribution(N), new SwapNeighbor()));

		algo[1] = new StandardGeneticAlgorithm(200, 100, 10, new GenericGeneticAlgorithmProblem(evalFunction, new DiscretePermutationDistribution(N), new SwapMutation(), new TravelingSalesmanCrossOver((TravelingSalesmanEvaluationFunction) evalFunction)));

		int[] r = new int[N];
		Arrays.fill(r, N);

		algo[2] = new MIMIC(200, 100, new GenericProbabilisticOptimizationProblem(evalFunction, new DiscreteUniformDistribution(r), new DiscreteDependencyTree(.1, r)));
	}

	public static void main(String[] args) throws Exception {
		TSPOpt opt = new TSPOpt();

		for(int i=0; i<3; i++) opt.train(i);

	}

	private void train(int i) throws Exception{
		System.out.println("Training " + names[i]);

		PrintWriter out = new PrintWriter(new File(names[i] + "_TSP.dat"));

		double optimal = 0;
		double t0 = System.nanoTime();
		double topt = 0;
		double tend = 0;
		double totTime = 0;

		for(int j = 0; j < NUM_ITERATIONS; j++) {
			System.out.println(names[i] + " training iteration : " + j);
			algo[i].train();

			double tmpFit = evalFunction.value(algo[i].getOptimal());
			out.println(tmpFit);
			if(optimal < tmpFit) {
				optimal = tmpFit;
				topt = System.nanoTime();
			}
		}

		tend = System.nanoTime();


		out.println("Total Training Time : " + df.format((tend - t0)/pow(10,9)) + " seconds");
		out.println("Optimal instance found at : " + df.format((topt-t0)/pow(10,9)) + " seoconds");
		out.println("Optimal solution : " + df.format(optimal));

		out.flush();
		out.close();
	}
}
