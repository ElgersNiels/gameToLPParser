package parsers;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import edu.stanford.multiagent.gamer.Game;
import gametools.GameTools;

public class OptimalCorrelatedEquilibriumLPParser {

	/**
	 * Creates and write the OCE LP for given game to given .lp file.
	 * @param game The Game for which to create the OCE LP.
	 * @param path The path (folder) in which the .lp file is to be located.
	 * @param name The name of the lp file, .lp extension will be added automatically.
	 * @throws FileNotFoundException when the given path + name combination is invalid.
	 * @post The .lp file will be located at: path + name + ".lp" and will have a UTF-8 encoding.
	 */
	public static void gameToOCELPfile(Game game, String path, String name) throws FileNotFoundException {

		try {
			PrintWriter writer = new PrintWriter(path + name + ".lp", "UTF-8"); //Create output stream.
			
			int		nbOfPlayers	 = game.getNumPlayers();		 //Get number of players in given Game.
			int		nbOfOutcomes = GameTools.nbOfOutcomes(game); //Compute number of outcomes in given Game.
			int[]	nbOfActions	 = game.getNumActions().clone(); //Get the number of actions of each player.
			int[][] outcomes	 = GameTools.getOutcomes(game);  //Compute all outcomes in given Game.
			
			//**COMMENTS**
			
			//In .lp files, lines starting with \ are to be ignored.
			writer.println("\\This LP represents the problem of finding an optimal correlated equilibria.");
			writer.println("\\The game in this instance is: " + game.getName() + ".\n");
			
			writer.println("\\Number of players: " + game.getNumPlayers());
			writer.print("\\Respective number of actions for each player:"); for (int actions : nbOfActions) writer.print(" " + actions); writer.println();
			writer.println("\\The number of outcomes: " + nbOfOutcomes + "\n");
			
			//**OBJECTIVE FUNCTION**
			writer.println("MAXIMIZE");
			writer.print(" obj:");
			
			//The coefficient of a outcome variable is the linear combination of the payoff values of all players for that outcome.
			
			//For all outcome variables.
			for (int outcome = 0; outcome < nbOfOutcomes; outcome++) {
				double coeff = 0;
				//Make linear combination of payoff values of all players.
				for (int player = 0; player < nbOfPlayers; player++)
					coeff += game.getPayoff(outcomes[outcome], player);
				
				//Write new found coefficient to .lp file
				
				writer.print(" ");
				//If this isn't the first coefficient and the coefficient is positive, write a plus.
				//A minus is automatically written when the coefficient is negative.
				if (coeff >= 0 && outcome > 0) writer.print("+ ");
				//Write the name of the coefficient.
				writer.print(coeff + " x" + outcome);
			}
			writer.println();
			
			
			//**CONSTRAINTS**
			writer.println("SUBJECT TO");
			
			////CE CONSTRAINTS
			// Players would not want to deviate from the given outcome.
			// https://en.wikipedia.org/wiki/Correlated_equilibrium
			
			// \forall p \in P, \forall i,j \in S_p : sum_{s \in S_{-p}} (u_p(i,s) - u_p(j,s))x_{is} >= 0
			
			//\forall p \in P.
			for (int player = 0; player < nbOfPlayers; player++) {
				
				// S_{-p}
				int[][] outcomesExcept = GameTools.getOutcomesExcept(game, player); 
				
				// \forall i \in S_p
				for (int actionI = 1; actionI <= nbOfActions[player]; actionI++) {
					
					// \forall j \in S_p
					for (int actionJ = 1; actionJ <= nbOfActions[player]; actionJ++) {
						if (actionI == actionJ) continue; //The coefficients would evaluate to zero in this case, leaving a trivial constraint.
						
						writer.print(" " + (player+1) + "_" + actionI + "_" + actionJ + ":");
						
						// sum_{s \in S_{-p}}
						for (int outcomeExcept = 0; outcomeExcept < outcomesExcept.length; outcomeExcept++) {
							double coeff = 0;
							
							int[] outcomeI = outcomesExcept[outcomeExcept].clone(); outcomeI[player] = actionI;
							int[] outcomeJ = outcomesExcept[outcomeExcept].clone(); outcomeJ[player] = actionJ;
							
							// coeff = (u_p(i,s) - u_p(j,s))
							coeff = game.getPayoff(outcomeI, player) - game.getPayoff(outcomeJ, player);
							
							//If this isn't the first coefficient and the coefficient is positive, write a plus.
							//A minus is automatically written when the coefficient is negative.
							writer.print(" ");
							if (coeff >= 0 && outcomeExcept > 0) writer.print("+ ");
							// coeff + x_{is}
							writer.print(coeff + " x" + GameTools.getOutcomeIndex(game, outcomeI));
						}
				
						// >= 0
						writer.println( " >= 0.0");
					}	
				}
			}
			
			////EQUALS CONSTRAINTS
			// \sum_{s \in S} x_s = 1
			
			writer.print(" equals:");
			for (int outcome = 0; outcome < nbOfOutcomes; outcome++) {
				if (outcome != 0) writer.print(" +"); writer.print(" x" + outcome);
			}
			writer.println(" = 1.0");
			
			////POSITIVE VARIABLES CONSTRAINTS
			// \forall s \in S x_s >= 0
				
			for (int outcome = 0; outcome < nbOfOutcomes; outcome++)
				writer.println(" p" + outcome + ": " + "x" + outcome + " >= 0.0");
			
			//END
			writer.println("END");
			
			writer.close();
			
			System.out.println(game.getName() + " OCE LP was succesfully written to: " + path + name + ".lp");
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
