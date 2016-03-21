package parsers;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import edu.stanford.multiagent.gamer.Game;
import gametools.OutcomeIterator;

public class PureNashEquilibriumLPParser {

	/**
	 * Creates and write the PNE LP for given game to given .lp file.
	 * @param game The game for which to create the PNE LP.
	 * @param path The path of the .lp file.
	 * @param name The name of the .lp file.
	 * @throws FileNotFoundException
	 * @post The .lp file will be located at: path + name + ".lp" and will have a UTF-8 encoding.
	 */
	
	public static void gameToPNELPfile(Game game, String path, String name) throws FileNotFoundException {
		
		try {
			PrintWriter writer = new PrintWriter(path + name + ".lp", "UTF-8"); //Create output stream.

			double beta = computeBeta(game) + 100; // +100 just to be sure.
			
			writer.println("Subject To");
			for (int player = 0; player < game.getNumPlayers(); player++) {
				OutcomeIterator outcomesExcept = new OutcomeIterator(game, player);
				
				//Expected payoff constraints (EPC).
				for (int action = 1; action <= game.getNumActions(player); action++) {
					writer.print(" epc_" + player + "/" + action + ": ");
					while (outcomesExcept.hasNext()) {
						int[] outcome = outcomesExcept.next().clone(); outcome[player] = action;
						
						double payoff = game.getPayoff(outcome, player);
						
						if (outcomesExcept.getCount() != 0 && payoff >= 0) writer.print("+");
						
						writer.print(payoff + " " + makeY(game, outcome, player) + " ");
				
					}
					
					writer.println("+ lambda_" + player + "/" + action + " - mu_" + player + " = 0");
				}
				
				
				//distribution constraint (DC).
				writer.print(" dc_" + player + ": ");
				for (int action = 1; action <= game.getNumActions(player); action++) {
					if (action != 1) writer.print(" + ");
					writer.print("x_" + player + "/" + action);
				}
				writer.println(" = 1");
				
				//beta constraint (BC).
				for (int action = 1; action <= game.getNumActions(player); action++) {
					writer.print(" bc_" + player + "/" + action + ": ");
					writer.print("lambda_" + player + "/" + action + " ");
					
					if (beta >= 0) writer.print("+");
					
					writer.println(beta + " x_" + player + "/" + action + " <= " + beta);
				}
				
				outcomesExcept.reset();
				while (outcomesExcept.hasNext()) {
					int[] outcome = outcomesExcept.next().clone();
					
					//sum constraint (SC).
					writer.print(" sum_" + player + "/" + outcomesExcept.getCount() + ": ");
					writer.print(makeY(game, outcome, player));
					
					for (int playerH = 0; playerH < game.getNumPlayers(); playerH++) {
						if (playerH == player) continue;;
						
						writer.print(" - x_" + playerH + "/" + outcome[playerH]);
					}
					
					
					writer.println(" >= " + -(game.getNumPlayers() - 2));
					
					//I don't know constraint (IDK)
					for (int playerK = 0; playerK < game.getNumPlayers(); playerK++) {
						if (playerK == player) continue;
						
						writer.print(" idk_" + player + "/" + outcomesExcept.getCount() + "/" + playerK + ": ");
						writer.print(makeY(game, outcome, player));
						writer.println(" - x_" + playerK + "/" + outcome[playerK] + " <= 0");	
					}
				}
				
				
			}
			
			writer.println("Bounds");
			for (int player = 0; player < game.getNumPlayers(); player++)
				writer.println(" mu_" + player + " free");
			
			writer.println("Binary");
			for (int player = 0; player < game.getNumPlayers(); player++)
				for (int action = 1; action <= game.getNumActions(player); action++) 
					writer.print(" x_" + player + "/" + action);
			
			writer.println();
			writer.println("End");
			writer.close();
			
			System.out.println(game.getName() + " PNE LP was succesfully written to " + path + name + ".lp");
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private static String makeY(Game game, int[] outcome, int exceptPlayer) {
		String y = "y";
		
		for (int player = 0; player < game.getNumPlayers(); player++) {
			if (player == exceptPlayer) continue;
			
			y += "_" + player + "/" + outcome[player];
		}
		
		return y;
	}
	
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
