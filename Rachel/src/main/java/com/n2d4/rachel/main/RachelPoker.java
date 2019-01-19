package com.n2d4.rachel.main;

import java.util.Arrays;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import com.n2d4.rachel.learning.CostFunction;
import com.n2d4.rachel.learning.neuralnetwork.NeuralNetwork;
import com.n2d4.rachel.main.gameengines.CardGame;
import com.n2d4.rachel.main.gameengines.CardGame.Card;
import com.n2d4.rachel.util.Requirements;
import com.n2d4.rachel.vectorization.ContinuousDataSet;
import com.n2d4.rachel.vectorization.InputSet;
import com.n2d4.rachel.vectorization.OutputSet;
import com.n2d4.rachel.vectorization.TestSet;
import com.n2d4.rachel.vectorization.TrainingSet;
import com.n2d4.rachel.vectorization.ValidationSet;
import com.n2d4.rachel.vectorization.VectorizedData;

public class RachelPoker {
	
	
	public static void main(String[] args) {
		int cardCount = 5;
		int playerCount = 2;
		int trainOnEvery = 25;
		int trainingSetSize = 300;
		
		int totalIterations = 1_000_000_000;
		int logEvery = 200_000;
		int logIterations = 100_000;
		
		
		PokerDataSet dataSet = new PokerDataSet(cardCount * 2, trainingSetSize);
		NeuralNetwork network = new NeuralNetwork(dataSet, 0.3, 50, 50);
		
		
		
		for (int iterations = 1; iterations <= totalIterations; iterations++) {
			Card[][] hands = getHands(playerCount, cardCount);
			double[] values = getValues(hands);
			int winner = getWinner(values);
			
			
			for (int i = 0; i < hands.length; i++) {
				dataSet.add(createData(hands[i]), new double[] {i == winner ? 1 : 0});
			}
			
			if (iterations * playerCount > trainingSetSize && iterations % trainOnEvery == 0) {
				network.train();
			}
			
			if (iterations % logEvery == 0) {
				System.out.println("Training iteration " + iterations + " after " + network.getIterationCount() + " network iterations:");
				System.out.println(network);
				System.out.println("Training set error:\n" + network.getTrainingError());
				System.out.println("Linear training set error:\n" + network.getTrainingError(CostFunction.LINEAR));
				int correct = 0;
				for (int i = 0; i < logIterations; i++) {
					Card[][] logHands = getHands(playerCount, cardCount);
					double[] logValues = getValues(logHands);
					int logWinner = getWinner(logValues);
					
					double[][] in = new double[logHands.length][];
					for (int j = 0; j < logHands.length; j++) {
						in[j] = createData(logHands[j]);
					}
					
					INDArray arr = VectorizedData.getINDArray(network.process(new InputSet(in)));
					for (int j = 0; j < logHands.length; j++) {
						double out = arr.getDouble(j);
						if ((out > 0.5) == (j == logWinner)) {
							correct++;
						}
					}
				}
				System.out.println("Correct: " + correct + "/" + (playerCount * logIterations) + " (" + String.format("%.1f", (100 * correct / (double) (playerCount * logIterations))) + "%)");
				System.out.println();
			}
		}
	}
	
	
	
	
	private static int getWinner(double[] values) {
		int winner = -1;
		double winningValue = 0;
		for (int i = 0; i < values.length; i++) {
			if (values[i] > winningValue) {
				winner = i;
				winningValue = values[i];
			}
		}
		return winner;
	}
	
	private static Card[][] getHands(int playerCount, int cardCount) {
		Card[][] hands = new Card[playerCount][cardCount];
		Card[] stack = Card.getShuffledStack();
		int i = 0;
		for (int j = 0; j < hands.length; j++) {
			hands[j] = Arrays.copyOfRange(stack, i, i += cardCount);
		}
		return hands;
	}
	
	private static double[] getValues(Card[][] hands) {
		double[] values = new double[hands.length];
		for (int i = 0; i < hands.length; i++) {
			values[i] = CardGame.getPokerValue(hands[i]);
		}
		return values;
	}
	
	private static double[] createData(Card[] hand) {
		double[] result = new double[2 * hand.length];
		int i = 0;
		for (Card c : hand) {
			result[i++] = c.getColor().ordinal();
			result[i++] = c.getType().ordinal();
		}
		return result;
	}
	
	
	
	
	
	
	

	public static void testPoker(String args[]) {
		for (int i = 1; i <= 10_000_000; i++) {
			Card[] hand = Card.sort(Card.getRandom(7));
			double value = CardGame.getPokerValue(hand);
			
			if (value >= 8) {
				System.out.println("Poker Round #" + i + ":");
				for (Card card : hand) {
					System.out.print(card.getName() + " ");
				}
				System.out.println();
				System.out.println("That's " + value + "!");
				System.out.println();
				System.out.println();
			}
		}
	}
	
	
	
	
	
	
	
	
	private static class PokerDataSet implements ContinuousDataSet {
		
		private final int setSize;
		private final int inputSize;
		private final InputSet inputSet;
		private final OutputSet outputSet;
		private int counter = 0;
		
		public PokerDataSet(int inputSize, int setSize) {
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
