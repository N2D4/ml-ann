package com.n2d4.rachel.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

import com.n2d4.rachel.learning.ActivationFunction;
import com.n2d4.rachel.learning.CostFunction;
import com.n2d4.rachel.learning.WeightApplyFunction;
import com.n2d4.rachel.learning.neuralnetwork.NeuralNetwork;
import com.n2d4.rachel.vectorization.DataSet;
import com.n2d4.rachel.vectorization.InputSet;
import com.n2d4.rachel.vectorization.LayerWeights;
import com.n2d4.rachel.vectorization.OutputSet;
import com.n2d4.rachel.vectorization.StaticDataSet;
import com.n2d4.rachel.vectorization.VectorizedData;

/**
 * Blame XKCD for the name
 */
public class Rachel {
	
	public static void main(String[] args) {
		try (Scanner reader = new Scanner(System.in);) {
			
			
			
			Random genrnd = new Random(42133769L);
			
			
			int dataSource = 8; // -1 = output the input, 0 = random input, pre-defined target weights; 1 = random input, sinus-curved output; 2 = in-output from data; 3 = random output, 4 = expected linear weights, small Gaussian distribution, 5 = XOR, 6 = AND, 7 = comparison, 8 = poker data set
			
			InputSet inputSet;
			OutputSet outputSet;
			switch (dataSource) {
			case -1:
				inputSet = new InputSet(generateCoolArray(500, 1));
				outputSet = new OutputSet(VectorizedData.getINDArray(inputSet));
				break;
			case 0: case 4:
				inputSet = new InputSet(generateInputSetArray(genrnd, 1));
				LayerWeights expectedWeights = new LayerWeights(new double[][] {{0.15, 0.75}/*, {2, 1}, {0, -1}*/});
				System.out.println("Actual parameters:");
				System.out.println(expectedWeights.toString());
				outputSet = expectedWeights.applyOn(inputSet, WeightApplyFunction.MATRIX_MULT, ActivationFunction.LINEAR);
				System.out.println(outputSet);
				if (dataSource == 0) {
					outputSet = polarize(outputSet, genrnd);
				} else if (dataSource == 4) {
					outputSet = gaussianDist(outputSet, genrnd);
				}
				break;
			case 1:
				inputSet = new InputSet(generateInputSetArray(genrnd, 1));
				outputSet = new OutputSet(Transforms.sin(VectorizedData.getINDArray(inputSet)).add(1).div(2));
				break;
			case 2:
				System.out.println("Iris dataset path: ");
				double[][][] set = readSet(reader.nextLine());
				inputSet = new InputSet(set[0]);
				outputSet = new OutputSet(set[1]);
				break;
			case 3:
				inputSet = new InputSet(generateInputSetArray(genrnd, 1));
				double[][] out = new double[inputSet.getSetCount()][1];
				for (int i = 0; i < out.length; i++) {
					out[i][0] = genrnd.nextDouble() < 0.7 ? 1 : 0;
				}
				outputSet = new OutputSet(out);
				break;
			case 5:
				inputSet = new InputSet(new double[][] {{0, 0}, {0, 1}, {1, 0}, {1, 1}});
				outputSet = new OutputSet(new double[][]{{0}, {1}, {1}, {0}});
				break;
			case 6:
				inputSet = new InputSet(new double[][] {{0, 0}, {0, 1}, {1, 0}, {1, 1}});
				outputSet = new OutputSet(new double[][]{{0}, {0}, {0}, {1}});
				break;
			case 7:
				inputSet = new InputSet(generateInputSetArray(genrnd, 2));
				System.out.println(VectorizedData.getINDArray(inputSet).shapeInfoToString());
				out = new double[inputSet.getSetCount()][1];
				for (int i = 0; i < out.length; i++) {
					double[] dset = inputSet.getSet(i);
					out[i][0] = (dset[1] > dset[0] && dset[1] > -2 && !(dset[1] > 3 && dset[1] < 4)) ? 1 : 0;
				}
				outputSet = new OutputSet(out);
				break;
			case 8:
				System.out.println("Poker dataset path: ");
				set = readPokerSet(reader.nextLine());
				inputSet = new InputSet(set[0]);
				outputSet = new OutputSet(set[1]);
				break;
			default:
				System.out.println("Illegal data source");
				return;
			}
			
			
			DataSet dataSet = new StaticDataSet(inputSet, outputSet);

			System.out.println("Actual data set:");
			System.out.println(dataSet.toString());
			System.out.println(outputSet);
			System.out.println();
			
			
			/*INDArray ksetI = VectorizedData.getINDArray(dataSet.getTrainingSet().getInputSet());
			INDArray ksetO = VectorizedData.getINDArray(dataSet.getTrainingSet().getOutputSet());
			for (int i = 0; i < ksetI.rows(); i++) {
				System.out.println("			\\addplot[mark = x] (" + ksetI.getDouble(i, 0) + ", " + ksetO.getDouble(i, 0) + ");");
			}*/
			
			
			
		
			int iterations = 10_000_000;
			int logevery = 1000;
			int batchSize = 300;
			double learningRateMin = 3;
			double learningRateMax = 3;
			double learningRateGrowth = 3;
			int[][] layerSizes = {{50, 50}};
			
			for (int[] layerSize : layerSizes) {
				for (double learningRate = learningRateMin; learningRate <= learningRateMax; learningRate *= learningRateGrowth > 1 ? learningRateGrowth : 3) {
					NeuralNetwork network = new NeuralNetwork(dataSet, learningRate, layerSize);
					
					for (int i = 1; i <= iterations; i++) {
						boolean log = logevery > 0 && (i % logevery == 0 || i == iterations);
						
						if (log) {
							OutputSet out = network.processTestSet();
							System.out.println("=== Prior to training iteration " + i + " ===");
							System.out.println(network);
							
							
							/*InputSet coolInSet = new InputSet(generateCoolArray(1000, 16));
							INDArray ksetI = VectorizedData.getINDArray(coolInSet);
							ksetI.subi(8);
							INDArray ksetO = VectorizedData.getINDArray(network.process(coolInSet));
							for (int mk = 0; mk < ksetI.rows(); mk++) {
								System.out.print("(" + String.format("%.2f", ksetI.getDouble(mk, 0)) + ", " + String.format("%.2f", ksetO.getDouble(mk, 0)) + ") ");
							}
							System.out.println();*/
							
							
							System.out.println(out);
							System.out.println("Difference:\n" + out.getDifference(network.getDataSet().getTestSet().getOutputSet()));
							System.out.println("Training set error:\n" + network.getTrainingError());
							System.out.println("Validation set error:\n" + network.getValidationError());
							System.out.println("Test set error:\n" + network.getError());
							System.out.println("Linear test set error:\n" + network.getError(CostFunction.LINEAR));
						}
						
						long timeStarted = System.nanoTime();
						if (batchSize > 0) network.train(batchSize);
						else network.train();
						long timeEnded = System.nanoTime();
						
						if (log) {
							System.out.println("Training iteration " + i + " complete after " + String.format("%.2f", (timeEnded - timeStarted) / 1_000_000d) + "ms!");
							System.out.println();
						}
					}
					
					OutputSet out = network.processTestSet();
					
					System.out.println("Final output with learning rate " + learningRate + " and layer sizes " + Arrays.toString(layerSize) + ":");
					System.out.println(network);
					System.out.println(out);
					System.out.println("Difference:\n" + out.getDifference(network.getDataSet().getTestSet().getOutputSet()));
					System.out.println("Training set error:\n" + network.getTrainingError());
					System.out.println("Validation set error:\n" + network.getValidationError());
					System.out.println("Test set error:\n" + network.getError());
					System.out.println("Linear test set error:\n" + network.getError(CostFunction.LINEAR));
					System.out.println();
				}
			}
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	

	protected static final double[][] generateInputSetArray(Random rand, int dim) {
		double[][] result = new double[rand.nextInt(500) + 1000][dim];
		
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[i].length; j++) {
				result[i][j] = rand.nextDouble() * (rand.nextBoolean() ? 8 : -8) + 2 * rand.nextGaussian() * rand.nextDouble();
			}
		}
		
		return result;
	}
	
	protected static final double[][][] readSet(String path) throws NumberFormatException, IOException {
		if (path.equals(".")) path = System.getProperty("user.home") + "/Desktop/dataset.txt";
		List<double[]> input = new ArrayList<>();
		List<double[]> output = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String s;
		while ((s = reader.readLine()) != null) {
			if (s.equals("")) continue;
			double[] curin = new double[4];
			double[] curout = new double[3];
			String[] arrs = s.split(",");
			for (int i = 0; i < curin.length; i++) {
				curin[i] = Double.parseDouble(arrs[i]);
			}
			switch (arrs[4]) {
			case "Iris-setosa":
				curout[0] = 1d;
				break;
			case "Iris-versicolor":
				curout[1] = 1d;
				break;
			case "Iris-virginica":
				curout[2] = 1d;
				break;
			}
			input.add(curin);
			output.add(curout);
		}
		reader.close();
		return new double[][][] {input.toArray(new double[0][]), output.toArray(new double[0][])};
	}
	
	protected static final double[][][] readPokerSet(String path) throws NumberFormatException, IOException {
		if (path.equals(".")) path = System.getProperty("user.home") + "/Desktop/pokerset.txt";
		List<double[]> input = new ArrayList<>();
		List<double[]> output = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String s;
		while ((s = reader.readLine()) != null) {
			if (s.equals("")) continue;
			double[] curin = new double[10];
			double[] curout = new double[10];
			String[] arrs = s.split(",");
			for (int i = 0; i < curin.length; i++) {
				curin[i] = Double.parseDouble(arrs[i]);
			}
			curout[Integer.parseInt(arrs[curin.length])] = 1;
			input.add(curin);
			output.add(curout);
		}
		reader.close();
		return new double[][][] {input.toArray(new double[0][]), output.toArray(new double[0][])};
	}
	
	protected static final double[][] generateCoolArray(int size, double bound) {
		double[][] result = new double[size][1];
		for (int i = 0; i < result.length; i++) {
			result[i][0] = 1d / (size - 1) * bound * i;
		}
		return result;
	}
	
	
	protected static final OutputSet polarize(OutputSet set, Random rand) {
		INDArray arr = VectorizedData.getINDArray(set);
		int[] shape = arr.shape();
		int[] cur = new int[shape.length];
		
		mainloop: while (true) {
			arr.putScalar(cur, rand.nextGaussian() < arr.getDouble(cur) ? 1 : 0);
			for (int i = cur.length - 1; i >= 0; i--) {
				if (++cur[i] == shape[i]) cur[i] = 0;
				else continue mainloop;
			}
			break;
		}
		
		return set;
	}
	
	
	protected static final OutputSet gaussianDist(OutputSet set, Random rand) {
		INDArray arr = VectorizedData.getINDArray(set);
		arr.addi(Nd4j.randn(arr.rows(), arr.columns(), rand.nextLong()).muli(0.2d));
		
		return set;
	}

}
