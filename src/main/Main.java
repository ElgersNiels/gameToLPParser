package main;

import creators.GameCreator;
import edu.stanford.multiagent.gamer.Game;
import parsers.PureNashEquilibriumLPParser;

public class Main {

	public static void main(String[] args0) {
		switch(args0[0]) {
		case "RNFG":
			handleRandomGame(args0);
			break;
		case "RPG-RNFG":
			handlePolymatrixGame(args0);
			break;
		default:
			throw new IllegalArgumentException("I don't know: " + args0[0]);
		}
	}
	
	public static void handleRandomGame(String[] args0) {
		int instance = Integer.parseInt(args0[2]);
		int players = Integer.parseInt(args0[3]);
		int actions = Integer.parseInt(args0[4]);
		Game game = GameCreator.createRandomGame(players, actions);
		PureNashEquilibriumLPParser.gameToPNELPfile(game, args0[1] + "/", instance + "_" + args0[0] + "_" + players + "_" + actions);
	}
	
	public static void handlePolymatrixGame(String[] args0) {
		int instance = Integer.parseInt(args0[2]);
		int players = Integer.parseInt(args0[3]);
		int actions = Integer.parseInt(args0[4]);
		Game game = GameCreator.createRandomGraphPolymatrixRandomGame(players, actions, players*(players-1)/2);
		PureNashEquilibriumLPParser.gameToPNELPfile(game, args0[1] + "/", instance + "_" + args0[0] + "_" + players + "_" + actions);
	}
}
