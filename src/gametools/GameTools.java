package gametools;

import edu.stanford.multiagent.gamer.Game;

/**
 * Class containing static methods used by the game to LP parser.
 * Might also prove useful for any game relevant project.
 */
public class GameTools {

	/**
	 * Computes the number of possible outcomes for given Game.
	 * @param game The Game for which to compute the number of possible outcomes.
	 * @return The number of possible outcomes for given Game.
	 */
	public static int nbOfOutcomes(Game game) {
		return nbOfOutcomes(game.getNumActions());
	}
	
	/**
	 * Computes the number of possible outcomes for the given nbActions.
	 * @param nbActions The number of actions each player has.
	 * @return The number different possible outcomes.
	 */
	public static int nbOfOutcomes(int[] nbActions) {
		int nbOfOutcomes = 1;
		
		for (int player = 0; player < nbActions.length; player++)
			nbOfOutcomes *= nbActions[player];
		
		return nbOfOutcomes;
	}
	
	/**
	 * Computes all possible outcomes for given Game.
	 * It is essentially the Cartesian product of all strategy vectors of all players.
	 * It is important to be aware of the order of appearance of the outcomes in the returned result.
	 * The first outcome is where all players play their first action.
	 * The subsequent outcomes are where the first player plays his next actions and the other players keep the same action.
	 * When the first player has played all his actions, he returns to his first action and the next player plays his next action.
	 * The second player now plays his second action and now the first player continues by playing all his actions again.
	 * This process is continued until all outcomes have been determined.
	 * @param game The Game for which to compute all possible outcomes.
	 * @return All possible outcomes for given Game, the order of appearance is as described above.
	 */
	public static int[][] getOutcomes(Game game) {
		return getOutcomes(game.getNumActions());
	}
	
	/**
	 * Computes all possible outcomes for given Game but act as if the given player only has one action.
	 * This is to create the the Cartesian product of all the strategy vectors of all players, except the given player's strategy vector.
	 * However, we would like to have some placeholder for the excluded player so every outcome has length game.getNumPlayers().
	 * Therefore, the number of actions of this excluded player is changed to 1 and so it would be as if the excluded player plays his first action in all outcomes.
	 * More details about the order of appearance of these outcomes can be found in the description of the getOutcomes(Game game) method.
	 * @param game The Game for which to compute all possible outcomes.
	 * @param player The player to exclude from the Cartesian product.
	 * @return All possible outcomes for given Game with the given player excluded.
	 */
	public static int[][] getOutcomesExcept(Game game, int player) {
		int[] nbOfActions = game.getNumActions().clone(); nbOfActions[player] = 1;
		return getOutcomes(nbOfActions);
	}
	
	public static int[][] getOutcomes(int[] nbOfActions) {
		int 	nbOfPlayers  	= nbOfActions.length;			//Number of players in given Game.
		int 	nbOfOutcomes 	= nbOfOutcomes(nbOfActions);	//Number of outcomes in given Game.
		
		//Initialize result.
		int[][] outcomes = new int[nbOfOutcomes][nbOfPlayers];
		
		//Set first outcome to [1, 1, ..., 1].
		for (int player = 0; player < nbOfPlayers; player++)
			outcomes[0][player] = 1;
		
		for (int outcome = 1; outcome < nbOfOutcomes; outcome++) {
			//Copy previous outcome.
			outcomes[outcome] = outcomes[outcome-1].clone();
			
			//Find next outcome.
			for (int player = 0; player < nbOfPlayers; player++) {
				//The current player has reached his last action.
				//Set this player's action to 1 and continue the loop: no new outcome has yet been found.
				if (outcomes[outcome][player] == nbOfActions[player]) {
					outcomes[outcome][player] = 1;
					continue;
				}
				//The current player has not reached his last action yet.
				//Change this player's action to the next one, and break the loop: a new outcome has been found.
				else {
					outcomes[outcome][player]++;
					break;
				}
			}
		}

		return outcomes;
	}
	
	/**
	 * Compute the index of the given outcome in getOutcomes(game). The index is defined by the order of appearance of the getOutcomes(Game game) method.
	 * @param game The Game for which to compute the index of the given outcome.
	 * @param outcome The outcome for which to compute the index in given Game.
	 * @return The index of the outcome in getOutcomes(Game game).
	 */
	public static int getOutcomeIndex(Game game, int[] outcome) {
		return getOutcomeIndex(game.getNumActions(), outcome);
	}
	
	public static int getOutcomeIndex(int[] nbActions, int[] outcome) {
		//Example:
		//nbActions = [5, 6, 7]
		//outcome 	= [3, 4, 2]
		//Then the index is:
		//(3-1)*(1) + (4-1)*(5) + (2-1)*(6*5) = 47.
		
		int index = 0; //The index.
		int mult  = 1; //Variable to avoid some redundant calculations. In the example above, mult will equal, respectively, 1; 1*5; 1*5*6;
		
		for (int i = 0; i < outcome.length; i ++) {
			index += (outcome[i] - 1)*mult;
			mult  *= nbActions[i];
		}
		
		return index;
	}
}
