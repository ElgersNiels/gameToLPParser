package gametools;

import java.util.Iterator;

import edu.stanford.multiagent.gamer.Game;

/**
 * Class implementing Iterator that iterates over all possible outcomes in a give Game.
 */
public class OutcomeIterator implements Iterator<int[]> {
	private int count;	//Number of currentOutcome. 0-based.
	
	private int nbOfPlayers;	//Number of players of the Game.
	private int[] nbOfActions;	//Number of actions for each player of the Game.
	
	private int[] currentOutcome;	//Current outcome.
	
	/**
	 * Constructor 
	 * @param game Game to construct OutcomeIterator for.
	 */
	public OutcomeIterator(Game game) {
		this.nbOfPlayers = game.getNumPlayers();
		this.nbOfActions = game.getNumActions();
		
		for (int player = 0; player < game.getNumPlayers(); player++) currentOutcome[player] = 1;
		
		count = 0;
	}
	
	/**
	 * Constructor. Act as if the given player only has one action.
	 * @param game Game to construct OutcomeIterator for.
	 * @param player player for which to act as if (s)he only had one action.
	 */
	public OutcomeIterator(Game game, int player) {
		this(game);
		
		this.nbOfActions[player] = 1;
		
	}

	@Override
	public boolean hasNext() {
		for (int player = 0; player < nbOfPlayers; player++)
			if (currentOutcome[player] < nbOfActions[player]) return true;
		
		return false;
	}

	@Override
	public int[] next() {
		for (int player = 0; player < nbOfPlayers; player++) {
			if (currentOutcome[player] == nbOfActions[player]) {
				currentOutcome[player] = 1;
				continue;
			}
			else {
				currentOutcome[player]++;
				break;
			}
		}
		
		count++;
		return currentOutcome;
	}
	
	public int getCount() {
		return count;
	}
	
}
