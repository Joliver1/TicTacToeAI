import java.util.*;
/** AIPlayer using Minimax algorithm */
public class AIPlayerMinimax extends AIPlayer {

	/** Constructor with the given game board */
	public AIPlayerMinimax(Seed[][] board) {
		super(board);
	}

	/** Get next best move for computer. Return int[2] of {row, col} */
	@Override
	int[] move() {
		int[] result = minimax(2, mySeed); // depth, max turn
		return new int[] {result[1], result[2]};   // row, col
	}

	/** Recursive minimax at level of depth for either maximizing or minimizing player.
       Return int[3] of {score, row, col}  */
	private int[] minimax(int depth, Seed player) {
		// Generate possible next moves in a List of int[2] of {row, col}.
		List<int[]> nextMoves = generateMoves();

		// mySeed is maximizing; while oppSeed is minimizing
		int bestScore = (player == mySeed) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		int currentScore;
		int bestRow = -1;
		int bestCol = -1;

		if (nextMoves.isEmpty() || depth == 0) {
			// Gameover or depth reached, evaluate score
			if(ROWS==COLS)bestScore = evaluateSquare();
			else bestScore=evaluate();

		} else {
			for (int[] move : nextMoves) {
				// Try this move for the current "player"
				cells[move[0]][move[1]] = player;
				if (player == mySeed) {  // mySeed (computer) is maximizing player
					currentScore = minimax(depth - 1, oppSeed)[0];
					if (currentScore > bestScore) {
						bestScore = currentScore;
						bestRow = move[0];
						bestCol = move[1];
					}
				} else {  // oppSeed is minimizing player
					currentScore = minimax(depth - 1, mySeed)[0];
					if (currentScore < bestScore) {
						bestScore = currentScore;
						bestRow = move[0];
						bestCol = move[1];
					}
				}
				// Undo move
				cells[move[0]][move[1]] = Seed.EMPTY;
			}
		}
		return new int[] {bestScore, bestRow, bestCol};
	}

	/** Find all valid next moves.
       Return List of moves in int[2] of {row, col} or empty list if gameover */
	private List<int[]> generateMoves() {
		List<int[]> nextMoves = new ArrayList<int[]>(); // allocate List

		// Search for empty cells and add to the List
		for (int row = 0; row < ROWS; ++row) {
			for (int col = 0; col < COLS; ++col) {
				if (cells[row][col] == Seed.EMPTY) {
					nextMoves.add(new int[] {row, col});
				}
			}
		}
		return nextMoves;
	}

	private int evaluateLine(int[] row, int[] col)
	{
		int score = 0;

		for (int i = 0; i < row.length; i++)
		{
			if(score==0)
			{
				if (cells[row[i]][col[i]] == mySeed) 
				{
					score = 1;
				} else if (cells[row[i]][col[i]] == oppSeed) 
				{
					score = -1;
				}
			}else
			{
				if(score>0)
				{
					if (cells[row[i]][col[i]] == mySeed) 
					{
						score = score*11;
					} else 
					{
						if(cells[row[i]][col[i]] == oppSeed) return 0;
					}
				}else if(score<0)
				{
					if (cells[row[i]][col[i]] == oppSeed) 
					{
						score = score*10;
					} else
					{
						if(cells[row[i]][col[i]] == mySeed) return 0;
					}
				}
			}
		}		
		return score;
	}

	//called if board is a square
	private int evaluateSquare()
	{
		int score = 0;
		int[] row = new int[WIN], col = new int[WIN];

		for (int z=0; z<=COLS-WIN; z++)
		{
			for (int i=0; i<ROWS; i++)
			{
				//evaluate rows
				for(int j = 0; j<WIN; j++)
				{
					row[j]=i;
					col[j]=j+z;
				}
				score+=evaluateLine(row, col);

				//evaluate cols
				for(int j = 0; j<WIN; j++)
				{
					row[j]=j+z;
					col[j]=i;
				}
				score+=evaluateLine(row, col);
			}

			//checking top left to bot right
			for(int i=0; i<=ROWS-WIN; i++)
			{
				for(int j =0; j<WIN; j++)
				{
					row[j]=j+i;
					col[j]=j+z;
				}
				score+=evaluateLine(row,col);
			}
		}

		//checking top right to bot left
		for(int i =COLS; i>=WIN; i--)
		{
			for(int j = 0; j<=ROWS-WIN; j++)
			{
				for(int z = 0; z<WIN;z++)
				{
					row[z]=j+z;
					col[z]=i-z-1;
				}
				score+=evaluateLine(row,col);
			}
		}
		return score;
	}

	//called if board is not a square
	private int evaluate()
	{
		int score = 0;
		int[] row = new int[WIN], col = new int[WIN];
		int smallSide=ROWS;
		if(ROWS>COLS) smallSide=COLS;

		//evaluating rows
		for (int z=0; z<=COLS-WIN; z++)
		{
			for (int i=0; i<ROWS; i++)
			{
				for(int j = 0; j<WIN; j++)
				{
					row[j]=i;
					col[j]=j+z;
				}
				score+=evaluateLine(row, col);
			}
		}

		//evaluate cols
		for (int z=0; z<=ROWS-WIN; z++)
		{
			for (int i=0; i<COLS; i++)
			{
				for(int j = 0; j<WIN; j++)
				{
					row[j]=j+z;
					col[j]=i;
				}
				score+=evaluateLine(row, col);
			}
		}

		//checking top left to bot right
		for(int c = 0; c<=COLS-WIN;c++)
		{
			for(int z = 0; (z<= smallSide-WIN); z++)
			{
				for(int j =0; j<WIN; j++)
				{
					row[j]=j+z;
					col[j]=j+c;
				}
				score+=evaluateLine(row,col);
			}
		}

		//checking top right to bot left
		for(int i =COLS; i>=WIN; i--)
		{
			for(int j = 0; j<=ROWS-WIN; j++)
			{
				for(int z = 0; z<WIN;z++)
				{
					row[z]=j+z;
					col[z]=i-z-1;
				}
				score+=evaluateLine(row,col);
			}
		}

		return score;
	}

}