from .token_spot import TokenSpot
from enum import Enum
from sys import stderr

# { spots: TokenSpot[8] }

class Side(Enum):
    TOP=0
    RIGHT=1
    BOTTOM=2
    LEFT=3

class MapSquare:

    """ Creates a MapSquare that contains TokenSpots and can receive a MapCard

        Args:
            token_spots=None -- List of TokenSpots for use in testing
    """
    def __init__(self, token_spots=None):
        if token_spots is None:
            self._spots = [TokenSpot()] * 8
        else:
            self._spots = token_spots
        self._map_card = None

    """ Places a card in the MapSquare, connecting TokenSpots as appropriate.

        Args:
            map_card -- a MapCard representing the paths to be connected.
    """
    def place_card(self, map_card):
        assert self._map_card is None

        self._map_card = map_card

        for path in map_card.get_paths():
            start = self._spots[path.get_start()]
            end = self._spots[path.get_end()]

            start.pair_via_path(end)
            end.pair_via_path(start)

    """ Binds the TokenSpots on the borders of adjacent MapSquares
        
        Args:
            other -- MapSquare that is adjacent to self
            side -- Side showing on which side of self other lies
    """
    def set_adjacent(self, other, side):
        # Sadly, switch doesn't exist :(
        
        if side == Side.TOP:
            # Set the 0th and 1st nodes of self to 5th and 4th nodes of other, respectively
            TokenSpot.pair_adjacent(self._spots[0], other._spots[5])
            TokenSpot.pair_adjacent(self._spots[1], other._spots[4])
        elif side == Side.RIGHT:
            # Set the 2nd and 3rd nodes of self to 8th and 7th nodes of other, respectively
            TokenSpot.pair_adjacent(self._spots[2], other._spots[7])
            TokenSpot.pair_adjacent(self._spots[3], other._spots[6])
        elif side == Side.BOTTOM:
            # Set the 4th and 5th nodes of self to 1st and 0th nodes of other, respectively
            TokenSpot.pair_adjacent(self._spots[4], other._spots[1])
            TokenSpot.pair_adjacent(self._spots[5], other._spots[0])
            pass
        elif side == Side.LEFT:
            # Set the 7th and 8th nodes of self to 3rd and 2nd nodes of other, respectively
            TokenSpot.pair_adjacent(self._spots[6], other._spots[3])
            TokenSpot.pair_adjacent(self._spots[7], other._spots[2])
        else:
            # Improper imput, don't mess anything up by doing anything
            print("In MapSquare.set_adjacent(): encountered invalid side argument: " 
                + str(side), stderr)

        

    

    