/**
 * MinesweeperGUI.java: A class that creates a graphical user 
 * interface for playing the classic game Minesweeper.
 * 
 * Author: Joshua Xiong
 * Date: 2014-06-08
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.StringTokenizer;

import javax.imageio.*;
import javax.swing.*;

public class MinesweeperGUI {

	private Minesweeper game = new Minesweeper(9, 9, 10);
	private int difficulty = 0;
	
	private Minesweeper[] games = { new Minesweeper(9, 9, 10),
			new Minesweeper(16, 16, 40), new Minesweeper(16, 30, 99) };

	private int numCols = game.getNumCols();
	private int numRows = game.getNumRows();

	private TimePanel timePanel = new TimePanel();
	private Grid grid = new Grid();
	private JFrame frame = new JFrame("Minesweeper");
	
	
	private final int CELL_DIMENSION = 16;
	private int height = numRows * CELL_DIMENSION;
	private int width = numCols * CELL_DIMENSION;
	
	private int minesRemaining = game.getMines();
	
	private final int TIMER_DELAY = 1000;
	private Timer timer = new Timer(TIMER_DELAY, new TimerListener());
	private int currentTime = 0;
	
	private final String highScorePath = "scores.txt";

	/**
	 * Runs the game.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new MinesweeperGUI();
	}

	/* Constructor */

	/**
	 * Creates a GUI for the Minesweeper game.
	 */
	public MinesweeperGUI() {
		try {setHighScores();} catch (Exception e) {}
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		grid.repaint();
		MenuBar menubar = new MenuBar();
		JMenuBar bar = menubar.createMenuBar();
		frame.setJMenuBar(bar);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(grid, BorderLayout.SOUTH);
		panel.add(timePanel, BorderLayout.NORTH);
		frame.add(panel);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void setHighScores() throws Exception {
		File file = new File(highScorePath);
		if (!file.exists()) {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			for (int i = 0; i < 3; i++)
				out.println("999 Anonymous");
			out.close();
		}
	}
	
	public class TimerListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			if (currentTime < 999)
				currentTime++;
			timePanel.repaint();
		}
	}
	
	public class MenuBar implements ActionListener {
		
		public JMenuBar createMenuBar() {
			JMenuBar menubar = new JMenuBar();
			JMenu gameMenu = new JMenu("Game");
			menubar.add(gameMenu);
			
			ButtonGroup group = new ButtonGroup();
			
			JRadioButtonMenuItem beginner = new JRadioButtonMenuItem("Beginner");
			beginner.setName("0");
			beginner.addActionListener(this);
			beginner.setSelected(true);
			JRadioButtonMenuItem inter = new JRadioButtonMenuItem("Intermediate");
			inter.setName("1");
			inter.addActionListener(this);
			JRadioButtonMenuItem advanced = new JRadioButtonMenuItem("Advanced");
			advanced.setName("2");
			advanced.addActionListener(this);
			
			group.add(beginner);
			group.add(inter);
			group.add(advanced);
			
			gameMenu.add(beginner);
			gameMenu.add(inter);
			gameMenu.add(advanced);
			
			return menubar;
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			JMenuItem source = (JMenuItem)(event.getSource());
			int index = Integer.parseInt(source.getName());
			game = games[index];
			game.clear();
			difficulty = index;
			updateVariables();
			grid.repaint();
			timePanel.repaint();
			frame.pack();
		}
		
		private void updateVariables() {
			numCols = game.getNumCols();
			numRows = game.getNumRows();
			
			height = numRows * CELL_DIMENSION;
			width = numCols * CELL_DIMENSION;
			
			minesRemaining = game.getMines();
			
			currentTime = 0;
			
			grid.resetVariables();	
		}
	}
	
	public class TimePanel extends JPanel {

		private static final long serialVersionUID = 1L;
		
		private final BufferedImage[] digits = new BufferedImage[11];
		private final int IMAGE_WIDTH = 13;
		private final int IMAGE_HEIGHT = 23;
		
		private final int PANEL_HEIGHT = 32;
		private final int HEIGHT_OFFSET = 7;
		
		public TimePanel() {
			for (int i = 0; i < digits.length-1; i++)
				try {
					digits[i] = ImageIO.read(new File("time" + i + ".gif"));
				} catch (Exception e) {} // no Exception should be thrown
			try {
				digits[10] = ImageIO.read(new File("time-.gif"));
			} catch (Exception e) {} // no Exception should be thrown
			
		}
		
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			Image[] mineCounter = new Image[3];
			int mIndex = 0;
			int mines = minesRemaining;
			if (mines < 0) {
				mineCounter[0] = digits[10];
				mines = -mines;
				mIndex = 1;
			}
			for (; mIndex < 3; mIndex++)
				mineCounter[mIndex] = digits[getIndex(mIndex+1, mines)];
			
			for (int i = 0; i < mineCounter.length; i++) {
				g2.drawImage(mineCounter[i], i * IMAGE_WIDTH, HEIGHT_OFFSET, 
						(i + 1)	* IMAGE_WIDTH, IMAGE_HEIGHT + HEIGHT_OFFSET, 0, 0, 
						IMAGE_WIDTH, IMAGE_HEIGHT, null);
			}
			
			Image[] timeCounter = new Image[3];
			
			for (int tIndex = 0; tIndex < 3; tIndex++)
				timeCounter[tIndex] = digits[getIndex(tIndex+1, currentTime)];
			
			int startingX = width - 3 * IMAGE_WIDTH;
			
			for (int i = 0; i < timeCounter.length; i++) {
				g2.drawImage(timeCounter[i], startingX + i * IMAGE_WIDTH, HEIGHT_OFFSET, 
						startingX + (i + 1) * IMAGE_WIDTH, IMAGE_HEIGHT + HEIGHT_OFFSET, 0, 0, 
						IMAGE_WIDTH, IMAGE_HEIGHT, null);
			}
		}
		
		public int getIndex(int digit, int value) {
			return (value / (int) (Math.pow(10, 3 - digit))) % 10;
		}
		
		public Dimension getPreferredSize() {
			return new Dimension(width, PANEL_HEIGHT);
		}
	}

	/**
	 * The Grid class is the graphical implementation of the game. All changes
	 * to the game state occur here.
	 */
	public class Grid extends JPanel {

		private static final long serialVersionUID = 1L;

		private boolean hasWon = false;
		private boolean hasLost = false;

		private boolean[][] visited = new boolean[numRows][numCols];
		private boolean[][] flagged = new boolean[numRows][numCols];
		private boolean[][] shaded = new boolean[numRows][numCols];
		
		private JButton newGameButton = new JButton(" ");
		
		private boolean hasNotStarted = true;
		
		/**
		 * These global booleans are needed for compatibility of shading squares
		 * for all different types of mice.
		 */
		private boolean isRightClick = false;
		private boolean isLeftClick = false;

		private int openedCells = 0;

		/**
		 * This array holds the images for the individual cells.
		 */
		private final BufferedImage[] images = new BufferedImage[14];

		/* Constructor */

		/**
		 * Creates the Grid object, and adds the MouseListeners.
		 */
		public Grid() {
			/* MouseListeners */
			MouseListener listener = new MouseAdapter() {	
				/**
				 * When the mouse is pressed, the appropriate cells are shaded.
				 */
				public void mousePressed(MouseEvent event) {
					if (hasLost) return;
					shadeCells(event);
					repaint();
				}

				/**
				 * When the mouse is released, the appropriate cells are opened.
				 */
				public void mouseReleased(MouseEvent event) {
					if (hasLost) return;
					// no squares are shaded anymore
					shaded = new boolean[numRows][numCols];
					openCells(event);
				}
			};
			addMouseListener(listener);

			MouseMotionListener dragListener = new MouseMotionAdapter() {
				/**
				 * Shades the appropriate squares as the mouse is dragged.
				 */
				public void mouseDragged(MouseEvent event) {
					shadeCells(event);
					repaint();
				}
			};
			addMouseMotionListener(dragListener);

			/* Images */
			for (int i = 0; i < images.length; i++)
				try {
					images[i] = ImageIO.read(new File("tile_" + i + ".JPG"));
				} catch (Exception e) {}

			/* Buttons */
			ActionListener newGameListener = new NewGameListener();
			newGameButton.addActionListener(newGameListener);
			timePanel.add(newGameButton);

		}
		
		/**
		 * Opens the appropriate cells.
		 * @param event the MouseEvent associated with the press of the mouse
		 */
		private void openCells(MouseEvent event) {
			boolean both = isLeftClick && isRightClick;
			
			isLeftClick = isLeftClick && !SwingUtilities.isLeftMouseButton(event);
			isRightClick = isRightClick && !SwingUtilities.isRightMouseButton(event);
			
			boolean left = SwingUtilities.isLeftMouseButton(event);
			boolean right = SwingUtilities.isRightMouseButton(event);
			
			if (event.getX() < width && event.getX() > -1 && event.getY() < height && event.getY() > -1) {
				
				int x = event.getX() / CELL_DIMENSION;
				int y = event.getY() / CELL_DIMENSION;
				if (both) {
					if (visited[y][x]) 
						if (getFlaggedNeighbors(x, y) == game.getNeighbors()[y][x])
							for (int i = x - 1; i < x + 2; i++)
								for (int j = y - 1; j < y + 2; j++)
									openCell(i, j);
					 
				} else if (left)
					openCell(x, y);
				else if (right)
					if (minesRemaining > -99)
						flagCell(x, y);
				repaint();
			}
		}
		
		/**
		 * Shades the appropriate cells.
		 * @param event the MouseEvent associated with the release of the mouse.
		 */
		public void shadeCells(MouseEvent event) {
			if (event.getX() < width && event.getX() > -1 && event.getY() < height && event.getY() > -1) {
				isLeftClick = isLeftClick || SwingUtilities.isLeftMouseButton(event);
				isRightClick = isRightClick || SwingUtilities.isRightMouseButton(event);
				int x = event.getX() / CELL_DIMENSION;
				int y = event.getY() / CELL_DIMENSION;
				if (isLeftClick && isRightClick)
					for (int i = x - 1; i < x + 2; i++)
						for (int j = y - 1; j < y + 2; j++)
							shadeCell(i, j);
				else if (isLeftClick)
					shadeCell(x, y);
				
				repaint();
			}
		}

		/**
		 * A class for the button located at the top center of the GUI.
		 * Upon clicking the button, a new game is created.
		 */
		private class NewGameListener implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				game.clear();
				resetVariables();
				timer.stop();
				repaint();
				timePanel.repaint();
			}
		}

		/**
		 * Paints the Minesweeper game onto the JPanel. Images are used to
		 * represent each of the cells.
		 */
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			// for drawing the images, Graphics2D is needed
			Graphics2D g2 = (Graphics2D) g;

			for (int i = 0; i < numRows; i++) {
				for (int j = 0; j < numCols; j++) {
					BufferedImage img = null;
					
					// if game has not been lost, game will display as usual
					if (!hasLost) {
						if (flagged[i][j])
							img = images[12];
						else if (shaded[i][j])
							img = images[1];
						else if (!visited[i][j])
							img = images[11];
						else
							img = images[game.getNeighbors()[i][j] + 1];
					} else { // if game has been lost, opened mine(s) will be shown in red
						if (flagged[i][j])
							if (game.getCell(j, i)) img = images[12];
							else img = images[13];
						else if (!visited[i][j]) // and all other mines will be shown
							if (game.getCell(j, i)) img = images[10];
							else img = images[11];
						else
							img = images[game.getNeighbors()[i][j] + 1];
					}
					g2.drawImage(img, j * CELL_DIMENSION, i * CELL_DIMENSION,
							(j + 1) * CELL_DIMENSION, (i + 1) * CELL_DIMENSION,
							0, 0, CELL_DIMENSION, CELL_DIMENSION, null);
				}
			}
			// clears the array of shaded cells
			shaded = new boolean[numRows][numCols];

			// draws a grid for visual contrast between cells
			g.setColor(new Color(160, 160, 160));
			for (int i = 0; i <= width; i += CELL_DIMENSION)
				g.drawLine(i, 0, i, height);
			for (int i = 0; i <= height; i += CELL_DIMENSION)
				g.drawLine(0, i, width, i);
		}

		/**
		 * Opens the specified cell. If the cell that is opened has no
		 * neighboring mines, all neighboring cells are opened as well.
		 * If the cell clicked is a mine, the game will end.
		 * @param x
		 *            x-index of the cell to be opened
		 * @param y
		 *            y-index of the cell to be opened
		 * 
		 */
		private void openCell(int x, int y) {
			if (hasNotStarted) {
				game.setBoard(x, y);
				hasNotStarted = false;
				timer.start();
			}
			if (!game.isValid(x, y) || hasWon || visited[y][x] || flagged[y][x])
				return;
			visited[y][x] = true;
			if (game.getCell(x, y))
				loseGame();
			else {
				openedCells++;
				if (openedCells == game.getSquares() - game.getMines())
					winGame();
				else if (game.getNeighbors()[y][x] == 0) {
					for (int i = x - 1; i < x + 2; i++)
						for (int j = y - 1; j < y + 2; j++)
							if (i != x || j != y)
								openCell(i, j);
				}
			}
		}

		/**
		 * Updates the two-dimensional array of flagged cells, as well as the
		 * number of flags remaining.
		 * 
		 * @param x
		 *            x-coordinate of the cell to flag.
		 * @param y
		 *            y-coordinate of the cell to flag.
		 */
		private void flagCell(int x, int y) {
			if (!visited[y][x] && !hasWon) {
				if (flagged[y][x]) minesRemaining++;
				else minesRemaining--;
				flagged[y][x] = !flagged[y][x];
			}
			timePanel.repaint();
		}

		/**
		 * Disables the grid, displays and updates high scores.
		 */
		private void winGame() {
			timer.stop();
			hasWon = true;
			minesRemaining = 0;
			newGameButton.setText("W");
			for(int i = 0; i < numRows; i++)
				for (int j = 0; j < numCols; j++)
					if (game.getCell(j, i))
						flagged[i][j] = true;
			timePanel.repaint();
			repaint();
			try {
				displayHighScores();
			} catch (Exception e) {}
			
		}
		
		/**
		 * 
		 * @throws Exception will not throw as file is guaranteed to exist
		 */
		private void displayHighScores() throws Exception {
			int[] scores = new int[3];
			String[] names = new String[3];

			File file = new File(highScorePath);

			BufferedReader br = new BufferedReader(new FileReader(file));
			for (int i = 0; i < scores.length; i++) {
				StringTokenizer st = new StringTokenizer(br.readLine());
				scores[i] = Integer.parseInt(st.nextToken());
				names[i] = st.nextToken();
				while (st.hasMoreTokens()) {
					names[i] += " " + st.nextToken();
				}
			}
			br.close();

			if (currentTime < scores[difficulty]) {
				
				String input = "";
				while (input == null || input.equals("") || input.length() > 12)
					input = JOptionPane.showInputDialog(new JFrame(),
							"Input name (max 12 characters):\n", "High Score!",
							JOptionPane.PLAIN_MESSAGE);
				scores[difficulty] = currentTime;
				names[difficulty] = input;
				writeHighScores(scores, names, file);
			}
			int input = 1;
			while (input == 1) {
				String text = "Beginner:\t" + scores[0] + " seconds\t"
						+ names[0] + "\n" + "Intermediate:\t" + scores[1]
						+ " seconds\t" + names[1] + "\n" + "Advanced:\t"
						+ scores[2] + " seconds\t" + names[2];
				JTextArea textArea = new JTextArea(text);

				JFrame frame = new JFrame();
				textArea.setBackground(frame.getBackground());

				input = JOptionPane.showOptionDialog(frame, textArea,
						"High Scores", JOptionPane.DEFAULT_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, new String[] 
						{ "OK",	"Reset Scores" }, 0);
				if (input == 1) {
					scores = new int[] { 999, 999, 999 };
					names = new String[] { "Anonymous", "Anonymous", "Anonymous" };
					writeHighScores(scores, names, file);
				}
			}

		}

		private void writeHighScores(int[] scores, String[] names, File file) throws Exception {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			for (int i = 0; i < scores.length; i++)
				out.println(scores[i] + " " + names[i]);
			out.close();
		}
		
		/**
		 * 
		 */
		private void loseGame() {
			hasLost = true;
			newGameButton.setText("L");
			timer.stop();
			repaint();
			isRightClick = false;
			isLeftClick = false;
		}

		/**
		 * Updates the two-dimensional array of cells to shade.
		 * @param x x-coordinate of the cell to shade
		 * @param y y-coordinate of the cell to shade
		 */
		private void shadeCell(int x, int y) {
			if (!game.isValid(x, y) || hasWon || visited[y][x] || flagged[y][x])
				return;
			shaded[y][x] = true;
		}

		/**
		 * Determines the number of flagged neighbors of the specified cell.
		 * @param x x-coordinate of the cell
		 * @param y y-coordinate of the cell
		 * @return the number of flagged neighbors of the cell.
		 */
		private int getFlaggedNeighbors(int x, int y) {
			int total = 0;
			for (int i = x - 1; i < x + 2; i++)
				for (int j = y - 1; j < y + 2; j++)
					if (game.isValid(i, j))
						if (flagged[j][i])
							total++;
			return total;
		}
		
		
		private void resetVariables() {		
			hasNotStarted = true;
			hasWon = false;
			hasLost = false;

			visited = new boolean[numRows][numCols];
			flagged = new boolean[numRows][numCols];
			shaded = new boolean[numRows][numCols];

			openedCells = 0;
			minesRemaining = game.getMines();
			newGameButton.setText(" ");
			currentTime = 0;
			
			isRightClick = false;
			isLeftClick = false;
		}

		/**
		 * @return the preferred size of the Grid component
		 */
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(width, height);
		}

	}

}
