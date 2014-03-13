import java.util.Arrays;
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

public class CountOnesOpt {
	private static final int N = 1000; // let us do 1<<1,000 :)
	private static final int NUM_ITERATIONS = 1000;

	private static DecimalFormat df = new DecimalFormat("0.000");

	public static void main(String[] args) throws Exception {
		int[] ranges = new int[N];
		Arrays.fill(ranges, 2);
		EvaluationFunction ef = new CountOnesEvaluationFunction();
		Distribution odd = new DiscreteUniformDistribution(ranges);
		NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
		MutationFunction mf = new DiscreteChangeOneMutation(ranges);
		CrossoverFunction cf = new UniformCrossOver();
		Distribution df = new DiscreteDependencyTree(.1, ranges);
		GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
		ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
		HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);

		SimulatedAnnealing sa = new SimulatedAnnealing(100, .95, hcp);
		train(sa, ef, "sa");

		StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(20, 20, 0, gap);
		train(ga, ef, "ga");

		MIMIC mimic = new MIMIC(50, 10, pop);
		train(mimic, ef, "mimic");
	}

	public static void train(OptimizationAlgorithm algo, EvaluationFunction evalFunction, String name) throws Exception {
		System.out.println("Training " + name);

		PrintWriter out = new PrintWriter(name + "_co.dat");

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
