package com.n2d4.rachel.main;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import com.n2d4.rachel.learning.CostFunction;
import com.n2d4.rachel.learning.neuralnetwork.NeuralNetwork;
import com.n2d4.rachel.main.gameengines.CardGame;
import com.n2d4.rachel.main.gameengines.CardGame.Card;
import com.n2d4.rachel.main.gameengines.CardGame.CardColor;
import com.n2d4.rachel.main.gameengines.CardGame.CardType;
import com.n2d4.rachel.util.Requirements;
import com.n2d4.rachel.vectorization.ContinuousDataSet;
import com.n2d4.rachel.vectorization.InputSet;
import com.n2d4.rachel.vectorization.OutputSet;
import com.n2d4.rachel.vectorization.TestSet;
import com.n2d4.rachel.vectorization.TrainingSet;
import com.n2d4.rachel.vectorization.ValidationSet;
import com.n2d4.rachel.vectorization.VectorizedData;

public class RachelCards {

	public static void main(String[] args) {
		final int cardCount = 4;
		final int ofWhich = 4;
		final int trainOnEvery = 25;
		final int trainingSetSize = 300;
		
		final int totalIterations = 1_000_000_000;
		final int logEvery = 50_000;
		final int logIterations = 20_000;
		final double learningRate = 0.5;
		
		final boolean compactData = false;
		
		final boolean careAboutColor = true;
		
		
		
		
		System.out.println("Card count: " + cardCount);
		System.out.println("Of which: " + ofWhich);
		System.out.println("Train on every: " + trainOnEvery);
		System.out.println("Training set size: " + trainingSetSize);
		
		System.out.println("Total iterations: " + totalIterations);
		System.out.println("Log every: " + logEvery);
		System.out.println("Log iterations: " + logIterations);
		System.out.println("Learning rate: " + learningRate);
		
		System.out.println("Compact data: " + compactData);
		
		System.out.println("Color mode: " + careAboutColor);
		System.out.println();
		
		
		
		
		CardsDataSet dataSet = new CardsDataSet(cardCount * (!compactData ? CardType.getTotalCount() + CardColor.values().length : 2), trainingSetSize);
		NeuralNetwork network = new NeuralNetwork(dataSet, learningRate, 200, 200);
		
		
		for (int iterations = 1; iterations <= totalIterations; iterations++) {
			Card[] cards = Card.getRandom(cardCount);
			double value = CardGame.getSimpleValue(cards, ofWhich, careAboutColor);
			
			dataSet.add(createData(cards, compactData), new double[] {value});
			
			if (iterations > trainingSetSize && iterations % trainOnEvery == 0) {
				network.train();
			}
			
			if (iterations % logEvery == 0) {
				System.out.println("Training iteration " + iterations + " after " + network.getIterationCount() + " network iterations:");
				System.out.println(network);
				System.out.println("Training set error:\n" + network.getTrainingError());
				System.out.println("Linear training set error:\n" + network.getTrainingError(CostFunction.LINEAR));
				int correct = 0;
				int totPositives = 0;
				int totNegatives = 0;
				int truePositives = 0;
				int trueNegatives = 0;
				int falsePositives = 0;
				int falseNegatives = 0;
				for (int i = 0; i < logIterations; i++) {
					Card[] logCards = Card.getRandom(cardCount);
					double logValue = CardGame.getSimpleValue(logCards, ofWhich, careAboutColor);
					
					INDArray arr = VectorizedData.getINDArray(network.process(new InputSet(createData(logCards, compactData))));
					double out = arr.getDouble(0);
					boolean cor = (logValue > 0.5);
					boolean exp = (out > 0.5);
					if (cor == exp) {
						correct++;
						if (cor) truePositives++;
						else trueNegatives++;
					} else {
						if (cor) falsePositives++;
						else falseNegatives++;
					}
					if (cor) totPositives++;
					else totNegatives++;
				}
				System.out.println("Correct: " + correct + "/" + (logIterations) + " (" + String.format("%.1f", (100 * correct / (double) logIterations)) + "%)");
				System.out.println("True positives: " + truePositives + "/" + (totPositives) + " (" + String.format("%.1f", (100 * truePositives / (double) totPositives)) + "%)");
				System.out.println("False negatives: " + falsePositives + "/" + (totPositives) + " (" + String.format("%.1f", (100 * falsePositives / (double) totPositives)) + "%)");
				System.out.println("True negatives: " + trueNegatives + "/" + (totNegatives) + " (" + String.format("%.1f", (100 * trueNegatives / (double) totNegatives)) + "%)");
				System.out.println("False positives: " + falseNegatives + "/" + (totNegatives) + " (" + String.format("%.1f", (100 * falseNegatives / (double) totNegatives)) + "%)");
				System.out.println();
			}
		}
	}
	
	
	private static double[] createData(Card[] hand, boolean compact) {
		return compact ? createDataCompact(hand) : createData(hand);
	}
	
	
	private static double[] createData(Card[] hand) {
		int totalColors = CardColor.values().length;
		int totalTypes = CardType.values().length;
		double[] result = new double[hand.length * (totalColors + totalTypes)];
		int i = 0;
		for (Card c : hand) {
			result[i + c.getColor().ordinal()] = 1;
			i += totalColors;
			result[i + c.getType().ordinal()] = 1;
			i += totalTypes;
		}
		return result;
	}
	
	
	private static double[] createDataCompact(Card[] hand) {
		double[] result = new double[hand.length * 2];
		int i = 0;
		for (Card c : hand) {
			result[i++] = c.getColor().ordinal();
			result[i++] = c.getType().ordinal();
		}
		return result;
	}

	
	
	
	
	
	
	
	
	
	private static class CardsDataSet implements ContinuousDataSet {
		
		private final int setSize;
		private final int inputSize;
		private final InputSet inputSet;
		private final OutputSet outputSet;
		private int counter = 0;
		
		public CardsDataSet(int inputSize, int setSize) {
			this.inputSize = inputSize;
			this.setSize = setSize;
			
			inputSet = new InputSet(Nd4j.randn(setSize, getInputSize()));
			outputSet = new OutputSet(Nd4j.randn(setSize, getOutputSize()));
		}
		
		@Override public TrainingSet getTrainingSet() {
			return new TrainingSet(inputSet, outputSet);
		}
		
		@Override public ValidationSet getValidationSet() {
			return new ValidationSet(inputSet, outputSet);
		}
		
		@Override public TestSet getTestSet() {
			return new TestSet(inputSet, outputSet);
		}
		
		@Override public int getInputSize() {
			return inputSize;
		}
	
		@Override public int getOutputSize() {
			return 1;
		}
		public void add(double[] input, double[] output) {
			Requirements.equal(input.length, getInputSize(), "input length");
			Requirements.equal(output.length, getOutputSize(), "output length");
			
			VectorizedData.getINDArray(inputSet).putRow(counter, Nd4j.create(input));
			VectorizedData.getINDArray(outputSet).putRow(counter, Nd4j.create(output));
			counter = ++counter % setSize;
		}
	}


}
