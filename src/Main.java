import java.util.*;

import shared.DataHandler;

import java.text.*;

//ABAGAIL imports
import dist.*;
import opt.*;
import opt.example.*;
import opt.ga.*;
import shared.*;
import func.nn.backprop.*;

public class Main {

	//variables for solving the problem
	private static DataSet 	dataSet;			//dataset that we are observing
	private static BackPropagationNetworkFactory
		factory;								//produce the backpropagation
	private static
		BackPropagationNetwork network;			//back prop network
	private static
		NeuralNetworkOptimizationProblem optProb; //optimization problem
	private static OptimizationAlgorithm algo; 	//optimization alogrithm
	private static ErrorMeasure measure;		//error measuring scheme

	//input variables
	private static String 	fileName;			//name of the datafile
	private static int		numInstances;		//number of instances
	private static int		numIterations;		//number of training iterations
	private static int		inputLayerSize;		//size of the input Layer of our ann
	private static int		hiddenLayerSize;	//size of the hidden Layer of our ann
	private static int		outputLayerSize;	//size of the output Layer of our ann
	private static String	algorithmName;		//name of the algorithm that we are going to use

	//pretty stuff
	private static DecimalFormat df = new DecimalFormat("0.000");

	public static void main(String[] args){
		//dataSet = DataHandler.getDataSet("/home/steven/ml/test.txt", 452);

		// System.out.println("made it");
		// validate input values
		if(args.length < 7){
			printHelp();
			return;
		}

		factory = new BackPropagationNetworkFactory();
		measure = new SumOfSquaresError();

		//parse input values
		try {
			fileName 			= args[0];
			numInstances 		= Integer.parseInt(args[1]);
			numIterations 		= Integer.parseInt(args[2]);
			inputLayerSize	 	= Integer.parseInt(args[3]);
			hiddenLayerSize 	= Integer.parseInt(args[4]);
			outputLayerSize 	= Integer.parseInt(args[5]);
			algorithmName		= args[6];
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		//get the dataset
		dataSet = DataHandler.getDataSet(fileName, numInstances);
		
		//run the ann
		network = factory.createClassificationNetwork(new int[] {inputLayerSize, hiddenLayerSize, outputLayerSize});
		optProb = new NeuralNetworkOptimizationProblem(dataSet, network, measure);

		if((algo = getOptAlgo(args)) == null) {
			System.err.println("you must have messed up the input somehow...");
			printHelp();
			return;
		}
		
		double trainingTime;
		double testingTime;
		double correct = 0;
		double incorrect = 0;
		double timer = System.nanoTime();

		train();
		trainingTime = System.nanoTime() - timer;
		trainingTime /= Math.pow(10,9);

		Instance optimalInstance = algo.getOptimal();
		network.setWeights(optimalInstance.getData());

		double predicted, actual;
		timer = System.nanoTime();

		for(int j = 0; j < dataSet.size(); j++) {
			Instance currInstance = dataSet.get(j);
			network.setInputValues(currInstance.getData());
			network.run();

			predicted = Double.parseDouble(currInstance.getLabel().toString());
			actual = Double.parseDouble(network.getOutputValues().toString());

			if (Math.abs(predicted - actual) < 0.5) {
				correct++;
			
			} else {
				incorrect++;
			}
		}
		testingTime = System.nanoTime() - timer;
		testingTime /= Math.pow(10,9);

		String results =  "\nResults for " + algorithmName + ": \nCorrectly classified " + correct + " instances." +
			"\nIncorrectly classified " + incorrect + " instances.\nPercent correctly classified: "
			+ df.format(correct/(correct+incorrect)*100) + "%\nTraining time: " + df.format(trainingTime)
			+ " seconds\nTesting time: " + df.format(testingTime) + " seconds\n";

		System.out.println(results);

	}

	private static void train() {

		for(int i = 0; i < numIterations; i++) {
			algo.train();

			double error = 0;
			for(int j = 0; j < dataSet.size(); j++) {
				Instance currInstance = dataSet.get(j);
				network.setInputValues(currInstance.getData());
				network.run();

				Instance output = currInstance.getLabel();
				Instance example = new Instance(network.getOutputValues());
				example.setLabel(new Instance(Double.parseDouble(network.getOutputValues().toString())));
				error += measure.value(output, example);
			}
		}
	}

	private static OptimizationAlgorithm getOptAlgo(String args[]){

		OptimizationAlgorithm ret = null;

		if (algorithmName.equals("-RHC")) {
			
			if(args.length != 7) {
				return null;
			}
			
			ret = new RandomizedHillClimbing(optProb);

		} else if (algorithmName.equals("-SA")) {

			if (args.length != 9) {
				return null;
			}

			ret = new SimulatedAnnealing(Long.parseLong(args[7]), Double.parseDouble(args[8]), optProb);

		} else if (algorithmName.equals("-GA")) {
			if (args.length != 10) {
				return null;
			}

			ret = new StandardGeneticAlgorithm(Integer.parseInt(args[7]), Integer.parseInt(args[8]), Integer.parseInt(args[9]), optProb);
		}

		return ret;
	}


	private static void printHelp(){
		System.out.println("Input should be formatted like this : java -jar Abaii <path-to-data> <number of instances> <training iterations> <inputLayer size> <hiddenLayer size> <outputLayer size> [specific optimization] \n\n");

		System.out.println("input for specific optimizer : ");
		System.out.println("Randomize Hill Climing: -RHC");
		System.out.println("Simulated Annealing : -SA <t> <cooling> ");
		System.out.println("Genetic Algorithm : -GA <population size> <# mate each turn> <# mutate each turn> ");

	}
}
