package soc.base.model;

import soc.base.GameController;

import java.util.LinkedList;

/**
 * Represents a terrain hex on a Settlers of Catan board. Contains the tile's
 * type of terrain (which dictates what type of resource it produces), its
 * number token, whether or not it is occupied by the robber, and a list of all
 * the settlements adjacent to it.
 * @author Connor Barnes
 */
public class Tile {
    //The index of a terrain is the index of the resource it produces in GameController.RESOURCE_TYPES
    public static final String[] TERRAIN_TYPES = {"Hills", "Fields", "Forrest", "Mountains", "Pasture", "Desert"};
    public static final int HILLS = GameController.BRICK;
    public static final int FIELDS = GameController.GRAIN;
    public static final int FORREST = GameController.LUMBER;
    public static final int MOUNTAINS = GameController.ORE;
    public static final int PASTURE = GameController.WOOL;
    public static final int DESERT = TERRAIN_TYPES.length - 1;

    private int terrain;//Dictates what resource this tile yields
    private NumberToken numberToken;
    private boolean occupiedByRobber;//true if the robber is on this tile, false if not
	private LinkedList<Integer> settlementLocs;//Corner locations of all the settlements that are touching the tile

    /**
     * Constructs a tile with the specified terrain. The number token for this
     * tile is zero and is not occupied by the robber.
     * @param terrain the terrain of the tile
     */
    public Tile(int terrain) {
        //TODO: Throw InvalidTerrainException?
        this.terrain = terrain;
        numberToken = null;
		occupiedByRobber = false;
		settlementLocs = new LinkedList<Integer>();
	}

    /**
     * Constructs a tile with the specified terrain and is occupied by the
     * robber if the robberStatus argument is true (otherwise not). The number
     * token for this tile is zero.
     * @param terrain the terrain of the tile
     * @param robberStatus the robber occupies this tile if robberStatus is
     *                     true (otherwise not).
     */
    public Tile(int terrain, boolean robberStatus) {
        //TODO: Throw InvalidTerrainException?
        this.terrain = terrain;
        numberToken = null;
		occupiedByRobber = robberStatus;
		settlementLocs = new LinkedList<Integer>();
	}

    /**
     * Constructs a deep copy of the specified tile.
     * @param inTile the tile to copy
     */
	public Tile(Tile inTile) {
		terrain = inTile.terrain;
		numberToken = inTile.numberToken;
		occupiedByRobber = inTile.occupiedByRobber;
		settlementLocs = new LinkedList<Integer>(inTile.settlementLocs);
	}

    /**
     * Returns this tile's terrain.
     * @return this tile's terrain
     */
    public int getTerrain() {
        return terrain;
    }

    /**
     * Sets this tile's number token to the specified number token.
     * @param token this tile's new number token
     */
	public void setNumberToken(NumberToken token) {
		numberToken = token;
	}

    /**
     * Returns the number on this tile's number token.
     * @return the number on this tile's number token
     */
    public int getNumberToken() {
        return numberToken.getNumber();
    }

    /**
     * Returns the letter above the number on the number token.
     * @return the letter above the number on the number token
     */
    public char getNumberTokenLetter() {
        return  numberToken.getLetter();
    }

    /**
     * Determines whether or not the robber is occupying this tile.
     * @param robberStatus whether or not the robber is occupying this tile
     */
	public void setRobberStatus(boolean robberStatus) {
		occupiedByRobber = robberStatus;
	}

    /**
     * Returns true if the robber is currently occupying this tile; false otherwise.
     * @return true if the robber is currently occupying this tile; false otherwise
     */
    public boolean getRobberStatus() {
        return occupiedByRobber;
    }

    /**
     * Adds the specified location to the list of settlements adjacent to this
     * tile.
     * @param newSettlementLoc the location of the new settlement
     */
	public void addSettlementLoc(int newSettlementLoc) {
		settlementLocs.add(newSettlementLoc);
	}

    /**
     * Returns the list of locations of settlements adjacent to this tile.
     * @return the list of locations of settlements adjacent to this tile
     */
	public LinkedList<Integer> getSettlementLocs() {
		return new LinkedList<Integer>(settlementLocs);
	}
}
