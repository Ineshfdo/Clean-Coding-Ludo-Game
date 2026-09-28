package model.board.cell;

import config.enums.PlayerColor;


// One cell of the track. Some cells are marked as the Approach cell or the Entry cell of a colour.

public final class StandardCell {

    private final int position;
    private final PlayerColor approachOwner;
    private final PlayerColor entryOwner;

        /**
         * Creates a track cell.
         *
         * @param position the position of the cell on the track
         * @param approachOwner the colour whose Approach cell this is, or null for none
         * @param entryOwner the colour whose Entry cell this is, or null for none
        */
      
    public StandardCell(int position, PlayerColor approachOwner, PlayerColor entryOwner) {
        this.position = position;
        this.approachOwner = approachOwner;
        this.entryOwner = entryOwner;
    }

        /**
         * Gives the position of the cell.
         *
         * @return the track position
        */

    public int getPosition() {
        return position;
    }

        /**
         * Tells whether this is the Approach cell of a colour.
         *
         * @param color the colour to check
         * @return true when the cell is the Approach cell of that colour
        */

   // Checks if this cell is that colour's Approach point.
    public boolean isApproachPointFor(PlayerColor color) {
        return approachOwner == color;
    }

        /**
         * Tells whether this is the Entry cell of a colour.
         *
         * @param color the colour to check
         * @return true when the cell is the Entry cell of that colour
        */


    // Checks if this cell is that colour's entry(X) point.
    public boolean isEntryPointFor(PlayerColor color) {
        return entryOwner == color;
    }

}
