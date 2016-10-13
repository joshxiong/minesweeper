/*
 * Board.java: Creates a game board. Boards can be created based on a
 * two-dimensional array or the length and width dimensions. We define
 * a "cell" to be a particular spot on the board with a unique row and
 * column number, and is thus associated with an ordered pair of indices
 * in the form (rowIndex, columnIndex).
 * 
 * This board may be used for a variety of games, under the condition that
 * the gameplay of said game is based on a two-dimensional grid, and that
 * each spot on the board has a finite amount of states (less than 2^32).
 * 
 * The "pieces" of the board are represented by number; each piece has a 
 * unique number. The number 0, however, signifies an empty cell (one that
 * does not contain a piece).
 * 
 * Utility methods include checking if the board contains a certain cell, 
 * checking if a cell is occupied, setting a cell to a certain number,
 * and clearing the board.
 * 
 * Author: Joshua Xiong
 * Version: 1.0
 * Date: 2013-11-24
 * Period: 6
 */

public class Board {
	boolean[][] grid;
	private int numCols;
	private int numRows;
	
	/* Constructors */
	
	/**
	 * Creates an empty Board with the specified number of rows and columns
	 * @param row Number of rows of the Board
	 * @param col Number of columns of the Board
	 */
	public Board(int row, int col){
		this.grid = new boolean[row][col];
		this.numRows = row;
		this.numCols = col;
	}
	
	/* Methods */
	
	/**
	 * Checks to see if a cell is contained within the Board based on the indices
	 * @param row
	 * @param col
	 * @return Whether the cell is contained within the Board, as a boolean
	 */
	public boolean isValidCell(int row, int col){
		return (row < numRows && row > -1 &&
				col < numCols && col > -1);
	}
	
	/**
	 * Sets all of the cells on the Board to false, effective ly clearing the Board
	 */
	public void clear(){
		grid = new boolean[numRows][numCols];
	}
	
	/**
	 * Returns the value that is located within a cell. If the cell is out of bounds,
	 * false is returned.
	 * @param row Row index of cell
	 * @param col Column index of cell
	 * @return The boolean value of the cell.
	 */
	public boolean getCell(int row, int col){
		return this.isValidCell(row, col) && grid[row][col];
	}
	
	/**
	 * Changes the cell of the Board to a specified value. If the cell is out of bounds,
	 * nothing happnes, and false is returned.
	 * @param row Row index of cell
	 * @param col Column index of cell
	 * @param newValue Boolean to set the cell equal to
	 * @return The old value of the cell, unless it is out of bounds;
	 * false is returned in this case
	 */
	public boolean setCell(int row, int col, boolean newValue){
		if (this.isValidCell(row, col)){
			boolean oldValue = getCell(row,col);
			grid[row][col] = newValue;
			return oldValue;
		}
		return false;
	}
	
	/**
	 * @return A String representation of the Board
	 */
	public String toString(){
		String array = "     0  ";
		
		// top indices
		for (int i = 1; i < numCols; i++){
			array += i;
			for (int j = 0; j < 2 - ((int) Math.log10(i)); j++)
				array += " ";
		}
		
		// top horizontal line
		array += "\n" + "   ——";
		for (int i = 0; i < numCols; i++)
			array += "———";
		array += "\n";
		
		// rows
		for (int row = 0; row < numRows; row++){
			array += row;
			if (row == 0)
				array += "  ";
			for (int j = 1; j < 3-((int) Math.log10(row)); j++)
				array += " ";
			array+= "| ";
			for (int col = 0; col < numCols; col++) {
				String str;
				if (grid[row][col]) str = "*";
				else str = "O";
				array += str + ", ";
			}
			array += "\n";
		}
		return array;
	}
	
	public int getNumRows(){return this.numRows;}
	public int getNumCols(){return this.numCols;}
	
}
