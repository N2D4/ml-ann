package com.n2d4.rachel.main.gameengines;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.n2d4.rachel.util.Requirements;
import com.n2d4.rachel.util.Util;

public abstract class TileGame {
	
	private final int[][] board;
	private final int toWin;
	private final boolean gravity;
	private final int playerCount;
	private int curPlayer;
	private boolean gameEnded = false;

	public TileGame(int playerCount, int boardWidth, int boardHeight, int toWin, boolean gravity) {
		Requirements.positive(playerCount, "player count");
		Requirements.positive(boardWidth, "board width");
		Requirements.positive(boardHeight, "board height");
		Requirements.positive(toWin, "to win");
		
		this.board = new int[boardWidth][boardHeight];
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				board[i][j] = -1;
			}
		}
		this.playerCount = playerCount;
		this.toWin = toWin;
		this.gravity = gravity;
	}
	
	
	public int getPlayerCount() {
		return playerCount;
	}
	
	public int getCurrentPlayer() {
		return curPlayer;
	}
	
	public int getToWin() {
		return toWin;
	}
	
	public boolean hasGravity() {
		return gravity;
	}
	
	public int[][] getBoard() {
		int[][] result = new int[getBoardWidth()][getBoardHeight()];
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[i].length; j++) {
				result[i][j] = board[i][j];
			}
		}
		return result;
	}
	
	public int getTile(int x, int y) {
		return board[x][y];
	}
	
	public int getTile(int[] pos) {
		Requirements.equal(pos.length, 2, "position array length");
		
		return getTile(pos[0], pos[1]);
	}
	
	public int[][] getAvailableTiles() {
		List<int[]> result = new ArrayList<>();
		int[] max = getBoardSize();
		int[] cur = Util.elementWiseLoopInit(max.length);
		while (Util.elementWiseIncrement(cur, max)) {
			if (getTile(cur) < 0) result.add(Arrays.copyOf(cur, cur.length));
		}
		return result.toArray(new int[0][]);
	}
	
	public void setTile(int x, int y, int player) {
		if (hasGravity()) {
			int height = getBoardHeight();
			do {
				if (getTile(x, y + 1) >= 0) break; 
			} while (++y < height);
		}
		
		board[x][y] = player;
	}
	
	public void setTile(int[] pos, int player) {
		Requirements.equal(pos.length, 2, "position array length");
		
		setTile(pos[0], pos[1], player);
	}
	
	public void resetTile(int x, int y) {
		board[x][y] = -1;
	}
	
	public int[] getBoardSize() {
		return new int[] {getBoardWidth(), getBoardHeight()};
	}
	
	public int getBoardWidth() {
		return board.length;
	}
	
	public int getBoardHeight() {
		return board[0].length;
	}
	
	public int getTileCount() {
		int result = 1;
		for (int size : getBoardSize()) {
			result *= size;
		}
		return result;
	}
	
	public BoardState getBoardState() {
		return new BoardState();
	}
	
	public boolean hasEnded() {
		return gameEnded;
	}
	
	
	
	protected TurnResult turn(int x, int y) {
		return this.turn(new int[] {x, y});
	}
	
	
	protected TurnResult turn(int[] pos) {
		Requirements.nonNull(pos, "pos");
		for (int i = 0; i < pos.length; i++) {
			String name = "position[" + i + "]";
			Requirements.nonNegative(pos[i], name);
			Requirements.smallerThan(pos[i], getBoardSize()[i], name);
		}
		
		
		TurnResultType type;
		if (hasEnded()) {
			type = TurnResultType.GAME_ALREADY_ENDED;
		} else if (getTile(pos) >= 0) {
			type = TurnResultType.TILE_OCCUPIED;
		} else {
			setTile(pos, curPlayer);
			Integer winner = getWinner();
			if (winner == null) {
				type = TurnResultType.VALID;
				if (++curPlayer >= playerCount) curPlayer = 0;
			} else {
				gameEnded = true;
				if (winner < 0) {
					type = TurnResultType.DRAW;
				} else {
					type = TurnResultType.VICTORY;
					curPlayer = winner;
				}
			}
		}
		
		
		
		return new TurnResult(type, curPlayer);
	}
	
	
	/**
	 * null for no winner, negative for draw, non-negative for the winner.
	 * 
	 * Could be optimized like hell, but this ain't a contest entry or something
	 */
	public Integer getWinner() {
		final int[][] dims = new int[][] {{1, 0}, {0, 1}, {1, 1}, {-1, 1}};		// directions to check
		
		int size[] = getBoardSize();
		int cur[] = Util.elementWiseLoopInit(size.length);
		int toWin = getToWin();
		
		outer: while (Util.elementWiseIncrement(cur, size)) {
			int tile = getTile(cur);
			if (tile < 0) continue;
			boolean[] succ = new boolean[dims.length];
			int tot = succ.length;
			for (int i = 0; i < succ.length; i++) {
				succ[i] = true;
				for (int j = 0; j < dims[i].length; j++) {
					int max = cur[j] + (toWin - 1) * dims[i][j];
					if (max < 0 || max >= size[j]) {
						succ[i] = false;
						tot--;
						break;
					}
				}
			}
			if (tot <= 0) continue outer;
			for (int i = 0; i < toWin; i++) {
				for (int j = 0; j < succ.length; j++) {
					if (!succ[j]) continue;
					int[] newCoords = new int[dims[j].length];
					for (int k = 0; k < newCoords.length; k++) {
						newCoords[k] = cur[k] + i * dims[j][k];
					}
					if (getTile(newCoords) != tile) {succ[j] = false; tot--;}
				}
				if (tot <= 0) continue outer;
			}
			return tile;
		}
		
		while (Util.elementWiseIncrement(cur, size)) {
			if (getTile(cur) < 0) return null;
		}
		return -1;
	}
	
	
	
	
	
	
	
	
	
	
	public class BoardState implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private final int[][] board = TileGame.this.getBoard();
		private final int currentPlayer = curPlayer;
		
		public int[][] getBoard() {
			return board;
		}
		
		public int getCurrentPlayer() {
			return currentPlayer;
		}
		
		public TileGame getGame() {
			return TileGame.this;
		}
		
		@Override public String toString() {
			StringBuilder builder = new StringBuilder(this.getClass().toString() + "\n");
			builder.append((hasEnded() ? "Last player" : "Current player") + ": " + getPlayerString(currentPlayer) + " (" + (currentPlayer + 1) + "/" + playerCount + ")\n");
			for (int j = 0; j < getBoardHeight(); j++) {
				if (j > 0) {
					for (int k = 0; k < getBoardWidth() - 1; k++) {
						builder.append("---+");
					}
					builder.append("---\n");
				}
				for (int i = 0; i < getBoardWidth() - 1; i++) {
					builder.append(posToString(i, j) + "|");
				}
				builder.append(posToString(getBoardWidth() - 1, j) + "\n");
			}
			return builder.toString();
		}
		
		private String posToString(int x, int y) {
			int p = getBoard()[x][y];
			if (p >= 0) return " " + getPlayerString(p) + " ";
			int n = y * getBoardWidth() + x + 1;
			if (n < 10) return " " + n + " ";
			if (n < 100) return " " + n;
			if (n < 1000) return Integer.toString(n);
			return "...";
		}
		
		private String getPlayerString(int player) {
			if (getPlayerCount() == 2) return player == 0 ? "X" : "O";
			return player >= 26 ? "\u25CF" : Character.toString((char) (65 + player));
		}
	}
	
	
	public static class TurnResult {
		private TurnResultType type;
		private int player;
		
		TurnResult(TurnResultType type, int player) {
			this.type = type;
			this.player = player;
		}
		
		public TurnResultType getType() {
			return type;
		}
		
		public int getPlayer() {
			return player;
		}
		
	}
	
	
	public static enum TurnResultType {
		VALID, GAME_ALREADY_ENDED, DRAW, VICTORY, TILE_OCCUPIED
	}

}
