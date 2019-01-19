package com.n2d4.rachel.main;

import java.util.Arrays;

import org.nd4j.linalg.factory.Nd4j;

import com.n2d4.rachel.learning.ActivationFunction;
import com.n2d4.rachel.learning.CostFunction;
import com.n2d4.rachel.learning.neuralnetwork.NeuralNetwork;
import com.n2d4.rachel.main.gameengines.TicTacToeGame;
import com.n2d4.rachel.main.gameengines.TileGame.TurnResult;
import com.n2d4.rachel.util.Requirements;
import com.n2d4.rachel.util.Util;
import com.n2d4.rachel.vectorization.ContinuousDataSet;
import com.n2d4.rachel.vectorization.InputSet;
import com.n2d4.rachel.vectorization.OutputSet;
import com.n2d4.rachel.vectorization.TestSet;
import com.n2d4.rachel.vectorization.TrainingSet;
import com.n2d4.rachel.vectorization.ValidationSet;
import com.n2d4.rachel.vectorization.VectorizedData;

/**
 * Tiles is a game with an unknown amount of tiles. The agent may then choose one of a bunch of known
 * tiles which they'll then mark. The agent does not know how to win or lose. The agent does know
 * however that choosing a tile will mark it, and it also knows what tiles it can choose and what
 * not.
 * 
 * Tic Tac Toe (aka. noughts and crosses or X and Os) is a special 3x3 Tile game for 2 players. To
 * win, one must mark three tiles in a vertical, horizontal or diagonal line.
 *
 */
public class RachelTicTacToe {
	
	protected static final TilesDataSet data = new TilesDataSet(9 * 2);
	protected static final NeuralNetwork network = new NeuralNetwork(CostFunction.HALF_SQUARED, ActivationFunction.ANALYTIC, data, 0.05, new int[] {50, 50});
	
	private static double[] lastInput;
	
	public static void main(String[] args) throws InterruptedException {
		lognet();
		
		
		
		long totalIterations = 10_000;
		long logEvery = 1000;
		long testIterations = 1000;
		
		
		for (int i = 0; i < totalIterations; i++) {
			playRound(-1, false, true);
			
			
			
			if (i % logEvery == logEvery - 1) {
				lognet();
				System.out.println("After " + (i + 1) + " training iterations, starting " + testIterations + " test iterations:");
				int wins = 0;
				int draws = 0;
				int losses = 0;
				for (int j = 0; j < testIterations; j++) {
					int nwinner = playRound(1, false, false);
					switch(nwinner) {
					case -1:
						losses++;
						break;
					case 0:
						draws++;
						break;
					case 1:
						wins++;
						break;
					}
				}
				System.out.println("Wins: " + wins + ", " + String.format("%.2f", 100 * wins / (double) testIterations) + "%");
				System.out.println("Draws: " + draws + ", " + String.format("%.2f", 100 * draws / (double) testIterations) + "%");
				System.out.println("Losses: " + losses + ", " + String.format("%.2f", 100 * losses / (double) testIterations) + "%");
				System.out.println();
			}
			
		}
		
		System.out.println("Playing some example games in 5 seconds...");
		Thread.sleep(5_000);
		
		for (int i = 0; i < 15; i++) {
			System.out.println("New game! Go player 2!");
			playRound(1, true, false);
			System.out.println();
			System.out.println();
		}
	}
	
	private static void lognet() {
		OutputSet out = network.processTestSet();
		System.out.println("=== Neural Network After Iteration " + network.getIterationCount() + " ===");
		System.out.println(network);
		System.out.println(network.getDataSet().getTestSet());
		System.out.println(out);
		System.out.println("Difference:\n" + out.getDifference(network.getDataSet().getTestSet().getOutputSet()));
		System.out.println("Training set error:\n" + network.getTrainingError());
		System.out.println("Validation set error:\n" + network.getValidationError());
		System.out.println("Test set error:\n" + network.getError());
		System.out.println("Linear test set error:\n" + network.getError(CostFunction.LINEAR));
		System.out.println();
	}
	
	
	public static int playRound(int botPlayer, boolean log, boolean train) {
		TicTacToeGame game = new TicTacToeGame();
		
		while (true) {
			lastInput = null;
			int[] tile;
			if (botPlayer < 0 || game.getCurrentPlayer() == botPlayer) {
				tile = findTrainingBest(game);
			} else {
				tile = findRandom(game);
			}
			
			TurnResult turn = game.turn(tile);
			if (log) System.out.print(game.getBoardState());
			int reward = 0;
			switch (turn.getType()) {
			case VALID:
				// Everything fine! Go on!
				break;
			case DRAW:
				if (log) System.out.println("The game has ended in a draw!");
				if (train) registerReward(lastInput, createInput(game), -0.05);
				return 0;
			case VICTORY:
				if (log) System.out.println("Player " + (turn.getPlayer() + 1) + " is the winner!");
					if (train) registerReward(lastInput, createInput(game), 1);
					return (botPlayer < 0 || turn.getPlayer() == botPlayer) ? 1 : -1;
			case GAME_ALREADY_ENDED:
				throw new RuntimeException("The game has already ended!");
			case TILE_OCCUPIED:
				throw new RuntimeException("This tile is already occupied!");
			}
			if (train) registerReward(lastInput, createInput(game), reward);
			
			if (log) System.out.println();
		}
	}
	
	
	
	
	public static int[] findTrainingBest(TicTacToeGame game) {
		Requirements.nonNull(game, "game");
		final double epsilon = 0.1;		// chance to ignore all more-or-less intelligent advice and go full SMOrc
		// This is good for training to simulate some irregularities here and then. This should never trigger in the final AI, however.
		
		if (Util.getRandom().nextDouble() >= epsilon) return findBest(game);
		
		return findRandom(game);
	}
	
	public static int[] findRandom(TicTacToeGame game) {
		int[][] all = game.getAvailableTiles();
		return all[Util.getRandom().nextInt(all.length)];
	}
	
	public static int[] findBest(TicTacToeGame game) {
		Requirements.nonNull(game, "game");
		int[][] all = game.getAvailableTiles();
		int[] boardSize = game.getBoardSize();
		
		int[] curBest = null;
		double curBestRating = 0;
		double[] input = lastInput = createInput(game);
		for (int[] cur : all) {
			double[] ninput = Arrays.copyOf(input, input.length);
			int ipos = Util.toFlatInt(cur, boardSize) * game.getPlayerCount();
			ninput[ipos] = 1;
			double rating = rate(ninput);
			if (curBest == null || rating > curBestRating) {
				curBest = cur;
				curBestRating = rating;
			}
		}
		
		return curBest;
	}
	
	
	
	protected static double[] createInput(TicTacToeGame game) {
		int playerCount = game.getPlayerCount();
		int curPlayer = game.getCurrentPlayer();
		double[] result = new double[game.getTileCount() * playerCount];
		int[] max = game.getBoardSize();
		int[] cur = Util.elementWiseLoopInit(max.length);
		while (Util.elementWiseIncrement(cur, max)) {
			int t = game.getTile(cur);
			for (int i = 0; i < playerCount; i++) {
				int p = (curPlayer + i) % playerCount;
				int ipos = Util.toFlatInt(cur, max) * playerCount + i;
				result[ipos] = (p == t ? 1 : 0);
			}
		}
		
		return result;
	}
	
	
	protected static double rate(double[] input) {
		double reward = VectorizedData.getINDArray(network.process(new InputSet(input))).getDouble(0);
		return reward;
	}
	
	protected static void registerReward(double[] lastInput, double[] nextBoard, double reward) {
		if (lastInput == null) return;
		
		reward += rate(nextBoard);
		data.add(lastInput, new double[] {reward});
		network.train();
		lognet();
	}
	
	
	
	
	
	private static class TilesDataSet implements ContinuousDataSet {
		
		private static final int setSize = 1;
		
		private final int inputSize;
		private final InputSet inputSet;
		private final OutputSet outputSet;
		private int counter = 0;
		
		public TilesDataSet(int inputSize) {
			this.inputSize = inputSize;
			
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
