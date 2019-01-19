package com.n2d4.rachel.main;

import java.util.Arrays;

import com.n2d4.rachel.learning.CostFunction;
import com.n2d4.rachel.learning.neuralnetwork.NeuralNetwork;
import com.n2d4.rachel.learning.qlearning.NeuralQLearner;
import com.n2d4.rachel.learning.qlearning.NeuralQTable;
import com.n2d4.rachel.main.gameengines.CardGame;
import com.n2d4.rachel.main.gameengines.CardGame.Card;
import com.n2d4.rachel.main.gameengines.CardGame.CardColor;

public class RachelThrow {

	public static void main(String[] args) {
		final int cardCount = 3;
		final int trainOnEvery = 30;
		final int trainingSetSize = 500;
		
		final int totalIterations = 1_000_000;
		final int logEvery = 100;
		final int exampleEvery = 1000;
		final double neuralLearningRate = 1;
		final int[] hiddenLayerSizes = {50, 50};
		
		final double qLearningRate = 0.2;
		final double discountFactor = 0.6;
		final double epsilon = 0.05;
		
		final boolean compactData = false;
		
		
		
		
		System.out.println("Card count: " + cardCount);
		System.out.println("Train on every: " + trainOnEvery);
		System.out.println("Training set size: " + trainingSetSize);
		
		System.out.println("Total iterations: " + totalIterations);
		System.out.println("Log every: " + logEvery);
		System.out.println("Example every: " + logEvery);
		System.out.println("Neural learning rate: " + neuralLearningRate);
		System.out.println("Hidden layer sizes: " + Arrays.toString(hiddenLayerSizes));
		
		System.out.println("Q-Learning rate: " + qLearningRate);
		System.out.println("Discount factor: " + discountFactor);
		System.out.println("Epsilon: " + epsilon);
		
		System.out.println("Compact data: " + compactData);
		System.out.println();
		
		
		
		int inSize = compactData ? cardCount : (cardCount * (CardColor.values().length));
		NeuralQLearner learner = new NeuralQLearner(new NeuralQTable(inSize, cardCount, trainingSetSize, trainOnEvery, neuralLearningRate, hiddenLayerSizes), qLearningRate, discountFactor);
		
		double avg = -1;
		for (int iterations = 1; iterations <= totalIterations; iterations++) {
			boolean log = iterations % logEvery == 0;
			
			double cur = playRound(learner, cardCount, epsilon, compactData, true, iterations % exampleEvery == 0);
			if (avg < 0) avg = cur;
			else avg = avg * 0.99 + cur * 0.01;
			
			if (log) {
				NeuralNetwork network = learner.getNetwork();
				System.out.println("Training iteration " + iterations + " after " + network.getIterationCount() + " network iterations:");
				//System.out.println(Arrays.toString(network.getWeights()));
				//System.out.println(network.processTestSet());
				System.out.println("Training set error:\n" + network.getTrainingError());
				System.out.println("Linear training set error:\n" + network.getTrainingError(CostFunction.LINEAR));
				
				System.out.println();
				
				System.out.println("Average steps needed: " + avg);
				System.out.println();
			}
		}
	}
	
	
	private static double playRound(NeuralQLearner learner, int cardCount, double epsilon, boolean compactData, boolean learn, boolean log) {
		learner.resetRound();
		Card[] cards = Card.getRandom(cardCount);
		if (log) System.out.println("Cards: " + Arrays.toString(cards));
		
		int i = 1;
		while (i < 10_000) {
			if (log) System.out.println("Turn " + i);
			i++;
			int discard;
			if (Math.random() < epsilon) {
				learner.resetRound();
				discard = (int) (Math.random() * cardCount);
				if (log) System.out.println("Epsilon discard: " + discard);
			} else {
				discard = learner.getBestAction(createData(cards, compactData));
				if (log) System.out.println("Discard: " + discard);
			}
			Card[] newCards = Arrays.copyOf(cards, cards.length);
			newCards[discard] = Card.getRandom();
			if (log) System.out.println("New cards: " + Arrays.toString(newCards));
			
			double reward = CardGame.getThrowValue(newCards);
			if (log) System.out.println("Reward: " + reward);
			
			if (learn) learner.registerReward(createData(cards, compactData), discard, reward);
			
			if (log) System.out.println();
			
			if (reward >= 1) return i;
			
			cards = newCards;
		}
		return i;
	}
	
	
	private static double[] createData(Card[] hand, boolean compact) {
		return compact ? createDataCompact(hand) : createData(hand);
	}
	
	
	private static double[] createData(Card[] hand) {
		int totalColors = CardColor.values().length;
		double[] result = new double[hand.length * (totalColors)];
		int i = 0;
		for (Card c : hand) {
			result[i + c.getColor().ordinal()] = 1;
			i += totalColors;
		}
		return result;
	}
	
	
	private static double[] createDataCompact(Card[] hand) {
		double[] result = new double[hand.length];
		int i = 0;
		for (Card c : hand) {
			result[i++] = c.getColor().ordinal();
		}
		return result;
	}


}
