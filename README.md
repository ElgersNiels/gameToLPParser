# Game to LP Parser

Source code to parse Games generated by GAMUT to .lp files which can be fed to LP solvers to compute (optimal) equilibria.

Currently:
  - Pure Nash Equilibrium
  - Optimal Correlated Equilibrium
  
Example of usage:

```java
PureNashEquilibriumLPParser.gameToPNELPfile(game, "D:\", "PNELP.lp");
```

In which *game* is a Game generated by GAMUT. After parsing ended, the LP file will be at D:\PNELP.lp.

It is important to note that the parsers take polynomial amount of memory in the number of players/actions and an exponential running time in the number of players.

# Pure Nash Equilibrium

**Pure Nash Equilibrium** is a solution concept in which an equilibrium is reached when each player plays an action in a way none of the other players will want to (unilaterally) deviate. Deciding whether a Pure Nash Equilibrium exists is NP-Complete for many classes of games, such as: Normal Form Games, Graphical Games with bounded neighbourhood, Acyclic-(hyper)graph Game, (...).

# Optimal Correlated Equilibrium

**Correlated Equilibrium** is a solution concept which is a probability distribution over all the possible outcomes the relevant game can have. A strategy is assigned to each player according to this distribution. In equilibrium, no player will want to deviate from his given strategy. In many games, computing such an equilibrium can be done in polynomial time. Examples of such games are: Normal Form Games, Graphical Games, Symmetric Games, Anonymous Games, Polymatrix Games, Congestion Games, (...).

**Optimizing over all correlated equilibria** according to some linear function (e.g. sum of all players expected payoffs in equilibrium) is in some cases NP-Hard. Examples of games in which computing Optimal Correlated Equilibria is NP-Hard: Graphical Games, Polymatrix Games, Congestion Games, (...). Examples in which it can be done in polynomial time: Normal Form Games, Symmetric Games, Anonymous Games, (...)

# To do:

- ~~Parsers currently take exponential amount of memory (getOutcomes(), getOutcomesExcept(), ...) . Avoid.~~
- Clean up code. Add comments.
- Add relevant links to README
