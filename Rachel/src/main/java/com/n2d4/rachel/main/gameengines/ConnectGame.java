package com.n2d4.rachel.main.gameengines;

public class ConnectGame extends TileGame {

	public ConnectGame(int playerCount, int boardWidth, int boardHeight, int toWin) {
		super(playerCount, boardWidth, boardHeight, toWin, true);
	}

	public ConnectGame() {
		this(2, 7, 6, 4);
	}
	
	
	
	
	public TurnResult turn(int x) {
		return super.turn(x, getBoardHeight());
	}

}
