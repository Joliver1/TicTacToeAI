import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;

import javax.swing.*;
/**
 * Tic-Tac-Toe: Two-player Graphics version with Simple-OO
 * Joey Oliver, Alexander Gonzales, Joni Elshani
 */
@SuppressWarnings("serial")
public class TTTGraphics2P extends JFrame {
	//AI Player
	public AIPlayerMinimax AI;
	public int[] aiMove;
	// Named-constants for the game board
	public static int ROWS = 3;  // ROWS by COLS cells
	public static int COLS = 3;
	public static int WIN = 3;

	// Named-constants of the various dimensions used for graphics drawing
	public static final int CELL_SIZE = 100; // cell width and height (square)
	public static int CANVAS_WIDTH = CELL_SIZE * COLS;  // the drawing canvas
	public static int CANVAS_HEIGHT = CELL_SIZE * ROWS;
	public static final int GRID_WIDTH = 8;                   // Grid-line's width
	public static final int GRID_WIDHT_HALF = GRID_WIDTH / 2; // Grid-line's half-width
	// Symbols (cross/nought) are displayed inside a cell, with padding from border
	public static final int CELL_PADDING = CELL_SIZE / 6;
	public static final int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2; // width/height
	public static final int SYMBOL_STROKE_WIDTH = 8; // pen's stroke width

	public static void setWin(int x){
		WIN=x;
	}

	public static void setRows(int x){
		ROWS=x;
		CANVAS_HEIGHT=CELL_SIZE*x;
	}

	public static void setCols(int y){
		COLS=y;
		CANVAS_WIDTH=CELL_SIZE*y;
	}

	// Use an enumeration (inner class) to represent the various states of the game
	public enum GameState {
		PLAYING, DRAW, CROSS_WON, NOUGHT_WON
	}
	private GameState currentState;  // the current game state

	private Seed currentPlayer;  // the current player

	private static Seed[][] board   ; // Game board of ROWS-by-COLS cells
	private DrawCanvas canvas; // Drawing canvas (JPanel) for the game board
	private JLabel statusBar;  // Status Bar

	/** Constructor to setup the game and the GUI components */
	public TTTGraphics2P() {
		canvas = new DrawCanvas();  // Construct a drawing canvas (a JPanel)
		canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

		// The canvas (JPanel) fires a MouseEvent upon mouse-click
		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {  // mouse-clicked handler
				int mouseX = e.getX();
				int mouseY = e.getY();
				// Get the row and column clicked
				int rowSelected = mouseY / CELL_SIZE;
				int colSelected = mouseX / CELL_SIZE;

				if (currentState == GameState.PLAYING) {
					if (rowSelected >= 0 && rowSelected < ROWS && colSelected >= 0
							&& colSelected < COLS && board[rowSelected][colSelected] == Seed.EMPTY) {
						board[rowSelected][colSelected] = currentPlayer; // Make a move
						updateGame(currentPlayer, rowSelected, colSelected); // update state
						// Switch player
						currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
						if(currentState==GameState.PLAYING)
						{
							aiMove = AI.move();
							board[aiMove[0]][aiMove[1]]=currentPlayer;
							updateGame(currentPlayer, aiMove[0], aiMove[1]);
							currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
						}

					}
				} else {       // game over
					initGame(); // restart the game
				}
				// Refresh the drawing canvas
				repaint();  // Call-back paintComponent().
			}
		});

		// Setup the status bar (JLabel) to display status message
		statusBar = new JLabel("  ");
		statusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
		statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));

		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(canvas, BorderLayout.CENTER);
		cp.add(statusBar, BorderLayout.PAGE_END); // same as SOUTH

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();  // pack all the components in this JFrame
		setTitle("Tic Tac Toe");
		setVisible(true);  // show this JFrame

		board = new Seed[ROWS][COLS]; // allocate array
		initGame(); // initialize the game board contents and game variables
	}

	/** Initialize the game-board contents and the status */
	public void initGame() {
		for (int row = 0; row < ROWS; ++row) {
			for (int col = 0; col < COLS; ++col) {
				board[row][col] = Seed.EMPTY; // all cells empty
			}
		}
		currentState = GameState.PLAYING; // ready to play
		currentPlayer = Seed.CROSS;// cross plays first
		this.AI = new AIPlayerMinimax(board);
	}

	/** Update the currentState after the player with "theSeed" has placed on
       (rowSelected, colSelected). */
	public void updateGame(Seed theSeed, int rowSelected, int colSelected) {
		if (hasWon(theSeed, rowSelected, colSelected)) {  // check for win
			currentState = (theSeed == Seed.CROSS) ? GameState.CROSS_WON : GameState.NOUGHT_WON;
		} else if (isDraw()) {  // check for draw
			currentState = GameState.DRAW;
		}
		// Otherwise, no change to current state (still GameState.PLAYING).
	}

	/** Return true if it is a draw (i.e., no more empty cell) */
	public boolean isDraw() {
		for (int row = 0; row < ROWS; ++row) {
			for (int col = 0; col < COLS; ++col) {
				if (board[row][col] == Seed.EMPTY) {
					return false; // an empty cell found, not draw, exit
				}
			}
		}
		return true;  // no more empty cell, it's a draw
	}

	/** Return true if the player with "theSeed" has won after placing at
       (rowSelected, colSelected) */
	public static boolean hasWon(Seed theSeed, int rowSelected, int colSelected) {
		boolean[] wins = new boolean[WIN];

		for(int i=0; i<=COLS-WIN; i++)
		{
			for(int j = 0; j<=ROWS-WIN; j++)
			{
				//checking rows
				for(int z = 0; z<WIN; z++)
				{
					wins[z]=(board[rowSelected][i+z]==theSeed);
				}
				if(winHelper(wins)==true) return true;

				//checking cols
				for(int z = 0; z<WIN; z++)
				{
					wins[z]=(board[j+z][colSelected]==theSeed);
				}
				if(winHelper(wins)==true) return true;

				//checking diagonal to right
				for(int z = 0; z<WIN; z++)
				{
					wins[z]=(board[j+z][i+z]==theSeed);
				}
				if(winHelper(wins)==true) return true;

			}
		}

		for(int i=COLS; i>=WIN; i--)
		{
			for(int j = 0; j<=ROWS-WIN; j++)
			{
				//checking diagonal to left
				for(int z = 0; z<WIN; z++)
				{
					//System.out.println("i:"+i+"    j:"+ j+ "   z:"+z);
					wins[z]=(board[j+z][i-z-1]==theSeed);
				}
				if(winHelper(wins)==true) return true;
			}
		}

		return false;
	}

	public static boolean winHelper(boolean[] wins)
	{
		for(int i=0; i< wins.length; i++)
		{
			if (wins[i]==false) return false;
		}
		return true;
	}

	/**
	 *  Inner class DrawCanvas (extends JPanel) used for custom graphics drawing.
	 */
	class DrawCanvas extends JPanel {
		@Override
		public void paintComponent(Graphics g) {  // invoke via repaint()
			super.paintComponent(g);    // fill background
			setBackground(Color.WHITE); // set its background color

			// Draw the grid-lines
			g.setColor(Color.LIGHT_GRAY);
			for (int row = 1; row < ROWS; ++row) {
				g.fillRoundRect(0, CELL_SIZE * row - GRID_WIDHT_HALF,
						CANVAS_WIDTH-1, GRID_WIDTH, GRID_WIDTH, GRID_WIDTH);
			}
			for (int col = 1; col < COLS; ++col) {
				g.fillRoundRect(CELL_SIZE * col - GRID_WIDHT_HALF, 0,
						GRID_WIDTH, CANVAS_HEIGHT-1, GRID_WIDTH, GRID_WIDTH);
			}

			// Draw the Seeds of all the cells if they are not empty
			// Use Graphics2D which allows us to set the pen's stroke
			Graphics2D g2d = (Graphics2D)g;
			g2d.setStroke(new BasicStroke(SYMBOL_STROKE_WIDTH, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND));  // Graphics2D only
			for (int row = 0; row < ROWS; ++row) {
				for (int col = 0; col < COLS; ++col) {
					int x1 = col * CELL_SIZE + CELL_PADDING;
					int y1 = row * CELL_SIZE + CELL_PADDING;
					if (board[row][col] == Seed.CROSS) {
						g2d.setColor(Color.RED);
						int x2 = (col + 1) * CELL_SIZE - CELL_PADDING;
						int y2 = (row + 1) * CELL_SIZE - CELL_PADDING;
						g2d.drawLine(x1, y1, x2, y2);
						g2d.drawLine(x2, y1, x1, y2);
					} else if (board[row][col] == Seed.NOUGHT) {
						g2d.setColor(Color.BLUE);
						g2d.drawOval(x1, y1, SYMBOL_SIZE, SYMBOL_SIZE);
					}
				}
			}

			// Print status-bar message
			if (currentState == GameState.PLAYING) {
				statusBar.setForeground(Color.BLACK);
				if (currentPlayer == Seed.CROSS) {
					statusBar.setText("X's Turn");
				} else {
					statusBar.setText("O's Turn");
				}
			} else if (currentState == GameState.DRAW) {
				statusBar.setForeground(Color.RED);
				statusBar.setText("It's a Draw! Click to play again.");
			} else if (currentState == GameState.CROSS_WON) {
				statusBar.setForeground(Color.RED);
				statusBar.setText("'X' Won! Click to play again.");
			} else if (currentState == GameState.NOUGHT_WON) {
				statusBar.setForeground(Color.RED);
				statusBar.setText("'O' Won! Click to play again.");
			}
		}
	}


	/** The entry main() method */
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.println("Insert Board Rows");
		int x = Integer.parseInt(in.nextLine());
		System.out.println("Insert Board Columns");
		int y = Integer.parseInt(in.nextLine());
		System.out.println("Insert Winning Number(Cannot be bigger than either Rows or Columns)");
		int win = Integer.parseInt(in.nextLine());
		while(win>x || win>y)
		{
			System.out.println("Winning Number cannot exceed rows or columns, please try again.");
			win=in.nextInt();
		}
		setWin(win);
		setRows(x);
		setCols(y);
		// Run GUI codes in the Event-Dispatching thread for thread safety
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new TTTGraphics2P(); // Let the constructor do the job
			}
		});
	}
}