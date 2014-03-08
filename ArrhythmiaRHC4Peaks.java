import java.util.*;

public class ArrhythmiaRHC4Peaks implements Runnable{

	protected DataSet set;
	protected BackPropagationNetworkFactory factory;
	protected BackPropagationNetwork network;

	protected ErrorMeasure measure;
	protected NeuralNetworkOptimizationProblem nno;
	protected EvaluationFunction ef;
	protected OptimizationAlgorithm o;
	protected FixedIterationTrainer fit;


	public ArrhythmiaTHC4Peaks() throws Exception {
		this.set = DataHandler.getDataSet("home/steven/ml/test.txt", 452);

        this.factory = new BackPropagationNetworkFactory();

        this.network = factory.createClassificationNetwork(new int[] { 279, 20, 1 });
        this.measure = new SumOfSquaresError();

        NeuralNetworkOptimizationProblem nno = new NeuralNetworkOptimizationProblem(set, network, measure);
        EvaluationFunction ef = new FourPeaksEvaluationFunction(-.005);
        OptimizationAlgorithm o = new RandomizedHillClimbing(nno);
        FixedIterationTrainer fit = new FixedIterationTrainer(o, 5000);
	}

	public void run() throws Exception {
        fit.train();
        Instance opt = o.getOptimal();
        network.setWeights(opt.getData());

        System.out.println("Random Hill Climbing with Four Peaks");
        System.out.println("Optimal");
        System.out.println(ef.value(o.getOptimal()));

        int[] labels = {0,1};
        TestMetric acc = new AccuracyTestMetric();
        TestMetric cm  = new ConfusionMatrixTestMetric(labels);
        Tester t = new NeuralNetworkTester(network, acc, cm);
        t.test(trainInstances);

        acc.printResults();
        cm.printResults();

	}
}
