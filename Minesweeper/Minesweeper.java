/**
 * Minesweeper.java: A class that implements the rules of the classic
 * game Minesweeper.
 * 
 * 
 * In order to prevent the board from becoming too dense with mines, we restrict the
 * number of mines to be at most one-third of the total number of squares. If a player
 * asks for more mines, 
 * 
 * Author: Joshua Xiong
 * Version: 1.0
 * Date: 2013-11-24
 * Period: 6
 */

import java.util.*;

public class Minesweeper {

	private Board board;
	private int rows;
	private int cols;
	private final int squares;
	private int mines;
	private final int[][] neighbors;

	/**
	 * Creates a Minesweeper object that holds data related to the state of the
	 * game. As in the original version of Minesweeper
	 * 
	 * @param row
	 *            number of rows of the Board
	 * @param col
	 *            number of columns of the Board
	 * @param mines
	 *            number of mines to play the game with
	 */
	public Minesweeper(int row, int col, int mines) {
		this.rows = row;
		this.cols = col;
		this.squares = rows * cols;
		this.mines = Math.min(mines, (rows - 1) * (cols - 1));
		this.board = new Board(rows, cols);
		this.neighbors = new int[rows][cols];
	}

	/**
	 * 
	 * @param x
	 *            x-index of cell
	 * @param y
	 *            y-index of cell
	 * @return whether a certain set of indices is located within the Board
	 */
	public boolean isValid(int x, int y) {
		return board.isValidCell(y, x);
	}

	/**
	 * 
	 * @param row
	 *            Row index of cell
	 * @param col
	 *            Column index of cell
	 * @return the total number of mines that a cell borders
	 */
	public int getOccupiedNeighbors(int row, int col) {
		int neighbors = 0;
		for (int i = row - 1; i < row + 2; i++)
			for (int j = col - 1; j < col + 2; j++)
				if (board.isValidCell(i, j))
					if (board.getCell(i, j))
						neighbors++;
		if (board.getCell(row, col))
			neighbors--;
		return neighbors;
	}

	/**
	 * 
	 * @param x
	 *            x-index of cell
	 * @param y
	 *            y-index of cell
	 * @return the boolean that is contained in the specified cell
	 */
	public boolean getCell(int x, int y) {
		return this.board.getCell(y, x);
	}

	/*
	 * Parameters: None Returns: Void Description: Sets all of the cells in the
	 * Board to 0 (i.e. all cells are dead), and resets the generation to 0.
	 */
	public void clear() {
		this.board.clear();
	}

	/**
	 * Fills the Board with mines such that the cell that is described by the
	 * parameters is guaranteed to not be a mine.
	 * 
	 * @param x
	 *            the x-index of the cell
	 * @param y
	 *            the y-index of the cell
	 */
	public void setBoard(int x, int y) {
		int index = y * cols + x;
		setBoard(getMineLocs(index));
		initializeNeighbors();
	}

	/**
	 * 
	 * @param indexToSkip
	 * @return a HashSet of the locations to place the mines.
	 */
	private HashSet<Integer> getMineLocs(int indexToSkip) {
		HashSet<Integer> hashset = new HashSet<Integer>();
		while (hashset.size() < mines) {
			int index = (int) (Math.random() * squares);
			if (index != indexToSkip)
				hashset.add(index);
		}
		return hashset;
	}

	/**
	 * 
	 * @param h
	 */
	private void setBoard(HashSet<Integer> h) {
		Iterator<Integer> iterator = h.iterator();
		while (iterator.hasNext()) {
			int index = iterator.next();
			setMine(index);
		}
	}

	private void setMine(int index) {
		int yIndex = index / cols;
		int xIndex = index % cols;
		board.setCell(yIndex, xIndex, true);
	}

	private void initializeNeighbors() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (!board.getCell(i, j))
					neighbors[i][j] = getOccupiedNeighbors(i, j);
				else
					neighbors[i][j] = -1;
			}
		}
	}

	/**
	 * @return a String representation of the game. The Board is printed as in
	 *         the Board class. The only modification is that the number of
	 *         mines is shown below.
	 */
	public String toString() {
		return this.board.toString() + "\nMines: " + mines;
	}

	/* Accessors */

	/**
	 * 
	 * @return the number of rows
	 */
	public int getNumRows() {
		return this.board.getNumRows();
	}

	/**
	 * 
	 * @return the number of columns
	 */
	public int getNumCols() {
		return this.board.getNumCols();
	}

	/**
	 * 
	 * @return the number of squares
	 */
	public int getSquares() {
		return this.squares;
	}

	/**
	 * 
	 * @return the number of mines
	 */
	public int getMines() {
		return this.mines;
	}

	/**
	 * 
	 * @return the grid of neighbors
	 */
	public int[][] getNeighbors() {
		return this.neighbors;
	}

	public static void main(String[] args) {
		Minesweeper m = new Minesweeper(2, 3, 5);
		m.setBoard(1, 1);
		System.out.println(m);
	}
}
