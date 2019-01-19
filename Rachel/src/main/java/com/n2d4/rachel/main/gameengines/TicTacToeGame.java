package com.n2d4.rachel.main.gameengines;

public class TicTacToeGame extends TileGame {

	public TicTacToeGame(int playerCount, int boardWidth, int boardHeight, int toWin) {
		super(playerCount, boardWidth, boardHeight, toWin, false);
	}

	public TicTacToeGame() {
		this(2, 3, 3, 3);
	}
	
	
	@Override
	public TurnResult turn(int x, int y) {
		return super.turn(x, y);
	}
	
	@Override
	public TurnResult turn(int[] pos) {
		return super.turn(pos);
	}

}
