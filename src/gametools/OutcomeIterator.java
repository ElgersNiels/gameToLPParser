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
	private int nbOfOutcomes;
	private int[] currentOutcome;	//Current outcome.
	
	/**
	 * Constructor 
	 * @param game Game to construct OutcomeIterator for.
	 */
	public OutcomeIterator(Game game) {
		this.nbOfPlayers = game.getNumPlayers();
		this.nbOfActions = game.getNumActions().clone();
		
		this.currentOutcome = new int[nbOfPlayers]; for (int player = 0; player < nbOfPlayers; player++) currentOutcome[player] = 1;
		
		this.nbOfOutcomes = GameTools.nbOfOutcomes(nbOfActions);
		this.count = 0;
	}
	
	/**
	 * Constructor. Act as if the given player only has one action.
	 * @param game Game to construct OutcomeIterator for.
	 * @param player player for which to act as if (s)he only had one action.
	 */
	public OutcomeIterator(Game game, int player) {
		this(game);
		
		this.nbOfActions[player] = 1;
		this.nbOfOutcomes /= game.getNumActions(player);
		
	}

	@Override
	public boolean hasNext() {
		return count < nbOfOutcomes;
	}

	@Override
	public int[] next() {
		int[] toReturn = currentOutcome.clone();
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
		return toReturn;
	}
	
	public void reset() {
		count = 0;
		for (int player = 0; player < nbOfPlayers; player++) currentOutcome[player] = 1;
	}
	
	public int getCount() {
		return count;
	}
	
}
