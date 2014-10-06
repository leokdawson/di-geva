/*
 * Battleship.java
 *
 * Created on May 29, 2007, 12:08 PM
 *
 */

package FitnessEvaluation.Battleship;

import Exceptions.BadParameterException;
import Individuals.Individual;
import Individuals.Phenotype;
import java.util.Properties;

import FitnessEvaluation.FitnessFunction;
import Util.Constants;

/**
 * Example fitness function (with game logic hard-coded) for Battleship game.
 * See tutorial0.html
 *
 * @author jmmcd
 */

public class Battleship implements FitnessFunction {

    private static int gridSize = 10;
    private static int nShips = 4;
    private static double salvoPenalty = 3.0, shotPenalty = 1.0, hitBonus = 10.0, sunkBonus = 30.0;
    private static int[][] map = new int[10][10];
    private static int[][] ships = {
	{1, 4, 4},
	{4, 6, 3},
	{0, 7, 3},
	{7, 9, 2}
    };
    
    /** Creates a new instance of Battleship */
    public Battleship() {
	clearGrid();
	addShips();
    }

    public void setProperties(Properties p) {
	// this method must be over-ridden but we don't need to do anything here.
    }

    /**
     * Calculate the hits of a given firing pattern. Firing pattern
     * consists of multiple salvos (separated by semi-colons); salvo
     * consists of a horizontal row of shots starting at (x, y) and
     * proceeding east (ie right, ie x-ward) for n shots. Salvo is
     * x,y,n (separated by commas).
     *
     * Fitness is always minimised in GEVA. So we start at 100,
     * add points for hits, add for ships completely sunk,
     * subtract for number of salvos and for total number of shots.
     * Then take a safe reciprocal (1 / (1 + x)) and return that.
     * @param p Compared phenotype
     * @return fitness
     */
    public double evaluateString(Phenotype p) {
        String pS = p.getStringNoSpace();
	double fitness = 100.0;

	clearGrid();
	addShips();
	String[] salvos = pS.split(";");
	for (String salvo: salvos) {
	    fitness -= salvoPenalty;
	    String [] data = salvo.split(",");
	    int x = Integer.parseInt(data[0]);
	    int y = Integer.parseInt(data[1]);
	    int size = Integer.parseInt(data[2]);
	    for (int i = x; i < x + size && i < gridSize; i++) {
		// each salvo is a horizontal row of shots of given size
		fitness -= shotPenalty;
		if (map[i][y] == 1) {
		    map[i][y] = 0;
		    fitness += hitBonus;
		}
	    }
	    for (int i = 0; i < nShips; i++) {
		if (shipSunk(ships[i])) {
		    fitness += sunkBonus;
		}
	    }
	}
	    
	// Visualise each individual. This is *not* conceptually a
	// part of the fitness function so this in an ugly hack. There
	// are better ways but they require some more work so are
	// omitted.
	// Note iterate faster over i to print row-by-row


	// This is commented-out for now since it can be a bit slow. If you uncomment it,
	// the GEVA GUI might become unresponsive while the battleship problem is running.
 	for (int j = 0; j < gridSize; j++) {
 	    for (int i = 0; i < gridSize; i++) {
 		if (map[i][j] == 1) {
 		    System.out.print("X ");
 		} else {
 		    System.out.print(". ");
 		}
 	    }
 	    System.out.println("");
 	}
 	System.out.println(pS);
 	System.out.println("");
		    
	return 1.0 / (1.0 + fitness);
    }

    private void clearGrid() {
	for (int i = 0; i < gridSize; i++) {
	    for (int j = 0; j < gridSize; j++) {
		// indicating "no ship at this cell"
		map[i][j] = 0;
	    }
	}
    }

    private void addShips() {
	for (int i = 0; i < nShips; i++) {
	    int x = ships[i][0];
	    int y = ships[i][1];
	    int size = ships[i][2];
	    for (int j = x; j < x + size && j < gridSize; j++) {
		// each ship is a horizontal row starting at (x, y)
		// of length size
		map[j][y] = 1;
	    }
	}
    }

    /* Has every cell of the ship been hit?
     */
    private boolean shipSunk(int[] ship) {
	int x = ship[0];
	int y = ship[1];
	int size = ship[2];
	for (int i = x; i < x + size && i < gridSize; i++) {
	    if (map[i][y] == 1) {
		return false;
	    }
	}
	return true;
    }

    public void getFitness(Individual i) {
        i.getFitness().setDouble(this.evaluateString(i.getPhenotype()));
    }

    public boolean canCache()
    {   return true;
    }

}

