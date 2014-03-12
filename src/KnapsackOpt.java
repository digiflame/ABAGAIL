import java.util.Arrays;
import java.util.Random;
import java.text.*;
import static java.lang.Math.*;
import java.io.*;
import java.util.*;

import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;

import opt.DiscreteChangeOneNeighbor;
import opt.EvaluationFunction;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteChangeOneMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.ga.UniformCrossOver;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;
import opt.*;

public class KnapsackOpt {
	/** Random number generator */
	private static final Random random = new Random();
	/** The number of items */
	private static final int NUM_ITEMS = 40;
	/** The number of copies each */
	private static final int COPIES_EACH = 4;
	/** The maximum weight for a single element */
	private static final double MAX_WEIGHT = 50;
	/** The maximum volume for a single element */
	private static final double MAX_VOLUME = 50;
	/** The volume of the knapsack */
	private static final double KNAPSACK_VOLUME =
		MAX_VOLUME * NUM_ITEMS * COPIES_EACH * .4;
	private static final int NUM_ITERATIONS = 1000;

	private static DecimalFormat df = new DecimalFormat("0.000");

	public static void main(String[] args) throws Exception{
		int[] copies = new int[NUM_ITEMS];
		Arrays.fill(copies, COPIES_EACH);
		double[] weights = new double[NUM_ITEMS];
		double[] volumes = new double[NUM_ITEMS];
		for (int i = 0; i < NUM_ITEMS; i++) {
			weights[i] = random.nextDouble() * MAX_WEIGHT;
			volumes[i] = random.nextDouble() * MAX_VOLUME;
		}
		int[] ranges = new int[NUM_ITEMS];
		Arrays.fill(ranges, COPIES_EACH + 1);
		EvaluationFunction ef = new KnapsackEvaluationFunction(weights, volumes, KNAPSACK_VOLUME, copies);
		Distribution odd = new DiscreteUniformDistribution(ranges);
		NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
		MutationFunction mf = new DiscreteChangeOneMutation(ranges);
		CrossoverFunction cf = new UniformCrossOver();
		Distribution df = new DiscreteDependencyTree(.1, ranges);
		HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
		GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
		ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);

		SimulatedAnnealing sa = new SimulatedAnnealing(100, .95, hcp);
		train(sa, ef, "sa");
		//sanity test
		//FixedIterationTrainer fit = new FixedIterationTrainer(sa, 1000);
		//fit.train();
		//System.out.println(ef.value(sa.getOptimal()));

		StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 150, 25, gap);
		train(ga, ef, "ga");

		MIMIC mimic = new MIMIC(200, 100, pop);
		train(mimic, ef, "mimic");
	}

	public static void train(OptimizationAlgorithm algo, EvaluationFunction evalFunction, String name) throws Exception {
		System.out.println("Training " + name);

		PrintWriter out = new PrintWriter(name + "_Knap.dat");

		double optimal = 0;
		double t0 = System.nanoTime();
		double topt = 0;
		double tend = 0;
		double totTime = 0;

		for(int j = 0; j < NUM_ITERATIONS; j++) {
			System.out.println(name + " training iteration : " + j);
			algo.train();

			double tmpFit = evalFunction.value(algo.getOptimal());
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
