
public abstract class AIPlayer {
   protected int ROWS = TTTGraphics2P.ROWS;  // number of rows
   protected int COLS = TTTGraphics2P.COLS;  // number of columns
   protected int WIN = TTTGraphics2P.WIN;
 
   protected Seed[][] cells; // the board's ROWS-by-COLS array of Cells
   protected Seed mySeed;    // computer's seed
   protected Seed oppSeed;   // opponent's seed
 
   /** Constructor with reference to game board */
   public AIPlayer(Seed[][] board) {
      cells = board;
      mySeed = Seed.NOUGHT;
      oppSeed = Seed.CROSS;
   }
 
   /** Set/change the seed used by computer and opponent */
   public void setSeed(Seed seed) {
      this.mySeed = seed;
      oppSeed = (mySeed == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
   }
 
   /** Abstract method to get next move. Return int[2] of {row, col} */
   abstract int[] move();  // to be implemented by subclasses
}