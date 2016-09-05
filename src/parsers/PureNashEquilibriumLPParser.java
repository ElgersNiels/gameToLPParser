package parsers;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import edu.stanford.multiagent.gamer.Game;
import gametools.OutcomeIterator;

public class PureNashEquilibriumLPParser {

	/**
	 * Creates and write the PNE LP for given game to given .lp file.
	 * Note that this process may be computationally expensive (i.e. exponential) for large games.
	 * @param game The (GAMUT) Game for which to create the PNE LP.
	 * @param path The path of the .lp file.
	 * @param name The name of the .lp file.
	 * @throws FileNotFoundException When an illegal path is given.
	 * @post The .lp file will be located at: path + name + ".lp" and will have a UTF-8 encoding.
	 * http://www.hindawi.com/journals/mpe/2014/640960/
	 */
	
	public static void gameToPNELPfile(Game game, String path, String name) {
		
		try {
			PrintWriter toFile = new PrintWriter(path + name + ".lp", "UTF-8"); //Create output stream.

			double beta = computeBeta(game) + 100; // +100 just to be sure.
			
			//Objective function.
			//We don't actually need one since we're looking for a feasible solution only.
			//A 'dummy' objective function is added, regardless.
			toFile.println("Maximize");
			toFile.println(" 0");
			
			//The constraints.
			toFile.println("Subject To");
			for (int player = 0; player < game.getNumPlayers(); player++) {
				OutcomeIterator outcomesExcept = new OutcomeIterator(game, player);
				
				//Payoff constraints (first constraint in LP from paper).
				//Forces that no player can improve his payoff by deviating.
				for (int action = 1; action <= game.getNumActions(player); action++) {
					outcomesExcept.reset();
					//Constraint name.
					toFile.print(" epc_" + player + "!" + action + ": ");
					
					//Summation part.
					while (outcomesExcept.hasNext()) {
						int[] outcome = outcomesExcept.next().clone(); outcome[player] = action;
						
						double payoff = game.getPayoff(outcome, player);
						
						if (outcomesExcept.getCount() != 0 && payoff >= 0) toFile.print("+");
						
						toFile.print(payoff + " " + makeY(game, outcome, player) + " ");
					}
					
					//Lambda, mu part.
					toFile.println("+ lambda_" + player + "!" + action + " - mu_" + player + " = 0");
				}
				
				
				//Distribution constraint (second constraint).
				//Forces a strategy to be a probability distribution.
				
				//Constraint name.
				toFile.print(" dc_" + player + ": ");
				
				//Sum of all probabilities of player...
				for (int action = 1; action <= game.getNumActions(player); action++) {
					if (action != 1) toFile.print(" + ");
					toFile.print("x_" + player + "!" + action);
				}
				//... must equal one.
				toFile.println(" = 1");
				
				//Beta constraint (third constraint).
				//Forces lambda to be zero for the action of a player in equilibrium.
				//Otherwise lambda must be smaller than beta.
				//beta is bigger or equal than the maximum payoff gain of all players.
				//This constraint forces mu (first constraint) to be equal to the payoff value of a player in equilibrium.
				for (int action = 1; action <= game.getNumActions(player); action++) {
					//Constraint name.
					toFile.print(" bc_" + player + "!" + action + ": ");
					//The constraint.
					toFile.print("lambda_" + player + "!" + action + " ");
					if (beta >= 0) toFile.print("+");
					toFile.println(beta + " x_" + player + "!" + action + " <= " + beta);
				}
				
				//Kinda hazy on what these constraints force (should look into it again).
				//Constraints involving y variable.
				outcomesExcept.reset();
				while (outcomesExcept.hasNext()) {
					int[] outcome = outcomesExcept.next().clone();
					
					toFile.print(" sum_" + player + "!" + outcomesExcept.getCount() + ": ");
					toFile.print(makeY(game, outcome, player));
					
					for (int playerH = 0; playerH < game.getNumPlayers(); playerH++) {
						if (playerH == player) continue;;
						
						toFile.print(" - x_" + playerH + "!" + outcome[playerH]);
					}
					
					
					toFile.println(" >= " + -(game.getNumPlayers() - 2));
					
					for (int playerK = 0; playerK < game.getNumPlayers(); playerK++) {
						if (playerK == player) continue;
						
						toFile.print(" idk_" + player + "!" + outcomesExcept.getCount() + "!" + playerK + ": ");
						toFile.print(makeY(game, outcome, player));
						toFile.println(" - x_" + playerK + "!" + outcome[playerK] + " <= 0");	
					}
				}
				
				
			}
			
			//Bounds.
			//µ variables are free.
			//Other variables positive (which is the case by default).
			toFile.println("Bounds");
			for (int player = 0; player < game.getNumPlayers(); player++)
				toFile.println(" mu_" + player + " free");
			
			//Binaries.
			//Actions are binary: they're either played or not (pure strategy).
			toFile.println("Binary");
			for (int player = 0; player < game.getNumPlayers(); player++)
				for (int action = 1; action <= game.getNumActions(player); action++) 
					toFile.print(" x_" + player + "!" + action);
			
			
			//Wrap up.
			toFile.println();
			toFile.println("End");
			toFile.close();
			
			System.out.println(game.getName() + " PNE LP was succesfully written to " + path + name + ".lp");
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//Create y variable name.
	private static String makeY(Game game, int[] outcome, int exceptPlayer) {
		String y = "y";
		
		for (int player = 0; player < game.getNumPlayers(); player++) {
			if (player == exceptPlayer) continue;
			
			y += "_" + player + "!" + outcome[player];
		}
		
		return y;
	}
	
	//Compute beta variable.
	private static double computeBeta(Game game) {
		OutcomeIterator outcomeIterator = new OutcomeIterator(game);
		
		double max = -Double.MAX_VALUE;
		
		for (int player = 0; player < game.getNumPlayers(); player++) {
			double maxPlayer = -Double.MAX_VALUE;
			double minPlayer = Double.MAX_VALUE;
			
			outcomeIterator.reset();
			while(outcomeIterator.hasNext()) {
				double payoff = game.getPayoff(outcomeIterator.next(), player);
				
				if (payoff > maxPlayer) maxPlayer = payoff;
				if (payoff < minPlayer) minPlayer = payoff;
			}
			
			if ((maxPlayer - minPlayer) > max) max = maxPlayer - minPlayer; 
		}
		
		return max;
	}
}
