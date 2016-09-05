package parsers;

import java.io.PrintWriter;

import edu.stanford.multiagent.gamer.Game;
import gametools.OutcomeIterator;

public class PureNashEquilibriumOptLPParser {

	public static void gameToPNELPfile(Game game, String path, String name) {
		
		try{
			PrintWriter toFile = new PrintWriter(path + name + ".lp", "UTF-8"); //Create output stream.
			
			toFile.println("MAXIMIZE");
			toFile.println(" mu");
			
			toFile.println("SUBJECT TO");
//			for (int player = 0; player < game.getNumPlayers(); player++) {
//				toFile.print(" sol_" + player + ": ");
//				for (int action = 1; action <= game.getNumActions(player); action++) {
//					if (action != 1) toFile.print( " + ");
//					toFile.print( "x_" + player + "_" + action);
//				}
//				toFile.println(" = 1");
//			}
			
			OutcomeIterator outcomeIterator = new OutcomeIterator(game);
			for (int outcome = 0; outcomeIterator.hasNext(); outcome++) {
				int[] currentOutcome = outcomeIterator.next();
				for (int player = 0; player < game.getNumPlayers(); player++) {
					for (int action = 1; action <= game.getNumActions(player); action++) {
						int[] altOutcome = currentOutcome.clone(); altOutcome[player] = action;
						
						double regret = game.getPayoff(currentOutcome, player) - game.getPayoff(altOutcome, player);
						
						toFile.println(" lambda_o" + outcome + "_p" + player + "_a" + action + " = " + regret);
						toFile.println(" lambda_o" + outcome + "_p" + player + "_a" + action + " - lambda_o" + outcome + " >= 0");
					}
				}
				toFile.println(" lambda_o" + outcome + " - mu >= 0");
			}
			
			toFile.println("BOUNDS");
			outcomeIterator.reset();
			for (int outcome = 0; outcomeIterator.hasNext(); outcome++) {
				outcomeIterator.next();
				for (int player = 0; player < game.getNumPlayers(); player++) {
					for (int action = 1; action <= game.getNumActions(player); action++) {
						toFile.println(" lambda_o" + outcome + "_p" + player + "_a" + action + " free");
					}
				}
				toFile.println(" lambda_o" + outcome + " free");
			}
			toFile.println(" mu free");
			toFile.println("END");
			
			toFile.close();
		} catch (Exception e) {
			System.out.println("Error");
		}
	}
}
